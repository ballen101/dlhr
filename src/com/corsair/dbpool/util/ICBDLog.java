package com.corsair.dbpool.util;

import com.corsair.dbpool.CDBConnection;

/**
 * 连接池日志接口
 * 
 * @author Administrator
 *
 */
public interface ICBDLog {
	public void writelog(CDBConnection con, String msg);
}
