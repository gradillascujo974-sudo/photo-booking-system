package com.photobooking.controller;

import com.photobooking.common.Result;
import com.photobooking.config.AuthInterceptor;
import com.photobooking.dto.OrderCreateRequest;
import com.photobooking.dto.OrderStatusRequest;
import com.photobooking.entity.ServiceOrder;
import com.photobooking.service.OrderService;
import com.photobooking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @GetMapping
    public Result<List<ServiceOrder>> list() {
        Map<String, Object> user = userService.getById(AuthInterceptor.requireUserId());
        String role = (String) user.get("role");
        if ("ADMIN".equals(role)) {
            return Result.success(orderService.listAll());
        }
        return Result.success(orderService.listForCurrentUser(role, (Integer) user.get("id")));
    }

    @PostMapping
    public Result<ServiceOrder> create(@RequestBody OrderCreateRequest req) {
        return Result.success(orderService.create(req));
    }

    @PostMapping("/{id}/contact")
    public Result<ServiceOrder> contact(@PathVariable Integer id) {
        return Result.success(orderService.contactPhotographer(id));
    }

    @PostMapping("/{id}/confirm")
    public Result<Map<String, Object>> confirm(@PathVariable Integer id) {
        return Result.success(orderService.confirmPhotographer(id));
    }

    @GetMapping("/{id}/contact")
    public Result<Map<String, Object>> contactInfo(@PathVariable Integer id) {
        return Result.success(orderService.getContactInfo(id));
    }

    @PutMapping("/{id}/status")
    public Result<ServiceOrder> updateStatus(@PathVariable Integer id, @RequestBody OrderStatusRequest req) {
        return Result.success(orderService.updateStatus(id, req.getStatus()));
    }
}
