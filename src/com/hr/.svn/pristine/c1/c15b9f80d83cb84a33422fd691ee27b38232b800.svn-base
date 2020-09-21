package com.hr.perm.ctr;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.perm.entity.Hr_empptjob_app;
import com.hr.perm.entity.Hr_empptjob_break;
import com.hr.perm.entity.Hr_empptjob_info;
import com.hr.salary.ctr.CtrSalaryCommon;

public class CtrHr_empptjob_break extends JPAController {

	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		wf.subject.setValue(((Hr_empptjob_break) jpa).ptjbcode.getValue());
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc arg3, boolean arg4) throws Exception {
		// TODO Auto-generated method stub
		if (arg4)
			breadPartJobInfo(jpa, con);
	}

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished)
			breadPartJobInfo(jpa, con);
	}

	private void breadPartJobInfo(CJPA jpa, CDBConnection con) throws Exception {
		Hr_empptjob_break bk = (Hr_empptjob_break) jpa;
		Hr_empptjob_app app = new Hr_empptjob_app();
		app.findByID(bk.ptjaid.getValue());
		if (app.isEmpty())
			throw new Exception("ID为【】的兼职申请单不存在");
		app.breaked.setAsInt(1);
		app.ptbbdate1.setValue(bk.ptbbdate.getValue());
		app.save(con);
		CtrSalaryCommon.newSalaryChangeLog(con, app, false);

		Hr_empptjob_info ji = new Hr_empptjob_info();
		String sqlstr = "select * from hr_empptjob_info where sourceid=" + app.ptjaid.getValue();
		ji.findBySQL(sqlstr, false);
		if (!ji.isEmpty()) {
			ji.enddate.setAsDatetime(bk.ptbbdate.getAsDatetime());
			ji.save(con);
		}
	}

}
