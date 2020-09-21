package com.corsair.server.listener;

public class SWSpecClass {
	public enum SwSpecClassType {
		servetContextInit
	}

	private SwSpecClassType clsType;
	private Object clsObj;

	public SWSpecClass() {

	}

	public SWSpecClass(SwSpecClassType clsType, Object clsObj) {
		this.clsType = clsType;
		this.clsObj = clsObj;
	}

	public SwSpecClassType getClsType() {
		return clsType;
	}

	public void setClsType(SwSpecClassType clsType) {
		this.clsType = clsType;
	}

	public Object getClsObj() {
		return clsObj;
	}

	public void setClsObj(Object clsObj) {
		this.clsObj = clsObj;
	}
}
