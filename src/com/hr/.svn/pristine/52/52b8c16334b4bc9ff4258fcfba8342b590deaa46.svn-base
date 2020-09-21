package com.hr.perm.ctr;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.generic.Shworg;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.base.entity.Hr_orgposition;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_traintran_batch;
import com.hr.perm.entity.Hr_traintran_batchline;
import com.hr.perm.entity.Hr_transfer_prob;
import com.hr.salary.ctr.CtrSalaryCommon;

public class CtrlHr_traintran_batch extends JPAController {

	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		wf.subject.setValue(((Hr_traintran_batch) jpa).ttranb_code.getValue());
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc arg3, boolean arg4) throws Exception {
		// TODO Auto-generated method stub
		if (arg4)
			dotraintran(jpa, con);
	}

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished)
			dotraintran(jpa, con);
	}

	private void dotraintran(CJPA jpa, CDBConnection con) throws Exception {
		Hr_employee emp = new Hr_employee();
		Hr_orgposition ho = new Hr_orgposition();
		Shworg org = new Shworg();
		Hr_traintran_batch ttb = (Hr_traintran_batch) jpa;
		CJPALineData<Hr_traintran_batchline> ttbls = ttb.hr_traintran_batchlines;
		for (CJPABase jp : ttbls) {
			Hr_traintran_batchline tbl = (Hr_traintran_batchline) jp;
			emp.findByID4Update(con, tbl.er_id.getValue(), false);
			if (emp.isEmpty()) {
				throw new Exception("ID为【" + tbl.er_id.getValue() + "】的人事档案不存在");
			}
			if (emp.empstatid.getAsInt() != 7) {
				throw new Exception("只有实【习试用】状态人事资料允许【实习转正】");
			}
			ho.clear();
			ho.findByID(tbl.ospid.getValue(), false);
			if (ho.isEmpty()) {
				throw new Exception("ID为【" + tbl.ospid.getValue() + "】的机构职位未发现");
			}

			org.clear();
			org.findByID(ho.orgid.getValue());
			if (org.isEmpty())
				throw new Exception("没有找到ID为【" + ho.orgid.getValue() + "】的机构");

			emp.lv_id.setValue(ho.lv_id.getValue());
			emp.lv_num.setValue(ho.lv_num.getValue());
			emp.hg_id.setValue(ho.hg_id.getValue());
			emp.hg_code.setValue(ho.hg_code.getValue());
			emp.hg_name.setValue(ho.hg_name.getValue());
			emp.ospid.setValue(ho.ospid.getValue());
			emp.ospcode.setValue(ho.ospcode.getValue());
			emp.sp_name.setValue(ho.sp_name.getValue());
			emp.hwc_namezl.setValue(ho.hwc_namezl.getValue());
			emp.hwc_namezq.setValue(ho.hwc_namezq.getValue());
			emp.hwc_namezz.setValue(ho.hwc_namezz.getValue());
			emp.orgid.setValue(org.orgid.getValue());
			emp.orgcode.setValue(org.code.getValue());
			emp.orgname.setValue(org.extorgname.getValue());
			emp.idpath.setValue(org.idpath.getValue());
			
			emp.empstatid.setAsInt(8);// 见习期
			emp.save(con);
		}
		CtrSalaryCommon.newSalaryChangeLog(con,ttb);
	}
}
