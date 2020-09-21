package com.corsair.server.eai;

public class CEAICondt {
	private String field;
	private String oper;
	private String value;
	private int fieldtype;

	public String getField() {
		return field;
	}

	public String getOper() {
		return oper;
	}

	public String getValue() {
		return value;
	}

	public void setField(String field) {
		this.field = field;
	}

	public void setOper(String oper) {
		this.oper = oper;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getFieldtype() {
		return fieldtype;
	}

	public void setFieldtype(int fieldtype) {
		this.fieldtype = fieldtype;
	}

}
