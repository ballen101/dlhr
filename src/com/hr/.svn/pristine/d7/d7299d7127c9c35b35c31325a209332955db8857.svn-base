package com.hr.portals.co;

import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.dbpool.DBPools;
import com.corsair.server.base.CSContext;
import com.corsair.server.generic.Shworg;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CReport;
import com.corsair.server.util.CorUtil;
import com.hr.attd.ctr.HrkqUtil;
import com.hr.perm.entity.Hr_employee;
import com.hr.util.HRUtil;

@ACO(coname = "web.hr.Portalsm")
public class COHr_portalsm {
	@ACOAction(eventname = "getEmpInfo", Authentication = true, ispublic = true, notes = "获取人员信息")
	public String getimageid() throws Exception {
		String username = CSContext.getUserName();
		String sqlstr = "SELECT * FROM hr_employee t WHERE t.employee_code = '" + username + "'";
		Hr_employee emp = new Hr_employee();
		emp.findBySQL(sqlstr, false);
		if (emp.isEmpty()) {
			throw new Exception("当前登录用户无对应人事资料");
		}
		return emp.tojson();
	}

	@ACOAction(eventname = "getEmpInfoNotice", Authentication = true, ispublic = true, notes = "获取人员通知信息")
	public String getEmpInfoNotice() throws Exception {
		String username = CSContext.getUserName();
		String sqlstr = "SELECT * FROM hr_employee t WHERE t.employee_code = '" + username + "'";
		Hr_employee emp = new Hr_employee();
		emp.findBySQL(sqlstr, false);
		if (emp.isEmpty()) {
			throw new Exception("当前登录用户无对应人事资料");
		}
		Shworg deforg = CSContext.getUserDefaultOrg();

		boolean isorgmanger = HRUtil.hasPostions("6");// 岗位是部门负责人
		boolean isomorerom = isorgmanger || HRUtil.hasRoles("3,60,16");// 岗位是部门负责人
		// 或
		// 角色是人事经理人事专员
		int days = Integer.valueOf(HrkqUtil.getParmValue("TRYNOTICEBEFOREDAY"));// 试用考察提前通知X天

		// trystat 试用期人事状态 试用期中、试用过期、试用延期、己转正、试用不合格
		// wfresult 评审结果 1 同意转正 2 延长试用 3 试用不合格

		// 试用转正提醒数量
		sqlstr = "SELECT  IFNULL(COUNT(*),0)  ct FROM hr_entry_try t,hr_employee e " + "WHERE t.hwc_namezl <>'OO' "
				+ " AND((t.trystat = 2 OR t.trystat = 1) AND t.promotionday <= DATE_SUB(NOW(), INTERVAL -" + days + " DAY) "
				+ "  OR (t.trystat=3 AND t.delaypromotionday<= DATE_SUB(NOW(), INTERVAL -" + days + " DAY))) "
				+ " AND e.empstatid>1 AND e.empstatid<10 AND e.er_id=t.er_id";
		if (isorgmanger)
			sqlstr = sqlstr + CSContext.getIdpathwhere().replace("idpath", "t.idpath");
		else
			sqlstr = sqlstr + " and t.er_id=" + emp.er_id.getValue();
		int syzztxnum = Integer.valueOf(emp.pool.openSql2List(sqlstr).get(0).get("ct"));// 试用转正提醒数量

		// 试用转正延期数量
		sqlstr = "SELECT  IFNULL(COUNT(*),0)  ct FROM hr_entry_try t,hr_employee e  " + "WHERE t.trystat=3 and t.hwc_namezl <>'OO'"
				+ " AND e.empstatid>1 AND e.empstatid<10 AND e.er_id=t.er_id";
		if (isomorerom)
			sqlstr = sqlstr + CSContext.getIdpathwhere().replace("idpath", "t.idpath");
		else
			sqlstr = sqlstr + " and t.er_id=" + emp.er_id.getValue();
		int syzzyxnum = Integer.valueOf(emp.pool.openSql2List(sqlstr).get(0).get("ct"));// 试用转正延期数量

		// 考察转正提醒数量
		sqlstr = "SELECT  IFNULL(COUNT(*),0)  ct FROM hr_transfer_try t,hr_employee e " + "WHERE t.hwc_namezl <> 'OO' "
				+ "AND ((t.trystat = 2 OR t.trystat = 1) AND t.probationdate <= DATE_SUB(NOW(), INTERVAL -" + days + " DAY) "
				+ "    OR (t.trystat = 3  AND t.delaypromotionday <= DATE_SUB(NOW(), INTERVAL -" + days + " DAY))) "
				+ "  AND e.empstatid > 1 AND e.empstatid < 10 AND e.er_id = t.er_id ";

		if (isorgmanger)
			sqlstr = sqlstr + CSContext.getIdpathwhere().replace("idpath", "t.idpath");
		else
			sqlstr = sqlstr + " and t.er_id=" + emp.er_id.getValue();
		int kczztxnum = Integer.valueOf(emp.pool.openSql2List(sqlstr).get(0).get("ct"));// 考察转正提醒数量

		// 考察转正延期数量
		sqlstr = "SELECT  IFNULL(COUNT(*),0)  ct FROM hr_transfer_try t,hr_employee e " + "WHERE t.trystat=3  and t.hwc_namezl <>'OO'"
				+ " AND e.empstatid>1 AND e.empstatid<10 AND e.er_id=t.er_id";
		if (isomorerom)
			sqlstr = sqlstr + CSContext.getIdpathwhere().replace("idpath", "t.idpath");
		else
			sqlstr = sqlstr + " and t.er_id=" + emp.er_id.getValue();
		int kczzyqnum = Integer.valueOf(emp.pool.openSql2List(sqlstr).get(0).get("ct"));// 考察转正延期数量

		JSONObject rst = new JSONObject();

		if (isorgmanger)
			rst.put("isorgmanger", 1);
		else
			rst.put("isorgmanger", 2);
		if (isomorerom)
			rst.put("isomorerom", 1);
		else
			rst.put("isomorerom", 2);

		sqlstr = "SELECT IFNULL(COUNT(*),0) ct FROM hrrl_declare d1,hr_employee e  "
				+ "WHERE d1.rer_id=e.er_id AND e.empstatid<10 AND e.empstatid>0 AND d1.useable=1 AND d1.stat=9  " + "AND d1.rltype1=1 AND d1.rer_id="
				+ emp.er_id.getValue() + " AND NOT EXISTS( " + " SELECT 1 FROM hrrl_declare d2 WHERE d2.rltype1=1 AND d2.er_id=d1.rer_id AND d2.useable=1  "
				+ " AND d1.rltype2=d2.rltype2 AND rer_id=d1.er_id AND d2.dcldate>=d1.dcldate)";
		int glgxsbtx = Integer.valueOf(emp.pool.openSql2List(sqlstr).get(0).get("ct"));// 关联关系申报提醒
		rst.put("syzztxnum", syzztxnum);
		rst.put("syzzyxnum", syzzyxnum);
		rst.put("kczztxnum", kczztxnum);
		rst.put("kczzyqnum", kczzyqnum);
		rst.put("glgxsbtx", glgxsbtx);

		return rst.toString();
	}

	@ACOAction(eventname = "getGLGXNoticeNotice", Authentication = true, ispublic = true, notes = "获取关联关系通知列表")
	public String getGLGXNoticeNotice() throws Exception {
		String username = CSContext.getUserName();
		String sqlstr = "SELECT * FROM hr_employee t WHERE t.employee_code = '" + username + "'";
		Hr_employee emp = new Hr_employee();
		emp.findBySQL(sqlstr, false);
		if (emp.isEmpty()) {
			throw new Exception("当前登录用户无对应人事资料");
		}
		sqlstr = "SELECT d1.* from hrrl_declare d1,hr_employee e  " + "WHERE d1.rer_id=e.er_id AND e.empstatid<10 AND e.empstatid>0 AND d1.useable=1  "
				+ "AND d1.rltype1=1 AND d1.rer_id=" + emp.er_id.getValue() + " AND NOT EXISTS( "
				+ " SELECT 1 FROM hrrl_declare d2 WHERE d2.rltype1=1 AND d2.er_id=d1.rer_id AND d2.useable=1  "
				+ " AND d1.rltype2=d2.rltype2 AND rer_id=d1.er_id AND d2.dcldate>=d1.dcldate)";
		sqlstr = sqlstr + "  LIMIT 300";
		return DBPools.defaultPool().opensql2json(sqlstr);
	}

	@ACOAction(eventname = "getSyzztxlist", Authentication = true, ispublic = true, notes = "获取试用转正通知列表")
	public String getSyzztxlist() throws Exception {
		String username = CSContext.getUserName();
		int days = Integer.valueOf(HrkqUtil.getParmValue("TRYNOTICEBEFOREDAY"));// 试用考察提前通知X天
		// String sqlstr = "SELECT * FROM hr_entry_try " +
		// "WHERE hwc_namezl <>'OO' and (trystat =2 OR trystat=1 ) AND
		// promotionday<=DATE_SUB(NOW(),INTERVAL " + days + " DAY)";
		String sqlstr = "SELECT  t.* FROM hr_entry_try t,hr_employee e " + "WHERE t.hwc_namezl <>'OO' "
				+ " AND((t.trystat = 2 OR t.trystat = 1) AND t.promotionday <= DATE_SUB(NOW(), INTERVAL -" + days + " DAY) "
				+ "  OR (t.trystat=3 AND t.delaypromotionday<= DATE_SUB(NOW(), INTERVAL -" + days + " DAY))) "
				+ " AND e.empstatid>1 AND e.empstatid<10 AND e.er_id=t.er_id";
		sqlstr = sqlstr + CSContext.getIdpathwhere().replace("idpath", "t.idpath");
		sqlstr = sqlstr + " order by IF(e.employee_code='" + username + "',0,1) ";
		sqlstr = sqlstr + "  LIMIT 300";
		return DBPools.defaultPool().opensql2json(sqlstr);
	}

	@ACOAction(eventname = "getSyzzyqlist", Authentication = true, ispublic = true, notes = "获取试用转正延期列表")
	public String getSyzzyqlist() throws Exception {
		// String sqlstr = "SELECT * FROM hr_entry_try " +
		// "WHERE hwc_namezl <>'OO' and trystat=3 ";
		String username = CSContext.getUserName();
		String sqlstr = "SELECT  t.* FROM hr_entry_try t,hr_employee e  " + "WHERE t.trystat=3 and t.hwc_namezl <>'OO'"
				+ " AND e.empstatid>1 AND e.empstatid<10 AND e.er_id=t.er_id";
		sqlstr = sqlstr + CSContext.getIdpathwhere().replace("idpath", "t.idpath");
		sqlstr = sqlstr + " order by IF(e.employee_code='" + username + "',0,1) ";
		sqlstr = sqlstr + "  LIMIT 300";
		return DBPools.defaultPool().opensql2json(sqlstr);
	}

	@ACOAction(eventname = "getSyzztxinfo", Authentication = true, ispublic = true, notes = "获取个人试用转正提示信息")
	public String getSyzztxinfo() throws Exception {
		String username = CSContext.getUserName();
		String sqlstr = "SELECT * FROM hr_employee t WHERE t.employee_code = '" + username + "'";
		Hr_employee emp = new Hr_employee();
		emp.findBySQL(sqlstr, false);
		if (emp.isEmpty()) {
			throw new Exception("当前登录用户无对应人事资料");
		}
		sqlstr = "SELECT  * FROM hr_entry_try WHERE employee_code='" + username + "'";
		return DBPools.defaultPool().opensql2json(sqlstr);
	}

	@ACOAction(eventname = "getkczzlist", Authentication = true, ispublic = true, notes = "获取考察转正通知列表")
	public String getkczzlist() throws Exception {
		String username = CSContext.getUserName();
		int days = Integer.valueOf(HrkqUtil.getParmValue("TRYNOTICEBEFOREDAY"));// 试用考察提前通知X天
		// String sqlstr = "SELECT * FROM hr_transfer_try " +
		// "WHERE (trystat =2 OR trystat=1 ) and hwc_namezl <>'OO' AND
		// probationdate<=DATE_SUB(NOW(),INTERVAL " + days + " DAY)";
		String sqlstr = "SELECT  t.* FROM hr_transfer_try t,hr_employee e " + "WHERE t.hwc_namezl <> 'OO' "
				+ "AND ((t.trystat = 2 OR t.trystat = 1) AND t.probationdate <= DATE_SUB(NOW(), INTERVAL -" + days + " DAY) "
				+ "    OR (t.trystat = 3  AND t.delaypromotionday <= DATE_SUB(NOW(), INTERVAL -" + days + " DAY))) "
				+ "  AND e.empstatid > 1 AND e.empstatid < 10 AND e.er_id = t.er_id ";
		sqlstr = sqlstr + CSContext.getIdpathwhere().replace("idpath", "t.idpath");
		sqlstr = sqlstr + " order by IF(e.employee_code='" + username + "',0,1) ";
		sqlstr = sqlstr + "  LIMIT 300";
		return DBPools.defaultPool().opensql2json(sqlstr);
	}

	@ACOAction(eventname = "getkcyclist", Authentication = true, ispublic = true, notes = "获取考察延长通知列表")
	public String getkcyclist() throws Exception {
		String username = CSContext.getUserName();
		// String sqlstr = "SELECT * FROM hr_transfer_try " +
		// "WHERE trystat=3 and hwc_namezl <>'OO' ";
		String sqlstr = "SELECT  t.* FROM hr_transfer_try t,hr_employee e " + "WHERE t.trystat=3  and t.hwc_namezl <>'OO'"
				+ " AND e.empstatid>1 AND e.empstatid<10 AND e.er_id=t.er_id";
		sqlstr = sqlstr + CSContext.getIdpathwhere().replace("idpath", "t.idpath");
		sqlstr = sqlstr + " order by IF(e.employee_code='" + username + "',0,1) ";
		sqlstr = sqlstr + "  LIMIT 300";
		return DBPools.defaultPool().opensql2json(sqlstr);
	}

	@ACOAction(eventname = "getkcyxinfo", Authentication = true, ispublic = true, notes = "获取个人考察延期提示信息")
	public String getkcyxinfo() throws Exception {
		String username = CSContext.getUserName();
		String sqlstr = "SELECT  * FROM hr_transfer_try WHERE employee_code='" + username + "' ORDER BY createtime DESC";
		return DBPools.defaultPool().opensql2json(sqlstr);
	}

	////
	@ACOAction(eventname = "getMySubmitWFS", Authentication = true, ispublic = true, notes = "获取我提交的流程")
	public String getMySubmitWFS() throws Exception {
		String sqlstr = "select DISTINCT * from (SELECT   tp.wftempname, a.* ,p.procname,p.arivetime,p.procid " + " FROM  shwwf a, shwwftemp tp ,shwwfproc p "
				+ " WHERE a.stat = 1 " + "   AND a.wftemid = tp.wftempid " + "   AND a.wfid=p.wfid" + "   AND p.isbegin<>1" + "   AND p.isend<>1"
				+ "   AND p.stat=2" + "   AND a.submituserid =" + CSContext.getUserID() + " ORDER BY a.createtime DESC ) tb where 1=1 ";

		JSONArray rts = DBPools.defaultPool().opensql2json_O(sqlstr);
		for (int i = 0; i < rts.size() - 1; i++) {
			JSONObject jo = rts.getJSONObject(i);
			sqlstr = "SELECT u.displayname,u.userid FROM shwwfproc p,shwwfprocuser u" + " WHERE p.procid=u.procid AND  p.stat=2 AND u.stat IN(1,3) AND p.wfid="
					+ jo.getString("wfid");
			String names = "";
			String uids = "";
			List<HashMap<String, String>> us = DBPools.defaultPool().openSql2List(sqlstr);
			for (int j = 0; j < us.size(); j++) {
				HashMap<String, String> u = us.get(j);
				names = names + u.get("displayname") + ",";
				uids = uids + u.get("userid") + ",";
			}
			if (!names.isEmpty())
				names = names.substring(0, names.length() - 1);
			if (!uids.isEmpty())
				uids = uids.substring(0, uids.length() - 1);
			jo.put("curusers", names);
			jo.put("curuserids", uids);
		}
		return rts.toString();
	}

	@ACOAction(eventname = "getEmpAddrInfos", Authentication = true, ispublic = true, notes = "获取通讯录")
	public String getEmpAddrInfos() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid");
		String sqlstr = "SELECT e.orgid,e.orgcode,e.orgname,e.employee_name,e.sp_name,e.lv_num,a.* " + " FROM hr_employee e,hr_interfaceempaddr a"
				+ " WHERE e.employee_code=a.employee_code";
		if ((orgid != null) && (!orgid.isEmpty())) {
			sqlstr = sqlstr + " and e.orgid=" + orgid;
		}
		return new CReport(sqlstr, null).findReport();
	}

}
