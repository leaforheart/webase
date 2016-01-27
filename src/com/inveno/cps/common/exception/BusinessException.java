package com.inveno.cps.common.exception;

/**
 * 业务异常类，可被调用者处理的异常
 * 2011-1-11
 * @author robin
 */
public class BusinessException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public BusinessException(String message){
		super(message);
	}
	
	public BusinessException(Throwable cause){
		super(cause);
	}
	
	public BusinessException(String message,Throwable cause){
		super(message,cause);
	}
}
