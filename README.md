# Http-Proxy-Servlet

Http-Proxy-Servlet 能提供流量转发功能，并且支持 HTTPS 双向认证。它可以很方便的整合到你的 SpringBoot 程序中，甚至你可以在此基础上搭建一个"网关"程序。

项目地址：https://github.com/LinYuanBaoBao/http-proxy-servlet.git

## 快速上手

引入依赖：
```java
<dependency>
    <groupId>com.github.LinYuanBaoBao</groupId>
    <artifactId>http-proxy-servlet</artifactId>
    <version>1.0.1-RELEASE</version>
</dependency>
```

### 在 SpringBoot 中使用

> 本示例你可以从 `http-proxy-servlet-example-http` 中获取

在 SpringBoot 中使用 Http-Proxy-Servlet 非常的简单，通过 SpringMVC 提供的 `ServletWrappingController` 将 Servlet 包装成一个 Controller，并通过 `SimpleUrlHandlerMapping` 将 Controller 与 `/proxy` 接口进行绑定。

```java
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
            addHandler(new HttpClientProxyInitHandler());
            addHandler(new ProxyHandler());
            addHandler(new ResponseHandler());
        }
    }

}
```

因为 **ProxyServlet** 是个抽象类，需要实现 `initHandler()` 方法初始化处理器，上面代码中添加了三个 **Handler** 用于支持基本的 HTTP 流量转发功能：

- **HttpClientProxyInitHandler**：前置处理器，用于初始化 HttpClientProxy 代理对象
- **ProxyHandler**：代理处理器，用于执行代理请求
- **ResponseHandler**：后置处理器，用于将代理请求结果响应回客户端

你也可以直接使用 `DefaultProxyServlet`，其同样初始化了这三个处理器。

#### 添加 TargetHandler 处理器

单单如此还不足以实现流量转发功能，你还需要自定义一个前置处理器，用于告诉 `ProxyServlet` 你准备将把请求代理至何处。

这需要你自定义一个 **TargetHandler** 并在业务逻辑中获取待目标的主机地址，如下面代码：

```java
@Component
public class TargetHandler implements Handler {

    @Override
    public void doHandle(RequestContext requestContext) {
        // 将请求代理至 localhost:8081 
        TargetHost targetHost = new TargetHost("localhost",8081);
        requestContext.setTargetHost(targetHost);
    }

    @Override
    public HandlerType getType() {
        return HandlerType.PRE_PROXY;
    }

    @Override
    public Integer getOrder() {
        return Integer.MIN_VALUE;
    }

}
```

Handler 类型有三种：

- **PRE_PROXY**：前置处理器，它将会在请求代理前运行
- **PROXY**：请求代理处理器
- **POST_PROXY**：前置处理器，它将会在请求代理后运行

`getOrder()` 方法返回值将会决定 Handler 在处理链中的执行顺序，**其值越小越先执行，值相同则会根据添加的先后顺序。**

- 对于需要最先执行的 Handler 通常会使用 Integer.MIN_VALUE 值，如用于初始化 Proxy 代理对象的处理器。
- 对于需要最后执行的 Handler 通常会使用 Integer.MAX_VALUE 值，如用于将代理请求结果响应会客户端的处理器。

修改 `initHandler()` 方法，将 TargetHandler 添加至处理器链，如下：

```java
public static class SpringProxyServlet extends ProxyServlet {
    @Override
    public void initHandler() {
        addHandler(new TargetHandler());
        addHandler(new HttpClientProxyInitHandler());
        addHandler(new ProxyHandler());
        addHandler(new ResponseHandler());
    }
}
```

#### 测试

此时运行程序，假设访问代理服务 http://localhost:8080/proxy/v1/applications 的请求将会被代理至 http://localhost:8081/v1/applications 

### 支持 HTTPS 双向认证

> 本示例你可以从 `http-proxy-servlet-example-https` 中获取

若代理的目标服务支持 Https，并且目标服务还需验证请求客户端的身份，那么你可以参考下面示例实现 Https 双向认证。

首先对先前的 **TargetHandler** 做一些修改：

```java
public class TargetHandler implements Handler {

    @Override
    public void doHandle(RequestContext requestContext) {
        TargetHost targetHost = new TargetHost("localhost", 8082);
        requestContext.setTargetHost(targetHost);
        requestContext.setScheme(Scheme.HTTPS);
    }

    @Override
    public HandlerType getType() {
        return HandlerType.PRE_PROXY;
    }

    @Override
    public Integer getOrder() {
        return Integer.MIN_VALUE;
    }

}
```

我们需要将请求 **Scheme** 类型设置为 HTTPS。

其次确保 `getOrder()` 返回值为 Integer.MIN_VALUE，HttpsClientProxyInitHandler 会根据 **Scheme** 决定是否创建 HTTPS 连接，因此需要 **TargetHandler** 先与 **HttpsClientProxyInitHandler** 执行。

修改 `initHandler()` 方法，将 **HttpClientProxyInitHandler** 替换为 **HttpsClientProxyInitHandler**。

```java
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
```

**HttpsClientProxyInitHandler** 构造函数需要一个 **HttpsConnectionSocketFactoryRegister** 接口实现，用于创建 HTTPS-Socket 连接工厂，这里使用 **DefaultHttpsConnectionSocketFactoryRegister**，需要注意的是它暂未对 *hostname* 进行验证，你可以自己实现验证逻辑，并调用 `setHostnameVerifier()` 方法配置它。

而 **DefaultHttpsConnectionSocketFactoryRegister** 构造函数需要一个 **SSLContextInitializer** 接口实现，用于初始化 SSLContext 上下文对象，这里使用的 **DefaultSSLContextInitializer** 它会跳过对服务端的证书校验，你可以继承该类重写 `loadTrustManager()` 方法实现校验逻辑。

此外 **DefaultSSLContextInitializer** 并不清楚你客户端的证书保存于何处，因为它可能跟你的业务逻辑有关，因此你需要实现 **KeyStoreLoader** 接口获取 **KeyStore**。

上面代码中，我本地测试用的客户端证书位于项目资源目录下，并名为 client.p12，其密码为 123456，证书类型为 PKCS12。

#### 测试

此时如果代理的目标服务端已信任 client.p12 证书，则请求将会成功。

你可以参考 [此文章](https://blog.csdn.net/BlackButton_CC/article/details/99956259) 搭建一个双向认证服务端尝试一下。

### 自定义 Proxy 代理对象

真正转发流量的并非是 **Handler** 而是 **Proxy**，由 **Proxy** 去完成实际代理并将结果响应回调用方。目前仅提供了基于 HttpClient 实现的 **HttpClientProxy**。

如果有需要自定义你可以继承自 **AbstractProxy** 并实现以下三个方法：

- **proxy()**：执行代理请求
- **response()**：将代理请求结果响应回调用方
- **destroy()**：销毁代理对象资源

也可以继承 **HttpClientProxy** 并在原有的基础上做修改，如修改响应结果，可以重写 `setResponseEntity()` 方法。

## 扩展参考

### 通过跳板机将请求代理至内网主机

如果你需要将请求代理至内网主机，你可以利用跳板机的端口转发功能，这里推荐使用 **Jcraft**，你的代码可能会是这个样子：

```java
private Integer buildTunnel(Session jumpSession, String targetIp, Integer targetPort) throws JSchException {
  return jumpSession.setPortForwardingL(0, targetIp, targetPort);
}
```

## 作者信息

聪明的杰瑞 - [掘金博客](https://juejin.im/user/5bd1c4886fb9a05cd777874a)、邮箱：765371578@qq.com
