package com.hr.attd.ctr;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPAJSON;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.CJPABase.CJPAStat;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.generic.Shworg;
import com.corsair.server.util.CorUtil;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.attd.entity.Hrkq_workschmonth;
import com.hr.attd.entity.Hrkq_workschmonthline;
import com.hr.attd.entity.Hrkq_workschmonthlist;
import com.hr.attd.entity.Hrkq_workschweekdft;
import com.hr.base.entity.Hr_standposition;
import com.hr.perm.entity.Hr_employee;

public class CtrHrkq_workschmonth extends JPAController {

	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		wf.subject.setValue(((Hrkq_workschmonth) jpa).wkcode.getValue());
	}

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			doUpdatePBList(jpa, con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc nxtproc, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			doUpdatePBList(jpa, con);
		}
	}

	private void doUpdatePBList(CJPA jpa, CDBConnection con) throws Exception {
		Hrkq_workschmonth wcm = (Hrkq_workschmonth) jpa;
		String sqlstr = "select * from hrkq_workschmonthline where wkid=" + wcm.wkid.getValue() + " order by ttype,LENGTH(tname)";
		CJPALineData<Hrkq_workschmonthline> cms = new CJPALineData<Hrkq_workschmonthline>(Hrkq_workschmonthline.class);
		cms.findDataBySQL(sqlstr, true, false);
		for (CJPABase cjpa : cms) {
			Hrkq_workschmonthline cm = (Hrkq_workschmonthline) cjpa;
			// 1 班制组 2 部门 3 个人
			if (cm.ttype.getAsInt() == 1) {

			} else if (cm.ttype.getAsInt() == 2) {
				parOrgPB(wcm, cm, con);
			} else if (cm.ttype.getAsInt() == 3) {
				parEmPD(wcm, cm, con);
			}
		}
	}

	private void parOrgPB(Hrkq_workschmonth wcm, Hrkq_workschmonthline cm, CDBConnection con) throws Exception {
		// 借调 兼职 在哪里排班？
		Shworg org = new Shworg();
		org.findByID(cm.tid.getValue(), false);
		if (org.isEmpty()) {
			throw new Exception("ID为【" + cm.tid.getValue() + "】的机构不存在");
		}
		String yearmonth = wcm.yearmonth.getValue();
		String llvdate = yearmonth + "-01";
		String sqlstr = "SELECT * "
				+ " FROM hr_employee e "
				+ " WHERE e.idpath LIKE '" + org.idpath.getValue() + "%' "
				+ " AND ( empstatid<=10 || kqdate_end >='" + llvdate + "')";
		CJPALineData<Hr_employee> ems = new CJPALineData<Hr_employee>(Hr_employee.class);
		ems.findDataBySQL(sqlstr, true, false);
		Hr_standposition sp = new Hr_standposition();
		for (CJPABase cjpa : ems) {
			Hr_employee em = (Hr_employee) cjpa;
			sqlstr = "SELECT s.* FROM hr_standposition s,hr_orgposition o ,hr_employee e "
					+ " WHERE s.sp_id=o.sp_id AND e.ospid=o.ospid AND e.er_id=" + em.er_id.getValue();
			sp.findBySQL(sqlstr, false);
			if (sp.isEmpty())
				continue;
			if (sp.alloworgwsch.getAsIntDefault(0) != 1)
				continue;
			putEmPB(wcm, cm, em, con);
		}
	}

	private void parEmPD(Hrkq_workschmonth wcm, Hrkq_workschmonthline cm, CDBConnection con) throws Exception {
		String sqlstr = "SELECT * FROM hr_employee WHERE er_id=" + cm.tid.getValue();
		// + " AND empstatid IN (SELECT statvalue FROM  hr_employeestat WHERE neetkq =1)";
		Hr_employee em = new Hr_employee();
		em.findBySQL(sqlstr, false);
		if (em.isEmpty())
			throw new Exception("员工【" + cm.tname.getValue() + "】的员工对应的【人事状态】标记为【无需考勤】");
		putEmPB(wcm, cm, em, con);
	}

	private void putEmPB(Hrkq_workschmonth wcm, Hrkq_workschmonthline cm, Hr_employee em, CDBConnection con) throws Exception {
		String sqlstr = "select * from hrkq_workschmonthlist where yearmonth='" + wcm.yearmonth.getValue() + "' and er_id=" + em.er_id.getValue();
		Hrkq_workschmonthlist ml = new Hrkq_workschmonthlist();
		ml.findBySQL(con, sqlstr, false);
		ml.assignfield(cm, true);
		ml.yearmonth.setValue(wcm.yearmonth.getValue());
		ml.er_id.setValue(em.er_id.getValue());
		ml.employee_code.setValue(em.employee_code.getValue());
		ml.employee_name.setValue(em.employee_name.getValue());
		ml.card_number.setValue(em.card_number.getValue());
		ml.hiredday.setValue(em.hiredday.getValue()); // 入职日期
		ml.orgid.setValue(em.orgid.getValue()); // 部门ID
		ml.orgcode.setValue(em.orgcode.getValue()); // 部门编码
		ml.orgname.setValue(em.orgname.getValue()); // 部门名称
		ml.lv_num.setValue(em.lv_num.getValue()); // 职级
		ml.save(con);
	}

	/**
	 * 用于调入、入职等人事机构异动生成从开始日期（包含）到开始日期所在月，月末机构默认排班；
	 * 条件：如果开始日期不在当月（系统当前年月）、不生成排班；
	 * 机构默认周排班方式，从人事当前机构上溯找到有配置“机构默认周排班”的第一个机构，按周进行自动排班；
	 * 自动排班严格按周排班，不考虑节假日及特殊放假等情况；
	 * 
	 * @param con
	 * @param em
	 * @param bgdate
	 * @throws Exception
	 */
	public static void putEmDdtWeekPB(CDBConnection con, Hr_employee em, Date bgdate) throws Exception {
		String bgym = Systemdate.getStrDateByFmt(bgdate, "yyyy-MM");
		String sysym = Systemdate.getStrDateByFmt(new Date(), "yyyy-MM");
		if (!bgym.equalsIgnoreCase(sysym))// 如果开始日期不在当月（系统当前年月）,不生成排班；
			return;
		String idp = em.idpath.getValue();
		if ((idp == null) || (idp.isEmpty()))
			throw new Exception("机构【" + em.employee_name.getValue() + "】IDPATH为空");
		idp = idp.substring(0, idp.length() - 1);
		String sqlstr = "SELECT * FROM hrkq_workschweekdft WHERE useable=1 AND orgid IN(" + idp + ") AND LENGTH(idpath)="
				+ "(SELECT MAX(LENGTH(idpath)) FROM hrkq_workschweekdft "
				+ "WHERE useable=1 AND orgid IN(" + idp + "))";
		Hrkq_workschweekdft ww = new Hrkq_workschweekdft();
		ww.findBySQL(sqlstr, false);
		if (ww.isEmpty())// 没有默认周排班，不生成排班；
			return;

		Calendar cal = Calendar.getInstance();
		cal.setTime(new SimpleDateFormat("yyyy-MM").parse(bgym));
		int md = cal.getActualMaximum(Calendar.DAY_OF_MONTH);// 月度最大天数
		cal.setTime(bgdate);
		int day = cal.get(Calendar.DAY_OF_MONTH); // 获取当前天数

		sqlstr = "select * from hrkq_workschmonthlist where yearmonth='" + sysym + "' and er_id=" + em.er_id.getValue();
		Hrkq_workschmonthlist ml = new Hrkq_workschmonthlist();
		ml.findBySQL(con, sqlstr, false);

		for (int i = day; i <= md; i++) {// 按周设置
			Date cd = new SimpleDateFormat("yyyy-MM-dd").parse(bgym + "-" + rightSubstr("0" + i, 2));
			// 如果需要控制法定假，可以在这里判断
			int wd = getdayofweek(cd);
			ml.cfield("scid" + i).setValue(ww.cfield("scid" + wd).getValue());
			ml.cfield("scdname" + i).setValue(ww.cfield("scdname" + wd).getValue());
			ml.cfield("backcolor" + i).setValue(ww.cfield("backcolor" + wd).getValue());
		}

		ml.yearmonth.setValue(sysym);
		ml.er_id.setValue(em.er_id.getValue());
		ml.employee_code.setValue(em.employee_code.getValue());
		ml.employee_name.setValue(em.employee_name.getValue());
		ml.card_number.setValue(em.card_number.getValue());
		ml.hiredday.setValue(em.hiredday.getValue()); // 入职日期
		ml.orgid.setValue(em.orgid.getValue()); // 部门ID
		ml.orgcode.setValue(em.orgcode.getValue()); // 部门编码
		ml.orgname.setValue(em.orgname.getValue()); // 部门名称
		ml.lv_num.setValue(em.lv_num.getValue()); // 职级
		ml.save(con);
	}

	private static int getdayofweek(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		boolean isFirstSunday = (cal.getFirstDayOfWeek() == Calendar.SUNDAY);
		// 获取周几
		int weekDay = cal.get(Calendar.DAY_OF_WEEK);
		// 若一周第一天为星期天，则-1
		if (isFirstSunday) {
			weekDay = weekDay - 1;
			if (weekDay == 0) {
				weekDay = 7;
			}
		}
		return weekDay;
	}

	private static String rightSubstr(String s, int l) {
		return s.substring(s.length() - l, s.length());
	}

	// 不为空的返回值，将作为查询结果返回给前台
	@Override
	public String OnCCoCopy(Class<CJPA> jpaclass, HashMap<String, String> pparms) throws Exception {
		Hrkq_workschmonth wh = new Hrkq_workschmonth();
		String copy_ym = CorUtil.hashMap2Str(pparms, "copy_ym", "需要参数copy_ym");
		String id = pparms.get("id");
		if ((id == null) || id.isEmpty())
			throw new Exception("需要id参数");

		String[] cfs = null;
		String clearfields = pparms.get("clearfields");
		if ((clearfields != null) && !clearfields.isEmpty()) {
			cfs = clearfields.trim().split(",");
		}

		wh.findByID(id.trim());

		wh.clearAllId();
		if (wh.cfieldbycfieldname("stat") != null) {
			wh.cfieldbycfieldname("stat").setAsInt(1);
		}
		if (cfs != null)
			for (String fd : cfs) {
				CField cfd = wh.cfieldbycfieldname(fd);
				if (cfd != null) {
					cfd.setValue(null);
				} else {
					new Exception("【" + jpaclass + "】没发现字段【" + fd + "】");
				}
			}

		Date fdate = Systemdate.getDateByStr(copy_ym + "-01");
		Calendar time = Calendar.getInstance();
		time.setTime(fdate);
		int days = time.getActualMaximum(Calendar.DAY_OF_MONTH);// 本月份的天数
		CJPALineData<Hrkq_workschmonthline> ls = wh.hrkq_workschmonthlines;
		for (CJPABase j : ls) {
			Hrkq_workschmonthline l = (Hrkq_workschmonthline) j;
			BZInfo[] bizs = getMothFirstWeekPB(wh.yearmonth.getValue(), l);// 第一个元素是周一的
			for (int i = 1; i <= 31; i++) {
				if (i <= days) {
					Date tdate = Systemdate.dateDayAdd(fdate, i - 1);
					time.setTime(tdate);
					int d = time.get(Calendar.DAY_OF_WEEK) - 2;
					if (d == -1)
						d = 6;
					BZInfo bzi = bizs[d];
					l.cfield("scid" + i).setValue(bzi.scid);
					l.cfield("scdname" + i).setValue(bzi.scdname);
					l.cfield("backcolor" + i).setValue(bzi.backcolor);
				} else {
					l.cfield("scid" + i).setValue(null);
					l.cfield("scdname" + i).setValue(null);
					l.cfield("backcolor" + i).setValue(null);
				}
			}
		}
		wh.yearmonth.setValue(copy_ym);
		// throw new Exception("开发中，等等等...........");
		wh.setJpaStat(CJPAStat.RSINSERT);
		return ((CJPAJSON) wh.save()).tojson();
	}

	private class BZInfo {
		String scid = null;
		String scdname = null;
		String backcolor = null;
	}

	/**
	 * @param yearmonth
	 * @param whl
	 * @return 第一个元素是周一的
	 */
	private BZInfo[] getMothFirstWeekPB(String yearmonth, Hrkq_workschmonthline whl) {
		//System.out.println("getMothFirstWeekPB yearmonth:" + yearmonth);
		Date bdate = Systemdate.getMonthFirstWeekDay(yearmonth, Calendar.MONDAY);
		int fday = Integer.valueOf(Systemdate.getStrDateByFmt(bdate, "dd"));
		// System.out.println("bdate:" + Systemdate.getStrDateyyyy_mm_dd(bdate));
		// System.out.println("fday:" + fday);
		BZInfo[] rst = new BZInfo[7];
		for (int i = 0; i < 7; i++) {
			rst[i] = new BZInfo();
			// System.out.println("scid fd:" + whl.cfield("scid" + fday) + " scid" + fday);
			rst[i].scid = whl.cfield("scid" + fday).getValue();
			rst[i].scdname = whl.cfield("scdname" + fday).getValue();
			rst[i].backcolor = whl.cfield("backcolor" + fday).getValue();
			fday++;
		}
		return rst;
	}
}
