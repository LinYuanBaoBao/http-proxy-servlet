package com.example.http.proxy.servlet.example.http.config;

import com.deepexi.devops.proxy.ProxyServlet;
import com.deepexi.devops.proxy.ProxyServletConfig;
import com.deepexi.devops.proxy.handler.post.ResponseHandler;
import com.deepexi.devops.proxy.handler.proxy.ProxyHandler;
import com.deepexi.devops.proxy.support.httpclient.HttpClientProxyInitHandler;
import com.example.http.proxy.servlet.example.http.handler.TargetHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.ServletWrappingController;

import java.util.Properties;

@Configuration
public class ProxyServletConfiguration {

    private final String PROXY_CONTROLLER = "proxyController";
    private final String PROXY_API = "/proxy";

    @Bean
    public ServletWrappingController proxyController() throws Exception {
        // Servlet 配置
        Properties properties = new Properties();
        properties.put(ProxyServletConfig.P_CONTEXT, PROXY_API);
        properties.put(ProxyServletConfig.P_LOG, true);
        properties.put(ProxyServletConfig.P_READ_TIMEOUT, -1);
        properties.put(ProxyServletConfig.P_CONNECTION_REQUEST_TIMEOUT, -1);
        properties.put(ProxyServletConfig.P_CONNECT_TIMEOUT, -1);

        ServletWrappingController controller = new ServletWrappingController();
        controller.setServletClass(SpringProxyServlet.class);
        controller.setBeanName(PROXY_CONTROLLER);
        controller.setInitParameters(properties);
        controller.afterPropertiesSet();
        return controller;
    }

    @Bean
    public SimpleUrlHandlerMapping proxyControllerMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        Properties urlProperties = new Properties();
        urlProperties.put(PROXY_API + "/**", PROXY_CONTROLLER);
        mapping.setMappings(urlProperties);
        mapping.setOrder(Integer.MAX_VALUE - 2);
        return mapping;
    }

    public static class SpringProxyServlet extends ProxyServlet {
        @Override
        public void initHandler() {
            addHandler(new TargetHandler());
            addHandler(new HttpClientProxyInitHandler());
            addHandler(new ProxyHandler());
            addHandler(new ResponseHandler());
        }
    }

}
