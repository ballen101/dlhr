package com.corsair.cjpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import com.corsair.cjpa.util.CJPASqlUtil;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.CDBConnection.DBType;
import com.corsair.dbpool.util.CPoolSQLUtil;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.PraperedSql;
import com.corsair.dbpool.util.PraperedValue;

/**
 * JPA加载数据实现
 * 
 * @author Administrator
 *
 */
public abstract class CJPALoad extends CJPAJSON {

	public CJPALoad() throws Exception {

	}

	public CJPALoad(String sqlstr) throws Exception {
		this();
		findBySQL(sqlstr);
	}

	/**
	 * 查询并锁定对象
	 * 
	 * @param con
	 * @throws Exception
	 */
	public void findByID4Update(CDBConnection con) throws Exception {
		CField idfd = this.getIDField();
		if (idfd == null) {
			throw new Exception("根据ID查询JPA【" + this.getClass().getSimpleName() + "】数据没发现ID字段");
		}
		findByID4Update(con, idfd.getValue(), true);
	}

	/**
	 * 查询并锁定对象
	 * 
	 * @param con
	 * @param id
	 * @param selflink
	 * @throws Exception
	 */
	public void findByID4Update(CDBConnection con, String id, boolean selflink) throws Exception {
		CField idfd = this.getIDField();
		if (idfd == null) {
			throw new Exception("根据ID查询JPA【" + this.getClass().getSimpleName() + "】数据没发现ID字段");
		}
		String sqlfdname = CJPASqlUtil.getSqlField(pool.getDbtype(), idfd.getFieldname());
		String sqlvalue = CJPASqlUtil.getSqlValue(pool.getDbtype(), idfd.getFieldtype(), id);
		String sqlstr = "select * from " + tablename + " where " + sqlfdname + "=" + sqlvalue + " for update";
		findBySQL4Update(con, sqlstr, selflink);
	}

	/**
	 * 查询并锁定对象
	 * 
	 * @param con
	 * @param sqlstr
	 * @param selflink
	 * @throws Exception
	 */
	public void findBySQL4Update(CDBConnection con, String sqlstr, boolean selflink) throws Exception {
		try {
			clear();
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = con.con.createStatement();
				long time = System.currentTimeMillis();
				rs = stmt.executeQuery(sqlstr);
				if (DBPools.getCblog() != null) {
					long et = System.currentTimeMillis();
					DBPools.getCblog().writelog(con, "【执行SQL：" + sqlstr + "；耗时:" + (et - time) + "】");
				}
				if (!rs.next()) {
					return;
				}
				load4DataSet(con, rs, selflink, true);
			} finally {
				DBPools.safeCloseS_R(stmt, rs);
			}
		} catch (Exception e) {
			throw new Exception("根据SQL载入数据错误:" + e.getMessage() + sqlstr);
		}
	}

	// 查询并锁定对象 end

	/**
	 * 根据ID查询
	 * 
	 * @param selflink
	 * @return
	 * @throws Exception
	 */
	public CJPABase findByID(boolean selflink) throws Exception {
		CField idfd = this.getIDField();
		if (idfd == null) {
			throw new Exception("根据ID查询JPA【" + this.getClass().getSimpleName() + "】数据没发现ID字段");
		}
		findByID(idfd.getValue(), selflink);
		return (CJPABase) this;
	}

	/**
	 * 根据ID查询
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public CJPABase findByID(String id) throws Exception {
		findByID(id, true);
		return (CJPABase) this;
	}

	/**
	 * 根据ID查询
	 * 
	 * @param con
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public CJPABase findByID(CDBConnection con, String id) throws Exception {
		findByID(con, id, true);
		return (CJPABase) this;
	}

	/**
	 * 根据ID查询
	 * 
	 * @param con
	 * @param id
	 * @param selflink
	 * @return
	 * @throws Exception
	 */
	public CJPABase findByID(CDBConnection con, String id, boolean selflink) throws Exception {
		CField idfd = this.getIDField();
		if (idfd == null) {
			throw new Exception("根据ID查询JPA【" + this.getClass().getSimpleName() + "】数据没发现ID字段");
		}
		String sqlfdname = CJPASqlUtil.getSqlField(pool.getDbtype(), idfd.getFieldname());
		String sqlvalue = CJPASqlUtil.getSqlValue(pool.getDbtype(), idfd.getFieldtype(), id);
		String sqlstr = "select * from " + tablename + " where " + sqlfdname + "=" + sqlvalue;
		findBySQL(con, sqlstr, selflink);
		return (CJPABase) this;
	}

	/**
	 * 根据ID查询
	 * 
	 * @param id
	 * @param selflink
	 * @return
	 * @throws Exception
	 */
	public CJPABase findByID(String id, boolean selflink) throws Exception {
		if (pool == null) {
			throw new Exception("JPA通过sql载入数据前，必须初始化连接池pool");
		}
		CDBConnection con = pool.getCon(this);
		try {
			findByID(con, id, selflink);
		} catch (Exception e) {
			throw e;
		} finally {
			con.close();
		}
		return (CJPABase) this;
	}

	/**
	 * 根据SQL查询
	 * 
	 * @param psql
	 * @return
	 * @throws Exception
	 */
	public CJPABase findByPSQL(PraperedSql psql) throws Exception {
		return findByPSQL(psql, true);
	}

	/**
	 * 根据SQL查询
	 * 
	 * @param psql
	 * @param selflink
	 * @return
	 * @throws Exception
	 */
	public CJPABase findByPSQL(PraperedSql psql, boolean selflink) throws Exception {
		if (pool == null) {
			throw new Exception("JPA通过sql载入数据前，必须初始化连接池pool");
		}
		CDBConnection con = pool.getCon(this);
		try {
			findByPSQL(con, psql, selflink);
			return (CJPABase) this;
		} catch (Exception e) {
			throw new Exception("根据SQL载入数据错误:" + e.getMessage() + psql.getSqlstr());
		} finally {
			con.close();
		}
	}

	/**
	 * 根据SQL查询
	 * 
	 * @param con
	 * @param psql
	 * @param selflink
	 * @return
	 * @throws Exception
	 */
	public CJPABase findByPSQL(CDBConnection con, PraperedSql psql, boolean selflink) throws Exception {
		clear();
		String sqlstr = psql.getSqlstr();
		if (con.getDBType() == DBType.mysql)
			sqlstr = sqlstr + " limit 1";

		if (con.getDBType() == DBType.oracle)
			// sqlstr = sqlstr + " and rownum<=1";
			sqlstr = "select * from (" + sqlstr + ") where rownum<=1";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = con.con.prepareStatement(sqlstr);
			for (int i = 0; i < psql.getParms().size(); i++) {
				PraperedValue pv = psql.getParms().get(i);
				CPoolSQLUtil.setSqlPValue(pstmt, i + 1, pv);
			}
			long time = System.currentTimeMillis();
			rs = pstmt.executeQuery();
			if (DBPools.getCblog() != null) {
				long et = System.currentTimeMillis();
				DBPools.getCblog().writelog(con, "【执行SQL：" + psql.getSqlstr() + "；耗时:" + (et - time) + "】");
			}
			if (!rs.next()) {
				return (CJPABase) this;
			}
			load4DataSet(con, rs, selflink, false);
			return (CJPABase) this;
		} finally {
			DBPools.safeCloseS_R(pstmt, rs);
		}
	}

	/**
	 * 根据SQL查询
	 * 
	 * @param sqlstr
	 * @return
	 * @throws Exception
	 */
	public CJPABase findBySQL(String sqlstr) throws Exception {
		findBySQL(sqlstr, true);
		return (CJPABase) this;
	}

	/**
	 * 根据SQL查询
	 * 
	 * @param selflink
	 * @return
	 * @throws Exception
	 */
	public CJPABase findBySQL(boolean selflink) throws Exception {
		String sqlstr = "select * from " + this.tablename + " where 1=1 " + SqlWhere;
		findBySQL(sqlstr, selflink);
		return (CJPABase) this;
	}

	/**
	 * 根据SQL查询
	 * 
	 * @param con
	 * @param sqlstr
	 * @param selflink 是否查询行表
	 * @return
	 * @throws Exception
	 */
	public CJPABase findBySQL(CDBConnection con, String sqlstr, boolean selflink) throws Exception {
		clear();
		if (con.getDBType() == DBType.mysql)
			sqlstr = sqlstr + " limit 1";

		if (con.getDBType() == DBType.oracle)
			// sqlstr = sqlstr + " and rownum<=1";
			sqlstr = "select * from (" + sqlstr + ") where rownum<=1";
		Logsw.debug("执行查询:" + sqlstr);
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.con.createStatement();
			long time = System.currentTimeMillis();
			rs = stmt.executeQuery(sqlstr);
			if (DBPools.getCblog() != null) {
				long et = System.currentTimeMillis();
				DBPools.getCblog().writelog(con, "【执行SQL：" + sqlstr + "；耗时:" + (et - time) + "】");
			}
			if (!rs.next()) {
				return (CJPABase) this;
			}
			load4DataSet(con, rs, selflink, false);
			return (CJPABase) this;
		} finally {
			DBPools.safeCloseS_R(stmt, rs);
		}
	}

	/**
	 * 根据SQL查询
	 * 
	 * @param sqlstr
	 * @param selflink 是否查询行表
	 * @return
	 * @throws Exception
	 */
	public CJPABase findBySQL(String sqlstr, boolean selflink) throws Exception {
		if (pool == null) {
			throw new Exception("JPA通过sql载入数据前，必须初始化连接池pool");
		}
		CDBConnection con = pool.getCon(this);
		try {
			return findBySQL(con, sqlstr, selflink);
		} catch (Exception e) {
			throw new Exception("根据SQL载入数据错误:" + e.getMessage() + sqlstr);
		} finally {
			con.close();
		}
	}

	/**
	 * 从数据集加载
	 * 
	 * @param con
	 * @param rs
	 * @param selflink
	 * @param update
	 * @return
	 * @throws Exception
	 */
	public boolean load4DataSet(CDBConnection con, ResultSet rs, boolean selflink, boolean update) throws Exception {
		try {
			ResultSetMetaData rsmd = rs.getMetaData(); // 得到结果集的定义结构
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				CField fd = cfieldNoCase(rsmd.getColumnName(i));
				if (fd == null) {
					continue;
				}
				fd.setFieldtype(rsmd.getColumnType(i));
				fd.setSize(rsmd.getColumnDisplaySize(i));
				String value = CjpaUtil.getShowvalue(con.getDBType(), rs, i, fd.getFieldtype());
				fd.setValue(value);
				fd.setChanged(false);
			}
		} catch (Exception e) {
			throw new Exception("CJAP从ResultSet载入数据错误:" + e.getMessage());
		}
		if (selflink) {
			for (CJPALineData<CJPABase> ld : cJPALileDatas) {
				ld.loadDataByOwner(con, true, true, update);
			}
		}
		setAllJpaStat(CJPAStat.RSLOAD4DB);
		return true;
	}
}
