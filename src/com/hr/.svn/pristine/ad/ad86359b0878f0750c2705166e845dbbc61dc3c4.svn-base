package com.hr.util;

import java.util.TimerTask;

import com.corsair.dbpool.util.Logsw;
import com.hr.salary.ctr.CtrHr_salary_postsub;

public class TimerTaskAUTOSalary extends TimerTask{
	@Override
	public void run() {
		Logsw.debug("薪资岗位津贴到期");
		try{
			CtrHr_salary_postsub.scanOutTimePostSub();//岗位津贴过期调薪
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
