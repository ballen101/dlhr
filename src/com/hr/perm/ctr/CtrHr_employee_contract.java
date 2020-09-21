package com.hr.perm.ctr;

import com.corsair.cjpa.CJPABase;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.perm.entity.Hr_employee_contract;

public class CtrHr_employee_contract extends JPAController {

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			setContractRenew(jpa, con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con,Shwwf wf, Shwwfproc proc,
			Shwwfproc nxtproc, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			setContractRenew(jpa, con);
		}
	}

	// 提交新的合同将原有效合同状态改为续签
	private void setContractRenew(CJPA jpa, CDBConnection con) throws Exception {
		Hr_employee_contract hrec = (Hr_employee_contract) jpa;

		String sqlstr = "SELECT * FROM hr_employee_contract WHERE  contractstat=1 AND stat=9"
				+ " AND er_id=" + hrec.er_id.getValue();
		Hr_employee_contract contract = new Hr_employee_contract();
		// CJPALineData<Hr_employee_contract> contracts = new
		// CJPALineData<Hr_employee_contract>(Hr_employee_contract.class);
		// contracts.findDataBySQL(sqlstr);
		contract.findBySQL(sqlstr);
		if (!contract.isEmpty()) {
			if (contract.con_id.getAsInt() != hrec.con_id.getAsInt()) {
				contract.contractstat.setAsInt(6);
				contract.renew_date.setAsDatetime(hrec.sign_date.getAsDatetime());
				if (contract.deadline_type.getAsInt() == 1) {
					contract.end_date.setAsDatetime(hrec.sign_date.getAsDatetime());
				}
				contract.save(con);
			}
		}

	}

	@Override
	public void BeforeSave(CJPABase jpa, CDBConnection con, boolean selfLink)
			throws Exception {
		// TODO Auto-generated method stub
		//super.BeforeSave(jpa, con, selfLink);
		if(selfLink){
			//checkConctractNumber(jpa);
		}
	}
	
	private void checkConctractNumber(CJPABase jpa) throws Exception{
		if(jpa instanceof Hr_employee_contract){
			Hr_employee_contract hrec=(Hr_employee_contract)jpa;
			if(!hrec.con_id.isEmpty()){
				return ;//已保存无需判断
			}
			String connumber=hrec.con_number.getValue();
			String sqlstr="select count(*) ct from hr_employee_contract where con_number='"+connumber+"'";
			if(Integer.valueOf(hrec.pool.openSql2List(sqlstr).get(0).get("ct"))!=0){
				throw new Exception("合同编号：【"+connumber+"】 重复！");
			}
		}
	}

}
