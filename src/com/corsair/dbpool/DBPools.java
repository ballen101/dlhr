package com.corsair.dbpool;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.corsair.dbpool.util.ICBDLog;

/**
 * 多个连接池
 * 
 * @author Administrator
 *
 */
public class DBPools {
	private static List<CDBPool> dbpools = new ArrayList<CDBPool>();
	private static ICBDLog cblog = null;

	/**
	 * @return
	 */
	public static List<CDBPool> getDbpools() {
		return dbpools;
	}

	public DBPools() {

	}

	/**
	 * 添加连接池
	 * 
	 * @param value
	 */
	public static void addPool(CDBPool value) {
		dbpools.add(value);
	}

	public static CDBPool defaultPool() {
		for (CDBPool p : dbpools) {
			if (p.pprm.isdefault) {
				return p;
			}
		}
		return null;
	}

	/**
	 * 根据名称获取连接池
	 * 
	 * @param poolname
	 * @return
	 */
	public static CDBPool poolByName(String poolname) {
		if ((poolname == null) || (poolname.isEmpty())) {
			return defaultPool();
		}
		for (CDBPool p : dbpools) {
			if (p.pprm.name.equals(poolname)) {
				return p;
			}
		}
		return null;
	}

	/**
	 * 获取日志对象
	 * 
	 * @return
	 */
	public static ICBDLog getCblog() {
		return cblog;
	}

	public static void setCblog(ICBDLog cblog) {
		DBPools.cblog = cblog;
	}

	/**
	 * 安全关闭数据集
	 * 
	 * @param s
	 * @param r
	 */
	public static void safeCloseS_R(Statement s, ResultSet r) {
		if (r != null)
			try {
				r.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if (s != null)
			try {
				s.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}
}
