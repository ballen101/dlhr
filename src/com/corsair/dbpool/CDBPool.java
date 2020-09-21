package com.corsair.dbpool;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.cjpa.CJPABase;
import com.corsair.dbpool.CDBConnection.ConStat;
import com.corsair.dbpool.CDBConnection.DBType;
import com.corsair.dbpool.util.CResultSetMetaData;
import com.corsair.dbpool.util.CResultSetMetaDataItem;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.PraperedSql;
import com.corsair.dbpool.util.PraperedValue;

public class CDBPool {

	private DBType dbtype = null;

	public DBPoolParms pprm;
	public List<CDBConnection> sessions = new ArrayList<CDBConnection>();

	private Lock lock = new ReentrantLock();
	private int reConnectTimes = 1;// 重连次数;
	private long reConnectSptime = 1000;// 重连间隔；

	private IDBContext ict;

	/**
	 * @return 获取连接池信息
	 */
	public synchronized JSONArray getinfo() {
		JSONArray rst = new JSONArray();
		for (CDBConnection session : sessions) {
			JSONObject s = new JSONObject();
			s.put("key", session.getKey());
			s.put("stat", session.getStat());
			s.put("time", session.getTime());
			s.put("isValid", !session.isValid());
			s.put("inbs", session.isInTrans());
			s.put("lastcmd", session.getLastcmd());
			s.put("cmdsds", (System.currentTimeMillis() - session.getLastcmdstarttime()) / 1000);
			if (session.getOwner() == null)
				s.put("owner", null);
			else
				s.put("owner", session.getOwner().toString());
			s.put("username", session.getCurUserName());
			s.put("clientip", session.getClientIP());
			rst.add(s);
		}
		return rst;
	}

	/**
	 * 构建连接池
	 * 
	 * @param pprm
	 */
	public CDBPool(DBPoolParms pprm) {
		this.pprm = pprm;
		try {
			// 数据库链接用到再初始化
			// for (int i = 0; i < pprm.minsession; i++) {
			// RstConMsg rcm = newconnecttodb();
			// if (rcm.getCon() == null)
			// throw new Exception(rcm.getErrmsg());
			// }
		} catch (Exception e) {
			Logsw.error("为连接池【" + pprm.name + "】创建数据库连接错误");
			e.printStackTrace();
		}
		checkthread cktd = new checkthread();
		cktd.start();
	}

	/**
	 * 执行sql语句
	 * 
	 * @param sqlstr
	 * @param dbginfo
	 * 是否打印日志
	 * @return 变更数据条数
	 * @throws Exception
	 */
	public int execsql(String sqlstr, boolean dbginfo) throws Exception {
		CDBConnection con = getCon(this);
		try {
			return con.execsql(sqlstr, dbginfo);
		} finally {
			con.close();
		}
	}

	/**
	 * 执行sql语句
	 * 
	 * @param sqlstr
	 * @return 变更数据条数
	 * @throws Exception
	 */
	public int execsql(String sqlstr) throws Exception {
		return execsql(sqlstr, false);
	}

	/**
	 * 指定连接执行一批sql语句
	 * 
	 * @param con
	 * @param sqls
	 * @throws Exception
	 */
	public void execSqls(CDBConnection con, List<String> sqls) throws Exception {
		con.execSqls(sqls);
	}

	/**
	 * 执行一批sql语句
	 * 
	 * @param con
	 * @param sqls
	 * @throws Exception
	 */
	public void execSqls(List<String> sqls) throws Exception {
		CDBConnection con = getCon(this);
		con.startTrans();
		try {
			execSqls(con, sqls);
			con.submit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	/**
	 * 执行查询sql语句，返回JSON数组
	 * 
	 * @param sqlstr
	 * @return
	 * @throws Exception
	 */
	public JSONArray opensql2json_O(String sqlstr) throws Exception {
		CDBConnection con = getCon(this);
		try {
			return con.opensql2json_o(sqlstr);
		} finally {
			con.close();
		}
	}

	/**
	 * 执行查询sql语句，返回JSON数组
	 * 
	 * @param psql
	 * @return
	 * @throws Exception
	 */
	public JSONArray opensql2json_O(PraperedSql psql) throws Exception {
		CDBConnection con = getCon(this);
		try {
			return con.opensql2json_o(psql);
		} finally {
			con.close();
		}
	}

	/**
	 * 执行查询sql语句，返回JSON对象
	 * 
	 * @param sqlstr
	 * @param page
	 * 页码
	 * @param pagesize
	 * 每页数量
	 * @return {page:page,total:total,rows:rows} total:总行数 rows:rows 数据的json数组
	 * @throws Exception
	 */
	public JSONObject opensql2json_O(String sqlstr, int page, int pagesize) throws Exception {
		CDBConnection con = getCon(this);
		try {
			return con.opensql2json_o(sqlstr, page, pagesize);
		} finally {
			con.close();
		}
	}

	/**
	 * 执行查询sql语句，返回JSON对象
	 * 
	 * @param sqlstr
	 * @param page
	 * 页码
	 * @param pagesize
	 * 每页数量
	 * @return {page:page,total:total,rows:rows} total:总行数 rows:rows 数据的json数组
	 * @throws Exception
	 */
	public JSONObject opensql2json_O(String sqlstr, int page, int pagesize, boolean withMetaData) throws Exception {
		CDBConnection con = getCon(this);
		try {
			return con.opensql2json_o(sqlstr, page, pagesize, withMetaData);
		} finally {
			con.close();
		}
	}

	/**
	 * 执行查询sql语句，返回JSON数组
	 * 
	 * @param sqlstr
	 * @param dbginfo
	 * 是否打印日志
	 * @return
	 * @throws Exception
	 */
	public String opensql2json(String sqlstr, boolean dbginfo) throws Exception {
		CDBConnection con = getCon(this);
		try {
			return con.opensql2json(sqlstr, dbginfo);
		} finally {
			con.close();
		}
	}

	/**
	 * 执行查询sql语句，返回JSON数组字符串
	 * 
	 * @param sqlstr
	 * @return
	 * @throws Exception
	 */
	public String opensql2json(String sqlstr) throws Exception {
		CDBConnection con = getCon(this);
		try {
			return con.opensql2json(sqlstr);
		} finally {
			con.close();
		}
	}

	public String openrowsql2json(String sqlstr) throws Exception {
		CDBConnection con = getCon(this);
		try {
			return con.openrowsql2json(sqlstr);
		} finally {
			con.close();
		}
	}

	/**
	 * 执行查询sql语句，返回JSON数组第一行字符串
	 * 
	 * @param sqlstr
	 * @return
	 * @throws Exception
	 */
	public String openrowsql2json(PraperedSql psql) throws Exception {
		CDBConnection con = getCon(this);
		try {
			return con.openrowsql2json(psql);
		} finally {
			con.close();
		}
	}

	/**
	 * 执行查询sql语句，返回JSON数组字符串 分页
	 * 
	 * @param sqlstr
	 * @return
	 * @throws Exception
	 */
	public String opensql2json(PraperedSql psql, int page, int pagesize) throws Exception {
		CDBConnection con = getCon(this);
		try {
			return con.opensql2json(psql, page, pagesize);
		} finally {
			con.close();
		}
	}

	/**
	 * 执行查询，返回列表 参数如下 PraperedSql psql = new PraperedSql();
	 * psql.setSqlstr("select * from table where id=?"); psql.getParms().add(new
	 * PraperedValue(Types.INTEGER, id));
	 * 
	 * @param psql
	 * @return
	 * @throws Exception
	 */
	public List<HashMap<String, String>> openSql2List(PraperedSql psql) throws Exception {
		CDBConnection con = getCon(this);
		try {
			return con.openSql2List(psql);
		} finally {
			con.close();
		}
	}

	/**
	 * 执行查询，返回JOSN字符串 参数如下 PraperedSql psql = new PraperedSql();
	 * psql.setSqlstr("select * from table where id=?"); psql.getParms().add(new
	 * PraperedValue(Types.INTEGER, id));
	 * 
	 * @param psql
	 * @return
	 * @throws Exception
	 */
	public String opensql2json(PraperedSql psql) throws Exception {
		return opensql2json(psql, true);
	}

	/**
	 * 执行查询，返回JOSN字符串 参数如下 PraperedSql psql = new PraperedSql();
	 * psql.setSqlstr("select * from table where id=?"); psql.getParms().add(new
	 * PraperedValue(Types.INTEGER, id));
	 * 
	 * @param psql
	 * @param dbginfo
	 * 是否显示日志
	 * @return
	 * @throws Exception
	 */
	public String opensql2json(PraperedSql psql, boolean dbginfo) throws Exception {
		CDBConnection con = getCon(this);
		try {
			return con.opensql2json(psql, dbginfo);
		} finally {
			con.close();
		}
	}

	/**
	 * 分页查询，返回JSON字符串
	 * 
	 * @param sqlstr
	 * @param page
	 * @param pagesize
	 * @return
	 * @throws Exception
	 */
	public String opensql2json(String sqlstr, int page, int pagesize) throws Exception {
		CDBConnection con = getCon(this);
		try {
			return con.opensql2json(sqlstr, page, pagesize);
		} finally {
			con.close();
		}
	}

	/**
	 * 执行查询树结构JSON数组
	 * 
	 * @param sqlstr
	 * @param idfd
	 * id字段名
	 * @param pidfd
	 * 父id字段名
	 * @param async
	 * 是否异步 false 将一次生成整个树结构，true：只生成第一级别
	 * @return [{fd:v,children:[{fd:v,children:[]},{}]}]
	 * @throws Exception
	 */
	public JSONArray opensql2jsontree_o(String sqlstr, String idfd, String pidfd, boolean async) throws Exception {
		CDBConnection con = getCon(this);
		try {
			return con.opensql2jsontree_o(sqlstr, idfd, pidfd, async);
		} finally {
			con.close();
		}
	}

	/**
	 * 执行查询树结构JSON数组字符串
	 * 
	 * @param sqlstr
	 * @param idfd
	 * id字段名
	 * @param pidfd
	 * 父id字段名
	 * @param async
	 * 是否异步 false 将一次生成整个树结构，true：只生成第一级别
	 * @return [{fd:v,children:[{fd:v,children:[]},{}]}]
	 * @throws Exception
	 */
	public String opensql2jsontree(String sqlstr, String idfd, String pidfd, boolean async) throws Exception {
		CDBConnection con = getCon(this);
		try {
			return con.opensql2jsontree(sqlstr, idfd, pidfd, async);
		} finally {
			con.close();
		}

	}

	/**
	 * 执行查询返回列表
	 * 
	 * @param sqlstr
	 * @return
	 * @throws Exception
	 */
	public List<HashMap<String, String>> openSql2List(String sqlstr) throws Exception {
		CDBConnection con = getCon(this);
		try {
			return con.openSql2List(sqlstr);
		} finally {
			con.close();
		}

	}

	/**
	 * 执行查询返回JPA对象列表（包含自关联数据）
	 * 
	 * @param sqlstr
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public List<CJPABase> openSql2List(String sqlstr, Class<?> cls) throws Exception {
		CDBConnection con = getCon(this);
		try {
			return con.openSql2List(sqlstr, cls);
		} finally {
			con.close();
		}

	}

	public CResultSetMetaData<CResultSetMetaDataItem> getsqlMetadata(String sqlstr) throws Exception {
		CDBConnection con = getCon(this);
		try {
			return con.getsqlMetadata(sqlstr);
		} finally {
			con.close();
		}
	}

	class checkthread extends Thread {
		public checkthread() {

		}

		public void run() {
			while (true) {
				// if (CDBPool.this == null)
				// break;
				CDBPool.this.checklinks();
				try {
					Thread.sleep(1000 * CDBPool.this.pprm.checkcontime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 从连接池中取出一个链接

	private synchronized RstConMsg dogetcon(Object owner) {
		for (CDBConnection conn : sessions) {
			if (conn.getStat() == ConStat.ready) {
				conn.open(owner);
				return new RstConMsg(conn);
			}
		}
		RstConMsg rcm = newconnecttodb();// 如果没有找到可用连接 就生成一个新的
		if (rcm.getCon() != null) {
			rcm.getCon().open(owner);
		}
		return rcm;
	}

	// 从连接池中取出一个链接
	/**
	 * 从连接池获取一个连接，使用完毕需要close 或 提交 或回滚，否则连接将不能使用，直到超时
	 * 
	 * @param owner
	 * 创建连接的对象，用来DEBUG
	 * @return 返回一个独用数据库连接
	 * @throws Exception
	 */
	public synchronized CDBConnection getCon(Object owner) throws Exception {
		lock.lock();
		try {
			RstConMsg rcm = dogetcon(owner);
			if (rcm.getCon() != null)
				return rcm.getCon();
			else
				throw new Exception(rcm.getErrmsg());
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 关闭一个链接并放回连接池 并不断开数据库连接
	 * 
	 * @param con
	 */
//	public synchronized void closeCon1(CDBConnection con) {
//		con.close();
//	}

	/**
	 * 返回数据库类型 DBType.sqlserver, DBType.hsql, DBType.mysql, DBType.oracle,
	 * DBType.unknow
	 * 
	 * @return
	 */
	public DBType getDbtype() {
		if (dbtype == null) {
			if (sessions.size() == 0) {
				newconnecttodb();
			}
			System.out.println("sessions:" + sessions.size());
			dbtype = sessions.get(0).getDBType();
		}
		return dbtype;
	}

	// 创建一个新的链接放入 连接池
	private synchronized RstConMsg newconnecttodb() {
		if (sessions.size() >= pprm.maxsession) {
			return new RstConMsg(pprm.name + "数据库连接超过上限:" + pprm.maxsession);
		}
		try {
			CDBConnection dbc = new CDBConnection(this, pprm);
			if ((dbc == null) || (!dbc.isValid())) {
				return new RstConMsg(pprm.name + "无法连接数据库！");
			}
			sessions.add(dbc);
			return new RstConMsg(dbc);
		} catch (Exception e) {
			e.printStackTrace();
			return new RstConMsg(pprm.name + "无法连接数据库：" + e.getMessage());
		}

	}

	private void checklinks() {
		lock.lock();
		try {
			Iterator<CDBConnection> iter = sessions.iterator();
			while (iter.hasNext()) {
				CDBConnection session = iter.next();
				if (!session.isValid()) {
					Logsw.error("异常中断的数据库连接，被干掉:" + session.getKey() + ":" + getObjectStr(session.getOwner()));
					session.close();
					iter.remove();
					session = null;
					continue;
				}
				if (session.isUsesTimeout()) {// 使用超时 关闭
					Logsw.error("使用中的数据库连接超时，被连接池回收:" + session.getKey() + ":" + getObjectStr(session.getOwner()) + ":" + session.getLastcmd() + ":" + session.getCurUserName());
					session.disConnect();
					iter.remove();
					session = null;
					continue;
				}
				if ((session != null) && (!session.isInUse()) && (sessions.size() > pprm.minsession)) {// 检查是否连接数是否超初始化数量
					// 超过就干掉
					Logsw.error("未使用的链接超过初始化数量，被干掉:" + session.getKey() + ":" + getObjectStr(session.getOwner()));
					session.disConnect();
					iter.remove();
					session = null;
					continue;
				}
			}
		} finally {
			lock.unlock();
		}
	}

	private String getObjectStr(Object o) {
		if (o == null)
			return null;
		if ((o.getClass().getSimpleName().equalsIgnoreCase("String"))) {
			return (String) o;
		} else
			return o.getClass().getName();
	}

	public IDBContext getIct() {
		return ict;
	}

	public void setIct(IDBContext ict) {
		this.ict = ict;
	}

}
