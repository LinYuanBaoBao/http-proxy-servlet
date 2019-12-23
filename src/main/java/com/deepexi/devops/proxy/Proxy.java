package com.deepexi.devops.proxy;

import com.deepexi.devops.proxy.exception.ProxyDestroyException;
import com.deepexi.devops.proxy.exception.ProxyResponseException;
import com.deepexi.devops.proxy.exception.ProxyRequestException;

/**
 * 该接口声明了 Proxy 代理对象所需实现的基本方法
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-19
 */
public interface Proxy {

    /**
     * 执行代理请求
     * @throws ProxyRequestException 请求代理异常
     */
    void proxy() throws ProxyRequestException;

    /**
     * 将代理请求结果响应回调用方
     * @throws ProxyResponseException 代理请求结果响应客户端异常
     */
    void response() throws ProxyResponseException;

    /**
     * 销毁代理对象资源
     * @throws ProxyDestroyException Proxy 对象资源销毁异常
     */
    void destroy() throws ProxyDestroyException;

}
