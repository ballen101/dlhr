package com.hr.timer;

import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

import com.corsair.dbpool.util.Systemdate;
import com.hr.perm.ctr.CtrHrPerm;

public class TimerTaskMonthy extends TimerTask {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// 每月1号月结
		Calendar now = Calendar.getInstance();
		int minute=now.get(Calendar.MINUTE);
		int hour=now.get(Calendar.HOUR_OF_DAY);
		if (now.get(Calendar.DAY_OF_MONTH) == 1 && hour==1 && minute==15) {
			String ym = Systemdate.getStrDateByFmt(Systemdate.dateMonthAdd(new Date(), -1), "yyyy-MM");
			try {
				CtrHrPerm.putmonthemployee(ym);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				CtrHrPerm.putmonthosp(ym);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				CtrHrPerm.putmonthqtc(ym);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
