package com.deepexi.devops.proxy.handler.post;

import com.deepexi.devops.proxy.RequestContext;
import com.deepexi.devops.proxy.enums.HandlerType;
import com.deepexi.devops.proxy.handler.Handler;

/**
 * 将 Handler 将代理结果响应回调用方
 *
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-19
 */
public class ResponseHandler implements Handler {

    @Override
    public void doHandle(RequestContext requestContext) {
        requestContext.getProxy().response();
    }

    @Override
    public HandlerType getType() {
        return HandlerType.POST_PROXY;
    }

    @Override
    public Integer getOrder() {
        return Integer.MAX_VALUE;
    }
}
