package com.deepexi.devops.proxy;

import lombok.Data;

/**
 * 代理请求的目标主机信息
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-19
 */
@Data
public class TargetHost {

    private String host;
    private Integer port;

    public TargetHost() {
    }

    public TargetHost(String host) {
        this(host, -1);
    }

    public TargetHost(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public String toAddr() {
        return (port != -1) ? host + ":" + port : host;
    }
}
