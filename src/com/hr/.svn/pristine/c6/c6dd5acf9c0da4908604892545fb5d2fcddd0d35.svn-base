package com.hr.perm.co;

import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.JSONParm;
import com.corsair.server.base.CSContext;
import com.corsair.server.genco.COShwUser;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CorUtil;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_employee_linked;
import com.hr.perm.entity.Hr_train_reg;
import com.hr.perm.entity.Hr_train_regex;

@ACO(coname = "web.hr.trainreg")
public class COHr_train_reg {
	private static String sctfields = "t.treg_id,t.treg_code,t.er_id,t.employee_code,t.regtype,t.regdate,t.enddatetrain,t.enddatetry,t.jxdatetry,"
			+ " t.isfrainflish,t.ospid,t.lv_num,t.norgid,t.norgname,t.nospid,t.nsp_name,t.nlv_num,t.remark,t.wfid,t.stat,t.idpath,t.creator,t.createtime,t.rectname,"
			+ " t.updator,t.updatetime,t.attribute1,t.attribute2,t.attribute3,t.attribute4,t.attribute5,e.nationality,e.bwday,"
			+ " e.id_number, e.card_number,e.employee_name, e.mnemonic_code, e.english_name, e.avatar_id1,e.avatar_id2,e.birthday, e.sex, e.hiredday,"
			+ " e.degree, e.married,e.nativeid, e.nativeplace,e.address, e.nation, e.email, e.empstatid, e.modify, e.usedname,e.pldcp, e.major,"
			+ " e.registertype, e.registeraddress, e.health,e.medicalhistory, e.bloodtype, e.height, e.importway, e.importor,e.cellphone,"
			+ " e.urgencycontact, e.telphone, e.introducer, e.guarantor,e.skill,"
			+ " e.skillfullanguage, e.speciality, e.welfare, e.talentstype,e.orgid, e.orgcode, e.orgname, e.lv_id, "
			+ " e.hg_id,e.hg_code, e.hg_name,  e.ospcode, e.sp_name, e.usable,e.sscurty_addr, e.sscurty_startdate,"
			+ " e.iskey,e.hwc_namezq,e.hwc_namezz,e.degreecheck,e.degreetype,e.idtype,e.sign_date,e.expired_date,"
			+ " e.sign_org,e.entrysourcr,e.dispunit,e.juridical,e.atdtype,e.noclock,e.kqdate_start,"
			+ " t.newstru_id,t.newstru_name,t.newposition_salary,t.newbase_salary,t.newtech_salary,t.newachi_salary,t.newotwage,t.newtech_allowance,t.newpostsubs,"
			+ " t.newchecklev,t.newattendtype,e.shoesize, e.pants_code,e.coat_code, e.dorm_bed, e.pay_way, e.schedtype, e.note, t.attid,e.emnature";

	@ACOAction(eventname = "save", Authentication = true, notes = "保存实习登记单据")
	public String save() throws Exception {
		Hr_employee_linked emp = new Hr_employee_linked();
		Hr_train_reg treg = new Hr_train_reg();
		String psd = CSContext.getPostdata();
		JSONObject oj = JSONObject.fromObject(psd);
		Object oerid = oj.get("er_id");
		String[] disFlds = null;
		if ((oerid != null) && (!oerid.toString().isEmpty())) {
			emp.findByID(oerid.toString());
			if (emp.isEmpty()) {
				throw new Exception("没有找到ID为【" + oerid.toString() + "】的员工档案 ");
			}
			disFlds = new String[] { "idpath", "creator", "createtime", "updator", "updatetime" };
		} else {
			disFlds = new String[] {};
		}
		String[] flds = emp.getFieldNames(disFlds);
		emp.fromjson(psd, flds);
		emp.empstatid.setAsInt(6);// 待入职

		// checkIDNumber(emp);// 检查是否有重复身份证号码 检查在职的
		// 检查岗位年龄

		treg.fromjson(psd);
		CDBConnection con = emp.pool.getCon(this);
		con.startTrans();
		try {
			emp.save(con);
			treg.er_id.setValue(emp.er_id.getValue());
			treg.employee_code.setValue(emp.employee_code.getValue());
			treg.er_id.setValue(emp.er_id.getValue());
			treg.save(con);
			con.submit();
			return findbyid(treg.treg_id.getValue());
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	private void checkIDNumber(Hr_employee_linked emp) throws Exception {
		if (!emp.er_id.isEmpty())
			return;// 已经保存的资料无需判断
		String idnumber = emp.id_number.getValue();
		String sqlstr = "select count(*) ct from hr_employee where id_number='" + idnumber + "'";
		if (Integer.valueOf(emp.pool.openSql2List(sqlstr).get(0).get("ct")) != 0)
			throw new Exception("身份证号码【" + idnumber + "】重复!");
	}

	@ACOAction(eventname = "find", Authentication = true, notes = "查询实习登记单据")
	public String find() throws Exception {
		HashMap<String, String> urlparms = CSContext.getParms();
		String type = CorUtil.hashMap2Str(urlparms, "type", "需要参数type");
		String sqlwhere = urlparms.get("sqlwhere");
		if ("list".equalsIgnoreCase(type) || "tree".equalsIgnoreCase(type)) {
			String parms = urlparms.get("parms");
			String edittps = CorUtil.hashMap2Str(urlparms, "edittps", "需要参数edittps");
			String activeprocname = urlparms.get("activeprocname");

			HashMap<String, String> edts = CJSON.Json2HashMap(edittps);

			String smax = urlparms.get("max");
			int max = (smax == null) ? 300 : Integer.valueOf(smax);
			String order = urlparms.get("order");

			String spage = urlparms.get("page");
			String srows = urlparms.get("rows");

			int page = (spage == null) ? 1 : Integer.valueOf(spage);
			int rows = (srows == null) ? 300 : Integer.valueOf(srows);

			CJPALineData<Hr_train_regex> jpas = new CJPALineData<Hr_train_regex>(Hr_train_regex.class);
			Hr_train_reg enty = new Hr_train_reg();

			List<JSONParm> jps = CJSON.getParms(parms);
			String where = CjpaUtil.buildFindSqlByJsonParms(new Hr_train_regex(), jps);
			// if (dfi && chgi && (userIsInside()) && (jpa.cfield("idpath") !=
			// null)) {
			// where = where + " and idpath like '1,%'";
			// } else if (dfi && )
			if (enty.cfield("idpath") != null)
				where = where + CSContext.getIdpathwhere();
			if ((sqlwhere != null) && (sqlwhere.length() > 0))
				where = where + " and " + sqlwhere + " ";

			// edittps:{"isedit":true,"issubmit":true,"isview":true,"isupdate":false,"isfind":true}
			if (enty.cfieldbycfieldname("stat") != null) {
				String sqlstat = "";
				if (Obj2Bl(edts.get("isedit")))
					sqlstat = sqlstat + " (stat=1) or";
				if (Obj2Bl(edts.get("issubmit"))) {
					sqlstat = sqlstat + " (stat>1 and stat<9) or";
					// 去掉 在流程中、当前节点为数据保护节点 且 当前 登录用户不在 当前节点
				}
				if (Obj2Bl(edts.get("isview")))
					sqlstat = sqlstat + " (stat<=9) or";
				if (Obj2Bl(edts.get("isupdate")) || Obj2Bl(edts.get("isfind")))
					sqlstat = sqlstat + " (stat=9) or";
				if (sqlstat.length() > 0) {
					sqlstat = sqlstat.substring(1, sqlstat.length() - 2);
					where = where + " and (" + sqlstat + ")";
				}
			}
			if ((activeprocname != null) && (!activeprocname.isEmpty())) {
				String idfd = enty.getIDField().getFieldname();
				String ew = "SELECT " + idfd + " FROM " + enty.tablename + " t,shwwf wf,shwwfproc wfp"
						+ " WHERE t.stat>1 AND t.stat<9 AND t.wfid=wf.wfid AND wf.wfid=wfp.wfid "
						+ "  AND wfp.stat=2 AND wfp.procname='" + activeprocname + "'";
				ew = " and " + idfd + " in (" + ew + ")";
				where = where + ew;
			}
			String sqltr = null;

			String textfield = urlparms.get("textfield");

			sqltr = "select * from (SELECT " + sctfields + " FROM hr_train_reg t,hr_employee e WHERE t.er_id=e.er_id) tb where 1=1 " + where;

			if ((order != null) && (!order.isEmpty())) {
				sqltr = sqltr + " order by " + order;
			} else
				sqltr = sqltr + " order by " + enty.getIDFieldName() + " desc ";

			if ("list".equalsIgnoreCase(type)) {
				if ((spage == null) || (srows == null)) {
					jpas.findDataBySQL(sqltr, false, false, 1, max, true);
					return jpas.tojson();
				} else {
					jpas.findDataBySQL(sqltr, false, false, page, rows, true);
					return jpas.tojsonpage();
				}
			}
		}
		// //by id
		if ("byid".equalsIgnoreCase(type)) {
			String id = CorUtil.hashMap2Str(urlparms, "id", "需要参数id");
			return findbyid(id);
		}
		return null;
	}

	private boolean Obj2Bl(Object o) {
		if (o == null)
			return false;
		return Boolean.valueOf(o.toString());
	}

	private String findbyid(String treg_id) throws Exception {
		String sqlstr = "SELECT " + sctfields + " FROM hr_train_reg t,hr_employee e WHERE t.er_id=e.er_id AND t.treg_id=" + treg_id;
		Hr_train_reg treg = new Hr_train_reg();
		JSONArray os = treg.pool.opensql2json_O(sqlstr);
		if (os.size() == 0)
			throw new Exception("没有找到ID为【" + treg_id + "】的实习登记表单");
		JSONObject o = os.getJSONObject(0);
		String er_id = o.getString("er_id");
		o.put("hr_employee_works", treg.pool.opensql2json_O("select * from hr_employee_work where er_id=" + er_id));
		o.put("hr_employee_rewards", treg.pool.opensql2json_O("select * from hr_employee_reward where er_id=" + er_id));
		// o.put("hr_employee_relations", treg.pool.opensql2json_O("select * from hr_employee_relation where er_id=" + er_id));
		o.put("hr_employee_phexas", treg.pool.opensql2json_O("select * from hr_employee_phexa where er_id=" + er_id));
		o.put("hr_employee_leanexps", treg.pool.opensql2json_O("select * from hr_employee_leanexp where er_id=" + er_id));
		o.put("hr_employee_familys", treg.pool.opensql2json_O("select * from hr_employee_family where er_id=" + er_id));
		o.put("hr_employee_cretls", treg.pool.opensql2json_O("select * from hr_employee_cretl where er_id=" + er_id));
		o.put("hr_employee_trainexps", treg.pool.opensql2json_O("select * from hr_employee_trainexp where er_id=" + er_id));
		return o.toString();
	}

	@ACOAction(eventname = "del", Authentication = true, notes = "删除入职单据")
	public String del() throws Exception {
		String id = CSContext.getParms().get("id");
		if ((id == null) || id.isEmpty())
			throw new Exception("需要id参数");
		Hr_train_reg reg = new Hr_train_reg();
		CDBConnection con = reg.pool.getCon(this);
		con.startTrans();
		try {
			reg.findByID4Update(con, id, false);
			if (reg.stat.getAsInt() > 1)
				throw new Exception("非制单状态，不允许删除");
			Hr_employee_linked el = new Hr_employee_linked();
			reg.delete(con, id, true);
			el.delete(con, reg.er_id.getValue(), true);
			con.submit();
			return "{\"result\":\"OK\"}";
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}

	}

	@ACOAction(eventname = "findSXEmoloyeeList", Authentication = true, ispublic = false, notes = "根据登录权限查询实习生资料")
	public String findSXEmoloyeeList() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String eparms = parms.get("parms");
		String empstatid = CorUtil.hashMap2Str(parms, "empstatid", "需要参数empstatid");
		List<JSONParm> jps = CJSON.getParms(eparms);
		Hr_employee he = new Hr_employee();
		String where = CjpaUtil.buildFindSqlByJsonParms(he, jps);

		String sqlstr = "select * from (SELECT " + sctfields + " FROM hr_train_reg t,hr_employee e WHERE t.er_id=e.er_id and e.empstatid="
				+ empstatid + ") tb where 1=1 " + CSContext.getIdpathwhere() + where;
		return he.pool.opensql2json(sqlstr);
		// JSONArray ems = he.pool.opensql2json_O(sqlstr);
		// for (int i = 0; i < ems.size(); i++) {
		// JSONObject em = ems.getJSONObject(i);
		// em.put("extorgname", COShwUser.getOrgNamepath(em.getString("orgid")));
		// }
		// return ems.toString();
	}

}
