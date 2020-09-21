package com.corsair.server.util;

import java.util.HashMap;
import java.util.List;

public class DictionaryTempItem {
	private String valueField;
	private String captionField;
	private List<HashMap<String, String>> datas;

	public DictionaryTempItem(String valueField, String captionField, List<HashMap<String, String>> datas) {
		this.valueField = valueField;
		this.captionField = captionField;
		this.datas = datas;
	}

	public String getValueByCation(String caption) throws Exception {
		if (datas == null) {
			throw new Exception("Dictionary数据为空，不允许调用getValueByCation方法");
		}
		for (HashMap<String, String> dt : datas) {
			Object oc = dt.get(captionField);
			if (oc == null) {
				throw new Exception("字段【" + captionField + "】不存在");
			}
			Object ov = dt.get(valueField);
			if (ov == null) {
				throw new Exception("字段【" + valueField + "】不存在");
			}
			if (caption.equals(oc.toString())) {
				return ov.toString();
			}
		}
		return null;
	}

	public String getCaptionByValue(String value) throws Exception {
		if (datas == null) {
			throw new Exception("Dictionary数据为空，不允许调用getValueByCation方法");
		}
		for (HashMap<String, String> dt : datas) {
			Object oc = dt.get(captionField);
			if (oc == null) {
				throw new Exception("字段【" + captionField + "】不存在");
			}
			Object ov = dt.get(valueField);
			if (ov == null) {
				throw new Exception("字段【" + valueField + "】不存在");
			}
			if (value.equals(ov.toString())) {
				return oc.toString();
			}
		}
		return null;
	}

	public String getValueField() {
		return valueField;
	}

	public void setValueField(String valueField) {
		this.valueField = valueField;
	}

	public String getCaptionField() {
		return captionField;
	}

	public void setCaptionField(String captionField) {
		this.captionField = captionField;
	}

	public List<HashMap<String, String>> getDatas() {
		return datas;
	}

	public void setDatas(List<HashMap<String, String>> datas) {
		this.datas = datas;
	}
}
