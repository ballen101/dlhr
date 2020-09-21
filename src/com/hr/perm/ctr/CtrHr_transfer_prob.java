package com.hr.perm.ctr;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.corsair.cjpa.CJPABase;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.base.entity.Hr_employeestat;
import com.hr.base.entity.Hr_orgposition;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_employee_transfer;
import com.hr.perm.entity.Hr_entry_prob;
import com.hr.perm.entity.Hr_transfer_prob;
import com.hr.perm.entity.Hr_transfer_try;
import com.hr.salary.co.Cosacommon;
import com.hr.salary.ctr.CtrSalaryCommon;
import com.hr.salary.entity.Hr_salary_chgbill;
import com.hr.salary.entity.Hr_salary_orgminstandard;
import com.hr.salary.entity.Hr_salary_positionwage;
import com.hr.salary.entity.Hr_salary_structure;
import com.hr.util.HRUtil;

import net.sf.json.JSONObject;

public class CtrHr_transfer_prob extends JPAController {

	/**
	 * 保存前
	 * 
	 * @param jpa里面有值
	 * ，还没检测数据完整性，没生成ID CODE 设置默认值
	 * @param con
	 * @param selfLink
	 * @throws Exception
	 */
	@Override
	public void BeforeSave(CJPABase jpa, CDBConnection con, boolean selfLink) throws Exception {
		Hr_transfer_prob tp = (Hr_transfer_prob) jpa;
		//		Hr_employee_transfer tr = new Hr_employee_transfer();
		//		tr.findByID(tp.sourceid.getValue(), false);
		//		if (tr.isEmpty())
		//			return;
		//		if (tr.salary_qcnotnull.getAsIntDefault(0) == 1) {
		//			if (tp.salary_quotacode.isEmpty())
		//				throw new Exception("工资额度编号不允许为空");
		//		}

		Hr_orgposition osp = new Hr_orgposition();
		osp.findByID(tp.newospid.getValue(), false);
		if (!osp.isEmpty()) {
			if ((osp.isoffjob.getAsIntDefault(0) == 1) && (tp.pbtsarylev.getAsFloatDefault(0) > 0))
				if (tp.salary_quotacode.isEmpty())
					throw new Exception("工资额度编号不允许为空");
		}
		if ((((tp.newposition_salary.getAsFloatDefault(0) > tp.oldposition_salary.getAsFloatDefault(0)) || (tp.newtech_allowance.getAsFloatDefault(0) > tp.oldtech_allowance.getAsFloatDefault(0))) && tp.pbtsarylev.getAsFloatDefault(0) == 0)||
				(((tp.newposition_salary.getAsFloatDefault(0)>tp.oldposition_salary.getAsFloatDefault(0)))
						&&tp.overf_salary.getAsFloatDefault(0)==0)) {
			resetsalaryinfo(tp);
			//throw new Exception("薪资有调整但调整额度为0，请检查单据或清理浏览器缓存后再制单。（清理浏览器缓存方法：同时按 ctr+shift+delete 即可弹出删除缓存对话框。）");
		}
		Logsw.dblog("重新计算薪资生效日期");
		Date pbtdate=Systemdate.getDateByyyyy_mm_dd(tp.pbtdate.getValue());
		Calendar ca = Calendar.getInstance();
		ca.setTime(pbtdate);
		int pbmonth=ca.get(Calendar.MONTH);
		int pbyear=ca.get(Calendar.YEAR);
		Calendar now = Calendar.getInstance();
		int nowmonth=now.get(Calendar.MONTH);
		int nowyear=now.get(Calendar.YEAR);
		
		if(pbyear>nowyear){
			tp.salarydate.setValue(tp.pbtdate.getValue());
		}else if(pbyear<nowyear){
			tp.salarydate.setValue(Systemdate.getStrDateyyyy_mm_01());
		}
		else if(pbmonth>=nowmonth){
			tp.salarydate.setValue(tp.pbtdate.getValue());
		}else if(pbmonth<nowmonth){
			tp.salarydate.setValue(Systemdate.getStrDateyyyy_mm_01());
		}
		Logsw.dblog("实际调薪日期："+tp.salarydate.getValue());
	}





	private void resetsalaryinfo(Hr_transfer_prob scs) throws Exception{
		System.out.println("-----------重新核算薪资各项");
		float np=scs.newposition_salary.getAsFloatDefault(0);
		float op=scs.oldposition_salary.getAsFloatDefault(0);
		if((np>op)&scs.pbtsarylev.getAsFloatDefault(0)==0){
			Hr_salary_structure  ss=new Hr_salary_structure();
			ss.findByID(scs.newstru_id.getValue());
			if(!ss.isEmpty()){
				if(ss.strutype.getAsInt()==1){
					float minstand=0;
					String sqlstr="SELECT * FROM `hr_salary_orgminstandard` WHERE stat=9 AND usable=1 AND INSTR('"+scs.idpath.getValue()+"',idpath)=1  ORDER BY idpath DESC  ";
					Hr_salary_orgminstandard oms=new Hr_salary_orgminstandard();
					oms.findBySQL(sqlstr);
					if(oms.isEmpty()){
						minstand=0;
					}else{
						minstand=oms.minstandard.getAsFloatDefault(0);
					}
					float bw=(np*ss.basewage.getAsFloatDefault(0))/100;
					float bow=(np*ss.otwage.getAsFloatDefault(0))/100;
					float sw=(np*ss.skillwage.getAsFloatDefault(0))/100;
					float pw=(np*ss.meritwage.getAsFloatDefault(0))/100;
					if(minstand>bw){
						if((bw+bow)>minstand){
							bow=bw+bow-minstand;
							bw=minstand;
						}else if((bw+bow+sw)>minstand){
							sw=bw+bow+sw-minstand;
							bow=0;
							bw=minstand;
						}else if((bw+bow+sw+pw)>minstand){
							pw=bw+bow+sw+pw-minstand;
							sw=0;
							bow=0;
							bw=minstand;
						}else{
							bw=np;
							pw=0;
							sw=0;
							bow=0;
						}
					}
					scs.newbase_salary.setAsFloat(bw);
					scs.newtech_salary.setAsFloat(sw);
					scs.newachi_salary.setAsFloat(pw);
					scs.newotwage.setAsFloat(bow);
					scs.chgbase_salary.setAsFloat(bw-scs.oldbase_salary.getAsFloatDefault(0));
					scs.chgtech_salary.setAsFloat(sw-scs.oldtech_salary.getAsFloatDefault(0));
					scs.chgachi_salary.setAsFloat(pw-scs.oldachi_salary.getAsFloatDefault(0));
					scs.chgotwage.setAsFloat(bow-scs.oldotwage.getAsFloatDefault(0));
				}else{
					scs.chgbase_salary.setAsFloat(scs.newbase_salary.getAsFloatDefault(0)-scs.oldbase_salary.getAsFloatDefault(0));
					scs.chgtech_salary.setAsFloat(scs.newtech_salary.getAsFloatDefault(0)-scs.oldtech_salary.getAsFloatDefault(0));
					scs.chgachi_salary.setAsFloat(scs.newachi_salary.getAsFloatDefault(0)-scs.oldachi_salary.getAsFloatDefault(0));
					scs.chgotwage.setAsFloat(scs.newotwage.getAsFloatDefault(0)-scs.oldotwage.getAsFloatDefault(0));
				}
				scs.chgposition_salary.setAsFloat(np-scs.oldposition_salary.getAsFloatDefault(0));
				scs.chgtech_allowance.setAsFloat(scs.newtech_allowance.getAsFloatDefault(0)-scs.oldtech_allowance.getAsFloatDefault(0));
				scs.pbtsarylev.setAsFloat(np+scs.newtech_allowance.getAsFloatDefault(0)-scs.oldposition_salary.getAsFloatDefault(0)-scs.oldtech_allowance.getAsFloatDefault(0));
			}
		}
		if(np>0){
			Hr_salary_positionwage spw=new Hr_salary_positionwage();
			spw.findBySQL("SELECT pw.* FROM `hr_salary_positionwage` pw,`hr_orgposition` op WHERE pw.stat=9 AND pw.usable=1 AND pw.ospid=op.sp_id AND op.ospid="+scs.newospid.getValue());
			if(spw.isEmpty()){
				scs.overf_salary.setValue(0);
				scs.overf_salary_chgtech.setValue(0);
			}else{
				if(np>spw.psl5.getAsFloatDefault(0)){
					float spwage=spw.psl5.getAsFloatDefault(0);
					float chgspw=np-spwage;
					scs.overf_salary.setAsFloat(chgspw);
					float chgper=(chgspw/spwage)*100;
					scs.overf_salary_chgtech.setAsFloat(chgper);
				}
			}
		}

	}


	/**
	 * 通用保存后，提交事务前触发
	 * 
	 * @param con
	 * @param jpa
	 * @return 不为空的返回值，将作为查询结果返回给前台
	 * @throws Exception
	 */
	@Override
	public String AfterCCoSave(CDBConnection con, CJPA jpa) throws Exception {
		Hr_transfer_prob ep = (Hr_transfer_prob) jpa;
		HashMap<String, String> pparms = CSContext.parPostDataParms();
		String jpadata = pparms.get("jpadata");
		if ((jpadata == null) || jpadata.isEmpty())
			throw new Exception("需要jpadata参数");
		JSONObject jo = JSONObject.fromObject(jpadata);
		Logsw.dblog("流程AfterCCoSave:"+ep.salarydate.getValue());
		Cosacommon.save_salary_chgblill(con, ep, jo);//保存薪资异动记录
		return null;
	}

	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		Hr_transfer_prob tp = (Hr_transfer_prob) jpa;
		wf.subject.setValue(tp.pbtcode.getValue());
		if ((tp.shw_attachs == null) || (tp.shw_attachs.size() == 0)) {
			throw new Exception("需要上传附件");
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc arg3, boolean arg4) throws Exception {
		// TODO Auto-generated method stub
		if (arg4)
			doepend(jpa, con);
		Hr_transfer_prob prob=(Hr_transfer_prob) jpa;
		CtrSalaryCommon.updateWageState(prob.salary_quotacode.getValue(),prob.employee_code.getValue());
	}

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		HRUtil.issalary_quotacode_used(3, ((Hr_transfer_prob) jpa).salary_quotacode.getValue(), ((Hr_transfer_prob) jpa).pbtid.getValue());

		String sqlstr = "SELECT * FROM hr_salary_chgbill WHERE scatype=3 AND stype=6 AND sid=" + ((Hr_transfer_prob) jpa).pbtid.getValue();
		Hr_salary_chgbill cb = new Hr_salary_chgbill();
		cb.findBySQL(con, sqlstr, false);
		if (cb.isEmpty())
			throw new Exception("薪资结构不能为空");

		if (isFilished){
			doepend(jpa, con);
			//updateSalarydate(jpa, con);//更新调薪生效月份
			
		}

	}
	/*
	 * 更新调薪生效月份
	 */


	private void doepend(CJPA jpa, CDBConnection con) throws Exception {
		Hr_transfer_prob tp = (Hr_transfer_prob) jpa;
		Hr_employee emp = new Hr_employee();
		Hr_employeestat estat = new Hr_employeestat();
		Hr_employee_transfer tsf = new Hr_employee_transfer();
		Hr_transfer_try tt = new Hr_transfer_try();

		emp.findByID4Update(con, tp.er_id.getValue(), false);
		if (emp.isEmpty())
			throw new Exception("没有找到id为【】的员工资料");
		tsf.findByID4Update(con, tp.sourceid.getValue(), false);

		estat.findBySQL("select * from hr_employeestat where statvalue=" + emp.empstatid.getValue());
		tt.findBySQL("select * from hr_transfer_try where emptranf_id=" + tsf.emptranf_id.getValue());

		int pbttype1 = tp.pbttype1.getAsInt();// 转正类型 实习转正 入职转正 调动转正
		if (pbttype1 == 1) {
			if (estat.issxenprob.getAsInt() != 1) {
				throw new Exception("状态为【" + estat.language1.getValue() + "】的员工不允许【实习生转正操作】");
			}
		} else if (pbttype1 == 2) {
			if (estat.isptemprob.getAsInt() != 1) {
				throw new Exception("状态为【" + estat.language1.getValue() + "】的员工不允许【转正操作】");
			}
		} else if (pbttype1 == 3) {
			if (estat.isjxprob.getAsInt() != 1) {
				throw new Exception("状态为【" + estat.language1.getValue() + "】的员工不允许【考察期转正操作】");
			}
		} else
			throw new Exception("转正类型错误【" + pbttype1 + "】");
		int wfresult = tp.wfresult.getAsInt(); // 1 同意转正 2 延长考察 3 降职 4 其它
		if (wfresult == 1) {// 1 同意转正
			emp.empstatid.setAsInt(4);// 正式
			tsf.ispromotioned.setAsInt(1);// 已经转正
			// dosetSACHGLog(tp, con);
			CtrSalaryCommon.newSalaryChangeLog(con, tp);//保存薪资记录
			if (!tt.isEmpty()) {
				if (tt.delaytimes.getAsIntDefault(0) > 0) {
					tt.delaypromotiondaytrue.setAsDatetime(new Date());// 延长过考察期转正时间
				} else {
					tt.probationdatetrue.setAsDatetime(new Date());// 没延长过考察期转正时间
				}
				tt.trystat.setAsInt(4);// 1考察期中、2考察过期、3考察延期、4己转正、5考察不合格
				tt.save(con);
			}
		} else if (wfresult == 2) {// 2 延长考察
			tsf.probationdate.setValue(tp.wfpbtdate.getValue());
			if (!tt.isEmpty()) {
				tt.delaypromotionday.setValue(tp.wfpbtdate.getValue());
				tt.delaytimes.setAsInt(tt.delaytimes.getAsIntDefault(0) + 1);
				tt.trystat.setAsInt(3);// 1考察期中、2考察过期、3考察延期、4己转正、5考察不合格
				tt.delayprobation.setAsInt(tt.delayprobation.getAsIntDefault(0) + tp.wfpbprobation.getAsIntDefault(0));
				tt.save(con);
			}
		} else if (wfresult == 3) {
			// 建议降职
		} else if (wfresult == 4) {
			// 其它建议
		} else
			throw new Exception("评审结果错误");
		emp.save(con, false);
		tsf.save(con, false);
	};

	// private void dosetSACHGLog(Hr_transfer_prob tp, CDBConnection con) throws Exception {
	// Hrsa_chglg sac = new Hrsa_chglg();
	// sac.er_id.setValue(tp.er_id.getValue()); // 人事ID
	// sac.scatype.setValue("3"); // 入职定薪、调薪核薪、转正调薪、年度调薪、特殊调薪
	// sac.stype.setValue("6"); // 来源类别 历史数据、实习登记、入职表单、调动表单、入职转正、调动转正....
	// sac.sid.setValue(tp.pbtid.getValue()); // 来源ID
	// sac.scode.setValue(tp.pbtcode.getValue()); // 来源单号
	// sac.oldattendtype.setValue(tp.odattendtype.getValue()); // 调薪前出勤类别
	// sac.oldcalsalarytype.setValue(tp.oldcalsalarytype.getValue()); // 调薪前计薪方式
	// sac.oldposition_salary.setValue(tp.oldposition_salary.getValue()); // 调薪前职位工资
	// sac.oldbase_salary.setValue(tp.oldbase_salary.getValue()); // 调薪前基本工资
	// sac.oldtech_salary.setValue(tp.oldtech_salary.getValue()); // 调薪前技能工资
	// sac.oldachi_salary.setValue(tp.oldachi_salary.getValue()); // 调薪前技能工资
	// sac.oldtech_allowance.setValue(tp.oldtech_salary.getValue()); // 调薪前技术津贴
	// sac.oldavg_salary.setValue("0"); // 调薪前平均工资
	// sac.newattendtype.setValue(tp.newattendtype.getValue()); // 调薪后出勤类别
	// sac.newcalsalarytype.setValue(tp.newcalsalarytype.getValue()); // 调薪后计薪方式
	// sac.newposition_salary.setValue(tp.newposition_salary.getValue()); // 调薪前职位工资
	// sac.newbase_salary.setValue(tp.newbase_salary.getValue()); // 调薪后基本工资
	// sac.newtech_salary.setValue(tp.newtech_salary.getValue()); // 调薪后技能工资
	// sac.newachi_salary.setValue(tp.newachi_salary.getValue()); // 调薪后技能工资
	// sac.newtech_allowance.setValue(tp.newtech_salary.getValue()); // 调薪后技术津贴
	// sac.sacrage.setAsFloat(sac.newposition_salary.getAsFloatDefault(0) - sac.oldposition_salary.getAsFloatDefault(0)); // 调薪幅度
	// sac.chgdate.setValue(tp.pbtdate.getValue()); // 调薪日期
	// sac.chgreason.setValue(""); // 调薪原因
	// sac.remark.setValue(""); // 备注
	// sac.createtime.setAsDatetime(new Date()); // 创建时间
	// sac.save(con);
	// }
}
