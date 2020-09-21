package com.hr.attd.ctr;

import java.util.Calendar;
import java.util.Date;

import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.attd.entity.Hrkq_calc;
import com.hr.util.CalcThread;

public class CtrHrkq_calc extends JPAController {
	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished)
			throws Exception {
		if (isFilished) {
			doExeKQCacl(jpa, con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc nxtproc, boolean isFilished) throws Exception {
		if (isFilished) {
			doExeKQCacl(jpa, con);
		}
	}

	private void doExeKQCacl(CJPA jpa, CDBConnection con) throws Exception {
		Hrkq_calc ca = (Hrkq_calc) jpa;
		if (ca.schtype.getAsIntDefault(0) == 1) { // 1立即 2一次 3每天 4每周 5每月
			// 立即执行
			// (new CacalKQData()).calcKQReq(ca, con);
			// ca.exstat.setAsInt(1);// 正在执行
			// ca.save();// 不通过事务 直接保存 避免其它调度 找到继续执行，会报错 可能会有问题
			// CalcThread ct = new CalcThread(ca, ca.pool); // 线程运行
			// ct.start();
		} else {
			Date nxttime = null;
			if (ca.schtype.getAsIntDefault(0) == 2) {
				Date curdate = new Date();
				String sctime = ca.sctime.getValue();
				Date curexetime = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(curdate) + " " + sctime);
				if (curexetime.getTime() > curdate.getTime()) {
					nxttime = curexetime;
				} else {
					nxttime = Systemdate.dateDayAdd(curexetime, 1);
				}
			} else
				nxttime = CacalKQData.getNextExecTime(ca);

			if ((nxttime == null) || (nxttime.getTime() <= (new Date().getTime()))) {
				new Exception("获取下次执行时间错误");
			}
			ca.nxtexectime.setAsDatetime(nxttime);
			ca.save(con);
		}
	}
}
