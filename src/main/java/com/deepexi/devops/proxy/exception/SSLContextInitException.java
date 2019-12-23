package com.deepexi.devops.proxy.exception;

import com.deepexi.devops.proxy.RequestContext;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;

/**
 * SSLContext 上下文对象初始化异常
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-18
 */
public class SSLContextInitException extends ProxyException {

    public SSLContextInitException(String message, RequestContext requestContext) {
        super(message, requestContext);
    }

}
