package com.deepexi.devops.proxy.exception;

import com.deepexi.devops.proxy.RequestContext;

/**
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-18
 */
public class RequestBodyException extends ProxyRequestException {

    public RequestBodyException(String message, RequestContext requestContext) {
        super(message, requestContext);
    }
}
