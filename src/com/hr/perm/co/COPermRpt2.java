package com.hr.perm.co;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.JSONParm;
import com.corsair.server.base.CSContext;
import com.corsair.server.generic.Shworg;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CReport;
import com.hr.perm.entity.Hr_employee;
import com.hr.util.HRUtil;

@ACO(coname = "web.hr.permrpt2")
public class COPermRpt2 {

	@ACOAction(eventname = "findEmployeeTrainXL", Authentication = true, notes = "人事学历分析")
	public String findEmployeeTrainXL() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String ps = parms.get("parms");
		if (ps == null) {
			throw new Exception("需要参数orgid");
		}
		List<JSONParm> jps = CJSON.getParms(ps);
		JSONParm jp = getParmByName(jps, "orgid");
		if (jp == null) {
			throw new Exception("需要参数orgid");
		}
		String orgid = jp.getParmvalue();
		JSONArray dws = HRUtil.getOrgsByPid(orgid);
		Hr_employee he = new Hr_employee();
		DecimalFormat dec = new DecimalFormat("0.0000");
		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty())
			throw new Exception("ID为【" + orgid + "】的机构不存在");
		dws.add(0, org.toJsonObj());
		for (int i = 0; i < dws.size(); i++) {
			JSONObject dw = dws.getJSONObject(i);
			boolean includechld = (i != 0);
			int sumpeo = getDegreeEmoloyssct(he, dw, null, includechld);// 总人数
			int ss = getDegreeEmoloyssct(he, dw, "1,2", includechld);// 硕士及以上
			int bk = getDegreeEmoloyssct(he, dw, "3", includechld);// 本科
			int dz = getDegreeEmoloyssct(he, dw, "4", includechld);// 大专
			int gz = getDegreeEmoloyssct(he, dw, "5", includechld);// 高中
			int zz =getDegreeEmoloyssct(he, dw, "6", includechld);// 中专
			int zj =getDegreeEmoloyssct(he, dw, "7", includechld);// 中技
			int cz = getDegreeEmoloyssct(he, dw, "8,9,10", includechld);// 高中
			dw.put("sumpeo", sumpeo);
			dw.put("ss", ss);
			dw.put("bk", bk);
			dw.put("dz", dz);
			dw.put("gz", gz);
			dw.put("zz", zz);
			dw.put("zj", zj);
			dw.put("cz", cz);
			dw.put("bss", ((sumpeo > 0) ? (dec.format((float) ss / sumpeo)) : "0.000"));
			dw.put("bbk", ((sumpeo > 0) ? (dec.format((float) bk / sumpeo)) : "0.000"));
			dw.put("bdz", ((sumpeo > 0) ? (dec.format((float) dz / sumpeo)) : "0.000"));
			dw.put("bgz", ((sumpeo > 0) ? (dec.format((float) gz / sumpeo)) : "0.000"));
			dw.put("bcz", ((sumpeo > 0) ? (dec.format((float) cz / sumpeo)) : "0.000"));
			dw.put("bzz", ((sumpeo > 0) ? (dec.format((float) zz / sumpeo)) : "0.000"));
			dw.put("bzj", ((sumpeo > 0) ? (dec.format((float) zj / sumpeo)) : "0.000"));
		}
		String scols = parms.get("cols");
		if (scols == null) {
			return dws.toString();
		} else {
			(new CReport()).export2excel(dws, scols);
			return null;
		}
	}

	private int getDegreeEmoloyssct(Hr_employee he, JSONObject dw, String degree, boolean includchd) throws Exception {
		String sqlstr = "SELECT count(*) ct FROM hr_employee WHERE usable=1 ";
		if ("0".equals(degree))
			sqlstr = sqlstr + " AND degree is null ";
		else if (degree != null)
			sqlstr = sqlstr + " AND degree in(" + degree + ") ";
		sqlstr = sqlstr + " AND empstatid IN(SELECT statvalue FROM hr_employeestat WHERE usable=1 AND statvalue=1) "; 
		if (includchd)
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
		return Integer.valueOf(HRUtil.getReadPool().openSql2List(sqlstr).get(0).get("ct"));
	}

	@ACOAction(eventname = "findEmployeeTrainAge", Authentication = true, notes = "人事年龄分析")
	public String findEmployeeTrainAge() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String ps = parms.get("parms");
		if (ps == null) {
			throw new Exception("需要参数orgid");
		}
		List<JSONParm> jps = CJSON.getParms(ps);
		JSONParm jp = getParmByName(jps, "orgid");
		if (jp == null) {
			throw new Exception("需要参数orgid");
		}
		String orgid = jp.getParmvalue();
		JSONArray dws = HRUtil.getOrgsByPid(orgid);
		Hr_employee he = new Hr_employee();
		DecimalFormat dec = new DecimalFormat("0.0000");
		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty())
			throw new Exception("ID为【" + orgid + "】的机构不存在");
		dws.add(0, org.toJsonObj());
		for (int i = 0; i < dws.size(); i++) {
			JSONObject dw = dws.getJSONObject(i);
			boolean includchd = (i != 0);
			int sumpeo = getAgeEmoloyct(he, dw, -1, -1, includchd);// 总人数
			int f50 = getAgeEmoloyct(he, dw, 50, -1, includchd);// 50岁及以上
			int f40 = getAgeEmoloyct(he, dw, 40, 50, includchd);// 40(含)-50岁
			int f30 = getAgeEmoloyct(he, dw, 30, 40, includchd);// 30(含)-40岁
			int f20 = getAgeEmoloyct(he, dw, 18, 30, includchd);// 20(含)-30岁
			int f00 = getAgeEmoloyct(he, dw, -1, 18, includchd);// 20岁以下
			dw.put("sumpeo", sumpeo);
			dw.put("f50", f50);
			dw.put("f40", f40);
			dw.put("f30", f30);
			dw.put("f20", f20);
			dw.put("f00", f00);
			dw.put("bf50", ((sumpeo > 0) ? (dec.format((float) f50 / sumpeo)) : "0.000"));
			dw.put("bf40", ((sumpeo > 0) ? (dec.format((float) f40 / sumpeo)) : "0.000"));
			dw.put("bf30", ((sumpeo > 0) ? (dec.format((float) f30 / sumpeo)) : "0.000"));
			dw.put("bf20", ((sumpeo > 0) ? (dec.format((float) f20 / sumpeo)) : "0.000"));
			dw.put("bf00", ((sumpeo > 0) ? (dec.format((float) f00 / sumpeo)) : "0.000"));
		}
		String scols = parms.get("cols");
		if (scols == null) {
			return dws.toString();
		} else {
			(new CReport()).export2excel(dws, scols);
			return null;
		}
	}

	// -1 作为条件 包含minAge
	private int getAgeEmoloyct(Hr_employee he, JSONObject dw, int minAge, int MaxAge, boolean includchd) throws Exception {
		String sqlstr = "SELECT COUNT(*) ct FROM ("
				+ " SELECT he.*, (DATE_FORMAT(FROM_DAYS(TO_DAYS(NOW()) - TO_DAYS(birthday)),'%Y') + 0) AS age "
				+ " FROM hr_employee he WHERE usable=1  AND empstatid IN (SELECT statvalue FROM hr_employeestat WHERE usable=1 AND statvalue=1 ) ";
		if (includchd)
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%' ";
		else
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
		sqlstr = sqlstr + ") tb where 1=1 ";
		if (minAge != -1)
			sqlstr = sqlstr + " and tb.age>=" + minAge;
		if (MaxAge != -1)
			sqlstr = sqlstr + " and tb.age<" + MaxAge;
		return Integer.valueOf(HRUtil.getReadPool().openSql2List(sqlstr).get(0).get("ct"));
	}

	@ACOAction(eventname = "findEmployeeTrainSex", Authentication = true, notes = "人事性别分析")
	public String findEmployeeTrainSex() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String ps = parms.get("parms");
		if (ps == null) {
			throw new Exception("需要参数orgid");
		}
		List<JSONParm> jps = CJSON.getParms(ps);
		JSONParm jp = getParmByName(jps, "orgid");
		if (jp == null) {
			throw new Exception("需要参数orgid");
		}
		String orgid = jp.getParmvalue();
		JSONArray dws = HRUtil.getOrgsByPid(orgid);
		Hr_employee he = new Hr_employee();
		DecimalFormat dec = new DecimalFormat("0.0000");
		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty())
			throw new Exception("ID为【" + orgid + "】的机构不存在");
		dws.add(0, org.toJsonObj());
		for (int i = 0; i < dws.size(); i++) {
			JSONObject dw = dws.getJSONObject(i);
			boolean includchd = (i != 0);
			int sumpeo = getDegreeEmoloySexct(he, dw, -1, includchd);// 总人数
			int nan = getDegreeEmoloySexct(he, dw, 1, includchd);// 男
			int nv = getDegreeEmoloySexct(he, dw, 2, includchd);// 女
			dw.put("sumpeo", sumpeo);
			dw.put("nan", nan);
			dw.put("nv", nv);
			dw.put("bnan", ((sumpeo > 0) ? (dec.format((float) nan / sumpeo)) : "0.000"));
			dw.put("bnv", ((sumpeo > 0) ? (dec.format((float) nv / sumpeo)) : "0.000"));

		}
		String scols = parms.get("cols");
		if (scols == null) {
			return dws.toString();
		} else {
			(new CReport()).export2excel(dws, scols);
			return null;
		}
	}

	// sex -1 所有
	private int getDegreeEmoloySexct(Hr_employee he, JSONObject dw, int sex, boolean includchd) throws Exception {
		String sqlstr = "SELECT count(*) ct FROM hr_employee WHERE usable=1 ";
		if (sex != -1)
			sqlstr = sqlstr + " AND sex =" + sex + " ";
		sqlstr = sqlstr + " AND empstatid IN(SELECT statvalue FROM hr_employeestat WHERE usable=1 AND statvalue=1) ";
		if (includchd)
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
		return Integer.valueOf(HRUtil.getReadPool().openSql2List(sqlstr).get(0).get("ct"));
	}
	
	@ACOAction(eventname = "findEmployeeTrainLoss", Authentication = true, notes = "实习生流失率分析")
	public String findEmployeeTrainLoss() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String ps = parms.get("parms");
		if (ps == null) {
			throw new Exception("需要参数orgid");
		}
		List<JSONParm> jps = CJSON.getParms(ps);
		JSONParm jp = getParmByName(jps, "orgid");
		if (jp == null) {
			throw new Exception("需要参数orgid");
		}
		String orgid = jp.getParmvalue();
		JSONArray dws = HRUtil.getOrgsByPid(orgid);
		Hr_employee he = new Hr_employee();
		DecimalFormat dec = new DecimalFormat("0.0000");
		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty())
			throw new Exception("ID为【" + orgid + "】的机构不存在");
		dws.add(0, org.toJsonObj());
		
		for (int i = 0; i < dws.size(); i++) {
			JSONObject dw = dws.getJSONObject(i);
			boolean includchd = (i != 0);
			int ins = getTrainIn(he, dw, includchd);//入职的单据
			int outs = getTrainOut(he, dw, includchd);//离职的单据
			dw.put("ctin", ins);
			dw.put("ctout", outs);
			dw.put("bct", ((ins > 0) ? (dec.format((float) outs / ins)) : "0.000"));
				
		}
		String scols = parms.get("cols");
		if (scols == null) {
			return dws.toString();
		} else {
			(new CReport()).export2excel(dws, scols);
			return null;
		}
	}
	
	private int getTrainIn(Hr_employee he,JSONObject dw, boolean includchd)throws Exception{
		Date d=new Date();
		 String cd=formatdate(getYearFirstDay(d),"yyyy-MM-dd HH:mm:ss");
	     String nd=formatdate(d,"yyyy-MM-dd HH:mm:ss");
        
		String sqlstr = "SELECT COUNT(*) ctin FROM `hr_train_reg` tr  "+
        " WHERE regtype=1 AND stat=9 AND entid=1 AND regdate>='"+cd+"'"+
				" and regdate<='"+nd+"'"; 
		if (includchd)
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
		return Integer.valueOf(HRUtil.getReadPool().openSql2List(sqlstr).get(0).get("ctin"));
	}
	
	private int getTrainOut(Hr_employee he,JSONObject dw, boolean includchd)throws Exception{
		Date d=new Date();
        String cd=formatdate(getYearFirstDay(d),"yyyy-MM-dd HH:mm:ss");
        String nd=formatdate(d,"yyyy-MM-dd HH:mm:ss");
		String sqlstr = "SELECT COUNT(*) ctout FROM hr_leavejob lj "+
	      "WHERE ljtype=1 AND stat=9 AND entid=1 AND ljdate>='"+cd+"'"+
				" and ljdate<='"+nd+"'";
		if (includchd)
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
		return Integer.valueOf(HRUtil.getReadPool().openSql2List(sqlstr).get(0).get("ctout"));
	}
	
	private Date getYearFirstDay(Date d){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
        int year = calendar.get(Calendar.YEAR);
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        Date curday = calendar.getTime();
        return curday;
	}
	
	private String formatdate(Date d,String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(d);
	}
	
	@ACOAction(eventname = "findEmployeeTrainMZ", Authentication = true, notes = "员工民族分析")
	public String findEmployeeTrainMZ() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String ps = parms.get("parms");
		if (ps == null) {
			throw new Exception("需要参数orgid");
		}
		List<JSONParm> jps = CJSON.getParms(ps);
		JSONParm jp = getParmByName(jps, "orgid");
		if (jp == null) {
			throw new Exception("需要参数orgid");
		}
		String orgid = jp.getParmvalue();
		JSONArray dws = HRUtil.getOrgsByPid(orgid);
		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty())
			throw new Exception("ID为【" + orgid + "】的机构不存在");
		dws.add(0, org.toJsonObj());
		Hr_employee he = new Hr_employee();
		DecimalFormat dec = new DecimalFormat("0.0000");
		for (int i = 0; i < dws.size(); i++) {
			boolean includchd = (i != 0);
			JSONObject dw = dws.getJSONObject(i);
			JSONArray mzs = getEmployeeTrainMZ(he, dw, includchd);
			int sumpeo = 0;
			for (int j = 0; j < mzs.size(); j++) {
				JSONObject mz = mzs.getJSONObject(j);
				int ct = mz.getInt("ct");
				sumpeo = sumpeo + ct;
				dw.put("f" + mz.getString("dictvalue"), ct);
			}
			dw.put("sumpeo", sumpeo);
			for (int j = 0; j < mzs.size(); j++) {
				JSONObject mz = mzs.getJSONObject(j);
				if (sumpeo != 0) {
					int ct = mz.getInt("ct");
					dw.put("bf" + mz.getString("dictvalue"), dec.format((float) ct / sumpeo));
				} else {
					dw.put("bf" + mz.getString("dictvalue"), 0);
				}
			}
		}
		String scols = parms.get("cols");
		if (scols == null) {
			return dws.toString();
		} else {
			(new CReport()).export2excel(dws, scols);
			return null;
		}
	}

	private JSONArray getEmployeeTrainMZ(Hr_employee he, JSONObject dw, boolean includchd) throws Exception {
		String sqlstr = "SELECT d.dictvalue,IFNULL(ct,0) ct FROM shwdict d LEFT JOIN "
				+ " (SELECT nation,COUNT(*) ct  FROM ("
				+ " SELECT * FROM hr_employee WHERE usable=1 AND empstatid IN(SELECT statvalue FROM hr_employeestat WHERE usable=1 AND statvalue=1) ";
		if (includchd)
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
		sqlstr = sqlstr + "  ) tb"
				+ " GROUP BY nation) tb1 ON d.dictvalue=tb1.nation"
				+ " WHERE d.pid=797"
				+ " ORDER BY (d.dictvalue+0)";
		return HRUtil.getReadPool().opensql2json_O(sqlstr);
	}
	
	@ACOAction(eventname = "findorgs", Authentication = true, notes = "实习生流失率分析")
	public String findorgs() throws Exception {
		JSONArray dws = HRUtil.getOrgsByType("4");// 董事会 模块 制造群
		return dws.toString();
	}
	
	private JSONParm getParmByName(List<JSONParm> jps, String pname) {
		for (JSONParm jp : jps) {
			if (jp.getParmname().equals(pname))
				return jp;
		}
		return null;
	}

	
	@ACOAction(eventname = "findtrainzyrpt", Authentication = true, notes = "实习生专业分布分析")
	public String findtrainzyrpt() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String ps = parms.get("parms");
		if (ps == null) {
			throw new Exception("需要参数orgid");
		}
		List<JSONParm> jps = CJSON.getParms(ps);
		JSONParm jp = getParmByName(jps, "orgid");
		if (jp == null) {
			throw new Exception("需要参数orgid");
		}
		String orgid = jp.getParmvalue();

		JSONArray dws = HRUtil.getOrgsByPid(orgid);
		Hr_employee he = new Hr_employee();

		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty())
			throw new Exception("ID为【" + orgid + "】的机构不存在");
		dws.add(0, org.toJsonObj());
		// System.out.println("dws:" + dws.toString());
		JSONArray rst = new JSONArray();
		for (int i = 0; i < dws.size(); i++) {
			JSONObject dw = dws.getJSONObject(i);
			JSONArray dwrst = getOrgTrainZYPres(he, dw, (i != 0));
			if (dwrst.size() != 0)
				rst.addAll(dwrst);
		}
		String scols = parms.get("cols");
		if (scols == null) {
			return rst.toString();
		} else {
			(new CReport()).export2excel(rst, scols);
			return null;
		}

	}

	private JSONArray getOrgTrainZYPres(Hr_employee he, JSONObject dw, boolean childs) throws Exception {
		String sqlstr = "SELECT '" + dw.getString("orgname") + "' orgname, major,COUNT(*) ct FROM hr_employee WHERE (empstatid=1 or empstatid=7) "+
	                    " AND er_id IN (SELECT er_id FROM `hr_train_reg` WHERE stat=9 ";
		if (childs)
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
		sqlstr = sqlstr + ") GROUP BY major";
		JSONArray dwrst = HRUtil.getReadPool().opensql2json_O(sqlstr);
		int sumpeo = 0;
		for (int i = 0; i < dwrst.size(); i++) {
			JSONObject to = dwrst.getJSONObject(i);
			sumpeo = sumpeo + to.getInt("ct");
		}
		DecimalFormat dec = new DecimalFormat("0.0000");
		for (int i = 0; i < dwrst.size(); i++) {
			JSONObject to = dwrst.getJSONObject(i);
			to.put("bl", dec.format((float) to.getInt("ct") / sumpeo));
		}
		if (dwrst.size() != 0) {
			JSONObject to = new JSONObject();
			to.put("orgname", dw.getString("orgname"));
			to.put("major", "小计");
			to.put("ct", sumpeo);
			dwrst.add(to);
			return dwrst;
		} else
			return dwrst;
	}
	
	@ACOAction(eventname = "findtrainnorpt", Authentication = true, notes = "实习生需求单位分布分析")
	public String findtrainnorpt() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String ps = parms.get("parms");
		if (ps == null) {
			throw new Exception("需要参数orgid");
		}
		List<JSONParm> jps = CJSON.getParms(ps);
		JSONParm jp = getParmByName(jps, "orgid");
		if (jp == null) {
			throw new Exception("需要参数orgid");
		}
		String orgid = jp.getParmvalue();

		JSONArray dws = HRUtil.getOrgsByPid(orgid);
		Hr_employee he = new Hr_employee();

		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty())
			throw new Exception("ID为【" + orgid + "】的机构不存在");
		dws.add(0, org.toJsonObj());
		// System.out.println("dws:" + dws.toString());
		JSONArray rst = new JSONArray();
		for (int i = 0; i < dws.size(); i++) {
			JSONObject dw = dws.getJSONObject(i);
			JSONArray dwrst = getOrgNeedTrain(he, dw, (i != 0));
			if (dwrst.size() != 0)
				rst.addAll(dwrst);
		}
		String scols = parms.get("cols");
		if (scols == null) {
			return rst.toString();
		} else {
			(new CReport()).export2excel(rst, scols);
			return null;
		}

	}

	private JSONArray getOrgNeedTrain(Hr_employee he, JSONObject dw, boolean childs) throws Exception {
		String sqlstr = "SELECT '" + dw.getString("orgname") + "' orgname,COUNT(*) ct FROM hr_train_reg WHERE stat=9 ";
		if (childs)
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
		sqlstr = sqlstr + " GROUP BY orgid";
		JSONArray dwrst = HRUtil.getReadPool().opensql2json_O(sqlstr);
		int sumpeo = 0;
		for (int i = 0; i < dwrst.size(); i++) {
			JSONObject to = dwrst.getJSONObject(i);
			sumpeo = sumpeo + to.getInt("ct");
		}
		DecimalFormat dec = new DecimalFormat("0.0000");
		for (int i = 0; i < dwrst.size(); i++) {
			JSONObject to = dwrst.getJSONObject(i);
			to.put("bl", dec.format((float) to.getInt("ct") / sumpeo));
		}
		if (dwrst.size() != 0) {
			JSONObject to = new JSONObject();
			to.put("orgname", dw.getString("orgname"));
			to.put("ct", sumpeo);
			dwrst.add(to);
			return dwrst;
		} else
			return dwrst;
	}

}
