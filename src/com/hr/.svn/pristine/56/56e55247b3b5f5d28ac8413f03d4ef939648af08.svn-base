package com.hr.base.co;

import java.util.HashMap;

import net.sf.json.JSONObject;

import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.PraperedSql;
import com.corsair.dbpool.util.PraperedValue;
import com.corsair.server.base.CSContext;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CorUtil;
import com.hr.attd.entity.Hrkq_parms;
import com.hr.base.entity.Hr_month_orgposition;
import com.hr.base.entity.Hr_month_quotaoc;
import com.hr.perm.ctr.CtrHrPerm;
import com.hr.perm.entity.Hr_employee;

@ACO(coname = "web.hr.common")
public class COHrCommon {
    @ACOAction(eventname = "getHRParm", Authentication = true, notes = "获取HR参数")
    public String getHRParm() throws Exception {
	HashMap<String, String> parms = CSContext.getParms();
	String parmcode = CorUtil.hashMap2Str(parms, "parmcode");
	PraperedSql psql = new PraperedSql();
	String sqlstr = "select * from hr_systemparms where usable=1 ";
	if (parmcode != null) {
	    sqlstr = sqlstr + " and '" + parmcode + "'";
	    psql.getParms().add(new PraperedValue(java.sql.Types.VARCHAR, parmcode));
	}
	psql.setSqlstr(sqlstr);
	return DBPools.defaultPool().opensql2json(psql, false);
    }

    @ACOAction(eventname = "gethrkq_parms", Authentication = true, notes = "获取HR参数列表")
    public String gethrkq_parms() throws Exception {
	String sqlstr = "select * from hrkq_parms";
	return DBPools.defaultPool().opensql2json(sqlstr);
    }

    @ACOAction(eventname = "savehrkq_parms", Authentication = true, notes = "保存HR参数列表")
    public String savehrkq_parms() throws Exception {
	HashMap<String, String> parms = CSContext.get_pjdataparms();
	String parmid = CorUtil.hashMap2Str(parms, "parmid", "需要参数parmid");//
	String parmvalue = CorUtil.hashMap2Str(parms, "parmvalue", "需要参数parmvalue");//
	Hrkq_parms hp = new Hrkq_parms();
	hp.findByID(parmid);
	if (hp.isEmpty())
	    throw new Exception("ID为【" + parmid + "】的参数不存在");
	if (hp.canedit.getAsIntDefault(0) != 1)
	    throw new Exception("参数不允许编辑");
	hp.parmvalue.setValue(parmvalue);
	hp.save();
	return hp.toString();
    }

    @ACOAction(eventname = "getRealtionPem", Authentication = true, notes = "获取某员工有效关联关系")
    public String getRealtionPem() throws Exception {
	HashMap<String, String> parms = CSContext.getParms();
	String employee_code = CorUtil.hashMap2Str(parms, "employee_code", "参数【employee_code】不能为空");
	String ldtype = CorUtil.hashMap2Str(parms, "ldtype");
	Hr_employee emp = new Hr_employee();
	String sqlstr = "select * from hr_employee where employee_code='" + employee_code + "'";
	emp.findBySQL(sqlstr, false);
	if (emp.isEmpty())
	    throw new Exception("工号为【" + employee_code + "】的人事资料不存在");
	String er_id = emp.er_id.getValue();
	// sqlstr = "SELECT * FROM hrrl_declare WHERE useable=1 and stat=9 AND
	// (er_id=" + er_id + " OR rer_id=" + er_id + ")";
	sqlstr = "SELECT * FROM hrrl_declare WHERE useable=1 and stat=9 AND er_id=" + er_id;
	if ((ldtype != null) && (!ldtype.isEmpty())) {
	    sqlstr = sqlstr + " and ldtype=" + ldtype;
	}
	return emp.pool.opensql2json_O(sqlstr).toString();
    }

    @ACOAction(eventname = "putmonthosp", Authentication = true, ispublic = true, notes = "机构职位月结")
    public String putmonthosp() throws Exception {
	HashMap<String, String> parms = CSContext.get_pjdataparms();
	String ym = CorUtil.hashMap2Str(parms, "ym", "需要参数ym");// yyyy-mm 年月
	JSONObject rst = new JSONObject();
	rst.put("result", CtrHrPerm.putmonthosp(ym));
	return rst.toString();
    }

    @ACOAction(eventname = "putmonthqtc", Authentication = true, ispublic = true, notes = "机构职位月结")
    public String putmonthqtc() throws Exception {
	HashMap<String, String> parms = CSContext.get_pjdataparms();
	String ym = CorUtil.hashMap2Str(parms, "ym", "需要参数ym");// yyyy-mm 年月
	JSONObject rst = new JSONObject();
	rst.put("result", CtrHrPerm.putmonthqtc(ym));
	return rst.toString();
    }

    // 编制月结
    // SELECT COUNT(*) FROM hr_orgposition op
    // WHERE EXISTS(
    // SELECT 1 FROM shworg o
    // WHERE op.`orgid`=o.orgid AND o.`usable`=1
    // )
    //
    // SELECT COUNT(*) FROM hr_quotaoc oc
    // WHERE EXISTS(
    // SELECT 1 FROM shworg o
    // WHERE oc.`orgid`=o.orgid AND o.`usable`=1
    // )

}
