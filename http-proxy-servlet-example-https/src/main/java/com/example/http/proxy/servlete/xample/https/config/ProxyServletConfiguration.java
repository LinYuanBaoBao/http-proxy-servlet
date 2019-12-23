package com.example.http.proxy.servlete.xample.https.config;

import com.deepexi.devops.proxy.*;
import com.deepexi.devops.proxy.handler.post.ResponseHandler;
import com.deepexi.devops.proxy.handler.proxy.ProxyHandler;
import com.deepexi.devops.proxy.support.httpclient.ssl.*;
import com.example.http.proxy.servlete.xample.https.handler.TargetHandler;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.ServletWrappingController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.security.KeyStore;
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

            // HTTPS 双向认证
            KeyStoreLoader keyStoreLoader = new KeyStoreLoader() {
                @Override
                public KeyStoreModel loadKeyStoreModel(RequestContext requestContext) {
                    try {
                        KeyStore keyStore = KeyStore.getInstance("PKCS12");
                        File certificateFile = ResourceUtils.getFile("classpath:client.p12");
                        KeyStore.PasswordProtection password = new KeyStore.PasswordProtection("123456".toCharArray());
                        keyStore.load(FileUtils.openInputStream(certificateFile), password.getPassword());
                        return new KeyStoreModel(keyStore, password);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            DefaultSSLContextInitializer sslContextInitializer = new DefaultSSLContextInitializer(keyStoreLoader);
            DefaultHttpsConnectionSocketFactoryRegister httpsConnectionSocketFactoryRegister = new DefaultHttpsConnectionSocketFactoryRegister(sslContextInitializer);
            addHandler(new HttpsClientProxyInitHandler(httpsConnectionSocketFactoryRegister));

            addHandler(new ProxyHandler());
            addHandler(new ResponseHandler());
        }
    }

}
