package com.deepexi.devops.proxy;

import javax.net.ssl.SSLContext;

/**
 * 该接口声明了 SSLContext 对象的创建方法
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-18
 */
public interface SSLContextInitializer {

    SSLContext init(RequestContext requestContext);

}
