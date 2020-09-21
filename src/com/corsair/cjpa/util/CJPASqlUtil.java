package com.corsair.cjpa.util;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPASave;
import com.corsair.dbpool.CDBConnection.DBType;
import com.corsair.dbpool.util.CPoolSQLUtil;
import com.corsair.dbpool.util.PraperedValue;
import com.corsair.dbpool.DBPools;

/**
 * SQL创建工具
 * 
 * @author Administrator
 *
 */
public class CJPASqlUtil {
	public static PraperedValue getSqlPValue(DBType dbtype, CField cf) {
		PraperedValue rst = new PraperedValue();
		rst.setFieldtype(cf.getFieldtype());

		if (CPoolSQLUtil.eInArray(rst.getFieldtype(), CPoolSQLUtil.numFDType))
			if ((cf.getValue() == null) || (cf.getValue().isEmpty()))
				rst.setValue(null);
			else
				rst.setValue(cf.getValue());
		if (CPoolSQLUtil.eInArray(cf.getFieldtype(), CPoolSQLUtil.strFDType)) {
			if ((cf.getValue() == null) || (cf.getValue().isEmpty()))
				rst.setValue(null);
			else
				rst.setValue(cf.getValue());
		}
		if (CPoolSQLUtil.eInArray(cf.getFieldtype(), CPoolSQLUtil.dateFDType)) {
			if ((cf.getValue() == null) || (cf.getValue().isEmpty()))
				rst.setValue(null);
			else {
				rst.setValue(cf.getAsDatetime());
			}
		}
		return rst;
	}

	public static String getSqlValue(DBType dbtype, int datatype, String value) {
		String result = "";
		if ((value == null) || value.isEmpty())
			return "null";
		result = value;
		if (CPoolSQLUtil.eInArray(datatype, CPoolSQLUtil.numFDType))
			result = value;
		if (CPoolSQLUtil.eInArray(datatype, CPoolSQLUtil.strFDType)) {
			result = value.replaceAll("\\\\", "\\\\\\\\");
			result = result.replaceAll("\'", "\\\\'");
			result = "'" + result + "'";
		}
		if (CPoolSQLUtil.eInArray(datatype, CPoolSQLUtil.dateFDType)) {
			if (dbtype == DBType.mysql)
				result = "'" + value + "'";
			if (dbtype == DBType.oracle)
				result = "to_date('" + value + "','yyyy-mm-dd hh24:mi:ss')";
		}
		return result;
	}

	public static String getSqlTable(String tbname) {
		return getSqlTable(DBPools.defaultPool().getDbtype(), tbname);
	}

	public static String getSqlTable(DBType dbtype, String tbname) {
		String result = tbname;
		if (dbtype == DBType.mysql)
			if (!result.startsWith("`"))
				result = "`" + result.toLowerCase() + "`";
		if (dbtype == DBType.oracle)
			result = result.toUpperCase();
		return result;
	}

	public static String getSqlField(DBType dbtype, String parmname) {
		int idx = parmname.lastIndexOf('.');
		String tbname = null;
		String fdname = parmname;
		if (idx != -1) {
			tbname = parmname.substring(0, idx);
			fdname = parmname.substring(idx + 1);
		}
		String result = parmname;
		if (dbtype == DBType.mysql)
			result = (tbname == null) ? "`" + fdname + "`" : tbname + ".`" + fdname + "`";
		if (dbtype == DBType.oracle)
			result = (tbname == null) ? "" + fdname + "" : tbname + "." + fdname + "";
		return result;
	}

	// 生成 某字段 更新SQL语句 如：fdname=value
	public static String getFieldSQLUpdate(CJPASave cjpa, String fdname, String value) {
		DBType dbtype = cjpa.pool.getDbtype();
		return getSqlField(dbtype, fdname) + "=" + getSqlValue(dbtype, cjpa.cfield(fdname).getFieldtype(), value);
	}

	/**
	 * 生成 某些字段 更新SQL语句 如：fdname1=value1,fdname2=value2,fdname3=value3
	 * 
	 * @param upjpa
	 * @param fdnames
	 * @param valjpa
	 * @return
	 */
	public static String getFieldSQLUpdate(CJPASave upjpa, String[] fdnames, CJPASave valjpa) {
		DBType dbtype = upjpa.pool.getDbtype();
		String rst = "";
		for (String fdname : fdnames) {
			rst = rst + getSqlField(dbtype, fdname) + "="
					+ getSqlValue(dbtype, upjpa.cfield(fdname).getFieldtype(), valjpa.cfield(fdname).getValue()) + ",";
		}
		if (!rst.isEmpty())
			rst = rst.substring(0, rst.length() - 1);
		return rst;
	}

	public static int countStr(String str1, String str2) {
		int counter = 0;
		if (str1.indexOf(str2) == -1) {
			return 0;
		}
		while (str1.indexOf(str2) != -1) {
			counter++;
			str1 = str1.substring(str1.indexOf(str2) + str2.length());
		}
		return counter;
	}
}
