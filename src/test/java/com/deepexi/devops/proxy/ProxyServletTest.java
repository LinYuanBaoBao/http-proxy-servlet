package com.deepexi.devops.proxy;

import com.deepexi.devops.proxy.enums.HandlerType;
import com.deepexi.devops.proxy.handler.Handler;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ProxyServletTest {

    @Test
    public void addHandler() {
        ProxyServlet proxyServlet = new ProxyServlet() {
            public void initHandler() {}
        };
        proxyServlet.addHandler(createHandler(HandlerType.PRE_PROXY,Integer.MIN_VALUE));
        proxyServlet.addHandler(createHandler(HandlerType.PRE_PROXY,10));
        proxyServlet.addHandler(createHandler(HandlerType.PRE_PROXY,-1));
        proxyServlet.addHandler(createHandler(HandlerType.PRE_PROXY, 1));
        proxyServlet.addHandler(createHandler(HandlerType.PRE_PROXY, 0));
        proxyServlet.addHandler(createHandler(HandlerType.PRE_PROXY, Integer.MAX_VALUE));
        proxyServlet.addHandler(createHandler(HandlerType.PRE_PROXY, 2));
        Map<HandlerType, List<Handler>> handlerChain = proxyServlet.getHandlerChain();
        List<Handler> handlers = handlerChain.get(HandlerType.PRE_PROXY);
        System.out.println(String.format("处理器顺序:%s",handlers.stream().map(handler -> handler.getOrder()+"").collect(Collectors.joining("、"))));
//        assertThat(handlers.get(0).getOrder()).isEqualTo(Integer.MIN_VALUE);
//        assertThat(handlers.get(handlers.size()-1).getOrder()).isEqualTo(Integer.MAX_VALUE);
    }

    private Handler createHandler(HandlerType type, Integer order) {
        return new Handler() {
            public void doHandle(RequestContext requestContext) {

            }

            public HandlerType getType() {
                return type;
            }

            public Integer getOrder() {
                return order;
            }
        };
    }

}
