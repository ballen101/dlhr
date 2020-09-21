package com.hr.util;

import java.util.Date;
import java.util.TimerTask;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.CDBPool;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.Systemdate;
import com.hr.attd.ctr.CacalKQData;
import com.hr.attd.entity.Hrkq_calc;

//计算
public class TimerTaskHRKQCalc extends TimerTask {
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Logsw.debug("检查是否有计算需求");
		try {
			//CDBPool pool = new Hrkq_calc().pool;
			String nschtime = Systemdate.getStrDateByFmt(new Date(), "yyyy-MM-dd HH:mm:") + "00";
			String sqlstr = "SELECT * FROM hrkq_calc WHERE stat=9 AND nxtexectime<'" + nschtime + "' AND nxtexectime IS NOT NULL AND schtype>1 and exstat<>1 AND scstat<>3";
			CJPALineData<Hrkq_calc> cas = new CJPALineData<Hrkq_calc>(Hrkq_calc.class);
			cas.findDataBySQL(sqlstr);
			for (CJPABase jpa : cas) {
				Hrkq_calc ca = (Hrkq_calc) jpa;
				// ca.exstat.setAsInt(1);// 正在执行
				// ca.save();// 不通过事务 直接保存 避免其它调度 找到继续执行，会报错
				CalcThread ct = new CalcThread(ca.clcid.getValue()); // 线程运行
				ct.start();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
