package com.photobooking.controller;

import com.photobooking.common.Result;
import com.photobooking.entity.MediaFile;
import com.photobooking.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/api/upload/image")
    public Result<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) {
        return Result.success(mediaService.upload(file));
    }

    @GetMapping("/api/media/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Integer id) {
        MediaFile media = mediaService.get(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400")
                .contentType(MediaType.parseMediaType(media.getContentType()))
                .body(media.getData());
    }
}
