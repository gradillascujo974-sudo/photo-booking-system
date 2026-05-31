package com.photobooking.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.photobooking.common.BusinessException;
import com.photobooking.config.AuthInterceptor;
import com.photobooking.dto.PortfolioRequest;
import com.photobooking.dto.PortfolioVo;
import com.photobooking.entity.Portfolio;
import com.photobooking.entity.SysUser;
import com.photobooking.mapper.PortfolioMapper;
import com.photobooking.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    public static final String STATUS_ON = "上架";
    public static final String STATUS_OFF = "下架";

    private final PortfolioMapper portfolioMapper;
    private final SysUserMapper userMapper;

    public List<Portfolio> listByPhotographer(Integer photographerId) {
        return portfolioMapper.selectList(new LambdaQueryWrapper<Portfolio>()
                .eq(Portfolio::getPhotographerId, photographerId)
                .orderByDesc(Portfolio::getSortOrder)
                .orderByDesc(Portfolio::getId));
    }

    /** 用户端作品集：仅展示已上架（与摄影师后台同一 portfolio 表） */
    public List<PortfolioVo> listPublic(String keyword, Integer photographerId) {
        LambdaQueryWrapper<Portfolio> qw = new LambdaQueryWrapper<>();
        qw.and(w -> w.eq(Portfolio::getStatus, STATUS_ON).or().isNull(Portfolio::getStatus));
        if (photographerId != null) {
            qw.eq(Portfolio::getPhotographerId, photographerId);
        }
        qw.orderByDesc(Portfolio::getSortOrder).orderByDesc(Portfolio::getId);
        List<Portfolio> list = portfolioMapper.selectList(qw);
        if (keyword != null && !keyword.isBlank()) {
            String k = keyword.trim();
            list = list.stream().filter(p ->
                    p.getName().contains(k) || (p.getDescr() != null && p.getDescr().contains(k))
            ).toList();
        }
        return list.stream().map(this::toVo).collect(Collectors.toList());
    }

    public PortfolioVo toVo(Portfolio p) {
        PortfolioVo vo = new PortfolioVo();
        vo.setId(p.getId());
        vo.setName(p.getName());
        vo.setDescr(p.getDescr());
        vo.setImg(p.getImg());
        vo.setPhotographerId(p.getPhotographerId());
        vo.setReadCount(p.getReadCount());
        vo.setLikeCount(p.getLikeCount());
        vo.setStatus(isOnShelf(p.getStatus()) ? STATUS_ON : p.getStatus());
        vo.setSortOrder(p.getSortOrder());
        vo.setTime(p.getTime());
        SysUser photo = userMapper.selectById(p.getPhotographerId());
        vo.setPhotographerName(photo != null ? photo.getName() : "-");
        return vo;
    }

    public void normalizeStatusOnStartup() {
        List<Portfolio> all = portfolioMapper.selectList(null);
        for (Portfolio p : all) {
            if (!isOnShelf(p.getStatus())) {
                if (p.getStatus() == null || p.getStatus().isBlank()) {
                    p.setStatus(STATUS_ON);
                    portfolioMapper.updateById(p);
                }
            }
        }
    }

    public boolean isOnShelf(String status) {
        return status == null || status.isBlank() || STATUS_ON.equals(status);
    }

    public List<Map<String, Object>> listForAdmin(String keyword, String status, Integer photographerId) {
        LambdaQueryWrapper<Portfolio> qw = new LambdaQueryWrapper<>();
        if (status != null && !status.isBlank()) {
            qw.eq(Portfolio::getStatus, status);
        }
        if (photographerId != null) {
            qw.eq(Portfolio::getPhotographerId, photographerId);
        }
        qw.orderByDesc(Portfolio::getSortOrder).orderByDesc(Portfolio::getId);
        List<Portfolio> list = portfolioMapper.selectList(qw);
        if (keyword != null && !keyword.isBlank()) {
            String k = keyword.trim();
            list = list.stream().filter(p ->
                    p.getName().contains(k) || (p.getDescr() != null && p.getDescr().contains(k))
            ).toList();
        }
        return list.stream().map(this::toAdminVo).collect(Collectors.toList());
    }

    public Portfolio createByPhotographer(Integer userId, PortfolioRequest req) {
        requirePhotographer(userId);
        validateRequest(req);
        Portfolio p = new Portfolio();
        p.setPhotographerId(userId);
        fillPortfolio(p, req);
        p.setStatus(STATUS_ON);
        p.setReadCount(0);
        p.setLikeCount(0);
        p.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0);
        p.setTime(now());
        portfolioMapper.insert(p);
        return p;
    }

    public Portfolio updateByPhotographer(Integer userId, Integer id, PortfolioRequest req) {
        requirePhotographer(userId);
        validateRequest(req);
        Portfolio p = requireOwned(userId, id);
        fillPortfolio(p, req);
        portfolioMapper.updateById(p);
        return p;
    }

    @Transactional
    public void deleteByPhotographer(Integer userId, Integer id) {
        requirePhotographer(userId);
        Portfolio p = requireOwned(userId, id);
        portfolioMapper.deleteById(p.getId());
    }

    public Portfolio createByAdmin(PortfolioRequest req) {
        if (req.getPhotographerId() == null) {
            throw new BusinessException("请选择摄影师");
        }
        SysUser photo = userMapper.selectById(req.getPhotographerId());
        if (photo == null || !"PHOTOGRAPHER".equals(photo.getRole())) {
            throw new BusinessException("摄影师不存在");
        }
        validateRequest(req);
        Portfolio p = new Portfolio();
        p.setPhotographerId(req.getPhotographerId());
        fillPortfolio(p, req);
        p.setStatus(req.getStatus() != null ? req.getStatus() : STATUS_ON);
        p.setReadCount(0);
        p.setLikeCount(0);
        p.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0);
        p.setTime(now());
        portfolioMapper.insert(p);
        return p;
    }

    public Portfolio updateByAdmin(Integer id, PortfolioRequest req) {
        Portfolio p = portfolioMapper.selectById(id);
        if (p == null) throw new BusinessException("作品不存在");
        if (req.getPhotographerId() != null) {
            SysUser photo = userMapper.selectById(req.getPhotographerId());
            if (photo == null || !"PHOTOGRAPHER".equals(photo.getRole())) {
                throw new BusinessException("摄影师不存在");
            }
            p.setPhotographerId(req.getPhotographerId());
        }
        if (req.getName() != null && !req.getName().isBlank()) {
            p.setName(req.getName().trim());
        }
        if (req.getDescr() != null) p.setDescr(req.getDescr());
        if (req.getImg() != null && !req.getImg().isBlank()) p.setImg(req.getImg().trim());
        if (req.getStatus() != null) p.setStatus(req.getStatus());
        if (req.getSortOrder() != null) p.setSortOrder(req.getSortOrder());
        portfolioMapper.updateById(p);
        return p;
    }

    public void deleteByAdmin(Integer id) {
        if (portfolioMapper.selectById(id) == null) {
            throw new BusinessException("作品不存在");
        }
        portfolioMapper.deleteById(id);
    }

    public void assertVisible(Portfolio p) {
        if (p == null) throw new BusinessException("作品不存在");
        if (!isOnShelf(p.getStatus()) && STATUS_OFF.equals(p.getStatus())) {
            throw new BusinessException("该作品已下架");
        }
    }

    private Portfolio requireOwned(Integer userId, Integer id) {
        Portfolio p = portfolioMapper.selectById(id);
        if (p == null) throw new BusinessException("作品不存在");
        if (!p.getPhotographerId().equals(userId)) {
            throw new BusinessException("无权操作该作品");
        }
        return p;
    }

    private void requirePhotographer(Integer userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null || !"PHOTOGRAPHER".equals(user.getRole())) {
            throw new BusinessException("仅摄影师可操作");
        }
    }

    private void validateRequest(PortfolioRequest req) {
        if (req.getName() == null || req.getName().isBlank()) {
            throw new BusinessException("请输入作品名称");
        }
        if (req.getImg() == null || req.getImg().isBlank()) {
            throw new BusinessException("请填写封面图片");
        }
    }

    private void fillPortfolio(Portfolio p, PortfolioRequest req) {
        p.setName(req.getName().trim());
        p.setDescr(req.getDescr());
        p.setImg(req.getImg().trim());
        if (req.getStatus() != null) p.setStatus(req.getStatus());
        if (req.getSortOrder() != null) p.setSortOrder(req.getSortOrder());
    }

    private Map<String, Object> toAdminVo(Portfolio p) {
        Map<String, Object> vo = new HashMap<>();
        vo.put("id", p.getId());
        vo.put("name", p.getName());
        vo.put("descr", p.getDescr());
        vo.put("img", p.getImg());
        vo.put("photographerId", p.getPhotographerId());
        vo.put("readCount", p.getReadCount());
        vo.put("likeCount", p.getLikeCount());
        vo.put("status", p.getStatus());
        vo.put("sortOrder", p.getSortOrder());
        vo.put("time", p.getTime());
        SysUser photo = userMapper.selectById(p.getPhotographerId());
        vo.put("photographerName", photo != null ? photo.getName() : "-");
        return vo;
    }

    private String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
