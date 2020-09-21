package com.corsair.server.task;

import java.util.Date;
import java.util.TimerTask;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.cjpa.CJPAWorkFlow2;
import com.corsair.server.cjpa.CWFNotify;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfprocuser;

public class TaskScanWorkFlowPress extends TimerTask {

	@Override
	public void run() {
		Logsw.debug("TaskScanWorkFlowPress.run......");
		int ljavm = 60 * 24;// token有效期 60分钟
		int wpc = ConstsSw.getSysParmIntDefault("WFPressCycle", 0);
		if (wpc == 0)
			return;

		try {
			String sqlstr = "SELECT w.wfname,w.subject,u.* FROM shwwf w,shwwfproc p,shwwfprocuser u"
					+ " WHERE w.wfid=p.wfid AND p.procid=u.procid"
					+ "   AND w.stat=1 AND p.stat=2 AND u.stat=1 AND (u.nextpresstime<NOW() OR u.nextpresstime IS NULL) "
					+ "   ORDER BY w.wfid,p.procid";
			JSONArray pus = DBPools.defaultPool().opensql2json_O(sqlstr);
			Shwwfprocuser pu = new Shwwfprocuser();
			Shwwf wf = new Shwwf();
			for (int i = 0; i < pus.size(); i++) {
				JSONObject jpu = pus.getJSONObject(i);
				try {
					pu.clear();
					pu.findByID(jpu.getString("wfprocuserid"), false);
					wf.clear();
					wf.findByID(jpu.getString("wfid"), false);
					CWFNotify.wfPressNotify(CJPAWorkFlow2.getJPAByWF(wf), wf, jpu.getString("userid"), jpu.getString("wfprocuserid"));
					Date nextpdate = Systemdate.dateDayAdd(new Date(), wpc);
					pu.pressnum.setAsInt(pu.pressnum.getAsIntDefault(0) + 1);
					pu.nextpresstime.setAsDatetime(nextpdate);
					pu.save();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
