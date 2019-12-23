package com.deepexi.devops.proxy.support.httpclient.ssl;

import com.deepexi.devops.proxy.Proxy;
import com.deepexi.devops.proxy.RequestContext;
import com.deepexi.devops.proxy.enums.Scheme;
import com.deepexi.devops.proxy.support.httpclient.HttpClientProxy;
import com.deepexi.devops.proxy.support.httpclient.HttpClientProxyInitHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;

import javax.servlet.http.HttpServletRequest;

/**
 * 继承自 {@link HttpClientProxyInitHandler} ，并提供了 HTTPS 实现
 * <p>
 * 该处理器需要提供一个 {@link HttpsConnectionSocketFactoryRegister} 的实现类对象。
 * 用于创建基于 SSL 的 ConnectionSocketFactory。
 * </p>
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-19
 */
public class HttpsClientProxyInitHandler extends HttpClientProxyInitHandler {

    private HttpsConnectionSocketFactoryRegister register;

    public HttpsClientProxyInitHandler(HttpsConnectionSocketFactoryRegister register) {
        this.register = register;
    }

    @Override
    public Proxy initProxy(RequestContext requestContext) {
        HttpClientBuilder builder = HttpClientBuilder.create();
        if (requestContext.getScheme()== Scheme.HTTPS) {
            builder.setConnectionManager(new BasicHttpClientConnectionManager(register.registry(requestContext)));
        }

        CloseableHttpClient httpClient = builder
                .setDefaultRequestConfig(buildRequestConfig(requestContext.getServletConfig()))
                .setDefaultSocketConfig(buildSocketConfig(requestContext.getServletConfig()))
                .build();

        return new HttpClientProxy(requestContext, httpClient);
    }

}
