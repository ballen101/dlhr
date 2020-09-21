package com.hr.perm.ctr;

import java.util.Date;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.perm.entity.Hr_employee_contract;
import com.hr.perm.entity.Hr_employee_contract_relieve;

public class CtrHr_employee_contract_relieve extends JPAController{

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if(isFilished){
			relieveContract(jpa,con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con,Shwwf wf, Shwwfproc proc,
			Shwwfproc nxtproc, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if(isFilished){
			relieveContract(jpa,con);
		}
	}
	
	private void relieveContract(CJPA jpa, CDBConnection con)throws Exception{
		Hr_employee_contract_relieve hrconrl =  (Hr_employee_contract_relieve)jpa;
		if (hrconrl.isEmpty())
			throw new Exception("没找到相关联的解除合同表单!");
		if (!hrconrl.con_id.isEmpty() ) {
			Hr_employee_contract hrecon = new Hr_employee_contract();
			hrecon.findByID(hrconrl.con_id.getValue());
			if (hrecon.isEmpty()) {
				throw new Exception("没找到关联ID为"
						+ hrconrl.con_id.getValue() + "的合同资料!");
			}
			if ((hrecon.stat.getAsInt() == 9)
					&& (hrecon.contractstat.getAsInt() == 1)) {
				hrecon.contractstat.setAsInt(5);
				if ((hrecon.deadline_type.getAsInt() == 1)
						&& (!hrecon.begin_date.isEmpty())) {
					hrecon.end_date.setAsDatetime(new Date());
				}
				hrecon.save(con);

			} else {
				throw new Exception("表单关联的合同资料未审核或合同非有效状态!");
			}
		}
		hrconrl.contractstat.setAsInt(5);
		if ((hrconrl.deadline_type.getAsInt() == 1)
				&& (!hrconrl.begin_date.isEmpty())) {
			hrconrl.end_date.setAsDatetime(new Date());
		}
		hrconrl.save(con);
	}
	

}
