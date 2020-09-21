package com.hr.recruit.co;

import java.util.HashMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.cjpa.CJPABase;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.PraperedSql;
import com.corsair.server.base.CSContext;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CorUtil;
import com.hr.attd.ctr.HrkqUtil;
import com.hr.perm.entity.Hr_employee;
import com.hr.recruit.entity.Hr_recruit_quachk;
import com.hr.util.HRUtil;

@ACO(coname = "web.hr.Recruit.quachk")
public class COHr_recruit_quachk extends JPAController {

	/**
	 * 保存前
	 * 
	 * @param jpa里面有值
	 * ，还没检测数据完整性，没生成ID CODE 设置默认值
	 * @param con
	 * @param selfLink
	 * @throws Exception
	 */
	@Override
	public void BeforeSave(CJPABase jpa, CDBConnection con, boolean selfLink) throws Exception {
		Hr_recruit_quachk rq = (Hr_recruit_quachk) jpa;
		int age = Integer.valueOf(HrkqUtil.getParmValue("EMPLOYEE_AGE_LMITE"));// 年龄小于*岁不允许入职
		float ag = HRUtil.getAgeByBirth(rq.birthday.getAsDatetime());
		System.out.println("ag:" + ag);
		if (ag < age) {
			rq.compar_result.setValue(2);
			rq.compar_result1.setValue(2);
		} else {
			rq.compar_result1.setValue(1);
		}
	}

	@ACOAction(eventname = "findEmployeeByIDCard", Authentication = true, ispublic = false, notes = "入职按身份证查找重入")
	public String findEmployeeByIDCard() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String id_number = CorUtil.hashMap2Str(parms, "id_number", "id_number参数不能为空");
		String sqlstr = "SELECT t.hiredday,t.id_number,t.employee_name,t.orgname,t.empstatid,DATEDIFF(NOW(), t.ljdate) dates,"
				+ "  t.ljdate,l.ljtype1,l.ljtype2,l.ljreason,l.remark leaveremark, l.ljid,ba.baid, "
				+ " t.sign_org,t.birthday,t.sign_date,t.expired_date,t.sex,t.address,t.nation,"
				+ "  ba.addtype,ba.addtype1,ba.addnum,ba.blackreason,  ba.addappdate,ba.adddate,t.registeraddress "
				+ " FROM hr_employee t  LEFT JOIN hr_leavejob l ON t.er_id = l.er_id AND l.stat=9  LEFT JOIN hr_black_add ba ON l.ljid=ba.ljid "
				+ "  WHERE t.empstatid>=10 and t.id_number =? ORDER BY t.hiredday desc ";
		String[] vs = { id_number };
		PraperedSql psql = new PraperedSql(sqlstr, vs);
		JSONArray rows = DBPools.defaultPool().opensql2json_O(psql);
		int ct = 0, ctadd = 0;
		for (int i = rows.size() - 1; i >= 0; i--) {
			JSONObject row = rows.getJSONObject(i);
			row.put("liidcount", ++ct);
			if (row.has("baid")) {
				String baid = row.getString("baid");
				if ((baid != null) && (!baid.isEmpty())) {
					ctadd++;
				}
			}
			row.put("blidcount", ctadd);
		}
		return rows.toString();
	}

	@ACOAction(eventname = "FindEffectiveEmployee", Authentication = true, ispublic = false, notes = "查询在职人员")
	public String FindEffectiveEmployee() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String id_number = CorUtil.hashMap2Str(parms, "id_number", "id_number参数不能为空");
		String sqlstr = "SELECT IFNULL(COUNT(*),0) ct FROM hr_employee t WHERE t.empstatid NOT IN (0,12,13) AND t.id_number = '" + id_number + "'";
		return DBPools.defaultPool().openrowsql2json(sqlstr);
	}

	@ACOAction(eventname = "findreglist", Authentication = true, ispublic = false, notes = "查询招募登记记录")
	public String findreglist() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String id_number = CorUtil.hashMap2Str(parms, "id_number", "id_number参数不能为空");
		// String sqlstr = "SELECT * FROM hr_employee WHERE empstatid=0 AND
		// id_number=? ORDER BY er_id DESC";
		String sqlstr = "SELECT " + COHr_recruit_form.sctfields
				+ " FROM hr_recruit_form h,hr_employee e WHERE e.empstatid=0 and h.er_id=e.er_id AND e.id_number=?";
		String[] vs = { id_number };
		PraperedSql psql = new PraperedSql(sqlstr, vs);
		return DBPools.defaultPool().opensql2json(psql);
	}

}
