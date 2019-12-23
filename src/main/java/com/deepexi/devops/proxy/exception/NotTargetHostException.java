package com.deepexi.devops.proxy.exception;

import com.deepexi.devops.proxy.RequestContext;

/**
 * 缺少目标主机异常
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-18
 */
public class NotTargetHostException extends ProxyRequestException {

    public NotTargetHostException(String message, RequestContext requestContext) {
        super(message, requestContext);
    }
}
