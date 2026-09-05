package com.wms.common;

/**
 * 未登录/登录失效异常：返回 401 业务码
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
