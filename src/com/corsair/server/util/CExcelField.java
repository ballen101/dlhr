package com.corsair.server.util;

/*
 * excel导入字段定义
 * */
public class CExcelField {
	private String caption;
	private String fdname;
	private boolean notnull = false;
	private int col = -1;

	public CExcelField(String caption, String fdname) {
		this.caption = caption;
		this.fdname = fdname;
	}

	public CExcelField(String caption, String fdname, boolean notnull) {
		this.caption = caption;
		this.fdname = fdname;
		this.notnull = notnull;
	}

	public String getCaption() {
		return caption;
	}

	public String getFdname() {
		return fdname;
	}

	public boolean isNotnull() {
		return notnull;
	}

	public int getCol() {
		return col;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public void setFdname(String fdname) {
		this.fdname = fdname;
	}

	public void setNotnull(boolean notnull) {
		this.notnull = notnull;
	}

	public void setCol(int col) {
		this.col = col;
	}

}
