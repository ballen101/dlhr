package com.hr.attd.ctr;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.util.DictionaryTemp;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.attd.entity.Hrkq_leave_blance;
import com.hr.attd.entity.Hrkq_overtime;
import com.hr.attd.entity.Hrkq_overtime_hour;
import com.hr.attd.entity.Hrkq_overtime_line;
import com.hr.attd.entity.Hrkq_overtime_list;

public class CtrHrkq_overtime extends JPAController {

	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		wf.subject.setValue(((Hrkq_overtime) jpa).ot_code.getValue());
	}

	@Override
	public void BeforeWFStart(CJPA jpa, String wftempid, CDBConnection con) throws Exception {
		// 检查数据合法性
		checkdata(jpa, con);
		checkdata2(jpa, con);
	}

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		checkjc(jpa, con);
		if (isFilished) {
			doUpdateOverTimeList(jpa, con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc nxtproc, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			doUpdateOverTimeList(jpa, con);
		}
	}

	private void checkjc(CJPA jpa, CDBConnection con) throws Exception {
		Hrkq_overtime ot = (Hrkq_overtime) jpa;
		for (CJPABase jpa1 : ot.hrkq_overtime_lines) {
			Hrkq_overtime_line otl = (Hrkq_overtime_line) jpa1;
			for (CJPABase jpa2 : otl.hrkq_overtime_hours) {
				Hrkq_overtime_hour otlh = (Hrkq_overtime_hour) jpa2;
				Date bgtime = otlh.begin_date.getAsDatetime();
				Date edtime = otlh.end_date.getAsDatetime();
				HrkqUtil.checkAllOverlapDatetime(2, ot.ot_id.getValue(), otlh.er_id.getValue(), otlh.employee_name.getValue(), bgtime, edtime);
			}
		}
	}

	private void doUpdateOverTimeList(CJPA jpa, CDBConnection con) throws Exception {
		Hrkq_overtime ot = (Hrkq_overtime) jpa;
		Hrkq_overtime_list otlst = new Hrkq_overtime_list();
		for (CJPABase jpa1 : ot.hrkq_overtime_lines) {
			Hrkq_overtime_line otl = (Hrkq_overtime_line) jpa1;
			for (CJPABase jpa2 : otl.hrkq_overtime_hours) {
				Hrkq_overtime_hour otlh = (Hrkq_overtime_hour) jpa2;
				otlst.clear();
				otlst.oth_id.setValue(otlh.oth_id.getValue()); // 加班时间明细ID
				otlst.otl_id.setValue(otl.otl_id.getValue()); // 加班申请明细ID
				otlst.otltype.setAsInt(1); // 源单类型 1 批量申请单
				otlst.ot_id.setValue(ot.ot_id.getValue()); // 加班申请ID
				otlst.ot_code.setValue(ot.ot_code.getValue()); // 加班申请编码
				otlst.er_id.setValue(otl.er_id.getValue()); // 申请人档案ID
				otlst.employee_code.setValue(otl.employee_code.getValue()); // 申请人工号
				otlst.employee_name.setValue(otl.employee_name.getValue()); // 申请人姓名
				otlst.dealtype.setValue(ot.dealtype.getValue()); // 加班处理 1 调休 2 计加班费
				otlst.over_type.setValue(ot.over_type.getValue());// 加班类型
				otlst.needchedksb.setValue(otlh.needchedksb.getValue());// 上班需要打卡
				otlst.needchedkxb.setValue(otlh.needchedkxb.getValue());// 下班需要打卡
				otlst.bgtime.setValue(otlh.begin_date.getValue()); // 上班时间
				otlst.edtime.setValue(otlh.end_date.getValue()); // 下班时间
				otlst.otrate.setValue(ot.otrate.getValue()); // 加班倍率
				otlst.applyhours.setValue(otlh.applyhours.getValue());// 加班申请时数
				otlst.othours.setAsInt(0); // 加班时数
				otlst.deductoff.setValue(otlh.deductoff.getValue()); // 扣休息时数
				otlst.allfreetime.setAsInt(0); // 调休时长小时
				otlst.freeedtime.setAsInt(0); // 已休息时间小时
				otlst.allotamont.setAsInt(0); // 加班费
				otlst.payedotamont.setAsInt(0); // 已发放加班费
				otlst.save(con);
			}
		}
	}

	private void checkdata(CJPA jpa, CDBConnection con) throws Exception {
		Hrkq_overtime ot = (Hrkq_overtime) jpa;
		if ((ot.isoffjob.getAsIntDefault(0) == 2) || (ot.dealtype.getAsIntDefault(0) == 1) || ((ot.dealtype.getAsIntDefault(0) == 2) && (ot.over_type.getAsIntDefault(0) == 3)))// 非脱产
																																								// 不控制
			return;
		// Hrkq_overtime_list otlst = new Hrkq_overtime_list();
		for (CJPABase jt : ot.hrkq_overtime_lines) {
			Hrkq_overtime_line otl = (Hrkq_overtime_line) jt;
			check(ot, otl);
		}
	}

	private void checkdata2(CJPA jpa, CDBConnection con) throws Exception {
		Hrkq_overtime ot = (Hrkq_overtime) jpa;
		for (CJPABase jt : ot.hrkq_overtime_lines) {
			Hrkq_overtime_line otl = (Hrkq_overtime_line) jt;
			check2(ot, otl);
		}
	}

	public static void checkOver_Qual(Date bgtime, Date edtime, String over_type, String er_id, String employee_name, float oths) throws Exception {
		Hrkq_overtime otem = new Hrkq_overtime();
		String sqlstr = "select  m.permonlimit from hrkq_overtime_qual m,hrkq_overtime_qual_line l "
				+ " where m.oq_id=l.oq_id and m.stat=9 and m.begin_date<='" + Systemdate.getStrDateyyyy_mm_dd(bgtime)
				+ "'  and m.end_date>='" + Systemdate.getStrDateyyyy_mm_dd(edtime) + "' "
				+ "       and l.er_id=" + er_id + " and m.over_type like '%" + over_type + "%'"
				+ "  and l.breaked=2 "
				+ " order by m.oq_id desc limit 1";
		List<HashMap<String, String>> ml = otem.pool.openSql2List(sqlstr);
		String over_typecap = (new DictionaryTemp()).getCaptionByValue("923", over_type);
		if (ml.size() == 0) {
			throw new Exception("加班时间段【" + Systemdate.getStrDate(bgtime) + "】【" + Systemdate.getStrDate(edtime) + "】内没发现员工【" + employee_name
					+ "】类型为【" + over_typecap + "】的资格申请");
		}
		float permonlimit = Float.valueOf(ml.get(0).get("permonlimit"));// 月度限制
		int flag = Integer.valueOf(HrkqUtil.getParmValueErr("OVERTIME_HOUR_LIMIT"));// 加班申请月度时长限制 1 限制 2 不限制
		if ((permonlimit == 0) || (flag == 2))
			return;// 为0 不限制
		// float oths = getotlhours(otl);// 本次需要加班时长H
		oths = (float) (Math.round(oths * 10)) / 10;
		float usedoths = getMonthOtlHours(er_id, bgtime);// 月度已经提交时长H
		usedoths = (float) (Math.round(usedoths * 10)) / 10;
		if ((usedoths + oths) > permonlimit) {
			throw new Exception("员工【" + employee_name + "】【" + Systemdate.getStrDateByFmt(bgtime, "yyyy-MM")
					+ "】月已经提交加班申请时间合计【" + usedoths + "】，本次提交加班时间合计【" + oths + "】，超过月度加班时间限制【" + permonlimit + "】");
		}
	}

	// 获取有效资格表单
	public static void check(Hrkq_overtime ot, Hrkq_overtime_line otl) throws Exception {
		if ((ot.isoffjob.getAsIntDefault(0) == 2) || (ot.dealtype.getAsIntDefault(0) == 1) || ((ot.dealtype.getAsIntDefault(0) == 2) && (ot.over_type.getAsIntDefault(0) == 3)))// 非脱产																																								// 不控制
			return;

		CJPALineData<Hrkq_overtime_hour> hs = otl.hrkq_overtime_hours;
		Date bgtime = Systemdate.getDateByStr("2999-12-12");
		Date edtime = Systemdate.getDateByStr("1907-01-01");
		for (CJPABase jpa : hs) {
			Hrkq_overtime_hour h = (Hrkq_overtime_hour) jpa;
			if (bgtime.getTime() > h.begin_date.getAsDatetime().getTime())
				bgtime = h.begin_date.getAsDatetime();
			if (edtime.getTime() < h.end_date.getAsDatetime().getTime())
				edtime = h.end_date.getAsDatetime();
		}
		if (ot.dealtype.getAsIntDefault(0) == 2) {
			float oths = getotlhours(otl);// 本次需要加班时长H
			checkOver_Qual(bgtime, edtime, ot.over_type.getValue(), otl.er_id.getValue(), otl.employee_name.getValue(), oths);
		}
	}

	public static void check2(Hrkq_overtime ot, Hrkq_overtime_line otl) throws Exception {
		CJPALineData<Hrkq_overtime_hour> hs = otl.hrkq_overtime_hours;
		for (CJPABase jpa : hs) {
			Hrkq_overtime_hour h = (Hrkq_overtime_hour) jpa;
			if(!ot.creator.getValue().equals("inteface")){
				HrkqUtil.checkValidDate(h.begin_date.getAsDatetime());
			}
			//HrkqUtil.checkValidDate(h.begin_date.getAsDatetime());
		}
	}

	// 获取加班行的所有加班时间段 小时合计
	private static float getotlhours(Hrkq_overtime_line otl) {
		long sumh = 0;
		for (CJPABase jpa : otl.hrkq_overtime_hours) {
			Hrkq_overtime_hour h = (Hrkq_overtime_hour) jpa;
			sumh += h.end_date.getAsDatetime().getTime() - h.begin_date.getAsDatetime().getTime();
		}
		float rst = (float) sumh / (1000 * 60 * 60);
		return rst;
	}

	// 获取该员工 当月 已经提交的加班时间总和；
	private static float getMonthOtlHours(String erid, Date bgtime) throws Exception {
		String bgdate = Systemdate.getStrDateByFmt(bgtime, "yyyy-MM-01");
		String eddate = Systemdate.getStrDateByFmt(Systemdate.dateMonthAdd(bgtime, 1), "yyyy-MM-01");
		String sqlstr = "SELECT DISTINCT h.* FROM hrkq_overtime m,hrkq_overtime_line l,hrkq_overtime_hour h"
				+ " WHERE m.ot_id=l.ot_id AND l.otl_id=h.otl_id and m.dealtype=2 "
				+ "   AND m.stat>1 AND m.stat<=9 AND l.er_id=" + erid + " AND h.begin_date>='" + bgdate + "' "
				+ "   AND h.begin_date<'" + eddate + "'";
		CJPALineData<Hrkq_overtime_hour> ohs = new CJPALineData<Hrkq_overtime_hour>(Hrkq_overtime_hour.class);
		ohs.findDataBySQL(sqlstr, false, false);
		float sumh = 0;
		for (CJPABase jl : ohs) {
			Hrkq_overtime_hour oh = (Hrkq_overtime_hour) jl;
			float othours = oh.othours.getAsFloatDefault(0);
			if (othours == 0) {
				sumh += oh.applyhours.getAsFloatDefault(0);
			} else {
				sumh += othours;
			}
		}
		return sumh;
	}

	@Override
	public void BeforeSave(CJPABase jpa, CDBConnection con, boolean selfLink) throws Exception {
		Hrkq_overtime ot = (Hrkq_overtime) jpa;
		CJPALineData<Hrkq_overtime_line> ls = ot.hrkq_overtime_lines;
		boolean hasnotcheck = false;
		for (CJPABase jpa1 : ls) {
			Hrkq_overtime_line l = (Hrkq_overtime_line) jpa1;
			CJPALineData<Hrkq_overtime_hour> hs = l.hrkq_overtime_hours;
			for (CJPABase jpa2 : hs) {
				Hrkq_overtime_hour h = (Hrkq_overtime_hour) jpa2;
				if (h.begin_date.isEmpty() || h.end_date.isEmpty())
					throw new Exception("工号【" + l.employee_code.getValue() + "】加班上班下班时间都不能为空");
				h.begin_date.setValue(Systemdate.getStrDateByFmt(h.begin_date.getAsDatetime(), "yyyy-MM-dd HH:mm:00"));
				h.end_date.setValue(Systemdate.getStrDateByFmt(h.end_date.getAsDatetime(), "yyyy-MM-dd HH:mm:00"));
				if (!hasnotcheck)
					hasnotcheck = ((h.needchedksb.getAsIntDefault(0) == 2) || (h.needchedkxb.getAsIntDefault(0) == 2));
			}
		}
		if (hasnotcheck)
			ot.hasnotcheck.setValue(1);
		else
			ot.hasnotcheck.setValue(2);
	}

	@Override
	public String OnCCoVoid(CDBConnection con, CJPA jpa) throws Exception {
		Hrkq_overtime ot = (Hrkq_overtime) jpa;
		String sqlstr = "SELECT DISTINCT * from (SELECT lb.* "
				+ " FROM hrkq_leave_blance lb,hrkq_overtime_list ol"
				+ " WHERE lb.stype=2 AND lb.sid=ol.otlistid AND ol.ot_id=" + ot.ot_id.getValue() + ") tb where 1=1 ";
		CJPALineData<Hrkq_leave_blance> lbs = new CJPALineData<Hrkq_leave_blance>(Hrkq_leave_blance.class);
		lbs.findDataBySQL(sqlstr);
		for (CJPABase cjpa : lbs) {
			Hrkq_leave_blance lb = (Hrkq_leave_blance) cjpa;
			if (lb.usedlbtime.getAsIntDefault(-1) > 0) {
				throw new Exception("表单已经有调休记录，无法作废");
			}
		}
		sqlstr = "DELETE FROM hrkq_leave_blance WHERE stype=2 AND sid IN( SELECT l.otlistid FROM hrkq_overtime_list l WHERE l.ot_id=" + ot.ot_id.getValue() + ")";
		con.execsql(sqlstr);// 删除可调休明细
		sqlstr = "DELETE FROM hrkq_overtime_list WHERE ot_id=" + ot.ot_id.getValue();
		con.execsql(sqlstr);// 删除加班列表 如果不删除 可能会导致 计算考勤时候产生调休记录
		return null;
	}

}
