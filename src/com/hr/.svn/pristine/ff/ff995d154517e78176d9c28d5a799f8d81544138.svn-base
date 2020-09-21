package com.hr.base.ctr;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.base.entity.Hr_empaddr_chg;
import com.hr.perm.entity.Hr_employee;

public class CtrHr_empaddr_chg extends JPAController {

	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		wf.subject.setValue(((Hr_empaddr_chg) jpa).empaccode.getValue());

	}

	@Override
	public void OnWfSubmit(CJPA arg0, CDBConnection arg1, Shwwf wf, Shwwfproc arg2,
			Shwwfproc arg3, boolean arg4) throws Exception {
		// TODO Auto-generated method stub
		if (arg4)
			setEmpInfo(arg0, arg1);
	}

	@Override
	public void AfterWFStart(CJPA arg0, CDBConnection arg1, Shwwf wf, boolean arg2) throws Exception {
		// TODO Auto-generated method stub
		if (arg2)
			setEmpInfo(arg0, arg1);
	}

	private void setEmpInfo(CJPA arg0, CDBConnection con) throws Exception {
		Hr_empaddr_chg ecg = (Hr_empaddr_chg) arg0;
		Hr_employee emp = new Hr_employee();
		emp.findByID(con, ecg.er_id.getValue(), false);
		emp.address.setValue(ecg.newaddress.getValue());
		emp.telphone.setValue(ecg.newtelphone.getValue());
		emp.email.setValue(ecg.newemail.getValue());
		emp.cellphone.setValue(ecg.newcellphone.getValue());
		emp.urgencycontact.setValue(ecg.newurgencycontact.getValue());
		emp.urmail.setValue(ecg.newurmail.getValue());
		emp.degree.setValue(ecg.newdegree.getValue());
		emp.major.setValue(ecg.newmajor.getValue());
		emp.married.setValue(ecg.newmarried.getValue());
		emp.registeraddress.setValue(ecg.newregisteraddress.getValue());
		emp.sign_org.setValue(ecg.newsign_org.getValue());
		emp.sign_date.setValue(ecg.newsign_date.getValue());
		emp.expired_date.setValue(ecg.newexpired_date.getValue());
		emp.save(con, false);
	}

}
