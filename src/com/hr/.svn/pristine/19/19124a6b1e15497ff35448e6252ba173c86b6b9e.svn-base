package com.hr.perm.ctr;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_quota_adjust;
import com.hr.perm.entity.Hr_train_reg;
import com.hr.salary.ctr.CtrSalaryCommon;

public class CtrHr_train_reg extends JPAController {
	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		Hr_train_reg et = (Hr_train_reg) jpa;
		Hr_employee emp = new Hr_employee();
		emp.findByID4Update(con, et.er_id.getValue(), false);
		if (emp.isEmpty()) {
			throw new Exception("ID为【" + et.er_id.getValue() + "】的人事档案不存在");
		}
		String sbjet = et.treg_code.getValue();
		wf.subject.setValue(sbjet);
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc arg3, boolean arg4) throws Exception {
		// TODO Auto-generated method stub
		if (arg4)
			doReg(jpa, con);
	}

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished)
			doReg(jpa, con);
	}

	private void doReg(CJPA jpa, CDBConnection con) throws Exception {
		Hr_train_reg tr = (Hr_train_reg) jpa;
		Hr_employee emp = new Hr_employee();
		emp.findByID4Update(con, tr.er_id.getValue(), false);
		if (emp.isEmpty()) {
			throw new Exception("ID为【" + tr.er_id.getValue() + "】的人事档案不存在");
		}
		if (emp.empstatid.getAsInt() != 6) {// 待入职
			throw new Exception("【待入职】状态人事资料才允许登记审批");
		}
		if (tr.regtype.getAsInt() == 1) { // 1:实习生
			emp.empstatid.setAsInt(1);// 实习期
			tr.isfrainflish.setAsInt(2);// 实习未完成
		} else if (tr.regtype.getAsInt() == 2) { // 2：毕业生
			emp.empstatid.setAsInt(7);// 实习试用期
			tr.isfrainflish.setAsInt(1);// 实习已经完成
			CtrSalaryCommon.newSalaryChangeLog(con,tr);//类型为毕业生时生成薪资异动，异动类型为实习生入职
		} else
			throw new Exception("登记类型错误，只允许为1、2");
		emp.save(con);
	}
}
