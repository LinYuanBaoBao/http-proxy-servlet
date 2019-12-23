package com.deepexi.devops.proxy.util;

import com.deepexi.devops.proxy.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author taccisum - liaojinfeng@deepexi.com
 * @since 2019-11-13
 */
public abstract class ThreadLocalUtils {
    private static ThreadLocal<RequestContext> context = new ThreadLocal<>();

    public static RequestContext getRequestContext(HttpServletRequest request, HttpServletResponse response) {
        if (context.get() == null) {
            context.set(new RequestContext(request, response));
        }
        return context.get();
    }

    public static void clearRequestContext() {
        context.remove();
    }
}
