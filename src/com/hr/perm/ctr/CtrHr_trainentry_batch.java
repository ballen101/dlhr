package com.hr.perm.ctr;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.base.entity.Hr_orgposition;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_traindisp_batch;
import com.hr.perm.entity.Hr_trainentry_batch;
import com.hr.perm.entity.Hr_trainentry_batchline;
import com.hr.perm.entity.Hr_traintran_batch;
import com.hr.salary.ctr.CtrSalaryCommon;

public class CtrHr_trainentry_batch extends JPAController {

	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		wf.subject.setValue(((Hr_trainentry_batch) jpa).tetyb_code.getValue());
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc arg3, boolean arg4) throws Exception {
		// TODO Auto-generated method stub
		if (arg4)
			doteeb(jpa, con);
	}

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished)
			doteeb(jpa, con);
	}

	private void doteeb(CJPA jpa, CDBConnection con) throws Exception {
		Hr_employee emp = new Hr_employee();
		Hr_orgposition ho = new Hr_orgposition();
		Hr_trainentry_batch tb = (Hr_trainentry_batch) jpa;
		CJPALineData<Hr_trainentry_batchline> tbls = tb.hr_trainentry_batchlines;
		for (CJPABase jp : tbls) {
			Hr_trainentry_batchline tbl = (Hr_trainentry_batchline) jp;
			emp.clear();
			emp.findByID4Update(con, tbl.er_id.getValue(), false);
			if (emp.isEmpty()) {
				throw new Exception("ID为【" + tbl.er_id.getValue() + "】的人事档案不存在");
			}
			if (emp.empstatid.getAsInt() != 1) {
				throw new Exception("只有实习状态人事资料允许【实习入职】");
			}
			ho.clear();
			ho.findByID(tbl.ospid.getValue(), false);
			if (ho.isEmpty()) {
				throw new Exception("ID为【" + tbl.ospid.getValue() + "】的机构职位未发现");
			}
			emp.lv_id.setValue(ho.lv_id.getValue());
			emp.lv_num.setValue(ho.lv_num.getValue());
			emp.hg_id.setValue(ho.hg_id.getValue());
			emp.hg_code.setValue(ho.hg_code.getValue());
			emp.hg_name.setValue(ho.hg_name.getValue());
			emp.ospid.setValue(ho.ospid.getValue());
			emp.ospcode.setValue(ho.ospcode.getValue());
			emp.sp_name.setValue(ho.sp_name.getValue());
			emp.empstatid.setAsInt(7);// 实习试用
			emp.hiredday.setValue(tbl.hiredday.getValue());// 入职日期
			emp.save(con);
		}
		CtrSalaryCommon.newSalaryChangeLog(con,tb);
	}
}
