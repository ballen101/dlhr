package com.corsair.server.base;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.naming.*;
import javax.sql.*;

import oracle.sql.BLOB;
import oracle.sql.CLOB;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.Logsw;

/** 过时
 * @author Administrator
 *
 */
public class DBBusiness {

//	public Connection getConnByTomcat1() {
//		// String lookupds = "java:comp/env/jdbc/corsair";
//		String lookupds = "java:comp/env/jdbc/" + ConstsSw.geAppParmStr("DataBase_ConnectStr");
//		Connection conn = null;
//		try {
//			Context ctx = new InitialContext();
//			DataSource ds = (DataSource) ctx.lookup(lookupds);
//			conn = ds.getConnection();
//		} catch (Exception e) {
//			Logsw.error("取得数据库连接错误:", e);
//			// throw new Exception(errorstr);
//		}
//		return conn;
//	}
//
//	public String getcon4pool(String xml) throws Exception {
//		Document document = DocumentHelper.parseText(xml);
//		Element employees = document.getRootElement();
//		String poolname = employees.element("poolname").getText();
//
//		CDBConnection result = DBPools.poolByName(poolname).getCon(this);
//		result.con.setAutoCommit(false);// 开始事务处理
//		// result.close();//还给缓冲池 等待客户端下次调用
//		Logsw.debug("客户端请求事务处理:" + result.key);
//		return CodeXML.AddCodeStr(ConstsSw.code_of_dbtool_sessionopen, result.key);
//	}
//
//	public String dbsessioncommit(String XMLStr) throws Exception {
//		// Document document = DocumentHelper.parseText(XMLStr);
//		// Element employees = document.getRootElement();
//		// String sessionKey = employees.element("sessionKey").getText();
//		// String poolname = employees.element("poolname").getText();
//		// CDBConnection session =
//		// DBPools.poolByName(poolname).getConLocked(sessionKey);
//		// Logsw.debug("客户端提交事务处理:" + session.key);
//		// session.submit(); // ////提交后 会自动关闭连接 取消事物处理
//		// return CodeXML.AddCodeStr(ConstsSw.code_of_dbtool_sessioncommit,
//		// "OK");
//		return null;
//	}
//
//	public String dbsessionrollback(String XMLStr) throws Exception {
//		// Document document = DocumentHelper.parseText(XMLStr);
//		// Element employees = document.getRootElement();
//		// String sessionKey = employees.element("sessionKey").getText();
//		// String poolname = employees.element("poolname").getText();
//		// CDBConnection session =
//		// DBPools.poolByName(poolname).getConLocked(sessionKey);
//		// Logsw.debug("客户端回滚事务处理:" + session.key);
//		// session.rollback();// 回滚后会自动关闭连接 取消事物处理
//		// return CodeXML.AddCodeStr(ConstsSw.code_of_dbtool_sessionrollback,
//		// "OK");
//		return null;
//	}
//
//	public String OpenDataset(String XMLStr) throws Exception {
//		// String result = null;
//		// Document document = DocumentHelper.parseText(XMLStr);
//		// Element employees = document.getRootElement();
//		// String sqlstr = employees.element("sqlstr").getText();
//		// String sessionkey = employees.element("sessionkey").getText();
//		// String poolname = employees.element("poolname").getText();
//		// CDBConnection con = null;
//		// if (sessionkey.trim().isEmpty()) {
//		// con = DBPools.poolByName(poolname).getCon(this);
//		// } else
//		// con = DBPools.poolByName(poolname).getConLocked(sessionkey);
//		//
//		// Statement stmt;
//		//
//		// try {
//		// stmt = con.con.createStatement();
//		// Logsw.debug("查询语句:" + sqlstr);
//		// // System.out.println("Open:" + ConstsSw.getCurrentDateTime(null));
//		// ResultSet rs = stmt.executeQuery(sqlstr);
//		// // System.out.println("opened:" +
//		// // ConstsSw.getCurrentDateTime(null));
//		// result = generateXML(rs);
//		// // System.out.println("generated:" +
//		// // ConstsSw.getCurrentDateTime(null));
//		// rs.close();
//		// stmt.close();
//		// }
//		//
//		// catch (SQLException e) {
//		// // System.out.println(e.getClass() + "  " + );
//		// if (ConstsSw.getAppParmBoolean("Debug_Mode")) {
//		// e.printStackTrace();
//		// }
//		// String errorstr;
//		// if (e.getErrorCode() == 1205)
//		// errorstr = "数据对象被锁定,不允许更新，稍后重试!";
//		// else
//		// errorstr = "执行查询语句错误:" + sqlstr + "---" + e.getMessage();
//		// Logsw.error(errorstr, e);
//		// throw e;
//		// } finally {
//		// try {
//		// if (sessionkey.trim().isEmpty()) //
//		// con.close();
//		// } catch (Exception e) {
//		// e.printStackTrace();
//		// }
//		// }
//		//
//		// return CodeXML.AddCodeStr(ConstsSw.code_of_opendatawithsql, result);
//		return null;
//		// System.out.println("AddCodeXML_" + (new systemdate()).getdate());
//	}
//
//	public String OpenDatasetpage(String XMLStr) throws Exception {
//		String result = null;
//		Document document = DocumentHelper.parseText(XMLStr);
//		Element employees = document.getRootElement();
//		String sqlstr = employees.element("SQL").getText();
//		int pagesize = Integer.valueOf(employees.element("PageSize").getText());
//		int pageindex = Integer.valueOf(employees.element("PageIndex").getText());
//		String poolname = employees.element("poolname").getText();
//		CDBConnection con = DBPools.poolByName(poolname).getCon(this);
//		Statement stmt = con.con.createStatement();
//		Logsw.debug("分页查询语句:" + sqlstr);
//		// System.out.println("Open:" + ConstsSw.getCurrentDateTime(null));
//
//		String sqlct = "select count(*) ct from (" + sqlstr + ") tb";
//		ResultSet rs = stmt.executeQuery(sqlct);
//
//		rs.first();
//		int recordcount = Integer.valueOf(rs.getObject(1).toString());
//		int pagecount = 0;
//		if ((recordcount % pagesize) == 0) {
//			pagecount = recordcount / pagesize;
//		} else {
//			pagecount = (recordcount / pagesize) + 1;
//		}
//		if (pageindex > pagecount) {
//			pageindex = pagecount;
//		}
//
//		int rb = pageindex * pagesize;// 起始记录条数
//
//		String sqlstrpage = "select * from (" + sqlstr + ") tb limit " + String.valueOf(rb) + "," + String.valueOf(pagesize);
//		Logsw.debug("page sql:" + sqlstrpage);
//		rs.close();
//		rs = stmt.executeQuery(sqlstrpage);
//		String data = generateXML(rs);
//
//		// System.out.println(data);
//
//		rs.close();
//		stmt.close();
//		con.close();
//
//		document = DocumentHelper.createDocument();
//		Element root = document.addElement(ConstsSw.xml_root_corsair);// 创建根节点
//		root.addElement("pagecount").setText(String.valueOf(pagecount));
//		root.addElement("pageindex").setText(String.valueOf(pageindex));
//		root.addElement("pagesize").setText(String.valueOf(pagesize));
//		root.addElement("data").setText(data);
//		result = document.asXML();
//
//		// System.out.println(result);
//
//		return CodeXML.AddCodeStr(ConstsSw.code_of_opendatawithsql_paged, result);
//	}
//
//	String getFdName(ResultSetMetaData rsmd, int con) {// MySQL的bug，所以要分开取字段名
//		// 否则别名取不到
//		try {
//			// String f1 = rsmd.getCatalogName(con);
//			// String f2 = rsmd.getColumnLabel(con);
//
//			if (ConstsSw.getAppParmInt("DataBaseType") == ConstsSw._DataBase_Oracle) {
//				return rsmd.getColumnLabel(con);
//				// return rsmd.getCatalogName(con);
//			}
//			if (ConstsSw.getAppParmInt("DataBaseType") == ConstsSw._DataBase_MySQL) {
//				return rsmd.getColumnLabel(con);
//			}
//			return "";
//		} catch (Exception e) {
//			return "";
//		}
//	}
//
//	public String generateXML(final ResultSet rs) throws SQLException {
//		String result = null;
//		if (rs == null)
//			return "";
//
//		Document document = DocumentHelper.createDocument();
//		Element root = document.addElement(ConstsSw.xml_root_corsair);// 创建根节点
//		Element meta_root = root.addElement(ConstsSw.xml_data_metadata);
//		Element data_root = root.addElement(ConstsSw.xml_data_data);
//		ResultSetMetaData rsmd = rs.getMetaData(); // 得到结果集的定义结构
//
//		Element field;
//		Element data;
//		Element row;
//
//		// Set RepeatColumns = null;
//		Vector<Integer> RepeatColumns = new Vector<Integer>();
//
//		int colCount = rsmd.getColumnCount(); // 得到列的总数
//		// System.out.println("colCount:" + colCount);
//
//		for (int i = 1; i <= colCount; i++) {// 标记重复列
//			for (int j = i + 1; j <= colCount; j++) {
//				if (getFdName(rsmd, i).equals(getFdName(rsmd, j))) {
//					RepeatColumns.add(new Integer(j));
//				}
//			}
//		}
//
//		// System.out.println(colCount);
//
//		for (int i = 1; i <= colCount; i++) {//
//			// System.out.println("所有列：" + getFdName(rsmd, i));
//			if (!RepeatColumns.contains(i)) {
//				int type = rsmd.getColumnType(i);
//				field = meta_root.addElement(ConstsSw.xml_field);
//				data = field.addElement(ConstsSw.xml_field_name);
//				data.setText(getFdName(rsmd, i));
//
//				data = field.addElement(ConstsSw.xml_field_type);
//				data.setText(String.valueOf(type));
//
//				data = field.addElement(ConstsSw.xml_field_size);
//				data.setText(String.valueOf(rsmd.getColumnDisplaySize(i)));
//
//			} else {
//				// System.out.println("重复列：" + getFdName(rsmd, i));
//			}
//		}
//		// System.out.println(document.asXML());
//		if (!rs.next())
//			return document.asXML();
//
//		int id = 0;
//		while (true) {
//			row = data_root.addElement(ConstsSw.xml_row);
//			row.setText(String.valueOf(++id));
//			for (int i = 1; i <= colCount; i++) {
//				if (!RepeatColumns.contains(i)) {
//					int type = rsmd.getColumnType(i);
//					data = row.addElement(ConstsSw.xml_field_value);
//					data.setText(getvalue(rs, i, type));
//				}
//			}
//			if (!rs.next())
//				break;
//		}
//		result = document.asXML();
//		return result;
//	}
//
//	private String getvalue(final ResultSet rs, int colNum, int type) throws SQLException {
//		switch (type) {
//		case Types.ARRAY:
//		case Types.BLOB:
//		case Types.CLOB:
//		case Types.DISTINCT:
//		case Types.LONGVARBINARY:
//		case Types.VARBINARY:
//		case Types.BINARY:
//		case Types.REF:
//		case Types.STRUCT:
//			return "undefined";
//		case Types.DATE:
//		case Types.TIMESTAMP: {
//
//			Object value = null;
//			try {
//				value = rs.getObject(colNum);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			if (rs.wasNull() || (value == null))
//				return (ConstsSw.xml_null);
//			else {
//				String v = value.toString();
//				if (v.substring(v.length() - 2, v.length()).endsWith(".0"))
//					v = v.substring(0, v.length() - 2);
//				return (v);
//			}
//		}
//
//		default: {
//			Object value = rs.getObject(colNum);
//			if (rs.wasNull() || (value == null))
//				return (ConstsSw.xml_null);
//			else {
//				// System.out.println(type);
//				// System.out.println(value.toString());
//				return (value.toString());
//			}
//		}
//		}
//	}
//
//	public String ExecSQLDataset(String XMLStr) throws Exception {
//
//		// Document document = DocumentHelper.parseText(XMLStr);
//		// Element employees = document.getRootElement();
//		// String sqlstr = employees.element("sqlstr").getText();
//		// String sessionkey = employees.element("sessionkey").getText();
//		// String poolname = employees.element("poolname").getText();
//		// CDBConnection con = null;
//		// if (sessionkey.trim().isEmpty()) {
//		// con = DBPools.poolByName(poolname).getCon(this);
//		// } else
//		// con = DBPools.poolByName(poolname).getConLocked(sessionkey);
//		//
//		// Statement stmt;
//		// String result = null;
//		// try {
//		// stmt = con.con.createStatement();
//		// Logsw.debug("更新语句:" + sqlstr);
//		// result = String.valueOf(stmt.executeUpdate(sqlstr));
//		// stmt.close();
//		// }
//		//
//		// catch (Exception e) {
//		// String errorstr = "执行更新语句错误:" + sqlstr + "---" + e.getMessage();
//		// Logsw.error(errorstr, e);
//		// throw e;
//		// } finally {
//		// try {
//		// if (sessionkey.trim().isEmpty())
//		// con.close();
//		// } catch (Exception e) {
//		// e.printStackTrace();
//		// }
//		// }
//		//
//		// return CodeXML.AddCodeStr(ConstsSw.code_of_Execsql, result);
//		return null;
//	}
//
//	@SuppressWarnings("unchecked")
//	public String ExecSQLSDataset(String XMLStr) throws Exception {
//		//
//		// String result = null;
//		// String dbgstr = null;
//		//
//		// // 解析出所有SQL语句
//		// Document document = DocumentHelper.parseText(XMLStr);
//		// Element employees = document.getRootElement();
//		// String sessionkey = employees.element("sessionkey").getText();
//		// List<Element> sqls = employees.elements("sqlstr");
//		// List<String> sqlstrs = new ArrayList<String>();
//		// for (Element sql : sqls) {
//		// sqlstrs.add(sql.getText());
//		// // System.out.println("事务:" + sql.getText());
//		// Logsw.debug("事务:" + sql.getText());
//		// }
//		// String poolname = employees.element("poolname").getText();
//		// CDBConnection con = null;
//		// if (sessionkey.trim().isEmpty()) {
//		// con = DBPools.poolByName(poolname).getCon(this);// //不是客户端的事务处理
//		// } else {
//		// con = DBPools.poolByName(poolname).getConLocked(sessionkey);//
//		// //客户端的事物处理
//		// }
//		// Statement stmt;
//		// try {
//		// stmt = con.con.createStatement();
//		// if (sessionkey.trim().isEmpty()) {// 不是客户端的事务处理 自己事务处理
//		// Logsw.debug("非客户端事务，主动启动事务处理！");
//		// con.con.setAutoCommit(false);// 不允许自动提交
//		// }
//		// for (String sqlstr : sqlstrs) {
//		// Logsw.debug("执行事务处理:" + sqlstr);
//		// String.valueOf(stmt.executeUpdate(sqlstr));
//		// }
//		//
//		// if (sessionkey.trim().isEmpty()) {
//		// Logsw.debug("非客户端事务，提交！");
//		// if (!con.con.getAutoCommit())
//		// con.con.commit();
//		// con.con.setAutoCommit(true);
//		// }
//		// result = "OK";
//		// stmt.close();
//		// }
//		//
//		// catch (Exception e) {
//		// if (sessionkey.trim().isEmpty()) {
//		// Logsw.debug("非客户端事务，回滚！");
//		// if (!con.con.getAutoCommit())
//		// con.con.rollback();
//		// con.con.setAutoCommit(true);
//		// }
//		// result = "ERROR";
//		// String errorstr = "事务处理错误:" + dbgstr + "---" + e.getMessage();
//		// Logsw.error(errorstr, e);
//		// throw e;
//		// } finally {
//		// if (sessionkey.trim().isEmpty())//
//		// con.close();
//		// }
//		//
//		// return CodeXML.AddCodeStr(ConstsSw.code_of_ExecTransSQL, result);
//		return null;
//	}
//
//	@SuppressWarnings("deprecation")
//	public String SaveBlobClob(String XMLStr, Integer dbk_type) throws Exception {
//		Document document = DocumentHelper.parseText(XMLStr);
//		Element employees = document.getRootElement();
//
//		String tablename = employees.element(ConstsSw.xml_table_name).getText();
//		String fieldname = employees.element(ConstsSw.xml_field_name).getText();
//		String sql_where = employees.element(ConstsSw.xml_sql_str).getText();
//		String blob_str = employees.element(ConstsSw.xml_field_value).getText();
//		String poolname = employees.element("poolname").getText();
//
//		CDBConnection con = DBPools.poolByName(poolname).getCon(this);
//		con.con.setAutoCommit(false);
//		String result = null;
//		String sqlstr = null;
//		try {
//			Statement stmt = con.con.createStatement();
//			// 需要先清空数据
//			String stremptString = null;
//			if (dbk_type == ConstsSw.dbk_BLOB)
//				if (ConstsSw.getAppParmInt("DataBaseType") == ConstsSw._DataBase_Oracle) {
//					stremptString = "empty_blob()";
//				}
//			if (ConstsSw.getAppParmInt("DataBaseType") == ConstsSw._DataBase_MySQL) {
//				stremptString = "null";
//			}
//
//			else if (dbk_type == ConstsSw.dbk_CLOB)
//				if (ConstsSw.getAppParmInt("DataBaseType") == ConstsSw._DataBase_Oracle) {
//					stremptString = "empty_clob()";
//				}
//			if (ConstsSw.getAppParmInt("DataBaseType") == ConstsSw._DataBase_MySQL) {
//				stremptString = "null";
//			}
//
//			sqlstr = "update " + tablename + " set " + fieldname + "=" + stremptString + " where 1=1 " + sql_where;
//
//			Logsw.debug("保存BLOB/CLOB语句" + sqlstr);
//			stmt.execute(sqlstr);
//
//			PreparedStatement ptmt = con.con.prepareStatement("select " + fieldname + " from " + tablename + " where 1=1 " + sql_where + " for update");
//			ResultSet rs = ptmt.executeQuery();
//			if (rs.next()) {
//				ptmt = con.con.prepareStatement("update " + tablename + " set " + fieldname + " =? " + "  where 1=1 " + sql_where);
//				if (dbk_type == ConstsSw.dbk_BLOB) {
//					if (ConstsSw.getAppParmInt("DataBaseType") == ConstsSw._DataBase_Oracle) {
//						BLOB blob = (BLOB) rs.getBlob(fieldname);
//						blob.putBytes(1, blob_str.getBytes("UTF-8"));
//						ptmt.setBlob(1, blob);
//					}
//					if (ConstsSw.getAppParmInt("DataBaseType") == ConstsSw._DataBase_MySQL) {
//						ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(blob_str.getBytes("UTF-8"));
//						ptmt.setBinaryStream(1, tInputStringStream, tInputStringStream.available());
//					}
//				}
//				if (dbk_type == ConstsSw.dbk_CLOB) {
//					CLOB clob = (CLOB) rs.getClob(fieldname);
//					clob.putString(1, blob_str);
//					ptmt.setClob(1, clob);
//					// System.out.println(blob_str);
//				}
//
//				Integer rtInteger = ptmt.executeUpdate();
//				ptmt.close();
//				stmt.close();
//
//				// System.out.println(String.valueOf(rtInteger));
//				con.con.commit();
//				result = "OK";
//			}
//		} catch (Exception e) {
//			// System.out.println("RollBack");
//			Logsw.debug("RollBack");
//			con.con.rollback();
//			result = "ERROR";
//			String errorstr = "保存Blob/Clob数据错误:" + sqlstr + "---" + e.getMessage();
//			Logsw.error(errorstr, e);
//			throw e;
//		} finally {
//			con.con.setAutoCommit(true);
//			try {
//				con.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//
//		if (dbk_type == ConstsSw.dbk_BLOB)
//			return CodeXML.AddCodeStr(ConstsSw.code_of_SaveBlob, result);
//		else if (dbk_type == ConstsSw.dbk_CLOB)
//			return CodeXML.AddCodeStr(ConstsSw.code_of_SaveClob, result);
//		else
//			return null;
//	}
//
//	public String FetchBlobClob(String XMLStr, Integer dbk_type) throws Exception {
//		Document document = DocumentHelper.parseText(XMLStr);
//		Element employees = document.getRootElement();
//
//		String tablename = employees.element(ConstsSw.xml_table_name).getText();
//		String fieldname = employees.element(ConstsSw.xml_field_name).getText();
//		String sql_where = employees.element(ConstsSw.xml_sql_str).getText();
//		String poolname = employees.element("poolname").getText();
//
//		CDBConnection con = DBPools.poolByName(poolname).getCon(this);
//		String result = null;
//		String sqlstr = null;
//		String blob_str = null;
//		try {
//
//			Statement stmt = con.con.createStatement();
//			sqlstr = "select " + fieldname + " from " + tablename + " where 1=1 " + sql_where;
//			Logsw.debug("读取BLOB/CLOB语句" + sqlstr);
//
//			ResultSet rs = stmt.executeQuery(sqlstr);
//			if (rs.next()) {
//				if (dbk_type == ConstsSw.dbk_BLOB) {
//					if ((ConstsSw.getAppParmInt("DataBaseType") == ConstsSw._DataBase_Oracle) && (rs.getBlob(fieldname) != null)) {
//						BLOB blob = (BLOB) rs.getBlob(fieldname);
//						byte b[] = blob.getBytes(1, (int) blob.length());
//						blob_str = new String(b, "UTF-8");
//					}
//					if ((ConstsSw.getAppParmInt("DataBaseType") == ConstsSw._DataBase_MySQL) && (rs.getBlob(fieldname) != null)) {
//						blob_str = "";
//						InputStream in = rs.getBinaryStream(fieldname);
//						byte buffer[] = new byte[1024];
//						int len = 0;
//						while ((len = in.read(buffer)) != -1) {
//							blob_str += new String(buffer, 0, len);
//						}
//						in.close();
//					}
//				}
//				if ((dbk_type == ConstsSw.dbk_CLOB) && (rs.getClob(fieldname) != null)) {
//					CLOB clob = (CLOB) rs.getClob(fieldname);
//					if (clob != null)
//						blob_str = clob.getSubString(1, (int) clob.length());
//				}
//			}
//			stmt.close();
//			result = blob_str;
//		} catch (Exception e) {
//			result = "ERROR";
//
//			String errorstr = "读取Blob//Clob数据错误:" + sqlstr + "---" + e.getMessage();
//			Logsw.error(errorstr, e);
//			throw e;
//		} finally {
//			try {
//				con.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//
//		if (dbk_type == ConstsSw.dbk_BLOB)
//			return CodeXML.AddCodeStr(ConstsSw.code_of_FetchBlobJava, result);
//		else if (dbk_type == ConstsSw.dbk_CLOB)
//			return CodeXML.AddCodeStr(ConstsSw.code_of_FetchClobJava, result);
//		else
//			return null;
//	}
//
//	public String GetNewSEQID(String ID_OraSequence) throws Exception {
//		String result = "";
//
//		String a[] = ID_OraSequence.split("\\.");
//		String tablename = null;
//		String poolname = null;
//		if (a.length == 1) {
//			tablename = a[0];
//		}
//		if (a.length >= 2) {
//			poolname = a[0];
//			tablename = a[1];
//		}
//
//		String sqlstr = "select " + tablename + ".NEXTVAL as id From dual";
//		CDBConnection con = DBPools.poolByName(poolname).getCon(this);
//		try {
//			// Connection con = getConnection();
//			Statement stmt = con.con.createStatement();
//			ResultSet rs = stmt.executeQuery(sqlstr);
//			if (rs.next())
//				result = rs.getString("id");
//
//		} finally {
//			con.close();
//		}
//		return result;
//	}
//
//	public String findTablePriKeys(String XMLStr) throws Exception {
//		CDBConnection con = DBPools.poolByName(null).getCon(this);
//		Document document;
//		try {
//			DatabaseMetaData meta = con.con.getMetaData();
//			ResultSet rsKey = meta.getPrimaryKeys(null, null, XMLStr); // 获取指定表的主键列信息
//			document = DocumentHelper.createDocument();
//			Element root = document.addElement(ConstsSw.xml_root_corsair);// 创建根节点
//			while (rsKey.next()) {
//				Element fde = root.addElement(ConstsSw.xml_field_name);
//				fde.setText(rsKey.getString(4));
//			}
//		} finally {
//			con.close();
//		}
//
//		return CodeXML.AddCodeStr(ConstsSw.code_of_GetTablePriKeys, document.asXML());
//	}

}
