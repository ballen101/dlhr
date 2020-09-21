package com.corsair.server.exception;

/**
 * 流程自定义错误
 * 
 * @author Administrator
 *
 */
public class CWFException extends Exception {
	public static final int ERR_NEED_SELECT_USER = 1001;// 需要从列表中选择一个或多个审批人
	public static final int ERR_NEED_SELECT_ALLUSER = 1002;// 需要全局选择审批人
	private static final long serialVersionUID = 1L;
	private int errcode;
	private String infocode;
	private Object userobj;// 用户对象

	public CWFException(int errcode, String infocode, String msg) {
		super(msg);
		this.errcode = errcode;
		this.infocode = infocode;
	}

	public int getErrcode() {
		return errcode;
	}

	public String getInfocode() {
		return infocode;
	}

	public Object getUserobj() {
		return userobj;
	}

	public void setUserobj(Object userobj) {
		this.userobj = userobj;
	}

}
