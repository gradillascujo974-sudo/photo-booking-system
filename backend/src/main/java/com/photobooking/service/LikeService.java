package com.photobooking.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.photobooking.config.AuthInterceptor;
import com.photobooking.entity.LikeRecord;
import com.photobooking.entity.Portfolio;
import com.photobooking.mapper.LikeRecordMapper;
import com.photobooking.mapper.PortfolioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRecordMapper likeMapper;
    private final PortfolioMapper portfolioMapper;

    public boolean isLiked(Integer portfolioId, Integer userId) {
        if (userId == null) return false;
        return likeMapper.selectCount(new LambdaQueryWrapper<LikeRecord>()
                .eq(LikeRecord::getPortfolioId, portfolioId)
                .eq(LikeRecord::getUserId, userId)) > 0;
    }

    public int getLikeCount(Integer portfolioId) {
        Portfolio p = portfolioMapper.selectById(portfolioId);
        int base = p != null && p.getLikeCount() != null ? p.getLikeCount() : 0;
        long extra = likeMapper.selectCount(new LambdaQueryWrapper<LikeRecord>()
                .eq(LikeRecord::getPortfolioId, portfolioId));
        return base + (int) extra;
    }

    @Transactional
    public Map<String, Object> toggle(Integer portfolioId) {
        Integer userId = AuthInterceptor.requireUserId();
        LikeRecord existing = likeMapper.selectOne(new LambdaQueryWrapper<LikeRecord>()
                .eq(LikeRecord::getPortfolioId, portfolioId)
                .eq(LikeRecord::getUserId, userId));

        Map<String, Object> result = new HashMap<>();
        if (existing != null) {
            likeMapper.deleteById(existing.getId());
            result.put("liked", false);
        } else {
            LikeRecord like = new LikeRecord();
            like.setPortfolioId(portfolioId);
            like.setUserId(userId);
            like.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            likeMapper.insert(like);
            result.put("liked", true);
        }
        result.put("count", getLikeCount(portfolioId));
        return result;
    }
}
