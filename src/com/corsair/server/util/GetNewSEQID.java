package com.corsair.server.util;

import java.util.HashMap;
import java.util.List;

import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.CDBPool;
import com.corsair.dbpool.CDBConnection.DBType;
import com.corsair.dbpool.DBPools;

public class GetNewSEQID {
	public static String dogetnewid1(String tbname, int num) throws Exception {
		String a[] = tbname.split("\\.");
		String tablename = null;
		String poolname = null;
		if (a.length == 1) {
			tablename = a[0];
		}
		if (a.length >= 2) {
			poolname = a[0];
			tablename = a[1];
		}
		if (poolname != null)
			poolname = poolname.toLowerCase();
		CDBConnection con = DBPools.poolByName(poolname).getCon(GetNewSEQID.class.getName());
		con.startTrans();
		try {
			String idx = doCreateNewID(con, tablename, num);
			con.submit();
			return idx;
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	public static String doCreateNewID(CDBConnection con, String tbname, int num) throws Exception {
		String tablename = getTrueTablename(tbname);
		String sqlstr = "select * from seq_all where tbname='" + tablename + "' for update";// 试着锁定行
		List<HashMap<String, String>> rows = con.openSql2List(sqlstr);
		if (rows.size() == 0) { // 第一次插入行
			// con.openSql2List("select * from seq_all for update");// 锁表 貌似不用锁表
			long idx = num + 1;
			if (!tbname.equalsIgnoreCase("seq_all")) {
				String sid = doCreateNewID(con, "seq_all", 1);
				sqlstr = "insert into seq_all(sid,tbname,idx) values(" + sid + ",'" + tablename + "'," + idx + ")";
			} else {
				sqlstr = "insert into seq_all(sid,tbname,idx) values(0,'seq_all'," + idx + ")";
			}
			con.execsql(sqlstr);
			return "1";
		} else if (rows.size() == 1) {// 已经有行
			HashMap<String, String> row = rows.get(0);
			long idx = Long.valueOf(row.get("idx"));
			long newidx = idx + num;
			sqlstr = "update seq_all set idx=" + newidx + " where tbname='" + tablename + "'";
			con.execsql(sqlstr);
			return String.valueOf(idx);
		} else if (rows.size() > 1) {
			throw new Exception("序列表【" + tablename + "】发现多行数据！");
		} else
			throw new Exception("序列表【" + tablename + "】返回负数行，数据库连接错误?");
	}

	private static String getTrueTablename(String tbname) {
		String a[] = tbname.split("\\.");
		String tablename = (a.length == 2) ? a[1] : a[0];

		tablename = tablename.toLowerCase();
		if (tablename.startsWith("`"))
			tablename = tablename.substring(1);
		if (tablename.endsWith("`"))
			tablename = tablename.substring(0, tablename.length() - 1);
		return tablename;
	}

	public static String dogetnewid_old(CDBConnection con, String tbname, int num) throws Exception {
		String a[] = tbname.split("\\.");
		String tablename = (a.length == 2) ? a[1] : a[0];

		tablename = tablename.toLowerCase();
		if (tablename.startsWith("`"))
			tablename = tablename.substring(1);
		if (tablename.endsWith("`"))
			tablename = tablename.substring(0, tablename.length() - 1);

		String sid = getrowid_old(con, tablename);

		List<HashMap<String, String>> rows = con.openSql2List("select * from seq_all where sid=" + sid + " for update");
		if (rows.size() != 1) {
			throw new Exception("序列表【" + tablename + "】无数据！");
		}
		long idx = Long.valueOf(rows.get(0).get("idx"));
		long newidx = idx + num;
		String sqlstr = "update seq_all set idx=" + newidx + " where sid=" + sid;
		con.execsql(sqlstr);
		return String.valueOf(idx);
	}

	private static String getrowid_old(CDBConnection con, String tablename) throws Exception {
		List<HashMap<String, String>> rows = con.openSql2List("select * from seq_all where tbname='" + tablename + "'");
		if (rows.size() > 1)
			throw new Exception("序列表【" + tablename + "】发现多行数据！");
		if (rows.size() == 0) {// 没有找到
			String sqlstr = "";
			if (!tablename.equalsIgnoreCase("seq_all")) {
				String sid = dogetnewid_old(con, "seq_all", 1);
				sqlstr = "insert into seq_all(sid,tbname,idx) values(" + sid + ",'" + tablename + "',1)";
			} else {
				sqlstr = "insert into seq_all(sid,tbname,idx) values(0,'seq_all',1)";
			}
			con.execsql(sqlstr);
			rows = con.openSql2List("select * from seq_all where tbname='" + tablename + "'");
		}
		return rows.get(0).get("sid").toString();
	}

	// //reset seq
	public static void resetSeq(String poolname) throws Exception {
		CDBPool pool = DBPools.poolByName(poolname);
		CDBConnection con = pool.getCon(GetNewSEQID.class.getName());
		con.startTrans();
		try {
			String schema = pool.pprm.schema;
			con.openSql2List("select * from seq_all for update");// 锁表
			String sqlstr = null;
			if (con.getDBType() == DBType.mysql)
				sqlstr = "select TABLE_NAME,COLUMN_NAME from information_schema.KEY_COLUMN_USAGE where TABLE_NAME not like '_seq_%'"
						+ " and CONSTRAINT_NAME='PRIMARY' and ORDINAL_POSITION=1 and TABLE_SCHEMA='" + schema + "'";
			if (con.getDBType() == DBType.oracle)
				sqlstr = "select cc.TABLE_NAME,cc.COLUMN_NAME  from all_cons_columns cc, all_constraints ac "
						+ " where cc.OWNER=ac.OWNER and cc.TABLE_NAME=ac.TABLE_NAME and ac.CONSTRAINT_NAME=cc.CONSTRAINT_NAME "
						+ " and ac.CONSTRAINT_TYPE='P'and cc.CONSTRAINT_NAME not like 'BIN%' and cc.OWNER='" + schema.toUpperCase() + "'";

			List<HashMap<String, String>> rows = con.openSql2List(sqlstr);
			for (HashMap<String, String> row : rows) {
				String tbname = row.get("TABLE_NAME").toString().toLowerCase();
				String fdname = row.get("COLUMN_NAME").toString().toLowerCase();
				sqlstr = "select max(" + fdname + ") mx from " + tbname;
				List<HashMap<String, String>> mrows = con.openSql2List(sqlstr);
				Object o = mrows.get(0).get("mx");
				long idx = 0;
				try {
					if (o == null) {
						idx = 1;
					} else {
						idx = Long.valueOf(o.toString()) + 1;
					}
					if (con.openSql2List("select * from seq_all where tbname='" + tbname + "'").size() == 0) {
						String sid = doCreateNewID(con, "seq_all", 1);
						con.execsql("insert into seq_all(sid,tbname,idx) values(" + sid + ",'" + tbname + "'," + idx + ")");
					} else
						con.execsql("update seq_all set idx=" + idx + " where tbname='" + tbname + "'");
					System.out.println("reset seq table 【" + tbname + "】 Ok,idx is " + idx);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("reset seq table 【" + tbname + "】错误，略过");
				}
			}
			con.submit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
		// 检查 seq_all 是否存在 存在则新建
		// 循环所有 非 _seq 开头的表 检索每个表主键 当前值
		// 更新 seq_all表
	}
}
