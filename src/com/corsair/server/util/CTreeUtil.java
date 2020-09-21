package com.corsair.server.util;

import com.corsair.cjpa.util.CJPASqlUtil;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.ctrl.OnChangeIDPathLevListener;
import com.corsair.server.ctrl.OnChangeOrgInfoListener;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/*
 * 设置树 idpath  lev
 * */
public class CTreeUtil {
	private static String idpsepchar = ",";

	/**
	 * 不修改根节点IDpath；
	 * 
	 * @param jsons
	 *            数据JSON对象
	 * @param jpa
	 *            一个数据对象 用来获取table field等
	 * @param idfd
	 *            ID字段名
	 * @param phfd
	 *            idpath 字段名
	 * @param lvfd
	 *            级别字段名
	 * @throws Exception
	 */
	public static void reSetIDPathLev(JSONArray jsons, CJPA jpa, String idfd, String phfd, String lvfd) throws Exception {
		CDBConnection con = jpa.pool.getCon(CTreeUtil.class);
		con.startTrans();
		try {
			reSetIDPathLev(con, jsons, jpa, idfd, phfd, lvfd);
			con.submit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		}finally{
			con.close();
		}
	}

	public static void reSetIDPathLev(CDBConnection con, JSONArray jsons, CJPA jpa, String idfd, String phfd, String lvfd) throws Exception {
		chekoption(jpa, idfd, phfd, lvfd);
		for (int i = 0; i < jsons.size(); i++) {
			JSONObject child = jsons.getJSONObject(i);
			reSetIDPathLevNode(con, null, null, child, jpa, idfd, phfd, lvfd);
		}
	}

	// 不修改根节点IDpath；
	public static void reSetIDPathLev(CDBConnection con, OnChangeIDPathLevListener oc, JSONObject json, CJPA jpa, String idfd,
			String phfd, String lvfd)
			throws Exception {
		chekoption(jpa, idfd, phfd, lvfd);
		reSetIDPathLevNode(con, oc, null, json, jpa, idfd, phfd, lvfd);
	}

	// 不修改根节点IDpath；
	public static void reSetIDPathLev(JSONObject json, CJPA jpa, String idfd, String phfd, String lvfd) throws Exception {
		chekoption(jpa, idfd, phfd, lvfd);
		CDBConnection con = jpa.pool.getCon(CTreeUtil.class);
		con.startTrans();
		try {
			reSetIDPathLev(con, null, json, jpa, idfd, phfd, lvfd);
			con.submit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		}finally{
			con.close();
		}

	}

	private static void chekoption(CJPA jpa, String idfd, String phfd, String lvfd) throws Exception {
		if (jpa == null)
			throw new Exception("【jpa】不能为空");
		if ((idfd == null) || (idfd.isEmpty()))
			throw new Exception("【idfd】不能为空");
		if ((phfd == null) || (phfd.isEmpty()))
			throw new Exception("【phfd】不能为空");
	}

	private static void reSetIDPathLevNode(CDBConnection con, OnChangeIDPathLevListener oc, JSONObject pjson, JSONObject json, CJPA jpa, String idfd,
			String phfd,
			String lvfd)
			throws Exception {
		if (pjson != null) {
			String oldp = json.getString(phfd);
			String nidp = pjson.getString(phfd) + json.getString(idfd) + idpsepchar;
			boolean chged = false;
			String sqlstr = "update " + jpa.tablename + " set ";
			if (!nidp.equals(oldp)) {
				json.put(phfd, nidp);// idpath需要记录，后续会用到
				sqlstr = sqlstr + phfd + " = "
						+ CJPASqlUtil.getSqlValue(jpa.pool.getDbtype(), jpa.cfield(phfd).getFieldtype(), nidp);
				chged = true;
			}
			if ((lvfd != null) && (!lvfd.isEmpty())) {
				int nlev = getLevel(nidp);
				int oldlev = json.get(lvfd) == null ? 0 : Integer.valueOf(json.get(lvfd).toString());
				json.put(lvfd, nlev);
				if (nlev != oldlev) {
					if (chged)
						sqlstr = sqlstr + ",";
					sqlstr = sqlstr + lvfd + " = "
							+ CJPASqlUtil.getSqlValue(jpa.pool.getDbtype(), jpa.cfield(lvfd).getFieldtype(), String.valueOf(nlev));
					chged = true;
				}
			}
			if (chged) {
				sqlstr = sqlstr
						+ " where "
						+ jpa.getIDFieldName()
						+ " = "
						+ CJPASqlUtil.getSqlValue(jpa.pool.getDbtype(), jpa.getIDField().getFieldtype(),
								json.getString(jpa.getIDField().getFieldname()));
				con.execsql(sqlstr);
				if (oc != null) {
					oc.OnChange(con, json, pjson);
				}
			}
		}
		if (json.has("children")) {
			JSONArray chlds = json.getJSONArray("children");
			for (int i = 0; i < chlds.size(); i++) {
				JSONObject child = chlds.getJSONObject(i);
				reSetIDPathLevNode(con, oc, json, child, jpa, idfd, phfd, lvfd);
			}
		}
	}

	public static int getLevel(String midpath) {
		int num = 0;
		char[] chars = midpath.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (',' == chars[i]) {
				num++;
			}
		}
		return num;
	}

}
