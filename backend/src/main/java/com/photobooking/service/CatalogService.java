package com.photobooking.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.photobooking.dto.PortfolioVo;
import com.photobooking.entity.*;
import com.photobooking.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final BannerMapper bannerMapper;
    private final NoticeMapper noticeMapper;
    private final PhotoTypeMapper typeMapper;
    private final PhotoServiceMapper serviceMapper;
    private final PortfolioMapper portfolioMapper;
    private final PortfolioService portfolioService;

    public List<Banner> banners() {
        return bannerMapper.selectList(null);
    }

    public List<Notice> notices() {
        return noticeMapper.selectList(new LambdaQueryWrapper<Notice>().orderByDesc(Notice::getId));
    }

    public Notice addNotice(Notice notice) {
        if (notice.getTitle() == null || notice.getTitle().isBlank()) {
            throw new com.photobooking.common.BusinessException("请输入公告标题");
        }
        if (notice.getTime() == null) {
            notice.setTime(java.time.LocalDate.now().toString());
        }
        noticeMapper.insert(notice);
        return notice;
    }

    public Notice updateNotice(Integer id, Notice updates) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new com.photobooking.common.BusinessException("公告不存在");
        }
        if (updates.getTitle() != null && !updates.getTitle().isBlank()) {
            notice.setTitle(updates.getTitle());
        }
        if (updates.getContent() != null) {
            notice.setContent(updates.getContent());
        }
        if (updates.getTime() != null && !updates.getTime().isBlank()) {
            notice.setTime(updates.getTime());
        }
        noticeMapper.updateById(notice);
        return notice;
    }

    public void deleteNotice(Integer id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new com.photobooking.common.BusinessException("公告不存在");
        }
        noticeMapper.deleteById(id);
    }

    public List<PhotoType> categories() {
        return typeMapper.selectList(null);
    }

    public List<PhotoService> services(String keyword, String style, Integer typeId,
                                       BigDecimal priceMin, BigDecimal priceMax) {
        LambdaQueryWrapper<PhotoService> qw = new LambdaQueryWrapper<>();
        qw.eq(PhotoService::getStatus, "上架");
        if (style != null && !style.isBlank()) qw.eq(PhotoService::getStyle, style);
        if (typeId != null) qw.eq(PhotoService::getTypeId, typeId);
        if (priceMin != null) qw.ge(PhotoService::getPrice, priceMin);
        if (priceMax != null) qw.le(PhotoService::getPrice, priceMax);
        List<PhotoService> list = serviceMapper.selectList(qw);
        if (keyword != null && !keyword.isBlank()) {
            String k = keyword.trim();
            list = list.stream().filter(s ->
                    s.getName().contains(k) ||
                            (s.getTags() != null && s.getTags().contains(k)) ||
                            (s.getStyle() != null && s.getStyle().contains(k))
            ).toList();
        }
        return list;
    }

    public PhotoService getService(Integer id) {
        return serviceMapper.selectById(id);
    }

    public List<PhotoService> servicesByPhotographer(Integer photographerId) {
        return serviceMapper.selectList(new LambdaQueryWrapper<PhotoService>()
                .eq(PhotoService::getPhotographerId, photographerId));
    }

    public List<PortfolioVo> portfolios(String keyword, Integer photographerId) {
        return portfolioService.listPublic(keyword, photographerId);
    }

    public Portfolio getPortfolio(Integer id) {
        Portfolio p = portfolioMapper.selectById(id);
        if (p == null) {
            throw new com.photobooking.common.BusinessException("作品不存在");
        }
        portfolioService.assertVisible(p);
        return p;
    }

    public List<Portfolio> portfoliosByPhotographer(Integer photographerId) {
        return portfolioService.listByPhotographer(photographerId);
    }
}
