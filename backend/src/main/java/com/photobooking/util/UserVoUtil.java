package com.photobooking.util;

import com.photobooking.entity.SysUser;

import java.util.HashMap;
import java.util.Map;

public final class UserVoUtil {

    private UserVoUtil() {}

    public static Map<String, Object> toVo(SysUser user) {
        Map<String, Object> vo = new HashMap<>();
        vo.put("id", user.getId());
        vo.put("username", user.getUsername());
        vo.put("role", user.getRole());
        vo.put("name", user.getName());
        vo.put("phone", user.getPhone());
        vo.put("email", user.getEmail());
        vo.put("avatar", user.getAvatar());
        vo.put("account", user.getAccount());
        vo.put("points", user.getAccount() != null ? user.getAccount().intValue() : 0);
        vo.put("introduce", user.getIntroduce());
        vo.put("city", user.getCity());
        if (user.getStyles() != null && !user.getStyles().isBlank()) {
            vo.put("styles", user.getStyles().split(","));
        }
        vo.put("certified", user.getCertified() != null && user.getCertified() == 1);
        vo.put("rating", user.getRating());
        return vo;
    }

    /** 公开展示，不含联系方式与收款码 */
    public static Map<String, Object> toPublicVo(SysUser user) {
        Map<String, Object> vo = toVo(user);
        vo.remove("phone");
        vo.remove("wechat");
        vo.remove("payQrcode");
        vo.remove("email");
        return vo;
    }

    public static Map<String, Object> toPhotographerSelfVo(SysUser user) {
        Map<String, Object> vo = toVo(user);
        vo.put("wechat", user.getWechat());
        vo.put("payQrcode", user.getPayQrcode());
        return vo;
    }
}
