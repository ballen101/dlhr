package com.hr.perm.ctr;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.perm.entity.Hr_black_del;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_leavejob;

public class CtrHr_black_del extends JPAController {
	@Override
	public void OnWfSubmit(CJPA arg0, CDBConnection arg1,Shwwf wf, Shwwfproc arg2,
			Shwwfproc arg3, boolean arg4) throws Exception {
		// TODO Auto-generated method stub
		if (arg4)
			doadd(arg0, arg1);
	}

	@Override
	public void AfterWFStart(CJPA arg0, CDBConnection arg1,Shwwf wf, boolean arg2) throws Exception {
		// TODO Auto-generated method stub
		if (arg2)
			doadd(arg0, arg1);
	}

	private void doadd(CJPA jpa, CDBConnection con) throws Exception {
		Hr_black_del ba = (Hr_black_del) jpa;
		Hr_employee emp = new Hr_employee();
		emp.findByID4Update(con, ba.er_id.getValue(), false);
		if (emp.isEmpty()) {
			throw new Exception("ID为【" + ba.er_id.getValue() + "】的人事档案不存在");
		}
		Hr_leavejob lv = new Hr_leavejob();
		lv.findByID4Update(con, ba.ljid.getValue(), false);
		if (lv.isEmpty())
			throw new Exception("ID为【" + ba.ljid.getValue() + "】的离职表单不存在");
		// if (lv.iscanced.getAsInt() == 1)
		// throw new Exception("离职表单已经撤销，不允许解封黑名单！");
		// lv.isblacklist.setAsInt(1); 不能设置，否则解封后 不能再次加封
		emp.empstatid.setAsInt(12);// 变为离职状态 允许重新入职
		// lv.save(con);
		emp.save(con);
		CtrHrEmployeeUtil.removeBlackList(con, emp.id_number.getValue());

	}
}
