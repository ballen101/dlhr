package com.hr.salary.ctr;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.salary.entity.Hr_salary_hotsub_qual_cancel;
import com.hr.salary.entity.Hr_salary_hotsub_qual_line;

public class CtrHr_salary_hotsub_qual_cancel extends JPAController{
	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf,
			boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if(isFilished){
			doCancelHotSubQual(jpa,con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf,
			Shwwfproc proc, Shwwfproc nxtproc, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if(isFilished){
			doCancelHotSubQual(jpa,con);
		}
	}

	private void doCancelHotSubQual(CJPA jpa, CDBConnection con) throws Exception{
		Hr_salary_hotsub_qual_cancel hqc=(Hr_salary_hotsub_qual_cancel)jpa;
		Hr_salary_hotsub_qual_line hsql=new Hr_salary_hotsub_qual_line();
		hsql.findBySQL("SELECT * FROM hr_salary_hotsub_qual_line WHERE  usable=1 AND hsql_id="+hqc.hsql_id.getValue());
		if(!hsql.isEmpty()){
			hsql.usable.setValue(2);
			hsql.canceldate.setValue(hqc.canceldate.getValue());
			hsql.save(con);
		}
		
	}

}
