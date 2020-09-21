package com.hr.perm.co;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.text.html.HTMLDocument.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.dbpool.CDBPool;
import com.corsair.dbpool.DBPools;
import com.corsair.server.base.CSContext;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CorUtil;
import com.hr.attd.ctr.CtrHrkq_leave_blance;
import com.hr.base.ctr.CtrHr_systemparms;
import com.hr.perm.entity.Hr_employee;
import com.hr.salary.co.Cosacommon;

@ACO(coname = "web.hr.rptempinfo")
public class COHr_Rptempinfo {

	@ACOAction(eventname = "findEmpExtInfo", Authentication = true, notes = "查询员工扩展信息")
	public String findEmpExtInfo() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String er_id = CorUtil.hashMap2Str(urlparms, "er_id", "需要参数er_id");

		String extinfs = CSContext.getPostdata();
		JSONArray extLins = JSONArray.fromObject(extinfs);
		// 权限判断
		Hr_employee emp = new Hr_employee();
		CDBPool pool = emp.pool;
		emp.findByID(er_id, false);
		if (emp.isEmpty()) {
			throw new Exception("没有找到ID为【" + er_id + "】的资料");
		}
		String id_number = emp.id_number.getValue();
		JSONObject rst = emp.toJsonObj();
		rst.put("workym", CtrHrkq_leave_blance.getworkymString(emp.hiredday.getAsDatetime(), new Date()));// 工龄
		if (!emp.birthday.isEmpty()) {
			rst.put("old", getAge(emp.birthday.getAsDatetime()));// 年龄
		}

		findextinfos(pool, extLins, geterids(pool, id_number), rst);

		return rst.toString();
	}

	// 由出生日期获得年龄
	public int getAge(Date birthDay) throws Exception {
		Calendar cal = Calendar.getInstance();
		if (cal.before(birthDay)) {
			throw new IllegalArgumentException(
					"The birthDay is before Now.It's unbelievable!");
		}
		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH);
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(birthDay);

		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH);
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

		int age = yearNow - yearBirth;

		if (monthNow <= monthBirth) {
			if (monthNow == monthBirth) {
				if (dayOfMonthNow < dayOfMonthBirth)
					age--;
			} else {
				age--;
			}
		}
		return age;
	}

	private String geterids(CDBPool pool, String id_number) throws Exception {
		String rst = "";
		String sqlstr = "SELECT er_id FROM hr_employee WHERE id_number ='" + id_number + "'";
		List<HashMap<String, String>> ids = pool.openSql2List(sqlstr);
		for (HashMap<String, String> id : ids) {
			rst = rst + id.get("er_id") + ",";
		}
		if (!rst.isEmpty())
			rst = rst.substring(0, rst.length() - 1);
		return rst;
	}

	private void findextinfos(CDBPool pool, JSONArray extLins, String erids, JSONObject rst) throws Exception {
		String sqlstr = null;
		for (int i = 0; i < extLins.size(); i++) {
			JSONObject extline = extLins.getJSONObject(i);
			String lname = extline.getString("lname");
			String fds = extline.getString("fds");
			if ((lname == null) || (fds == null) || (lname.isEmpty()) || (fds.isEmpty()))
				continue;

			if (lname.equalsIgnoreCase("hr_employee_work")) {
				sqlstr = "select DISTINCT * from (SELECT " + fds + " FROM hr_employee_work WHERE er_id IN(" + erids + ") ORDER BY start_date desc)tb where 1=1 ";
				rst.put(lname, pool.opensql2json_O(sqlstr));
			}

			if (lname.equalsIgnoreCase("hr_employee_leanexp")) {
				sqlstr = "select DISTINCT * from (SELECT " + fds + " FROM hr_employee_leanexp WHERE er_id IN(" + erids + ") ORDER BY begintime desc)tb where 1=1 ";
				rst.put(lname, pool.opensql2json_O(sqlstr));
			}

			if (lname.equalsIgnoreCase("hr_employee_trainexp")) {
				sqlstr = "select DISTINCT * from (SELECT " + fds + " FROM hr_employee_trainexp WHERE er_id IN(" + erids + ") ORDER BY begintime desc)tb where 1=1 ";
				rst.put(lname, pool.opensql2json_O(sqlstr));
			}

			if (lname.equalsIgnoreCase("hr_employee_trainwk")) {
				sqlstr = "select DISTINCT * from (SELECT " + fds + " FROM hr_employee_trainwk WHERE er_id IN(" + erids + ") ORDER BY begintime desc)tb where 1=1 ";
				rst.put(lname, pool.opensql2json_O(sqlstr));
			}

			if (lname.equalsIgnoreCase("hr_employee_family")) {
				sqlstr = "select DISTINCT * from (SELECT " + fds + " FROM hr_employee_family WHERE er_id IN(" + erids + ") ORDER BY frname)tb where 1=1 ";
				rst.put(lname, pool.opensql2json_O(sqlstr));
			}

			// if (lname.equalsIgnoreCase("hr_employee_relation")) {
			// sqlstr = "SELECT DISTINCT " + fds + " FROM hr_employee_relation WHERE er_id IN(" + erids + ") ORDER BY remployee_name";
			// rst.put(lname, pool.opensql2json_O(sqlstr));
			// }

			if (lname.equalsIgnoreCase("hrrl_declare_nb")) {
				sqlstr = "select DISTINCT * from (SELECT " + fds + " FROM hrrl_declare WHERE  ldtype = 1 AND useable=1 AND stat=9 and er_id IN(" + erids + ") ORDER BY ldid desc)tb where 1=1 ";
				rst.put(lname, pool.opensql2json_O(sqlstr));
			}

			if (lname.equalsIgnoreCase("hrrl_declare_wb")) {
				sqlstr = "select DISTINCT * from (SELECT " + fds + " FROM hrrl_declare WHERE  ldtype in(2,3) AND useable=1 AND stat=9 and er_id IN(" + erids + ") ORDER BY ldid desc)tb where 1=1 ";
				rst.put(lname, pool.opensql2json_O(sqlstr));
			}

			if (lname.equalsIgnoreCase("hr_employee_contract")) {
				sqlstr = "select DISTINCT * from (SELECT " + fds + " FROM hr_employee_contract WHERE er_id IN(" + erids + ") ORDER BY con_id desc)tb where 1=1 ";
				rst.put(lname, pool.opensql2json_O(sqlstr));
			}

			if (lname.equalsIgnoreCase("hr_employee_contract")) {
				sqlstr = "select DISTINCT * from (SELECT " + fds + " FROM hr_employee_contract WHERE er_id IN(" + erids + ") ORDER BY con_id desc)tb where 1=1 ";
				rst.put(lname, pool.opensql2json_O(sqlstr));
			}

			if (lname.equalsIgnoreCase("hr_employee_contract")) {
				sqlstr = "select DISTINCT * from (SELECT " + fds + " FROM hr_employee_contract WHERE er_id IN(" + erids + ") ORDER BY con_id desc)tb where 1=1 ";
				rst.put(lname, pool.opensql2json_O(sqlstr));
			}

			if (lname.equalsIgnoreCase("hr_employee_reward")) {
				sqlstr = "select DISTINCT * from (SELECT " + fds + " FROM hr_employee_reward WHERE er_id IN(" + erids + ") ";
				if (CtrHr_systemparms.getIntValue("ALLFJL") != 1)// 不允许负激励
					sqlstr = sqlstr + " and rwnature=1 ";
				sqlstr = sqlstr + "ORDER BY rewardtime desc)tb where 1=1 ";
				rst.put(lname, pool.opensql2json_O(sqlstr));
			}

			if (lname.equalsIgnoreCase("hr_employee_transferinfo")) {
				sqlstr = String.format(_eti, erids, erids);
				rst.put("hr_employee_transferinfo", pool.opensql2json_O(sqlstr));
				rst.put(lname, pool.opensql2json_O(sqlstr));
			}

			if (lname.equalsIgnoreCase("hr_empptjob_appinfo")) {
				sqlstr = "select DISTINCT * from (SELECT " + fds + " FROM hr_empptjob_app h WHERE h.stat=9 AND h.er_id IN(" + erids + ") order by h.createtime desc)tb where 1=1 ";
				rst.put(lname, pool.opensql2json_O(sqlstr));
			}

			if (lname.equalsIgnoreCase("hr_leavejobinfo")) {
				sqlstr = "select DISTINCT * from (SELECT " + fds + " FROM hr_leavejob h WHERE h.stat=9 AND h.er_id in(" + erids + ") ORDER BY h.createtime DESC)tb where 1=1 ";
				rst.put(lname, pool.opensql2json_O(sqlstr));
			}

			if (lname.equalsIgnoreCase("hr_black_addinfo")) {
				sqlstr = "select DISTINCT * from (SELECT " + fds + " FROM hr_black_add h WHERE h.stat=9 AND h.er_id in(" + erids + ") ORDER BY h.createtime DESC)tb where 1=1 ";
				rst.put(lname, pool.opensql2json_O(sqlstr));
			}

			if (lname.equalsIgnoreCase("hr_black_delinfo")) {
				sqlstr = "select DISTINCT * from (SELECT " + fds + " FROM hr_black_del h WHERE h.stat=9 AND h.er_id in(" + erids + ") ORDER BY h.createtime DESC)tb where 1=1 ";
				rst.put(lname, pool.opensql2json_O(sqlstr));
			}

			// if (Cosacommon.hasaccess4sa()) {
			// if (lname.equalsIgnoreCase("hrsa_chglg")) {
			// sqlstr = "SELECT DISTINCT " + fds + " FROM hrsa_chglg h WHERE h.er_id in(" + erids + ") ORDER BY h.chgdate DESC";
			// rst.put(lname, pool.opensql2json_O(sqlstr));
			// }
			// }

			if (lname.equalsIgnoreCase("hrpm_rstyear")) {
				// sqlstr = "SELECT DISTINCT " + fds + " FROM hrpm_rstyear h WHERE h.er_id in(" + erids + ") ORDER BY h.pmyyear DESC";
				// rst.put(lname, pool.opensql2json_O(sqlstr));
				rst.put(lname, findpmrstyear(pool, erids));
			}

			if (lname.equalsIgnoreCase("hr_emppt_ent_tran_try")) {
				sqlstr = String.format(_ent_tran_try_sql, erids, erids);
				rst.put("hr_emppt_ent_tran_try", pool.opensql2json_O(sqlstr));
				rst.put(lname, pool.opensql2json_O(sqlstr));
			}
		}
	}

	private JSONArray findpmrstyear(CDBPool pool, String erids) throws Exception {
		String sqlstr = "select DISTINCT * from (SELECT pmyear,pmtype FROM hrpm_rstmonthex WHERE er_id IN (" + erids + ") ORDER BY pmyear desc)tb where 1=1 ";
		JSONArray jos = pool.opensql2json_O(sqlstr);
		for (int i = 0; i < jos.size(); i++) {
			JSONObject jo = jos.getJSONObject(i);
			sqlstr = "SELECT * FROM hrpm_rstmonthex WHERE er_id IN (" + erids + ") AND pmyear=" + jo.getString("pmyear")
					+ " and pmtype=" + jo.getString("pmtype") + " ORDER BY pmonth,pmtype";
			JSONArray mjos = pool.opensql2json_O(sqlstr);
			float summ = 0;
			for (int j = 0; j < mjos.size(); j++) {
				JSONObject mjo = mjos.getJSONObject(j);
				float qrst = (mjo.has("qrst")) ? Float.valueOf(mjo.getString("qrst")) : 0;
				summ = summ + qrst;
				jo.put("m" + mjo.getString("pmonth"), qrst);
			}
			float mavg = (mjos.size() == 0) ? 0 : summ / mjos.size();
			jo.put("mavg", String.format("%.2f", mavg));
		}
		return jos;
	}

	private String _eti = "SELECT * FROM ( "
			+ " SELECT '单独调动' trantype,t.tranftype1,t.tranftype2,t.emptranfcode code,t.tranfcmpdate,t.tranfreason,t.probation,"
			+ " t.odorgname,t.odsp_name,t.oldhwc_namezl,t.odhg_name,t.odlv_num,t.odattendtype,t.oldcalsalarytype,t.oldposition_salary,t.oldbase_salary,t.oldtech_salary,t.oldtech_allowance,t.oldavg_salary, "
			+ " t.neworgname,t.newsp_name,t.newhwc_namezl,t.newhg_name,t.newlv_num,t.newattendtype,t.newcalsalarytype,t.newposition_salary,t.newbase_salary,t.newtech_salary,t.newtech_allowance,t.newavg_salary"
			+ " FROM hr_employee_transfer t WHERE t.stat=9 AND t.er_id in (%s)"
			+ " UNION all "
			+ " SELECT '批量调动' trantype,3 tranftype1,1 tranftype2,h.emptranfbcode code,h.tranfcmpdate,h.tranfreason,h.probation,"
			+ " l.odorgname,l.odsp_name,l.oldhwc_namezl,l.odhg_name,l.odlv_num,l.odattendtype,l.oldcalsalarytype,'' oldposition_salary,'' oldbase_salary,'' oldtech_salary,'' oldtech_allowance,'' oldavg_salary,"
			+ " l.neworgname,l.newsp_name,l.newhwc_namezl,l.newhg_name,l.newlv_num,l.newattendtype,l.newcalsalarytype,'' oldposition_salary,'' oldbase_salary,'' oldtech_salary,'' oldtech_allowance,'' oldavg_salary"
			+ " FROM hr_emptransferbatch h,hr_emptransferbatch_line l"
			+ " WHERE h.emptranfb_id=l.emptranfb_id "
			+ " AND h.stat=9 AND er_id in(%s)) t"
			+ " ORDER BY tranfcmpdate DESC";

	private String _ent_tran_try_sql = "SELECT "
			+ "  '调动转正' rowtype,e.hiredday,tt.tranfcmpdate,  tf.tranftype4, tf.tranftype1, tt.probation,tt.probationdate,tt.probationdatetrue,tt.wfresult"
			+ " FROM  hr_transfer_try tt, Hr_employee_transfer tf ,hr_employee e"
			+ " WHERE tf.emptranf_id = tt.emptranf_id "
			+ " AND tf.er_id=e.er_id"
			+ "   AND tf.ispromotioned = 2  AND tf.er_id in(%s)"
			+ " UNION  "
			+ " SELECT "
			+ "   '入职转正' rowtype,tb1.hiredday,NULL tranfcmpdate, NULL tranftype4 ,NULL tranftype1, tb1.probation, tb1.promotionday, IF("
			+ "     tb1.ispromotioned = 1, IFNULL(tb2.wfpbtdate, tb1.promotionday), NULL"
			+ "   ) probationdatetrue, tb2.wfresult "
			+ " FROM"
			+ "   (SELECT "
			+ "     en.er_id, en.entry_id, e.hiredday, en.probation, en.promotionday, en.ispromotioned "
			+ "   FROM  hr_entry en, hr_employee e "
			+ "   WHERE en.er_id = e.er_id "
			+ "     AND e.er_id in(%s) "
			+ "     AND en.stat = 9) tb1 "
			+ "   LEFT JOIN "
			+ "     (SELECT  *   FROM hr_entry_prob ep "
			+ "     WHERE ep.stat = 9) tb2 "
			+ "     ON tb1.entry_id = tb2.sourceid   ";

}
