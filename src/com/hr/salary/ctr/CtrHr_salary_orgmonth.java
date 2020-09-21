package com.hr.salary.ctr;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.base.entity.Hr_orgposition;
import com.hr.salary.entity.Hr_salary_chglg;
import com.hr.salary.entity.Hr_salary_list;
import com.hr.salary.entity.Hr_salary_orgmonth;
import com.hr.salary.entity.Hr_salary_orgmonth_line;

public class CtrHr_salary_orgmonth extends JPAController {

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf,
			boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			doSetNewMonthSalarys(jpa, con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf,
			Shwwfproc proc, Shwwfproc nxtproc, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			doSetNewMonthSalarys(jpa, con);
		}
	}

	/**
	 * 从薪资变动获取数据生成薪资明细
	 * 
	 * @param jpa
	 * @param con
	 * @throws Exception
	 */
	private void doSetNewMonthSalarys(CJPA jpa, CDBConnection con)
			throws Exception {
		Hr_salary_orgmonth om = (Hr_salary_orgmonth) jpa;
		CJPALineData<Hr_salary_orgmonth_line> omls = om.hr_salary_orgmonth_lines;
		for (CJPABase omljpa : omls) {
			Hr_salary_orgmonth_line oml = (Hr_salary_orgmonth_line) omljpa;
			Hr_salary_chglg scold = CtrSalaryCommon
					.getCur_salary_chglg(oml.er_id.getValue());// 获取现在薪资情况
			if (scold.isEmpty()) {
				throw new Exception("工号为【" + oml.employee_code.getValue()
						+ "】的员工薪资变动记录为空！");
			}
			Hr_orgposition sp=new Hr_orgposition();
			sp.findByID(oml.ospid.getValue());
			if(sp.isEmpty())
				throw new Exception("工号【" + oml.employee_code.getValue() + "】的ID为【"+oml.ospid.getValue()+"】的职位不存在");
			
			Hr_salary_list sl = new Hr_salary_list();
			sl.remark.setValue(om.remark.getValue()); // 备注
			sl.yearmonth.setValue(om.yearmonth.getValue()); // 年月
			
			sl.stru_id.setValue(oml.stru_id.getValue()); // 工资结构id
			sl.stru_name.setValue(oml.stru_name.getValue()); // 工资结构名
			sl.er_id.setValue(oml.er_id.getValue()); // 人事档案id
			sl.employee_code.setValue(oml.employee_code.getValue()); // 申请人工号
			sl.employee_name.setValue(oml.employee_name.getValue()); // 姓名
			sl.orgid.setValue(oml.orgid.getValue()); // 部门
			sl.orgcode.setValue(oml.orgcode.getValue()); // 部门编码
			sl.orgname.setValue(oml.orgname.getValue()); // 部门名称
			sl.ospid.setValue(oml.ospid.getValue()); // 职位id
			sl.ospcode.setValue(oml.ospcode.getValue()); // 职位编码
			sl.sp_name.setValue(oml.sp_name.getValue()); // 职位名称
			sl.lv_id.setValue(oml.lv_id.getValue()); // 职级id
			sl.lv_num.setValue(oml.lv_num.getValue()); // 职级
			sl.hg_id.setValue(sp.hg_id.getValue()); // 职等id
			sl.hg_name.setValue(sp.hg_name.getValue()); // 职等
			sl.hwc_idzl.setValue(sp.hwc_idzl.getValue()); // 职类id
			sl.hwc_namezl.setValue(sp.hwc_namezl.getValue()); // 职类
			sl.hwc_idzq.setValue(sp.hwc_idzq.getValue()); // 职群id
			sl.hwc_namezq.setValue(sp.hwc_namezq.getValue()); // 职群
			sl.hwc_idzz.setValue(sp.hwc_idzz.getValue()); // 职种id
			sl.hwc_namezz.setValue(sp.hwc_namezz.getValue()); // 职种
			sl.hiredday.setValue(oml.hiredday.getValue()); // 入职日期
			sl.poswage.setValue(oml.poswage.getValue()); // 职位工资
			sl.basewage.setValue(oml.basewage.getValue()); // 基本工资
			sl.baseotwage.setValue(oml.baseotwage.getValue()); // 固定加班工资
			sl.skillwage.setValue(oml.skillwage.getValue()); // 技能工资
			sl.perforwage.setValue(oml.perforwage.getValue()); // 绩效工资
			sl.skillsubs.setValue(oml.skillsubs.getValue()); // 技能津贴
			sl.parttimesubs.setValue(oml.parttimesubs.getValue()); // 兼职津贴
			sl.postsubs.setValue(oml.postsubs.getValue()); // 岗位津贴
			sl.wagetype.setValue(om.wagetype.getValue());// 薪资类型
			sl.usable.setAsInt(1);// 有效
			sl.save(con);
		}
	}

}
