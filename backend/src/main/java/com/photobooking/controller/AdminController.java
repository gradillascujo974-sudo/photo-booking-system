package com.photobooking.controller;

import com.photobooking.common.BusinessException;
import com.photobooking.common.Result;
import com.photobooking.config.AuthInterceptor;
import com.photobooking.entity.Notice;
import com.photobooking.service.CatalogService;
import com.photobooking.service.IdentifyService;
import com.photobooking.service.OrderService;
import com.photobooking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final OrderService orderService;
    private final IdentifyService identifyService;
    private final CatalogService catalogService;

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        requireAdmin();
        List<Map<String, Object>> users = userService.listUsersForAdmin();
        long userCount = users.stream().filter(u -> "USER".equals(u.get("role"))).count();
        long photoCount = users.stream().filter(u -> "PHOTOGRAPHER".equals(u.get("role"))).count();
        long pending = identifyService.listAll().stream().filter(i -> "待审核".equals(i.getStatus())).count();
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userCount);
        stats.put("photoCount", photoCount);
        stats.put("orderCount", orderService.listAll().size());
        stats.put("pendingIdentify", pending);
        return Result.success(stats);
    }

    @GetMapping("/users")
    public Result<List<Map<String, Object>>> users() {
        requireAdmin();
        return Result.success(userService.listUsersForAdmin());
    }

    @PutMapping("/users/{id}/points")
    public Result<Map<String, Object>> updateUserPoints(@PathVariable Integer id,
                                                          @RequestBody Map<String, Object> body) {
        requireAdmin();
        Object points = body.get("points");
        if (points == null) {
            throw new BusinessException("请输入积分");
        }
        return Result.success(userService.adminUpdatePoints(id, new BigDecimal(points.toString())));
    }

    @PostMapping("/notices")
    public Result<Notice> createNotice(@RequestBody Notice notice) {
        requireAdmin();
        return Result.success(catalogService.addNotice(notice));
    }

    @PutMapping("/notices/{id}")
    public Result<Notice> updateNotice(@PathVariable Integer id, @RequestBody Notice notice) {
        requireAdmin();
        return Result.success(catalogService.updateNotice(id, notice));
    }

    @DeleteMapping("/notices/{id}")
    public Result<Void> deleteNotice(@PathVariable Integer id) {
        requireAdmin();
        catalogService.deleteNotice(id);
        return Result.success();
    }

    private void requireAdmin() {
        Map<String, Object> user = userService.getById(AuthInterceptor.requireUserId());
        if (!"ADMIN".equals(user.get("role"))) {
            throw new BusinessException("无管理员权限");
        }
    }
}
