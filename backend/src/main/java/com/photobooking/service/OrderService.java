package com.photobooking.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.photobooking.common.BusinessException;
import com.photobooking.common.OrderStatus;
import com.photobooking.common.PointsConfig;
import com.photobooking.config.AuthInterceptor;
import com.photobooking.dto.OrderCreateRequest;
import com.photobooking.entity.PhotoService;
import com.photobooking.entity.ServiceOrder;
import com.photobooking.entity.SysUser;
import com.photobooking.mapper.PhotoServiceMapper;
import com.photobooking.mapper.ServiceOrderMapper;
import com.photobooking.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ServiceOrderMapper orderMapper;
    private final PhotoServiceMapper serviceMapper;
    private final SysUserMapper userMapper;
    private final PointsService pointsService;

    public List<ServiceOrder> listForCurrentUser(String role, Integer userId) {
        LambdaQueryWrapper<ServiceOrder> qw = new LambdaQueryWrapper<>();
        if ("USER".equals(role)) {
            qw.eq(ServiceOrder::getUserId, userId);
        } else if ("PHOTOGRAPHER".equals(role)) {
            qw.eq(ServiceOrder::getPhotographerId, userId);
            qw.ne(ServiceOrder::getStatus, OrderStatus.WAIT_CONTACT);
        }
        qw.orderByDesc(ServiceOrder::getId);
        return orderMapper.selectList(qw);
    }

    public List<ServiceOrder> listAll() {
        return orderMapper.selectList(new LambdaQueryWrapper<ServiceOrder>()
                .orderByDesc(ServiceOrder::getId));
    }

    /** 提交预约意向，不扣积分 */
    @Transactional
    public ServiceOrder create(OrderCreateRequest req) {
        Integer userId = AuthInterceptor.requireUserId();
        PhotoService service = serviceMapper.selectById(req.getServerId());
        if (service == null) throw new BusinessException("服务不存在");

        SysUser user = userMapper.selectById(userId);
        ServiceOrder order = new ServiceOrder();
        order.setOrderNo("ORD" + System.currentTimeMillis());
        order.setUserId(userId);
        order.setPhotographerId(service.getPhotographerId());
        order.setServerId(service.getId());
        order.setServerName(service.getName());
        order.setTotal(service.getPrice());
        order.setServerTime(req.getServerTime());
        order.setPhone(req.getPhone() != null ? req.getPhone() : user.getPhone());
        order.setRemark(req.getRemark());
        order.setStatus(OrderStatus.WAIT_CONTACT);
        order.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        orderMapper.insert(order);
        return order;
    }

    /** 平台联系摄影师，扣 3 积分 */
    @Transactional
    public ServiceOrder contactPhotographer(Integer orderId) {
        ServiceOrder order = requireUserOrder(orderId);
        if (!OrderStatus.WAIT_CONTACT.equals(order.getStatus())) {
            throw new BusinessException("当前状态不可联系摄影师");
        }
        pointsService.deductPoints(order.getUserId(), PointsConfig.CONTACT_POINTS);
        order.setStatus(OrderStatus.CONTACTED);
        orderMapper.updateById(order);
        return order;
    }

    /** 确定摄影师，扣 30 积分，返回联系方式与收款码 */
    @Transactional
    public Map<String, Object> confirmPhotographer(Integer orderId) {
        ServiceOrder order = requireUserOrder(orderId);
        if (!OrderStatus.CONTACTED.equals(order.getStatus())) {
            throw new BusinessException("请先联系摄影师（消耗3积分）后再确定");
        }
        pointsService.deductPoints(order.getUserId(), PointsConfig.CONFIRM_POINTS);
        order.setStatus(OrderStatus.CONFIRMED);
        orderMapper.updateById(order);

        SysUser photographer = userMapper.selectById(order.getPhotographerId());
        if (photographer == null) throw new BusinessException("摄影师不存在");

        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        Map<String, Object> contact = new HashMap<>();
        contact.put("name", photographer.getName());
        contact.put("phone", photographer.getPhone());
        contact.put("wechat", photographer.getWechat());
        contact.put("payQrcode", photographer.getPayQrcode());
        contact.put("depositTip", "请扫描摄影师收款码支付定金，线下完成后续拍摄费用结算");
        result.put("contact", contact);
        return result;
    }

    /** 查看已确定订单的摄影师联系方式 */
    public Map<String, Object> getContactInfo(Integer orderId) {
        ServiceOrder order = requireUserOrder(orderId);
        if (!OrderStatus.CONFIRMED.equals(order.getStatus()) && !OrderStatus.DONE.equals(order.getStatus())) {
            throw new BusinessException("确定摄影师后可查看联系方式与收款码");
        }
        SysUser photographer = userMapper.selectById(order.getPhotographerId());
        Map<String, Object> contact = new HashMap<>();
        contact.put("name", photographer.getName());
        contact.put("phone", photographer.getPhone());
        contact.put("wechat", photographer.getWechat());
        contact.put("payQrcode", photographer.getPayQrcode());
        contact.put("depositTip", "请扫描摄影师收款码支付定金");
        return contact;
    }

    @Transactional
    public ServiceOrder updateStatus(Integer orderId, String status) {
        ServiceOrder order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException("订单不存在");

        Integer userId = AuthInterceptor.requireUserId();
        SysUser user = userMapper.selectById(userId);

        if (OrderStatus.CANCELLED.equals(status)) {
            if (!"ADMIN".equals(user.getRole()) && !order.getUserId().equals(userId)) {
                throw new BusinessException("无权取消该订单");
            }
            if (OrderStatus.CANCELLED.equals(order.getStatus()) || OrderStatus.DONE.equals(order.getStatus())) {
                throw new BusinessException("订单不可取消");
            }
            refundOnCancel(order);
            order.setStatus(OrderStatus.CANCELLED);
            orderMapper.updateById(order);
            return order;
        }

        if ("PHOTOGRAPHER".equals(user.getRole())) {
            if (!order.getPhotographerId().equals(userId)) {
                throw new BusinessException("无权操作该订单");
            }
            if (OrderStatus.DONE.equals(status) && OrderStatus.CONFIRMED.equals(order.getStatus())) {
                order.setStatus(OrderStatus.DONE);
                orderMapper.updateById(order);
                return order;
            }
        }

        throw new BusinessException("不支持的状态变更");
    }

    private void refundOnCancel(ServiceOrder order) {
        if (OrderStatus.CONTACTED.equals(order.getStatus())) {
            pointsService.refundPoints(order.getUserId(), PointsConfig.CONTACT_POINTS);
        } else if (OrderStatus.CONFIRMED.equals(order.getStatus())) {
            pointsService.refundPoints(order.getUserId(), PointsConfig.CONTACT_POINTS + PointsConfig.CONFIRM_POINTS);
        }
    }

    private ServiceOrder requireUserOrder(Integer orderId) {
        ServiceOrder order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        Integer userId = AuthInterceptor.requireUserId();
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该订单");
        }
        return order;
    }
}
