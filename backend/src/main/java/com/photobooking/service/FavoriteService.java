package com.photobooking.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.photobooking.config.AuthInterceptor;
import com.photobooking.entity.Favorite;
import com.photobooking.mapper.FavoriteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteMapper favoriteMapper;

    public List<Favorite> listMine() {
        Integer userId = AuthInterceptor.requireUserId();
        return favoriteMapper.selectList(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getId));
    }

    public Map<String, Object> toggle(Integer serverId) {
        Integer userId = AuthInterceptor.requireUserId();
        Favorite existing = favoriteMapper.selectOne(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getServerId, serverId)
                .eq(Favorite::getUserId, userId));
        Map<String, Object> result = new HashMap<>();
        if (existing != null) {
            favoriteMapper.deleteById(existing.getId());
            result.put("favorited", false);
        } else {
            Favorite fav = new Favorite();
            fav.setServerId(serverId);
            fav.setUserId(userId);
            fav.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            favoriteMapper.insert(fav);
            result.put("favorited", true);
        }
        return result;
    }

    public boolean isFavorited(Integer serverId, Integer userId) {
        if (userId == null) return false;
        return favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getServerId, serverId)
                .eq(Favorite::getUserId, userId)) > 0;
    }
}
