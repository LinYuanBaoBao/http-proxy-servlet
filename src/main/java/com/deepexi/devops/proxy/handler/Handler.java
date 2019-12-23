package com.deepexi.devops.proxy.handler;

import com.deepexi.devops.proxy.enums.HandlerType;
import com.deepexi.devops.proxy.RequestContext;

/**
 * 该接口声明了一个 Handler（处理器）所需实现的基本方法
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-19
 */
public interface Handler {

    /**
     * 执行处理操作
     * @param requestContext 请求上下文
     */
    void doHandle(RequestContext requestContext);

    /**
     * 获取处理器类型
     * @return 参考 {@link HandlerType}
     */
    HandlerType getType();

    /**
     * 返回值决定了处理器在链中的顺序
     * @return 通常值越小越先执行，但有两个特殊值：0（最先执行），-1（最后执行）
     */
    Integer getOrder();

}
