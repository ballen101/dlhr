package com.hr.perm.ctr;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.generic.Shworg;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.perm.entity.Hr_quota_release;
import com.hr.perm.entity.Hr_quotaoc;
import com.hr.perm.entity.Hr_quotaoc_chglog;
import com.hr.perm.entity.Hr_quotaoc_release;
import com.hr.perm.entity.Hr_quotaoc_releaseline;

public class CtrHr_quotaoc_release extends JPAController {
	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		Hr_quotaoc_release et = (Hr_quotaoc_release) jpa;
		String sbjet = et.oocrlscode.getValue() + "-" + et.quota_year.getValue();
		wf.subject.setValue(sbjet);
	}
	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con,Shwwf wf, Shwwfproc proc,
			Shwwfproc arg3, boolean arg4) throws Exception {
		// TODO Auto-generated method stub
		if (arg4)
			saveQuotaChgLog(con, jpa);
	}

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished)
			saveQuotaChgLog(con, jpa);
	}

	private void saveQuotaChgLog(CDBConnection con, CJPA jpa) throws Exception {
		Hr_quotaoc_release qocr = (Hr_quotaoc_release) jpa;
		Shworg org = new Shworg();

		CJPALineData<Hr_quotaoc_releaseline> hr_quotaoc_releaselines = qocr.hr_quotaoc_releaselines;
		Hr_quotaoc_chglog qocc = new Hr_quotaoc_chglog();
		Hr_quotaoc qoc = new Hr_quotaoc();
		for (CJPABase jpal : hr_quotaoc_releaselines) {
			Hr_quotaoc_releaseline qocrl = (Hr_quotaoc_releaseline) jpal;
			org.findByID(qocrl.orgid.getValue());
			// 编制
			qoc.clear();
			String sqlstr = "select * from hr_quotaoc where orgid=" + qocrl.orgid.getValue() + " and classid=" + qocrl.hwc_idzl.getValue();
			qoc.findBySQL4Update(con, sqlstr, false);
			int oldquota = qoc.quota.getAsIntDefault(0);
			int newquota = qocrl.quota.getAsInt();
			qoc.orgid.setValue(qocrl.orgid.getValue());
			qoc.classid.setValue(qocrl.hwc_idzl.getValue());
			qoc.quota.setAsInt(newquota);
			qoc.usable.setAsInt(1);
			qoc.save(con, false);
			// log
			qocc.clear();
			qocc.hwc_idzl.setValue(qocrl.hwc_idzl.getValue());
			qocc.hw_codezl.setValue(qocrl.hw_codezl.getValue());
			qocc.hwc_namezl.setValue(qocrl.hwc_namezl.getValue());
			qocc.orgid.setValue(qocrl.orgid.getValue());
			qocc.orgcode.setValue(qocrl.orgcode.getValue());
			qocc.orgname.setValue(qocrl.orgname.getValue());
			qocc.sourceid.setValue(qocr.qocrlsid.getValue());
			qocc.sourcecode.setValue(qocr.oocrlscode.getValue());
			qocc.sourcelineid.setValue(qocrl.qocrlslineid.getValue());
			qocc.oldquota.setAsInt(oldquota);
			qocc.newquota.setAsInt(newquota);
			qocc.idpath.setValue(org.idpath.getValue());
			qocc.sourcetype.setAsInt(1);
			qocc.save(con, false);
		}

	}
}
