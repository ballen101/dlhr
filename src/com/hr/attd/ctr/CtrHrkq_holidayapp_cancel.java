package com.hr.attd.ctr;

import java.util.Date;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.CJPABase.CJPAStat;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.asset.entity.Hrkq_holidayapp_backpay;
import com.hr.attd.entity.Hrkq_holidayapp;
import com.hr.attd.entity.Hrkq_holidayapp_cancel;
import com.hr.attd.entity.Hrkq_holidayapp_cancel_month;
import com.hr.attd.entity.Hrkq_holidayapp_month;
import com.hr.perm.entity.Hr_employee;

public class CtrHrkq_holidayapp_cancel extends JPAController {

	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		wf.subject.setValue(((Hrkq_holidayapp_cancel) jpa).haccode.getValue());
	}

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished)
			throws Exception {

		if (isFilished) {
			setHAEndTrueTime(jpa, con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc nxtproc, boolean isFilished) throws Exception {
		if (isFilished) {
			setHAEndTrueTime(jpa, con);
		}
	}

	private void setHAEndTrueTime(CJPA jpa, CDBConnection con) throws Exception {
		Hrkq_holidayapp_cancel hac = (Hrkq_holidayapp_cancel) jpa;
		Hrkq_holidayapp ha = new Hrkq_holidayapp();
		ha.findByID4Update(con, hac.haid.getValue(), true);
		if (ha.isEmpty())
			throw new Exception("没有发现ID为【" + hac.haid.getValue() + "】的请假申请单");
		ha.timeedtrue.setValue(hac.canceltime.getValue());
		ha.timebk.setValue(hac.canceltime.getValue()); // 申请到期时间不变呢？
		ha.hdaystrue.setValue(hac.hdaystrue.getValue());
		CJPALineData<Hrkq_holidayapp_month> ls = ha.hrkq_holidayapp_months;
		CJPALineData<Hrkq_holidayapp_cancel_month> lcs = hac.hrkq_holidayapp_cancel_months;
		for (CJPABase j : lcs) {
			Hrkq_holidayapp_cancel_month lc = (Hrkq_holidayapp_cancel_month) j;
			Hrkq_holidayapp_month l = (Hrkq_holidayapp_month) (ls.getJPAByID(lc.hamid.getValue()));
			if (l == null)
				throw new Exception("没有发现ID为【" + lc.hamid.getValue() + "】的请假申请单月度分解");
			l.lhdaystrue.setValue(lc.lhdaystrue.getValue());
		}
		ha.save(con);
		putHolidayapp_BackPay(ha, hac, con);
	}

	/**
	 * 补发工资数据
	 * 
	 * @param ha
	 * @param hac
	 * @param con
	 * @throws Exception
	 */
	private void putHolidayapp_BackPay(Hrkq_holidayapp ha, Hrkq_holidayapp_cancel hac, CDBConnection con) throws Exception {
		int lhmind = Integer.valueOf(HrkqUtil.getParmValue("LONG_HOLDAY_MINDAYS"));// 超过X天，记为长假
		int lhmaxd = Integer.valueOf(HrkqUtil.getParmValue("LONG_HOLDAY_MAXMDAY"));// 未在当月X日前销假停发当月工资
		if (ha.hdaystrue.getAsIntDefault(0) >= lhmind) {
			Date timebg = ha.timebg.getAsDate();
			Date timeed = ha.timeedtrue.getAsDate();
			String lstym = Systemdate.getStrDateByFmt(timeed, "yyyy-MM");// 请假最后一个月
			String fstym = Systemdate.getStrDateByFmt(timebg, "yyyy-MM");// 请假第一个月

			String fsym = null; // 发薪月度，这个月补发前面几个月的
			int endday = Integer.valueOf(Systemdate.getStrDateByFmt(timeed, "dd"));// 请假最后一天
			if (endday > lhmaxd) {// 发薪时间为到期时间的下个月
				fsym = Systemdate.getStrDateByFmt(Systemdate.dateMonthAdd(timeed, 1), "yyyy-MM");
			} else {// 发薪时间为到期时间所在月
				fsym = lstym;
			}

			Date curtime = timebg;
			while (curtime.getTime() < timeed.getTime()) {
				String syyyymm = Systemdate.getStrDateByFmt(Systemdate.dateMonthAdd(curtime, -1), "yyyy-MM");// 工资月份 请假月份的前一个月
				String yyyymm = Systemdate.getStrDateByFmt(curtime, "yyyy-MM");// 循环请假月度
				if (yyyymm.equalsIgnoreCase(lstym)) {// 最后一个月啦
					String dd = Systemdate.getStrDateByFmt(timeed, "dd");
					int day = Integer.valueOf(dd);
					if (day > lhmaxd) {
						// 当月也要生成补发
						createBackPayRow(con, ha, hac, syyyymm, fsym);
					}
					break;
				} else if (yyyymm.equalsIgnoreCase(fstym)) {// 第一个月
					String dd = Systemdate.getStrDateByFmt(timeed, "dd");
					int day = Integer.valueOf(dd);
					if (day <= lhmaxd) {
						createBackPayRow(con, ha, hac, syyyymm, fsym);
					}
				} else {// 不是最后一个月
					createBackPayRow(con, ha, hac, syyyymm, fsym);
				}
				curtime = Systemdate.dateMonthAdd(curtime, 1);
			}
		}
	}

	/**
	 * 创建补发行
	 * 
	 * @param con
	 * @param ha
	 * @param hac
	 * @param habpym
	 * @param hapym // 发薪月度 这个月补发 habpym 这个月度的薪资
	 * @throws Exception
	 */
	private void createBackPayRow(CDBConnection con, Hrkq_holidayapp ha, Hrkq_holidayapp_cancel hac, String habpym, String hapym) throws Exception {
		Hr_employee emp = new Hr_employee();
		emp.findByID(ha.er_id.getValue());
		if (emp.isEmpty())
			throw new Exception("ID为【" + ha.er_id.getValue() + "】的人事资料不存在");
		Hrkq_holidayapp_backpay hb = new Hrkq_holidayapp_backpay();
		hb.habpym.setValue(habpym); // 补发月度yyyy-mm
		hb.hapym.setValue(hapym); // 发薪月度 请假月度
		hb.hacid.setValue(hac.hacid.getValue()); // 销假ID
		hb.haccode.setValue(hac.haccode.getValue()); // 销假编码
		hb.haid.setValue(ha.haid.getValue()); // 申请ID
		hb.hacode.setValue(ha.hacode.getValue()); // 请假编码
		hb.er_id.setValue(emp.er_id.getValue()); // 人事ID
		hb.employee_code.setValue(emp.employee_code.getValue()); // 工号
		hb.employee_name.setValue(emp.employee_name.getValue()); // 姓名
		hb.orgid.setValue(emp.orgid.getValue()); // 部门ID
		hb.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
		hb.orgname.setValue(emp.orgname.getValue()); // 部门
		hb.ospid.setValue(emp.ospid.getValue()); // 职位ID
		hb.ospcode.setValue(emp.ospcode.getValue()); // 职位编码
		hb.sp_name.setValue(emp.sp_name.getValue()); // 职位
		hb.lv_num.setValue(emp.lv_num.getValue()); // 职级
		hb.hdaystrue.setValue(ha.hdaystrue.getValue()); // 实际请假天数
		hb.timebg.setValue(ha.timebg.getValue()); // 请假时间开始 yyyy-MM-dd hh:mm
		hb.timeed.setValue(ha.timeed.getValue()); // 请假时间截止 yyyy-MM-dd hh:mm
		hb.idpath.setValue(emp.idpath.getValue()); // idpath
		hb.save(con);
	}

	@Override
	public void BeforeSave(CJPABase jpa, CDBConnection con, boolean selfLink) throws Exception {
		Hrkq_holidayapp_cancel hac = (Hrkq_holidayapp_cancel) jpa;
		//System.out.println("hac:" + hac.tojson());
		if (selfLink) {
			CJPALineData<Hrkq_holidayapp_cancel_month> ls = hac.hrkq_holidayapp_cancel_months;
			float sd = 0;
			for (CJPABase j : ls) {
				Hrkq_holidayapp_cancel_month l = (Hrkq_holidayapp_cancel_month) j;
				if (l.getJpaStat() != CJPAStat.RSDEL) {// 标记为删除的不处理
					float d = l.lhdaystrue.getAsFloatDefault(0);
					if ((d * 10 % 5) != 0)
						throw new Exception("分解到月度错误，【" + l.yearmonth.getValue() + "】最小单位为0.5天");
					l.lhdaystrue.setAsFloat(d);
					sd += d;
				}
			}
			if (hac.hdaystrue.getAsFloat() != sd) {
				throw new Exception("请假时间为【" + hac.hdaystrue.getAsFloat() + "】天，分解到月度时间合计为【" + sd + "】，分解错误");
			}
		}
	}

}
