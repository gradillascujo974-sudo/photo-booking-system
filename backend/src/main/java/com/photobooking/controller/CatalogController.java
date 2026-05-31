package com.photobooking.controller;

import com.photobooking.common.Result;
import com.photobooking.config.UserContext;
import com.photobooking.dto.PortfolioVo;
import com.photobooking.entity.*;
import com.photobooking.service.CatalogService;
import com.photobooking.service.FavoriteService;
import com.photobooking.service.LikeService;
import com.photobooking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;
    private final UserService userService;
    private final LikeService likeService;
    private final FavoriteService favoriteService;

    @GetMapping("/banners")
    public Result<List<Banner>> banners() {
        return Result.success(catalogService.banners());
    }

    @GetMapping("/notices")
    public Result<List<Notice>> notices() {
        return Result.success(catalogService.notices());
    }

    @GetMapping("/categories")
    public Result<List<PhotoType>> categories() {
        return Result.success(catalogService.categories());
    }

    @GetMapping("/services")
    public Result<List<PhotoService>> services(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String style,
            @RequestParam(required = false) Integer typeId,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax) {
        return Result.success(catalogService.services(keyword, style, typeId, priceMin, priceMax));
    }

    @GetMapping("/services/{id}")
    public Result<Map<String, Object>> serviceDetail(@PathVariable Integer id) {
        PhotoService service = catalogService.getService(id);
        Map<String, Object> data = new HashMap<>();
        data.put("service", service);
        data.put("photographer", userService.getPublicById(service.getPhotographerId()));
        data.put("favorited", favoriteService.isFavorited(id, UserContext.getUserId()));
        return Result.success(data);
    }

    @GetMapping("/photographers")
    public Result<List<Map<String, Object>>> photographers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city) {
        return Result.success(userService.listPhotographers(keyword, city));
    }

    @GetMapping("/photographers/{id}")
    public Result<Map<String, Object>> photographer(@PathVariable Integer id) {
        return Result.success(userService.getPublicById(id));
    }

    @GetMapping("/portfolios")
    public Result<List<PortfolioVo>> portfolios(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer photographerId) {
        return Result.success(catalogService.portfolios(keyword, photographerId));
    }

    @GetMapping("/portfolios/{id}")
    public Result<Map<String, Object>> portfolioDetail(@PathVariable Integer id) {
        Portfolio portfolio = catalogService.getPortfolio(id);
        Map<String, Object> data = new HashMap<>();
        data.put("portfolio", portfolio);
        data.put("photographer", userService.getPublicById(portfolio.getPhotographerId()));
        data.put("liked", likeService.isLiked(id, UserContext.getUserId()));
        data.put("likeCount", likeService.getLikeCount(id));
        return Result.success(data);
    }

    @GetMapping("/photographer/services")
    public Result<List<PhotoService>> myServices() {
        Integer uid = com.photobooking.config.AuthInterceptor.requireUserId();
        return Result.success(catalogService.servicesByPhotographer(uid));
    }

    @GetMapping("/photographer/portfolios")
    public Result<List<Portfolio>> myPortfolios() {
        Integer uid = com.photobooking.config.AuthInterceptor.requireUserId();
        return Result.success(catalogService.portfoliosByPhotographer(uid));
    }
}
