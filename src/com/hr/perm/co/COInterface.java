package com.hr.perm.co;

import java.util.HashMap;
import java.util.List;

import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.JSONParm;
import com.corsair.server.base.CSContext;
import com.corsair.server.generic.Shworg;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CorUtil;
import com.hr.perm.entity.Hr_employee;

@ACO(coname = "web.hr.interface")
public class COInterface {
	private int maxct = 1000;

	@ACOAction(eventname = "ifOrg", Authentication = true, notes = "机构列表通用接口")
	public String ifOrg() throws Exception {
		String[] allfields = { "orgid", "orgname", "code", "superid", "extorgname", "manager", "orgtype", "idpath", "stat", "creator",
				"createtime", "updator", "updatetime", "attribute1" };
		HashMap<String, String> urlparms = CSContext.getParms();
		String parms = CorUtil.hashMap2Str(urlparms, "parms");
		String fields = urlparms.get("fields");
		int page = Integer.valueOf(CorUtil.hashMap2Str(urlparms, "page", "需要参数page"));
		int rows = Integer.valueOf(CorUtil.hashMap2Str(urlparms, "rows", "需要参数rows"));
		if (rows > maxct)
			throw new Exception("单页数据不超过【" + maxct + "】条");

		List<JSONParm> jps = CJSON.getParms(parms);
		Shworg org = new Shworg();
		String where = CjpaUtil.buildFindSqlByJsonParms(org, jps);
		where = where + CSContext.getIdpathwhere();
		String sqlstr = "select " + getfields(allfields, fields)
				+ " from "
				+ " shworg where 1=1 " + where;
		return org.pool.opensql2json(sqlstr, page, rows);
	}

	@ACOAction(eventname = "ifEmp", Authentication = true, notes = "人事档案通用接口")
	public String ifEmp() throws Exception {
		String[] allfields = { "er_id", "employee_code", "id_number", "employee_name", "birthday", "sex", "hiredday", "degree", "married", "nationality",
				"nativeplace", "address", "nation", "empstatid", "pldcp", "major", "registertype", "registeraddress", "health", "medicalhistory", "bloodtype",
				"urgencycontact", "telphone", "speciality", "orgid", "orgcode", "orgname", "lv_num", "hg_code", "hg_name", "ospcode", "sp_name", "iskey",
				"hwc_namezq", "hwc_namezz", "dorm_bed", "pay_way", "note", "creator", "createtime", "updator", "updatetime" };

		HashMap<String, String> urlparms = CSContext.getParms();
		String parms = CorUtil.hashMap2Str(urlparms, "parms");
		String fields = urlparms.get("fields");
		int page = Integer.valueOf(CorUtil.hashMap2Str(urlparms, "page", "需要参数page"));
		int rows = Integer.valueOf(CorUtil.hashMap2Str(urlparms, "rows", "需要参数rows"));
		if (rows > maxct)
			throw new Exception("单页数据不超过【" + maxct + "】条");
		List<JSONParm> jps = CJSON.getParms(parms);
		Hr_employee emp = new Hr_employee();
		String where = CjpaUtil.buildFindSqlByJsonParms(emp, jps);
		where = where + CSContext.getIdpathwhere();

		String sqlstr = "SELECT " + getfields(allfields, fields)
				+ " FROM hr_employee where 1=1" + where;
		return emp.pool.opensql2json(sqlstr, page, rows);
	}

	private String getfields(String[] allfields, String rfields) {
		if ((rfields == null) || (rfields.isEmpty()))
			return strs2str(allfields);
		String[] rfds = rfields.split(",");
		String rst = "";
		if (rfds.length == 0)
			return strs2str(allfields);
		for (String rfd : rfds) {
			if (isStrInStrs(allfields, rfd))
				rst = rst + rfd + ",";
		}
		if (!rst.isEmpty())
			rst = rst.substring(0, rst.length() - 1);
		else
			return strs2str(allfields);
		return rst;
	}

	private boolean isStrInStrs(String[] fds, String fd) {
		if ((fd == null) || (fd.isEmpty()))
			return false;
		for (String afd : fds) {
			if (fd.equalsIgnoreCase(afd))
				return true;
		}
		return false;
	}

	private String strs2str(String[] fds) {
		String rst = "";
		for (String fd : fds) {
			rst = rst + fd + ",";
		}
		if (!rst.isEmpty())
			rst = rst.substring(0, rst.length() - 1);
		return rst;
	}
}
