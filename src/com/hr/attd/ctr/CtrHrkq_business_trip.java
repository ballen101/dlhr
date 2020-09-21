package com.hr.attd.ctr;

import java.util.Date;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.attd.entity.Hrkq_business_trip;
import com.hr.attd.entity.Hrkq_leave_blance;
import com.hr.attd.entity.Hrkq_wkoff;
import com.hr.util.HRUtil;

public class CtrHrkq_business_trip extends JPAController {
	@Override
	public String OnCCoVoid(CDBConnection con, CJPA jpa) throws Exception {
		Hrkq_business_trip bt = (Hrkq_business_trip) jpa;
		Hrkq_leave_blance lb = new Hrkq_leave_blance();
		String sqlstr = "select * from hrkq_leave_blance where stype=4 and sid=" + bt.bta_id.getValue();
		lb.findBySQL(sqlstr);
		if (lb != null) {
			if (lb.usedlbtime.getAsIntDefault(-1) > 0) {
				throw new Exception("表单已经有调休记录，无法作废");
			}
			lb.delete(con, true);
		}
		return null;
	}

	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		Hrkq_business_trip bt = (Hrkq_business_trip) jpa;
		wf.subject.setValue(bt.bta_code.getValue());
	}

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished) throws Exception {
		Hrkq_business_trip bt = (Hrkq_business_trip) jpa;
		Date bgtime = bt.begin_date.getAsDatetime();
		Date edtime = bt.end_date.getAsDatetime();
		if(!bt.creator.getValue().equals("inteface")){
			HrkqUtil.checkValidDate(bgtime);
		}
	
		HrkqUtil.checkAllOverlapDatetime(4, bt.bta_id.getValue(), bt.er_id.getValue(), bt.employee_name.getValue(), bgtime, edtime);
		if (isFilished)
			doWFAgent(jpa, con);
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc nxtproc, boolean isFilished) throws Exception {
		if (isFilished)
			doWFAgent(jpa, con);
	}

	private void doWFAgent(CJPA jpa, CDBConnection con) {
		Hrkq_business_trip wo = (Hrkq_business_trip) jpa;
		if (wo.iswfagent.getAsIntDefault(0) == 1) {
			HRUtil.setWFAgend(con, wo.employee_code.getValue(), wo.begin_date.getAsDatetime(), wo.end_date.getAsDatetime());
		}
	}
}
