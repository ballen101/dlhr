package com.corsair.server.eai;

public class EAIMapField {
	private String s_field;
	private String d_field;
	private int s_fieldtype = -999;
	private int d_fieldtype = -999;

	public String getS_field() {
		return s_field;
	}

	public String getD_field() {
		return d_field;
	}

	public int getS_fieldtype() {
		return s_fieldtype;
	}

	public int getD_fieldtype() {
		return d_fieldtype;
	}

	public void setS_field(String s_field) {
		this.s_field = s_field;
	}

	public void setD_field(String d_field) {
		this.d_field = d_field;
	}

	public void setS_fieldtype(int s_fieldtype) {
		this.s_fieldtype = s_fieldtype;
	}

	public void setD_fieldtype(int d_fieldtype) {
		this.d_fieldtype = d_fieldtype;
	}

}
