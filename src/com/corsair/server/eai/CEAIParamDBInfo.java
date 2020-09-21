package com.corsair.server.eai;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;

public class CEAIParamDBInfo extends CEAIParamXMLFile {

	public CEAIParamDBInfo(String xmlfname) throws Exception {
		super(xmlfname);
		try {
			initDBinfo();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new Exception("初始化EAI 字段数据类型<" + xmlfname + ">错误：" + e.getMessage());
		}
	}

	public CDBConnection getScon() throws Exception {
		CDBConnection con = DBPools.poolByName(getDbpool_source()).getCon(this);
		if (con == null)
			throw new Exception("获取数据库连接错误:" + getDbpool_source());
		return con;
	}

	public CDBConnection getDcon() throws Exception {
		CDBConnection con = DBPools.poolByName(getDbpool_target()).getCon(this);
		if (con == null)
			throw new Exception("获取数据库连接错误:" + getDbpool_target());
		return con;
	}

	// ////////////////////////////

	private void initDBinfo() throws Exception {
		if (!isEnable())
			return;
		String sqlstr = "select * from " + getS_tablename() + " where 0=1";

		CDBConnection cons = getScon();
		CDBConnection cond = getDcon();
		try {
			Statement stmt = cons.con.createStatement();
			// System.out.println(sqlstr);
			ResultSet rs = stmt.executeQuery(sqlstr);
			ResultSetMetaData rsmd = rs.getMetaData(); // 得到结果集的定义结构
			initSDBInfo(rsmd);
			//
			sqlstr = "select * from " + this.getT_tablename() + " where 1=0";
			stmt = cond.con.createStatement();
			rs = stmt.executeQuery(sqlstr);
			rsmd = rs.getMetaData(); // 得到结果集的定义结构
			initTDBInfo(rsmd);
		} finally {
			cons.close();
			cond.close();
		}
	}

	protected EAIMapField getMapField(List<EAIMapField> list, String fdname, boolean issource) {
		for (EAIMapField mf : list) {
			if (issource) {
				if (mf.getS_field().equalsIgnoreCase(fdname))
					return mf;
			} else {
				if (mf.getD_field().equalsIgnoreCase(fdname))
					return mf;
			}
		}
		return null;
	}

	private void initChildEaisSourceLinkFields(String fdname, int dataType) {
		for (CChildEAIParm ceai : getChildeais()) {
			for (EAIMapField mf : ceai.getLinkfields()) {
				if (mf.getS_field().equalsIgnoreCase(fdname))
					mf.setS_fieldtype(dataType);
			}
		}
	}

	private void initSDBInfo(ResultSetMetaData rsmd) throws SQLException {
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			String fdname = rsmd.getColumnLabel(i);
			EAIMapField ksfd = getMapField(getKeyfieds(), fdname, true);
			if (ksfd != null) {
				ksfd.setS_fieldtype(rsmd.getColumnType(i));
			}
			EAIMapField msfd = getMapField(getMapfields(), fdname, true);// getsmfield(fdname);
			if (msfd != null) {
				msfd.setS_fieldtype(rsmd.getColumnType(i));
				// System.out.println(fdname + "B:" + msfd.getS_fieldtype());
			}
			initChildEaisSourceLinkFields(fdname, rsmd.getColumnType(i));
		}
	}

	protected CEAICondt getcdt(String fdname) {
		for (CEAICondt cdt : getCondts()) {
			if (cdt.getField().equalsIgnoreCase(fdname))
				return cdt;
		}
		return null;
	}

	private void initTDBInfo(ResultSetMetaData rsmd) throws SQLException {
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			String fdname = rsmd.getColumnLabel(i);
			EAIMapField ksfd = getMapField(getKeyfieds(), fdname, false);
			if (ksfd != null) {
				ksfd.setD_fieldtype(rsmd.getColumnType(i));
				// System.out.println(fdname + "C:" + ksfd.getD_keyfieldtype());
			}
			EAIMapField msfd = getMapField(getMapfields(), fdname, false);
			if (msfd != null) {
				msfd.setD_fieldtype(rsmd.getColumnType(i));
				// System.out.println(fdname + "D:" + msfd.getD_fieldtype());
			}

			CEAICondt cdt = getcdt(fdname);
			if (cdt != null) {
				cdt.setFieldtype(rsmd.getColumnType(i));
			}
		}
	}

}
