package com.hr.attd.ctr;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.hr.attd.entity.Hr_kq_makeup_monthsubmit;
import com.hr.attd.entity.Hr_kq_makeup_monthsubmit_line;
import com.hr.attd.entity.Hr_kq_monthsubmit;
import com.hr.attd.entity.Hr_kq_monthsubmit_line;

public class CtrHr_kq_makeup_monthsubmit extends JPAController { 
	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished) throws Exception {
		//检查当月是否有提报
		Hr_kq_makeup_monthsubmit h = (Hr_kq_makeup_monthsubmit) jpa;
		CJPALineData<Hr_kq_makeup_monthsubmit_line> ls = h.hr_kq_makeup_monthsubmit_lines;
		for (CJPABase j : ls) {
			Hr_kq_makeup_monthsubmit_line l = (Hr_kq_makeup_monthsubmit_line) j;
			String sqlstr = "SELECT IFNULL(COUNT(*),0) ct FROM `Hr_kq_makeup_monthsubmit` h,  `Hr_kq_makeup_monthsubmit_line`  l"
					+ " WHERE h.mmkq_id =l.mmkq_id"
					+ " AND h.`submitdate`='" + h.submitdate.getValue() + "' AND l.`er_id`=" + l.er_id.getValue()
					+ " AND h.`stat`>1 AND h.`stat`<10";
			if (Integer.valueOf(h.pool.openSql2List(sqlstr).get(0).get("ct")) != 0)
				//提报日期【" + Systemdate.getStrDateyyyy_mm_dd(h.submitdate.getAsDatetime()) + "】【工号"+l.employee_code.getValue()+"】已经存在提保记录"
				throw new Exception(l.employee_code.getValue()+l.employee_name.getValue()+(h.submitdate.getAsDatetime().getMonth()+1)+"月已存在考勤提报数据，请勿重复提报");
		}
	}
}
