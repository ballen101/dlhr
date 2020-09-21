package com.corsair.dbpool.util;

import java.util.List;

import net.sf.json.JSONObject;

/** JSON参数对象
 * @author Administrator
 *
 */
public class JSONParm {

	private String parmname;
	private String reloper;
	private String parmvalue;

	public static JSONParm getParmByName(List<JSONParm> parms, String parmname) {
		for (JSONParm parm : parms) {
			if (parmname.equals(parm.parmname)) {
				return parm;
			}
		}
		return null;
	}

	public static boolean hasParmName(List<JSONParm> parms, String parmname) {
		JSONParm parm = getParmByName(parms, parmname);
		return (parm != null);
	}

	public JSONParm(String parmname, String reloper, String parmvalue) {
		this.parmname = parmname;
		this.reloper = reloper;
		this.parmvalue = parmvalue;
	}

	public String getParmname() {
		return parmname;
	}

	public String getReloper() {
		return reloper;
	}

	public String getParmvalue() {
		return parmvalue;
	}

	public void setParmname(String parmname) {
		this.parmname = parmname;
	}

	public void setReloper(String reloper) {
		this.reloper = reloper;
	}

	public void setParmvalue(String parmvalue) {
		this.parmvalue = parmvalue;
	}

	public String toJSON() {
		JSONObject jo = new JSONObject();
		jo.put("parmname", parmname);
		jo.put("reloper", reloper);
		jo.put("parmvalue", parmvalue);
		return jo.toString();
	}
}