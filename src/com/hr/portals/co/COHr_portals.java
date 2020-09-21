package com.hr.portals.co;

import java.util.Date;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.CJPABase.CJPAStat;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.JSONParm;
import com.corsair.dbpool.util.PraperedSql;
import com.corsair.dbpool.util.PraperedValue;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.genco.COShwUser;
import com.corsair.server.generic.Shw_attach;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.generic.Shworg;
import com.corsair.server.generic.Shwuser;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CExcelField;
import com.corsair.server.util.CExcelUtil;
import com.corsair.server.util.CReport;
import com.corsair.server.util.CSearchForm;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.DictionaryTemp;
import com.corsair.server.util.UpLoadFileEx;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.perm.entity.Hr_employee;

@ACO(coname = "web.hr.Portals")
public class COHr_portals extends JPAController {

	@ACOAction(eventname = "getportalrole", Authentication = false, notes = "获取角色")
	public String getportalrole() throws Exception {
		String useid = CSContext.getUserID();
		String sqlstr = "SELECT ifnull(count(*),0) ct FROM shwroleuser t WHERE t.roleid = 40 AND t.userid = " + useid;
		String sqlstr1 = "SELECT ifnull(count(*),0) ct FROM shwroleuser t WHERE t.roleid = 41 AND t.userid = " + useid;
		JSONObject rst = new JSONObject();
		if (Integer.valueOf(DBPools.defaultPool().openSql2List(sqlstr).get(0).get("ct")) > 0)
			rst.put("role40", 1);
		else
			rst.put("role40", 2);
		if (Integer.valueOf(DBPools.defaultPool().openSql2List(sqlstr1).get(0).get("ct")) > 0)
			rst.put("role41", 1);
		else
			rst.put("role41", 2);
		return rst.toString();
	}

	@ACOAction(eventname = "getimageid", Authentication = true, notes = "获取人员照片ID")
	public String getimageid() throws Exception {
		String username = CSContext.getUserName();
		String sqlstr = "SELECT * FROM hr_employee t WHERE t.usable=1 AND t.employee_code = '" + username + "'";
		Hr_employee emp = new Hr_employee();
		emp.findBySQL(sqlstr, false);
		if (emp.isEmpty()) {
			throw new Exception("当前登录用户无对应人事资料");
		}
		return emp.tojson();
	}

	@ACOAction(eventname = "getweekbirthday", Authentication = true, notes = "获取本周生日名单")
	public String getweekbirthday() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String tp = CorUtil.hashMap2Str(parms, "num", "需要参数num");
		String sqlstr = "SELECT * FROM hr_employee "
				+ " WHERE DATE_FORMAT(birthday, '%m-%d') >= DATE_FORMAT(CURDATE(), '%m-%d') "
				+ "   AND DATE_FORMAT(birthday, '%m-%d') <= DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL 7 DAY), '%m-%d')"
				+ "   AND empstatid NOT IN (12, 13) " + CSContext.getIdpathwhere();
		sqlstr = sqlstr + " ORDER BY DATE_FORMAT(birthday, '%m-%d') ";
		if (!tp.equals("Y")) {
			sqlstr = sqlstr + " limit 3 ";
		}
		// if (tp.equals("Y")) {//moyh 180130
		// sqlstr =
		// "SELECT * FROM hr_employee t WHERE DATE_FORMAT(t.birthday,'%m-%d') >= DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY),'%m-%d') " +
		// " AND t.birthday <= DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) - 6 DAY) " +
		// " AND t.empstatid NOT IN(12,13) ORDER BY t.birthday ";
		// } else {
		// sqlstr =
		// "SELECT * FROM hr_employee t WHERE DATE_FORMAT(t.birthday,'%m-%d') >= DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY),'%m-%d') " +
		// " AND t.birthday <= DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) - 6 DAY) " +
		// " AND t.empstatid NOT IN(12,13) ORDER BY t.birthday limit 3";
		// }
		String[] ignParms = null;
		String idpathw = null;
		return new CReport(sqlstr, null).findReport(ignParms, idpathw);
	}

	@ACOAction(eventname = "getmonthbirthday", Authentication = true, notes = "获取本月生日名单")
	public String getmonthbirthday() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String tp = CorUtil.hashMap2Str(parms, "num", "需要参数num");
		String sqlstr;
		if (tp.equals("Y")) {
			sqlstr = "SELECT * FROM hr_employee t WHERE MONTH(t.birthday) = MONTH(NOW()) AND t.empstatid NOT IN(12,13) " + CSContext.getIdpathwhere() + " ORDER BY t.birthday ";
		} else {
			sqlstr = "SELECT * FROM hr_employee t WHERE MONTH(t.birthday) = MONTH(NOW()) AND t.empstatid NOT IN(12,13) " + CSContext.getIdpathwhere() + " ORDER BY t.birthday limit 3";
		}
		String[] ignParms = null;
		String idpathw = null;
		return new CReport(sqlstr, null).findReport(ignParms, idpathw);
	}

	@ACOAction(eventname = "getfullmember", Authentication = true, notes = "获取待转正清单")
	public String getfullmember() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String tp = CorUtil.hashMap2Str(parms, "num", "需要参数num");
		String sqlstr;
		if (tp.equals("Y")) {
			sqlstr = "SELECT a.* FROM hr_entry_try a,hr_employee t WHERE a.er_id = t.er_id AND t.empstatid NOT IN(12,13) AND a.trystat NOT IN(4,5) AND DATE_FORMAT(a.promotionday,'%Y-%m') = DATE_FORMAT(NOW(),'%Y-%m') "
					+ CSContext.getIdpathwhere().replace("idpath", "t.idpath") + " ORDER BY promotionday";
		} else {
			sqlstr = "SELECT a.* FROM hr_entry_try a,hr_employee t WHERE a.er_id = t.er_id AND t.empstatid NOT IN(12,13) AND a.trystat NOT IN(4,5) AND DATE_FORMAT(a.promotionday,'%Y-%m') = DATE_FORMAT(NOW(),'%Y-%m') "
					+ CSContext.getIdpathwhere().replace("idpath", "t.idpath") + " ORDER BY promotionday limit 3";
		}
		String[] ignParms = null;
		String idpathw = null;
		return new CReport(sqlstr, null).findReport(ignParms, idpathw);
	}

	@ACOAction(eventname = "getdelaymember", Authentication = true, notes = "获取延期转正清单")
	public String getdelaymember() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String tp = CorUtil.hashMap2Str(parms, "num", "需要参数num");
		String sqlstr;
		if (tp.equals("Y")) {
			sqlstr = "SELECT a.* FROM hr_entry_try a,hr_employee t WHERE a.er_id = t.er_id AND t.empstatid NOT IN(12,13) AND a.trystat =3 AND DATE_FORMAT(a.delaypromotionday,'%Y-%m') = DATE_FORMAT(NOW(),'%Y-%m') "
					+ CSContext.getIdpathwhere().replace("idpath", "t.idpath") + " ORDER BY delaypromotionday";
		} else {
			sqlstr = "SELECT a.* FROM hr_entry_try a,hr_employee t WHERE a.er_id = t.er_id AND t.empstatid NOT IN(12,13) AND a.trystat =3 AND DATE_FORMAT(a.delaypromotionday,'%Y-%m') = DATE_FORMAT(NOW(),'%Y-%m') "
					+ CSContext.getIdpathwhere().replace("idpath", "t.idpath") + " ORDER BY delaypromotionday limit 3";
		}
		String[] ignParms = null;
		String idpathw = null;
		return new CReport(sqlstr, null).findReport(ignParms, idpathw);
	}

	@ACOAction(eventname = "getmonthcontract", Authentication = true, notes = "")
	public String getmonthcontract() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String tp = CorUtil.hashMap2Str(parms, "num", "需要参数num");
		String sqlstr;
		if (tp.equals("Y")) {
			sqlstr = "SELECT employee_code,employee_name,orgname,sp_name,hiredday,sign_date,end_date FROM hr_employee_contract " +
					" WHERE stat = 9 " +
					"  AND deadline_type = 1 " +
					"  AND contractstat = 1 " +
					"  AND contract_type = 1 " +
					"  AND DATE_FORMAT(end_date,'%Y-%m') = DATE_FORMAT(NOW(),'%Y-%m')" + CSContext.getIdpathwhere() +
					"  ORDER BY end_date";
		} else {
			sqlstr = "SELECT employee_code,employee_name,orgname,sp_name,hiredday,sign_date,end_date FROM hr_employee_contract " +
					" WHERE stat = 9 " +
					"  AND deadline_type = 1 " +
					"  AND contractstat = 1 " +
					"  AND contract_type = 1 " +
					"  AND DATE_FORMAT(end_date,'%Y-%m') = DATE_FORMAT(NOW(),'%Y-%m') " + CSContext.getIdpathwhere() +
					"  ORDER BY end_date" +
					"  limit 3";
		}
		String[] ignParms = null;
		String idpathw = null;
		return new CReport(sqlstr, null).findReport(ignParms, idpathw);
	}

	@ACOAction(eventname = "getlastmonthcontract", Authentication = true, notes = "")
	public String getlastmonthcontract() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String tp = CorUtil.hashMap2Str(parms, "num", "需要参数num");
		String sqlstr;
		if (tp.equals("Y")) {
			sqlstr = "SELECT employee_code,employee_name,orgname,sp_name,hiredday,sign_date,end_date FROM hr_employee_contract " +
					" WHERE stat = 9 " +
					"  AND deadline_type = 1 " +
					"  AND contractstat = 1 " +
					"  AND contract_type = 1 " +
					"  AND DATE_FORMAT(end_date,'%Y-%m') = DATE_FORMAT(DATE_ADD(CURDATE()-DAY(CURDATE())+1,INTERVAL 1 MONTH),'%Y-%m')" + CSContext.getIdpathwhere() +
					"  ORDER BY end_date";
		} else {
			sqlstr = "SELECT employee_code,employee_name,orgname,sp_name,hiredday,sign_date,end_date FROM hr_employee_contract " +
					" WHERE stat = 9 " +
					"  AND deadline_type = 1 " +
					"  AND contractstat = 1 " +
					"  AND contract_type = 1 " +
					"  AND DATE_FORMAT(end_date,'%Y-%m') = DATE_FORMAT(DATE_ADD(CURDATE()-DAY(CURDATE())+1,INTERVAL 1 MONTH),'%Y-%m')" + CSContext.getIdpathwhere() +
					"  ORDER BY end_date" +
					"  limit 3";
		}
		String[] ignParms = null;
		String idpathw = null;
		return new CReport(sqlstr, null).findReport(ignParms, idpathw);
	}

	@ACOAction(eventname = "getcontract", Authentication = true, notes = "")
	public String getcontract() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String tp = CorUtil.hashMap2Str(parms, "num", "需要参数num");
		String sqlstr;
		if (tp.equals("Y")) {
			sqlstr = "SELECT t.con_id,t.er_id,t.employee_code,t.employee_name,t.orgname,t.sp_name,t.hiredday,t.sign_date,t.end_date,e.idpath "
					+ "FROM hr_employee_contract t,hr_employee e " +
					" WHERE stat = 9 and t.er_id=e.er_id " +
					"  AND deadline_type = 1 " +
					"  AND contractstat = 1 " +
					"  AND contract_type = 1 " + 
					"  AND end_date<CURDATE() " +
					"  AND DATE_FORMAT(end_date,'%Y-%m') = DATE_FORMAT(NOW(),'%Y-%m') " +CSContext.getIdpathwhere().replace("idpath", "e.idpath") +
					"  ORDER BY end_date";
		} else {
			sqlstr = "SELECT t.con_id,t.er_id,t.employee_code,t.employee_name,t.orgname,t.sp_name,t.hiredday,t.sign_date,t.end_date,e.idpath FROM hr_employee_contract t,hr_employee e " +
					" WHERE stat = 9 and t.er_id=e.er_id " +
					"  AND deadline_type = 1 " +
					"  AND contractstat = 1 " +
					"  AND contract_type = 1 "+ 
					"  AND end_date<CURDATE() " +
					"  AND DATE_FORMAT(end_date,'%Y-%m') = DATE_FORMAT(NOW(),'%Y-%m') " + CSContext.getIdpathwhere().replace("idpath", "e.idpath") +
					"  ORDER BY end_date" +
					"  limit 3";
		}
		String[] ignParms = null;
		String idpathw = null;
		return new CReport(sqlstr, null).findReport(ignParms, idpathw);
	}

	@ACOAction(eventname = "getkcmember", Authentication = true, notes = "获取清单")
	public String getkcmember() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String tp = CorUtil.hashMap2Str(parms, "num", "需要参数num");
		String sqlstr;
		if (tp.equals("Y")) {
			sqlstr = "SELECT * FROM hr_transfer_try t WHERE t.trystat NOT IN (4,5) AND DATE_FORMAT(t.probationdate,'%Y-%m') = DATE_FORMAT(NOW(),'%Y-%m') " + CSContext.getIdpathwhere() + " ORDER BY t.probationdate ";
		} else {
			sqlstr = "SELECT * FROM hr_transfer_try t WHERE t.trystat NOT IN (4,5) AND DATE_FORMAT(t.probationdate,'%Y-%m') = DATE_FORMAT(NOW(),'%Y-%m') " + CSContext.getIdpathwhere() + " ORDER BY t.probationdate limit 3";
		}
		String[] ignParms = null;
		String idpathw = null;
		return new CReport(sqlstr, null).findReport(ignParms, idpathw);
	}

	@ACOAction(eventname = "getdelaykcmember", Authentication = true, notes = "获取延期考察清单")
	public String getdelaykcmember() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String tp = CorUtil.hashMap2Str(parms, "num", "需要参数num");
		String sqlstr;
		if (tp.equals("Y")) {
			sqlstr = "SELECT * FROM hr_transfer_try t WHERE t.trystat = 3 AND DATE_FORMAT(t.delaypromotionday,'%Y-%m') = DATE_FORMAT(NOW(),'%Y-%m') " + CSContext.getIdpathwhere() + " ORDER BY t.delaypromotionday ";
		} else {
			sqlstr = "SELECT * FROM hr_transfer_try t WHERE t.trystat = 3 AND DATE_FORMAT(t.delaypromotionday,'%Y-%m') = DATE_FORMAT(NOW(),'%Y-%m') " + CSContext.getIdpathwhere() + " ORDER BY t.delaypromotionday limit 3";
		}
		String[] ignParms = null;
		String idpathw = null;
		return new CReport(sqlstr, null).findReport(ignParms, idpathw);
	}

	@ACOAction(eventname = "getsfz", Authentication = true, notes = "获取身份证清单")
	public String getsfz() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String tp = CorUtil.hashMap2Str(parms, "num", "需要参数num");
		String sqlstr = null;
		if (tp.equals("Y")) {
			sqlstr = " SELECT *" +
					"  FROM (SELECT e.er_id," +
					"               e.employee_code," +
					"               e.employee_name," +
					"               e.orgname," +
					"               '身份证' ertname," +
					"               IF((expired_date IS NULL OR expired_date >= NOW())," +
					"                  '即将过期'," +
					"                  '已过期') cerstat," +
					"               e.expired_date," +
					"               e.orgid," +
					"               e.orgcode," +
					"               e.idpath" +
					"          FROM hr_employee e" +
					"         WHERE e.er_id <> 0 and expired_date <= date_add(NOW(), interval 1 MONTH) and e.empstatid<10 " + CSContext.getIdpathwhere().replace("idpath", "e.idpath") +
					"        UNION" +
					"        SELECT e.er_id," +
					"               e.employee_code," +
					"               e.employee_name," +
					"               e.orgname," +
					"               c.cert_name," +
					"               IF((c.expired_date IS NULL OR c.expired_date >= NOW())," +
					"                  '即将过期'," +
					"                  '已过期') cerstat," +
					"               c.expired_date," +
					"               e.orgid," +
					"               e.orgcode," +
					"               e.idpath" +
					"          FROM hr_employee e, hr_employee_cretl c" +
					"         WHERE e.er_id = c.er_id" +
					"           AND c.cert_name LIKE '%身份证%'" +
					"           AND c.expired_date <= date_add(NOW(), interval 1 MONTH)  and e.empstatid<10  " + CSContext.getIdpathwhere().replace("idpath", "e.idpath") +
					"           AND e.er_id <> 0) sfz" +
					" ORDER BY expired_date";
		} else {
			sqlstr = " SELECT *" +
					"  FROM (SELECT e.er_id," +
					"               e.employee_code," +
					"               e.employee_name," +
					"               e.orgname," +
					"               '身份证' ertname," +
					"               IF((expired_date IS NULL OR expired_date >= NOW())," +
					"                  '即将过期'," +
					"                  '已过期') cerstat," +
					"               e.expired_date," +
					"               e.orgid," +
					"               e.orgcode," +
					"               e.idpath" +
					"          FROM hr_employee e" +
					"         WHERE e.er_id <> 0 and expired_date <= date_add(NOW(), interval 1 MONTH)  and empstatid<10 " + CSContext.getIdpathwhere().replace("idpath", "e.idpath") +
					"        UNION" +
					"        SELECT e.er_id," +
					"               e.employee_code," +
					"               e.employee_name," +
					"               e.orgname," +
					"               c.cert_name," +
					"               IF((c.expired_date IS NULL OR c.expired_date >= NOW())," +
					"                  '即将过期'," +
					"                  '已过期') cerstat," +
					"               c.expired_date," +
					"               e.orgid," +
					"               e.orgcode," +
					"               e.idpath" +
					"          FROM hr_employee e, hr_employee_cretl c" +
					"         WHERE e.er_id = c.er_id" +
					"           AND c.cert_name LIKE '%身份证%'" +
					"           AND c.expired_date <= date_add(NOW(), interval 1 MONTH)  and empstatid<10  " + CSContext.getIdpathwhere().replace("idpath", "e.idpath") +
					"           AND e.er_id <> 0) sfz" +
					" ORDER BY expired_date LIMIT 3";
		}
		String[] ignParms = null;
		String idpathw = null;
		return new CReport(sqlstr, null).findReport(ignParms, idpathw);
	}

	@ACOAction(eventname = "getotherlieson", Authentication = true, notes = "获取其他证件清单")
	public String getotherlieson() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String tp = CorUtil.hashMap2Str(parms, "num", "需要参数num");
		String sqlstr;
		if (tp.equals("Y")) {
			sqlstr = " SELECT e.er_id," +
					"        e.employee_code," +
					"        e.employee_name," +
					"        e.orgname," +
					"        c.cert_name ertname," +
					"        IF((c.expired_date IS NULL OR c.expired_date >= NOW())," +
					"           '即将过期'," +
					"           '已过期') cerstat," +
					"        c.expired_date," +
					"        e.orgid," +
					"        e.orgcode," +
					"        e.idpath" +
					"   FROM hr_employee e, hr_employee_cretl c" +
					"  WHERE e.er_id = c.er_id" +
					"    AND c.cert_name NOT LIKE '%身份证%'" +
					"    AND e.er_id <> 0" + CSContext.getIdpathwhere().replace("idpath", "e.idpath") +
					"  ORDER BY c.expired_date";
		} else {
			sqlstr = " SELECT e.er_id," +
					"        e.employee_code," +
					"        e.employee_name," +
					"        e.orgname," +
					"        c.cert_name ertname," +
					"        IF((c.expired_date IS NULL OR c.expired_date >= NOW())," +
					"           '即将过期'," +
					"           '已过期') cerstat," +
					"        c.expired_date," +
					"        e.orgid," +
					"        e.orgcode," +
					"        e.idpath" +
					"   FROM hr_employee e, hr_employee_cretl c" +
					"  WHERE e.er_id = c.er_id" +
					"    AND c.cert_name NOT LIKE '%身份证%'" +
					"    AND e.er_id <> 0" + CSContext.getIdpathwhere().replace("idpath", "e.idpath") +
					"  ORDER BY c.expired_date LIMIT 3";
		}
		String[] ignParms = null;
		String idpathw = null;
		return new CReport(sqlstr, null).findReport(ignParms, idpathw);
	}

	@ACOAction(eventname = "getschedule", Authentication = true, notes = "获取排班信息")
	public String getschedule() throws Exception {
		HashMap<String, String> parms1 = CSContext.getParms();
		String tp = CorUtil.hashMap2Str(parms1, "num", "需要参数num");
		// 获取弹出窗参数
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String ps = parms.get("parms");
		List<JSONParm> jps = CJSON.getParms(ps);
		JSONParm jyearmonth = CjpaUtil.getParm(jps, "yearmonth");
		String ym = (jyearmonth == null) ? Systemdate.getStrDateByFmt(new Date(), "YYYY-MM") : jyearmonth.getParmvalue();
		String sqlstr;
		if (tp.equals("Y")) {
			sqlstr = "SELECT e.er_id," +
					"       e.employee_code," +
					"       e.employee_name," +
					"       e.orgid," +
					"       e.orgcode," +
					"       e.orgname," +
					"		e.sp_name," +
					"		e.hiredday," +
					"       '" + ym + "' yearmonth" +
					"  FROM hr_employee e" +
					" WHERE empstatid <= 10" +
					"   AND e.er_id <> 0" + CSContext.getIdpathwhere().replace("idpath", "e.idpath") +
					"   AND NOT EXISTS (SELECT 1" +
					"          FROM hrkq_workschmonthlist l" +
					"         WHERE l.yearmonth = '" + ym + "'" +
					"           AND l.er_id = e.er_id)";
		} else {
			sqlstr = "SELECT e.er_id," +
					"       e.employee_code," +
					"       e.employee_name," +
					"       e.orgid," +
					"       e.orgcode," +
					"       e.orgname," +
					"		e.sp_name," +
					"		e.hiredday," +
					"       DATE_FORMAT(CURDATE(), '%Y-%m') yearmonth" +
					"  FROM hr_employee e" +
					" WHERE empstatid <= 10" +
					"   AND e.er_id <> 0" + CSContext.getIdpathwhere().replace("idpath", "e.idpath") +
					"   AND NOT EXISTS (SELECT 1" +
					"          FROM hrkq_workschmonthlist l" +
					"         WHERE l.yearmonth = DATE_FORMAT(CURDATE(), '%Y-%m')" +
					"           AND l.er_id = e.er_id) LIMIT 3";
		}
		String[] ignParms = { "yearmonth" };// 忽略的查询条件
		String idpathw = null;
		return new CReport(sqlstr, null).findReport(ignParms, idpathw);
	}
}
