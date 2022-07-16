package com.katz.licensingservice.utils;

import com.katz.licensingservice.model.UserContext;


/**
 * (c) Katz Solutions
 * @Author: KatlegoM
 * Date: 20220713
 *
 * <p>
 *     A simple user context class
 * </p>
*/
public class UserContextHolder {
    public static ThreadLocal<UserContext> userContextThread = new ThreadLocal<>();

    public static UserContext get() {
        return userContextThread.get();
    }

    public static void set(UserContext userContext) {
        userContextThread.set(userContext);
    }

    public static void clear() {
        userContextThread.remove();
    }
}
