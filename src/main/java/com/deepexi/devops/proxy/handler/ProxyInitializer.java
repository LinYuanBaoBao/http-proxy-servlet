package com.deepexi.devops.proxy.handler;

import com.deepexi.devops.proxy.Proxy;
import com.deepexi.devops.proxy.RequestContext;

/**
 * 该接口声明了 Proxy 对象初始化方法
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-18
 */
public interface ProxyInitializer {
    Proxy initProxy(RequestContext requestContext);
}
