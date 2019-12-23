package com.deepexi.devops.proxy;

import com.deepexi.devops.proxy.enums.HandlerType;
import com.deepexi.devops.proxy.exception.HandlerIntegrityException;
import com.deepexi.devops.proxy.exception.ProxyException;
import com.deepexi.devops.proxy.handler.Handler;
import com.deepexi.devops.proxy.handler.ProxyInitializer;
import com.deepexi.devops.proxy.util.ThreadLocalUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 流量代理 Servlet 抽象类，继承自 HttpServlet，它会执行 HandlerChain（处理器链）中的 Handler（处理器）完成流量代理功能。
 * <p>该类有一个 {@link #initHandler()} 抽象方法，用于初始化处理器，子类需要实现该方法。</p>
 * <p>
 * 一个基本的 ProxyServlet 至少需要三个 Handler：
 * <ul>
 * <li>
 * 继承自 {@link com.deepexi.devops.proxy.handler.pre.AbstractProxyInitHandler}，
 * 并实现用于初始化 {@link com.deepexi.devops.proxy.Proxy} 代理对象的 {@code initProxy()} 方法的前置处理器，
 * 可参考 {@link com.deepexi.devops.proxy.support.httpclient.HttpClientProxyInitHandler}
 * </li>
 * <li>
 * 用于执行 {@link com.deepexi.devops.proxy.Proxy} 代理对象 {@code proxy()} 请求代理方法的处理器，
 * 可参考 {@link com.deepexi.devops.proxy.handler.proxy.ProxyHandler}
 * </li>
 * <li>
 * 用于执行 {@link com.deepexi.devops.proxy.Proxy} 代理对象 {@code response()} 方法将请求代理结果响应回客户端的后置处理器，
 * 可参考 {@link com.deepexi.devops.proxy.handler.post.ResponseHandler}
 * </li>
 * </ul>
 *
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-18
 */
public abstract class ProxyServlet extends HttpServlet {

    private ProxyServletConfig servletConfig;
    private Map<HandlerType, List<Handler>> handlerChain = new HashMap<>();

    /**
     * 子类需实现该方法初始化处理器
     */
    public abstract void initHandler();

    @Override
    public void init() {
        this.servletConfig = new ProxyServletConfig(this.getServletConfig());
        initHandler();
        // TODO::辅助性措施，检查处理器完整性并给予提示
        checkHandlerIntegrity();
    }

    @Override
    protected final void service(HttpServletRequest request, HttpServletResponse response) throws ProxyException {
        RequestContext context = ThreadLocalUtils.getRequestContext(request, response);
        context.put(RequestContext.ATTR_SERVLET_CONFIG, servletConfig);
        try {
            this.doHandle(HandlerType.PRE_PROXY, context);
            this.doHandle(HandlerType.PROXY, context);
            this.doHandle(HandlerType.POST_PROXY, context);
        } finally {
            if (Objects.nonNull(context.getProxy())) {
                context.getProxy().destroy();
            }
            ThreadLocalUtils.clearRequestContext();
        }
    }

    /**
     * 添加 Handler 处理器
     * 并根据 {@code getOrder()} 方法返回值对处理器进行排序
     *
     * @param handler 处理器
     */
    public void addHandler(Handler handler) {
        handlerChain.putIfAbsent(handler.getType(), new LinkedList<>());
        List<Handler> handlers = handlerChain.get(handler.getType());
        handlers.add(handler);
        handlers.sort(Comparator.comparingInt(Handler::getOrder));
    }

    /**
     * 获取处理器链
     *
     * @return 处理器链
     */
    public Map<HandlerType, List<Handler>> getHandlerChain() {
        return handlerChain;
    }

    /**
     * 获取 ProxyServlet 配置信息
     *
     * @return 配置信息
     */
    public ProxyServletConfig getConfig() {
        return this.servletConfig;
    }

    private void doHandle(HandlerType type, RequestContext requestContext) {
        List<Handler> handlers = handlerChain.get(type);
        if (handlers != null) {
            for (Handler handler : handlers) {
                handler.doHandle(requestContext);
            }
        }
    }

    private void checkHandlerIntegrity() {
        List<Handler> preHandlers = handlerChain.get(HandlerType.PRE_PROXY);
        List<Handler> proxyHandlers = handlerChain.get(HandlerType.PROXY);
        List<Handler> postHandlers = handlerChain.get(HandlerType.POST_PROXY);
        String errorStr;
        if (proxyHandlers.size() < 1) {
            errorStr = "缺少用于代理请求的 Handler 处理器，代理功能\"可能存在异常\"";
            this.log(errorStr, new HandlerIntegrityException(errorStr));
        }
        if (postHandlers.size() < 1) {
            errorStr = "缺少用于将代理请求结果响应回客户端的后置 Handler 处理器，代理功能\"可能存在异常\"";
            this.log(errorStr, new HandlerIntegrityException(errorStr));
        }
        if (preHandlers.size() > 0) {
            for (Handler handler : preHandlers) {
                if (handler instanceof ProxyInitializer) {
                    return;
                }
            }
        }
        errorStr = "缺少用于初始化【Proxy】的前置 Handler 处理器，代理功能\"可能存在异常\"";
        this.log(errorStr, new HandlerIntegrityException(errorStr));
    }

}
