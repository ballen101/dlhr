package com.corsair.server.util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.util.CJPASqlUtil;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.CDBConnection.DBType;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.Systemdate;

public class GetNewSystemCode {

	public synchronized String dogetnewsyscode(CJPABase jpa, String codeid) throws Exception {
		String result = "";
		String zstr = "00000000000000000000000000000";
		CDBConnection con = DBPools.defaultPool().getCon(this);
		con.startTrans();
		try {
			String sqlstr = null;
			if (con.getDBType() == DBType.mysql)
				sqlstr = "select now() dt,a.* from shwsyscode a where a.codeid=" + codeid + " for update";
			else if (con.getDBType() == DBType.oracle)
				sqlstr = "select sysdate dt,a.* from shwsyscode a where a.codeid=" + codeid + " for update";
			else
				throw new Exception("【" + this.getClass().getName() + "】不支持的数据库");
			List<HashMap<String, String>> rows = con.openSql2List(sqlstr);
			if (rows.size() == 0) {
				throw new Exception("没有找到ID为:" + codeid + "的编码规则!");
			}
			HashMap<String, String> row = rows.get(0);
			int istree = Integer.valueOf(row.get("istree").toString());
			// System.out.println("istree:" + istree);
			if (istree == 2) {
				String prefix = (row.get("prefix") == null) ? "" : row.get("prefix").toString();
				int len = Integer.valueOf(row.get("len").toString());
				int index = (row.get("curindex") == null) ? 0 : Integer.valueOf(row.get("curindex").toString());
				String strdt = row.get("dt").toString();
				Date dt = Systemdate.getDateByStr(strdt);
				String datestr = (row.get("datestr") != null) ? row.get("datestr").toString() : "";
				String fmt = (row.get("datefmt") != null) ? row.get("datefmt").toString() : "";
				String datestr1 = (fmt.isEmpty()) ? "" : Systemdate.getStrDateByFmt(dt, fmt);
				if (datestr1.equals(datestr)) {
					index++;
				} else {
					index = 1;
				}
				String str = zstr.substring(0, len) + String.valueOf(index);
				str = str.substring(str.length() - len);
				result = prefix + datestr1 + str;
				sqlstr = "update shwsyscode set curindex=" + index + ",datestr='" + datestr1 + "' where codeid=" + codeid;
				con.execsql(sqlstr);
				con.submit();
				return result;
			} else if (istree == 1) {
				con.submit();
				if (jpa == null)
					throw new Exception("获取级别code，JPAClass 不允许为空");
				return newtreecode(row, jpa);
			} else
				throw new Exception("字段【istree】值只允许为1、2");
		} catch (Exception e) {
			con.rollback();
			e.printStackTrace();
			throw e;
		} finally {
			con.close();
		}
	}

	private String newtreecode(HashMap<String, String> codeparm, CJPABase jpa) throws Exception {
		String zstr = "00000000000000000000000000000";
		CDBConnection con = jpa.pool.getCon(this);
		con.startTrans();
		try {
			int len = Integer.valueOf(codeparm.get("len").toString());
			String pfd = codeparm.get("parentfield").toString();
			String cfd = codeparm.get("codefield").toString();
			CField pcfd = jpa.cfield(pfd);
			String sqlstr = "select * from " + jpa.tablename + " where " + jpa.getIDFieldName() + " = "
					+ CJPASqlUtil.getSqlValue(jpa.pool.getDbtype(), pcfd.getFieldtype(), pcfd.getValue()) + " for update";
			List<HashMap<String, String>> rows = con.openSql2List(sqlstr);// 锁定上级记录
			String pcode = ""; 
			if (rows.size() > 0) {
				pcode = rows.get(0).get(cfd).toString();
			}
			sqlstr = "select max(" + cfd + ") " + cfd + " from " + jpa.tablename + " where " + pfd + " = "
					+ CJPASqlUtil.getSqlValue(jpa.pool.getDbtype(), pcfd.getFieldtype(), pcfd.getValue());
			rows = con.openSql2List(sqlstr);
			String rst = null;
			Object maxcodeo = rows.get(0).get(cfd);
			if (maxcodeo == null) {
				//System.out.print("isnull");
				String str = zstr + "1";
				str = str.substring(str.length() - len, str.length());
				rst = pcode + str;
			} else {
				//System.out.print("not isnull");
				String maxcode = maxcodeo.toString().trim();
				if (pcode.length() >= maxcode.length()) {
					throw new Exception("逻辑错误【上级编码长度 大于或等于 下级编码长度】!");
				}
				maxcode = maxcode.substring(pcode.length(), maxcode.length());
				int nmc = Integer.valueOf(maxcode) + 1;
				maxcode = String.valueOf(nmc);
				if (maxcode.length() > len) {
					throw new Exception("编码长度超过参数设置值");
				}
				String str = zstr + maxcode;
				str = str.substring(str.length() - len, str.length());
				rst = pcode + str;
			}
			con.submit();
			return rst;
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}
}
