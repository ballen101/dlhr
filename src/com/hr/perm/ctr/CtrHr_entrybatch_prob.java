package com.hr.perm.ctr;

import java.util.Date;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.hr.perm.entity.Hr_entry_prob;
import com.hr.perm.entity.Hr_entrybatch_prob;
import com.hr.perm.entity.Hr_entrybatch_probline;
import com.hr.util.HRUtil;

public class CtrHr_entrybatch_prob extends JPAController {
	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		if (isFilished) {
			doapprob(jpa, con);
		}
	}

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished) throws Exception {
		if (isFilished) {
			doapprob(jpa, con);
		}
	}

	/**
	 * 根据明细行，生成入职转正表单，保存并提交流程
	 * 
	 * @param jpa
	 * @param con
	 * @throws Exception
	 */
	private void doapprob(CJPA jpa, CDBConnection con) throws Exception {
		Hr_entrybatch_prob bp = (Hr_entrybatch_prob) jpa;
		CJPALineData<Hr_entrybatch_probline> pbls = bp.hr_entrybatch_problines;
		Hr_entry_prob ep = new Hr_entry_prob();
		for (CJPABase jpal : pbls) {
			Hr_entrybatch_probline pbl = (Hr_entrybatch_probline) jpal;
			ep.clear();
			ep.sourceid.setValue(pbl.sourceid.getValue()); // 入职表单ID
			ep.sourcecode.setValue(pbl.sourcecode.getValue()); // 入职编码
			ep.pbttype1.setValue("2"); // 转正类型
			ep.probation.setValue(pbl.probation.getValue()); // 试用期
			ep.pbtdate.setValue(pbl.pbtdate.getValue()); // 待转正时间
			ep.pbtlev.setValue("0"); // 转正层级
			ep.pbtsarylev.setValue("0"); // 调薪金额
			ep.overf_salary.setValue("0"); // 超标金额
			ep.overf_salary_chgtech.setValue("0"); // 超标比例
			ep.er_id.setValue(pbl.er_id.getValue()); // 档案ID
			ep.er_code.setValue(pbl.er_code.getValue()); // 档案编码
			ep.employee_code.setValue(pbl.employee_code.getValue()); // 工号
			ep.orgid.setValue(pbl.orgid.getValue()); // 部门ID
			ep.orgcode.setValue(pbl.orgcode.getValue()); // 部门编码
			ep.orgname.setValue(pbl.orgname.getValue()); // 部门名称
			ep.orghrlev.setAsInt(HRUtil.getOrgHrLev(pbl.orgid.getValue())); // 机构人事层级
			ep.id_number.setValue(pbl.id_number.getValue()); // 身份证号
			ep.employee_name.setValue(pbl.employee_name.getValue()); // 姓名
			ep.degree.setValue(pbl.degree.getValue()); // 学历
			ep.hwc_namezl.setValue(pbl.hwc_namezl.getValue()); // 职类
			ep.lv_id.setValue(pbl.lv_id.getValue()); // 职级ID
			ep.lv_num.setValue(pbl.lv_num.getValue()); // 职级
			ep.hg_id.setValue(pbl.hg_id.getValue()); // 职等ID
			ep.hg_code.setValue(pbl.hg_code.getValue()); // 职等编码
			ep.hg_name.setValue(pbl.hg_name.getValue()); // 职等名称
			ep.ospid.setValue(pbl.ospid.getValue()); // 职位ID
			ep.ospcode.setValue(pbl.ospcode.getValue()); // 职位编码
			ep.sp_name.setValue(pbl.sp_name.getValue()); // 职位名称
			ep.hiredday.setValue(pbl.hiredday.getValue()); // 入职日期
			ep.worksummary.setValue(""); // 工作总结
			ep.workeva.setValue(""); // 工作评价
			ep.wfresult.setValue(pbl.wfresult.getValue()); // 评审结果 1 同意转正 2 延长试用 3 试用不合格
			ep.wfpbprobation.setValue(pbl.wfpbprobation.getValue()); // 延长试用期
			ep.wfpbtdate.setValue(pbl.wfpbtdate.getValue()); // 延长试用日期
			ep.oldposition_salary.setValue("0"); // 转正前职位工资
			ep.oldbase_salary.setValue("0"); // 转正前基本工资
			ep.oldtech_salary.setValue("0"); // 转正前技能工资
			ep.oldachi_salary.setValue("0"); // 转正前绩效工资
			ep.oldtech_allowance.setValue("0"); // 转正前技术津贴
			ep.newposition_salary.setValue("0"); // 转正前职位工资
			ep.newbase_salary.setValue("0"); // 转正后基本工资
			ep.newtech_salary.setValue("0"); // 转正后技能工资
			ep.newachi_salary.setValue("0"); // 转正后绩效工资
			ep.newtech_allowance.setValue("0"); // 转正后技术津贴
			ep.chgposition_salary.setValue("0"); // 调整幅度职位工资
			ep.chgbase_salary.setValue("0"); // 调整幅度基本工资
			ep.chgtech_salary.setValue("0"); // 调整幅度技能工资
			ep.chgachi_salary.setValue("0"); // 调整幅度绩效工资
			ep.chgtech_allowance.setValue("0"); // 调整幅度技术津贴
			ep.salary_quotacode.setValue("0"); // 可用工资额度证明编号
			ep.salary_quota_stand.setValue("0"); // 标准工资额度
			ep.salary_quota_canuse.setValue("0"); // 可用工资额度
			ep.salary_quota_used.setValue("0"); // 己用工资额度
			ep.salary_quota_blance.setValue("0"); // 可用工资余额
			ep.salary_quota_appy.setValue("0"); // 申请工资额度
			ep.salary_quota_ovsp.setValue("0"); // 超额度审批
			ep.istp.setValue("2"); // 是否特批
			ep.isdreamposition.setValue("2"); // 是否梦职场转正
			ep.isdreamemploye.setValue("2"); // 是否梦职场储备员工
			ep.remark.setValue(pbl.remark.getValue()); // 备注
			ep.wfid.setValue(null); // wfid
			ep.attid.setValue(null); // attid
			ep.stat.setValue("1"); // 表单状态
			ep.idpath.setValue(pbl.idpath.getValue()); // idpath
			ep.entid.setValue("1"); // entid
			ep.creator.setValue("SYSTEM"); // 制单人
			ep.createtime.setAsDatetime(new Date()); // 制单时间
			ep.save(con);
			ep.wfcreate(null, con);//
		}
	}
}
