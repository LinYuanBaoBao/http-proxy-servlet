package com.deepexi.devops.proxy;

import lombok.Getter;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.HeaderGroup;

/**
 * Proxy 代理抽象类
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-20
 */
public abstract class AbstractProxy implements Proxy {

    @Getter
    private static final HeaderGroup skipHeaders;
    @Getter
    private RequestContext requestContext;

    static {
        skipHeaders = new HeaderGroup();
        String[] headers = new String[]{
                "Connection",
                "Keep-Alive",
                "HttpClientProxy-Authenticate",
                "HttpClientProxy-Authorization",
                "TE",
                "Trailers",
                "Transfer-Encoding",
                "Upgrade"
        };
        for (String header : headers) {
            skipHeaders.addHeader(new BasicHeader(header, null));
        }
    }

    public AbstractProxy(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

}
