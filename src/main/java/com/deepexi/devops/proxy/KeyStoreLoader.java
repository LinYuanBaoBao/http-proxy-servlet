package com.deepexi.devops.proxy;

/**
 * 该接口声明了 KeyStoreModel 对象的创建方法
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-18
 */
public interface KeyStoreLoader {

    KeyStoreModel loadKeyStoreModel(RequestContext requestContext);

}

