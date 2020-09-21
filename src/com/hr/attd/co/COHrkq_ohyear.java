package com.hr.attd.co;

import java.util.Date;
import java.util.HashMap;

import net.sf.json.JSONObject;

import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CorUtil;
import com.hr.attd.entity.Hrkq_ohyear;

@ACO(coname = "web.hrkq.ohyear")
public class COHrkq_ohyear {
	@ACOAction(eventname = "addYearHolidays", Authentication = true, notes = "添加法定年假")
	public String addYearHolidays() throws Exception {
		JSONObject parms = CSContext.parPostData2JSONObject();
		String ohyear = parms.getString("ohyear");
		Date ohdatebg = Systemdate.getDateByStr(parms.getString("ohdatebg"));
		Date ohdateed = Systemdate.getDateByStr(parms.getString("ohdateed"));
		int iswork = Integer.valueOf(parms.getString("iswork"));
		String daydis = parms.getString("daydis");
		String daymeo = parms.getString("daymeo");

		Hrkq_ohyear ho = new Hrkq_ohyear();
		CDBConnection con = ho.pool.getCon(this);
		con.startTrans();
		try {
			Date temdate = ohdatebg;
			while ((temdate.before(ohdateed)) || (temdate.equals(ohdateed))) {
				String dts = Systemdate.getStrDateyyyy_mm_dd(temdate);
				String sqlstr = "select * from hrkq_ohyear where ohdate=str_to_date('" + dts + "','%Y-%m-%d')";
				ho.clear();
				ho.findBySQL(sqlstr, false);
				ho.ohyear.setValue(ohyear);
				ho.ohdate.setValue(dts);
				ho.iswork.setAsInt(iswork);
				ho.daydis.setValue(daydis);
				ho.daymeo.setValue(daymeo);
				ho.save(con);
				temdate = Systemdate.dateDayAdd(temdate, 1);
			}
			con.submit();
			JSONObject rst = new JSONObject();
			rst.put("result", "OK");
			return rst.toString();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	@ACOAction(eventname = "findYearHolidays", Authentication = true, notes = "查询法定年假")
	public String findYearHolidays() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String ohyear = CorUtil.hashMap2Str(parms, "ohyear", "需要参数ohyear");
		String sqlstr = "select * from hrkq_ohyear where ohyear='" + ohyear + "'";
		Hrkq_ohyear ho = new Hrkq_ohyear();
		return ho.pool.opensql2json(sqlstr);
	}

	@ACOAction(eventname = "findMonthHolidays", Authentication = true, notes = "查询月度法定年假")
	public String findMonthHolidays() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String ohmonth = CorUtil.hashMap2Str(parms, "ohmonth", "需要参数ohmonth");
		Date bgdt = Systemdate.getDateByStr(ohmonth + "-01");
		Date eddt = Systemdate.dateMonthAdd(bgdt, 1);

		String sqlstr = "select * from hrkq_ohyear where ohdate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdt)
				+ "' and ohdate<'" + Systemdate.getStrDateyyyy_mm_dd(eddt) + "'";
		Hrkq_ohyear ho = new Hrkq_ohyear();
		return ho.pool.opensql2json(sqlstr);
	}
}
