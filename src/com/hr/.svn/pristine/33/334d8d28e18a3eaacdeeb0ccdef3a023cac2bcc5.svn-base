package com.hr.insurance.ctr;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.insurance.entity.Hr_ins_buyinsurance;
import com.hr.insurance.entity.Hr_ins_prebuyins;
import com.hr.perm.entity.Hr_employee;

public class CtrHr_ins_buyinsurance extends JPAController{

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf,
			boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if(isFilished){
			//setempinsbuystat(jpa,con);
			setprebuyinsstat(jpa,con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf,
			Shwwfproc proc, Shwwfproc nxtproc, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if(isFilished){
			//setempinsbuystat(jpa,con);
			setprebuyinsstat(jpa,con);
		}
	}
	
	private void setempinsbuystat(CJPA jpa, CDBConnection con)throws Exception{
		Hr_ins_buyinsurance bi=(Hr_ins_buyinsurance)jpa;
			Hr_employee emp = new Hr_employee();
			emp.findByID(con, bi.er_id.getValue(), false);
			if (emp.isEmpty()) {
				throw new Exception("ID为【" + bi.er_id.getValue() + "】的人事档案不存在");
			}
			emp.insurancestat.setAsInt(2);
			emp.save(con);
	}
	
	private void setprebuyinsstat(CJPA jpa, CDBConnection con)throws Exception{
		Hr_ins_buyinsurance bi=(Hr_ins_buyinsurance)jpa;
		Hr_ins_prebuyins pbi=new Hr_ins_prebuyins();
		String sqlstr="SELECT * FROM hr_ins_prebuyins WHERE isbuyins=2 AND er_id="+bi.er_id.getValue()+" AND dobuyinsdate='"+bi.buydday.getValue()+"' ";
		pbi.findBySQL(con, sqlstr, false);
		if(!pbi.isEmpty()){
			pbi.isbuyins.setAsInt(1);
			pbi.save(con);
		}
	}

}
