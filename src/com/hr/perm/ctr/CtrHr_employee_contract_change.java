package com.hr.perm.ctr;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.perm.entity.Hr_employee_contract;
import com.hr.perm.entity.Hr_employee_contract_change;

public class CtrHr_employee_contract_change extends JPAController{

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if(isFilished){
			changeContract(jpa,con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con,Shwwf wf, Shwwfproc proc,
			Shwwfproc nxtproc, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if(isFilished){
			changeContract(jpa,con);
		}
	}
	
	private void changeContract(CJPA jpa, CDBConnection con)throws Exception{
		Hr_employee_contract_change hrconchange =  (Hr_employee_contract_change)jpa;
		if (hrconchange.isEmpty())
			throw new Exception("没找到相关联的变更合同表单!");

		if (!hrconchange.con_id.isEmpty() ) {
			Hr_employee_contract hrecon = new Hr_employee_contract();
			hrecon.findByID(hrconchange.con_id.getValue());
			if (hrecon.isEmpty()) {
				throw new Exception("没找到关联ID为"
						+ hrconchange.con_id.getValue() + "的合同资料!");
			}
			if ((hrecon.stat.getAsInt() == 9)
					&& (hrecon.contractstat.getAsInt() == 1)) {
				hrecon.con_number.setValue(hrconchange.con_number.getValue());
				hrecon.contract_name.setValue(hrconchange.contract_name.getValue());
				hrecon.contract_type.setAsInt(hrconchange.contract_type.getAsInt());
				hrecon.contractstat.setAsInt(hrconchange.contractstat.getAsInt());
				hrecon.sign_date.setAsDatetime(hrconchange.sign_date.getAsDatetime());
				hrecon.version.setValue(hrconchange.version.getValue());
				if(!hrconchange.begin_date.isEmpty()){
					hrecon.begin_date.setAsDatetime(hrconchange.sign_date.getAsDatetime());
				}
				if(hrconchange.deadline_type.getAsInt()==1){
					if(!hrconchange.end_date.isEmpty()){
						hrecon.end_date.setAsDatetime(hrconchange.end_date.getAsDatetime());
					}
					if(!hrconchange.signyears.isEmpty()){
						hrecon.signyears.setValue(hrconchange.signyears.getValue());
					}
				}else{
					hrecon.end_date.setValue(null);
					hrecon.signyears.setValue(null);
				}
				
				if(!hrconchange.renew_date.isEmpty()){
					hrecon.renew_date.setAsDatetime(hrconchange.renew_date.getAsDatetime());
				}
				hrecon.deadline_type.setAsInt(hrconchange.deadline_type.getAsInt());
				hrecon.ispermanent.setAsInt(hrconchange.ispermanent.getAsInt());
				hrecon.is_remind.setAsInt(hrconchange.is_remind.getAsInt());
				hrecon.sign_number.setAsInt(hrconchange.sign_number.getAsInt());
				hrecon.remark.setValue(hrconchange.remark.getValue());
				hrecon.save(con);
			} else {
				throw new Exception("表单关联的合同资料未审核或合同非有效状态!");
			}
		}
	}

}
