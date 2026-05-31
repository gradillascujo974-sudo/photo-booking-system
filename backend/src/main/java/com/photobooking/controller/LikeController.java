package com.photobooking.controller;

import com.photobooking.common.Result;
import com.photobooking.config.UserContext;
import com.photobooking.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/check/{portfolioId}")
    public Result<Map<String, Object>> check(@PathVariable Integer portfolioId) {
        Map<String, Object> data = new HashMap<>();
        data.put("liked", likeService.isLiked(portfolioId, UserContext.getUserId()));
        data.put("count", likeService.getLikeCount(portfolioId));
        return Result.success(data);
    }

    @GetMapping("/count/{portfolioId}")
    public Result<Integer> count(@PathVariable Integer portfolioId) {
        return Result.success(likeService.getLikeCount(portfolioId));
    }

    @PostMapping("/toggle/{portfolioId}")
    public Result<Map<String, Object>> toggle(@PathVariable Integer portfolioId) {
        return Result.success(likeService.toggle(portfolioId));
    }
}
