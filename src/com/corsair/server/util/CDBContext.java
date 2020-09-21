package com.corsair.server.util;

import com.corsair.dbpool.IDBContext;
import com.corsair.server.base.CSContext;

public class CDBContext implements IDBContext {

	@Override
	public String getCurUserName() {
		return CSContext.getUserNameEx();
	}

	@Override
	public String getClentIP() {
		return CSContext.getClientIP();
	}

}
