package com.hr.attd.co;

import java.util.Date;
import java.util.HashMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.generic.Shworg;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CorUtil;
import com.hr.attd.ctr.CtrHrkq_resign;
import com.hr.perm.entity.Hr_employee;
import com.hr.util.TimerTaskHRSwcard;

@ACO(coname = "web.hrkq.swcd")
public class COHrkq_swcdlst {

	// @ACOAction(eventname = "findswcdlst", Authentication = true, notes = "查询打卡记录")
	// public String findswcdlst() {
	// return "";
	// }
	//
	// @ACOAction(eventname = "reqcacalkq", Authentication = true, notes = "考勤计算")
	// public String reqcacalkq() throws Exception {
	// HashMap<String, String> parms = CSContext.getParms();
	// String orgid = CorUtil.hashMap2Str(parms, "orgid", "需要参数orgid");
	//
	// return "";
	// }

	@ACOAction(eventname = "syntxswlist", Authentication = true, notes = "同步指定时间段考勤数据")
	public String syntxswlist() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String sbgdate = CorUtil.hashMap2Str(parms, "bgdate", "需要参数bgdate");
		String seddate = CorUtil.hashMap2Str(parms, "eddate", "需要参数eddate");
		String empcode = CorUtil.hashMap2Str(parms, "empcode");
		Date ftime = Systemdate.getDateByStr(sbgdate);
		Date ttime = Systemdate.dateDayAdd(Systemdate.getDateByStr(seddate), 1);
		TimerTaskHRSwcard.importdata4oldkq(ftime, ttime, empcode);
		JSONObject rst = new JSONObject();
		rst.put("rst", "ok");
		return rst.toString();
	}

	@ACOAction(eventname = "getMonthReSignTimes", Authentication = true, notes = "获取月度签卡数量")
	public String getMonthReSignTimes() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String er_id = CorUtil.hashMap2Str(parms, "er_id", "需要参数er_id");
		Date resdate = Systemdate.getDateByStr(CorUtil.hashMap2Str(parms, "resdate", "需要参数resdate"));
		int res_type = Integer.valueOf(CorUtil.hashMap2Str(parms, "res_type", "需要参数res_type"));
		int rgedtimes = CtrHrkq_resign.getResignTimesMonth(null, res_type, er_id, resdate);
		JSONObject rst = new JSONObject();
		rst.put("rst", rgedtimes);
		return rst.toString();
	}

	// 包括开始时间 结束时间 chdorg包括子机构
	public void calckqorg(String orgid, String yearmonth, boolean chdorg) throws Exception {
		String sqlstr = "select  card_number  from hr_employee where  empstatid IN (SELECT statvalue FROM  hr_employeestat WHERE neetkq =1)"
				+ " and card_number is not null and card_number<>''";
		if (chdorg) {
			Shworg org = new Shworg();
			org.findByID(orgid, false);
			if (org.isEmpty())
				throw new Exception("没发现ID为【" + orgid + "】的机构");
			sqlstr = sqlstr + " and idpath like '" + org.idpath.getValue() + "%' ";
		} else
			sqlstr = sqlstr + " and orgid=" + orgid;
		JSONArray jcds = (new Hr_employee()).pool.opensql2json_O(sqlstr);
		for (int i = 0; i < jcds.size(); i++) {
			JSONObject jcd = jcds.getJSONObject(i);
			calcdqper(jcd.getString("card_number"), yearmonth);
		}
	}

	//
	private void calcdqper(String cardno, String yearmonth) {
		if ((cardno == null) || (cardno.isEmpty()))
			return;
		String sqlstr = "";
	}

}
