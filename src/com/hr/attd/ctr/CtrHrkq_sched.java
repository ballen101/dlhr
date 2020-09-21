package com.hr.attd.ctr;

import java.util.Date;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.hr.attd.entity.Hrkq_resign;
import com.hr.attd.entity.Hrkq_sched;
import com.hr.attd.entity.Hrkq_sched_line;

public class CtrHrkq_sched extends JPAController {

	
	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		wf.subject.setValue(((Hrkq_sched) jpa).scdode.getValue());
	}
	
	// 不为空的返回值，将作为查询结果返回给前台
	@Override
	public String OnCCoSave(CDBConnection con, CJPA jpa) throws Exception {
		Hrkq_sched sc = (Hrkq_sched) jpa;
		sc.lct.setAsInt(sc.hrkq_sched_lines.size());
		toupcaselines(con, jpa);
		// checkJCTime(con, jpa);
		/*
		 * CJPALineData<Hrkq_sched_line> ls = sc.hrkq_sched_lines;
		 * float sm = 0;
		 * for (CJPABase sl : ls) {
		 * Hrkq_sched_line l = (Hrkq_sched_line) sl;
		 * sm += l.dayratio.getAsFloatDefault(0);
		 * }
		 * if (sm != 100)
		 * throw new Exception("所有班次行【天占比】合计必须为100");
		 */
		return null;
	}

	private void toupcaselines(CDBConnection con, CJPA jpa) {
		Hrkq_sched sc = (Hrkq_sched) jpa;
		CJPALineData<Hrkq_sched_line> ls = sc.hrkq_sched_lines;
		for (CJPABase sl1 : ls) {
			Hrkq_sched_line l1 = (Hrkq_sched_line) sl1;
			l1.frtime.setValue(l1.frtime.getValue().toUpperCase());
			l1.frvtimebg.setValue(l1.frvtimebg.getValue().toUpperCase());
			l1.tovtimebg.setValue(l1.tovtimebg.getValue().toUpperCase());
			l1.totime.setValue(l1.totime.getValue().toUpperCase());
			l1.frvtimebg.setValue(l1.frvtimebg.getValue().toUpperCase());
			l1.tovtimebg.setValue(l1.tovtimebg.getValue().toUpperCase());
		}
	}

	private void checkJCTime(CDBConnection con, CJPA jpa) throws Exception {
		String ym = "1900-01-01";
		Hrkq_sched sc = (Hrkq_sched) jpa;
		CJPALineData<Hrkq_sched_line> ls = sc.hrkq_sched_lines;
		for (CJPABase sl1 : ls) {
			Hrkq_sched_line l1 = (Hrkq_sched_line) sl1;
			for (CJPABase sl2 : ls) {
				Hrkq_sched_line l2 = (Hrkq_sched_line) sl2;
				if (l1 != l2) {
					String frtime = l1.frtime.getValue().toUpperCase();
					frtime.subSequence(0, 1).equals("T");
					Date f1 = Systemdate.getDateByStr(ym + " " + l1.frtime.getValue() + ":00");
					Date t1 = Systemdate.getDateByStr(ym + " " + l1.totime.getValue() + ":00");
					Date f2 = Systemdate.getDateByStr(ym + " " + l2.frtime.getValue() + ":00");
					Date t2 = Systemdate.getDateByStr(ym + " " + l2.totime.getValue() + ":00");
					if (Systemdate.isOverlapDate(f1, t1, f2, t2)) {
						throw new Exception("班次号【" + l1.bcno.getValue() + "】与【" + l2.bcno.getValue() + "】时间段交叉");
					}
				}
			}

		}
	}

	// 不为空的返回值，将作为查询结果返回给前台
	@Override
	public String OnCCoDel(CDBConnection con, Class<CJPA> jpaclass, String id) throws Exception {
		String sqlstr = "select count(*) ct from hrkq_workschmonthlist where ";
		String sqlw = "";
		for (int i = 1; i <= 31; i++) {
			sqlw += "(scid" + i + "=" + id + ")or";
		}
		if (!sqlw.isEmpty())
			sqlw = sqlw.substring(0, sqlw.length() - 2);
		sqlstr = sqlstr + sqlw;
		if (Integer.valueOf(con.openSql2List(sqlstr).get(0).get("ct")) != 0)
			throw new Exception("班制已经排班不允许删除");
		return null;
	}
}
