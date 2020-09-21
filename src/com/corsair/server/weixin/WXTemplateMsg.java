package com.corsair.server.weixin;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

public class WXTemplateMsg {
	private String touser;
	private String template_id;
	private String url;
	private String topcolor;
	private List<MsgParm> datas = new ArrayList<MsgParm>();

	public class MsgParm {
		private String parmname;
		private String value;
		private String color;

		public MsgParm(String parmname, String value, String color) {
			this.parmname = parmname;
			this.value = value;
			this.color = color;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getParmname() {
			return parmname;
		}

		public void setParmname(String parmname) {
			this.parmname = parmname;
		}

		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}
	}

	public WXTemplateMsg(String touser, String template_id, String url, String topcolor) {
		this.touser = touser;
		this.template_id = template_id;
		this.url = url;
		this.topcolor = topcolor;
	}

	public String getTouser() {
		return touser;
	}

	public String getTemplate_id() {
		return template_id;
	}

	public String getUrl() {
		return url;
	}

	public String getTopcolor() {
		return topcolor;
	}

	public void setTouser(String touser) {
		this.touser = touser;
	}

	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setTopcolor(String topcolor) {
		this.topcolor = topcolor;
	}

	public List<MsgParm> getDatas() {
		return datas;
	}

	public String toWXJson() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JsonFactory jf = new JsonFactory();
		JsonGenerator jg = jf.createJsonGenerator(baos);
		jg.writeStartObject();
		jg.writeStringField("touser", getTouser());
		jg.writeStringField("template_id", getTemplate_id());
		jg.writeStringField("url", getUrl());
		jg.writeStringField("topcolor", getTopcolor());
		jg.writeFieldName("data");
		jg.writeStartObject();
		for (MsgParm parm : getDatas()) {
			jg.writeFieldName(parm.getParmname());
			jg.writeStartObject();
			jg.writeStringField("value", parm.getValue());
			jg.writeStringField("color", parm.getColor());
			jg.writeEndObject();
		}
		jg.writeEndObject();
		jg.writeEndObject();
		jg.close();
		return baos.toString("utf-8");
	}
}
