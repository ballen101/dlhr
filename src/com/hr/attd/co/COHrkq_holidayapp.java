package com.hr.attd.co;

import java.util.HashMap;

import com.corsair.server.base.CSContext;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CReport;
import com.corsair.server.util.CorUtil;
import com.hr.attd.entity.Hrkq_holidayapp;

@ACO(coname = "web.hrkq.holiday")
public class COHrkq_holidayapp {
	@ACOAction(eventname = "findhas4cancel", Authentication = true, notes = "获取可以销假的请假单")
	public String findhas4cancel() throws Exception {//
		String sqlstr = "SELECT * FROM hrkq_holidayapp WHERE stat=9 AND timebk IS NULL";
		sqlstr = sqlstr + CSContext.getIdpathwhere();
		return new CReport(sqlstr, null).findReport();
	}

	@ACOAction(eventname = "findhals4cancel", Authentication = true, notes = "获取可以销假的请假单明细行")
	public String findhals4cancel() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String haid = CorUtil.hashMap2Str(parms, "haid", "需要参数haid");
		String sqlstr = "select * from hrkq_holidayapp_month where haid=" + haid;
		return new CReport(sqlstr, null).findReport();
	}
}
