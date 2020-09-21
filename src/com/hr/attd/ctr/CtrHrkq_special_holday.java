package com.hr.attd.ctr;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.attd.entity.Hrkq_sched;
import com.hr.attd.entity.Hrkq_special_holday;
import com.hr.attd.entity.Hrkq_special_holdayline;

public class CtrHrkq_special_holday extends JPAController {

	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		wf.subject.setValue(((Hrkq_special_holday) jpa).sphcode.getValue());
	}

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished)
			throws Exception {
		if (isFilished) {
			doUpdateBlance(jpa, con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc nxtproc, boolean isFilished) throws Exception {
		if (isFilished) {
			doUpdateBlance(jpa, con);
		}
	}

	private void doUpdateBlance(CJPA jpa, CDBConnection con) throws Exception {
		Hrkq_special_holday sh = (Hrkq_special_holday) jpa;
		CJPALineData<Hrkq_special_holdayline> ls = sh.hrkq_special_holdaylines;
		for (CJPABase j : ls) {
			Hrkq_special_holdayline l = (Hrkq_special_holdayline) j;
			CtrHrkq_leave_blance.putLeave_blance(con, l.sphlid.getValue(), sh.sphcode.getValue(), "特殊", 5, sh.sphdays.getAsFloatDefault(0) * 8,
					sh.valdate.getAsDatetime(), l.er_id.getValue(), l.remark.getValue(), 0f);
		}

	}
}
