package com.deepexi.devops.proxy.exception;

import com.deepexi.devops.proxy.RequestContext;
import lombok.Getter;

/**
 * 代理异常
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-18
 */
public class ProxyException extends RuntimeException {

    @Getter
    private RequestContext requestContext;

    public ProxyException(String message, RequestContext requestContext) {
        super(message);
        this.requestContext = requestContext;
    }

}
