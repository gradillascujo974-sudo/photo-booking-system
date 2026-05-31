package com.photobooking.service;

import com.photobooking.common.BusinessException;
import com.photobooking.config.AuthInterceptor;
import com.photobooking.entity.MediaFile;
import com.photobooking.entity.SysUser;
import com.photobooking.mapper.MediaFileMapper;
import com.photobooking.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MediaService {

    private static final long MAX_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/jpg"
    );

    private final MediaFileMapper mediaFileMapper;
    private final SysUserMapper userMapper;

    public Map<String, Object> upload(MultipartFile file) {
        Integer userId = AuthInterceptor.requireUserId();
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!"PHOTOGRAPHER".equals(user.getRole()) && !"ADMIN".equals(user.getRole())) {
            throw new BusinessException("仅摄影师或管理员可上传图片");
        }
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择图片文件");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException("图片大小不能超过 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED.contains(contentType.toLowerCase())) {
            throw new BusinessException("仅支持 JPG、PNG、GIF、WebP 格式");
        }

        byte[] data;
        try {
            data = file.getBytes();
        } catch (IOException e) {
            throw new BusinessException("读取文件失败");
        }

        MediaFile media = new MediaFile();
        media.setUserId(userId);
        media.setFilename(file.getOriginalFilename());
        media.setContentType(contentType);
        media.setData(data);
        media.setSize((int) file.getSize());
        media.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        mediaFileMapper.insert(media);

        String url = "/api/media/" + media.getId();
        Map<String, Object> result = new HashMap<>();
        result.put("id", media.getId());
        result.put("url", url);
        return result;
    }

    public MediaFile get(Integer id) {
        MediaFile media = mediaFileMapper.selectById(id);
        if (media == null) {
            throw new BusinessException("图片不存在");
        }
        return media;
    }
}
