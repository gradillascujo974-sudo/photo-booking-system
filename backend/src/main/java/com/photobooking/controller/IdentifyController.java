package com.photobooking.controller;

import com.photobooking.common.Result;
import com.photobooking.dto.AuditRequest;
import com.photobooking.dto.IdentifyRequest;
import com.photobooking.entity.PhotographerIdentify;
import com.photobooking.service.IdentifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/identify")
@RequiredArgsConstructor
public class IdentifyController {

    private final IdentifyService identifyService;

    @GetMapping
    public Result<List<PhotographerIdentify>> list() {
        return Result.success(identifyService.listAll());
    }

    @GetMapping("/mine")
    public Result<PhotographerIdentify> mine() {
        return Result.success(identifyService.getMine());
    }

    @PostMapping
    public Result<PhotographerIdentify> submit(@RequestBody IdentifyRequest req) {
        return Result.success(identifyService.submit(req));
    }

    @PutMapping("/{id}/audit")
    public Result<PhotographerIdentify> audit(@PathVariable Integer id, @RequestBody AuditRequest req) {
        return Result.success(identifyService.audit(id, req));
    }
}
