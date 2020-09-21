package com.hr.util;

import java.util.Calendar;
import java.util.Date;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Systemdate;
import com.hr.base.entity.Hr_orgposition;
import com.hr.perm.entity.Hr_employee;
import com.hr.salary.entity.Hr_salary_chglg;
import com.hr.salary.entity.Hr_salary_list;
import com.hr.util.hrmail.DLHRMailCenterWS;
import com.hr.util.hrmail.Hr_emailsend_log;

public class AUTOSETSalaryListThread extends Thread {
	public boolean stoped = false;
	private int timespec = 86400000;

	public void run() {
		while (true) {
			if (stoped)
				break;
			try {
				Calendar cal = Calendar.getInstance();
				int countday=cal.get(Calendar.DAY_OF_MONTH);
				if(countday==1){
					dosetsalarylist();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(timespec);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}

	private void dosetsalarylist() throws Exception {
		try {
			Date nowdate=Systemdate.getDateByyyyy_mm_dd(Systemdate.getStrDate());
			Date eddate=Systemdate.getFirstAndLastOfMonth(nowdate).date1;
			Date bgdate = Systemdate.dateMonthAdd(eddate, -1);// 减一月
			Date bfdate = Systemdate.dateMonthAdd(bgdate, -1);// 减一月
			String ymbg=Systemdate.getStrDateByFmt(bgdate, "yyyy-MM");
			String ymed=Systemdate.getStrDateByFmt(eddate, "yyyy-MM");
			String ymbf=Systemdate.getStrDateByFmt(bfdate, "yyyy-MM");
			
			String sqlstr="SELECT cl.* FROM hr_salary_chglg cl,hr_employee e WHERE cl.useable=1 AND cl.newposition_salary>0 "+
			"AND cl.er_id=e.er_id AND e.pay_way='月薪' AND e.empstatid<10 "+
			" AND cl.er_id NOT IN (SELECT * FROM (SELECT er_id FROM hr_salary_list WHERE wagetype=1 AND yearmonth>='"+
					ymbg+"' AND yearmonth<'"+ymed+"'  )tt) ";
			CJPALineData<Hr_salary_list> sllists = new CJPALineData<Hr_salary_list>(Hr_salary_list.class);
			CJPALineData<Hr_salary_chglg> chglgs = new CJPALineData<Hr_salary_chglg>(Hr_salary_chglg.class);
			chglgs.findDataBySQL(sqlstr, true, false);
			int rst=0;
			for (CJPABase jpa : chglgs) {
				int m=rst%2000;
				if(m==0){
					if (sllists.size() > 0) {
						System.out.println("====================自动生成工资明细条数【" + sllists.size() + "】");
						sllists.save();// 高速存储
					}
					sllists.clear();
				}
				Hr_salary_chglg cl=(Hr_salary_chglg)jpa;
				Hr_employee emp = new Hr_employee();
				emp.findByID(cl.er_id.getValue());
				if (emp.isEmpty())
					throw new Exception("id为【" + cl.er_id.getValue() + "】不存在人事资料");
				
				Hr_orgposition sp=new Hr_orgposition();
				sp.findByID(emp.ospid.getValue());
				if(sp.isEmpty())
					throw new Exception("员工id为【" + cl.er_id.getValue() + "】的ID为【"+emp.ospid.getValue()+"】的职位不存在");
				
				Hr_salary_list sall = new Hr_salary_list();
				sall.remark.setValue("月初异动自动生成薪资明细"); // 备注
				sall.yearmonth.setValue(ymbg); // 年月
				sall.er_id.setValue(emp.er_id.getValue()); // 人事档案id
				sall.employee_code.setValue(emp.employee_code.getValue()); // 申请人工号
				sall.employee_name.setValue(emp.employee_name.getValue()); // 姓名
				sall.orgid.setValue(emp.orgid.getValue()); // 部门
				sall.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
				sall.orgname.setValue(emp.orgname.getValue()); // 部门名称
				sall.idpath.setValue(emp.idpath.getValue()); // idpath
				sall.ospid.setValue(emp.ospid.getValue()); // 职位id
				sall.ospcode.setValue(emp.ospcode.getValue()); // 职位编码
				sall.sp_name.setValue(emp.sp_name.getValue()); // 职位名称
				sall.lv_id.setValue(emp.lv_id.getValue()); // 职级id
				sall.lv_num.setValue(emp.lv_num.getValue()); // 职级
				sall.hg_id.setValue(emp.hg_id.getValue()); // 职等id
				sall.hg_name.setValue(emp.hg_name.getValue()); // 职等
				sall.hwc_idzl.setValue(sp.hwc_idzl.getValue()); // 职类id
				sall.hwc_namezl.setValue(sp.hwc_namezl.getValue()); // 职类
				sall.hwc_idzq.setValue(sp.hwc_idzq.getValue()); // 职群id
				sall.hwc_namezq.setValue(sp.hwc_namezq.getValue()); // 职群
				sall.hwc_idzz.setValue(sp.hwc_idzz.getValue()); // 职种id
				sall.hwc_namezz.setValue(sp.hwc_namezz.getValue()); // 职种
				sall.hiredday.setValue(emp.hiredday.getValue()); // 入职日期
				sall.stru_id.setValue(cl.newstru_id.getValue()); // 工资结构id
				sall.stru_name.setValue(cl.newstru_name.getValue()); // 工资结构
				sall.poswage.setValue(cl.newposition_salary.getValue()); // 职位工资
				sall.basewage.setValue(cl.newbase_salary.getValue()); // 基本工资
				sall.baseotwage.setValue(cl.newotwage.getValue()); // 固定加班工资
				sall.skillwage.setValue(cl.newtech_salary.getValue()); // 技能工资
				sall.perforwage.setValue(cl.newachi_salary.getValue()); // 绩效工资
				sall.skillsubs.setValue(cl.newtech_allowance.getValue()); // 技能津贴
				sall.parttimesubs.setValue(cl.newparttimesubs.getValue()); // 兼职津贴
				sall.postsubs.setValue(cl.newpostsubs.getValue()); // 岗位津贴
				sall.wagetype.setAsInt(1);//薪资类型
				sall.usable.setAsInt(1);//有效
				sall.creator.setValue("DEV");//创建人
				sall.createtime.setValue(Systemdate.getStrDate());//创建时间
				sllists.add(sall);
				rst++;
			}
			if (sllists.size() > 0) {
				System.out.println("====================生成薪资明细记录条数【" + sllists.size() + "】");
				sllists.saveBatchSimple();// 高速存储sllists
			}
			sllists.clear();
			String sqlstr1="SELECT * FROM (SELECT sl.* FROM hr_salary_list sl,hr_employee e WHERE  sl.wagetype=1 AND sl.yearmonth>='"+ymbf+"' AND sl.yearmonth<'"+ymbg+
				"' AND sl.er_id=e.er_id AND e.empstatid<10 AND e.pay_way='月薪') sl WHERE er_id NOT IN (SELECT DISTINCT er_id FROM  hr_salary_list WHERE wagetype=1 AND  yearmonth>='"+ymbg+
				"' AND yearmonth<'"+ymed+"')  ";
			sllists.findDataBySQL(sqlstr1, true, false);
			for(CJPABase jpa : sllists){
				Hr_salary_list sl =(Hr_salary_list)jpa;
				sl.clearAllId();
				sl.yearmonth.setValue(ymbg);
				sl.createtime.setValue(Systemdate.getStrDate());
				sl.updatetime.setValue(Systemdate.getStrDate());
				sl.save();
			}
			System.out.println("自动生成【" + ymbg + "】月份的薪资明细【" + Systemdate.getStrDate() + "】");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
