package com.corsair.cjpa;

/**
 * 关联字段
 * 
 * @author Administrator
 *
 */
public class LinkField {
	private String mfield;
	private String lfield;

	public LinkField(String mfield, String lfield) {
		this.mfield = mfield;
		this.lfield = lfield;
	}

	public String getMfield() {
		return mfield;
	}

	public void setMfield(String mfield) {
		this.mfield = mfield;
	}

	public String getLfield() {
		return lfield;
	}

	public void setLfield(String lfield) {
		this.lfield = lfield;
	}
}
