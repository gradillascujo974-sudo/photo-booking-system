package com.photobooking.controller;

import com.photobooking.common.Result;
import com.photobooking.entity.Favorite;
import com.photobooking.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public Result<List<Favorite>> list() {
        return Result.success(favoriteService.listMine());
    }

    @PostMapping("/toggle/{serverId}")
    public Result<Map<String, Object>> toggle(@PathVariable Integer serverId) {
        return Result.success(favoriteService.toggle(serverId));
    }
}
