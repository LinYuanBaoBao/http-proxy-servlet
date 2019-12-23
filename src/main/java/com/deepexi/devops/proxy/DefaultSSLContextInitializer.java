package com.deepexi.devops.proxy;

import com.deepexi.devops.proxy.exception.SSLContextInitException;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;

/**
 * 实现了 {@link SSLContextInitializer} 接口，提供了默认的 SSLContext 上下文对象初始化。
 * <ul>
 * 该类所创建的 SSLContext 上下文对象具有以下几个特点：
 * <li>跳过服务端的证书进行验证</li>
 * </ul>
 * 你也可以继承该类重新其相应的方法来实现定制化。
 *
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-18
 */
public class DefaultSSLContextInitializer implements SSLContextInitializer {

    private KeyStoreLoader keyStoreLoader;

    public DefaultSSLContextInitializer(KeyStoreLoader keyStoreLoader) {
        this.keyStoreLoader = keyStoreLoader;
    }

    @Override
    public final SSLContext init(RequestContext requestContext) {
        try {
            TrustManager[] trustManagers = loadTrustManager(requestContext);
            KeyManager[] keyManagers = loadKeyStoreManager(requestContext);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, trustManagers, null);
            return sslContext;
        } catch (Exception e) {
            throw new SSLContextInitException(String.format("SSLContext 上下文对象初始化异常，错误信息：", e.getMessage()), requestContext);
        }
    }

    /**
     * 获取认证管理器，用于认证目标服务端的证书
     *
     * @param requestContext 请求上下文
     * @return TrustManager 数组对象
     */
    protected TrustManager[] loadTrustManager(RequestContext requestContext) {
        return new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }
        };
    }

    /**
     * 获取密钥管理器，用于管理请求目标服务时的证书
     *
     * @param requestContext 请求上下文
     * @return KeyManager 数组对象
     */
    protected KeyManager[] loadKeyStoreManager(RequestContext requestContext) throws Exception {
        KeyStoreModel keyStoreModel = keyStoreLoader.loadKeyStoreModel(requestContext);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStoreModel.getKeyStore(), keyStoreModel.getPassword().getPassword());
        return keyManagerFactory.getKeyManagers();
    }
}
