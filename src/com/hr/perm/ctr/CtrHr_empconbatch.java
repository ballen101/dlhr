package com.hr.perm.ctr;

import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.generic.Shworg;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.attd.entity.Hrkq_onduty;
import com.hr.base.entity.Hr_employeestat;
import com.hr.perm.entity.Hr_empconbatch;
import com.hr.perm.entity.Hr_empconbatch_line;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_employee_contract;

public class CtrHr_empconbatch extends JPAController {
	
	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		wf.subject.setValue(((Hr_empconbatch) jpa).conbatch_code.getValue());
	}
	
	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if(isFilished){
			doSignContract(jpa,con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con,Shwwf wf, Shwwfproc proc,
			Shwwfproc nxtproc, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if(isFilished){
			doSignContract(jpa,con);
		}
	}

	private void doSignContract(CJPA jpa,CDBConnection con) throws Exception{
		Hr_empconbatch emcb=(Hr_empconbatch)jpa;
		Hr_employee emp = new Hr_employee();
		Hr_employeestat estat = new Hr_employeestat();
		Shworg org = new Shworg();
		CJPALineData<Hr_empconbatch_line> cbls=emcb.hr_empconbatch_lines;
		for(int i=0;i<cbls.size();i++){
			Hr_empconbatch_line cbl= (Hr_empconbatch_line)cbls.get(i);
			emp.findByID(cbl.er_id.getValue());
			if(emp.isEmpty()){
				throw new Exception("ID为【" + cbl.er_id.getValue() + "】的人事档案不存在");
			}
			estat.findBySQL("select * from hr_employeestat where statvalue=" + emp.empstatid.getValue());
			
			if((estat.statvalue.getAsInt()==6)||(estat.statvalue.getAsInt()==11)||(estat.statvalue.getAsInt()==12)||(estat.statvalue.getAsInt()==13)){
				throw new Exception("当前状态状态为【" + estat.language1.getValue() + "】的人事档案不允许签订合同");
			}
			org.clear();
			org.findByID(cbl.orgid.getValue());
			if (org.isEmpty())
				throw new Exception("没有找到ID为【" + cbl.orgid.getValue() + "】的机构");
			
			Hr_employee_contract contract=new Hr_employee_contract();
			contract.con_number.setValue(cbl.con_number.getValue());
			contract.contract_name.setValue(cbl.contract_name.getValue());
			contract.er_id.setValue(emp.er_id.getValue());
			contract.employee_code.setValue(emp.employee_code.getValue());
			contract.employee_name.setValue(emp.employee_name.getValue());
			contract.orgid.setValue(emp.orgid.getValue());
			contract.orgcode.setValue(emp.orgcode.getValue());
			contract.orgname.setValue(emp.orgname.getValue());
			contract.lv_id.setValue(emp.lv_id.getValue());
			contract.lv_num.setValue(emp.lv_num.getValue());
			contract.ospid.setValue(emp.ospid.getValue());
			contract.ospcode.setValue(emp.ospcode.getValue());
			contract.sp_name.setValue(emp.sp_name.getValue());
			contract.hiredday.setValue(emp.hiredday.getValue());
			contract.contract_type.setValue(cbl.contract_type.getValue());
			contract.sign_number.setValue(cbl.sign_number.getValue());
			contract.remark.setValue(cbl.remark.getValue());
			contract.contractstat.setValue(emcb.contractstat.getValue());
			contract.signyears.setValue(emcb.signyears.getValue());
			contract.deadline_type.setValue(emcb.deadline_type.getValue());
			contract.sign_date.setValue(cbl.sign_date.getValue());
			contract.end_date.setValue(cbl.end_date.getValue());
			contract.renew_date.setValue(emcb.renew_date.getValue());
			contract.is_remind.setValue(emcb.is_remind.getValue());
			contract.usable.setValue(emcb.usable.getValue());
			contract.ispermanent.setValue(emcb.ispermanent.getValue());
			contract.version.setValue(emcb.version.getValue());
			contract.idpath.setValue(emp.idpath.getValue());
			contract.stat.setAsInt(9);
			contract.save(con);
		}
	}

}
