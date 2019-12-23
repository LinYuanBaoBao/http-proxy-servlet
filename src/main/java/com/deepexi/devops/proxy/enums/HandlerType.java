package com.deepexi.devops.proxy.enums;

/**
 * Handler（处理器）类型
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-18
 */
public enum HandlerType {
    /**
     * 请求代理前置处理器
     */
    PRE_PROXY,
    /**
     * 请求代理处理器
     */
    PROXY,
    /**
     * 请求代理后置处理器
     */
    POST_PROXY
}
