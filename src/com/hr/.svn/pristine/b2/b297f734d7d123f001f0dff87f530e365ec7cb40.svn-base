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
import com.hr.perm.entity.Hr_leavejob;
import com.hr.perm.entity.Hr_leavejobbatch;
import com.hr.perm.entity.Hr_quota_adjust;
import com.hr.perm.entity.Hr_quota_adjustline;
import com.hr.perm.entity.Hr_quota_chglog;

public class CtrHr_quota_adjust extends JPAController {

	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		wf.subject.setValue(((Hr_quota_adjust) jpa).qadjcode.getValue());
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc arg3, boolean arg4) throws Exception {
		// TODO Auto-generated method stub
		if (arg4)
			saveQuotaChgLog(jpa, con);
	}

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished)
			saveQuotaChgLog(jpa, con);
	}

	private void saveQuotaChgLog(CJPA jpa, CDBConnection con) throws Exception {
		Hr_quota_adjust hqa = (Hr_quota_adjust) jpa;
		Hr_orgposition hop = new Hr_orgposition();
		CJPALineData<Hr_quota_adjustline> hr_quota_releaselines = hqa.hr_quota_adjustlines;
		for (CJPABase jpal : hr_quota_releaselines) {
			Hr_quota_adjustline hqal = (Hr_quota_adjustline) jpal;
			hop.clear();
			hop.findByID4Update(con, hqal.ospid.getValue(), false);
			int adjtype = hqal.adjtype.getAsInt();
			int quota = 0;
			if (adjtype == 1) {
				quota = hqal.adjquota.getAsInt();
			} else if (adjtype == 2) {
				quota = -1 * hqal.adjquota.getAsInt();
			} else
				throw new Exception("机构【" + hqal.orgname.getAsFloat() + "】职位【" + hqal.sp_name.getValue() + "】调整类型错误");
			CtrHr_quota_release.dochgquota(con, hop, quota, hqa.qadjid.getValue(), hqal.qadjlineid.getValue(), hqa.qadjcode.getValue(), "2");
		}
	}

}
