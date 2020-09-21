package com.corsair.server.exception;

/**
 * 自定义错误
 * 
 * @author Administrator
 *
 */
public class CException extends RuntimeException {

	/**
	 * 
	 */
	private int errcode = 0;
	private static final long serialVersionUID = 1L;

	public CException(String message) {
		super(message);
	}

	public CException(int errcode, String message) {
		super(message);
		this.setErrcode(errcode);
	}

	public int getErrcode() {
		return errcode;
	}

	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}

}