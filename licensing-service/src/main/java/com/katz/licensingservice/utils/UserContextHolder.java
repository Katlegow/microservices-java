package com.katz.licensingservice.utils;

import com.katz.licensingservice.model.UserContext;

public class UserContextHolder {
    private static final ThreadLocal<UserContext> userContextThreadLocal = new ThreadLocal<>();

    public static void set(UserContext userContext) {
        userContextThreadLocal.set(userContext);
    }

    public static UserContext get() {
        return userContextThreadLocal.get();
    }

    public static void remove() {
        userContextThreadLocal.remove();
    }
}
