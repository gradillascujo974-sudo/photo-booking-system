package com.photobooking.controller;

import com.photobooking.common.Result;
import com.photobooking.config.AuthInterceptor;
import com.photobooking.dto.LoginRequest;
import com.photobooking.dto.RegisterRequest;
import com.photobooking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest req) {
        return Result.success(userService.login(req));
    }

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody RegisterRequest req) {
        return Result.success(userService.register(req));
    }

    @GetMapping("/user/me")
    public Result<Map<String, Object>> me() {
        return Result.success(userService.getById(AuthInterceptor.requireUserId()));
    }

    @PutMapping("/user/profile")
    public Result<Map<String, Object>> profile(@RequestBody Map<String, Object> body) {
        return Result.success(userService.updateProfile(AuthInterceptor.requireUserId(), body));
    }

    @PostMapping("/user/recharge")
    public Result<Map<String, Object>> recharge(@RequestBody Map<String, Object> body) {
        Object amount = body.get("amount");
        if (amount == null) {
            return Result.error("请输入充值积分");
        }
        BigDecimal money = new BigDecimal(amount.toString());
        return Result.success(userService.recharge(AuthInterceptor.requireUserId(), money));
    }
}
