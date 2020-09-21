package com.hr.attd.co;

import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CSearchForm;
import com.hr.attd.entity.Hrkq_leave_blance;

@ACO(coname = "web.hrkq.lbe")
public class COHrkq_leave_blance_extend {
	@ACOAction(eventname = "findbl4ext", Authentication = true, notes = "查找可延期的调休余额")
	public String findbl4ext() throws Exception {
		String sqlstr = "SELECT * FROM( "
				+ "SELECT *, IF(valdate>NOW(),2,1) isexpire,IF(usedlbtime<alllbtime,2,1) usup,IF((valdate>NOW()) AND (usedlbtime<alllbtime),1,2) canuses "
				+ "FROM hrkq_leave_blance where `extended`=2 ) tb where 1=1 and usedlbtime<alllbtime ";
		Hrkq_leave_blance app = new Hrkq_leave_blance();
		return CSearchForm.doExec2JSON(app.pool, sqlstr);
	}
}
