package com.deepexi.devops.proxy;

import com.deepexi.devops.proxy.enums.Scheme;
import com.deepexi.devops.proxy.exception.ProxyURIException;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * 该类表示请求上下文
 *
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-19
 */
public class RequestContext extends HashMap<String, Object> {

    public static final String ATTR_SERVLET_CONFIG = "ATTR-SERVLET-CONFIG";

    @Getter
    private String id;
    @Getter
    @Setter
    private Scheme scheme;
    @Getter
    private HttpServletRequest request;
    @Getter
    private HttpServletResponse response;
    @Getter
    @Setter
    private TargetHost targetHost;
    @Getter
    @Setter
    private Proxy proxy;

    public RequestContext(HttpServletRequest request, HttpServletResponse response) {
        this(request, response, Scheme.HTTP);
    }

    public RequestContext(HttpServletRequest request, HttpServletResponse response, Scheme scheme) {
        this.id = UUID.randomUUID().toString();
        this.request = request;
        this.response = response;
        this.scheme = scheme;
    }

    public ProxyServletConfig getServletConfig() {
        return getAttribute(ATTR_SERVLET_CONFIG);
    }

    public <T> T getAttribute(String key) {
        return (T) this.get(key);
    }

    public URI getProxyURI() {
        try {
            StringBuilder uri = new StringBuilder();
            uri.append(getScheme().name().toLowerCase() + "://");
            uri.append(targetHost.toAddr());
            uri.append(getTargetAPI());
            URIBuilder uriBuilder = new URIBuilder(uri.toString());
            //params
            uriBuilder.setParameters(getProxyUriParam());
            return uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new ProxyURIException(String.format("构建代理URI失败，错误信息：%s", e.getMessage()), this);
        }
    }

    private String getTargetAPI() {
        String targetURI = request.getRequestURI();
        int contextLength = getServletConfig().getContext().length();
        if (targetURI.length() >= contextLength) {
            targetURI = targetURI.substring(contextLength);
        }
        return targetURI;
    }

    private List<NameValuePair> getProxyUriParam() {
        List<NameValuePair> params = new LinkedList<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            BasicNameValuePair param = new BasicNameValuePair(key, request.getParameter(key));
            params.add(param);
        }
        return params;
    }

}
