package com.deepexi.devops.proxy.exception;

import com.deepexi.devops.proxy.RequestContext;

/**
 * 代理请求结果响应调用方异常
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-18
 */
public class ProxyResponseException extends ProxyException {

    public ProxyResponseException(String message, RequestContext requestContext) {
        super(message, requestContext);
    }
}
