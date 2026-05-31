package com.photobooking.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.photobooking.common.BusinessException;
import com.photobooking.config.AuthInterceptor;
import com.photobooking.dto.AuditRequest;
import com.photobooking.dto.IdentifyRequest;
import com.photobooking.entity.PhotographerIdentify;
import com.photobooking.entity.SysUser;
import com.photobooking.mapper.PhotographerIdentifyMapper;
import com.photobooking.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IdentifyService {

    private final PhotographerIdentifyMapper identifyMapper;
    private final SysUserMapper userMapper;

    public List<PhotographerIdentify> listAll() {
        return identifyMapper.selectList(new LambdaQueryWrapper<PhotographerIdentify>()
                .orderByDesc(PhotographerIdentify::getId));
    }

    public PhotographerIdentify getMine() {
        Integer userId = AuthInterceptor.requireUserId();
        return identifyMapper.selectOne(new LambdaQueryWrapper<PhotographerIdentify>()
                .eq(PhotographerIdentify::getUserId, userId));
    }

    public PhotographerIdentify submit(IdentifyRequest req) {
        Integer userId = AuthInterceptor.requireUserId();
        if (req.getCardNo() == null || req.getCardNo().isBlank()) {
            throw new BusinessException("请填写完整信息");
        }
        SysUser user = userMapper.selectById(userId);
        PhotographerIdentify record = identifyMapper.selectOne(new LambdaQueryWrapper<PhotographerIdentify>()
                .eq(PhotographerIdentify::getUserId, userId));
        if (record == null) {
            record = new PhotographerIdentify();
            record.setUserId(userId);
        }
        record.setUserName(user.getName());
        record.setCard(req.getCard());
        record.setCardNo(req.getCardNo().trim());
        record.setImg(req.getImg());
        record.setIntro(req.getIntro());
        record.setStatus("待审核");
        record.setReason("");
        record.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        if (record.getId() == null) {
            identifyMapper.insert(record);
        } else {
            identifyMapper.updateById(record);
        }
        return record;
    }

    @Transactional
    public PhotographerIdentify audit(Integer id, AuditRequest req) {
        PhotographerIdentify record = identifyMapper.selectById(id);
        if (record == null) throw new BusinessException("申请不存在");

        String status = req.getStatus();
        if ("通过".equals(status)) {
            record.setStatus("通过");
            record.setReason("");
            SysUser user = userMapper.selectById(record.getUserId());
            user.setRole("PHOTOGRAPHER");
            user.setCertified(1);
            if (record.getIntro() != null) user.setIntroduce(record.getIntro());
            if (user.getAccount() == null) user.setAccount(BigDecimal.ZERO);
            if (user.getRating() == null) user.setRating(BigDecimal.valueOf(5.0));
            userMapper.updateById(user);
        } else {
            record.setStatus("已驳回");
            record.setReason(req.getReason() != null ? req.getReason() : "材料不符合要求");
        }
        identifyMapper.updateById(record);
        return record;
    }
}
