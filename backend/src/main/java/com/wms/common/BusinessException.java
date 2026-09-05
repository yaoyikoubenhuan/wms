package com.wms.common;

/**
 * 业务异常：由 GlobalExceptionHandler 统一转为 Result.fail
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
