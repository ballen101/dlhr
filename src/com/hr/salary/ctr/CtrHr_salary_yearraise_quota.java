package com.hr.salary.ctr;

import java.util.ArrayList;

import com.corsair.cjpa.CJPABase;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.PraperedSql;
import com.corsair.server.cjpa.JPAController;
import com.hr.salary.entity.Hr_salary_yearraise_quota;

public class CtrHr_salary_yearraise_quota extends JPAController{
	@Override
	public void OnSave(CJPABase jpa, CDBConnection con,
			ArrayList<PraperedSql> sqllist, boolean selfLink) throws Exception {
		// TODO Auto-generated method stub
		Hr_salary_yearraise_quota yrq=(Hr_salary_yearraise_quota)jpa;
		String quotadate=yrq.salaryquotadate.getValue()+"-01";
		String sqlstr="UPDATE `hr_salary_yearraise_quota` SET usable=2 WHERE usable=1 AND orgid="+
		yrq.orgid.getValue()+" AND salaryquotadate='"+quotadate+
		"' AND yrqid<>"+yrq.yrqid.getValue();
		con.execsql(sqlstr);
	}
	

}
