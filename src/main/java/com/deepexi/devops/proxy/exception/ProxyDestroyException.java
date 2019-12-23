package com.deepexi.devops.proxy.exception;

import com.deepexi.devops.proxy.RequestContext;

/**
 * 销毁 Proxy 对象资源异常
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-18
 */
public class ProxyDestroyException extends ProxyException {

    public ProxyDestroyException(String message, RequestContext requestContext) {
        super(message, requestContext);
    }
}
