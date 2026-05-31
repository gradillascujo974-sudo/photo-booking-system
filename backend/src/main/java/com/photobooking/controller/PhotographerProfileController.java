package com.photobooking.controller;

import com.photobooking.common.Result;
import com.photobooking.config.AuthInterceptor;
import com.photobooking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/photographer")
@RequiredArgsConstructor
public class PhotographerProfileController {

    private final UserService userService;

    @GetMapping("/profile")
    public Result<Map<String, Object>> profile() {
        return Result.success(userService.getPhotographerProfile(AuthInterceptor.requireUserId()));
    }

    @PutMapping("/profile")
    public Result<Map<String, Object>> update(@RequestBody Map<String, Object> body) {
        return Result.success(userService.updatePhotographerProfile(AuthInterceptor.requireUserId(), body));
    }
}
