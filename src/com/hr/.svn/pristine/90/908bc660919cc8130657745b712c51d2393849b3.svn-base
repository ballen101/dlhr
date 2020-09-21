package com.hr.perm.ctr;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.generic.Shworg;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.base.entity.Hr_orgposition;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_quota_adjust;
import com.hr.perm.entity.Hr_traindisp_batch;
import com.hr.perm.entity.Hr_traindisp_batchline;
import com.hr.perm.entity.Hr_trainentry_batch;
import com.hr.salary.ctr.CtrSalaryCommon;

public class CtrHr_traindisp_batch extends JPAController {

	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		wf.subject.setValue(((Hr_traindisp_batch) jpa).tdipb_code.getValue());
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc arg3, boolean arg4) throws Exception {
		// TODO Auto-generated method stub
		if (arg4)
			dodisp(jpa, con);
	}

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished)
			dodisp(jpa, con);
	}

	private void dodisp(CJPA jpa, CDBConnection con) throws Exception {
		// 是否需要检测编制
		Hr_employee emp = new Hr_employee();
		Hr_orgposition ho = new Hr_orgposition();
		Shworg org = new Shworg();
		Hr_traindisp_batch tb = (Hr_traindisp_batch) jpa;
		CJPALineData<Hr_traindisp_batchline> tbls = tb.hr_traindisp_batchlines;
		for (CJPABase jp : tbls) {
			Hr_traindisp_batchline tbl = (Hr_traindisp_batchline) jp;
			emp.clear();
			emp.findByID4Update(con, tbl.er_id.getValue(), false);
			if (emp.isEmpty()) {
				throw new Exception("ID为【" + tbl.er_id.getValue() + "】的人事档案不存在");
			}
			if (emp.empstatid.getAsInt() != 8) {
				throw new Exception("只有【见习】状态人事资料允许【实习分配】");
			}
			ho.clear();
			ho.findByID(tbl.nospid.getValue(), false);
			if (ho.isEmpty()) {
				throw new Exception("ID为【" + tbl.nospid.getValue() + "】的机构职位未发现");
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
			emp.empstatid.setAsInt(4);// 正式员工
			emp.orgid.setValue(org.orgid.getValue());
			emp.orgcode.setValue(org.code.getValue());
			emp.orgname.setValue(org.extorgname.getValue());
			emp.idpath.setValue(org.idpath.getValue());
			emp.save(con);
		}
		tb.tdipbdate.setAsDatetime( Systemdate.getDateByyyyy_mm_dd(Systemdate.getStrDate()));
		tb.save(con);
		CtrSalaryCommon.newSalaryChangeLog(con,tb);
	}
}
