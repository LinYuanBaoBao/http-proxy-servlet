package com.deepexi.devops.proxy.handler.pre;

import com.deepexi.devops.proxy.handler.Handler;
import com.deepexi.devops.proxy.handler.ProxyInitializer;

/**
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-20
 */
public abstract class AbstractProxyInitHandler implements Handler, ProxyInitializer {

    @Override
    public Integer getOrder() {
        return Integer.MIN_VALUE;
    }

}
