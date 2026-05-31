package com.photobooking.config;

import com.photobooking.service.PortfolioService;
import com.photobooking.entity.*;
import com.photobooking.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final SysUserMapper userMapper;
    private final PhotoTypeMapper typeMapper;
    private final PhotoServiceMapper serviceMapper;
    private final PortfolioMapper portfolioMapper;
    private final BannerMapper bannerMapper;
    private final NoticeMapper noticeMapper;
    private final PortfolioService portfolioService;

    @Override
    public void run(String... args) {
        portfolioService.normalizeStatusOnStartup();
        patchPhotographerContacts();
        if (userMapper.selectCount(null) > 0) {
            return;
        }
        seedUsers();
        seedTypes();
        seedServices();
        seedPortfolios();
        seedBanners();
        seedNotices();
    }

    /** 为已有摄影师补全演示用联系方式（仅空值时写入） */
    private void patchPhotographerContacts() {
        userMapper.selectList(null).stream()
                .filter(u -> "PHOTOGRAPHER".equals(u.getRole()))
                .forEach(u -> {
                    boolean changed = false;
                    if (u.getWechat() == null || u.getWechat().isBlank()) {
                        u.setWechat(u.getUsername() + "_wx");
                        changed = true;
                    }
                    if (u.getPayQrcode() == null || u.getPayQrcode().isBlank()) {
                        u.setPayQrcode("https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=pay-" + u.getUsername());
                        changed = true;
                    }
                    if (changed) userMapper.updateById(u);
                });
    }

    private void seedUsers() {
        insertUser("user123", "123456", "USER", "张小雅", "13800138001", "user@demo.com",
                new BigDecimal("500"), null, null, null, null, null, 0, new BigDecimal("5.0"));
        insertUser("photo01", "123456", "PHOTOGRAPHER", "李镜头", "13900139001", "photo@demo.com",
                BigDecimal.ZERO, "8年婚礼与人像经验，擅长自然光与纪实风格。", "景德镇", "婚礼,纪实,人像",
                "lijingtou_wx", "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=pay-deposit-photo01", 1, new BigDecimal("4.9"));
        insertUser("admin", "admin123", "ADMIN", "系统管理员", "13700137001", "admin@demo.com",
                BigDecimal.ZERO, null, null, null, null, null, 0, new BigDecimal("5.0"));
        insertUser("photo02", "123456", "PHOTOGRAPHER", "陈光影", "13900139002", "photo2@demo.com",
                BigDecimal.ZERO, "日系清新风格摄影师，旅拍经验丰富。", "南昌", "日系,旅拍,写真",
                "chenguangying_wx", "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=pay-deposit-photo02", 1, new BigDecimal("4.8"));
        insertUser("photo03", "123456", "PHOTOGRAPHER", "王快门", "13900139003", "photo3@demo.com",
                BigDecimal.ZERO, "商业摄影与证件照专家。", "九江", "商业,证件照",
                "wangkaimen_wx", "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=pay-deposit-photo03", 0, new BigDecimal("4.6"));
    }

    private void insertUser(String username, String password, String role, String name, String phone,
                            String email, BigDecimal account, String introduce, String city,
                            String styles, String wechat, String payQrcode, int certified, BigDecimal rating) {
        SysUser u = new SysUser();
        u.setUsername(username);
        u.setPassword(password);
        u.setRole(role);
        u.setName(name);
        u.setPhone(phone);
        u.setEmail(email);
        u.setAccount(account);
        u.setIntroduce(introduce);
        u.setCity(city);
        u.setStyles(styles);
        u.setWechat(wechat);
        u.setPayQrcode(payQrcode);
        u.setCertified(certified);
        u.setRating(rating);
        userMapper.insert(u);
    }

    private void seedTypes() {
        String[] names = {"婚礼摄影", "个人写真", "亲子纪实", "旅拍跟拍", "商业拍摄"};
        for (String name : names) {
            PhotoType t = new PhotoType();
            t.setName(name);
            typeMapper.insert(t);
        }
    }

    private void seedServices() {
        insertService("婚礼全天跟拍", "从化妆到晚宴全程记录", "含精修50张，原片全送",
                "https://images.unsplash.com/photo-1519741497674-611481863552?w=800&q=80",
                new BigDecimal("3888"), "婚礼", "跟拍,纪实", 1, 2, 1280, 86);
        insertService("个人写真套餐", "2小时棚拍或外景", "含妆造建议与15张精修",
                "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=800&q=80",
                new BigDecimal("699"), "人像", "写真,棚拍", 2, 2, 920, 142);
        insertService("日系旅拍半日", "城市漫步式拍摄", "适合情侣与闺蜜",
                "https://images.unsplash.com/photo-1520854221256-17451cc331bf?w=800&q=80",
                new BigDecimal("1288"), "日系", "旅拍,清新", 4, 4, 756, 58);
        insertService("亲子纪实跟拍", "记录成长瞬间", "不上妆，自然抓拍",
                "https://images.unsplash.com/photo-1511895426328-acd190e3a8ba?w=800&q=80",
                new BigDecimal("899"), "纪实", "亲子,家庭", 3, 2, 445, 33);
        insertService("商业产品拍摄", "电商主图与详情页", "按件计费，可议价",
                "https://images.unsplash.com/photo-1542038784456-1ea8e935640e?w=800&q=80",
                new BigDecimal("500"), "商业", "产品,电商", 5, 5, 312, 21);
    }

    private void insertService(String name, String descr, String content, String img, BigDecimal price,
                               String style, String tags, int typeId, int photoId, int read, int sale) {
        PhotoService s = new PhotoService();
        s.setName(name);
        s.setDescr(descr);
        s.setContent(content);
        s.setImg(img);
        s.setPrice(price);
        s.setStyle(style);
        s.setTags(tags);
        s.setTypeId(typeId);
        s.setPhotographerId(photoId);
        s.setReadCount(read);
        s.setSaleCount(sale);
        s.setStatus("上架");
        serviceMapper.insert(s);
    }

    private void seedPortfolios() {
        insertPortfolio("秋日婚礼纪实", "景德镇户外婚礼",
                "https://images.unsplash.com/photo-1606216794074-735e91aa2a92?w=600&q=80", 2, 2340, 186);
        insertPortfolio("清新人像集", "自然光棚拍作品",
                "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=600&q=80", 2, 1890, 245);
        insertPortfolio("京都旅拍风格", "日系街道与和服",
                "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?w=600&q=80", 4, 1560, 198);
        insertPortfolio("亲子日常", "家庭纪实摄影",
                "https://images.unsplash.com/photo-1476703993599-003027a8e0a2?w=600&q=80", 2, 980, 87);
    }

    private void insertPortfolio(String name, String descr, String img, int photoId, int read, int likes) {
        Portfolio p = new Portfolio();
        p.setName(name);
        p.setDescr(descr);
        p.setImg(img);
        p.setPhotographerId(photoId);
        p.setReadCount(read);
        p.setLikeCount(likes);
        p.setStatus(PortfolioService.STATUS_ON);
        p.setSortOrder(0);
        p.setTime("2026-05-01 10:00:00");
        portfolioMapper.insert(p);
    }

    private void seedBanners() {
        String[] imgs = {
                "https://images.unsplash.com/photo-1511285560929-80b456fea0bc?w=1400&q=80",
                "https://images.unsplash.com/photo-1522673607210-f26f0ebdca72?w=1400&q=80",
                "https://images.unsplash.com/photo-1465495976277-438e610ebb0f?w=1400&q=80"
        };
        for (int i = 0; i < imgs.length; i++) {
            Banner b = new Banner();
            b.setImg(imgs[i]);
            b.setServerId(i + 1);
            bannerMapper.insert(b);
        }
    }

    private void seedNotices() {
        insertNotice("平台上线公告", "光影约拍系统正式上线，欢迎摄影师入驻认证。", "2026-05-01");
        insertNotice("五一档期提醒", "热门摄影师档期紧张，请提前预约。", "2026-04-28");
    }

    private void insertNotice(String title, String content, String time) {
        Notice n = new Notice();
        n.setTitle(title);
        n.setContent(content);
        n.setTime(time);
        noticeMapper.insert(n);
    }
}
