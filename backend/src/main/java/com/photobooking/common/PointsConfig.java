package com.photobooking.common;

import java.math.BigDecimal;

public final class PointsConfig {
    public static final int CONTACT_POINTS = 3;
    public static final int CONFIRM_POINTS = 30;
    public static final String POINT_RATE_DESC = "1积分=1元";

    private PointsConfig() {}

    public static BigDecimal contactAmount() {
        return BigDecimal.valueOf(CONTACT_POINTS);
    }

    public static BigDecimal confirmAmount() {
        return BigDecimal.valueOf(CONFIRM_POINTS);
    }
}
