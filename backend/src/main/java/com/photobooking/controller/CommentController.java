package com.photobooking.controller;

import com.photobooking.common.Result;
import com.photobooking.dto.CommentRequest;
import com.photobooking.entity.Comment;
import com.photobooking.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public Result<List<Comment>> list() {
        return Result.success(commentService.list());
    }

    @PostMapping
    public Result<Comment> create(@RequestBody CommentRequest req) {
        return Result.success(commentService.create(req));
    }
}
