package com.hr.insurance.ctr;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.insurance.entity.Hr_ins_buyins;
import com.hr.insurance.entity.Hr_ins_buyins_line;
import com.hr.perm.entity.Hr_employee;

public class CtrHr_ins_buyins extends JPAController{
	
	
	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf,
			boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if(isFilished)
			setempinsbuystat(jpa,con);
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf,
			Shwwfproc proc, Shwwfproc nxtproc, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if(isFilished)
			setempinsbuystat(jpa,con);
	}

	private void setempinsbuystat(CJPA jpa, CDBConnection con)throws Exception{
		Hr_ins_buyins bi=(Hr_ins_buyins)jpa;
		CJPALineData<Hr_ins_buyins_line> bils=bi.hr_ins_buyins_lines;
		for (CJPABase jpa2 : bils) {
			Hr_ins_buyins_line bil=(Hr_ins_buyins_line)jpa2;
			Hr_employee emp = new Hr_employee();
			emp.findByID4Update(con, bil.er_id.getValue(), false);
			if (emp.isEmpty()) {
				throw new Exception("ID为【" + bil.er_id.getValue() + "】的人事档案不存在");
			}
			emp.insurancestat.setAsInt(2);
			emp.save(con);
		}
	}

}
