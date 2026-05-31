package com.photobooking.controller;

import com.photobooking.common.BusinessException;
import com.photobooking.common.Result;
import com.photobooking.config.AuthInterceptor;
import com.photobooking.dto.PortfolioRequest;
import com.photobooking.entity.Portfolio;
import com.photobooking.service.PortfolioService;
import com.photobooking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PortfolioManageController {

    private final PortfolioService portfolioService;
    private final UserService userService;

    @PostMapping("/photographer/portfolios")
    public Result<Portfolio> createMine(@RequestBody PortfolioRequest req) {
        Integer uid = AuthInterceptor.requireUserId();
        return Result.success(portfolioService.createByPhotographer(uid, req));
    }

    @PutMapping("/photographer/portfolios/{id}")
    public Result<Portfolio> updateMine(@PathVariable Integer id, @RequestBody PortfolioRequest req) {
        Integer uid = AuthInterceptor.requireUserId();
        return Result.success(portfolioService.updateByPhotographer(uid, id, req));
    }

    @DeleteMapping("/photographer/portfolios/{id}")
    public Result<Void> deleteMine(@PathVariable Integer id) {
        Integer uid = AuthInterceptor.requireUserId();
        portfolioService.deleteByPhotographer(uid, id);
        return Result.success();
    }

    @GetMapping("/admin/portfolios")
    public Result<List<Map<String, Object>>> adminList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer photographerId) {
        requireAdmin();
        return Result.success(portfolioService.listForAdmin(keyword, status, photographerId));
    }

    @PostMapping("/admin/portfolios")
    public Result<Portfolio> adminCreate(@RequestBody PortfolioRequest req) {
        requireAdmin();
        return Result.success(portfolioService.createByAdmin(req));
    }

    @PutMapping("/admin/portfolios/{id}")
    public Result<Portfolio> adminUpdate(@PathVariable Integer id, @RequestBody PortfolioRequest req) {
        requireAdmin();
        return Result.success(portfolioService.updateByAdmin(id, req));
    }

    @DeleteMapping("/admin/portfolios/{id}")
    public Result<Void> adminDelete(@PathVariable Integer id) {
        requireAdmin();
        portfolioService.deleteByAdmin(id);
        return Result.success();
    }

    private void requireAdmin() {
        Map<String, Object> user = userService.getById(AuthInterceptor.requireUserId());
        if (!"ADMIN".equals(user.get("role"))) {
            throw new BusinessException("无管理员权限");
        }
    }
}
