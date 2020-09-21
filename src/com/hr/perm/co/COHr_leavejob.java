package com.hr.perm.co;

import java.util.HashMap;
import java.util.List;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CJPASqlUtil;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.JSONParm;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CReport;
import com.corsair.server.util.CorUtil;
import com.hr.perm.entity.Hr_leavejob;
import com.hr.util.HRUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@ACO(coname = "web.hr.leavejob")
public class COHr_leavejob {
	@ACOAction(eventname = "findLeaves2Cancel", Authentication = true, ispublic = false, notes = "根据登录权限查询可撤销的离职表单")
	public String findLeaves2Cancel() throws Exception {
		String sqlstr = "SELECT * FROM hr_leavejob WHERE stat=9 AND iscanced=2";
		String[] notnull = {};
		String[] ignParms = {};// 忽略的查询条件
		return new CReport(sqlstr, null, notnull).findReport(ignParms);

		// HashMap<String, String> parms = CSContext.getParms();
		// String eparms = parms.get("parms");
		// List<JSONParm> jps = CJSON.getParms(eparms);
		// Hr_leavejob lv = new Hr_leavejob();
		// String where = CjpaUtil.buildFindSqlByJsonParms(lv, jps);
		// String sqlstr = "SELECT * FROM hr_leavejob WHERE stat=9 AND
		// iscanced=2" + CSContext.getIdpathwhere() + where;
		// return lv.pool.opensql2json(sqlstr);
	}

	@ACOAction(eventname = "findEmpLastLvj", Authentication = true, ispublic = false, notes = "获取某员工最后离职表单(未撤销)")
	public String findEmpLastLvj() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String er_id = CorUtil.hashMap2Str(parms, "er_id", "需要参数er_id");
		String sqlstr = "SELECT * FROM hr_leavejob" + " WHERE er_id=" + er_id + " AND stat=9 AND iscanced=2 "
				+ " ORDER BY ljid DESC " + " LIMIT 0,1";
		Hr_leavejob lv = new Hr_leavejob();
		return lv.pool.opensql2json(sqlstr);
	}

	@ACOAction(eventname = "findleavelist", Authentication = true, ispublic = true, notes = "替换通用查询")
	public String findleavelist() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String jpaclass = CorUtil.hashMap2Str(urlparms, "jpaclass", "需要参数jpaclass");
		String type = CorUtil.hashMap2Str(urlparms, "type", "需要参数type");

		String disidpath = urlparms.get("disidpath");
		boolean disi = (disidpath != null) ? Boolean.valueOf(disidpath) : false;
		disi = disi || (ConstsSw.getSysParmIntDefault("ALLCLIENTCHGIDPATH", 2) == 2);
		disi = true;// 强制屏蔽idpath
		// System.out.println("dfidpath:" + dfidpath);
		// System.out.println("dfi:" + dfi);
		String sqlwhere = urlparms.get("sqlwhere");
		String selflines = urlparms.get("selfline");
		boolean selfline = (selflines != null) ? Boolean.valueOf(selflines) : true;

		// String chgidpath = urlparms.get("chgidpath");
		// boolean chgi = (chgidpath != null) ? Boolean.valueOf(chgidpath) :
		// false;

		if ("list".equalsIgnoreCase(type) || "tree".equalsIgnoreCase(type)) {
			selfline = false;
			String parms = urlparms.get("parms");
			String edittps = CorUtil.hashMap2Str(urlparms, "edittps", "需要参数edittps");
			String activeprocname = urlparms.get("activeprocname");

			HashMap<String, String> edts = CJSON.Json2HashMap(edittps);

			String smax = urlparms.get("max");
			String order = urlparms.get("order");

			String spage = urlparms.get("page");
			String srows = urlparms.get("rows");
			boolean needpage = false;// 是否需要分页
			int page = 0, rows = 0;
			if (spage == null) {
				if (smax == null) {
					needpage = false;
				} else {
					needpage = true;
					page = 1;
					rows = Integer.valueOf(smax);
				}
			} else {
				needpage = true;
				page = Integer.valueOf(spage);
				rows = (srows == null) ? 300 : Integer.valueOf(srows);
			}

			// CJPALineData<CJPABase> jpas = CjpaUtil.newJPALinedatas(jpaclass);
			CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass);

			List<JSONParm> jps = CJSON.getParms(parms);
			String where = CjpaUtil.buildFindSqlByJsonParms(jpa, jps);
			// if (dfi && chgi && (userIsInside()) && (jpa.cfield("idpath") !=
			// null)) {
			// where = where + " and idpath like '1,%'";
			// } else if (dfi && )
			if ((jpa.cfield("idpath") != null) && (!disi))
				where = where + CSContext.getIdpathwhere();
			if ((sqlwhere != null) && (sqlwhere.length() > 0))
				where = where + " and " + sqlwhere + " ";

			if (jpa.getPublicControllerBase() != null) {
				String w = ((JPAController) jpa.getPublicControllerBase()).OnCCoFindBuildWhere(jpa, urlparms);
				if (w != null)
					where = where + " " + w;
			}

			if (jpa.getController() != null) {
				String w = ((JPAController) jpa.getController()).OnCCoFindBuildWhere(jpa, urlparms);
				if (w != null)
					where = where + " " + w;
			}

			// edittps:{"isedit":true,"issubmit":true,"isview":true,"isupdate":false,"isfind":true}
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
			if ((activeprocname != null) && (!activeprocname.isEmpty())) {
				String idfd = jpa.getIDField().getFieldname();
				String ew = "SELECT " + idfd + " FROM " + jpa.tablename + " t,shwwf wf,shwwfproc wfp"
						+ " WHERE t.stat>1 AND t.stat<9 AND t.wfid=wf.wfid AND wf.wfid=wfp.wfid "
						+ "  AND wfp.stat=2 AND wfp.procname='" + activeprocname + "'";
				ew = " and " + idfd + " in (" + ew + ")";
				where = where + ew;
			}
			String sqltr = null;

			String textfield = urlparms.get("textfield");
			String pidfd = null;
			if ("tree".equalsIgnoreCase(type))
				pidfd = CorUtil.hashMap2Str(urlparms, "parentid", "需要参数parentid");

			if (("tree".equalsIgnoreCase(type)) && (textfield != null) && (textfield.length() > 0)) {
				String idfd = jpa.getIDField().getFieldname();
				sqltr = "select " + idfd + " as id," + textfield + " as text," + idfd + "," + textfield + "," + pidfd
						+ ",a.* from " + jpa.tablename + " a where 1=1 " + where;
			} else {
				String nidpath = null;
				if (!HRUtil.hasRoles("22")) {// 如果不是黑名單管理員
					nidpath = " 0=1 ";
				} else {
					String idp = CSContext.getIdpathwhere();
					nidpath = idp.substring(4, idp.length());
				}

				sqltr = "select ljid,ljcode,ljtype,orgid,orgcode,orgname,er_id,er_code,employee_code,sex,id_number,"
						+ "employee_name,degree,birthday,registeraddress,hiredday,ljappdate,ljdate,kqdate_end,"
						+ "worktime,ljtype1,ljtype2,ljreason,iscpst,if(" + nidpath + ",cpstarm,0) cpstarm,iscpt,isabrt,"
						+ "islawsuit,casenum,isblacklist,"
						+ "addtype,addtype1,blackreason,iscanced,pempstatid,lv_id,lv_num,hg_id,hg_code,hg_name,ospid,"
						+ "ospcode,sp_name,hwc_namezl,remark,wfid,attid,stat,idpath,entid,creator,createtime,updator,"
						+ "updatetime,attribute1,attribute2,attribute3,attribute4,attribute5 " + " from "
						+ jpa.tablename + " where 1=1 " + where;
			}

			if (!HRUtil.hasRoles("2")) {// 不是招募中心人员 只能看没有离职的 或 离职类型不是招募中心自理的
				sqltr = sqltr + " AND ljtype1<>8 ";
			}

			if ((order != null) && (!order.isEmpty())) {
				sqltr = sqltr + " order by " + order;
			} else
				sqltr = sqltr + " order by " + jpa.getIDFieldName() + " desc ";
			if (jpa.getPublicControllerBase() != null) {
				String rst = ((JPAController) jpa.getPublicControllerBase()).OnCCoFindList((Class<CJPA>) jpa.getClass(),
						jps, edts, disi, selfline);
				if (rst != null)
					return rst;
			}
			if (jpa.getController() != null) {
				String rst = ((JPAController) jpa.getController()).OnCCoFindList((Class<CJPA>) jpa.getClass(), jps,
						edts, disi, selfline);
				if (rst != null)
					return rst;
			}
			if ("list".equalsIgnoreCase(type)) {
				String scols = urlparms.get("cols");
				if (scols != null) {
					String[] ignParms = new String[] {};
					new CReport(sqltr, null).export2excel(ignParms, scols);
					return null;
				} else {
					if (!needpage) {
						JSONArray js = jpa.pool.opensql2json_O(sqltr);
						if (jpa.getController() != null) {
							String rst = ((JPAController) jpa.getController())
									.AfterCOFindList((Class<CJPA>) jpa.getClass(), js, 0, 0);
							if (rst != null)
								return rst;
						}
						return js.toString();
					} else {
						JSONObject jo = jpa.pool.opensql2json_O(sqltr, page, rows);
						if (jpa.getController() != null) {
							String rst = ((JPAController) jpa.getController())
									.AfterCOFindList((Class<CJPA>) jpa.getClass(), jo, page, rows);
							if (rst != null)
								return rst;
						}
						return jo.toString();
					}
				}
			}
			if ("tree".equalsIgnoreCase(type)) {
				return jpa.pool.opensql2jsontree(sqltr, jpa.getIDField().getFieldname(), pidfd, false);
			}
		}
		// //by id
		if ("byid".equalsIgnoreCase(type)) {
			String id = CorUtil.hashMap2Str(urlparms, "id", "需要参数id");
			CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass);
			if (jpa.getPublicControllerBase() != null) {
				String rst = ((JPAController) jpa.getPublicControllerBase()).OnCCoFindByID((Class<CJPA>) jpa.getClass(),
						id);
				if (rst != null)
					return rst;
			}
			if (jpa.getController() != null) {
				String rst = ((JPAController) jpa.getController()).OnCCoFindByID((Class<CJPA>) jpa.getClass(), id);
				if (rst != null)
					return rst;
			}

			CField idfd = jpa.getIDField();
			if (idfd == null) {
				throw new Exception("根据ID查询JPA<" + jpa.getClass().getSimpleName() + ">数据没发现ID字段");
			}

			String nidpath = null;
			if (!HRUtil.hasRoles("22")) {// 如果不是黑名單管理員
				nidpath = " 0=1 ";
			} else {
				String idp = CSContext.getIdpathwhere();
				nidpath = idp.substring(4, idp.length());
			}

			String sqlfdname = CJPASqlUtil.getSqlField(jpa.pool.getDbtype(), idfd.getFieldname());
			String sqlvalue = CJPASqlUtil.getSqlValue(jpa.pool.getDbtype(), idfd.getFieldtype(), id);
			String sqlstr = "select ljid,ljcode,ljtype,orgid,orgcode,orgname,er_id,er_code,employee_code,sex,id_number,"
					+ "employee_name,degree,birthday,registeraddress,hiredday,ljappdate,ljdate,kqdate_end,"
					+ "worktime,ljtype1,ljtype2,ljreason,iscpst,if(" + nidpath + ",cpstarm,0) cpstarm,iscpt,isabrt,"
					+ "islawsuit,casenum,isblacklist,"
					+ "addtype,addtype1,blackreason,iscanced,pempstatid,lv_id,lv_num,hg_id,hg_code,hg_name,ospid,"
					+ "ospcode,sp_name,hwc_namezl,remark,wfid,attid,stat,idpath,entid,creator,createtime,updator,"
					+ "updatetime,attribute1,attribute2,attribute3,attribute4,attribute5  from " + jpa.tablename
					+ " where " + sqlfdname + "=" + sqlvalue;
			// if ((chgi) && (userIsInside())) {
			// sqlstr = sqlstr + " and idpath like '1,%'";
			// } else if (dfi && (jpa.cfield("idpath") != null))
			// sqlstr = sqlstr + CSContext.getIdpathwhere();
			jpa.findBySQL(sqlstr, selfline);
			if (jpa.isEmpty())
				return "{}";
			else
				return jpa.tojson();
		}
		return "";
	}

	private static boolean Obj2Bl(Object o) {
		if (o == null)
			return false;
		return Boolean.valueOf(o.toString());
	}

}
