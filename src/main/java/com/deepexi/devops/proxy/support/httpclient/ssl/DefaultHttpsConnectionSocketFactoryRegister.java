package com.deepexi.devops.proxy.support.httpclient.ssl;

import com.deepexi.devops.proxy.SSLContextInitializer;
import lombok.Data;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.servlet.http.HttpServletRequest;

/**
 * 默认的 HTTPS Socket 连接工厂注入器
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-19
 */
@Data
public class DefaultHttpsConnectionSocketFactoryRegister implements HttpsConnectionSocketFactoryRegister {

    private SSLContextInitializer sslContextInitializer;
    private HostnameVerifier hostnameVerifier = new DefaultHostnameVerifier();

    public DefaultHttpsConnectionSocketFactoryRegister(SSLContextInitializer sslContextInitializer) {
        this.sslContextInitializer = sslContextInitializer;
    }

    @Override
    public Registry<ConnectionSocketFactory> registry(HttpServletRequest request) {
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                sslContextInitializer.init(request),
                getSupportProtocol(),
                null,
                hostnameVerifier);
        return RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslConnectionSocketFactory)
                .build();
    }

    /**
     * 获取支持的协议
     *
     * @return 协议名称数组
     */
    public String[] getSupportProtocol() {
        return new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"};
    }

    private static class DefaultHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }

}
