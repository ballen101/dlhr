package com.hr.attd.co;

import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CSearchForm;
import com.hr.perm.entity.Hr_empptjob_app;

@ACO(coname = "web.hrkq.overtimeqb")
public class COHrkq_overtime_qual_break {
	@ACOAction(eventname = "finotapp4break", Authentication = true, notes = "查找允许中断的,权限范围内")
	public String finotapp4break() throws Exception {
		String sqlstr = "select * from (SELECT h.oq_id, h.oq_code,  h.apply_date, h.over_type, h.begin_date, h.end_date, h.dealtype, h.employee_type, h.permonlimit,"
				+ " l.oql_id, l.er_id, l.employee_code, l.employee_name, l.orgid, l.orgcode, l.orgname, l.lv_id,l. lv_num,l.ospid, l.ospcode, l.sp_name, l.orghrlev, "
				+ " l.emplev FROM hrkq_overtime_qual h,hrkq_overtime_qual_line l WHERE h.oq_id=l.oq_id AND h.stat=9 AND h.begin_date<= CURDATE() AND  h.end_date>CURDATE() and l.breaked=2 ) tb where 1=1  ";
		Hr_empptjob_app app = new Hr_empptjob_app();
		return CSearchForm.doExec2JSON(app.pool, sqlstr);
	}
}
