package com.corsair.server.util;

import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.CDBPool;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.CJSON;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.generic.Shworg;

public class CReport {
	private String sqlstr;
	private String orderby = null;
	private String[] notnull;
	private CDBPool pool = null;

	public CReport() {
	}

	public CReport(String sqlstr, String[] notnull) {
		this.sqlstr = sqlstr;
		this.notnull = notnull;
	}

	public CReport(String sqlstr, String orderby, String[] notnull) {
		this.sqlstr = sqlstr;
		this.orderby = orderby;
		this.notnull = notnull;
	}

	public CReport(CDBPool pool, String sqlstr, String orderby, String[] notnull) {
		this.pool = pool;
		this.sqlstr = sqlstr;
		this.orderby = orderby;
		this.notnull = notnull;
	}

	/**
	 * @param ignParms
	 *            略过的条件
	 * @return
	 * @throws Exception
	 */
	public String findReport(String[] ignParms, String idpathw) throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String scols = urlparms.get("cols");
		if (scols == null) {
			JSONObject rst = findReport2JSON_O(ignParms, true, idpathw);// 查询的数据是分页的 ,所以这里需要返回不分页的SQL语句
			return rst.toString();
		} else {// 导出的数据不分页
			export2excel(ignParms, scols, idpathw);
			return null;
		}
	}

	/**
	 * @return 直接查询，不分页，自动处理idpath
	 * @throws Exception
	 */
	public String findReport() throws Exception {
		return findReport(null, CSContext.getIdpathwhereNoErr());
	}

	/**
	 * 直接查询，不分页，自动处理idpath
	 * 
	 * @param ignParms
	 *            略过的字段 ,不略过 可用 {} 或 null
	 * @return
	 * @throws Exception
	 */
	public String findReport(String[] ignParms) throws Exception {
		return findReport(ignParms, CSContext.getIdpathwhereNoErr());
	}

	public JSONObject findReport2JSON_O() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String scols = urlparms.get("cols");
		boolean paging = (scols == null);
		String[] ignParms = new String[] {};
		return findReport2JSON_O(ignParms, paging);
	}

	public JSONObject findReport2JSON_O(String[] ignParms, boolean paging) throws Exception {
		String idpw = CSContext.getIdpathwhereNoErr();
		return findReport2JSON_O((pool == null) ? DBPools.defaultPool() : pool, ignParms, idpw, paging);
	}

	public JSONObject findReport2JSON_O(String[] ignParms, boolean paging, String idpw) throws Exception {
		return findReport2JSON_O((pool == null) ? DBPools.defaultPool() : pool, ignParms, idpw, paging);
	}

	// public JSONObject findReport2JSON_O(String[] ignParms, String idpw, boolean paging) throws Exception {
	// return findReport2JSON_O(DBPools.defaultPool(), null, idpw, paging);
	// }

	public JSONObject findReport2JSON_O(String[] ignParms) throws Exception {
		String idpw = CSContext.getIdpathwhereNoErr();
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String scols = urlparms.get("cols");
		// System.out.println("3333333333333333333:" + scols);
		boolean paging = (scols == null);
		// System.out.println("22222222222222222222:" + paging);
		return findReport2JSON_O((pool == null) ? DBPools.defaultPool() : pool, ignParms, idpw, paging);
	}

	/**
	 * @param pool
	 * @return
	 * @throws Exception
	 */
	public JSONObject findReport2JSON_O(CDBPool pool) throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String scols = urlparms.get("cols");
		boolean paging = (scols == null);
		String idpw = CSContext.getIdpathwhereNoErr();
		return findReport2JSON_O(pool, null, idpw, paging);
	}

	/**
	 * @param pool
	 * @param ignParms
	 * @param idpw
	 *            传入的idpath的条件将替换默认的idpath，传“” null 一样会屏蔽默认处理
	 * @param paging
	 *            true 分页 false 不分页
	 * @return
	 * @throws Exception
	 */
	public JSONObject findReport2JSON_O(CDBPool pool, String[] ignParms, String idpw, boolean paging) throws Exception {
		return findReport2JSON_O(pool, ignParms, idpw, paging, false);
	}

	/**
	 * @param pool
	 * @param ignParms
	 * @param idpw
	 *            传入的idpath的条件将替换默认的idpath，传“” null 一样会屏蔽默认处理
	 * @param paging
	 *            true 分页 false 不分页
	 * @return
	 * @throws Exception
	 */
	public JSONObject findReport2JSON_O(CDBPool pool, String[] ignParms, String idpw, boolean paging, boolean withMetaData) throws Exception {
		// System.out.println("11111111111111111111111111111111111111111111111111111");
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String parms = urlparms.get("parms");
		String where = CjpaUtil.buildFindSqlByJsonParms(pool, sqlstr, parms, notnull, ignParms, idpw);
		String spage = urlparms.get("page");
		String srows = urlparms.get("rows");
		int page = (spage == null) ? 1 : Integer.valueOf(spage);
		int rows = (srows == null) ? 300 : Integer.valueOf(srows);

		if ((!paging) || (spage == null)) {// 不分页
			page = -1;
			String smax = urlparms.get("max");
			if ((smax != null) && (!smax.isEmpty())) {
				int max = Integer.valueOf(smax);
				if (max > 0) {
					page = 1;
					rows = max;
				}
			}
		}
		// String scols = urlparms.get("cols");
		sqlstr = "select * from (" + sqlstr + ") tb where 1=1 " + where;
		if (orderby != null) {
			sqlstr = sqlstr + " order by " + orderby;
		}
		return pool.opensql2json_O(sqlstr, page, rows, withMetaData);
	}

	private static JSONArray getTrueCol(String scols) {
		// System.out.println("scols:" + scols);
		JSONArray cols = new JSONArray();
		JSONArray tcols = JSONArray.fromObject(scols);
		for (int i = 0; i < tcols.size(); i++) {
			Object o = tcols.get(i);
			if (o instanceof JSONArray) {
				JSONArray os = (JSONArray) o;
				for (int j = 0; j < os.size(); j++) {
					Object to = os.get(j);
					if (to instanceof JSONObject) {
						JSONArray tmpo = (JSONArray) o;
						// ((JSONObject) to).put("title", )
						if (((JSONObject) to).has("field"))
							cols.add(to);
					}
				}
			}
			if (o instanceof JSONObject) {
				if (((JSONObject) o).has("field"))
					cols.add(o);
			}
		}
		return cols;
	}

	public void export2excel(JSONArray jdatas, String scols) throws Exception {
		JSONArray cols = JSONArray.fromObject(scols);
		// System.out.println("cols:" + cols.toString());
		HttpServletResponse rsp = CSContext.getResponse();
		rsp.setContentType("application/text");
		rsp.setHeader("content-disposition", "attachment; filename=" + getxlsfnane());
		OutputStream os = rsp.getOutputStream();
		try {
			CExcel.expByJSCols(jdatas, cols, os); // .expByCols(jdatas, cols, os);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
			os.flush();
			os.close();
		}
	}

	private static String getxlsfnane() {
		String rf = ConstsSw._root_filepath;
		if ((rf == null) || (rf.isEmpty())) {
			return "corsair.xlsx";
		} else {
			String fsep = System.getProperty("file.separator");
			rf = rf.replace("\\", fsep);
			rf = rf.replace("/", fsep);
			String f = (new File(rf.trim())).getName();
			if ((f == null) || (f.isEmpty()))
				return "corsair.xlsx";
			else
				return f + ".xlsx";
		}
	}

	public void export2excel(String[] ignParms, String scols, String idpw) throws Exception {
		export2excel(DBPools.defaultPool(), ignParms, scols, idpw);
	}

	public void export2excel(CDBPool pool, String[] ignParms, String scols, String idpw) throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String parms = urlparms.get("parms");
		String where = CjpaUtil.buildFindSqlByJsonParms((pool == null) ? DBPools.defaultPool() : pool, sqlstr, parms, notnull, ignParms, idpw);
		sqlstr = "select * from (" + sqlstr + ") tb where 1=1 " + where;
		// if (orderby != null) {
		// sqlstr = sqlstr + " order by " + orderby;
		// }
		JSONArray cols = JSONArray.fromObject(scols);
		// JSONArray cols = getTrueCol(scols);
		// List<HashMap<String, String>> rcols = CJSON.parArrJson(cols.toString());
		HttpServletResponse rsp = CSContext.getResponse();
		rsp.setContentType("application/text");
		rsp.setHeader("content-disposition", "attachment; filename=" + getxlsfnane());
		OutputStream os = rsp.getOutputStream();
		try {
			CExcel.expLargeByCols(pool, sqlstr, orderby, cols, os);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
			os.flush();
			os.close();
		}
	}

	public void export2excel(String[] ignParms, String scols) throws Exception {
		String idpw = CSContext.getIdpathwhereNoErr();
		export2excel(ignParms, scols, idpw);
	}
}
