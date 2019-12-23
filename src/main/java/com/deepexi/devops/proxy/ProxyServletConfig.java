package com.deepexi.devops.proxy;

import lombok.Getter;

import javax.servlet.ServletConfig;

/**
 * 该类表示 ProxyServlet 配置
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-19
 */
public class ProxyServletConfig {

    private ServletConfig servletConfig;

    public static final String P_LOG = "log";
    public static final String P_CONTEXT = "context";
    public static final String P_CONNECT_TIMEOUT = "connect.timeout";
    public static final String P_READ_TIMEOUT = "read.timeout";
    public static final String P_CONNECTION_REQUEST_TIMEOUT = "connection.request.timeout";

    @Getter
    protected Boolean doLog = true;
    @Getter
    protected String context = "";
    @Getter
    protected Integer connectTimeout = -1;
    @Getter
    protected Integer readTimeout = -1;
    @Getter
    protected Integer connectionRequestTimeout = -1;

    public ProxyServletConfig(ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
        this.initConfig();
    }

    private void initConfig() {
        String doLogStr = getConfigParam(P_LOG);
        if (doLogStr != null) {
            this.doLog = Boolean.parseBoolean(doLogStr);
        }

        String context = getConfigParam(P_CONTEXT);
        if (context != null) {
            this.context = context;
        }

        String connectTimeout = getConfigParam(P_CONNECT_TIMEOUT);
        if (connectTimeout != null) {
            this.connectTimeout = Integer.parseInt(connectTimeout);
        }

        String readTimeout = getConfigParam(P_READ_TIMEOUT);
        if (readTimeout != null) {
            this.readTimeout = Integer.parseInt(readTimeout);
        }

        String connectionRequestTimeout = getConfigParam(P_CONNECTION_REQUEST_TIMEOUT);
        if (connectionRequestTimeout != null) {
            this.connectionRequestTimeout = Integer.parseInt(connectionRequestTimeout);
        }

    }

    private String getConfigParam(String key) {
        return servletConfig.getInitParameter(key);
    }

}
