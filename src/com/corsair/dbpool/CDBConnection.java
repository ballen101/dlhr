package com.corsair.dbpool;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.CJSONTree;
import com.corsair.dbpool.util.CPoolSQLUtil;
import com.corsair.dbpool.util.CResultSetMetaData;
import com.corsair.dbpool.util.CResultSetMetaDataItem;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.PraperedSql;
import com.corsair.dbpool.util.PraperedValue;

public class CDBConnection {

	/**
	 * 数据连接Session状态
	 * 
	 * @author Administrator
	 *
	 */
	public enum ConStat {
		ready, inuse
	}

	/**
	 * 数据库类型
	 * 
	 * @author Administrator
	 *
	 */
	public enum DBType {
		sqlserver, hsql, mysql, oracle, unknow
	};

	private DBPoolParms pprm;
	public CDBPool pool;
	private Object owner;// 谁申请的
	public Connection con;
	private ConStat stat;
	private String key; // 数据库session 唯一序号
	private long time; // 最后活动时间
	private String lastcmd = null;// 最后执行的命令
	private long lastcmdstarttime = 0;// 最后命令开始时间
	private String curUserName; // 不知道能不能获取到
	private String clientIP;// 客户端IP
	private boolean distimeout = false;// 禁止超时

	/**
	 * 数据库连接宿主
	 * 
	 * @param owner
	 */
	public void setOwner(Object owner) {
		this.owner = owner;
	}

	/**
	 * 活动时间
	 * 
	 * @return
	 */
	public long getTime() {
		return time;
	}

	/**
	 * 连接标识
	 * 
	 * @return
	 */
	public String getKey() {
		return key;
	}

	/**
	 * 返回状态
	 * 
	 * @return
	 */
	public ConStat getStat() {
		return stat;
	}

	/**
	 * 使用中
	 * 
	 * @return
	 */
	public boolean isInUse() {
		return (stat == ConStat.inuse);
	}

	// public Connection getCon() {
	// return con;
	// }

	// 是否允许执行命令
	private void check2CMD() throws Exception {
		if (stat != ConStat.inuse) {
			throw new Exception("非使用中的连接【" + key + "】，不允许执行命令(或连接已被连接池回收)");
		}
	}

	/**
	 * 开始数据库事务
	 */
	public void startTrans() {
		try {
			check2CMD();
			if (DBPools.getCblog() != null)
				DBPools.getCblog().writelog(this, "开始事务");
			if (!con.getAutoCommit()) {
				throw new Exception("开始事务处理错误：已经是事务处理");
			}
			time = System.currentTimeMillis();
			con.setAutoCommit(false);
		} catch (Exception e) {
			Logsw.error(e.getMessage());
		}
	}

	/**
	 * @return 是否在事务中
	 */
	public boolean isInTrans() {
		try {
			return (!con.getAutoCommit());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 提交事务
	 */
	public void submit() {
		try {
			check2CMD();
			if (DBPools.getCblog() != null)
				DBPools.getCblog().writelog(this, "提交事务");
			if (con.getAutoCommit()) {
				throw new Exception("提交事务错误：未在事务处理状态");
			}
			time = System.currentTimeMillis();
			lastcmd = "commit";
			if (pool.getIct() != null) {
				setCurUserName(pool.getIct().getCurUserName());
				setClientIP(pool.getIct().getClentIP());
			}
			setLastcmdstarttime(time);
			con.commit();
		} catch (Exception e) {
			Logsw.error(e.getMessage());
		} finally {
			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// close();
		}

	}

	/**
	 * 回滚事务
	 */
	public void rollback() {
		try {
			check2CMD();
			if (DBPools.getCblog() != null)
				DBPools.getCblog().writelog(this, "回滚事务");
			if (con.getAutoCommit()) {
				throw new Exception("回滚事务错误：未在事务处理状态");
			}
			time = System.currentTimeMillis();
			lastcmd = "rollback";
			if (pool.getIct() != null) {
				setCurUserName(pool.getIct().getCurUserName());
				setClientIP(pool.getIct().getClentIP());
			}
			setLastcmdstarttime(time);
			con.rollback();
		} catch (Exception e) {
			Logsw.error(e.getMessage());
		} finally {
			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// close();
		}
	}

	/**
	 * @return 数据库类型
	 */
	public DBType getDBType() {
		try {
			time = System.currentTimeMillis();
			DatabaseMetaData dbmd = this.con.getMetaData();
			String dataBaseType = dbmd.getDatabaseProductName(); // 获取数据库类型
			if (("Microsoft SQL Server").equals(dataBaseType)) {
				return DBType.sqlserver;
			} else if (("HSQL Database Engine").equals(dataBaseType)) {
				return DBType.hsql;
			} else if (("MySQL").equals(dataBaseType)) {
				return DBType.mysql;
			} else if (("Oracle").equals(dataBaseType)) {
				return DBType.oracle;
			} else {
				return DBType.unknow;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return DBType.unknow;
	}

	/**
	 * 创建连接
	 * 
	 * @param pool 连接池
	 * @param pprm 连接参数
	 * @throws Exception
	 */
	public CDBConnection(CDBPool pool, DBPoolParms pprm) throws Exception {
		this.pprm = pprm;
		Class.forName(pprm.dirver);
		// jdbc:mysql://125.76.246.47:23306/yhdata?characterEncoding=utf-8&autoReconnect=true&useSSL=true
		// jdbc:sqlserver://192.168.217.129:1433; DatabaseName=testdb
		// jdbc:oracle:thin:@192.168.142.128:1521:orcl
		Connection conn = DriverManager.getConnection(pprm.url, pprm.user, pprm.password);
		con = conn;
		UUID uuid = UUID.randomUUID();
		stat = ConStat.ready;
		key = uuid.toString();
		time = System.currentTimeMillis();
		this.pool = pool;
	}

	/**
	 * 打开（标记为使用状态）
	 * 
	 * @param owner
	 */
	public void open(Object owner) {
		try {
			if (!con.getAutoCommit())// 在事务处理中的连接 一定要回滚 否则可能造成数据库死锁
				con.rollback();
			stat = ConStat.inuse;
			time = System.currentTimeMillis();
			this.owner = owner;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	/**
	 * 关闭（标记为未使用状态）
	 */
	public void close() {
		// pool.closeCon(this);
		try {
			if (!con.getAutoCommit())// 在事务处理中的连接 一定要回滚 否则可能造成数据库死锁
				con.rollback();
			owner = null;
			stat = ConStat.ready;
			time = 0;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// lock.unlock();
		}
	}

	/**
	 * 断开从数据库的连接
	 */
	public void disConnect() {
		close();
		if (con != null)
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	//
	/**
	 * @return 判断是否可用
	 */
	public boolean isValid() {
		try {
			if (con == null)
				return false;
			if (con.isClosed())
				return false;
			if (getDBType() != DBType.oracle) // oracle 调用这个方法报错
				if (!con.isValid(0))
					return false;
			return true;
			// return ((con != null) && (!con.isClosed()) && con.isValid(0));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * @return 检测是否超时
	 */
	public boolean isUsesTimeout() {
		boolean rst = false;
		if ((stat == ConStat.inuse) && (!distimeout)) {// 在使用中的，且 没有禁止超时
			// 检查时间
			int scend = (int) ((System.currentTimeMillis() - time) / 1000);
			if (scend > pprm.timeout) {
				rst = true;
			}
		}
		return rst;
	}

	/**
	 * 执行sql语句
	 * 
	 * @param sqlstr
	 * @return
	 * @throws Exception
	 */
	public int execsql(String sqlstr) throws Exception {
		return execsql(sqlstr, false);
	}

	/**
	 * 执行sql语句
	 * 
	 * @param sqlstr
	 * @param dbginfo 是否打印日志
	 * @return
	 * @throws Exception
	 */
	public int execsql(String sqlstr, boolean dbginfo) throws Exception {
		// if (dbginfo)
		Logsw.debug("DBConnect 执行 SQL:" + sqlstr);
		int rst = 0;
		try {
			check2CMD();
			time = System.currentTimeMillis();
			lastcmd = sqlstr;
			if (pool.getIct() != null) {
				setCurUserName(pool.getIct().getCurUserName());
				setClientIP(pool.getIct().getClentIP());
			}
			setLastcmdstarttime(time);
			Statement stmt = null;
			try {
				stmt = this.con.createStatement();
				rst = stmt.executeUpdate(sqlstr);
			} finally {
				DBPools.safeCloseS_R(stmt, null);
			}
			if (dbginfo)
				if (DBPools.getCblog() != null) {
					long et = System.currentTimeMillis();
					DBPools.getCblog().writelog(this, "【执行SQL：" + sqlstr + "；耗时:" + (et - time) + "】");
				}
		} catch (Exception e) {
			Logsw.error("连接池执行SQL错误:" + sqlstr, e);
			// throw new Exception("执行SQL错误【" + sqlstr + "】错误信息【" +
			// e.getMessage() + "】");
		}
		return rst;
	}

	/**
	 * 执行多行sql语句
	 * 
	 * @param sqls
	 * @throws Exception
	 */
	public void execSqls(List<String> sqls) throws Exception {
		execSqls(sqls, true);
	}

	public void execSqls(List<String> sqls, Boolean dbginfo) throws Exception {
		// if (dbginfo)
		// Logsw.debug("DBConnect 批量执行 SQL:" + getsql(sqls));
		try {
			time = System.currentTimeMillis();
			lastcmd = "executeBatch:" + sqls.toString();
			if (pool.getIct() != null) {
				setCurUserName(pool.getIct().getCurUserName());
				setClientIP(pool.getIct().getClentIP());
			}
			setLastcmdstarttime(time);
			Statement smt = null;
			try {
				smt = con.createStatement();
				for (String sql : sqls) {
					smt.addBatch(sql);
				}
				smt.executeBatch();
			} finally {
				DBPools.safeCloseS_R(smt, null);
			}
			if (dbginfo)
				if (DBPools.getCblog() != null) {
					long et = System.currentTimeMillis();
					DBPools.getCblog().writelog(this, "【执行SQL：" + getsql(sqls) + "；耗时:" + (et - time) + "】");
				}
		} catch (Exception e) {
			throw e;
		}
	}

	private String getsql(List<String> sqls) {
		String rst = "";
		for (String sql : sqls) {
			rst = rst + sql + "\n\r";
		}
		return rst;
	}

	/**
	 * 执行sql语句 返回分页的 json 字符串
	 * 
	 * @param psql
	 * @param page
	 * @param pagesize
	 * @return
	 * @throws Exception
	 */
	public String opensql2json(PraperedSql psql, int page, int pagesize) throws Exception {
		String yxsqlstr = psql.getSqlstr();
		psql.setSqlstr("select count(*) ct from (" + psql.getSqlstr() + ") tb");
		int size = Integer.valueOf(openSql2List(psql).get(0).get("ct").toString());// //有问题
		String psqlstr = null;
		if (page < 1)
			page = 1;
		if (getDBType() == DBType.mysql)
			psqlstr = yxsqlstr + " limit " + (page - 1) * pagesize + "," + pagesize;
		if (getDBType() == DBType.oracle)
			psqlstr = "SELECT * FROM( SELECT A.*,ROWNUM  num FROM (" + yxsqlstr + " )A  WHERE ROWNUM<=" + ((page - 1) * pagesize + pagesize) + ")WHERE num> "
					+ (page - 1) * pagesize;
		psql.setSqlstr(psqlstr);
		String js = opensql2json(psql, true);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JsonFactory jf = new JsonFactory();
		JsonGenerator jg = jf.createJsonGenerator(baos);
		jg.writeStartObject();
		jg.writeNumberField("total", size);
		jg.writeFieldName("rows");
		jg.writeRawValue(js);
		jg.writeEndObject();
		jg.close();
		return baos.toString("utf-8");
	}

	private int getsqlrstsize(String sqlstr) throws Exception {
		String nsql = "select count(*) ct from (" + sqlstr + ") tb";
		int size = Integer.valueOf(openSql2List(nsql).get(0).get("ct").toString());
		return size;
	}

	/**
	 * @param sqlstr
	 * @param page
	 * 当前页 -1表示不分页
	 * @param pagesize
	 * 每页行数
	 * @return
	 */
	private String getPagedSqlstr(String sqlstr, int page, int pagesize) {
		if (page == -1)
			return sqlstr;
		String psqlstr = sqlstr;
		if (page < 1)
			page = 1;
		if (getDBType() == DBType.mysql)
			psqlstr = sqlstr + " limit " + (page - 1) * pagesize + "," + pagesize;
		if (getDBType() == DBType.oracle)
			psqlstr = "SELECT * FROM( SELECT A.*,ROWNUM  num FROM (" + sqlstr + " )A  WHERE ROWNUM<=" + ((page - 1) * pagesize + pagesize) + ")WHERE num> "
					+ (page - 1) * pagesize;
		return psqlstr;
	}

	/**
	 * 执行sql语句 返回分页的 json 字符串
	 * 
	 * @param sqlstr
	 * @param page
	 * @param pagesize
	 * @return
	 * @throws Exception
	 */
	public String opensql2json(String sqlstr, int page, int pagesize) throws Exception {
		int size = getsqlrstsize(sqlstr);
		String psqlstr = getPagedSqlstr(sqlstr, page, pagesize);
		String js = opensql2json(psqlstr);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JsonFactory jf = new JsonFactory();
		JsonGenerator jg = jf.createJsonGenerator(baos);
		jg.writeStartObject();
		jg.writeNumberField("total", size);
		jg.writeFieldName("rows");
		jg.writeRawValue(js);
		jg.writeEndObject();
		jg.close();
		return baos.toString("utf-8");
	}

	/**
	 * @param sqlstr
	 * @param page
	 * @param pagesize
	 * @return 分页查询
	 * @throws Exception
	 */
	public JSONObject opensql2json_o(String sqlstr, int page, int pagesize) throws Exception {
		return opensql2json_o(sqlstr, page, pagesize, false);
	}

	/**
	 * @param sqlstr
	 * @param page
	 * @param pagesize
	 * @param withMetaData 是否包含元数据
	 * @return 分页查询
	 * @throws Exception
	 */
	public JSONObject opensql2json_o(String sqlstr, int page, int pagesize, boolean withMetaData) throws Exception {
		JSONObject rst = new JSONObject();
		try {
			check2CMD();
			time = System.currentTimeMillis();
			lastcmd = sqlstr;
			if (pool.getIct() != null) {
				setCurUserName(pool.getIct().getCurUserName());
				setClientIP(pool.getIct().getClentIP());
			}
			setLastcmdstarttime(time);
			int size = getsqlrstsize(sqlstr);
			String psqlstr = getPagedSqlstr(sqlstr, page, pagesize);
			// System.out.println("sqlstr:" + psqlstr);
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = this.con.createStatement();
				rs = stmt.executeQuery(psqlstr);
				if (DBPools.getCblog() != null) {
					long et = System.currentTimeMillis();
					DBPools.getCblog().writelog(this, "【执行SQL：" + psqlstr + "；耗时:" + (et - time) + "】");
				}

				JSONArray rows = CJSON.Dataset2JSON_O(rs);
				rst.put("page", page);
				rst.put("total", size);
				rst.put("rows", rows);
				if (withMetaData) {
					rst.put("metadata", CJSON.getMetaData_O(rs));
				}

			} finally {
				DBPools.safeCloseS_R(stmt, rs);
			}

		} catch (Exception e) {
			Logsw.error("连接池执行SQL错误:" + sqlstr, e);
		}
		return rst;
	}

	/**
	 * 执行sql语句 返回 json 数组
	 * 
	 * @param sqlstr
	 * @return
	 * @throws Exception
	 */
	public JSONArray opensql2json_o(String sqlstr) throws Exception {
		Logsw.debug("执行查询:" + sqlstr);
		JSONArray rst = null;
		try {
			check2CMD();
			time = System.currentTimeMillis();
			lastcmd = sqlstr;
			if (pool.getIct() != null) {
				setCurUserName(pool.getIct().getCurUserName());
				setClientIP(pool.getIct().getClentIP());
			}
			setLastcmdstarttime(time);
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = this.con.createStatement();
				rs = stmt.executeQuery(sqlstr);
				if (DBPools.getCblog() != null) {
					long et = System.currentTimeMillis();
					DBPools.getCblog().writelog(this, "【执行SQL：" + sqlstr + "；耗时:" + (et - time) + "】");
				}
				rst = CJSON.Dataset2JSON_O(rs);
			} finally {
				DBPools.safeCloseS_R(stmt, rs);
			}
		} catch (Exception e) {
			Logsw.error("连接池执行SQL错误:" + sqlstr, e);
		}
		return rst;
	}

	/**
	 * 执行sql语句 返回 json 数组
	 * 
	 * @param psql
	 * @return
	 * @throws Exception
	 */
	public JSONArray opensql2json_o(PraperedSql psql) throws Exception {
		Logsw.debug("执行查询:" + psql.getSqlstr());
		JSONArray rst = null;
		try {
			check2CMD();
			time = System.currentTimeMillis();
			lastcmd = psql.getSqlstr();
			if (pool.getIct() != null) {
				setCurUserName(pool.getIct().getCurUserName());
				setClientIP(pool.getIct().getClentIP());
			}
			setLastcmdstarttime(time);
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				pstmt = this.con.prepareStatement(psql.getSqlstr());
				for (int i = 0; i < psql.getParms().size(); i++) {
					PraperedValue pv = psql.getParms().get(i);
					CPoolSQLUtil.setSqlPValue(pstmt, i + 1, pv);
				}
				rs = pstmt.executeQuery();
				rst = CJSON.Dataset2JSON_O(rs);
			} finally {
				DBPools.safeCloseS_R(pstmt, rs);
			}
		} catch (Exception e) {
			Logsw.error("连接池执行SQL错误:" + psql.getSqlstr(), e);
		}
		return rst;
	}

	/**
	 * 执行sql语句 返回 json 字符串
	 * 
	 * @param psql
	 * @param dbginfo
	 * @return
	 * @throws Exception
	 */
	public String opensql2json(PraperedSql psql, boolean dbginfo) throws Exception {
		// if (dbginfo)
		Logsw.debug("执行查询:" + psql.getSqlstr());
		String rst = "[]";
		try {
			check2CMD();
			time = System.currentTimeMillis();
			lastcmd = psql.getSqlstr();
			if (pool.getIct() != null) {
				setCurUserName(pool.getIct().getCurUserName());
				setClientIP(pool.getIct().getClentIP());
			}
			setLastcmdstarttime(time);
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				pstmt = this.con.prepareStatement(psql.getSqlstr());
				for (int i = 0; i < psql.getParms().size(); i++) {
					PraperedValue pv = psql.getParms().get(i);
					CPoolSQLUtil.setSqlPValue(pstmt, i + 1, pv);
				}
				rs = pstmt.executeQuery();
				if (dbginfo)
					if (DBPools.getCblog() != null) {
						long et = System.currentTimeMillis();
						DBPools.getCblog().writelog(this, "【执行SQL：" + psql.getSqlstr() + "；耗时:" + (et - time) + "】");
					}
				rst = CJSON.Dataset2JSON(rs);
			} finally {
				DBPools.safeCloseS_R(pstmt, rs);
			}
		} catch (Exception e) {
			Logsw.error("连接池执行SQL错误:" + psql.getSqlstr(), e);
		}
		return rst;
	}

	/**
	 * 执行sql语句 返回 json 字符串
	 * 
	 * @param sqlstr
	 * @param dbginfo
	 * @return
	 * @throws Exception
	 */
	public String opensql2json(String sqlstr, boolean dbginfo) throws Exception {
		// if (dbginfo)
		// Logsw.debug("执行查询:" + sqlstr);
		String rst = "[]";
		try {
			check2CMD();
			if (DBPools.getCblog() != null) {
				DBPools.getCblog().writelog(this, "【计划执行SQL：" + sqlstr + "】");
			}
			time = System.currentTimeMillis();
			lastcmd = sqlstr;
			if (pool.getIct() != null) {
				setCurUserName(pool.getIct().getCurUserName());
				setClientIP(pool.getIct().getClentIP());
			}
			setLastcmdstarttime(time);
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = this.con.createStatement();
				rs = stmt.executeQuery(sqlstr);
				if (dbginfo)
					if (DBPools.getCblog() != null) {
						long et = System.currentTimeMillis();
						DBPools.getCblog().writelog(this, "【执行SQL耗时:" + (et - time) + "】");
					}
				rst = CJSON.Dataset2JSON(rs);
			} finally {
				DBPools.safeCloseS_R(stmt, rs);
			}
		} catch (Exception e) {
			Logsw.error("连接池执行SQL错误:" + sqlstr, e);
		}
		return rst;
	}

	/**
	 * 执行sql语句 返回 json 字符串
	 * 
	 * @param sqlstr
	 * @return
	 * @throws Exception
	 */
	public String opensql2json(String sqlstr) throws Exception {
		return opensql2json(sqlstr, true);
	}

	/**
	 * 执行sql语句 返回 json 字符串
	 * 
	 * @param psql
	 * @return
	 * @throws Exception
	 */
	public String openrowsql2json(PraperedSql psql) throws Exception {
		JSONArray jos = opensql2json_o(psql);
		if (jos.size() > 0) {
			return jos.get(0).toString();
		} else {
			return "{}";
		}
	}

	public String openrowsql2json(String sqlstr) throws Exception {
		// Logsw.debug("执行查询:" + sqlstr);
		String rst = "{}";
		try {
			check2CMD();
			time = System.currentTimeMillis();
			lastcmd = sqlstr;
			if (pool.getIct() != null) {
				setCurUserName(pool.getIct().getCurUserName());
				setClientIP(pool.getIct().getClentIP());
			}
			setLastcmdstarttime(time);
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = this.con.createStatement();
				rs = stmt.executeQuery(sqlstr);
				ResultSetMetaData rsmd = rs.getMetaData(); // 得到结果集的定义结构
				if (DBPools.getCblog() != null) {
					long et = System.currentTimeMillis();
					DBPools.getCblog().writelog(this, "【执行SQL：" + sqlstr + "；耗时:" + (et - time) + "】");
				}
				int columnCount = rsmd.getColumnCount();
				if (rs.next())
					rst = CJSON.DatasetRow2JSON(rs, rsmd, columnCount);
			} finally {
				DBPools.safeCloseS_R(stmt, rs);
			}
		} catch (Exception e) {
			Logsw.error("连接池执行SQL错误:" + sqlstr, e);
		}
		return rst;
	}

	/**
	 * 执行sql语句 返回树型 json 数组
	 * 
	 * @param sqlstr
	 * @param idfd 主键
	 * @param pidfd 父字段
	 * @param async 是否异步
	 * @return
	 * @throws Exception
	 */
	public JSONArray opensql2jsontree_o(String sqlstr, String idfd, String pidfd, boolean async) throws Exception {
		// Logsw.debug("执行查询:" + sqlstr);
		JSONArray rst = null;
		try {
			check2CMD();
			time = System.currentTimeMillis();
			lastcmd = sqlstr;
			if (pool.getIct() != null) {
				setCurUserName(pool.getIct().getCurUserName());
				setClientIP(pool.getIct().getClentIP());
			}
			setLastcmdstarttime(time);
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = this.con.createStatement();
				rs = stmt.executeQuery(sqlstr);
				if (DBPools.getCblog() != null) {
					long et = System.currentTimeMillis();
					DBPools.getCblog().writelog(this, "【执行SQL：" + sqlstr + "；耗时:" + (et - time) + "】");
				}
				rst = CJSONTree.Dataset2JSON(rs, idfd, pidfd, async);
			} finally {
				DBPools.safeCloseS_R(stmt, rs);
			}
		} catch (Exception e) {
			Logsw.error("连接池执行SQL错误:" + sqlstr, e);
		}
		return rst;
	}

	/**
	 * 执行sql语句 返回树型 json 字符串
	 * 
	 * @param sqlstr
	 * @param idfd 主键
	 * @param pidfd 父字段
	 * @param async
	 * @return
	 * @throws Exception
	 */
	public String opensql2jsontree(String sqlstr, String idfd, String pidfd, boolean async) throws Exception {
		// Logsw.debug("执行查询:" + sqlstr);
		String rst = null;
		try {
			check2CMD();
			time = System.currentTimeMillis();
			lastcmd = sqlstr;
			if (pool.getIct() != null) {
				setCurUserName(pool.getIct().getCurUserName());
				setClientIP(pool.getIct().getClentIP());
			}
			setLastcmdstarttime(time);
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = this.con.createStatement();
				rs = stmt.executeQuery(sqlstr);
				if (DBPools.getCblog() != null) {
					long et = System.currentTimeMillis();
					DBPools.getCblog().writelog(this, "【执行SQL：" + sqlstr + "；耗时:" + (et - time) + "】");
				}
				rst = CJSONTree.Dataset2JSONTree(rs, idfd, pidfd, async);
			} finally {
				DBPools.safeCloseS_R(stmt, rs);
			}
		} catch (Exception e) {
			Logsw.error("连接池执行SQL错误:" + sqlstr, e);
		}
		return rst;
	}

	/**
	 * 执行SQL语句，返回列表数据
	 * 
	 * @param psql
	 * @return
	 * @throws Exception
	 */
	public List<HashMap<String, String>> openSql2List(PraperedSql psql) throws Exception {
		// Logsw.debug("执行查询:" + psql.getSqlstr());
		List<HashMap<String, String>> records = null;
		try {
			check2CMD();
			time = System.currentTimeMillis();
			lastcmd = psql.getSqlstr();
			if (pool.getIct() != null) {
				setCurUserName(pool.getIct().getCurUserName());
				setClientIP(pool.getIct().getClentIP());
			}
			setLastcmdstarttime(time);
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try {
				stmt = this.con.prepareStatement(psql.getSqlstr());
				for (int i = 0; i < psql.getParms().size(); i++) {
					PraperedValue pv = psql.getParms().get(i);
					CPoolSQLUtil.setSqlPValue(stmt, i + 1, pv);
				}
				rs = stmt.executeQuery();
				if (DBPools.getCblog() != null) {
					long et = System.currentTimeMillis();
					DBPools.getCblog().writelog(this, "【执行SQL：" + psql.getSqlstr() + "；耗时:" + (et - time) + "】");
				}
				records = CJSONTree.Dataset2List(getDBType(), rs);
			} finally {
				DBPools.safeCloseS_R(stmt, rs);
			}
		} catch (Exception e) {
			Logsw.error("连接池执行SQL错误:" + psql.getSqlstr(), e);
		}
		return records;
	}

	/**
	 * 执行SQL语句，返回列表数据
	 * 
	 * @param sqlstr
	 * @return
	 * @throws Exception
	 */
	public List<HashMap<String, String>> openSql2List(String sqlstr) throws Exception {
		// Logsw.debug("执行查询:" + sqlstr);
		List<HashMap<String, String>> records = null;
		try {
			check2CMD();
			time = System.currentTimeMillis();
			lastcmd = sqlstr;
			if (pool.getIct() != null) {
				setCurUserName(pool.getIct().getCurUserName());
				setClientIP(pool.getIct().getClentIP());
			}
			setLastcmdstarttime(time);
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = this.con.createStatement();
				rs = stmt.executeQuery(sqlstr);
				if (DBPools.getCblog() != null) {
					long et = System.currentTimeMillis();
					DBPools.getCblog().writelog(this, "【执行SQL：" + sqlstr + "；耗时:" + (et - time) + "】");
				}
				records = CJSONTree.Dataset2List(getDBType(), rs);
			} finally {
				DBPools.safeCloseS_R(stmt, rs);
			}
		} catch (Exception e) {
			Logsw.error("连接池执行SQL错误:" + sqlstr, e);
		}
		return records;
	}

	/**
	 * 执行SQL语句，返回列表数据
	 * 
	 * @param sqlstr
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public List<CJPABase> openSql2List(String sqlstr, Class<?> cls) throws Exception {
		if (!CJPABase.class.isAssignableFrom(cls)) {
			throw new Exception(cls.getName() + "必须是 com.corsair.server.cjpa.CJPA的子类");
		}
		CJPALineData<CJPABase> rst = new CJPALineData<CJPABase>(cls);
		rst.findDataBySQL(sqlstr, true, true);
		return rst;
	}

	/**
	 * 获取数据集描述
	 * 
	 * @param sqlstr
	 * @return
	 * @throws Exception
	 */
	public CResultSetMetaData<CResultSetMetaDataItem> getsqlMetadata(String sqlstr) throws Exception {
		// Logsw.debug("执行查询:" + sqlstr);
		CResultSetMetaData<CResultSetMetaDataItem> rst = new CResultSetMetaData<CResultSetMetaDataItem>();
		try {
			check2CMD();
			time = System.currentTimeMillis();
			lastcmd = sqlstr;
			if (pool.getIct() != null) {
				setCurUserName(pool.getIct().getCurUserName());
				setClientIP(pool.getIct().getClentIP());
			}
			setLastcmdstarttime(time);
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = this.con.createStatement();
				rs = stmt.executeQuery(sqlstr);
				if (DBPools.getCblog() != null) {
					long et = System.currentTimeMillis();
					DBPools.getCblog().writelog(this, "【执行SQL：" + sqlstr + "；耗时:" + (et - time) + "】");
				}
				ResultSetMetaData rsmt = rs.getMetaData();
				for (int i = 1; i <= rsmt.getColumnCount(); i++) {
					rst.add(new CResultSetMetaDataItem(rsmt, i));
				}
			} finally {
				DBPools.safeCloseS_R(stmt, rs);
			}
		} catch (Exception e) {
			Logsw.error("连接池执行SQL错误:" + sqlstr, e);
		}
		return rst;
	}

	public Object getOwner() {
		return owner;
	}

	public String getLastcmd() {
		return lastcmd;
	}

	public void setLastcmd(String lastcmd) {
		this.lastcmd = lastcmd;
	}

	public long getLastcmdstarttime() {
		return lastcmdstarttime;
	}

	public void setLastcmdstarttime(long lastcmdstarttime) {
		this.lastcmdstarttime = lastcmdstarttime;
	}

	public String getCurUserName() {
		return curUserName;
	}

	public void setCurUserName(String curUserName) {
		this.curUserName = curUserName;
	}

	public String getClientIP() {
		return clientIP;
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	public boolean isDistimeout() {
		return distimeout;
	}

	public void setDistimeout(boolean distimeout) {
		this.distimeout = distimeout;
	}

}
