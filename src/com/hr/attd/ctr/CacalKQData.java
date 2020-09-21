package com.hr.attd.ctr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.dbpool.util.TwoDate;
import com.corsair.server.generic.Shworg;
import com.hr.attd.entity.Hrkq_bckqrst;
import com.hr.attd.entity.Hrkq_business_trip;
import com.hr.attd.entity.Hrkq_calc;
import com.hr.attd.entity.Hrkq_calcline;
import com.hr.attd.entity.Hrkq_holidayapp;
import com.hr.attd.entity.Hrkq_onduty;
import com.hr.attd.entity.Hrkq_ondutyline;
import com.hr.attd.entity.Hrkq_overtime_hour;
import com.hr.attd.entity.Hrkq_overtime_list;
import com.hr.attd.entity.Hrkq_sched;
import com.hr.attd.entity.Hrkq_sched_line;
import com.hr.attd.entity.Hrkq_swcdlst;
import com.hr.attd.entity.Hrkq_wkoff;
import com.hr.attd.entity.Hrkq_workschmonthlist;
import com.hr.msg.entity.Hr_kq_day_report;
import com.hr.perm.entity.Hr_employee;
import com.hr.util.DateUtil;

public class CacalKQData {

	private static final float min_lb_h = 3.5f;

	public int KQRSTCODE_NORMAL = 1;// 正常
	public int KQRSTCODE_LATE = 2;// 迟到
	public int KQRSTCODE_LEARLY = 3;// 早退
	public int KQRSTCODE_NOTPK = 4;// 无打卡
	public int KQRSTCODE_BTRA = 5;// 出差
	public int KQRSTCODE_LEAVEH = 6;// 请假
	public int KQRSTCODE_WORKOFF = 7;// 调休
	public int KQRSTCODE_RESIGN = 8;// 签卡
	public int KQRSTCODE_LABT = 9;// 旷工(迟到)
	public int KQRSTCODE_BABT = 10;// 旷工(早退)
	//
	public int KQRSTCODE_LATE_LEARLY = 11;// 迟到早退
	public int KQRSTCODE_ABTABT = 12;// 旷工(旷工)

	/**
	 * 考勤计算规则 map
	 */
//	private int[][] CKQ_MAP = {
//			{ KQRSTCODE_NORMAL, KQRSTCODE_NORMAL, KQRSTCODE_NORMAL },
//			{ KQRSTCODE_NORMAL, KQRSTCODE_LEARLY, KQRSTCODE_LEARLY },
//			{ KQRSTCODE_NORMAL, KQRSTCODE_NOTPK, KQRSTCODE_ABTABT },
//			{ KQRSTCODE_NORMAL, KQRSTCODE_BTRA, KQRSTCODE_BTRA },
//			{ KQRSTCODE_NORMAL, KQRSTCODE_LEAVEH, KQRSTCODE_LEAVEH },
//			{ KQRSTCODE_NORMAL, KQRSTCODE_WORKOFF, KQRSTCODE_WORKOFF },
//			{ KQRSTCODE_NORMAL, KQRSTCODE_RESIGN, KQRSTCODE_NORMAL },
//			{ KQRSTCODE_NORMAL, KQRSTCODE_BABT, KQRSTCODE_BABT },
//			{ KQRSTCODE_LATE, KQRSTCODE_NORMAL, KQRSTCODE_LATE },
//			{ KQRSTCODE_LATE, KQRSTCODE_LEARLY, KQRSTCODE_LATE_LEARLY },
//			{ KQRSTCODE_LATE, KQRSTCODE_NOTPK, KQRSTCODE_ABTABT },
//			{ KQRSTCODE_LATE, KQRSTCODE_BTRA, KQRSTCODE_BTRA }, ///
//			{ KQRSTCODE_LATE, KQRSTCODE_LEAVEH, KQRSTCODE_LEAVEH },
//			{ KQRSTCODE_LATE, KQRSTCODE_WORKOFF, KQRSTCODE_WORKOFF },
//			{ KQRSTCODE_LATE, KQRSTCODE_RESIGN, KQRSTCODE_LATE },
//			{ KQRSTCODE_LATE, KQRSTCODE_BABT, KQRSTCODE_BABT },
//			{ KQRSTCODE_NOTPK, KQRSTCODE_NORMAL, KQRSTCODE_ABTABT },
//			{ KQRSTCODE_NOTPK, KQRSTCODE_LEARLY, KQRSTCODE_ABTABT },
//			{ KQRSTCODE_NOTPK, KQRSTCODE_NOTPK, KQRSTCODE_ABTABT },
//			{ KQRSTCODE_NOTPK, KQRSTCODE_BTRA, KQRSTCODE_BTRA }, //
//			{ KQRSTCODE_NOTPK, KQRSTCODE_LEAVEH, KQRSTCODE_LEAVEH },
//			{ KQRSTCODE_NOTPK, KQRSTCODE_WORKOFF, KQRSTCODE_WORKOFF },
//			{ KQRSTCODE_NOTPK, KQRSTCODE_RESIGN, KQRSTCODE_ABTABT },
//			{ KQRSTCODE_NOTPK, KQRSTCODE_BABT, KQRSTCODE_ABTABT },
//			{ KQRSTCODE_BTRA, KQRSTCODE_NORMAL, KQRSTCODE_BTRA },
//			{ KQRSTCODE_BTRA, KQRSTCODE_LEARLY, KQRSTCODE_BTRA },
//			{ KQRSTCODE_BTRA, KQRSTCODE_NOTPK, KQRSTCODE_BTRA },
//			{ KQRSTCODE_BTRA, KQRSTCODE_BTRA, KQRSTCODE_BTRA },
//			{ KQRSTCODE_BTRA, KQRSTCODE_LEAVEH, KQRSTCODE_BTRA },
//			{ KQRSTCODE_BTRA, KQRSTCODE_WORKOFF, KQRSTCODE_BTRA },
//			{ KQRSTCODE_BTRA, KQRSTCODE_RESIGN, KQRSTCODE_BTRA },
//			{ KQRSTCODE_BTRA, KQRSTCODE_BABT, KQRSTCODE_BTRA },
//			{ KQRSTCODE_LEAVEH, KQRSTCODE_NORMAL, KQRSTCODE_LEAVEH },
//			{ KQRSTCODE_LEAVEH, KQRSTCODE_LEARLY, KQRSTCODE_LEAVEH },
//			{ KQRSTCODE_LEAVEH, KQRSTCODE_NOTPK, KQRSTCODE_LEAVEH },
//			{ KQRSTCODE_LEAVEH, KQRSTCODE_BTRA, KQRSTCODE_LEAVEH },
//			{ KQRSTCODE_LEAVEH, KQRSTCODE_LEAVEH, KQRSTCODE_LEAVEH },
//			{ KQRSTCODE_LEAVEH, KQRSTCODE_WORKOFF, KQRSTCODE_LEAVEH },
//			{ KQRSTCODE_LEAVEH, KQRSTCODE_RESIGN, KQRSTCODE_LEAVEH },
//			{ KQRSTCODE_LEAVEH, KQRSTCODE_BABT, KQRSTCODE_LEAVEH },
//			{ KQRSTCODE_WORKOFF, KQRSTCODE_NORMAL, KQRSTCODE_WORKOFF },
//			{ KQRSTCODE_WORKOFF, KQRSTCODE_LEARLY, KQRSTCODE_WORKOFF },
//			{ KQRSTCODE_WORKOFF, KQRSTCODE_NOTPK, KQRSTCODE_WORKOFF },
//			{ KQRSTCODE_WORKOFF, KQRSTCODE_BTRA, KQRSTCODE_WORKOFF },
//			{ KQRSTCODE_WORKOFF, KQRSTCODE_LEAVEH, KQRSTCODE_WORKOFF },
//			{ KQRSTCODE_WORKOFF, KQRSTCODE_WORKOFF, KQRSTCODE_WORKOFF },
//			{ KQRSTCODE_WORKOFF, KQRSTCODE_RESIGN, KQRSTCODE_WORKOFF },
//			{ KQRSTCODE_WORKOFF, KQRSTCODE_BABT, KQRSTCODE_WORKOFF },
//			{ KQRSTCODE_RESIGN, KQRSTCODE_NORMAL, KQRSTCODE_NORMAL },
//			{ KQRSTCODE_RESIGN, KQRSTCODE_LEARLY, KQRSTCODE_LEARLY },
//			{ KQRSTCODE_RESIGN, KQRSTCODE_NOTPK, KQRSTCODE_ABTABT },
//			{ KQRSTCODE_RESIGN, KQRSTCODE_BTRA, KQRSTCODE_BTRA },
//			{ KQRSTCODE_RESIGN, KQRSTCODE_LEAVEH, KQRSTCODE_LEAVEH },
//			{ KQRSTCODE_RESIGN, KQRSTCODE_WORKOFF, KQRSTCODE_WORKOFF },
//			{ KQRSTCODE_RESIGN, KQRSTCODE_RESIGN, KQRSTCODE_NORMAL },
//			{ KQRSTCODE_RESIGN, KQRSTCODE_BABT, KQRSTCODE_BABT },
//			{ KQRSTCODE_LABT, KQRSTCODE_NORMAL, KQRSTCODE_LABT },
//			{ KQRSTCODE_LABT, KQRSTCODE_LEARLY, KQRSTCODE_LABT },
//			{ KQRSTCODE_LABT, KQRSTCODE_NOTPK, KQRSTCODE_ABTABT },
//			{ KQRSTCODE_LABT, KQRSTCODE_BTRA, KQRSTCODE_BTRA }, //
//			{ KQRSTCODE_LABT, KQRSTCODE_LEAVEH, KQRSTCODE_LEAVEH },
//			{ KQRSTCODE_LABT, KQRSTCODE_WORKOFF, KQRSTCODE_WORKOFF },
//			{ KQRSTCODE_LABT, KQRSTCODE_RESIGN, KQRSTCODE_LABT },
//			{ KQRSTCODE_LABT, KQRSTCODE_BABT, KQRSTCODE_ABTABT }
//	};

///191227
//	{ 正常, 正常, 正常 },
//	{ 正常, 早退, 早退 },
//	{ 正常, 无打卡, 旷工(旷工) },
//	{ 正常, 出差, 出差 },
//	{ 正常, 请假, 请假 },
//	{ 正常, 调休, 调休 },
//	{ 正常, 签卡, 正常 },
//	{ 正常, 旷工(早退), 旷工(早退) },
//	{ 迟到, 正常, 迟到 },
//	{ 迟到, 早退, 迟到早退 },
//	{ 迟到, 无打卡, 旷工(旷工) },
//	{ 迟到, 出差, 迟到 }, 
//	{ 迟到, 请假, 迟到 },
//	{ 迟到, 调休, 迟到 },
//	{ 迟到, 签卡, 迟到 },
//	{ 迟到, 旷工(早退), 旷工(早退) },
//	{ 无打卡, 正常, 旷工(旷工) },
//	{ 无打卡, 早退, 旷工(旷工) },
//	{ 无打卡, 无打卡, 旷工(旷工) },
//	{ 无打卡, 出差, 旷工(旷工) }, 
//	{ 无打卡, 请假, 旷工(旷工) },
//	{ 无打卡, 调休, 旷工(旷工) },
//	{ 无打卡, 签卡, 旷工(旷工) },
//	{ 无打卡, 旷工(早退), 旷工(旷工) },
//	{ 出差, 正常, 出差 },
//	{ 出差, 早退, 早退},
//	{ 出差, 无打卡, 旷工(旷工) },
//	{ 出差, 出差, 出差 },
//	{ 出差, 请假, 请假 },
//	{ 出差, 调休, 出差 },
//	{ 出差, 签卡, 出差 },
//	{ 出差, 旷工(早退), 旷工(早退) },
//	{ 请假, 正常, 请假 },
//	{ 请假, 早退, 请假 },
//	{ 请假, 无打卡, 请假 },
//	{ 请假, 出差, 请假 },
//	{ 请假, 请假, 请假 },
//	{ 请假, 调休, 请假 },
//	{ 请假, 签卡, 请假 },
//	{ 请假, 旷工(早退), 旷工(早退) },
//	{ 调休, 正常, 调休 },
//	{ 调休, 早退, 早退 },
//	{ 调休, 无打卡, 无打卡 },
//	{ 调休, 出差, 调休 },
//	{ 调休, 请假, 调休 },
//	{ 调休, 调休, 调休 },
//	{ 调休, 签卡, 调休 },
//	{ 调休, 旷工(早退), 旷工(早退) },
//	{ 签卡, 正常, 正常 },
//	{ 签卡, 早退, 早退 },
//	{ 签卡, 无打卡, 旷工(旷工) },
//	{ 签卡, 出差, 出差 },
//	{ 签卡, 请假, 请假 },
//	{ 签卡, 调休, 调休 },
//	{ 签卡, 签卡, 正常 },
//	{ 签卡, 旷工(早退), 旷工(早退) },
//	{ 旷工(迟到), 正常, 旷工(迟到) },
//	{ 旷工(迟到), 早退, 旷工(迟到) },
//	{ 旷工(迟到), 无打卡, 旷工(旷工) },
//	{ 旷工(迟到), 出差, 旷工(迟到) }, 
//	{ 旷工(迟到), 请假, 旷工(迟到)},
//	{ 旷工(迟到), 调休, 旷工(迟到) },
//	{ 旷工(迟到), 签卡, 旷工(迟到) },
//	{ 旷工(迟到), 旷工(早退), 旷工(旷工) }
	private int[][] CKQ_MAP = {
			{ KQRSTCODE_NORMAL, KQRSTCODE_NORMAL, KQRSTCODE_NORMAL },
			{ KQRSTCODE_NORMAL, KQRSTCODE_LEARLY, KQRSTCODE_LEARLY },
			{ KQRSTCODE_NORMAL, KQRSTCODE_NOTPK, KQRSTCODE_ABTABT },
			{ KQRSTCODE_NORMAL, KQRSTCODE_BTRA, KQRSTCODE_BTRA },
			{ KQRSTCODE_NORMAL, KQRSTCODE_LEAVEH, KQRSTCODE_LEAVEH },
			{ KQRSTCODE_NORMAL, KQRSTCODE_WORKOFF, KQRSTCODE_WORKOFF },
			{ KQRSTCODE_NORMAL, KQRSTCODE_RESIGN, KQRSTCODE_NORMAL },
			{ KQRSTCODE_NORMAL, KQRSTCODE_BABT, KQRSTCODE_BABT },
			{ KQRSTCODE_LATE, KQRSTCODE_NORMAL, KQRSTCODE_LATE },
			{ KQRSTCODE_LATE, KQRSTCODE_LEARLY, KQRSTCODE_LATE_LEARLY },
			{ KQRSTCODE_LATE, KQRSTCODE_NOTPK, KQRSTCODE_ABTABT },
			{ KQRSTCODE_LATE, KQRSTCODE_BTRA, KQRSTCODE_LATE },
			{ KQRSTCODE_LATE, KQRSTCODE_LEAVEH, KQRSTCODE_LATE },
			{ KQRSTCODE_LATE, KQRSTCODE_WORKOFF, KQRSTCODE_LATE },
			{ KQRSTCODE_LATE, KQRSTCODE_RESIGN, KQRSTCODE_LATE },
			{ KQRSTCODE_LATE, KQRSTCODE_BABT, KQRSTCODE_BABT },
			{ KQRSTCODE_NOTPK, KQRSTCODE_NORMAL, KQRSTCODE_ABTABT },
			{ KQRSTCODE_NOTPK, KQRSTCODE_LEARLY, KQRSTCODE_ABTABT },
			{ KQRSTCODE_NOTPK, KQRSTCODE_NOTPK, KQRSTCODE_ABTABT },
			{ KQRSTCODE_NOTPK, KQRSTCODE_BTRA, KQRSTCODE_ABTABT },
			{ KQRSTCODE_NOTPK, KQRSTCODE_LEAVEH, KQRSTCODE_ABTABT },
			{ KQRSTCODE_NOTPK, KQRSTCODE_WORKOFF, KQRSTCODE_ABTABT },
			{ KQRSTCODE_NOTPK, KQRSTCODE_RESIGN, KQRSTCODE_ABTABT },
			{ KQRSTCODE_NOTPK, KQRSTCODE_BABT, KQRSTCODE_ABTABT },
			{ KQRSTCODE_BTRA, KQRSTCODE_NORMAL, KQRSTCODE_BTRA },
			{ KQRSTCODE_BTRA, KQRSTCODE_LEARLY, KQRSTCODE_LEARLY },
			{ KQRSTCODE_BTRA, KQRSTCODE_NOTPK, KQRSTCODE_ABTABT },
			{ KQRSTCODE_BTRA, KQRSTCODE_BTRA, KQRSTCODE_BTRA },
			{ KQRSTCODE_BTRA, KQRSTCODE_LEAVEH, KQRSTCODE_LEAVEH },
			{ KQRSTCODE_BTRA, KQRSTCODE_WORKOFF, KQRSTCODE_BTRA },
			{ KQRSTCODE_BTRA, KQRSTCODE_RESIGN, KQRSTCODE_BTRA },
			{ KQRSTCODE_BTRA, KQRSTCODE_BABT, KQRSTCODE_BABT },
			{ KQRSTCODE_LEAVEH, KQRSTCODE_NORMAL, KQRSTCODE_LEAVEH },
			{ KQRSTCODE_LEAVEH, KQRSTCODE_LEARLY, KQRSTCODE_LEAVEH },
			{ KQRSTCODE_LEAVEH, KQRSTCODE_NOTPK, KQRSTCODE_LEAVEH },
			{ KQRSTCODE_LEAVEH, KQRSTCODE_BTRA, KQRSTCODE_LEAVEH },
			{ KQRSTCODE_LEAVEH, KQRSTCODE_LEAVEH, KQRSTCODE_LEAVEH },
			{ KQRSTCODE_LEAVEH, KQRSTCODE_WORKOFF, KQRSTCODE_LEAVEH },
			{ KQRSTCODE_LEAVEH, KQRSTCODE_RESIGN, KQRSTCODE_LEAVEH },
			{ KQRSTCODE_LEAVEH, KQRSTCODE_BABT, KQRSTCODE_BABT },
			{ KQRSTCODE_WORKOFF, KQRSTCODE_NORMAL, KQRSTCODE_WORKOFF },
			{ KQRSTCODE_WORKOFF, KQRSTCODE_LEARLY, KQRSTCODE_LEARLY },
			{ KQRSTCODE_WORKOFF, KQRSTCODE_NOTPK, KQRSTCODE_NOTPK },
			{ KQRSTCODE_WORKOFF, KQRSTCODE_BTRA, KQRSTCODE_WORKOFF },
			{ KQRSTCODE_WORKOFF, KQRSTCODE_LEAVEH, KQRSTCODE_WORKOFF },
			{ KQRSTCODE_WORKOFF, KQRSTCODE_WORKOFF, KQRSTCODE_WORKOFF },
			{ KQRSTCODE_WORKOFF, KQRSTCODE_RESIGN, KQRSTCODE_WORKOFF },
			{ KQRSTCODE_WORKOFF, KQRSTCODE_BABT, KQRSTCODE_BABT },
			{ KQRSTCODE_RESIGN, KQRSTCODE_NORMAL, KQRSTCODE_NORMAL },
			{ KQRSTCODE_RESIGN, KQRSTCODE_LEARLY, KQRSTCODE_LEARLY },
			{ KQRSTCODE_RESIGN, KQRSTCODE_NOTPK, KQRSTCODE_ABTABT },
			{ KQRSTCODE_RESIGN, KQRSTCODE_BTRA, KQRSTCODE_BTRA },
			{ KQRSTCODE_RESIGN, KQRSTCODE_LEAVEH, KQRSTCODE_LEAVEH },
			{ KQRSTCODE_RESIGN, KQRSTCODE_WORKOFF, KQRSTCODE_WORKOFF },
			{ KQRSTCODE_RESIGN, KQRSTCODE_RESIGN, KQRSTCODE_NORMAL },
			{ KQRSTCODE_RESIGN, KQRSTCODE_BABT, KQRSTCODE_BABT },
			{ KQRSTCODE_LABT, KQRSTCODE_NORMAL, KQRSTCODE_LABT },
			{ KQRSTCODE_LABT, KQRSTCODE_LEARLY, KQRSTCODE_LABT },
			{ KQRSTCODE_LABT, KQRSTCODE_NOTPK, KQRSTCODE_ABTABT },
			{ KQRSTCODE_LABT, KQRSTCODE_BTRA, KQRSTCODE_LABT },
			{ KQRSTCODE_LABT, KQRSTCODE_LEAVEH, KQRSTCODE_LABT },
			{ KQRSTCODE_LABT, KQRSTCODE_WORKOFF, KQRSTCODE_LABT },
			{ KQRSTCODE_LABT, KQRSTCODE_RESIGN, KQRSTCODE_LABT },
			{ KQRSTCODE_LABT, KQRSTCODE_BABT, KQRSTCODE_ABTABT }
	};

	// 返回值 1 正常 2 迟到 3早退 4 无打卡 5 出差 6 请假
	private CJPALineData<Hrkq_sched> scheds = new CJPALineData<Hrkq_sched>(Hrkq_sched.class);// 班次缓存

	public void calcKQReq(String clcid) throws Exception {
		Hrkq_calc ca = new Hrkq_calc();
		ca.findByID(clcid);
		if (ca.isEmpty()) {
			throw new Exception("ID 为【" + clcid + "】的考勤计算单不存在");
		}
		if (ca.exstat.getAsIntDefault(0) != 2) {// 执行状态1 执行中 2 未在执行
			throw new Exception("考勤计算单正在计算中，请勿重复提交");
		}
		ca.exstat.setAsInt(1);// 标记为正在计算
		ca.save(); // 修改后立即提交，其它线程可见
		CDBConnection con = ca.pool.getCon(this);
		con.setDistimeout(true);
		con.startTrans();
		try {
			ca.findByID4Update(con, clcid, true);
			// if (ca.exstat.getAsIntDefault(0) != 2) {// 执行状态1 执行中 2 未在执行
			// throw new Exception("考勤计算单正在计算中，请勿重复提交");
			// }
			scheds.clear();
			TwoDate tdate = getCalcDate(ca);
			Date cstime = tdate.date1; // 考勤计算开始时间 YYYY-MM-DD 00:00:00
			Date cetime = tdate.date2; // 考勤计算截止时间 YYYY-MM-DD 00:00:00 后一个月
			String clccode = ca.clccode.getValue();
			ca.exectime.setAsDatetime(new Date());
			List<HashMap<String, String>> ers = getErList(ca, cstime, cetime);
			int i = 0;
			int ct = ers.size();
			String erIds="";
			for (HashMap<String, String> er : ers) {
				i++;
				String er_id = er.get("er_id");
				erIds+=er_id+",";
				String noclockstr = er.get("noclock");
				boolean noclock = ((noclockstr != null) && (Integer.valueOf(noclockstr) == 1)) ? true : false;
				// 删除加班 及 可调休信息 如果已经调休了 咋办？ 先禁止有调休的直落班
				delcalrst(er_id, cstime, cetime);// 删除考勤计算结果
				calckq_bywsclist(er_id, cstime, cetime, noclock);// 计算打卡
				calc_Btrips(er_id, cstime, cetime);// 计算出差
				calckq_overtime(er_id, cstime, cetime);// 计算加班
				calconduty(er_id, cstime, cetime);// 计算值班
				ca.filstrato.setAsFloat(i * 100 / ct);
				con.openSql2List("SELECT NOW()");
				System.out.println("【" + clccode + "】执行进度:" + i + "/" + ct + ";er_id:" + er_id);
			}
			erIds=erIds.substring(0, erIds.length()-1);
			CalcKqDayReport(erIds);//运算考勤异常明细
			ca.flishtime.setAsDatetime(new Date());
			ca.costtime.setAsFloat((ca.flishtime.getAsDatetime().getTime() - ca.exectime.getAsDatetime().getTime()) / (float) 1000);
			ca.rstmsg.setValue("成功");
			// ca.exstat.setAsInt(2);// 未在执行中
			Date ntime = getNextExecTime(ca);
			if ((ntime != null) && (ntime.getTime() <= (new Date().getTime()))) {
				ca.rstmsg.setValue(ca.rstmsg.getValue() + " 获取下次执行时间错误");
				ca.nxtexectime.setAsDatetime(null);
			} else {
				ca.nxtexectime.setAsDatetime(ntime);
				// throw new Exception("测试错误");
			}
		} catch (Exception e) {
			ca.rstmsg.setValue(e.getMessage());
			throw e;
		} finally {
			try {
				ca.exstat.setAsInt(2);// 未在执行中
				ca.save(con);
				con.submit();
			} catch (Exception e) {
				e.printStackTrace();
			}
			con.close();
		}

	}
	/**
	 * 
	 * @param ErIds
	 */
	public void CalcKqDayReport(String ErIds){
		try{
			String yerDate=DateUtil.getYerDate();//昨天
			String startDate=DateUtil.getFirstDayOfMonth(yerDate);//昨天当月第一天
			String endDate=DateUtil.getYerDate();
			String delSql="delete from Hr_kq_day_report where date='"+yerDate+"' and er_id in ("+ErIds+")";
			String strSql="select a.er_id,a.employee_code,a.employee_name,t.sbwdk,t1.xbwdk,t2.bq,t3.yerxbwdk,t4.yersbwdk from hr_employee a"+
					" left join (select er_id,count(1) as sbwdk from hrkq_bckqrst where kqdate >= '"+startDate+"' and kqdate<='"+endDate+"'  and frst='4'  GROUP BY er_id)as t on t.er_id=a.er_id"+
					" left join (select er_id,count(1) as xbwdk from hrkq_bckqrst where  kqdate >= '"+startDate+"' and kqdate<='"+endDate+"'   and trst='4'  GROUP BY er_id)as t1 on t1.er_id=a.er_id"+
					" left join (select er_id,count(1) as bq from hrkq_bckqrst where kqdate >= '"+startDate+"' and kqdate<='"+endDate+"'   and (trst='8' or frst='8')  GROUP BY er_id)as t2 on t2.er_id=a.er_id"+
					" left join (select er_id,count(1) as yerxbwdk from hrkq_bckqrst where kqdate ='"+yerDate+"'   and (trst='4')  GROUP BY er_id)as t3 on t3.er_id=a.er_id"+
					" left join (select er_id,count(1) as yersbwdk from hrkq_bckqrst where kqdate ='"+yerDate+"'   and (frst='4')  GROUP BY er_id)as t4 on t4.er_id=a.er_id"+
					" where a.er_id in ("+ErIds+")";
			Hrkq_bckqrst kq_bacqrst=new Hrkq_bckqrst();
			CJPALineData<Hr_kq_day_report> saveReportList = new CJPALineData<Hr_kq_day_report>(Hr_kq_day_report.class);
			//先删除已存在的记录
			int delNum=kq_bacqrst.pool.execsql(delSql);
			Logsw.dblog("删除条数为"+delNum+"执行SQL："+delSql+"");
			List<HashMap<String, String>> erIdList = kq_bacqrst.pool.openSql2List(strSql);
			for(HashMap<String, String>entity : erIdList){
				Hr_kq_day_report report= new Hr_kq_day_report();
				String er_id=entity.get("er_id");
				String employee_code=entity.get("employee_code");
				String employee_name=entity.get("employee_name");
				String sbwdk=entity.get("sbwdk")==null?"0":entity.get("sbwdk");//上班无打卡
				String xbwdk=entity.get("xbwdk")==null?"0":entity.get("xbwdk");//下班无打卡
				String yersbwdk=entity.get("yersbwdk")==null?"0":entity.get("yersbwdk");//昨天上班无打卡
				String yerxbwdk=entity.get("yerxbwdk")==null?"0":entity.get("yerxbwdk");//昨天下班无打卡
				String yerWdkTimes ="";
				int wdk=Integer.parseInt(sbwdk)+Integer.parseInt(xbwdk);//无打卡合计
				int bq=Integer.parseInt(entity.get("bq")==null?"0":entity.get("bq"));//签卡合计
				int yerwdk=Integer.parseInt(yersbwdk)+Integer.parseInt(yerxbwdk);//昨天无打卡合计
				if(wdk>0){
					if(yerwdk>0){
						//昨天有没打卡的，记录没打卡时间段
						String strYerSql="select frtime,totime,frsktime,tosktime from hrkq_bckqrst where er_id='"+er_id+"' and (frst='4' or  trst='4') and kqdate='"+yerDate+"'";
						List<HashMap<String, String>> yerList = kq_bacqrst.pool.openSql2List(strYerSql);
						for(HashMap<String, String>yerEntity : yerList){
							String frtime=yerEntity.get("frtime");
							String totime=yerEntity.get("totime");
							String frsktime=yerEntity.get("frsktime");
							String tosktime=yerEntity.get("tosktime");
							if(frsktime==null||frsktime.equals("")){
								yerWdkTimes+="/"+frtime;
							}
							if(tosktime==null||tosktime.equals("")){
								yerWdkTimes+="/"+totime;	
							}
						}
					}
					if(yerWdkTimes.length()>0){
						report.yer_no_card_times.setValue(yerWdkTimes.substring(1,yerWdkTimes.length()));
					}else{
						report.yer_no_card_times.setValue(null);
					}
					report.yer_no_card_num.setValue(yerwdk);
					report.er_id.setValue(er_id);
					report.employee_code.setValue(employee_code);
					report.employee_name.setValue(employee_name);
					report.no_card_num.setValue(wdk+bq);
					report.make_up_num.setValue(bq);
					report.date.setValue(yerDate);
					saveReportList.add(report);
					if(saveReportList.size()==500){
						//每1000条记录保存一次
						saveReportList.saveBatchSimple();
						saveReportList.clear();
					}
				}
			}
			if(saveReportList.getJpaSize()>0){
				saveReportList.saveBatchSimple();
			}
		
				
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private TwoDate getCalcDate(Hrkq_calc ca) throws Exception {
		int kqdatetype = ca.kqdatetype.getAsIntDefault(0);// 1 时间段 2 前X天 3 当前周 4 当前月
		if (kqdatetype == 0)
			throw new Exception("考勤计算时间区间错误【kqdatetype】不允许为空");

		if (kqdatetype == 1) {
			return new TwoDate(ca.kqstartdate.getAsDatetime(), ca.kqenddate.getAsDatetime());
		}
		if (kqdatetype == 2) {
			Date date2 = new Date();
			Date date1 = Systemdate.dateDayAdd(date2, ca.kqdateperxday.getAsIntDefault(0) * -1);
			return new TwoDate(date1, date2);
		}
		if (kqdatetype == 3) {
			return Systemdate.getFirstAndLastOfWeek(new Date());
		}
		if (kqdatetype == 4) {
			return Systemdate.getFirstAndLastOfMonth(new Date());
		}
		throw new Exception("考勤计算时间区间错误【kqdatetype】错误，不允许为值【" + kqdatetype + "】");
	}

	/**
	 * @param ca
	 * @return 获取下次计算时间
	 * @throws Exception
	 */
	public static Date getNextExecTime(Hrkq_calc ca) throws Exception {
		int schtype = ca.schtype.getAsIntDefault(0); // 1立即 2一次 3每天 4每周 5每月
		if (schtype <= 2)// 没有下次了
			return null;
		String sctime = ca.sctime.getValue();
		if (schtype == 3) {// 3每天
			Date curdate = new Date();
			Date ctime = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(curdate) + " " + sctime);
			if (ctime.getTime() < curdate.getTime())
				return Systemdate.dateDayAdd(ctime, 1);
			else
				return ctime;
		}
		if (schtype == 4) {// 每周
			int weekday = ca.weekday.getAsIntDefault(7);
			Date curdate = new Date();
			Date mdate = Systemdate.getFirstOfWeek(curdate);// 周一所在日期
			mdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(mdate) + " " + sctime);
			Calendar cal = Calendar.getInstance();
			cal.setTime(curdate);
			int day = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
			if (day < weekday) {// 本周继续执行
				return Systemdate.dateDayAdd(mdate, weekday - 1);
			} else {// 下周执行
				return Systemdate.dateDayAdd(mdate, weekday - 1 + 7);
			}
		}
		if (schtype == 5) {// 每月
			int monthday = ca.monthday.getAsIntDefault(1);
			Date curdate = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(curdate);
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH) + 1;
			int day = cal.get(Calendar.DAY_OF_MONTH) + 1;// 获得当前日期月的第几天
			Date cmdate = Systemdate.getDateByStr(y + "-" + m + "-" + monthday + " " + sctime);// 当月执行时间
			if (day < monthday) {// 本月执行
				return cmdate;
			} else {// 下月执行
				return Systemdate.dateMonthAdd(cmdate, 1);
			}
		}
		throw new Exception("考勤计算时间区间错误【schtype】错误，不允许为值【" + schtype + "】");
	}

	/**
	 * 删除员工该时间段考勤 及 加班信息 (所以只能加班费，调休就玩玩了)
	 * 
	 * @param er_id
	 * @param yearmonth
	 * @throws Exception
	 */
	private void delcalrst(String er_id, Date cstime, Date cetime) throws Exception {
		String fdstr = Systemdate.getStrDateyyyy_mm_dd(cstime);
		String tdstr = Systemdate.getStrDateyyyy_mm_dd(cetime);
		String sqlstr = "DELETE FROM hrkq_bckqrst WHERE er_id=" + er_id + " AND kqdate>='" + fdstr + "' AND kqdate<='" + tdstr + "'";
		Hrkq_bckqrst bq = new Hrkq_bckqrst();
		bq.pool.execsql(sqlstr);
		sqlstr = "DELETE FROM hrkq_overtime_list WHERE hrkq_overtime_list.otltype IN(3,4,5) and er_id=" + er_id + " AND kqdate>='" + fdstr + "' AND kqdate<='" + tdstr + "'";
		bq.pool.execsql(sqlstr);
	}

	private void addEmpty2List(List<HashMap<String, String>> emps, HashMap<String, String> emp) {
		if (emp == null)
			return;
		for (HashMap<String, String> emptp : emps) {
			if (emp.get("er_id").equalsIgnoreCase(emptp.get("er_id")))
				return;
		}
		emps.add(emp);
	}

	// 员工ID列表
	private List<HashMap<String, String>> getErList(Hrkq_calc ca, Date time1, Date time2) throws Exception {
		List<HashMap<String, String>> rst = new ArrayList<HashMap<String, String>>();
		for (CJPABase jpa : ca.hrkq_calclines) {
			Hrkq_calcline cal = (Hrkq_calcline) jpa;
			int ttype = cal.ttype.getAsIntDefault(0);
			if (ttype == 1) {
				// 班制组
			} else if (ttype == 2) {
				// 机构
				// String ym = Systemdate.getStrDateByFmt(time2, "yyyy-MM");

				String edym = Systemdate.getStrDateByFmt(time2, "yyyy-MM");// 截止年月
				// int ms = Systemdate.getBetweenMonth(time1, time2);// 相差月数
				String yms = "";
				for (int m = 0; m <= 24; m++) {
					Date curymdate = Systemdate.dateMonthAdd(time1, m);
					String curym = Systemdate.getStrDateByFmt(curymdate, "yyyy-MM");
					yms = yms + "'" + curym + "',";
					if (curym.equalsIgnoreCase(edym))
						break;
				}
				if (yms.isEmpty())
					throw new Exception("获取员工，开始截止日期错误");
				yms = yms.substring(0, yms.length() - 1);

				String sqlstr = "SELECT e.er_id,e.noclock FROM hr_employee e WHERE 1=1   ";
				int incldchd = cal.incldchd.getAsIntDefault(0);
				if (incldchd == 1) {
					Shworg org = new Shworg();
					org.findByID(cal.tid.getValue());
					if (org.isEmpty())
						throw new Exception("没有发现ID为【" + cal.tid.getValue() + "】的机构");
					sqlstr = sqlstr + " and e.idpath like '" + org.idpath.getValue() + "%'";
				} else {
					sqlstr = sqlstr + " and e.orgid=" + cal.tid.getValue();
				}
				sqlstr = sqlstr
						+ " AND EXISTS(SELECT 1 FROM ( "
						+ " SELECT DISTINCT er_id FROM( "
						+ " SELECT er_id FROM hrkq_workschmonthlist l WHERE l.yearmonth in (" + yms + ")  "
						+ " UNION "
						+ " SELECT er_id FROM hrkq_overtime_list l WHERE  DATE_FORMAT(l.edtime,'%Y-%m') in (" + yms + ")  "
						+ " UNION "
						+ " SELECT DISTINCT er_id FROM hrkq_onduty h,hrkq_ondutyline l WHERE h.od_id=l.od_id AND h.stat=9 AND  DATE_FORMAT(l.end_date,'%Y-%m') in (" + yms + ") "
						+ " UNION "
						+ " SELECT DISTINCT er_id FROM hrkq_business_trip l WHERE l.stat=9 AND DATE_FORMAT(l.end_date,'%Y-%m') in (" + yms + ")  "
						+ " ) tb "
						+ " ) l WHERE l.er_id =e.er_id)";
				List<HashMap<String, String>> ers = ca.pool.openSql2List(sqlstr);
				for (HashMap<String, String> er : ers) {
					HashMap<String, String> emp = new HashMap<String, String>();
					emp.put("er_id", er.get("er_id"));
					emp.put("noclock", er.get("noclock"));
					addEmpty2List(rst, emp);
				}
			} else if (ttype == 3) {
				// 个人
				if (rst.indexOf(cal.tid.getValue()) == -1) {
					String sqlstr = "SELECT er_id,noclock FROM hr_employee WHERE er_id=" + cal.tid.getValue();
					List<HashMap<String, String>> ers = ca.pool.openSql2List(sqlstr);
					if (ers.size() > 0) {
						HashMap<String, String> er = ers.get(0);
						HashMap<String, String> emp = new HashMap<String, String>();
						emp.put("er_id", er.get("er_id"));
						emp.put("noclock", er.get("noclock"));
						addEmpty2List(rst, emp);
					}
				}
			}
		}
		return rst;
	}

	/**
	 * @param scid
	 * @return
	 * @throws Exception
	 */
	private Hrkq_sched getCatchSched(String scid) throws Exception {
		Hrkq_sched rst = null;
		for (CJPABase jpa : scheds) {
			Hrkq_sched sched = (Hrkq_sched) jpa;
			if (sched.scid.getValue().equalsIgnoreCase(scid))
				rst = sched;
		}
		if (rst == null) {
			rst = new Hrkq_sched();
			rst.findBySQL("select * from hrkq_sched where scid=" + scid);
			scheds.add(rst);
		}
		return rst;
	}

	/**
	 * 获取某段时间打卡记录 开始时间前一天 截止时间后一天 满足跨天的需求 //需要后两天才能满足跨天需求
	 * 
	 * @param employee_code
	 * @param bgtime
	 * @param edtime
	 * @return
	 * @throws Exception
	 */
	private CJPALineData<Hrkq_swcdlst> getswcdlst(String employee_code, Date bgtime, Date edtime) throws Exception {
		String strbg = Systemdate.getStrDateyyyy_mm_dd(Systemdate.dateDayAdd(bgtime, -1));
		String stred = Systemdate.getStrDateyyyy_mm_dd(Systemdate.dateDayAdd(edtime, 2));
		CJPALineData<Hrkq_swcdlst> rst = new CJPALineData<Hrkq_swcdlst>(Hrkq_swcdlst.class);
		String sqlstr = "select skid, machineno,machno1,readerno, empno, card_number, sktype, synid,DATE_FORMAT(skdate,'%Y-%m-%d %H:%i:00') skdate  "
				+ " from hrkq_swcdlst where empno='" + employee_code + "' and skdate>='" +
				strbg + "' and  skdate<='" + stred + "' order by skdate asc";
		rst.findDataBySQL(sqlstr);
		// System.out.println("打卡记录条数:" + rst.size()); 
		return rst;
	}

	// 查询是否存在有效记录 tp 1 最早的 2 最晚的
	private Hrkq_swcdlst findValidSWCard(Date frTime, Date toTime, CJPALineData<Hrkq_swcdlst> swls, int tp) throws Exception {
		// System.out.println("swls:" + swls.size());
		if (tp == 1) {// 最早
			for (int i = 0; i < swls.size(); i++) {
				Hrkq_swcdlst swl = (Hrkq_swcdlst) swls.get(i);
				Date skdate = swl.skdate.getAsDatetime();
				// System.out.println("打卡:" + Systemdate.getStrDate(skdate) + " : " + skdate.getTime());
				// System.out.println("开始:" + Systemdate.getStrDate(frTime) + " : " + frTime.getTime());
				// System.out.println("截止:" + Systemdate.getStrDate(toTime) + " : " + toTime.getTime());
				if ((skdate.getTime() >= frTime.getTime()) && (skdate.getTime() <= toTime.getTime())) {
					// System.out.println("找到打卡记录");
					return swl;
				}
			}
			return null;
		} else if (tp == 2) {// 最晚
			for (int i = swls.size() - 1; i >= 0; i--) {
				Hrkq_swcdlst swl = (Hrkq_swcdlst) swls.get(i);
				Date skdate = swl.skdate.getAsDatetime();
				// System.out.println("下班打卡有效期:" + Systemdate.getStrDate(frTime) + "--" + Systemdate.getStrDate(toTime) + " 打卡时间:" +
				// Systemdate.getStrDate(skdate));
				if ((skdate.getTime() >= frTime.getTime()) && (skdate.getTime() <= toTime.getTime()))
					return swl;
			}
			return null;
		} else
			throw new Exception("类型错误");
	}

	/**
	 * 班次考勤计算 对比打卡记录 swls：打卡记录
	 * 
	 * @param sl
	 * 班次
	 * @param er_id
	 * @param daystr
	 * 日期字符串 yyyy-mm-dd
	 * @param tp
	 * 1上班 2 下班
	 * @param ernoclock
	 * 用户资料里面免打卡
	 * @param cps
	 * @return 返回值 1 正常 2 迟到 3早退 4 无打卡 5 出差 6 请假
	 * @throws Exception
	 */
	private Bbkqrst calckqby_bcline(Hrkq_sched_line sl, String er_id, String daystr, int tp,
			boolean ernoclock, KqcalcParms cps) throws Exception {
		Bbkqrst rst = new Bbkqrst();

		if (tp == 1) {// 上班
			if ((sl.frnoclock.getAsInt() == 1) || ernoclock) {
				rst.setCode(KQRSTCODE_NORMAL);
				return rst;
			} else {
				Date frtime = gettimebybltime(daystr, sl.frtime.getValue());// 上班时间 可能和考勤时间不一致
				Date frvtimebg = gettimebybltime(daystr, sl.frvtimebg.getValue());// 有效期开始
				Date frvtimeed = gettimebybltime(daystr, sl.frvtimeed.getValue());// 有效期结束
				if (frvtimebg.getTime() > frvtimeed.getTime()) {
					throw new Exception("【" + sl.sclid.getValue() + "】上班有效期设置错误");
				}
				// Logsw.debug("有效打卡时间【" + Systemdate.getStrDate(frvtimebg) + "】-【" + Systemdate.getStrDate(frvtimebg) + "】");

				if (isTrip2Holiday2Wof(rst, cps, frtime)) {// 是否有出差 请假 调休
					// System.out.println("有调休单......");
					rst.print();
					return rst;
				} else {
					Hrkq_swcdlst swl = findValidSWCard(frvtimebg, frvtimeed, cps.swls, 1);// 获取有效打卡记录
					if (swl == null) {
						rst.setCode(KQRSTCODE_NOTPK);
					} else {
						Date pktime = swl.skdate.getAsDatetime();
						Date lattime = Systemdate.dateMinuteAdd(frtime, sl.frafextimeabn.getAsIntDefault(0));// 迟到时间
						int frafextimeabt = sl.frafextimeabt.getAsIntDefault(0);
						Date lattime_kg = Systemdate.dateMinuteAdd(frtime, frafextimeabt);// 迟到旷工时间
						if ((pktime.getTime() > lattime_kg.getTime()) && (frafextimeabt != 0)) {// 迟到旷工
							rst.setCode(KQRSTCODE_LABT);
						} else {
							if (pktime.getTime() > lattime.getTime()) {// 上班推迟打卡超过时间记迟到 分钟
								rst.setCode(KQRSTCODE_LATE);
								rst.setMinte(Math.round((pktime.getTime() - frtime.getTime()) / (1000 * 60)));
							} else {
								if (swl.sktype.getAsIntDefault(0) == 2)
									rst.setCode(KQRSTCODE_RESIGN);
								else
									rst.setCode(KQRSTCODE_NORMAL);
								// System.out.println("上班时间:" + Systemdate.getStrDate(frtime) + "; 上班打卡时间:" + Systemdate.getStrDate(pktime));
								// System.out.println("上班提前分钟:" + Math.round((frtime.getTime() - pktime.getTime()) / (1000 * 60)));
								rst.setMinte_gd(Math.round((frtime.getTime() - pktime.getTime()) / (1000 * 60)));// 上班提前打卡时间
							}
						}
						rst.setPktime(getLTimeByDatetime(daystr, swl.skdate.getAsDatetime()));
					}
					return rst;
				}
			}
		} else if (tp == 2) {// 下班
			if ((sl.tonoclock.getAsInt() == 1) || ernoclock) {
				rst.setCode(KQRSTCODE_NORMAL);
				return rst;
			} else {
				Date totime = gettimebybltime(daystr, sl.totime.getValue());
				Date tovtimebg = gettimebybltime(daystr, sl.tovtimebg.getValue());// 有效期开始
				Date tovtimeed = gettimebybltime(daystr, sl.tovtimeed.getValue());// 有效期结束
				// System.out.println("下班打卡有效期:" + Systemdate.getStrDate(tovtimebg) + "--" + Systemdate.getStrDate(tovtimeed));
				if (tovtimebg.getTime() > tovtimeed.getTime()) {
					throw new Exception("【" + sl.sclid.getValue() + "】下班有效期设置错误");
				}
				if (isTrip2Holiday2Wof(rst, cps, totime)) {// 是否有出差 请假 调休
					return rst;
				} else {
					Hrkq_swcdlst swl = findValidSWCard(tovtimebg, tovtimeed, cps.swls, 2);
					// System.out.println("下班考勤有效打卡记录:" + swl);
					if (swl == null) {// 无有效into记录
						rst.setCode(KQRSTCODE_NOTPK);
					} else {
						Date pktime = swl.skdate.getAsDatetime();

						Date zttime = Systemdate.dateMinuteAdd(totime, (-1 * sl.tobfextimeabn.getAsIntDefault(0)));
						int tobfextimeabt = sl.tobfextimeabt.getAsIntDefault(0);
						// System.out.println("tobfextimeabn:" + sl.tobfextimeabn.getAsIntDefault(0));
						// System.out.println("totime:" + Systemdate.getStrDateMs(totime));
						// System.out.println("zttime:" + Systemdate.getStrDateMs(zttime));
						Date zttime_kg = Systemdate.dateMinuteAdd(totime, (-1 * tobfextimeabt));// 早退旷工时间
						if (pktime.getTime() < zttime_kg.getTime()) {// 早退旷工
							rst.setCode(KQRSTCODE_BABT);
						} else {
							if (pktime.getTime() < zttime.getTime()) {// 上班推迟打卡超过时间记迟到 分钟
								rst.setCode(KQRSTCODE_LEARLY);
								rst.setMinte(Math.round((totime.getTime() - pktime.getTime()) / (1000 * 60)));
							} else {
								if (swl.sktype.getAsIntDefault(0) == 2)
									rst.setCode(KQRSTCODE_RESIGN);
								else
									rst.setCode(KQRSTCODE_NORMAL);
								rst.setMinte_gd(Math.round((pktime.getTime() - totime.getTime()) / (1000 * 60)));// 下班晚打卡时间
							}
						}
						rst.setPktime(getLTimeByDatetime(daystr, swl.skdate.getAsDatetime()));
					}
					return rst;
				}
			}
		} else
			throw new Exception("类型错误");
	}

	/**
	 * 根据班次时间 返回日期，考虑跨天解析
	 * 
	 * @param daystr
	 * @param timestr
	 * @return
	 */
	private Date gettimebybltime(String daystr, String timestr) {
		int tp = 0;
		String tstr = timestr;
		if (timestr.substring(0, 1).equalsIgnoreCase("Y")) {// 前一天
			tp = -1;
			tstr = timestr.substring(1, timestr.length());
		}
		if (timestr.substring(0, 1).equalsIgnoreCase("T")) {// 后一天
			tp = 1;
			tstr = timestr.substring(1, timestr.length());
		}
		String dtstr = daystr + " " + tstr;
		// System.out.println("dtstr:" + dtstr);
		Date rst = Systemdate.getDateByStr(daystr + " " + tstr);
		rst = Systemdate.dateDayAdd(rst, tp);
		return rst;
	}

	/**
	 * 和上面方法gettimebybltime相反
	 * 根据 考勤日期 和 打卡时间(Datetime) 计算 打卡时间HH:mm 加上前一天或后一天标志
	 * 
	 * @param daystr
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	private static String getLTimeByDatetime(String daystr, Date date) throws ParseException {
		Date day = Systemdate.getDateByStr(daystr);
		Date kday = Systemdate.getDateYYYYMMDD(date);
		long dayt = day.getTime();
		long kdayt = kday.getTime();
		String rst = Systemdate.getStrDateByFmt(date, "HH:mm");
		if (dayt < kdayt) {// 后一天打的卡
			rst = "T" + rst;
		} else if (dayt > kdayt) {
			rst = "Y" + rst;// 前一天打的卡
		}
		return rst;
	}

	/**
	 * 处理是否出差请假调休
	 */
	private boolean isTrip2Holiday2Wof(Bbkqrst rst, KqcalcParms cps, Date time) {
		Hrkq_business_trip trip = isBTrip(cps, time);
		if ((trip != null) && !trip.isEmpty()) {
			rst.setCode(KQRSTCODE_BTRA);
			rst.setExtcode(trip.bta_code.getValue());
			return true;
		}
		Hrkq_holidayapp hd = isHoliday(cps, time);
		if ((hd != null) && !hd.isEmpty()) {
			// AND (viodeal<>2 OR viodeal IS NULL)
			if (hd.viodeal.getAsIntDefault(0) == 2) {
				rst.setCode(KQRSTCODE_NOTPK);// 标记为无打卡
				rst.setExtcode(hd.hacode.getValue());
				rst.setExttype(hd.htid.getValue());
			} else {
				rst.setCode(KQRSTCODE_LEAVEH);
				rst.setExtcode(hd.hacode.getValue());
				rst.setExttype(hd.htid.getValue());
			}
			return true;
		}
		Hrkq_wkoff wk = isChgWordoff(cps, time);
		if ((wk != null) && !wk.isEmpty()) {
			rst.setCode(KQRSTCODE_WORKOFF);
			rst.setExttype(wk.wocode.getValue());
			return true;
		}
		return false;
	}

	/**
	 * 按列表是否出差
	 * 
	 * @param cps
	 * @param time
	 * @return
	 */
	private Hrkq_business_trip isBTrip(KqcalcParms cps, Date time) {
		for (CJPABase jpa : cps.trips) {
			Hrkq_business_trip trip = (Hrkq_business_trip) jpa;
			if ((trip.begin_date.getAsDatetime().getTime() <= time.getTime())
					&& (trip.end_date.getAsDatetime().getTime() >= time.getTime()))
				return trip;
		}
		return null;
	}

	private Hrkq_holidayapp isHoliday(KqcalcParms cps, Date time) {
		for (CJPABase jpa : cps.hds) {
			Hrkq_holidayapp hd = (Hrkq_holidayapp) jpa;
			if ((hd.timebg.getAsDatetime().getTime() <= time.getTime())
					&& (hd.timeedtrue.getAsDatetime().getTime() >= time.getTime()))
				return hd;
		}
		return null;
	}

	private Hrkq_wkoff isChgWordoff(KqcalcParms cps, Date time) {
		for (CJPABase jpa : cps.wks) {
			Hrkq_wkoff trip = (Hrkq_wkoff) jpa;
			if ((trip.begin_date.getAsDatetime().getTime() <= time.getTime())
					&& (trip.end_date.getAsDatetime().getTime() >= time.getTime()))
				return trip;
		}
		return null;
	}

	/*
	 * // 是否出差
	 * private Hrkq_business_trip isBTrip(String er_id, Date time) throws Exception {
	 * String sqlstr = "SELECT * FROM hrkq_business_trip WHERE er_id=" + er_id + " AND begin_date<='"
	 * + Systemdate.getStrDate(time) + "' AND end_date >='" + Systemdate.getStrDate(time) + "' AND stat=9";
	 * Hrkq_business_trip trip = new Hrkq_business_trip();
	 * trip.findBySQL(sqlstr);
	 * return trip;
	 * }
	 * // 是否请假
	 * private Hrkq_holidayapp isHoliday(String er_id, Date time) throws Exception {
	 * String sqlstr = "SELECT * FROM hrkq_holidayapp WHERE er_id=" + er_id + " AND timebg<='"
	 * + Systemdate.getStrDate(time) + "' AND timeedtrue >='" + Systemdate.getStrDate(time) + "' AND stat=9 and (viodeal<>2 OR viodeal IS NULL) ";// 违规处理不为2
	 * Hrkq_holidayapp hd = new Hrkq_holidayapp();
	 * hd.findBySQL(sqlstr);
	 * return hd;
	 * }
	 * // 是否调休
	 * private Hrkq_wkoff isChgWordoff(String er_id, Date time) throws Exception {
	 * String sqlstr = "SELECT * FROM hrkq_wkoff WHERE er_id=" + er_id + " AND begin_date<='"
	 * + Systemdate.getStrDate(time) + "' AND end_date >='" + Systemdate.getStrDate(time) + "' AND stat=9";
	 * Hrkq_wkoff wk = new Hrkq_wkoff();
	 * wk.findBySQL(sqlstr);
	 * return wk;
	 * }
	 */

	private boolean isempty(String v) {
		return ((v == null) || v.isEmpty());
	}

	/**
	 * 班制考勤计算 某人一天
	 * 
	 * @param er_id
	 * @param sched
	 * 半只班次
	 * @param ernoclock
	 * 是否免打卡
	 * @param day
	 * 日期
	 * @param cps
	 * 考勤计算需要用到的参数
	 * @throws Exception
	 */
	private void calckq_bz(String er_id, Hrkq_sched sched, boolean ernoclock, Date day, KqcalcParms cps) throws Exception {
		String daystr = Systemdate.getStrDateyyyy_mm_dd(day);
		System.out.println("人事ID:" + er_id + " 计算日期:" + daystr);
		CJPALineData<Hrkq_sched_line> sls = sched.hrkq_sched_lines;
		Hrkq_bckqrst bq = new Hrkq_bckqrst();
		for (CJPABase jpa : sls) {
			Hrkq_sched_line sl = (Hrkq_sched_line) jpa;// 班次

			Bbkqrst sbbz = calckqby_bcline(sl, er_id, daystr, 1, ernoclock, cps);
			Bbkqrst xbbz = calckqby_bcline(sl, er_id, daystr, 2, ernoclock, cps);

			String sqlstr = "SELECT * FROM hrkq_bckqrst WHERE er_id=" + er_id + " AND kqdate='" + daystr + "' and sclid=" + sl.sclid.getValue();
			bq.clear();
			bq.findBySQL(sqlstr);
			bq.er_id.setValue(er_id); // 申请人档案ID
			bq.kqdate.setValue(daystr); // 考勤日期
			bq.scdname.setValue(sched.scdname.getValue()); // 班制
			bq.sclid.setValue(sl.sclid.getValue()); // 班次ID
			bq.bcno.setValue(sl.bcno.getValue()); // 班次号
			bq.frtime.setValue(sl.frtime.getValue()); // 上班时间
			bq.totime.setValue(sl.totime.getValue()); // 下班时间
			bq.frsktime.setValue(sbbz.getPktime()); // 上班打卡时间
			bq.tosktime.setValue(xbbz.getPktime()); // 下班打卡时间
			bq.frst.setAsInt(sbbz.getCode()); // 上班考勤结果 1 正常 2 迟到 3早退 4未打卡 5 出差 6 请假 7 调休
			bq.trst.setAsInt(xbbz.getCode()); // 下班考勤结果 1 正常 2 迟到 3早退 4未打卡 5 出差 6 请假 7 调休
			bq.mtslate.setAsInt(sbbz.getMinte()); // 迟到时间分钟
			bq.mtslearly.setAsInt(xbbz.getMinte()); // 早退时间分钟
			int lrst = calcbcrst(sbbz.getCode(), xbbz.getCode());
			bq.lrst.setAsInt(lrst);// 班次考勤结果
			String extcode = (!isempty(xbbz.getExtcode())) ? xbbz.getExtcode() : ((!isempty(sbbz.getExtcode())) ? sbbz.getExtcode() : null);
			bq.extcode.setValue(extcode);
			String exttype = (!isempty(xbbz.getExttype())) ? xbbz.getExttype() : ((!isempty(sbbz.getExttype())) ? sbbz.getExttype() : null);
			bq.exttype.setValue(exttype);
			int isabnom = ((lrst == KQRSTCODE_NORMAL) || (lrst == KQRSTCODE_BTRA) || (lrst == KQRSTCODE_LEAVEH) || (lrst == KQRSTCODE_WORKOFF) || (lrst == KQRSTCODE_RESIGN)) ? 2
					: 1;
			bq.isabnom.setAsInt(isabnom); // 是否异常
			bq.save();

			int frst = bq.frst.getAsInt();
			int trst = bq.trst.getAsInt();
			if (sl.exworktime.getAsFloatDefault(0) != 0) {// 处理定长加班
				// 上班正常、迟到，下班正常 早退 允许生成加班信息
				if (isIntInArr(frst, new int[] { 1, 8 }) && isIntInArr(trst, new int[] { 1, 8 }))// 正常或签卡生成
					appendOvertime(sl, bq);
			}

			if ((sl.frbfwe.getAsIntDefault(0) == 1) && (isIntInArr(frst, new int[] { 1, 8 }))) {// 上班计加班
				int frbfwemin = sl.frbfwemin.getAsIntDefault(0);
				System.out.println("上班早打卡超过frbfwemin分钟计加班 xbbz.getMinte_gd():" + xbbz.getMinte_gd() + " frbfwemin:" + frbfwemin);
				if (sbbz.getMinte_gd() > frbfwemin) {// 上班早打卡超过frbfwemin分钟计加班
					int jbminte = sbbz.getMinte_gd();
					if (jbminte > sl.frbfwemax.getAsIntDefault(0)) {// toafwemax下班加班最长
						jbminte = sl.frbfwemax.getAsIntDefault(0);
					}
					float hours = jbminte / 60f; // 小时
					appendOvertime(daystr, sl, bq, hours, 4);
				}
			}

			// System.out.println(daystr + " toafwe:" + sl.toafwe.getAsIntDefault(0));
			if ((sl.toafwe.getAsIntDefault(0) == 1) && (isIntInArr(trst, new int[] { 1, 8 }))) {// 下班计加班
				int toafwemin = sl.toafwemin.getAsIntDefault(0);
				// System.out.println("getMinte_gd和toafwemin:" + xbbz.getMinte_gd() + ":" + toafwemin);
				if (xbbz.getMinte_gd() > toafwemin) {// 下班晚打卡超过toafwemin分钟计加班
					// System.out.println("xbbz.getMinte_gd() > toafwemin：" + (xbbz.getMinte_gd() > toafwemin));
					int jbminte = xbbz.getMinte_gd();
					if (jbminte > sl.toafwemax.getAsIntDefault(0)) {// toafwemax下班加班最长
						jbminte = sl.toafwemax.getAsIntDefault(0);
					}
					float hours = jbminte / 60f; // 小时
					// System.out.println("保存加班时间" + daystr + ":" + hours);
					appendOvertime(daystr, sl, bq, hours, 5);
				}
			}

		}
	}

	private boolean isIntInArr(int v, int[] vs) {
		for (int p : vs) {
			if (v == p)
				return true;
		}
		return false;
	}

	private int calcbcrst(int frst, int trst) throws Exception {
		for (int[] mpar : CKQ_MAP) {
			if ((mpar[0] == frst) && (mpar[1] == trst))
				return mpar[2];
		}
		throw new Exception("上班考勤结果【" + frst + "】下班考勤结果【" + trst + "】无对应的计算MAP");
	}

	/**
	 * @param sl
	 * @param bq
	 * @param hours
	 * @param otltype
	 * 4 上班加班 5下班加班
	 * @throws Exception
	 */
	private void appendOvertime(String daystr, Hrkq_sched_line sl, Hrkq_bckqrst bq, float hours, int otltype) throws Exception {
		int exdealtype = sl.exdealtype.getAsIntDefault(1);
		float ovhours = 0;
		if (exdealtype == 1) {
			float mu = Float.valueOf(HrkqUtil.getParmValue("JBTXMINUNIT"));// 加班调休最小单位 4h
			// ovhours = (hours - hours % mu);
			ovhours = getTXHour(ovhours);

		} else if (exdealtype == 2) {
			float mu = 0.5f;// 加班计算加班费最小时间单位
			ovhours = (hours - hours % mu);
		} else
			throw new Exception("班次调休类型错误");
		if (ovhours > 0) {
			Hr_employee emp = new Hr_employee();
			emp.findByID(bq.er_id.getValue(), false);
			if (emp.isEmpty())
				throw new Exception("ID为【" + bq.er_id.getValue() + "】的人事资料不存在");
			// 对于加班列表，以人事ID+日期+班次ID为 主关键字检索
			String sqlstr = "select * from hrkq_overtime_list where otltype=" + otltype + " and er_id=" + bq.er_id.getValue()
					+ " and kqdate='" + Systemdate.getStrDateByFmt(bq.kqdate.getAsDatetime(), "yyyy-MM-dd")
					+ "' and oth_id=" + sl.sclid.getValue();
			Hrkq_overtime_list ol = new Hrkq_overtime_list();
			ol.findBySQL(sqlstr);
			ol.oth_id.setValue(sl.sclid.getValue()); // 加班时间明细ID/班次ID
			ol.otl_id.setValue(sl.scid.getValue()); // 加班申请明细ID/班制ID
			ol.otltype.setAsInt(otltype); // 源单类型 1 批量申请单 2 单独申请 3 班次固定加班 4上班加班 5 下班加班
			ol.ot_id.setValue("0"); // 加班申请ID
			ol.ot_code.setValue("0"); // 加班申请编码
			ol.kqdate.setValue(bq.kqdate.getValue()); // 考勤日期 针对考勤计算自动生成的加班信息
			ol.er_id.setValue(emp.er_id.getValue()); // 申请人档案ID
			ol.employee_code.setValue(emp.employee_code.getValue()); // 申请人工号
			ol.employee_name.setValue(emp.employee_name.getValue()); // 申请人姓名
			ol.dealtype.setValue(sl.exdealtype.getValue()); // 加班处理 1 调休 2 计加班费
			int needchedksb = (sl.frnoclock.getAsIntDefault(0) == 2) ? 1 : 2;
			int needchedkxb = (sl.tonoclock.getAsIntDefault(0) == 2) ? 1 : 2;
			ol.needchedksb.setAsInt(needchedksb);
			ol.needchedkxb.setAsInt(needchedkxb);

			Date totime = (sl.totime.isEmpty()) ? null : gettimebybltime(daystr, sl.totime.getValue());// Systemdate.getDateByStr(daystr + " " +
																										// sl.totime.getValue());/
			Date tosktime = (bq.tosktime.isEmpty()) ? null : gettimebybltime(daystr, bq.tosktime.getValue());// Systemdate.getDateByStr(daystr + " " +
																												// bq.tosktime.getValue());
			// System.out.println("tosktime:" + daystr + " " + bq.tosktime.getValue() + " " + Systemdate.getStrDate(tosktime));

			ol.bgtime.setAsDatetime(totime); // 上班时间
			ol.edtime.setAsDatetime(tosktime); // 下班时间
			ol.bgpktime.setAsDatetime(totime); // 上班打卡时间
			ol.edpktime.setAsDatetime(tosktime); // 下班打卡时间
			ol.frst.setValue("1"); // 上班考勤结果 1 正常 2 迟到 3早退 4未打卡
			ol.trst.setValue("1"); // 下班考勤结果 1 正常 2 迟到 3早退 4未打卡
			ol.applyhours.setValue("0"); // 申请时数
			ol.otrate.setValue("1"); // 加班倍率
			ol.othours.setAsFloat(ovhours); // 加班时数
			ol.deductoff.setValue("0"); // 扣休息时数
			ol.allfreetime.setValue("0"); // 调休时长小时
			ol.freeedtime.setValue("0"); // 已休息时间小时
			ol.allotamont.setValue("0"); // 加班费
			ol.save();
			if (exdealtype == 1)
				throw new Exception("系统暂不允许调休的直落班");
			// if (exdealtype == 1) {// 调休
			// CDBConnection con = ol.pool.getCon(this);
			// try {
			// Date valdate = Systemdate.dateMonthAdd(tosktime, Integer.valueOf(HrkqUtil.getParmValue("TB_LEAVE_VAILM")));// 加上月份
			// CtrHrkq_leave_blance.putLeave_blance(con, ol.otlistid.getValue(), ol.ot_code.getValue(), "加班", 2, ovhours, valdate, ol.er_id.getValue(),
			// "考勤计算自动生成");
			// con.submit();
			// } catch (Exception e) {
			// con.rollback();
			// throw e;
			// }
			// }
		}
	}

	/**
	 * 生成班次 固定加班信息
	 * 
	 * @param sl
	 * @param bq
	 * @throws Exception
	 */
	private void appendOvertime(Hrkq_sched_line sl, Hrkq_bckqrst bq) throws Exception {
		Hr_employee emp = new Hr_employee();
		emp.findByID(bq.er_id.getValue(), false);
		if (emp.isEmpty())
			throw new Exception("ID为【" + bq.er_id.getValue() + "】的人事资料不存在");
		float h = sl.exworktime.getAsFloatDefault(0) / 60;
		float othours = (float) (Math.floor(h / 0.5) * 0.5);// 最小单位半小时 小数去掉 不做四舍五入
		if (othours == 0)
			return;
		// 此处可以判断打卡异常是否生成加班记录
		// 对于加班列表，以人事ID+日期+班次ID为 主关键字检索
		String sqlstr = "select * from hrkq_overtime_list where otltype=3 and er_id=" + bq.er_id.getValue()
				+ " and kqdate='" + Systemdate.getStrDateByFmt(bq.kqdate.getAsDatetime(), "yyyy-MM-dd")
				+ "' and oth_id=" + sl.sclid.getValue();

		String daystr = Systemdate.getStrDateyyyy_mm_dd(bq.kqdate.getAsDatetime());

		Hrkq_overtime_list ol = new Hrkq_overtime_list();
		ol.findBySQL(sqlstr);
		ol.oth_id.setValue(sl.sclid.getValue()); // 加班时间明细ID/班次ID
		ol.otl_id.setValue(sl.scid.getValue()); // 加班申请明细ID/班制ID
		ol.otltype.setValue("3"); // 源单类型 1 批量申请单 2 单独申请 3 考勤计算
		ol.ot_id.setValue("0"); // 加班申请ID
		ol.ot_code.setValue("0"); // 加班申请编码
		ol.kqdate.setValue(bq.kqdate.getValue()); // 考勤日期 针对考勤计算自动生成的加班信息
		ol.er_id.setValue(emp.er_id.getValue()); // 申请人档案ID
		ol.employee_code.setValue(emp.employee_code.getValue()); // 申请人工号
		ol.employee_name.setValue(emp.employee_name.getValue()); // 申请人姓名
		ol.over_type.setValue(sl.over_type.getValue());
		ol.dealtype.setValue(sl.exdealtype.getValue()); // 加班处理 1 调休 2 计加班费
		// int needchedk = ((sl.frnoclock.getAsIntDefault(0) == 2) || (sl.frnoclock.getAsIntDefault(0) == 2)) ? 1 : 2;
		// ol.needchedk.setAsInt(needchedk); // 需要打卡
		int needchedksb = (sl.frnoclock.getAsIntDefault(0) == 2) ? 1 : 2;
		int needchedkxb = (sl.tonoclock.getAsIntDefault(0) == 2) ? 1 : 2;
		ol.needchedksb.setAsInt(needchedksb);
		ol.needchedkxb.setAsInt(needchedkxb);

		Date totime = (sl.totime.isEmpty()) ? null : gettimebybltime(daystr, sl.totime.getValue());
		// System.out.println("111111111111111111str tosktime:"+daystr + " " + bq.tosktime.getValue());
		Date tosktime = (bq.tosktime.isEmpty()) ? null : gettimebybltime(daystr, bq.tosktime.getValue()); // Systemdate.getDateByStr(daystr + " " + );
		// if(tosktime==null)
		// System.out.println("22222222222222tosktime:null");
		// else
		// System.out.println("22222222222222tosktime:"+Systemdate.getStrDate(tosktime));
		ol.bgtime.setValue((totime == null) ? null : Systemdate.getStrDateyyyy_mm_dd(totime)); // 上班时间
		ol.edtime.setAsDatetime(totime); // 下班时间
		ol.bgpktime.setValue(ol.bgtime.getValue()); // 上班打卡时间
		ol.edpktime.setAsDatetime(tosktime);// 下班打卡时间
		ol.frst.setValue("1"); // 上班考勤结果 1 正常 2 迟到 3早退 4未打卡
		ol.trst.setValue("1"); // 下班考勤结果 1 正常 2 迟到 3早退 4未打卡
		ol.applyhours.setValue("0"); // 申请时数
		ol.otrate.setValue("1"); // 加班倍率
		ol.othours.setAsFloat(othours); // 加班时数
		ol.deductoff.setValue("0"); // 扣休息时数
		ol.allfreetime.setValue("0"); // 调休时长小时
		ol.freeedtime.setValue("0"); // 已休息时间小时
		ol.allotamont.setValue("0"); // 加班费
		ol.save();
		if (sl.exdealtype.getAsIntDefault(0) == 1)
			throw new Exception("系统暂不允许调休的直落班");
		// if (sl.exdealtype.getAsIntDefault(0) == 1) {// 调休
		// CDBConnection con = ol.pool.getCon(this);
		// try {
		// Date valdate = Systemdate.dateMonthAdd(bq.tosktime.getAsDatetime(), Integer.valueOf(HrkqUtil.getParmValue("TB_LEAVE_VAILM")));// 加上月份
		// CtrHrkq_leave_blance.putLeave_blance(con, ol.otlistid.getValue(), ol.ot_code.getValue(), "加班", 2, othours, valdate,
		// ol.er_id.getValue(),
		// "考勤计算自动生成");
		// con.submit();
		// } catch (Exception e) {
		// con.rollback();
		// throw e;
		// }
		// }
	}

	/*
	 * // 某人一月考勤计算 wcm:默认月度排班; md:当月最大天数
	 * private void calckq_ermonth(Hrkq_workschmonthlist wcm, int md, boolean ernoclock) throws Exception {
	 * String er_id = wcm.er_id.getValue();
	 * Hr_employee emp = new Hr_employee();
	 * emp.findByID(er_id, false);
	 * if (emp.isEmpty())
	 * throw new Exception("ID为【" + er_id + "】的人事资料不存在");
	 * Date ljdate = emp.ljdate.getAsDatetime();
	 * CJPALineData<Hrkq_swcdlst> swls = getswcdlst(wcm.employee_code.getValue(), wcm.yearmonth.getValue());// 一月的打卡记录
	 * for (int i = 1; i <= md; i++) {
	 * Date day = new SimpleDateFormat("yyyy-MM-dd").parse(wcm.yearmonth.getValue() + "-" + rightSubstr("0" + i, 2));
	 * String daystr = Systemdate.getStrDateyyyy_mm_dd(day);
	 * String scid = wcm.cfield("scid" + i).getValue();
	 * Logsw.debug("计算【" + wcm.employee_name.getValue() + "】" + "【" + daystr + "】考勤");
	 * if ((ljdate == null) || (ljdate.getTime() >= day.getTime())) {
	 * if ((scid == null) || (scid.isEmpty())) {
	 * // 没排班
	 * } else {
	 * Hrkq_sched sched = getCatchSched(scid);
	 * calckq_bz(er_id, sched, daystr, swls, ernoclock);
	 * }
	 * }
	 * }
	 * }
	 */

	/**
	 * 计算指定时间内某人排班 new
	 * 
	 * @param er_id
	 * @param cstime
	 * @param cetime
	 * @throws Exception
	 */
	public void calckq_bywsclist(String er_id, Date cstime, Date cetime, boolean ernoclock) throws Exception {
		Hr_employee emp = new Hr_employee();
		emp.findByID(er_id, false);
		if (emp.isEmpty())
			throw new Exception("ID为【" + er_id + "】的人事资料不存在");
		Date kqdate_end = emp.kqdate_end.getAsDatetime();

		List<KqpbInfo> pbs = getpblist(er_id, cstime, cetime);// 获取所有排班
		CJPALineData<Hrkq_business_trip> trips = get_trps(er_id, cstime, cetime);// 出差
		CJPALineData<Hrkq_holidayapp> hds = get_hds(er_id, cstime, cetime);// 请假
		CJPALineData<Hrkq_wkoff> wks = get_wks(er_id, cstime, cetime);// 调休
		CJPALineData<Hrkq_swcdlst> swls = getswcdlst(emp.employee_code.getValue(), cstime, cetime);// 打卡记录

		KqcalcParms cps = new KqcalcParms();
		cps.swls = swls;
		cps.trips = trips;
		cps.wks = wks;
		cps.hds = hds;
		System.out.println("pbs.size():" + pbs.size());
		for (KqpbInfo pb : pbs) {
			Date day = pb.kqday;
			if ((kqdate_end == null) || (kqdate_end.getTime() >= day.getTime())) {
				if ((pb.scid == null) || (pb.scid.isEmpty())) {// 没排班

				} else {
					Hrkq_sched sched = getCatchSched(pb.scid);
					calckq_bz(er_id, sched, ernoclock, day, cps);// 计算一天考勤
				}
			}
		}
	}

	/**
	 * 获取某人 指定时间段内排班的班制ID 跨月没问题
	 * 
	 * @param er_id
	 * @param cstime
	 * @param cetime
	 * @return
	 * @throws Exception
	 */
	private List<KqpbInfo> getpblist(String er_id, Date cstime, Date cetime) throws Exception {
		String edym = Systemdate.getStrDateByFmt(cetime, "yyyy-MM");// 截止年月
		Date netime = Systemdate.dateDayAdd(cetime, 1);//
		// int ms = Systemdate.getBetweenMonth(cstime, cetime);// 相差月数
		String yms = "";
		for (int m = 0; m <= 24; m++) {
			Date curymdate = Systemdate.dateMonthAdd(cstime, m);
			String curym = Systemdate.getStrDateByFmt(curymdate, "yyyy-MM");
			yms = yms + "'" + curym + "',";
			if (curym.equalsIgnoreCase(edym))
				break;
		}
		if (yms.isEmpty())
			throw new Exception("获取排班列表，开始截止日期错误");
		yms = yms.substring(0, yms.length() - 1);
		String sqltb = "";
		for (int i = 1; i <= 31; i++) {
			sqltb = sqltb + " SELECT DATE_FORMAT(CONCAT(yearmonth,'-" + i + "'),'%Y-%m-%d')kqdate,scid" + i + " scid FROM hrkq_workschmonthlist WHERE er_id=" + er_id + " AND yearmonth IN(" + yms + ") union";
		}
		sqltb = sqltb.substring(0, sqltb.length() - 5);
		String strcstime = Systemdate.getStrDateyyyy_mm_dd(cstime);
		String strcetime = Systemdate.getStrDateyyyy_mm_dd(netime);
		String sqlstr = "SELECT * FROM (" + sqltb + ") tb "
				+ " WHERE tb.kqdate>='" + strcstime + "' AND tb.kqdate<'" + strcetime + "' and scid is not null"
				+ " ORDER BY kqdate";

		// System.out.println("1111111111111:"+sqlstr);
		List<HashMap<String, String>> rows = new Hrkq_workschmonthlist().pool.openSql2List(sqlstr);
		List<KqpbInfo> rst = new ArrayList<KqpbInfo>();
		for (HashMap<String, String> row : rows) {
			Date kqdate = Systemdate.getDateByStr(row.get("kqdate").toString());
			rst.add(new KqpbInfo(kqdate, row.get("scid").toString()));
		}
		return rst;
	}

	/**
	 * 查询某人一段时间所有有效出差单
	 * 
	 * @param er_id
	 * @param cstime
	 * @param cetime
	 * @return
	 * @throws Exception
	 */
	private CJPALineData<Hrkq_business_trip> get_trps(String er_id, Date cstime, Date cetime) throws Exception {
		String st1 = Systemdate.getStrDateyyyy_mm_dd(cstime);
		String st2 = Systemdate.getStrDateyyyy_mm_dd(Systemdate.dateDayAdd(cetime, 1));
		String sqlstr = "SELECT * FROM hrkq_business_trip WHERE er_id=" + er_id + " AND stat=9 "
				+ " AND ((begin_date>='" + st1 + "' AND begin_date<='" + st2 + "')"
				+ " ||(end_date>='" + st1 + "' AND end_date<='" + st2 + "') "
				+ " ||(begin_date<='" + st1 + "' AND end_date>'" + st2 + "'))";
		CJPALineData<Hrkq_business_trip> rst = new CJPALineData<Hrkq_business_trip>(Hrkq_business_trip.class);
		rst.findDataBySQL(sqlstr, false, false);
		return rst;
	}

	/**
	 * 获取某人一段时间有效请假记录
	 * 
	 * @param er_id
	 * @param cstime
	 * @param cetime
	 * @return
	 * @throws Exception
	 */
	private CJPALineData<Hrkq_holidayapp> get_hds(String er_id, Date cstime, Date cetime) throws Exception {
		String st1 = Systemdate.getStrDateyyyy_mm_dd(cstime);
		String st2 = Systemdate.getStrDateyyyy_mm_dd(Systemdate.dateDayAdd(cetime, 1));
		String sqlstr = "SELECT * FROM hrkq_holidayapp WHERE er_id=" + er_id + " AND stat=9 "// AND (viodeal<>2 OR viodeal IS NULL)
				+ " AND ((timebg>='" + st1 + "' AND timebg<='" + st2 + "')"
				+ " ||(timeedtrue>='" + st1 + "' AND timeedtrue<='" + st2 + "') "
				+ " ||(timebg<='" + st1 + "' AND timeedtrue>='" + st2 + "'))";

		CJPALineData<Hrkq_holidayapp> rst = new CJPALineData<Hrkq_holidayapp>(Hrkq_holidayapp.class);
		rst.findDataBySQL(sqlstr, false, false);
		return rst;
	}

	private CJPALineData<Hrkq_wkoff> get_wks(String er_id, Date cstime, Date cetime) throws Exception {
		String st1 = Systemdate.getStrDateyyyy_mm_dd(cstime);
		String st2 = Systemdate.getStrDateyyyy_mm_dd(Systemdate.dateDayAdd(cetime, 1));
		String sqlstr = "SELECT * FROM hrkq_wkoff WHERE er_id=" + er_id + " AND stat=9 "
				+ " AND ((begin_date>='" + st1 + "' AND begin_date<='" + st2 + "')"
				+ " ||(end_date>='" + st1 + "' AND end_date<='" + st2 + "') "
				+ " ||(begin_date<='" + st1 + "' AND end_date>'" + st2 + "'))";
		CJPALineData<Hrkq_wkoff> rst = new CJPALineData<Hrkq_wkoff>(Hrkq_wkoff.class);
		rst.findDataBySQL(sqlstr, false, false);
		return rst;
	}

	// 出差 /////////////////////////////////

	private CJPALineData<Hrkq_workschmonthlist> getwmlist(String er_id, Date bgtime, Date edtime) throws Exception {
		String ymbg = Systemdate.getStrDateByFmt(bgtime, "yyyy-MM");
		String ymed = Systemdate.getStrDateByFmt(edtime, "yyyy-MM");
		String sqlstr = "SELECT * FROM hrkq_workschmonthlist WHERE er_id=" + er_id + " AND yearmonth>='" + ymbg + "' AND yearmonth<='" + ymed + "'";
		CJPALineData<Hrkq_workschmonthlist> rst = new CJPALineData<Hrkq_workschmonthlist>(Hrkq_workschmonthlist.class);
		rst.findDataBySQL(sqlstr, true, false);
		return rst;
	}

	// 计算日期之间小时差 每天8小时 半天4小时 最小单位 半天
	// bgtime/edtime yyyy-MM-dd hh:mm
	public static float calcDateDiffHH(Date bgtime, Date edtime) {
		Date btdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(bgtime));// begin date yyyy-MM-dd
		Date eddate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(edtime));// end date yyyy-MM-dd
		float allhours = 0;
		Date dttem = btdate;
		while (dttem.getTime() <= eddate.getTime()) {
			float dayhours = 0f;
			if (btdate.getTime() == eddate.getTime()) {// 同一天
				dayhours = ((float) (edtime.getTime() - bgtime.getTime())) / (1000 * 60 * 60);
				if (dayhours < 3.5)
					dayhours = 0;
				else if (dayhours < 7)
					dayhours = 4;
				else
					dayhours = 8;
			} else {
				if (dttem.getTime() == btdate.getTime()) {// 第一天
					dayhours = getAllFreeHours(bgtime, 1);
				} else if (dttem.getTime() == eddate.getTime()) {// 最后一天
					dayhours = getAllFreeHours(edtime, 2);
				} else { // 整天
					dayhours = 8;
				}
			}
			System.out.println("dayhours:" + dayhours);
			allhours = allhours + dayhours;
			dttem = Systemdate.dateDayAdd(dttem, 1);
		}
		return allhours;
	}

	private float getBZCurDate(CJPALineData<Hrkq_workschmonthlist> wmls, Date date) throws Exception {
		String ym = Systemdate.getStrDateByFmt(date, "yyyy-MM");
		int day = Integer.valueOf(Systemdate.getStrDateByFmt(date, "dd"));
		for (CJPABase jpa : wmls) {
			Hrkq_workschmonthlist wml = (Hrkq_workschmonthlist) jpa;
			if (ym.equalsIgnoreCase(wml.yearmonth.getValue())) {
				CField fd = wml.cfield("scid" + day);
				if (fd.isEmpty()) {
					return 0;
				} else {
					String scid = fd.getValue();
					Hrkq_sched sched = getCatchSched(scid);
					CJPALineData<Hrkq_sched_line> sls = sched.hrkq_sched_lines;
					float dayratio = 0;
					for (CJPABase j : sls) {
						Hrkq_sched_line sl = (Hrkq_sched_line) j;
						dayratio = dayratio + sl.dayratio.getAsFloatDefault(0);
					}
					return dayratio;
				}
			}
		}
		return 0;
	}

	//
	private void calc_Btrip(Hrkq_business_trip bt, CDBConnection con) throws Exception {
		Date bgtime = bt.begin_date.getAsDatetime();
		Date edtime = bt.end_date.getAsDatetime();
		Hr_employee emp = new Hr_employee();
		emp.findByID(bt.er_id.getValue(), false);
		if (emp.isEmpty())
			throw new Exception("没有发现ID为【" + bt.er_id.getValue() + "】的员工资料");
		// 获取PB记录
		CJPALineData<Hrkq_workschmonthlist> wmls = getwmlist(emp.er_id.getValue(), bgtime, edtime);
		Date btdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(bgtime));// begin date yyyy-MM-dd
		Date eddate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(edtime));// end date yyyy-MM-dd
		Date dttem = btdate;
		float canfreeh = 0;
		while (dttem.getTime() <= eddate.getTime()) {
			System.out.println("dttem:" + Systemdate.getStrDate(dttem));
			float dayratio = getBZCurDate(wmls, dttem);
			int allfreeHs = 0;
			float dfh = 0;
			if (btdate.getTime() == eddate.getTime()) {// first last is same
				allfreeHs = Math.round((edtime.getTime() - bgtime.getTime()) / (1000 * 60 * 60));
				float bchours = getBCHours(wmls, bgtime, edtime);
				dfh = allfreeHs - bchours; // 总时间减去该期间已经排班的时间
				// System.out.println("出差时间和已排班时间:" + allfreeHs + ":" + bchours);
				dfh = getTXHour(dfh);
			} else {
				if (dttem.getTime() == btdate.getTime()) {// first day
					allfreeHs = getAllFreeHours(bgtime, 1);
					dfh = allfreeHs - getBCHours(wmls, bgtime, Systemdate.dateDayAdd(dttem, 1));// 总时间减去该期间已经排班的时间
				} else if (dttem.getTime() == eddate.getTime()) {// last day
					allfreeHs = getAllFreeHours(edtime, 2);
					dfh = allfreeHs - getBCHours(wmls, dttem, edtime);// 总时间减去该期间已经排班的时间
				} else {
					allfreeHs = 8;
					dfh = allfreeHs - getBCHours(wmls, dttem, Systemdate.dateDayAdd(dttem, 1));// 总时间减去该期间已经排班的时间
				}

			}
			dfh = (dfh < 0) ? 0 : dfh;
			if (dayratio >= 100)// 天占比大于100 为 0
				dfh = 0;
			else if ((dayratio > 0) && (dayratio <= 100)) {// 天占比大于0 小于100
				dfh = (dfh >= min_lb_h) ? 4 : dfh;
			} else {// 天占比为0 可能未排班，最多调休8小时
				dfh = (dfh >= min_lb_h * 2) ? 8 : dfh;
			}
			System.out.println(bt.bta_code.getValue() + " 日期:" + Systemdate.getStrDateyyyy_mm_dd(dttem) + " 出差时长:" + dfh);
			canfreeh = canfreeh + dfh;
			dttem = Systemdate.dateDayAdd(dttem, 1);
		}
		System.out.println("canfreeh：" + canfreeh);
		canfreeh = (canfreeh - canfreeh % 4);
		System.out.println("canfreeh(最小4小时后)：" + canfreeh);
		bt.shouldoffdays.setAsFloat(canfreeh);
		bt.save(con);

		Date valdate = Systemdate.dateMonthAdd(bt.end_date.getAsDatetime(), Integer.valueOf(HrkqUtil.getParmValue("TB_LEAVE_VAILM")));// 加上月份
		valdate = Systemdate.dateDayAdd(valdate, 1);// 加1天
		valdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(valdate));// 去掉时分秒
		// if (canfreeh > 0)
		CtrHrkq_leave_blance.putLeave_blance(con, bt.bta_id.getValue(), bt.bta_code.getValue(), "出差", 4, canfreeh, valdate, bt.er_id.getValue(), "", (float) 0);
	}

	// 获取某人某个BC总时间 小时
	private float getBCHours(CJPALineData<Hrkq_workschmonthlist> wmls, Date timebg, Date timeed) throws Exception {
		String ymd = Systemdate.getStrDateyyyy_mm_dd(timebg);// yyyy-mm-dd

		for (CJPABase jpa : wmls) {
			Hrkq_workschmonthlist wml = (Hrkq_workschmonthlist) jpa;
			int day = Integer.valueOf(Systemdate.getStrDateByFmt(timebg, "dd"));
			if (wml.yearmonth.getValue().equalsIgnoreCase(Systemdate.getStrDateByFmt(timebg, "yyyy-MM"))) {
				String scid = wml.cfield("scid" + day).getValue();
				if ((scid == null) || (scid.isEmpty()))
					return 0;// 当日未排
				else {
					Hrkq_sched sched = getCatchSched(scid);
					CJPALineData<Hrkq_sched_line> ls = sched.hrkq_sched_lines;
					int rwsend = 0;// 排班时间交叉时间
					for (CJPABase j : ls) {
						Hrkq_sched_line l = (Hrkq_sched_line) j;
						Date frtime = gettimebybltime(ymd, l.frtime.getValue());
						Date totime = gettimebybltime(ymd, l.totime.getValue());

						// 方式1 ：按时间与排班时间交叉 计算； 可能由于直落班等导致时差
						System.out.println("计算时间:" + Systemdate.getStrDate(timebg) + "-" + Systemdate.getStrDate(timeed));
						System.out.println("上下时间:" + Systemdate.getStrDate(frtime) + "-" + Systemdate.getStrDate(totime));
						float mlap = Systemdate.getMinuteOverlapDate(timebg, timeed, frtime, totime);
						System.out.println("交叉:" + mlap);
						if (mlap < 0)
							mlap = 0;
						rwsend += mlap;
						// 方式2：有交叉 全算 ，貌似也有问题
						// if (Systemdate.isOverlapDate(timebg, timeed, frtime, totime)) {
						// rwsend += l.realworktime.getAsIntDefault(0);
						// }
						// 方式3：有交叉大于4小时 全算 ，貌似也有问题
						// float mlap = Systemdate.getMinuteOverlapDate(timebg, timeed, frtime, totime);
						// System.out.println("mlap:" + mlap);
						// if ((mlap / 60) > 4)
						// rwsend += l.realworktime.getAsIntDefault(0);

					}
					double h = ((double) rwsend) / 60;
					float rst = (float) (Math.floor(h / 0.5) * 0.5);// 最小单位半小时 小数去掉 不做四舍五入
					return rst;
				}
			}
		}
		return 0;// 当月未排
	}

	// tp 1 开始时间 2 截止时间
	private static int getAllFreeHours(Date time, int tp) {
		float h = Integer.valueOf(Systemdate.getStrDateByFmt(time, "HH")) + Float.valueOf(Systemdate.getStrDateByFmt(time, "mm")) / 60;
		// 11.5f --> 11:30
		System.out.println("h:" + h);
		if (tp == 1) {
			if (h < 12)
				return 8;
			else if ((h >= 11.5f) && (h <= 18))
				return 4;
			else
				return 0;
		} else if (tp == 2) {
			if (h <= 8)
				return 0;// 上午8点前结束 不算
			else if (h <= 12)// = 11.5f
				return 4;// 上午12点前结束算半天
			else
				return 8;// 下午结束算一天
		} else
			return 0;
	}

	// 按出差截止日期 在当月的计算
	public void calc_Btrips(String er_id, Date frTime, Date toTime) throws Exception {
		Date eddate = Systemdate.dateDayAdd(toTime, 1);// 后一天
		String sqlstr = "SELECT * FROM hrkq_business_trip WHERE stat=9 AND er_id=" + er_id + " AND end_date>='"
				+ Systemdate.getStrDate(frTime) + "' AND end_date<='" + Systemdate.getStrDate(eddate) + "' order by begin_date";
		CJPALineData<Hrkq_business_trip> bts = new CJPALineData<Hrkq_business_trip>(Hrkq_business_trip.class);
		bts.findDataBySQL(sqlstr);
		calc_Btrips(bts);
	}

	public void calc_Btrips(CJPALineData<Hrkq_business_trip> bts) throws Exception {
		Hrkq_business_trip btt = new Hrkq_business_trip();
		CDBConnection con = btt.pool.getCon(this);
		con.startTrans();
		try {
			for (CJPABase jpa : bts) {
				Hrkq_business_trip bt = (Hrkq_business_trip) jpa;
				calc_Btrip(bt, con);
			}
			con.submit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	/**
	 * 加班计算 不计算 班制产生的加班记录（已经计算过）
	 * 
	 * @param er_id
	 * @param yearmonth
	 * @throws Exception
	 */
	public void calckq_overtime(String er_id, Date frTime, Date toTime) throws Exception {
		Date eddate = Systemdate.dateDayAdd(toTime, 1);// 后一天
		String sqlstr = "SELECT * FROM hrkq_overtime_list WHERE otltype in(1,2) and er_id=" + er_id + " AND edtime >='" + Systemdate.getStrDate(frTime)
				+ "' AND edtime<='" + Systemdate.getStrDate(eddate) + "' order by bgtime asc";
		CJPALineData<Hrkq_overtime_list> ots = new CJPALineData<Hrkq_overtime_list>(Hrkq_overtime_list.class);
		ots.findDataBySQL(sqlstr);
		calckq_overtime(ots);
	}

	public void calckq_overtime(CJPALineData<Hrkq_overtime_list> ots) throws Exception {
		Hrkq_overtime_list ott = new Hrkq_overtime_list();
		CDBConnection con = ott.pool.getCon(this);
		con.startTrans();
		try {
			for (CJPABase jpa : ots) {
				Hrkq_overtime_list ot = (Hrkq_overtime_list) jpa;
				calrkq_overtime(ot, con);
			}
			con.submit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	private void calrkq_overtime(Hrkq_overtime_list ot, CDBConnection con) throws Exception {
		Hr_employee emp = new Hr_employee();
		emp.findByID(ot.er_id.getValue(), false);
		if (emp.isEmpty())
			throw new Exception("没有发现ID为【" + ot.er_id.getValue() + "】的员工资料");
		Date bgtime = ot.bgtime.getAsDatetime();
		Date edtime = ot.edtime.getAsDatetime();
		long ts = edtime.getTime() - bgtime.getTime();
		if (ts <= 0) {
			Logsw.error("【" + ot.ot_id.getValue() + "】加班截止时间小于开始时间");
			return;
		}

		// if (!Systemdate.getStrDateyyyy_mm_dd(ot.bgtime.getAsDatetime()).equalsIgnoreCase(Systemdate.getStrDateyyyy_mm_dd(ot.edtime.getAsDatetime()))) {
		// throw new Exception("【" + ot.ot_id.getValue() + "】加班时间段跨天");
		// }
		// 允许跨天

		Date ftime = Systemdate.dateDayAdd(bgtime, -1);// 前一天
		Date ttime = Systemdate.dateDayAdd(edtime, 1);// 后一天
		CJPALineData<Hrkq_swcdlst> swls = getswcdlst(emp.employee_code.getValue(), ftime, ttime);//
		Date bgpktime = null;// 加班上班打卡时间
		Date edpktime = null;// 加班下班打卡时间
		Hrkq_swcdlst sw = null;// 打卡记录

		if (ot.needchedksb.getAsIntDefault(0) == 1) {
			// 上班有效打卡时间段
			int cd = Integer.valueOf(HrkqUtil.getParmValueErr("OH_BGSW_LST_LATE_M"));// 加班上班晚打卡X分钟计迟到
			Date bgtime_v1 = Systemdate.dateMinuteAdd(bgtime, Integer.valueOf(HrkqUtil.getParmValueErr("OH_BGSW_PER_M")) * -1);
			Date bgtime_v2 = Systemdate.dateMinuteAdd(bgtime, Integer.valueOf(HrkqUtil.getParmValueErr("OH_BGSW_LST_M")));
			sw = findValidSWCard(bgtime_v1, bgtime_v2, swls, 1);
			if (sw != null) {
				// bgpktime = (sw.skdate.getAsDatetime().getTime() < bgtime.getTime()) ? bgtime : sw.skdate.getAsDatetime();// 加班上班时间取前的
				bgpktime = sw.skdate.getAsDatetime();
				ot.bgpktime.setAsDatetime(bgpktime);
				// System.out.println("上班打卡时间:" + Systemdate.getStrDate(bgpktime) + " 上班允许迟到时间:" + Systemdate.getStrDate(Systemdate.dateMinuteAdd(bgtime,
				// cd)));
				if (bgpktime.getTime() > Systemdate.dateMinuteAdd(bgtime, cd).getTime())// 打卡时间在 允许迟到时间后的 记为迟到
					ot.frst.setAsInt(KQRSTCODE_LATE);
				else
					ot.frst.setAsInt(KQRSTCODE_NORMAL);
			} else
				ot.frst.setAsInt(KQRSTCODE_NOTPK);
		} else {
			ot.frst.setAsInt(KQRSTCODE_NORMAL);
		}

		if (ot.needchedkxb.getAsIntDefault(0) == 1) {
			// 下班有效打卡时间段
			Date edtime_v1 = Systemdate.dateMinuteAdd(edtime, Integer.valueOf(HrkqUtil.getParmValueErr("OH_EDSW_PER_M")) * -1);
			Date edtime_v2 = Systemdate.dateMinuteAdd(edtime, Integer.valueOf(HrkqUtil.getParmValueErr("OH_EDSW_LST_M")));
			int zt = Integer.valueOf(HrkqUtil.getParmValueErr("OH_EDSW_PER_ZT_M"));// 加班下班早打卡X分钟计早退
			sw = findValidSWCard(edtime_v1, edtime_v2, swls, 2);
			if (sw != null) {
				// edpktime = (sw.skdate.getAsDatetime().getTime() > edtime.getTime()) ? edtime : sw.skdate.getAsDatetime();// 加班下班时间取后的
				edpktime = sw.skdate.getAsDatetime();
				ot.edpktime.setAsDatetime(edpktime);
				if (edpktime.getTime() < Systemdate.dateMinuteAdd(edtime, (zt * -1)).getTime())// 打卡时间在 允许早退时间前的 记为 早退
					ot.trst.setAsInt(KQRSTCODE_LEARLY);
				else
					ot.trst.setAsInt(KQRSTCODE_NORMAL);
			} else
				ot.trst.setAsInt(KQRSTCODE_NOTPK);
		} else {
			ot.trst.setAsInt(KQRSTCODE_NORMAL);
		}

		// 上班没打卡 且 免打卡，上班打卡时间 按 上班时间算
		if ((bgpktime == null) && (ot.needchedksb.getAsIntDefault(0) == 2)) {
			bgpktime = bgtime;
		}

		// 下班没打卡 且 免打卡，下班打卡时间 按 下班时间算
		if ((edpktime == null) && (ot.needchedkxb.getAsIntDefault(0) == 2)) {
			edpktime = edtime;
		}

		if ((bgpktime != null) && (edpktime != null)) {
			Date sjbtime = (bgpktime.getTime() > bgtime.getTime()) ? bgpktime : bgtime;
			Date sjetime = (edpktime.getTime() > edtime.getTime()) ? edtime : edpktime;
			float sjhow = getHOw(ot.er_id.getValue(), sjbtime, sjetime);
			// System.out.println("实际时间:" + sjhow);
			float apphow = getHOw(ot.er_id.getValue(), bgtime, edtime);
			// System.out.println("申请时间:" + apphow);
			float othours = (sjhow > apphow) ? apphow : sjhow;
			// System.out.println("实际时间 申请时间 取小的:" + othours);
			ot.othours.setAsFloat(othours);
		} else
			ot.othours.setAsInt(0);

		ot.save(con);
		Hrkq_overtime_hour oh = new Hrkq_overtime_hour();
		oh.findByID(ot.oth_id.getValue());
		if (!oh.isEmpty()) {
			oh.othours.setValue(ot.othours.getValue());
			oh.save(con);
		}
		int obc = Integer.valueOf(HrkqUtil.getParmValueErr("OHLATE_BEFORE_CREATELD"));// 加班有迟到早退是否生成条休息信息 1 生成 2 不生成
		if (ot.dealtype.getAsInt() == 1) {// 生成可调休信息
			if (needtxovertime(obc, ot)) {
				float allothours = ot.othours.getAsFloat() * ot.otrate.getAsFloat();
				float mu = min_lb_h;// Float.valueOf(HrkqUtil.getParmValue("JBTXMINUNIT"));// 加班调休最小单位 4h
				// float fhs = (allothours - allothours % mu);
				System.out.println("allothours:" + allothours + " " + ot.ot_code.getValue());
				float fhs = getTXHour(allothours);
				System.out.println("fhs:" + fhs + "  " + ot.ot_code.getValue());
				if (fhs != 0) {
					Date valdate = Systemdate.dateMonthAdd(ot.edtime.getAsDatetime(), Integer.valueOf(HrkqUtil.getParmValue("OH_LEAVE_VAILM")));// 加上月份
					valdate = Systemdate.dateDayAdd(valdate, 1);// 加1天
					valdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(valdate));// 去掉时分秒
					String r = "加班时长" + ot.othours.getAsFloat() + "倍数" + ot.otrate.getAsFloat() + "最小调休单位";
					CtrHrkq_leave_blance.putLeave_blance(con, ot.otlistid.getValue(), ot.ot_code.getValue(), "加班", 2, fhs, valdate, ot.er_id.getValue(), r, (float) 0);
				}
			}
		}
	}

	private boolean needtxovertime(int obc, Hrkq_overtime_list ot) {
		if ((ot.frst.getAsInt() == KQRSTCODE_NORMAL) && (ot.trst.getAsInt() == KQRSTCODE_NORMAL))// 上下班都正常
			return true;
		if (obc == 1) {// 迟到早退生成 上下班 不为无打卡 上班不为 迟到旷工 下班不为早退旷工
			if ((ot.frst.getAsInt() != KQRSTCODE_NOTPK) && (ot.frst.getAsInt() != KQRSTCODE_NOTPK)
					&& (ot.trst.getAsInt() != KQRSTCODE_NOTPK) && (ot.trst.getAsInt() != KQRSTCODE_BABT)) {
				return true;
			}
		}
		return false;
	}

	private float getHOw(String er_id, Date ftime, Date ttime) throws Exception {
		// 检查期间内是否有排班信息 有排班 需要减去排班时长 加班 值班 一样
		CJPALineData<Hrkq_workschmonthlist> whs = getwmlist(er_id, ftime, ttime);
		long sx = (whs.size() == 0) ? 0 : getbclst(whs, ftime, ttime);
		if (sx < 0)
			sx = 0;
		// System.out.println("11111111111111:" + sx);
		double hw = ttime.getTime() - ftime.getTime() - sx;
		double h = (hw / (1000 * 60 * 60));
		float rst = (float) (Math.floor(h / 0.5) * 0.5);// 最小单位半小时 小数去掉 不做四舍五入
		// h = (float) (Math.round(h * 10) / 10);// 保留1位小数
		// System.out.println("2222222222222:" + rst);
		return rst;
	}

	// 计算需要减去的时间 毫秒
	private long getbclst(CJPALineData<Hrkq_workschmonthlist> whs, Date ftime, Date ttime) throws Exception {
		Date fday = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(ftime));// 开始日期
		Date tday = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(ttime));// 截止日期
		Date temday = fday;
		long ft = ftime.getTime();
		long tt = ttime.getTime();
		long rst = 0;
		while (true) {
			int day = Integer.valueOf(Systemdate.getStrDateByFmt(temday, "dd"));
			Hrkq_workschmonthlist wh = getpbyymm(whs, temday);
			if (wh != null) {
				String scid = wh.cfield("scid" + day).getValue();
				if ((scid != null) && (!scid.isEmpty())) {
					Hrkq_sched sched = getCatchSched(scid);
					for (CJPABase jpa : sched.hrkq_sched_lines) {
						Hrkq_sched_line sl = (Hrkq_sched_line) jpa;
						Date bcftime = gettimebybltime(Systemdate.getStrDateyyyy_mm_dd(temday), sl.frtime.getValue());
						// Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(temday) + " " + sl.frtime.getValue());// 班次上班时间
						Date bcttime = gettimebybltime(Systemdate.getStrDateyyyy_mm_dd(temday), sl.totime.getValue());
						// Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(temday) + " " + sl.totime.getValue());// 班次下班时间
						long bft = bcftime.getTime();
						long btt = bcttime.getTime();
						long sx = 0;
						if ((ft < bft) && (tt > bft) && (tt < bft))
							sx = tt - bft;
						else if ((ft <= bft) && (tt >= btt))
							sx = tt - ft - btt + bft;
						else if ((ft >= bft) && (tt <= btt))
							sx = tt - ft;
						else if ((ft <= btt) && (ft >= bft) && (tt >= btt))
							sx = btt - ft;
						else
							sx = 0;
						if (sx < 0)
							sx = 0;
						rst = rst + sx;
					}
				}
			}
			temday = Systemdate.dateDayAdd(temday, 1);
			if (temday.getTime() >= tday.getTime())
				break;
		}
		return rst;
	}

	private Hrkq_workschmonthlist getpbyymm(CJPALineData<Hrkq_workschmonthlist> whs, Date temday) {
		for (CJPABase jpa : whs) {
			Hrkq_workschmonthlist wh = (Hrkq_workschmonthlist) jpa;
			if (wh.yearmonth.getValue().equalsIgnoreCase(Systemdate.getStrDateByFmt(temday, "yyyy-mm"))) {
				return wh;
			}
		}
		return null;
	}

	// 值班计算 按截至时间
	public void calconduty(String er_id, Date frTime, Date toTime) throws Exception {
		Date eddate = Systemdate.dateDayAdd(toTime, 1);// 后一天
		String sqlstr = "SELECT o.od_id,o.od_code,o.er_id,o.duty_type, o.needchedksb,o.needchedkxb,o.dealtype,"
				+ "l.odlid,l.begin_date,l.end_date"
				+ " FROM hrkq_onduty o,hrkq_ondutyline l "
				+ " WHERE o.od_id=l.od_id and o.er_id=" + er_id
				+ " and o.stat=9 "
				+ " AND l.end_date >='" + Systemdate.getStrDate(frTime)
				+ "' AND l.end_date<='" + Systemdate.getStrDate(eddate) + "' order by begin_date asc";
		CJPALineData<CalcDutyEnty> cds = new CJPALineData<CalcDutyEnty>(CalcDutyEnty.class);
		cds.findDataBySQL(sqlstr);
		calconduty(cds);
	}

	public void calconduty(CJPALineData<CalcDutyEnty> cds) throws Exception {
		Hrkq_onduty ho = new Hrkq_onduty();
		CDBConnection con = ho.pool.getCon(this);
		con.startTrans();
		try {
			for (CJPABase jpa : cds) {
				CalcDutyEnty cd = (CalcDutyEnty) jpa;
				calconduty(cd, con);
			}
			con.submit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	public void calconduty(CalcDutyEnty cd, CDBConnection con) throws Exception {
		Hr_employee emp = new Hr_employee();
		emp.findByID(cd.er_id.getValue(), false);
		if (emp.isEmpty())
			throw new Exception("没有发现ID为【" + cd.er_id.getValue() + "】的员工资料");
		Date bgtime = cd.begin_date.getAsDatetime();
		Date edtime = cd.end_date.getAsDatetime();
		long ts = edtime.getTime() - bgtime.getTime();
		if (ts <= 0)
			throw new Exception("【" + cd.od_code.getValue() + "】值班截止时间小于开始时间");

		Hrkq_ondutyline hol = new Hrkq_ondutyline();
		hol.findByID4Update(con, cd.odlid.getValue(), false);

		Date ftime = Systemdate.dateDayAdd(bgtime, -1);// 前一天
		Date ttime = Systemdate.dateDayAdd(edtime, 1);// 后一天
		CJPALineData<Hrkq_swcdlst> swls = getswcdlst(emp.employee_code.getValue(), ftime, ttime);//
		int acd = Integer.valueOf(HrkqUtil.getParmValueErr("OD_BGSW_PER_LATE_M"));// 值班上班晚打卡X分钟计迟到
		int azt = Integer.valueOf(HrkqUtil.getParmValueErr("OD_EDSW_PER_ZT_M"));// 值班下班早打卡X分钟计早退

		if (cd.needchedksb.getAsIntDefault(0) == 2) {
			hol.frst.setAsInt(KQRSTCODE_NORMAL);
			hol.checkbegin_date.setAsDatetime(bgtime);
		} else {
			Date bgpktime = null;// 加班上班打卡时间
			// 上班有效打卡时间段
			Date bgtime_v1 = Systemdate.dateMinuteAdd(bgtime, Integer.valueOf(HrkqUtil.getParmValueErr("OD_BGSW_PER_M")) * -1);
			Date bgtime_v2 = Systemdate.dateMinuteAdd(bgtime, Integer.valueOf(HrkqUtil.getParmValueErr("OD_BGSW_LST_M")));
			Hrkq_swcdlst sw = findValidSWCard(bgtime_v1, bgtime_v2, swls, 1);
			if (sw != null) {
				// bgpktime = (sw.skdate.getAsDatetime().getTime() < bgtime.getTime()) ? bgtime : sw.skdate.getAsDatetime();// 加班上班时间取前的
				bgpktime = sw.skdate.getAsDatetime();
				hol.checkbegin_date.setAsDatetime(bgpktime);

				// System.out.println("打卡时间:" + Systemdate.getStrDate(bgpktime));
				// System.out.println("允许迟到打卡时间:" + Systemdate.getStrDate(Systemdate.dateMinuteAdd(bgtime, acd)));

				if (bgpktime.getTime() > Systemdate.dateMinuteAdd(bgtime, acd).getTime())
					hol.frst.setAsInt(KQRSTCODE_LATE);
				else
					hol.frst.setAsInt(KQRSTCODE_NORMAL);
			} else
				hol.frst.setAsInt(KQRSTCODE_NOTPK);
		}

		if (cd.needchedkxb.getAsIntDefault(0) == 2) {
			hol.trst.setAsInt(KQRSTCODE_NORMAL);
			hol.checkend_date.setAsDatetime(edtime);
		} else {
			Date edpktime = null;// 加班下班打卡时间

			// 下班有效打卡时间段
			Date edtime_v1 = Systemdate.dateMinuteAdd(edtime, Integer.valueOf(HrkqUtil.getParmValueErr("OD_EDSW_PER_M")) * -1);
			Date edtime_v2 = Systemdate.dateMinuteAdd(edtime, Integer.valueOf(HrkqUtil.getParmValueErr("OD_EDSW_LST_M")));
			Hrkq_swcdlst sw = findValidSWCard(edtime_v1, edtime_v2, swls, 2);
			if (sw != null) {
				// edpktime = (sw.skdate.getAsDatetime().getTime() > edtime.getTime()) ? edtime : sw.skdate.getAsDatetime();// 加班上班时间取后的
				edpktime = sw.skdate.getAsDatetime();
				hol.checkend_date.setAsDatetime(edpktime);

				if (edpktime.getTime() < Systemdate.dateMinuteAdd(edtime, (-1 * azt)).getTime())
					hol.trst.setAsInt(KQRSTCODE_LEARLY);
				else
					hol.trst.setAsInt(KQRSTCODE_NORMAL);
			} else
				hol.trst.setAsInt(KQRSTCODE_NOTPK);
		}

		if ((!hol.checkbegin_date.isEmpty()) && (!hol.checkend_date.isEmpty())) {
			Date cbdate = (hol.checkbegin_date.getAsDatetime().getTime() < bgtime.getTime()) ? bgtime : hol.checkbegin_date.getAsDatetime();
			Date cedata = (hol.checkend_date.getAsDatetime().getTime() > edtime.getTime()) ? edtime : hol.checkend_date.getAsDatetime();
			hol.dttimelong.setAsFloat(getHOw(cd.er_id.getValue(), cbdate, cedata));
		} else
			hol.dttimelong.setAsInt(0);

		hol.save(con);

		int obc = Integer.valueOf(HrkqUtil.getParmValueErr("ODLATE_BEFORE_CREATELD"));// 值班有迟到早退是否生成条休息信息 1 生成 2 不生成
		if (cd.dealtype.getAsInt() == 1) {// 调休
			if (needtx(obc, hol)) {
				int allothours = Math.round(hol.dttimelong.getAsInt() * 1);// 1倍 //如果限制为8小时，以下算法成立：18-05-14
				// float dfh = allothours;
				// if (dfh < min_lb_h)
				// dfh = 0;
				// else if (dfh < min_lb_h * 2) // 原来是8
				// dfh = 4;
				// else
				// dfh = 8;
				float dfh = getTXHour(allothours);

				// float ds = allothours / 8f;
				// ds = (float) (Math.floor(ds / 0.5) * 0.5);// 最小单位半半天 小数去掉 不做四舍五入
				// ds = ds * 8;
				// System.out.println("allothours:" + allothours + ";ds:" + ds);
				if (allothours != 0) {
					Date valdate = Systemdate.dateMonthAdd(cd.end_date.getAsDatetime(), Integer.valueOf(HrkqUtil.getParmValue("DUTY_LEAVE_VAILM")));// 加上月份
					valdate = Systemdate.dateDayAdd(valdate, 1);// 加1天
					valdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(valdate));// 去掉时分秒
					CtrHrkq_leave_blance.putLeave_blance(con, cd.odlid.getValue(), cd.od_code.getValue(), "值班", 3, dfh, valdate, cd.er_id.getValue(), "", (float) 0);
				}
			}

		}

	}

	private boolean needtx(int obc, Hrkq_ondutyline hol) throws Exception {
		// System.out.println("hol json:" + hol.tojson());
		if ((hol.frst.getAsInt() == KQRSTCODE_NORMAL) && (hol.trst.getAsInt() == KQRSTCODE_NORMAL))// 上下班都正常
			return true;
		if (obc == 1) {// 迟到早退生成 上下班 不为无打卡 上班不为 迟到旷工 下班不为早退旷工
			if ((hol.frst.getAsInt() != KQRSTCODE_NOTPK) && (hol.frst.getAsInt() != KQRSTCODE_NOTPK)
					&& (hol.trst.getAsInt() != KQRSTCODE_NOTPK) && (hol.trst.getAsInt() != KQRSTCODE_BABT)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @author shangwen
	 * 考勤计算参数
	 */
	class KqcalcParms {
		CJPALineData<Hrkq_swcdlst> swls; // 打卡记录
		CJPALineData<Hrkq_business_trip> trips;// 出差
		CJPALineData<Hrkq_holidayapp> hds;// 请假
		CJPALineData<Hrkq_wkoff> wks;// 调休
	}

	class KqpbInfo {
		Date kqday;
		String scid;

		public KqpbInfo(Date kqday, String scid) {
			this.kqday = kqday;
			this.scid = scid;
		}
	}

	/**
	 * 根据新规则（3.5） 计算调休时间，只考虑了最多一天的
	 * 
	 * @param txhs
	 * @return
	 */
	private static int getTXHour(float txhs) {
		int fhs = 0;
		if (txhs >= 8)
			fhs = 8;
		else if (txhs >= min_lb_h) {
			fhs = 4;
		} else
			fhs = 0;
		return fhs;
	}

	public static void main(String[] args) throws Exception {
		String kqdate = "2017-06-06";
		Date ftime = Systemdate.getDateByStr("2017-06-05 09:12:11");
		System.out.println(getLTimeByDatetime(kqdate, ftime));
	}
}
