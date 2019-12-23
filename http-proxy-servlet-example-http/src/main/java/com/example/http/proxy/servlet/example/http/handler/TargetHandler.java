package com.example.http.proxy.servlet.example.http.handler;

import com.deepexi.devops.proxy.RequestContext;
import com.deepexi.devops.proxy.TargetHost;
import com.deepexi.devops.proxy.enums.HandlerType;
import com.deepexi.devops.proxy.handler.Handler;

public class TargetHandler implements Handler {

    @Override
    public void doHandle(RequestContext requestContext) {
        // 实际请求的目标主机信息
        TargetHost targetHost = new TargetHost("localhost",8082);
        requestContext.setTargetHost(targetHost);
    }

    @Override
    public HandlerType getType() {
        return HandlerType.PRE_PROXY;
    }

    @Override
    public Integer getOrder() {
        return Integer.MIN_VALUE;
    }

}
