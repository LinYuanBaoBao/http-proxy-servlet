package com.deepexi.devops.proxy.handler.proxy;

import com.deepexi.devops.proxy.RequestContext;
import com.deepexi.devops.proxy.enums.HandlerType;
import com.deepexi.devops.proxy.handler.Handler;

/**
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-23
 */
public class ProxyHandler implements Handler {

    @Override
    public void doHandle(RequestContext requestContext) {
        requestContext.getProxy().proxy();
    }

    @Override
    public HandlerType getType() {
        return HandlerType.PROXY;
    }

    @Override
    public Integer getOrder() {
        return Integer.MIN_VALUE;
    }
}
