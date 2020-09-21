package com.corsair.server.task;

import java.util.TimerTask;

import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.Logsw;

public class TaskScanTokenTimeout extends TimerTask {

	@Override
	public void run() {
		try {
			String sqlstr = "DELETE  FROM shwusertoken WHERE timeout<" + System.currentTimeMillis();
			DBPools.defaultPool().execsql(sqlstr, false);
		} catch (Exception e) {
			Logsw.debug("run TaskScanTokenTimeout");
			e.printStackTrace();
		}
	}

}
