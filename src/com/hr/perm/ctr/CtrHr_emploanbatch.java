package com.hr.perm.ctr;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.hr.perm.entity.Hr_emploanbatch;

public class CtrHr_emploanbatch extends JPAController {
	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		Hr_emploanbatch et = (Hr_emploanbatch) jpa;
		String sbjet = et.loancode.getValue() + "-" + et.neworgname.getValue();
		wf.subject.setValue(sbjet);
	}
}
