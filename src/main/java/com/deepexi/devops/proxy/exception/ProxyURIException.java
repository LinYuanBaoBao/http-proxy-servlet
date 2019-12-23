package com.deepexi.devops.proxy.exception;

import com.deepexi.devops.proxy.RequestContext;

/**
 * 代理 URI 异常
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-18
 */
public class ProxyURIException extends ProxyException {

    public ProxyURIException(String message, RequestContext requestContext) {
        super(message, requestContext);
    }
}
