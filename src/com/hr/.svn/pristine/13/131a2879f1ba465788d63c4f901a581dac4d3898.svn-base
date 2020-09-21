package com.hr.salary.ctr;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.salary.entity.Hr_salary_techsub;

public class CtrHr_salary_techsub extends JPAController{

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf,
			boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if(isFilished){
			Hr_salary_techsub ts=(Hr_salary_techsub)jpa;
			CtrSalaryCommon.newSalaryChangeLog(con,ts,true);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf,
			Shwwfproc proc, Shwwfproc nxtproc, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if(isFilished){
			Hr_salary_techsub ts=(Hr_salary_techsub)jpa;
			CtrSalaryCommon.newSalaryChangeLog(con,ts,true);
		}
	}
	

}
