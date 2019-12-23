package com.deepexi.devops.proxy.exception;

import com.deepexi.devops.proxy.RequestContext;

/**
 * 请求代理异常
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-18
 */
public class ProxyRequestException extends ProxyException {

    public ProxyRequestException(String message, RequestContext requestContext) {
        super(message, requestContext);
    }
}
