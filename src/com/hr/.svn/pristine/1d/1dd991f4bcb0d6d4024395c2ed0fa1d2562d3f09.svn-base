package com.hr.util;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.generic.Shworg;
import com.hr.base.entity.Hr_orgposition;

public class HTQuotaUtil {
	// 脱产类
	public static boolean checkOrgPositionQuota(CDBConnection con, String ospid, int needq) throws Exception {
		Hr_orgposition op = new Hr_orgposition();
		op.findByID(con, ospid);
		if (op.isEmpty())
			throw new Exception("ID为【" + ospid + "】的机构职位不存在");
		HRQuotaUsedInfo qui = getQuotaInfo(con, op);
		boolean rst = (qui.getSumq() < (qui.getUsedq() + needq));
		return rst;
	}

	public static HRQuotaUsedInfo getQuotaInfo(CDBConnection con, Hr_orgposition op) throws Exception {
		String sqlstr = "SELECT COUNT(*) ct"
				+ " FROM"
				+ " (SELECT DISTINCT e.*"
				+ " FROM hr_employee e,hr_employeestat s"
				+ " WHERE e.empstatid=s.statvalue AND s.isquota=1 AND e.ospid=" + op.ospid.getValue() + ") tb";
		int se = Integer.valueOf(con.openSql2List(sqlstr).get(0).get("ct"));
		return new HRQuotaUsedInfo(op.quota.getAsIntDefault(0), se);
	}

	// 非脱产类
	public static boolean checkOrgClassQuota(CDBConnection con, String orgid, String classid, int needq) throws Exception {
		HRQuotaUsedInfo qui = getQuotaClassInfo(con, orgid, classid);
		boolean rst = (qui.getSumq() < (qui.getUsedq() + needq));
		return rst;
	}

	public static HRQuotaUsedInfo getQuotaClassInfo(CDBConnection con, String orgid, String classid) throws Exception {
		Shworg org = new Shworg();
		org.findByID(orgid);
		if (org.isEmpty())
			throw new Exception("ID为【" + orgid + "】的机构不存在");
		int sq = getClassQuota(con, org, classid);// 获取机构职类编制总数； 机构上溯
		int se = getClassEmp(con, org, classid);// 获取机构该职类编制占用数量

		return new HRQuotaUsedInfo(sq, se);
	}

	// op.hwc_idzl.getValue()
	private static int getClassQuota(CDBConnection con, Shworg org, String classid) throws Exception {
		String idp = org.idpath.getValue();
		idp = idp.substring(0, idp.length() - 1);
		String sqlstr = "SELECT IF((SUM(quota) IS NULL),0,SUM(quota)) quota "
				+ " FROM hr_quotaoc "
				+ " WHERE usable=1 AND classid=" + classid + " AND orgid IN(" + idp + ")";
		return Integer.valueOf(con.openSql2List(sqlstr).get(0).get("quota"));
	}

	private static int getClassEmp(CDBConnection con, Shworg org, String classid) throws NumberFormatException, Exception {
		String idp = org.idpath.getValue();
		idp = idp.substring(0, idp.length() - 1);
		String sqlstr = "SELECT COUNT(*) ct FROM "
				+ " (SELECT DISTINCT e.*"
				+ " FROM hr_employee e,hr_employeestat s,hr_orgposition op"
				+ " WHERE e.empstatid=s.statvalue AND s.isquota=1 AND e.ospid=op.ospid AND e.orgid=op.orgid"
				+ " AND op.hwc_idzl=" + classid + " AND e.orgid IN(" + idp + "))tb";
		return Integer.valueOf(con.openSql2List(sqlstr).get(0).get("ct"));
	}

}
