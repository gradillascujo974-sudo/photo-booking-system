package com.photobooking.config;

public final class UserContext {
    private static final ThreadLocal<Integer> USER_ID = new ThreadLocal<>();

    private UserContext() {}

    public static void setUserId(Integer id) {
        USER_ID.set(id);
    }

    public static Integer getUserId() {
        return USER_ID.get();
    }

    public static void clear() {
        USER_ID.remove();
    }
}
