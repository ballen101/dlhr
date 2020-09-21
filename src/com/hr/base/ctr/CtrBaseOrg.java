package com.hr.base.ctr;

import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.CDBPool;
import com.corsair.server.generic.Shworg;
import com.corsair.server.util.GetNewSystemCode;
import com.hr.perm.entity.Hr_employee;

public class CtrBaseOrg {
    public static void BeforeSave(CDBConnection con, Shworg org) throws Exception {
	if (org.attribute1.isEmpty()) {
	    String oldcode;
	    try {
		oldcode = (new GetNewSystemCode()).dogetnewsyscode(org, "55");
		org.attribute1.setValue(oldcode);
	    } catch (Exception e) {
		org.attribute1.setValue("生成旧编码错误");
		e.printStackTrace();
	    }
	}
	if ((!org.orgid.isEmpty()) && (org.usable.getAsIntDefault(0) == 2)) {
	    String sqlstr = "SELECT IFNULL(COUNT(*), 0) ct FROM  `hr_employee` WHERE `empstatid` < 10 and empstatid>0  AND `idpath` LIKE '"
		    + org.idpath.getValue() + "%' ";
	    int ct = Integer.valueOf(con.openSql2List(sqlstr).get(0).get("ct").toString());
	    if (ct > 0)
		throw new Exception("该机构下有【" + ct + "】个在职人员，不允许禁用");
	}
    }

    public static void BeforeDelete(CDBConnection con, Shworg org) throws Exception {
	CDBPool pool = (new Hr_employee()).pool;
	if (Integer.valueOf(pool.openSql2List("select count(*) ct from hr_employee where orgid=" + org.orgid.getValue()).get(0).get("ct")) > 0) {
	    throw new Exception("【" + org.orgname.getValue() + "】包含员工资料，不允许删除！");
	}
	if (Integer.valueOf(pool.openSql2List("select count(*) ct from hr_orgposition where orgid=" + org.orgid.getValue()).get(0).get("ct")) > 0) {
	    throw new Exception("【" + org.orgname.getValue() + "】包含职位，不允许删除！");
	}
	if (Integer.valueOf(pool.openSql2List("select count(*) ct from hr_quotaoc where orgid=" + org.orgid.getValue()).get(0).get("ct")) > 0) {
	    throw new Exception("【" + org.orgname.getValue() + "】包含编制，不允许删除！");
	}

    }

}
