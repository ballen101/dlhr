package com.corsair.server.util;

import net.sf.json.JSONObject;

import org.apache.poi.ss.usermodel.Workbook;

public class ExcelParmsRst {
	private JSONObject jo = null;
	private Workbook workbook = null;

	public ExcelParmsRst(JSONObject jo, Workbook workbook) {
		this.jo = jo;
		this.workbook = workbook;
	}

	public JSONObject getJo() {
		return jo;
	}

	public void setJo(JSONObject jo) {
		this.jo = jo;
	}

	public Workbook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}
}
