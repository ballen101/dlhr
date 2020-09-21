package com.hr.salary.ctr;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.salary.entity.Hr_salary_chglg;
import com.hr.salary.entity.Hr_salary_postsub_cancel;
import com.hr.salary.entity.Hr_salary_postsub_line;

public class CtrHr_salary_postsub_cancel extends JPAController{

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf,
			boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if(isFilished){
			doCancelPostSub(jpa,con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf,
			Shwwfproc proc, Shwwfproc nxtproc, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if(isFilished){
			doCancelPostSub(jpa,con);
		}
	}
	
	private void doCancelPostSub(CJPA jpa, CDBConnection con) throws Exception{
		Hr_salary_postsub_cancel psc=(Hr_salary_postsub_cancel)jpa;
		float subsidyarm = psc.npostsub.getAsFloatDefault(0);
		if (subsidyarm == 0)
			return;
		subsidyarm = subsidyarm * -1;
		Hr_salary_chglg scold = CtrSalaryCommon.getCur_salary_chglg(psc.er_id.getValue());// 获取现在薪资情况

		Hr_salary_chglg sc = new Hr_salary_chglg();
		sc.er_id.setValue(psc.er_id.getValue());
		sc.scatype.setValue(8);// 1 入职定薪、2调动核薪、3 转正调薪、4 年度调薪、5 特殊调薪 6调动转正 7 兼职
		sc.stype.setValue(11); // 来源类别 1 历史数据、2 实习登记、3 入职表单、4 调动表单、5 入职转正、6 调动转正、7个人特殊调薪 8兼职、9批量特殊调薪、10技术津贴、11岗位津贴....
		sc.sid.setValue(psc.psc_id.getValue()); // 来源ID
		sc.scode.setValue(psc.psc_code.getValue()); // 来源单号

		sc.oldstru_id.setValue(scold.newstru_id.getValue()); // 调薪前工资结构ID
		sc.oldstru_name.setValue(scold.newstru_name.getValue()); // 调薪前工资结构名
		sc.oldchecklev.setValue(scold.newchecklev.getValue());// 调薪前绩效考核层级
		sc.oldattendtype.setValue(scold.newattendtype.getValue()); // 调薪前出勤类别
		sc.oldcalsalarytype.setValue(scold.newcalsalarytype.getValue()); // 调薪前计薪方式
		sc.oldposition_salary.setValue(scold.newposition_salary.getValue()); // 调薪前职位工资
		sc.oldbase_salary.setValue(scold.newbase_salary.getValue()); // 调薪前基本工资
		sc.oldtech_salary.setValue(scold.newtech_salary.getValue()); // 调薪前技能工资
		sc.oldachi_salary.setValue(scold.newachi_salary.getValue()); // 调薪前绩效工资
		sc.oldotwage.setValue(scold.newotwage.getValue()); // 调薪前固定加班工资
		sc.oldtech_allowance.setValue(scold.newtech_allowance.getValue()); // 调薪前技术津贴
		sc.oldparttimesubs.setValue(scold.newparttimesubs.getValue()); // 调薪前兼职津贴
		sc.oldpostsubs.setValue(scold.newpostsubs.getValue()); // 调薪前岗位津贴
		sc.oldavg_salary.setValue(0); // 调薪前平均工资

		sc.newstru_id.setValue(scold.newstru_id.getValue()); // 调薪后工资结构ID
		sc.newstru_name.setValue(scold.newstru_name.getValue()); // 调薪后工资结构名
		sc.newchecklev.setValue(scold.newchecklev.getValue());// 调薪后绩效考核层级
		sc.newattendtype.setValue(scold.newattendtype.getValue()); // 调薪后出勤类别
		sc.newcalsalarytype.setValue(scold.newcalsalarytype.getValue()); // 调薪后计薪方式
		sc.newposition_salary.setValue(scold.newposition_salary.getAsFloatDefault(0)); // 调薪后职位工资
		sc.newbase_salary.setValue(scold.newbase_salary.getAsFloatDefault(0)); // 调薪后基本工资
		sc.newtech_salary.setValue(scold.newtech_salary.getAsFloatDefault(0)); // 调薪后技能工资
		sc.newachi_salary.setValue(scold.newachi_salary.getAsFloatDefault(0)); // 调薪后绩效工资
		sc.newotwage.setValue(scold.newotwage.getAsFloatDefault(0)); // 调薪后固定加班工资
		sc.newtech_allowance.setValue(scold.newtech_allowance.getAsFloatDefault(0)); // 调薪后技术津贴
		sc.newparttimesubs.setValue(scold.newparttimesubs.getAsFloatDefault(0)); // 调薪后兼职津贴

		float newpostsubs = scold.newpostsubs.getAsFloatDefault(0) + subsidyarm;
		if (newpostsubs < 0)
			newpostsubs = 0;
		sc.newpostsubs.setValue(newpostsubs); // 调薪后兼职津贴
		sc.sacrage.setValue(subsidyarm); // 调薪幅度
		sc.chgdate.setValue(psc.canceldate.getValue()); // 调薪日期
		sc.chgreason.setValue("岗位津贴终止调薪"); // 调薪原因
		sc.save(con);
		String sqlstr="SELECT psl.* FROM `hr_salary_postsub` ps,`hr_salary_postsub_line` psl "+
		"WHERE psl.ps_id=ps.ps_id AND psl.isend=2 AND ps.stat=9 and er_id="+psc.er_id.getValue()+
				" and psl_id="+psc.psl_id.getValue();
		Hr_salary_postsub_line psl=new Hr_salary_postsub_line();
		psl.findBySQL(sqlstr);
		if(!psl.isEmpty()){
			psl.isend.setAsInt(1);
			psl.save(con);
		}
		
	}

}
