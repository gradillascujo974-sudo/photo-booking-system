package com.photobooking.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.photobooking.common.BusinessException;
import com.photobooking.dto.LoginRequest;
import com.photobooking.dto.RegisterRequest;
import com.photobooking.entity.SysUser;
import com.photobooking.mapper.SysUserMapper;
import com.photobooking.util.UserVoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final SysUserMapper userMapper;

    public Map<String, Object> login(LoginRequest req) {
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, req.getUsername())
                .eq(SysUser::getPassword, req.getPassword())
                .eq(SysUser::getRole, req.getRole()));
        if (user == null) {
            throw new BusinessException("用户名、密码或角色不匹配");
        }
        return UserVoUtil.toVo(user);
    }

    public Map<String, Object> register(RegisterRequest req) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, req.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(req.getUsername());
        user.setPassword(req.getPassword());
        user.setRole("USER");
        user.setName(req.getName() != null ? req.getName() : req.getUsername());
        user.setPhone(req.getPhone());
        user.setEmail(req.getEmail());
        user.setAccount(BigDecimal.valueOf(100));
        user.setCertified(0);
        user.setRating(BigDecimal.valueOf(5.0));
        userMapper.insert(user);
        return UserVoUtil.toVo(user);
    }

    public Map<String, Object> getById(Integer id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) throw new BusinessException("用户不存在");
        return UserVoUtil.toVo(user);
    }

    public Map<String, Object> updateProfile(Integer userId, Map<String, Object> updates) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        if (updates.containsKey("name")) user.setName((String) updates.get("name"));
        if (updates.containsKey("phone")) user.setPhone((String) updates.get("phone"));
        if (updates.containsKey("email")) user.setEmail((String) updates.get("email"));
        userMapper.updateById(user);
        return UserVoUtil.toVo(user);
    }

    public Map<String, Object> getPublicById(Integer id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) throw new BusinessException("用户不存在");
        return UserVoUtil.toPublicVo(user);
    }

    public Map<String, Object> getPhotographerProfile(Integer userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null || !"PHOTOGRAPHER".equals(user.getRole())) {
            throw new BusinessException("摄影师不存在");
        }
        return UserVoUtil.toPhotographerSelfVo(user);
    }

    public Map<String, Object> updatePhotographerProfile(Integer userId, Map<String, Object> updates) {
        SysUser user = userMapper.selectById(userId);
        if (user == null || !"PHOTOGRAPHER".equals(user.getRole())) {
            throw new BusinessException("摄影师不存在");
        }
        if (updates.containsKey("phone")) user.setPhone((String) updates.get("phone"));
        if (updates.containsKey("wechat")) user.setWechat((String) updates.get("wechat"));
        if (updates.containsKey("payQrcode")) user.setPayQrcode((String) updates.get("payQrcode"));
        if (updates.containsKey("introduce")) user.setIntroduce((String) updates.get("introduce"));
        userMapper.updateById(user);
        return UserVoUtil.toPhotographerSelfVo(user);
    }

    public Map<String, Object> recharge(Integer userId, BigDecimal amount) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        if (!"USER".equals(user.getRole())) {
            throw new BusinessException("仅普通用户可充值积分");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("充值积分须大于 0");
        }
        BigDecimal current = user.getAccount() != null ? user.getAccount() : BigDecimal.ZERO;
        user.setAccount(current.add(amount));
        userMapper.updateById(user);
        return UserVoUtil.toVo(user);
    }

    public List<Map<String, Object>> listPhotographers(String keyword, String city) {
        LambdaQueryWrapper<SysUser> qw = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getRole, "PHOTOGRAPHER");
        if (city != null && !city.isBlank()) {
            qw.eq(SysUser::getCity, city);
        }
        List<SysUser> list = userMapper.selectList(qw);
        return list.stream()
                .filter(u -> matchKeyword(u, keyword))
                .map(u -> {
                    Map<String, Object> vo = UserVoUtil.toPublicVo(u);
                    vo.put("username", u.getUsername());
                    return vo;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> listUsersForAdmin() {
        return userMapper.selectList(new LambdaQueryWrapper<SysUser>()
                        .ne(SysUser::getRole, "ADMIN"))
                .stream().map(UserVoUtil::toVo).collect(Collectors.toList());
    }

    public Map<String, Object> adminUpdatePoints(Integer userId, BigDecimal points) {
        if (points == null || points.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("积分不能为负数");
        }
        SysUser user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        if ("ADMIN".equals(user.getRole())) {
            throw new BusinessException("不能修改管理员积分");
        }
        user.setAccount(points);
        userMapper.updateById(user);
        return UserVoUtil.toVo(user);
    }

    private boolean matchKeyword(SysUser u, String keyword) {
        if (keyword == null || keyword.isBlank()) return true;
        String k = keyword.trim();
        if (u.getName() != null && u.getName().contains(k)) return true;
        return u.getStyles() != null && u.getStyles().contains(k);
    }
}
