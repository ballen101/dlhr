package com.corsair.dbpool.util;

/**
 * 参数化SQL参数值对象
 * 
 * @author Administrator
 *
 */
public class PraperedValue {
	private Object value = null;
	private int fieldtype;

	public PraperedValue() {

	}

	public PraperedValue(int fieldtype, Object value) {
		this.value = value;
		this.fieldtype = fieldtype;
	}

	public Object getValue() {
		return value;
	}

	public int getFieldtype() {
		return fieldtype;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setFieldtype(int fieldtype) {
		this.fieldtype = fieldtype;
	}

}
