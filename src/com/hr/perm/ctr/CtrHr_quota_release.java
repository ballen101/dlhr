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
import com.hr.perm.entity.Hr_quota_adjust;
import com.hr.perm.entity.Hr_quota_chglog;
import com.hr.perm.entity.Hr_quota_release;
import com.hr.perm.entity.Hr_quota_releaseline;

public class CtrHr_quota_release extends JPAController {
	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		Hr_quota_release et = (Hr_quota_release) jpa;
		String sbjet = et.orlscode.getValue() + "-" + et.quota_year.getValue();
		wf.subject.setValue(sbjet);
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc, Shwwfproc arg3, boolean arg4) throws Exception {
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
		Hr_quota_release hqr = (Hr_quota_release) jpa;
		Hr_orgposition hop = new Hr_orgposition();
		CJPALineData<Hr_quota_releaseline> hr_quota_releaselines = hqr.hr_quota_releaselines;
		for (CJPABase jpal : hr_quota_releaselines) {
			Hr_quota_releaseline hqrl = (Hr_quota_releaseline) jpal;
			hop.clear();
			hop.findByID4Update(con, hqrl.ospid.getValue(), false);
			int oldquota = hop.quota.getAsInt();
			int newquota = hqrl.quota.getAsInt();
			int quota = newquota - oldquota;
			System.out.println("sp_name:" + hqrl.sp_name.getValue() + "oldquota:" + oldquota + ";newquota:" + newquota + ";quota:" + quota);
			dochgquota(con, hop, quota, hqr.qrlsid.getValue(), hqrl.qrlslineid.getValue(), hqr.orlscode.getValue(), "1");
			hop.quota.setAsInt(newquota);
			hop.save(con, false);
		}

	}

	// quota允许为负数
	public static void dochgquota(CDBConnection con, Hr_orgposition hop, int quota, String sourceid, String sourcelineid, String sourcecode, String sourcetype)
			throws Exception {
		int oldquota = hop.quota.getAsIntDefault(0);
		int newquota = oldquota + quota;
		if (newquota < 0)
			throw new Exception("机构【" + hop.orgname.getValue() + "】职位【" + hop.sp_name.getValue() + "】调整完的编制小于0");
		hop.quota.setAsInt(newquota);
		hop.save(con, false);
		Shworg org = new Shworg();
		org.findByID(hop.orgid.getValue());
		Hr_quota_chglog hqc = new Hr_quota_chglog();
		hqc.clear();
		hqc.ospid.setValue(hop.ospid.getValue());
		hqc.ospcode.setValue(hop.ospcode.getValue());
		hqc.sp_name.setValue(hop.sp_name.getValue());
		hqc.orgid.setValue(hop.orgid.getValue());
		hqc.orgname.setValue(hop.orgname.getValue());
		hqc.orgcode.setValue(hop.orgcode.getValue());
		hqc.sourceid.setValue(sourceid);
		hqc.sourcelineid.setValue(sourcelineid);
		hqc.sourcecode.setValue(sourcecode);
		hqc.sourcetype.setValue(sourcetype);
		hqc.idpath.setValue(org.idpath.getValue());
		hqc.oldquota.setAsInt(oldquota);
		hqc.newquota.setAsInt(newquota);
		hqc.save(con);
	}
}
