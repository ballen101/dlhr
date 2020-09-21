package com.corsair.server.util;

import java.util.HashMap;

import net.sf.json.JSONArray;

import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.CDBPool;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.CJSON;
import com.corsair.server.base.CSContext;
import com.corsair.server.cjpa.CJPA;

public class CSearchForm {

	public static String doExec2JSON(String sqlstr) throws Exception {
		CDBPool pool = DBPools.defaultPool();
		return pool.opensql2json(prepareSqlstr(pool, sqlstr, null));
	}

	public static JSONArray doExec2JSON_O(String sqlstr) throws Exception {
		CDBPool pool = DBPools.defaultPool();
		return pool.opensql2json_O(prepareSqlstr(pool, sqlstr, null));
	}

	public static String doExec2JSON(CDBPool apool, String sqlstr) throws Exception {
		CDBPool pool = (apool == null) ? DBPools.defaultPool() : apool;
		return pool.opensql2json(prepareSqlstr(pool, sqlstr, null));
	}

	public static JSONArray doExec2JSON_O(CDBPool apool, String sqlstr) throws Exception {
		CDBPool pool = (apool == null) ? DBPools.defaultPool() : apool;
		return pool.opensql2json_O(prepareSqlstr(pool, sqlstr, null));
	}

	public static String doExec2JSON(CDBPool apool, String sqlstr, String idpathAliaName) throws Exception {
		CDBPool pool = (apool == null) ? DBPools.defaultPool() : apool;
		return pool.opensql2json(prepareSqlstr(pool, sqlstr, idpathAliaName));
	}

	public static JSONArray doExec2JSON_O(CDBPool apool, String sqlstr, String idpathAliaName) throws Exception {
		CDBPool pool = (apool == null) ? DBPools.defaultPool() : apool;
		return pool.opensql2json_O(prepareSqlstr(pool, sqlstr, idpathAliaName));
	}

	public static String prepareSqlstr(CDBPool pool, String sqlstr, String idpathAliaName) throws Exception {
		String idpathw = CSContext.getIdpathwhere();
		if ((idpathAliaName != null) && (!idpathAliaName.isEmpty())) {
			if (idpathw == null)
				throw new Exception("idpath为NULL，不允许设置别名");
			idpathw = idpathw.replace("idpath", idpathAliaName + "." + idpathw);
		}

		HashMap<String, String> urlparms = CSContext.getParms();
		String parms = urlparms.get("parms");
		String where = CjpaUtil.buildFindSqlByJsonParms(pool, sqlstr, parms, null, idpathw);
		String esqlstr = sqlstr;
		if (where != null)
			esqlstr = esqlstr + where;

		String smax = urlparms.get("max");
		int max = (smax == null) ? 300 : Integer.valueOf(smax);
		esqlstr = "select * from (" + esqlstr + ") tbtem where 1=1 limit 0," + max;
		return esqlstr;
	}

	/**
	 * 获取通用查询比较复杂的条件语句
	 * 
	 * @param urlparms
	 * @param jpa
	 * @return
	 * @throws Exception
	 */
	public static String getCommonSearchWhere(HashMap<String, String> urlparms, CJPA jpa) throws Exception {
		String where = "";
		String sqlwhere = urlparms.get("sqlwhere");
		if ((sqlwhere != null) && (sqlwhere.length() > 0))
			where = where + " and " + sqlwhere + " ";

		String edittps = CorUtil.hashMap2Str(urlparms, "edittps");
		if ((edittps != null) && (!edittps.isEmpty())) {
			HashMap<String, String> edts = CJSON.Json2HashMap(edittps);
			if (jpa.cfieldbycfieldname("stat") != null) {
				String sqlstat = "";
				if (Obj2Bl(edts.get("isedit")))
					sqlstat = sqlstat + " (stat=1) or";
				if (Obj2Bl(edts.get("issubmit"))) {
					sqlstat = sqlstat + " (stat>1 and stat<9) or";
					// 去掉 在流程中、当前节点为数据保护节点 且 当前 登录用户不在 当前节点
				}
				if (Obj2Bl(edts.get("isview"))) // 查询所有单据 包含作废的
					sqlstat = sqlstat + " ( 1=1 ) or";
				if (Obj2Bl(edts.get("isupdate")) || Obj2Bl(edts.get("isfind")))
					sqlstat = sqlstat + " (stat=9) or";
				if (sqlstat.length() > 0) {
					sqlstat = sqlstat.substring(1, sqlstat.length() - 2);
					where = where + " and (" + sqlstat + ")";
				}
			}
		}

		String activeprocname = urlparms.get("activeprocname");
		if ((activeprocname != null) && (!activeprocname.isEmpty())) {
			String idfd = jpa.getIDField().getFieldname();
			String ew = "SELECT " + idfd + " FROM " + jpa.tablename + " t,shwwf wf,shwwfproc wfp"
					+ " WHERE t.stat>1 AND t.stat<9 AND t.wfid=wf.wfid AND wf.wfid=wfp.wfid "
					+ "  AND wfp.stat=2 AND wfp.procname='" + activeprocname + "'";
			ew = " and " + idfd + " in (" + ew + ")";
			where = where + ew;
		}
		return where;
	}

	private static boolean Obj2Bl(Object o) {
		if (o == null)
			return false;
		return Boolean.valueOf(o.toString());
	}

}
