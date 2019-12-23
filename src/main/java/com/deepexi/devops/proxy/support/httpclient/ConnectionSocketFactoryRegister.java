package com.deepexi.devops.proxy.support.httpclient;

import com.deepexi.devops.proxy.RequestContext;
import org.apache.http.config.Registry;
import org.apache.http.conn.socket.ConnectionSocketFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Socket 连接工厂注入器
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-19
 */
public interface ConnectionSocketFactoryRegister {
    Registry<ConnectionSocketFactory> registry(RequestContext requestContext);
}
