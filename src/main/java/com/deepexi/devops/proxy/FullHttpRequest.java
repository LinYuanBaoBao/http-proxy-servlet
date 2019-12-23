package com.deepexi.devops.proxy;

import lombok.Data;
import lombok.Getter;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

/**
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-19
 */
@Data
public class FullHttpRequest extends HttpEntityEnclosingRequestBase {
    private String method;
}
