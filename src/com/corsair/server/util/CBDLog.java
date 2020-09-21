package com.corsair.server.util;

import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.ICBDLog;
import com.corsair.dbpool.util.Logsw;
import com.corsair.server.base.CSContext;

public class CBDLog implements ICBDLog {
	@Override
	public void writelog(CDBConnection con, String msg) {
		String ms = "【user:" + CSContext.getUserNameNoErr() + "】【id:"+CSContext.getClientIP()+"】【dbsession:" + con.getKey() + "】" + msg;
		Logsw.dblog(ms);
	}

}
