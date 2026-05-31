package com.photobooking.service;

import com.photobooking.common.BusinessException;
import com.photobooking.common.PointsConfig;
import com.photobooking.entity.SysUser;
import com.photobooking.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PointsService {

    private final SysUserMapper userMapper;

    /** account 字段存储用户积分，1积分=1元 */
    public BigDecimal getPoints(Integer userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        return user.getAccount() != null ? user.getAccount() : BigDecimal.ZERO;
    }

    public void deductPoints(Integer userId, int points) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        BigDecimal current = user.getAccount() != null ? user.getAccount() : BigDecimal.ZERO;
        BigDecimal cost = BigDecimal.valueOf(points);
        if (current.compareTo(cost) < 0) {
            throw new BusinessException("积分不足，当前积分 " + current.intValue() + "，需要 " + points + " 积分");
        }
        user.setAccount(current.subtract(cost));
        userMapper.updateById(user);
    }

    public void refundPoints(Integer userId, int points) {
        if (points <= 0) return;
        SysUser user = userMapper.selectById(userId);
        if (user == null) return;
        BigDecimal current = user.getAccount() != null ? user.getAccount() : BigDecimal.ZERO;
        user.setAccount(current.add(BigDecimal.valueOf(points)));
        userMapper.updateById(user);
    }
}
