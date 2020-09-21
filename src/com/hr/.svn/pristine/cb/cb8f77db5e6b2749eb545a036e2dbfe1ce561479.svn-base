package com.hr.attd.ctr;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.attd.entity.Hrkq_holidayapp_cancel;
import com.hr.attd.entity.Hrkq_leave_blance;
import com.hr.attd.entity.Hrkq_leave_blance_extend;

public class CtrHrkq_leave_blance_extend extends JPAController {
	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		wf.subject.setValue(((Hrkq_leave_blance_extend) jpa).lbecode.getValue());
	}
	
	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			doextend(jpa, con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc nxtproc, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			doextend(jpa, con);
		}
	}

	private void doextend(CJPA jpa, CDBConnection con) throws Exception {
		Hrkq_leave_blance_extend be = (Hrkq_leave_blance_extend) jpa;
		Hrkq_leave_blance lb = new Hrkq_leave_blance();
		lb.findByID(be.lbid.getValue());
		if (lb.isEmpty())
			throw new Exception("ID为【" + be.lbid.getValue() + "】的可调休信息不存在");
		if (lb.extended.getAsIntDefault(0) == 1)
			throw new Exception("ID为【" + be.lbid.getValue() + "】的可调休信息已经展期");
		if (lb.valdate.getAsDatetime().getTime() > be.newvaldate.getAsDatetime().getTime())
			throw new Exception("展期日期必须大于到期日期");
		lb.valdate.setAsDatetime(be.newvaldate.getAsDatetime());
		lb.extended.setAsInt(1);
		lb.save(con);
	}
}
