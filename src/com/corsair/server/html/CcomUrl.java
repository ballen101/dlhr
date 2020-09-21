package com.corsair.server.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.Logsw;
import com.corsair.server.util.HttpTookit;

public class CcomUrl {
	private String index;
	private String type;
	private boolean multiple;
	private String url;
	private String valueField;
	private String textField;

	private String jsondata;
	private HttpTookit ht;

	public CcomUrl(HttpTookit ht, String index, String type, boolean multiple, String url, String valueField, String textField) throws Exception {
		this.ht = ht;
		this.index = index;
		this.type = type;
		this.multiple = multiple;
		this.url = url;
		this.valueField = valueField;
		this.textField = textField;
	}

	public String fetchJSFunction() throws Exception {
		Logsw.debug("url:" + url);
		jsondata = ht.doGet(url, null);
		String jsstr = null;
		if (type.equalsIgnoreCase("combobox")) {
			List<HashMap<String, String>> rows = CJSON.parArrJson(jsondata);
			List<HashMap<String, String>> nrows = new ArrayList<HashMap<String, String>>();
			for (HashMap<String, String> row : rows) {
				HashMap<String, String> nrow = new HashMap<String, String>();
				nrow.put(valueField, row.get(valueField));
				nrow.put(textField, row.get(textField));
				nrows.add(nrow);
			}
			jsstr = "jsondata:" + CJSON.List2JSON(nrows) + ",";
		}
		if (type.equalsIgnoreCase("combotree")) {
			jsstr = "jsondata:" + jsondata + ",";
			// System.out.println("jsondata:" + jsondata);
		}
		String vname = "comUrl_" + index;
		String cd = "var " + vname + " = {"
				+ "index: '" + index + "',"
				+ "type: '" + type + "',"
				+ "multiple: " + multiple + ","
				+ "valueField: '" + valueField + "',"
				+ "textField: '" + textField + "',"
				+ jsstr
				+ " formator: function (value, row) {"
				+ "     var jsondata = " + vname + ".jsondata;"
				+ "     if (value == 'get_com_data') {"
				+ "         return jsondata;"
				+ "     }"
				+ "     if (value == 'get_com_url') {"
				+ "         return " + vname + ";"
				+ "     }"
				+ "     if (" + vname + ".type == 'combobox') {"

				+ "      if (" + vname + ".multiple) {"
				+ "        if((!value)||(value.length==0)) return value;"
				+ "        var vs = value.split(',');"
				+ "        var rst = '';"
				+ "        for (var j = 0; j < vs.length; j++) {"
				+ "            var v = vs[j];"
				+ "            if ((v) && (v.length > 0)) {"
				+ "                for (var i = 0; i < jsondata.length; i++) {"
				+ "                    if (v == jsondata[i][" + vname + ".valueField]) {"
				+ "                        rst = rst + jsondata[i][" + vname + ".textField] + ',';"
				+ "                        break;"
				+ "                    }"
				+ "                }"
				+ "            }"
				+ "        }"
				+ "        if (rst.length > 0)"
				+ "            rst= rst.substring(0, rst.length - 1);"
				+ "        return rst;"
				+ "     } else {"
				+ "        for (var i = 0; i < jsondata.length; i++) {"
				+ "            if (value == jsondata[i][" + vname + ".valueField])"
				+ "               return jsondata[i][" + vname + ".textField];"
				+ "         }"
				+ "     }"
				+ "     }"
				+ "     if (" + vname + ".type == 'combotree') {"
				+ "         var txt = $getTreeTextById(jsondata, value);"
				+ "         if (txt == undefined) txt = value;"
				+ "         return txt;"
				+ "     }"
				+ "     return value;"
				+ " }";

		cd = cd + " };\n";

		if (type.equalsIgnoreCase("combobox"))
			cd = cd + vname + ".editor= {type: 'combobox', options: {valueField:" + vname + ".valueField, textField:" + vname + ".textField, data: " + vname
					+ ".jsondata}};";
		if (type.equalsIgnoreCase("combotree"))
			cd = cd + vname + ".editor= {type: 'combotree', options: {data: $C.tree.setTree1OpendOtherClosed(" + vname + ".jsondata)}};";
		return cd;
	}

	public String getIndex() {
		return index;
	}

	public String getType() {
		return type;
	}

	public String getUrl() {
		return url;
	}

	public String getValueField() {
		return valueField;
	}

	public String getTextField() {
		return textField;
	}

	public String getJsondata() {
		return jsondata;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setValueField(String valueField) {
		this.valueField = valueField;
	}

	public void setTextField(String textField) {
		this.textField = textField;
	}

	public void setJsondata(String jsondata) {
		this.jsondata = jsondata;
	}

}
