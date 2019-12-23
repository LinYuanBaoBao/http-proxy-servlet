package com.deepexi.devops.proxy.support.httpclient;

import com.deepexi.devops.proxy.Proxy;
import com.deepexi.devops.proxy.ProxyServletConfig;
import com.deepexi.devops.proxy.RequestContext;
import com.deepexi.devops.proxy.enums.HandlerType;
import com.deepexi.devops.proxy.handler.pre.AbstractProxyInitHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import static com.deepexi.devops.proxy.enums.HandlerType.PRE_PROXY;

/**
 * HttpClientProxy 初始化处理器
 *
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-19
 */
public class HttpClientProxyInitHandler extends AbstractProxyInitHandler {

    @Override
    public void doHandle(RequestContext context) {
        context.setProxy(initProxy(context));
    }

    @Override
    public Proxy initProxy(RequestContext requestContext) {
        CloseableHttpClient httpClient = HttpClientBuilder
                .create()
                .setDefaultRequestConfig(buildRequestConfig(requestContext.getServletConfig()))
                .setDefaultSocketConfig(buildSocketConfig(requestContext.getServletConfig()))
                .build();
        return new HttpClientProxy(requestContext, httpClient);
    }

    @Override
    public HandlerType getType() {
        return PRE_PROXY;
    }

    protected RequestConfig buildRequestConfig(ProxyServletConfig config) {
        return RequestConfig
                .custom()
                .setConnectTimeout(config.getConnectTimeout())
                .setSocketTimeout(config.getReadTimeout())
                .setConnectionRequestTimeout(config.getConnectionRequestTimeout())
                .build();
    }

    protected SocketConfig buildSocketConfig(ProxyServletConfig config) {
        Integer readTimeout = config.getReadTimeout();
        if (readTimeout < 1) {
            return null;
        }
        return SocketConfig
                .custom()
                .setSoTimeout(readTimeout)
                .build();
    }
}
