package com.photobooking.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.photobooking.common.BusinessException;
import com.photobooking.common.OrderStatus;
import com.photobooking.config.AuthInterceptor;
import com.photobooking.dto.CommentRequest;
import com.photobooking.entity.Comment;
import com.photobooking.entity.ServiceOrder;
import com.photobooking.entity.SysUser;
import com.photobooking.mapper.CommentMapper;
import com.photobooking.mapper.ServiceOrderMapper;
import com.photobooking.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;
    private final ServiceOrderMapper orderMapper;
    private final SysUserMapper userMapper;

    public List<Comment> list() {
        return commentMapper.selectList(new LambdaQueryWrapper<Comment>().orderByDesc(Comment::getId));
    }

    public List<Comment> listByOrderIds(List<Integer> orderIds) {
        if (orderIds.isEmpty()) return List.of();
        return commentMapper.selectList(new LambdaQueryWrapper<Comment>().in(Comment::getOrderId, orderIds));
    }

    public Comment create(CommentRequest req) {
        Integer userId = AuthInterceptor.requireUserId();
        if (req.getContent() == null || req.getContent().isBlank()) {
            throw new BusinessException("请输入评价内容");
        }
        ServiceOrder order = orderMapper.selectById(req.getOrderId());
        if (order == null) throw new BusinessException("订单不存在");
        if (!OrderStatus.DONE.equals(order.getStatus())) {
            throw new BusinessException("只有已完成订单可评价");
        }
        Long exists = commentMapper.selectCount(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getOrderId, req.getOrderId()));
        if (exists > 0) throw new BusinessException("该订单已评价");

        SysUser user = userMapper.selectById(userId);
        Comment comment = new Comment();
        comment.setOrderId(req.getOrderId());
        comment.setUserId(userId);
        comment.setUserName(user.getName());
        comment.setServerId(req.getServerId());
        comment.setPhotographerId(req.getPhotographerId());
        comment.setScore(req.getScore());
        comment.setContent(req.getContent().trim());
        comment.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        commentMapper.insert(comment);
        return comment;
    }
}
