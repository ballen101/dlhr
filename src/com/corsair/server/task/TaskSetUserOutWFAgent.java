package com.corsair.server.task;

import java.util.TimerTask;

import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.Logsw;

public class TaskSetUserOutWFAgent extends TimerTask {

	@Override
	public void run() {
		Logsw.debug("TaskScanWorkFlowPress.run......");
		try {
			String sqlstr = "UPDATE `shwuser` u SET u.goout=2  WHERE u.`goout`=1 AND u.`gooutendtime`<NOW()";
			DBPools.defaultPool().execsql(sqlstr, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
