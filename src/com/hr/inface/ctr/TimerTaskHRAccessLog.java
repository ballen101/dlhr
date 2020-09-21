package com.hr.inface.ctr;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.CDBPool;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.Systemdate;
import com.hr.access.entity.Hr_access_oplog;
import com.hr.inface.entity.View_Hrms_TXJocatList;
import com.hr.inface.entity.View_ICCO_OpLog;

public class TimerTaskHRAccessLog extends TimerTask {
	private static int syncrowno = 5000;

	@Override
	public void run() {
		try {
			String sqlstr = "SELECT MAX(id) mx FROM hr_access_oplog";
			Hr_access_oplog accessoplog = new Hr_access_oplog();
			List<HashMap<String, String>> sws = accessoplog.pool.openSql2List(sqlstr);
			long mx = ((sws.size() == 0) || (sws.get(0).get("mx") == null)) ? 0 : Long.valueOf(sws.get(0).get("mx"));
			CJPALineData<View_ICCO_OpLog> oplogls = new CJPALineData<View_ICCO_OpLog>(View_ICCO_OpLog.class);
			sqlstr = "SELECT top " + syncrowno + " * FROM dbo.view_ICCO_OpLog t WHERE t.id>" + mx + " order by id";// LIMIT 0," + syncrowno;
			oplogls.findDataBySQL(sqlstr, true, false);
			CJPALineData<Hr_access_oplog> hroplogls = new CJPALineData<Hr_access_oplog>(Hr_access_oplog.class);
			for (CJPABase jpa : oplogls) {
				View_ICCO_OpLog oplog = (View_ICCO_OpLog) jpa;
				Hr_access_oplog aoplog = new Hr_access_oplog();
				aoplog.id.setValue(oplog.id.getValue());
				aoplog.opTime.setValue(oplog.opTime.getValue());
				aoplog.BeWrite.setValue(oplog.BeWrite.getValue());
				aoplog.opUser.setValue(oplog.opUser.getValue());
				aoplog.opComPuter.setValue(oplog.opComPuter.getValue());
				aoplog.entid.setValue("1");
				aoplog.idpath.setValue("1,");
				aoplog.creator.setValue("SYSTEM");
				aoplog.updator.setValue("SYSTEM");
				hroplogls.add(aoplog);
			}
			if (hroplogls.size() > 0)
				hroplogls.saveBatchSimple();// 高速存储
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
