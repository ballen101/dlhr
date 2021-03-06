package com.hr.attd.ctr;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.JSONParm;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.generic.Shworg;
import com.corsair.server.util.CReport;
import com.hr.attd.entity.Hrkq_business_trip;
import com.hr.attd.entity.Hrkq_holidayapp;
import com.hr.attd.entity.Hrkq_onduty;
import com.hr.attd.entity.Hrkq_overtime;
import com.hr.attd.entity.Hrkq_overtime_list;
import com.hr.attd.entity.Hrkq_parms;
import com.hr.attd.entity.Hrkq_swcdlst;
import com.hr.attd.entity.Hrkq_wkoff;
import com.hr.attd.entity.Hrkq_workschmonthlist;
import com.hr.perm.entity.Hr_leavejob;
import com.hr.util.DateUtil;
import com.hr.util.HRUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class HrkqUtil {
	private static CJPALineData<Hrkq_parms> parms = new CJPALineData<Hrkq_parms>(Hrkq_parms.class);

	public static String getParmValue(String parmcode) throws Exception {
		if (parmcode == null)
			return null;
		if (parms.size() == 0) {
			String sqlstr = "select * from hrkq_parms"; 
			parms.findDataBySQL(sqlstr);
		}
		for (CJPABase jpa : parms) {
			Hrkq_parms parm = (Hrkq_parms) jpa;
			if (parmcode.equalsIgnoreCase(parm.parmcode.getValue())) {
				return parm.parmvalue.getValue();
			}
		}
		return null;
	}

	public static String getParmValueErr(String parmcode) throws Exception {
		String rst = getParmValue(parmcode);
		if (rst == null) {
			throw new Exception("没有发现编码为【" + parmcode + "】的考勤参数");
		} else
			return rst;
	}

	/**
	 * 全局检查时间交叉
	 * 
	 * @param stype 原单类型 1 调休（hrkq_wkoff） 2加班 3值班（hrkq_onduty） 4 出差(stype) 5
	 * 请假（hrkq_holidayapp）
	 * @param sid 原单ID
	 * @param time1 开始时间
	 * @param time2 截止时间
	 * @throws Exception
	 */
	public static void checkAllOverlapDatetime(int stype, String sid, String er_id, String employee_name, Date time1,
			Date time2) throws Exception {
		String strtime1 = Systemdate.getStrDate(time1);
		String strtime2 = Systemdate.getStrDate(time2);
		// 调休
		String sqlstr = "SELECT * FROM hrkq_wkoff WHERE er_id=" + er_id + " AND stat>1 AND stat<10 "
				+ " AND ("
				+ "  ((begin_date>='" + strtime1 + "') AND (begin_date<='" + strtime2 + "'))"
				+ " or ((begin_date<='" + strtime1 + "') and (end_date>='" + strtime2 + "'))"
				+ " or ((end_date>='" + strtime1 + "') and (end_date<='" + strtime2 + "'))"
				+ ")";
		if (stype == 1) {
			sqlstr = sqlstr + " and hrkq_wkoff.woid<>" + sid;
		}
		Hrkq_wkoff temwo = new Hrkq_wkoff();
		temwo.findBySQL(sqlstr, false);
		if (!temwo.isEmpty())
			throw new Exception("员工【" + employee_name + "】与已提交的调休申请【" + temwo.wocode.getValue() + "】时间交叉错误!");

		// 加班
		sqlstr = "SELECT o.* FROM hrkq_overtime o,hrkq_overtime_line l,hrkq_overtime_hour h "
				+ " WHERE o.ot_id=l.ot_id AND l.otl_id=h.otl_id AND o.stat>1 AND o.stat<=9 AND l.er_id=" + er_id
				+ " AND ("
				+ "  ((h.begin_date>='" + strtime1 + "') AND (h.begin_date<='" + strtime2 + "'))"
				+ " or ((h.begin_date<='" + strtime1 + "') and (h.end_date>='" + strtime2 + "'))"
				+ " or ((h.end_date>='" + strtime1 + "') and (h.end_date<='" + strtime2 + "'))"
				+ ")";
		if (stype == 2) {
			sqlstr = sqlstr + " and o.ot_id<>" + sid;
		}
		Hrkq_overtime ot = new Hrkq_overtime();
		ot.findBySQL(sqlstr, false);
		if (!ot.isEmpty())
			throw new Exception("员工【" + employee_name + "】与已经完成的加班申请【" + ot.ot_code.getValue() + "】时间交叉错误!");

		// 值班
		sqlstr = "SELECT o.* FROM hrkq_onduty o,hrkq_ondutyline l "
				+ " WHERE o.od_id=l.od_id AND o.stat>1 AND o.stat<=9 AND o.er_id= " + er_id
				+ " AND ("
				+ "  ((l.begin_date>='" + strtime1 + "') AND (l.begin_date<='" + strtime2 + "'))"
				+ " or ((l.begin_date<='" + strtime1 + "') and (l.end_date>='" + strtime2 + "'))"
				+ " or ((l.end_date>='" + strtime1 + "') and (l.end_date<='" + strtime2 + "'))"
				+ ")";
		if (stype == 3) {
			sqlstr = sqlstr + " AND l.odlid<>" + sid;// 不包含自己
		}
		Hrkq_onduty odnew = new Hrkq_onduty();
		odnew.findBySQL(sqlstr, false);
		if (!odnew.isEmpty())
			throw new Exception("员工【" + employee_name + "】与已提交的值班申请【" + odnew.od_code.getValue() + "】时间交叉错误!");

		sqlstr = "SELECT * FROM Hrkq_business_trip t WHERE t.stat>1 AND t.stat<=9 AND t.er_id=" + er_id
				+ " AND ("
				+ "  ((t.begin_date>='" + strtime1 + "') AND (t.begin_date<='" + strtime2 + "'))"
				+ " or ((t.begin_date<='" + strtime1 + "') and (t.end_date>='" + strtime2 + "'))"
				+ " or ((t.end_date>='" + strtime1 + "') and (t.end_date<='" + strtime2 + "'))"
				+ ")";
		// 出差
		if (stype == 4) {
			sqlstr = sqlstr + " and t.bta_id<>" + sid;
		}
		Hrkq_business_trip bt = new Hrkq_business_trip();
		bt.findBySQL(sqlstr, false);
		if (!bt.isEmpty())
			throw new Exception("员工【" + employee_name + "】与已提交的出差申请【" + bt.bta_code.getValue() + "】时间交叉错误!");

		// 请假
		sqlstr = "SELECT * FROM hrkq_holidayapp t WHERE t.stat>1 AND t.stat<=9 AND t.er_id=" + er_id
				+ " AND ("
				+ "  ((t.timebg>='" + strtime1 + "') AND (t.timebg<='" + strtime2 + "'))"
				+ " or ((t.timebg<='" + strtime1 + "') and (t.timeedtrue>='" + strtime2 + "'))"
				+ " or ((t.timeedtrue>='" + strtime1 + "') and (t.timeedtrue<='" + strtime2 + "'))"
				+ ")";
		if (stype == 5) {
			sqlstr = sqlstr + " and t.haid<>" + sid;
		}
		Hrkq_holidayapp hp = new Hrkq_holidayapp();
		hp.findBySQL(sqlstr, false);
		if (!hp.isEmpty())
			throw new Exception("员工【" + employee_name + "】与已提交的请假申请【" + hp.hacode.getValue() + "】时间交叉错误!");
	}

	/**
	 * 检查给出时间是否允许做考勤业务, 应对这个需求： 次月2号以后就不能再补提上月的考勤单据（调休、请假、出差、加班、值班）且
	 * 2号（包括）之前不能提起上个月及之前的单据；
	 * 
	 * @param date 业务时间
	 * @throws Exception
	 */
	public static void checkValidDate(Date bdate) throws Exception {
		// System.out.println("do_checkValidDate：" + CSContext.isAdminNoErr());
		Logsw.dblog("test1111");
		// if (CSContext.isAdminNoErr())// 管理员不控制
		// return;
		// 检查登录用户角色
		System.out.print("是否包含58角色："+HRUtil.hasRoles("58"));
		if (HRUtil.hasRoles("58"))// 单据维护员 和 管理员
			return;

		int days = Integer.valueOf(HrkqUtil.getParmValue("KQ_ALLOW_BSDAY"));// 次月X号以后就不能再补提上月的考勤单据（调休、请假、出差）
		if (days > 0)
		{
			Date m01 = Systemdate.getDateByStr(Systemdate.getStrDateByFmt(new Date(), "yyyy-MM-01"));// 本月1号
			int day = Integer.valueOf(Systemdate.getStrDateByFmt(new Date(), "dd"));// 当前日
			// System.out.println("day:" + day);
			// System.out.println(days);
			if (day <= days) {// 判断是否上上个月
				Date pm01 = Systemdate.dateMonthAdd(m01, -1);
				if (bdate.getTime() < pm01.getTime()) {
					throw new Exception("不能提交【" + Systemdate.getStrDateByFmt(pm01, "yyyy-MM-dd") + "】之前的表单");
				}
			} else {
				// System.out.println("bdate:" + Systemdate.getStrDate(bdate));
				// System.out.println("m01:" + Systemdate.getStrDate(m01));
				if (bdate.getTime() < m01.getTime()) {
					throw new Exception(
							"本月【" + days + "】号以后不能提交【" + Systemdate.getStrDateByFmt(m01, "yyyy-MM-dd") + "】之前的表单");
				}
			}
		}
		//create by czq2020-4-23 控制单据只能提交X天前的
		long advanceDays = Long.valueOf(HrkqUtil.getParmValue("KQ_ADVANCE_BSDAY"));
		if(advanceDays>0){	
			long betweenDays= DateUtil.getBetweenDays(bdate, new Date());
			Logsw.dblog("advanceDays="+advanceDays+"betweenDays="+betweenDays);
			if(betweenDays>advanceDays){
				throw new Exception("不能提交【"+advanceDays+"】天前的表单");
			}
		}
	}

	/**
	 * 将打卡记录同步到历史表中 每3个月一张表
	 * 
	 * @throws Exception
	 */
	public static void backAndMoveKQSWCT() throws Exception {
		String tbmonth = Systemdate.getStrDateByFmt(Systemdate.dateMonthAdd(new Date(), -3), "yyMM");
		String tbname = "hrkq_swcdlst_his" + tbmonth;
		String ctsqlstr = "CREATE TABLE IF NOT EXISTS  `" + tbname + "` (" + "  `skid` int(15) NOT NULL COMMENT 'ID',"
				+ "  `machineno` int(6) DEFAULT NULL COMMENT '机器号'," + "  `readerno` int(2) DEFAULT NULL COMMENT '读头',"
				+ "  `empno` varchar(10) NOT NULL COMMENT '工号',"
				+ "  `card_number` varchar(6) DEFAULT NULL COMMENT '卡号',"
				+ "  `skdate` datetime NOT NULL COMMENT 'yyyy-MM-dd hh:mm:ss',"
				+ "  `sktype` int(1) DEFAULT '1' COMMENT '1 刷卡 2 签卡',"
				+ "  `synid` bigint(20) NOT NULL DEFAULT '1' COMMENT '旧ID 同步条件',"
				+ "  `machno1` varchar(256) DEFAULT NULL COMMENT '打卡位置'"
				+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COMMENT='打卡记录历史表';";
		Hrkq_swcdlst sw = new Hrkq_swcdlst();
		CDBConnection con = sw.pool.getCon("backAndMoveKQSWCT");
		try {
			con.execsql(ctsqlstr);
			con.execsql("INSERT INTO " + tbname
					+ " (SELECT * FROM hrkq_swcdlst WHERE skdate<DATE_SUB(CURDATE(),INTERVAL 3 MONTH))");
			con.execsql("DELETE FROM hrkq_swcdlst WHERE skdate<DATE_SUB(CURDATE(),INTERVAL 3 MONTH)");
		} finally {
			con.close();
		}
	}

	/**
	 * 
	 * @param orgcode 部门编码
	 * @param dqdate  考勤月
	 * @param empIds  要查询的员工
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getkqorgmonthrptdetail(String orgcode,String dqdate,List<String> empIdsList) throws Exception {
		boolean edk = ConstsSw.getSysParmIntDefault("ENTRYDAYKQ", 1) == 1;// 入职当天计算考勤 1是 2否
		boolean lvk = ConstsSw.getSysParmIntDefault("LEAVDAYKQ", 1) == 1;// 离职当天计算考勤 1是 2否
		Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqdate)));// 去除时分秒
		Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
		String yearmonth = Systemdate.getStrDateByFmt(Systemdate.getDateByStr(dqdate), "yyyy-MM");
		String sqlstr = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		//		if(empIds==null){
		//			sqlstr = "SELECT * FROM hr_employee WHERE empstatid>0 and (empstatid NOT IN(6,11,12,13) OR(empstatid IN(11,12,13) AND kqdate_end>='" + yearmonth
		//					+ "-01'))" + " and idpath like '" + org.idpath.getValue() + "%' and kqdate_start<'" + Systemdate.getStrDateByFmt(eddate, "yyyy-MM-dd") + "'";
		//		}else{
		//			sqlstr = "SELECT * FROM hr_employee WHERE er_id in ("+empIds+") and  empstatid>0 and (empstatid NOT IN(6,11,12,13) OR(empstatid IN(11,12,13) AND kqdate_end>='" + yearmonth
		//					+ "-01'))" + " and idpath like '" + org.idpath.getValue() + "%' and kqdate_start<'" + Systemdate.getStrDateByFmt(eddate, "yyyy-MM-dd") + "'";
		//		}
		String empIdsString="";
		//List<String> allEmpIds=findEmployeeByZz(org.idpath.getValue(),yearmonth);
		//		if(empIdsList!=null && empIdsList.size()>0){
		//			//筛选有效数据
		//			for(String id : empIdsList){
		//				if(!allEmpIds.contains(id)){
		//					empIdsList.remove(id);
		//				}
		//			}
		//			empIds=HRUtil.tranInSql(empIdsList);
		//		}else{
		//			empIds=HRUtil.tranInSql(allEmpIds);
		//		}
		//if(empIds.equals(""))	throw new Exception("编码为【" + orgcode + "】的机构"+yearmonth+"没有考勤记录");
		if(empIdsList!=null && empIdsList.size()>0){
			empIdsString=" and a.er_id in ("+HRUtil.tranInSql(empIdsList)+")";
		}
		
		sqlstr = "select a.er_id,a.er_code,a.employee_code,a.id_number,a.sign_org,a.sign_date,a.expired_date,a.card_number,"+
				"a.employee_name,a.mnemonic_code,a.english_name,a.avatar_id1,a.avatar_id2,a.birthday,"+
				"a.sex,a.bwday,a.bwyear,a.hiredday,a.ljdate,a.degree,a.degreetype,a.degreecheck,"+
				"a.married,a.nationality,a.nativeid,a.nativeplace,a.address,a.nation,a.email,a.empstatid,a.modify,"+
				"a.usedname,a.pldcp,a.major,a.registertype,a.registeraddress,a.health,a.medicalhistory,a.bloodtype,a.height,a.importway,a.importor,a.cellphone,a.urgencycontact,a.urmail,"+
				"a.telphone,a.introducer,a.guarantor,a.skill,a.skillfullanguage,a.speciality,a.welfare,a.talentstype,a.emnature,a.orgid,a.orgcode,a.orgname,a.uorgid,a.uorgcode,a.uorgname,a.uidpath,a.hwc_namezl,"+
				"a.lv_id,a.lv_num,a.hg_id,a.hg_code,a.hg_name,a.ospid,a.ospcode,"+
				"a.sp_name,a.iskey,a.hwc_namezq,a.hwc_namezz,a.usable,a.sscurty_addr,a.sscurty_startdate,"+
				"a.sscurty_enddate,a.shoesize,a.pants_code,a.coat_code,a.needdrom,a.dorm_bed,a.pay_way,a.schedtype,"+
				"a.atdtype,a.resigntimes,a.wfbusyss,a.noclock,a.promotionday,"+
				"a.entrysourcr,a.rectcode,a.rectname,a.eovertype,a.mlev,"+
				"a.atmid,a.mdname,a.advtch_subsidy,a.isadvtch,a.attid,a.entid,a.insurancestat,"+
				"a.dispunit,a.dispeextime,b.kqdate_start,a.kqdate_end"+
				" from hr_month_employee a INNER JOIN hr_employee  b on a.employee_code=b.employee_code WHERE a.yearmonth='"+yearmonth+"'  and  a.idpath like '"+org.idpath.getValue()+"%' "+empIdsString+" and  a.er_id in (select er_id from hrkq_workschmonthlist where yearmonth='"+yearmonth+"')"+
				" union"+
				" select er_id,er_code,employee_code,id_number,sign_org,sign_date,expired_date,card_number,"+
				"employee_name,mnemonic_code,english_name,avatar_id1,avatar_id2,birthday,"+
				"sex,bwday,bwyear,hiredday,ljdate,degree,degreetype,degreecheck,"+
				"married,nationality,nativeid,nativeplace,address,nation,email,empstatid,modify,"+
				"usedname,pldcp,major,registertype,registeraddress,health,medicalhistory,bloodtype,height,importway,importor,cellphone,urgencycontact,urmail,"+
				"telphone,introducer,guarantor,skill,skillfullanguage,speciality,welfare,talentstype,emnature,orgid,orgcode,orgname,uorgid,uorgcode,uorgname,uidpath,hwc_namezl,"+
				"lv_id,lv_num,hg_id,hg_code,hg_name,ospid,ospcode,"+
				"sp_name,iskey,hwc_namezq,hwc_namezz,usable,sscurty_addr,sscurty_startdate,"+
				"sscurty_enddate,shoesize,pants_code,coat_code,needdrom,dorm_bed,pay_way,schedtype,"+
				"atdtype,resigntimes,wfbusyss,noclock,promotionday,"+
				"entrysourcr,rectcode,rectname,eovertype,mlev,"+
				"atmid,mdname,advtch_subsidy,isadvtch,attid,entid,insurancestat,"+
				"dispunit,dispeextime,kqdate_start,kqdate_end"+
				" from hr_employee where idpath like '"+org.idpath.getValue()+"%'  AND DATE_FORMAT(kqdate_end,'%Y-%m')='"+yearmonth+"' "+empIdsString.replace("a.", "")+" and  er_id in (select er_id from hrkq_workschmonthlist where yearmonth='"+yearmonth+"')";
		String[] ignParms = { "orgcode"};
		//String scols = urlparms.get("cols");
		JSONObject rst = new CReport(HRUtil.getReadPool(), sqlstr, null, null).findReport2JSON_O(ignParms);
		JSONArray emps = rst.getJSONArray("rows");
		for (int i = 0; i < emps.size(); i++) {
			JSONObject jo = emps.getJSONObject(i);
			if(empIdsList!=null && empIdsList.size()>0){
				//筛选有效数据
				if(!empIdsList.contains(jo.getString("er_id")))continue;
			}
			jo.put("zwxz", jo.has("emnature") ? jo.getString("emnature") : "");
			mq(jo, yearmonth, bgdate, eddate);// 满勤 及 法定假
			getHolidayEmpHour(jo, yearmonth);// 假期
			getOvertimeEmpHours(jo, bgdate, eddate);// 加班
			getKGCDZTDays(jo, bgdate, eddate, edk, lvk);// 旷工天数 迟到 早退次数
			// 实际出勤(不包含请假)；
			// 离职前、入职后、当前时间前
			rptresigntime(jo, bgdate, eddate);// 补签
			rptlvinfo(jo,bgdate, eddate);// 离职类型
			jo.put("nightworkdays", getNightDays(jo, bgdate, eddate, false));// 夜班上班天数
			
			float sjcq = (float) jo.getDouble("sjcq");
			sjcq = (float) (sjcq + jo.getDouble("gj") + jo.getDouble("hj")+ jo.getDouble("snj") + jo.getDouble("gsj") + jo.getDouble("fdjq"));
			if (sjcq < 0)sjcq = 0;
			float ycmq = (float) jo.getDouble("ycmq"); // 如果实际出勤大于应出满勤 ，则修改为应出满勤
			sjcq = (sjcq > ycmq) ? ycmq : sjcq;
			jo.put("sjcq", sjcq);// 实际出勤
			/*根据实际情况调整数据*/
			Logsw.dblog("实际出勤111sjcq="+jo.getString("sjcq"));
		}
		return rst;
		//		if (scols == null) {
		//			return rst.toString();
		//		} else {
		//			(new CReport()).export2excel(rst.getJSONArray("rows"), scols);
		//			return null;
		//		}

	}


	/**
	 * 获取满勤天数
	 * 
	 * @param jo
	 * @throws Exception
	 */
	private static void mq(JSONObject jo, String yearmonth, Date bgdate, Date eddate) throws Exception {
		Hrkq_workschmonthlist wml = new Hrkq_workschmonthlist();
		Object okqdate_end = jo.get("kqdate_end"); // ljdate-->kqdate_end
		String kqdate_end = ((okqdate_end == null) || (okqdate_end.toString().isEmpty())) ? null
				: Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(okqdate_end.toString()));

		Object okqdate_start = jo.get("kqdate_start"); // hiredday-->kqdate_start
		String kqdate_start = ((okqdate_start == null) || (okqdate_start.toString().isEmpty())) ? null
				: Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(okqdate_start.toString()));

		String er_id = jo.getString("er_id");
		String sqltem = "";
		for (int i = 1; i <= 31; i++) {
			String d = "00" + i;
			d = d.substring(d.length() - 2, d.length());
			sqltem = sqltem + "SELECT '" + yearmonth + "-" + d + "' d,scid" + i + " scid FROM hrkq_workschmonthlist WHERE er_id=" + er_id + " AND yearmonth='"
					+ yearmonth + "' UNION ";
		}
		if (!sqltem.isEmpty())
			sqltem = sqltem.substring(0, sqltem.length() - 6);
		String sqlstr = "SELECT IFNULL(SUM(l.dayratio),0) days FROM hrkq_sched_line l,(" + sqltem + ") tb WHERE l.scid=tb.scid ";
		// if (ljdate != null) 应出满勤与离职无关
		// sqlstr = sqlstr + " and d<='" + ljdate + "'";

		float days = Float.valueOf(HRUtil.getReadPool().openSql2List(sqlstr).get(0).get("days")) / 100;
		days = days + days % (0.5f);

		sqlstr = "SELECT IFNULL(COUNT(*),0)  ct  FROM hrkq_ohyear WHERE ohdate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND ohdate<'"
				+ Systemdate.getStrDateyyyy_mm_dd(eddate) + "' AND iswork=2";
		if (kqdate_end != null)
			sqlstr = sqlstr + " and ohdate<='" + kqdate_end + "'";
		if (kqdate_start != null)
			sqlstr = sqlstr + " and ohdate>='" + kqdate_start + "'";

		float fddays = Float.valueOf(HRUtil.getReadPool().openSql2List(sqlstr).get(0).get("ct"));
		fddays = fddays + fddays % (0.5f);
		jo.put("ycmq", days + fddays);// 应出满勤
		jo.put("fdjq", fddays);
	}

	/**
	 * 请假时长 天
	 * 
	 * @param he
	 * @param dw
	 * @param includechld
	 * @param bgdate
	 * @param eddate
	 * @return
	 * @throws Exception
	 */
	private static void getHolidayEmpHour(JSONObject jo, String ym) throws Exception {
		String sqlstr = "SELECT  " + " IFNULL(SUM(CASE  WHEN t.bhtype =2 or h.viodeal=1 THEN l.lhdaystrue ELSE 0 END ),0) sj," // 事假或违规为事假
				+ " IFNULL(SUM(CASE  WHEN t.bhtype =3 AND (htconfirm<>2 OR htconfirm IS NULL) THEN l.lhdaystrue ELSE 0 END ),0) gj,"// 且没违规
				+ " IFNULL(SUM(CASE  WHEN t.bhtype =4 AND (htconfirm<>2 OR htconfirm IS NULL) THEN l.lhdaystrue ELSE 0 END ),0) hj, "
				+ " IFNULL(SUM(CASE  WHEN t.bhtype =5 AND (htconfirm<>2 OR htconfirm IS NULL) THEN l.lhdaystrue ELSE 0 END ),0) cj,"
				+ " IFNULL(SUM(CASE  WHEN t.bhtype =5 AND h.htname='看护假' AND (htconfirm<>2 OR htconfirm IS NULL) THEN l.lhdaystrue ELSE 0 END ),0) khj,"
				+ " IFNULL(SUM(CASE  WHEN t.bhtype =7 AND (htconfirm<>2 OR htconfirm IS NULL) THEN l.lhdaystrue ELSE 0 END ),0) snj,"
				+ " IFNULL(SUM(CASE  WHEN t.bhtype =8 AND (htconfirm<>2 OR htconfirm IS NULL) THEN l.lhdaystrue ELSE 0 END ),0) bj,"
				+ " IFNULL(SUM(CASE  WHEN t.bhtype =9 AND (htconfirm<>2 OR htconfirm IS NULL) THEN l.lhdaystrue ELSE 0 END ),0) gsj"
				+ " FROM  hrkq_holidayapp h,hrkq_holidayapp_month l,hrkq_holidaytype t " 
				+ " WHERE h.stat = 9 AND h.htid=t.htid "
				+ " AND h.haid=l.haid AND l.yearmonth='" + ym + "' AND er_id=" + jo.getString("er_id");
		List<HashMap<String, String>> lm = HRUtil.getReadPool().openSql2List(sqlstr);
		jo.put("sj", Float.valueOf(lm.get(0).get("sj")));
		jo.put("gj", Float.valueOf(lm.get(0).get("gj")));
		jo.put("hj", Float.valueOf(lm.get(0).get("hj")));
		jo.put("cj", Float.valueOf(lm.get(0).get("cj")));
		jo.put("snj", Float.valueOf(lm.get(0).get("snj")));
		jo.put("bj", Float.valueOf(lm.get(0).get("bj")));
		jo.put("gsj", Float.valueOf(lm.get(0).get("gsj")));
		jo.put("khj", Float.valueOf(lm.get(0).get("khj")));//看护假
	}

	private static void getOvertimeEmpHours(JSONObject jo, Date bgdate, Date eddate) throws NumberFormatException, Exception {
		String bs = Systemdate.getStrDateyyyy_mm_dd(bgdate);
		String es = Systemdate.getStrDateyyyy_mm_dd(eddate);
		String sqlstr = "SELECT  IFNULL(SUM(CASE  WHEN (over_type = 1 AND otltype IN(1,2))  THEN othours ELSE 0 END ),0) prjbss,"
				+ " IFNULL(SUM(CASE  WHEN (over_type = 2 AND otltype IN(1,2))  THEN othours ELSE 0 END ),0) zmjbss,"
				+ " IFNULL(SUM(CASE  WHEN (over_type = 3 AND otltype IN(1,2))  THEN othours ELSE 0 END ),0) fdjbss,"
				+ " IFNULL(SUM(CASE  WHEN otltype IN(3,4,5) THEN othours ELSE 0 END ),0) zlbjbss," + " IFNULL(SUM(othours),0) zjbss "
				+ " FROM hrkq_overtime_list  WHERE ((bgtime >='" + bs + "' AND bgtime<'" + es + "') OR(edtime >='" + bs + "' AND edtime<'" + es
				+ "')) and dealtype=2 AND er_id =" + jo.getString("er_id");// 只显示计算加班费的
		List<HashMap<String, String>> lm = HRUtil.getReadPool().openSql2List(sqlstr);
		HashMap<String, String> m = lm.get(0);
		jo.put("prjbss", m.get("prjbss"));
		jo.put("zmjbss", m.get("zmjbss"));
		jo.put("fdjbss", m.get("fdjbss"));
		jo.put("zlbjbss", m.get("zlbjbss"));
		jo.put("zjbss", m.get("zjbss"));
	}

	/**
	 * @return 旷工天数,迟到次数,早退次数 离职前、当前日期之前
	 * @throws Exception
	 */
	private static void getKGCDZTDays(JSONObject jo, Date bgdate, Date eddate, boolean edk, boolean lvk) throws Exception {
		Object okqdate_end = jo.get("kqdate_end"); // ljdate-->kqdate_end
		String kqdate_end = ((okqdate_end == null) || (okqdate_end.toString().isEmpty())) ? null
				: Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(okqdate_end.toString()));

		Object okqdate_start = jo.get("kqdate_start"); // hiredday-->kqdate_start
		String kqdate_start = ((okqdate_start == null) || (okqdate_start.toString().isEmpty())) ? null
				: Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(okqdate_start.toString()));

		String bs = Systemdate.getStrDateyyyy_mm_dd(bgdate);
		String es = Systemdate.getStrDateyyyy_mm_dd(eddate);
		// String exs=Systemdate.getStrDateyyyy_mm_dd(new Date);
		String sqlstr = "SELECT " + "  IFNULL(SUM(CASE  WHEN r.lrst NOT IN(9,10,12,0,6) THEN sl.dayratio ELSE 0 END )/100,0) sjcq ,"
				+ "  IFNULL(SUM(CASE  WHEN r.lrst IN (9, 10, 12) THEN sl.dayratio ELSE 0 END )/100,0) kg ,"
				+ "  IFNULL(SUM(CASE  WHEN r.lrst =2 THEN 1 ELSE 0 END ),0) cd ," + "  IFNULL(SUM(CASE  WHEN r.lrst =3 THEN 1 ELSE 0 END ),0) zt "
				+ "FROM  hrkq_bckqrst r,hrkq_sched_line sl  " + " WHERE r.sclid=sl.sclid " + " and r.kqdate>='" + bs + "' and r.kqdate<'" + es
				+ "' and r.er_id  =" + jo.getString("er_id") + " and r.kqdate<'" + Systemdate.getStrDateyyyy_mm_dd() + "' ";
		if (kqdate_end != null) {
			if (lvk) {
				sqlstr = sqlstr + " and r.kqdate<='" + kqdate_end + "' ";
			} else
				sqlstr = sqlstr + " and r.kqdate<'" + kqdate_end + "' ";
		}

		if (kqdate_start != null) {
			if (edk)
				sqlstr = sqlstr + " and r.kqdate>='" + kqdate_start + "' ";
			else
				sqlstr = sqlstr + " and r.kqdate>'" + kqdate_start + "' ";
		}

		List<HashMap<String, String>> lm = HRUtil.getReadPool().openSql2List(sqlstr);
		HashMap<String, String> m = lm.get(0);
		float kg = Float.valueOf(m.get("kg"));
		kg = kg + kg % (0.5f);
		jo.put("kgts", kg);

		float sjcq = Float.valueOf(m.get("sjcq"));
		sjcq = sjcq + sjcq % (0.5f);
		jo.put("sjcq", sjcq);

		jo.put("cdcs", m.get("cd"));
		jo.put("ztcs", m.get("zt"));
	}




	/**
	 * 超签次数
	 * 
	 * @param jo
	 * @param bgdate
	 * @param eddate
	 * @throws Exception
	 */
	private static void rptresigntime(JSONObject jo, Date bgdate, Date eddate) throws Exception {
		String bs = Systemdate.getStrDateyyyy_mm_dd(bgdate);
		String es = Systemdate.getStrDateyyyy_mm_dd(eddate);
		String sqlstr = "SELECT  tb.*,IFNULL(m.resigntimes,3) mxrstime FROM " + " (SELECT r.er_id,COUNT(*) rstime " + " FROM hrkq_resign r,hrkq_resignline rl "
				+ " WHERE r.stat=9 AND rl.isreg=1 and rl.ri_times=1 AND r.resid=rl.resid " + " and r.resdate>='" + bs + "' and r.resdate<'" + es + "'"
				+ " and r.er_id  =" + jo.getString("er_id") + " and r.res_type=2 " + " GROUP BY r.orgid,r.er_id)tb "
				+ " LEFT JOIN hrkq_resigntimeparm m ON tb.er_id=m.er_id";
		JSONArray rs = HRUtil.getReadPool().opensql2json_O(sqlstr);
		if (rs.size() > 0) {
			JSONObject r = rs.getJSONObject(0);
			int cbrstime = r.getInt("rstime") - r.getInt("mxrstime");
			cbrstime = (cbrstime < 0) ? 0 : cbrstime;
			jo.put("cqcs", cbrstime);
		} else
			jo.put("cqcs", 0);
	}

	private static void rptlvinfo(JSONObject jo,Date bgdate, Date eddate) throws Exception {
		//String sqlstr = "SELECT * FROM hr_leavejob WHERE stat=9 AND  er_id=" + jo.getString("er_id");
		String bs = Systemdate.getStrDateyyyy_mm_dd(bgdate);
		String es = Systemdate.getStrDateyyyy_mm_dd(eddate);
		String sqlstr="SELECT ljtype2,ljtype1,ljreason FROM hr_leavejob l,hr_employee h WHERE stat=9 and l.er_id='"+jo.getString("er_id")+"' and l.er_id=h.er_id and h.kqdate_end is NOT NULL and h.ljdate>='"+bs+"' and h.ljdate<='"+es+"'";
		Hr_leavejob lv = new Hr_leavejob();
		lv.findBySQL(sqlstr);
		if (!lv.isEmpty()) {
			jo.put("ljtype2", lv.ljtype2.getValue());
			jo.put("ljtype1", lv.ljtype1.getValue());
			jo.put("ljreason", lv.ljreason.getValue());
		}else{
			jo.put("ljtype2","");
			jo.put("ljtype1","");
			jo.put("ljreason", "");
		}
	}

	/**
	 * @param jo
	 * @param all true：所有 false ：上班天数
	 * @throws Exception
	 * @throws NumberFormatException
	 */
	private static float getNightDays(JSONObject jo, Date bgdate, Date eddate, Boolean all) throws NumberFormatException, Exception {
		String er_id = jo.getString("er_id");
		String bs = Systemdate.getStrDateyyyy_mm_dd(bgdate);
		String es = Systemdate.getStrDateyyyy_mm_dd(eddate);
		//		String sqlstr = "SELECT IFNULL(SUM(s.allworktime) /8,0) days FROM hrkq_bckqrst r, hrkq_sched s,hrkq_sched_line sl "
		//				+ "WHERE r.sclid=sl.sclid AND sl.scid=s.scid AND s.sctype=2 AND er_id=" + er_id + " and kqdate>='" + bs + "' and kqdate<='" + es + "'";
		//		if (!all)
		//			sqlstr = sqlstr + " AND r.lrst IN(1,2,3) ";

		String sqlstr = "select ifnull(sum(dd),0) days from "
				+ "(select  CASE  WHEN d>1 THEN 1 WHEN d>0.5 and d<1 THEN 0.5 ELSE 0 END dd from "
				+ "(SELECT  sum(s.`allworktime`) /7 d FROM "
				+ "  hrkq_bckqrst r,  hrkq_sched s,  hrkq_sched_line sl "
				+ "WHERE r.sclid = sl.sclid  AND sl.scid = s.scid  AND s.sctype = 2 "
				+ "  AND er_id = " + er_id
				+ "  and kqdate >= '" + bs + "' "
				+ "  and kqdate < '" + es + "' ";
		if (!all)
			sqlstr = sqlstr + " AND r.lrst IN(1,2,3) ";
		sqlstr = sqlstr + "  group by kqdate) tb) tb2";

		float days = Float.valueOf(HRUtil.getReadPool().openSql2List(sqlstr).get(0).get("days").toString());
		// days = days - (days % (0.5f));// 比如数据库为16.875 应该计16.5
		return days;
	}
	/**
	 * 获取组织下的有排班的人员ID
	 * @param orgid
	 * @param orgid
	 * @return ym
	 * @throws Exception 
	 */
	public static List<String> findPbInOrg(Shworg org,String ym) throws Exception{
		String empIds="";
		List<String>erIdList=new ArrayList<>();
		String strSql="SELECT e.er_id  FROM hrkq_workschmonthlist l,hr_employee e WHERE l.er_id=e.er_id"
				+ "and  yearmonth='"+ym+"'";
		List<HashMap<String, String>> List = HRUtil.getReadPool().openSql2List(strSql);
		for(HashMap<String, String>emp : List){
			erIdList.add(emp.get("er_id"));
		}
		//		if(erIdList.size()>0){
		//			empIds=tranInSql(erIdList);
		//		}
		return erIdList;


	}
	/**
	 * 查询某月份在职员工并且有排班的人员
	 * @param idpath 部门IDpath
	 * @param ym 月份
	 * @return 在职员工ID
	 * @throws Exception
	 */
	public static List<String> findEmployeeByZz(String idpath,String ym) throws Exception{
		List<String>erIdList=new ArrayList<>();
		String strSql="select er_id from hr_month_employee  where  yearmonth='"+ym+"'  and  idpath like '"+idpath+"%' and  er_id in (select er_id from hrkq_workschmonthlist where yearmonth='"+ym+"')"+
				" UNION select er_id from hr_employee where idpath like '"+idpath+"%'  AND DATE_FORMAT(kqdate_end,'%Y-%m')='"+ym+"' and  er_id in (select er_id from hrkq_workschmonthlist where yearmonth='"+ym+"')";
		List<HashMap<String, String>> List = HRUtil.getReadPool().openSql2List(strSql);
		for(HashMap<String, String>emp : List){
			erIdList.add(emp.get("er_id"));
		}
		return erIdList;
	}




}
