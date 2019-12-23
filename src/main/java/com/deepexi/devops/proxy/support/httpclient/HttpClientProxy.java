package com.deepexi.devops.proxy.support.httpclient;

import com.deepexi.devops.proxy.AbstractProxy;
import com.deepexi.devops.proxy.FullHttpRequest;
import com.deepexi.devops.proxy.RequestContext;
import com.deepexi.devops.proxy.exception.*;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.util.Enumeration;
import java.util.Objects;

import static org.apache.http.HttpHeaders.CONTENT_LENGTH;
import static org.apache.http.HttpHeaders.HOST;
import static org.apache.http.cookie.SM.SET_COOKIE;
import static org.apache.http.cookie.SM.SET_COOKIE2;
import static org.apache.http.protocol.HTTP.TRANSFER_ENCODING;

/**
 * 基于 HttpClient 实现的 Proxy
 *
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-20
 */
public class HttpClientProxy extends AbstractProxy {

    private CloseableHttpClient httpClient;
    private FullHttpRequest proxyRequest;
    private HttpResponse proxyResponse;

    public HttpClientProxy(RequestContext requestContext, CloseableHttpClient httpClient) {
        super(requestContext);
        this.httpClient = httpClient;
    }

    @Override
    public void proxy() throws ProxyRequestException {
        checkTargetHost();
        initProxyRequest();
        setXForwardedForHeader();
        doProxyRequest();
    }

    @Override
    public void response() throws ProxyResponseException {
        try {
            setResponseStatus();
            setResponseHeaders();
            setResponseEntity();
        } finally {
            if (Objects.nonNull(proxyResponse)) {
                EntityUtils.consumeQuietly(proxyResponse.getEntity());
            }
        }
    }

    @Override
    public void destroy() throws ProxyDestroyException {
        try {
            if (Objects.nonNull(httpClient)) {
                httpClient.close();
            }
        } catch (Exception e) {
            throw new ProxyDestroyException(String.format("HttpClientProxy对象资源销毁失败，错误信息：", e.getMessage()), getRequestContext());
        }
    }

    protected void setResponseStatus() {
        getRequestContext()
                .getResponse()
                .setStatus(proxyResponse.getStatusLine().getStatusCode());
    }

    protected void setResponseHeaders() {
        for (Header header : proxyResponse.getAllHeaders()) {
            copyResponseHeader(header);
        }
    }

    protected void setResponseEntity() {
        try {
            HttpEntity entity = proxyResponse.getEntity();
            if (entity != null) {
                OutputStream stream = getRequestContext().getResponse().getOutputStream();
                entity.writeTo(stream);
            }
        } catch (IOException e) {
            throw new ProxyResponseException(String.format("更新响应体失败，错误信息：", e.getMessage()), getRequestContext());
        }
    }

    private void copyResponseHeader(Header header) {
        String headerName = header.getName();
        if (getSkipHeaders().containsHeader(headerName)) {
            return;
        }
        String headerValue = header.getValue();
        if (headerName.equalsIgnoreCase(SET_COOKIE) || headerName.equalsIgnoreCase(SET_COOKIE2)) {
            copyProxyCookie(headerValue);
        } else {
            getRequestContext()
                    .getResponse()
                    .addHeader(headerName, headerValue);
        }
    }

    private void copyProxyCookie(String headerValue) {
        HttpServletRequest request = getRequestContext().getRequest();
        String path = request.getContextPath() + request.getServletPath();
        if (path.isEmpty()) {
            path = "/";
        }

        for (HttpCookie cookie : HttpCookie.parse(headerValue)) {
            String proxyCookieName = cookie.getName();
            Cookie servletCookie = new Cookie(proxyCookieName, cookie.getValue());
            servletCookie.setComment(cookie.getComment());
            servletCookie.setMaxAge((int) cookie.getMaxAge());
            servletCookie.setPath(path);
            servletCookie.setSecure(cookie.getSecure());
            servletCookie.setVersion(cookie.getVersion());
            servletCookie.setHttpOnly(cookie.isHttpOnly());
            getRequestContext().getResponse().addCookie(servletCookie);
        }
    }

    protected void doProxyRequest() {
        try {
            proxyResponse = httpClient.execute(proxyRequest);
        } catch (Exception e) {
            throw new ProxyRequestException(e.getMessage(), getRequestContext());
        }
    }

    protected void initProxyRequest() {
        proxyRequest = new FullHttpRequest();
        proxyRequest.setMethod(getRequestContext().getRequest().getMethod());
        proxyRequest.setURI(getRequestContext().getProxyURI());
        proxyRequest.setEntity(getRequestBody());
        initRequestHeaders();
        setXForwardedForHeader();
    }

    protected void initRequestHeaders() {
        Enumeration<String> headerNames = getRequestContext().getRequest().getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            copyProxyRequestHeader(headerName);
        }
    }

    protected void setXForwardedForHeader() {
        HttpServletRequest request = getRequestContext().getRequest();
        String forHeaderName = "X-Forwarded-For";
        String forHeader = request.getRemoteAddr();
        String existingForHeader = request.getHeader(forHeaderName);
        if (existingForHeader != null) {
            forHeader = existingForHeader + ", " + forHeader;
        }
        proxyRequest.setHeader(forHeaderName, forHeader);

        String protoHeaderName = "X-Forwarded-Proto";
        String protoHeader = request.getScheme();
        proxyRequest.setHeader(protoHeaderName, protoHeader);
    }

    protected HttpEntity getRequestBody() {
        try {
            HttpServletRequest request = getRequestContext().getRequest();
            if (request.getHeader(CONTENT_LENGTH) != null || request.getHeader(TRANSFER_ENCODING) != null) {
                InputStreamEntity entity = new InputStreamEntity(request.getInputStream(), getContentLength());
                entity.setContentType(request.getHeader(HttpHeaders.CONTENT_TYPE));
                entity.setContentEncoding(request.getHeader(HttpHeaders.CONTENT_ENCODING));
                int parseInt = Integer.parseInt(request.getHeader(CONTENT_LENGTH));
                if (parseInt == -1) {
                    entity.setChunked(false);
                }
                return entity;
            }
            return null;
        } catch (IOException e) {
            throw new RequestBodyException(String.format("获取请求体失败，错误信息：", e.getMessage()), getRequestContext());
        }
    }

    private void copyProxyRequestHeader(String headerName) {
        if (headerName.equalsIgnoreCase(CONTENT_LENGTH)) {
            return;
        }
        if (getSkipHeaders().containsHeader(headerName)) {
            return;
        }
        Enumeration<String> headers = getRequestContext().getRequest().getHeaders(headerName);
        while (headers.hasMoreElements()) {
            String headerValue = headers.nextElement();
            if (headerName.equalsIgnoreCase(HOST)) {
                headerValue = getRequestContext().getTargetHost().toAddr();
            }
            proxyRequest.addHeader(headerName, headerValue);
        }
    }

    private long getContentLength() {
        String contentLengthHeader = getRequestContext().getRequest().getHeader(CONTENT_LENGTH);
        return (contentLengthHeader != null) ? Long.parseLong(contentLengthHeader) : -1L;
    }

    private void checkTargetHost() {
        if (Objects.isNull(getRequestContext().getTargetHost())) {
            throw new NotTargetHostException("缺少目标主机信息", getRequestContext());
        }
    }
}
