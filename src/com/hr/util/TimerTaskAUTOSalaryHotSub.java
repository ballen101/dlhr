package com.hr.util;

import java.util.TimerTask;

import com.corsair.dbpool.util.Logsw;
import com.hr.salary.ctr.CtrHr_salary_hotsub_qual;
import com.hr.salary.ctr.CtrHr_salary_postsub;

public class TimerTaskAUTOSalaryHotSub extends TimerTask{
	@Override
	public void run() {
		Logsw.debug("薪资高温津贴资格到期");
		try{
			CtrHr_salary_hotsub_qual.scanOutTimeHotSubQual();//高温补贴资格过期
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
