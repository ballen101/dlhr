package com.hr.attd.ctr;

import java.util.Date;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.generic.Shwuser;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.attd.entity.Hrkq_leave_blance;
import com.hr.attd.entity.Hrkq_special_holday;
import com.hr.attd.entity.Hrkq_wkoff;
import com.hr.attd.entity.Hrkq_wkoffsourse;
import com.hr.util.HRJPAEventListener;
import com.hr.util.HRUtil;

public class CtrHrkq_wkoff extends JPAController {

	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		wf.subject.setValue(((Hrkq_wkoff) jpa).wocode.getValue());
	}

	@Override
	public void BeforeSave(CJPABase jpa, CDBConnection con, boolean selfLink) throws Exception {
		Hrkq_wkoff wo = (Hrkq_wkoff) jpa;
		float wodays = wo.wodays.getAsFloatDefault(0);
		if (wodays < 0.5) {
			throw new Exception("调休最少为0.5天");
		}
		checkallsource(wo);
	}

	/**
	 * 检查月度最大调休
	 * 没做月度分解，按调休截止时间所在月度计算
	 * 
	 * @param wo
	 * @throws Exception
	 * @throws
	 */
	private void checkMonthLM(CJPA jpa, CDBConnection con) throws Exception {
		// 检查登录用户角色
		if (HRUtil.hasRoles("58"))// 单据维护员 和 管理员
			return;

		Hrkq_wkoff wo = (Hrkq_wkoff) jpa;
		float mlm = (HrkqUtil.getParmValueErr("MONTH_LEAVE_MAX") == null) ? 0 : Float.valueOf(HrkqUtil.getParmValueErr("MONTH_LEAVE_MAX"));
		if (mlm == 0)
			return;
		String ym = Systemdate.getStrDateByFmt(wo.end_date.getAsDatetime(), "yyyy-MM");
		String ym2 = Systemdate.getStrDateByFmt(Systemdate.dateMonthAdd(wo.end_date.getAsDatetime(), 1), "yyyy-MM");
		String sqlstr = "SELECT IFNULL(sum(wodays),0) wodays  FROM hrkq_wkoff  WHERE er_id=" + wo.er_id.getValue()
				+ " AND end_date>='" + ym + "-01' AND end_date<'" + ym2 + "-01' AND stat>1 AND stat<=9 ";
		if (!wo.woid.isEmpty())
			sqlstr = sqlstr + " and woid<>" + wo.woid.getValue();
		float wodays = Float.valueOf(con.openSql2List(sqlstr).get(0).get("wodays"));
		wodays = wodays + wo.wodays.getAsFloatDefault(0);
		if (wodays > mlm) {
			throw new Exception("【" + ym + "】月的调休累积为【" + wodays + "】，超过月度最大天数限制【" + mlm + "】");
		}
	}

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished)
			throws Exception {
		checkJXRecord(jpa);
		checkMonthLM(jpa, con);// 月度4天限制 提交时控制
		// 提交就扣减余额 不管其它的
		doDecWorkLeafBlance(jpa, con);
		if (isFilished)
			doWFAgent(jpa, con);
	}

	@Override
	public void OnWfBreak(CJPA jpa, Shwwf wf, CDBConnection con) throws Exception {
		// 中断 红冲
		doADDWorkLeafBlance(jpa, con);
	}

	@Override
	public String OnCCoVoid(CDBConnection con, CJPA jpa) throws Exception {
		// 作废 红冲
		doADDWorkLeafBlance(jpa, con);
		return null;
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc nxtproc, boolean isFilished) throws Exception {
		if (isFilished)
			doWFAgent(jpa, con);
	}

	private void doWFAgent(CJPA jpa, CDBConnection con) {
		Hrkq_wkoff wo = (Hrkq_wkoff) jpa;
		if (wo.iswfagent.getAsIntDefault(0) == 1) {
			HRUtil.setWFAgend(con, wo.employee_code.getValue(), wo.begin_date.getAsDatetime(), wo.end_date.getAsDatetime());
		}
	}

	private void checkallsource(Hrkq_wkoff wo) throws Exception {
		Date begin_date = wo.begin_date.getAsDatetime();
		Date end_date = wo.end_date.getAsDatetime();
		CJPALineData<Hrkq_wkoffsourse> ss = wo.hrkq_wkoffsourses;
		Date valdate = null;
		for (CJPABase jpa : ss) {
			Hrkq_wkoffsourse s = (Hrkq_wkoffsourse) jpa;
			if (s.wotime.getAsFloatDefault(0) > 0) {// 获取一个最小失效期
				if ((valdate == null) || (valdate.getTime() > s.valdate.getAsDatetime().getTime())) {
					valdate = s.valdate.getAsDatetime();
				}
			}
			if ((!s.edtime.isEmpty()) && (s.wotime.getAsIntDefault(0) > 0)) {
				Date edtime = s.edtime.getAsDatetime();
				if (edtime != null) {
					if (begin_date.getTime() < edtime.getTime()) {
						throw new Exception("所有调休源【截止时间】必须小于调休【开始时间】");
					}
				}
			}
		}
		if (valdate == null)
			throw new Exception("未发现调休源失效时间");
		if (end_date.getTime() > valdate.getTime()) {
			throw new Exception("调休截止时间不允许大于任一调休失效时间");
		}
	}

	private void checkJXRecord(CJPA jpa) throws Exception {
		Hrkq_wkoff wo = (Hrkq_wkoff) jpa;
		Date bgtime = wo.begin_date.getAsDatetime();
		Date edtime = wo.end_date.getAsDatetime();
		if(!wo.creator.getValue().equals("inteface")){
			HrkqUtil.checkValidDate(bgtime);
		}
		//HrkqUtil.checkValidDate(bgtime);
		HrkqUtil.checkAllOverlapDatetime(1, wo.woid.getValue(), wo.er_id.getValue(), wo.employee_name.getValue(), bgtime, edtime);
		// String bgtstr1 = Systemdate.getStrDate(bgtime1);
		// String edtstr1 = Systemdate.getStrDate(edtime1);
		// String sqlstr = "SELECT * FROM hrkq_wkoff WHERE er_id=" + wo.er_id.getValue() + " AND hrkq_wkoff.woid<>" + wo.woid.getValue()
		// + " AND stat>1 AND stat<10 "
		// + " AND (((begin_date<='" + bgtstr1 + "') AND (end_date>='" + bgtstr1 + "'))||((begin_date>='" + bgtstr1 + "') and (begin_date<='" + edtstr1
		// + "')))";
		// Hrkq_wkoff temwo = new Hrkq_wkoff();
		// temwo.findBySQL(sqlstr);
		// if (!temwo.isEmpty())
		// throw new Exception("员工【" + wo.employee_name.getValue() + "】申请的调休时间与已经提交的调休申请【" + temwo.wocode.getValue() + "】时间交叉错误!");
	}

	/**
	 * 扣减
	 * 
	 * @param jpa
	 * @param con
	 * @throws Exception
	 */
	private void doDecWorkLeafBlance(CJPA jpa, CDBConnection con) throws Exception {
		Hrkq_wkoff wo = (Hrkq_wkoff) jpa;
		CJPALineData<Hrkq_wkoffsourse> wss = wo.hrkq_wkoffsourses;
		Hrkq_leave_blance lb = new Hrkq_leave_blance();
		for (CJPABase jpa1 : wss) {
			Hrkq_wkoffsourse ws = (Hrkq_wkoffsourse) jpa1;
			lb.clear();
			lb.findByID4Update(con, ws.lbid.getValue(), false);
			// System.out.println("lb.alllbtime:" + lb.alllbtime.getValue());
			int alllbtime = lb.alllbtime.getAsIntDefault(0);
			int usedlbtime = lb.usedlbtime.getAsIntDefault(0);
			usedlbtime = usedlbtime + ws.wotime.getAsIntDefault(0);
			// System.out.println("usedlbtime:" + usedlbtime + " ;alllbtime:" + alllbtime);
			if (usedlbtime > alllbtime) {
				throw new Exception("【" + wo.employee_name.getValue() + "】【" + lb.lbname.getValue() + "】累计调休时间超出【可调休时间】");
			}
			lb.usedlbtime.setAsInt(usedlbtime);
			lb.save(con);
		}
	}

	/**
	 * 红冲
	 * 
	 * @param jpa
	 * @param con
	 * @throws Exception
	 */
	private void doADDWorkLeafBlance(CJPA jpa, CDBConnection con) throws Exception {
		Hrkq_wkoff wo = (Hrkq_wkoff) jpa;
		CJPALineData<Hrkq_wkoffsourse> wss = wo.hrkq_wkoffsourses;
		Hrkq_leave_blance lb = new Hrkq_leave_blance();
		for (CJPABase jpa1 : wss) {
			Hrkq_wkoffsourse ws = (Hrkq_wkoffsourse) jpa1;
			if (ws.wotime.getAsIntDefault(0) > 0) {
				lb.clear();
				lb.findByID4Update(con, ws.lbid.getValue(), false);
				if (lb.isEmpty())
					throw new Exception("调休源【" + ws.sccode.getValue() + "】不存在，无法红冲");
				// System.out.println("lb.alllbtime:" + lb.alllbtime.getValue());
				// int alllbtime = lb.alllbtime.getAsIntDefault(0);
				int usedlbtime = lb.usedlbtime.getAsIntDefault(0);
				usedlbtime = usedlbtime - ws.wotime.getAsIntDefault(0);
				// System.out.println("usedlbtime:" + usedlbtime + " ;alllbtime:" + alllbtime);
				if (usedlbtime < 0) {
					throw new Exception("【" + wo.employee_name.getValue() + "】【" + lb.lbname.getValue() + "】累计调休时间红冲后小于0，逻辑错误！");
				}
				lb.usedlbtime.setAsInt(usedlbtime);
				lb.save(con);
			}
		}
	}

}
