package com.deepexi.devops.proxy;

import com.deepexi.devops.proxy.handler.post.ResponseHandler;
import com.deepexi.devops.proxy.handler.proxy.ProxyHandler;
import com.deepexi.devops.proxy.support.httpclient.HttpClientProxyInitHandler;

/**
 * 继承自 ProxyServlet，并实现了 initHandler() 方法添加了一些用于支持 HTTP 请求代理的处理器。
 * <ul>
 * <li>前置 Handler-{@link HttpClientProxyInitHandler}，用于初始化 HttpClientProxy 代理对象</li>
 * <li>代理 Handler-{@link ProxyHandler}，用于执行代理请求</li>
 * <li>后置 Handler-{@link ResponseHandler}，用于将代理请求结果响应回客户端</li>
 * </ul>
 *
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-18
 */
public class DefaultProxyServlet extends ProxyServlet {

    @Override
    public void initHandler() {
        addHandler(new HttpClientProxyInitHandler());
        addHandler(new ProxyHandler());
        addHandler(new ResponseHandler());
    }
}
