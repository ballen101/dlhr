package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.perm.ctr.CtrHr_entry_prob;

import java.sql.Types;

@CEntity(controller = CtrHr_entry_prob.class)
public class Hr_entry_prob extends CJPA {
	@CFieldinfo(fieldname = "pbtid", iskey = true, notnull = true, caption = "转正ID", datetype = Types.INTEGER)
	public CField pbtid; // 转正ID
	@CFieldinfo(fieldname = "pbtcode", codeid = 61, notnull = true, caption = "转正编码", datetype = Types.VARCHAR)
	public CField pbtcode; // 转正编码
	@CFieldinfo(fieldname = "sourceid", iskey = false, notnull = true, caption = "入职表单ID", datetype = Types.INTEGER)
	public CField sourceid; // 入职表单ID
	@CFieldinfo(fieldname = "sourcecode", notnull = true, precision = 15, scale = 0, caption = "入职编码", datetype = Types.VARCHAR)
	public CField sourcecode; // 入职编码
	@CFieldinfo(fieldname = "pbttype1", notnull = true, precision = 2, scale = 0, caption = "转正类型", defvalue = "2", datetype = Types.INTEGER)
	public CField pbttype1; // 转正类型
	@CFieldinfo(fieldname = "probation", notnull = true, precision = 2, scale = 0, caption = "试用期", datetype = Types.INTEGER)
	public CField probation; // 试用期
	@CFieldinfo(fieldname = "pbtdate", notnull = true, precision = 19, scale = 0, caption = "待转正时间", datetype = Types.TIMESTAMP)
	public CField pbtdate; // 待转正时间
	@CFieldinfo(fieldname = "pbtlev", notnull = true, precision = 2, dicid = 1223, scale = 0, caption = "转正层级", datetype = Types.INTEGER)
	public CField pbtlev; // 转正层级
	@CFieldinfo(fieldname = "pbtsarylev", precision = 10, scale = 2, caption = "调薪金额", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField pbtsarylev; // 调薪金额
	@CFieldinfo(fieldname = "overf_salary", precision = 10, scale = 2, caption = "超标金额", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField overf_salary; // 超标金额
	@CFieldinfo(fieldname = "overf_salary_chgtech", precision = 10, scale = 2, caption = "超标比例", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField overf_salary_chgtech; // 超标比例
	@CFieldinfo(fieldname = "er_id", notnull = true, precision = 20, scale = 0, caption = "档案ID", datetype = Types.INTEGER)
	public CField er_id; // 档案ID
	@CFieldinfo(fieldname = "er_code", precision = 16, scale = 0, caption = "档案编码", datetype = Types.VARCHAR)
	public CField er_code; // 档案编码
	@CFieldinfo(fieldname = "employee_code", notnull = true, precision = 16, scale = 0, caption = "工号", datetype = Types.VARCHAR)
	public CField employee_code; // 工号
	@CFieldinfo(fieldname = "orgid", notnull = true, precision = 10, scale = 0, caption = "部门ID", datetype = Types.INTEGER)
	public CField orgid; // 部门ID
	@CFieldinfo(fieldname = "orgcode", notnull = true, precision = 16, scale = 0, caption = "部门编码", datetype = Types.VARCHAR)
	public CField orgcode; // 部门编码
	@CFieldinfo(fieldname = "orgname", notnull = true, precision = 128, scale = 0, caption = "部门名称", datetype = Types.VARCHAR)
	public CField orgname; // 部门名称
	@CFieldinfo(fieldname = "orghrlev", notnull = true, precision = 1, scale = 0, caption = "机构人事层级", defvalue = "0", datetype = Types.INTEGER)
	public CField orghrlev; // 机构人事层级
	@CFieldinfo(fieldname = "id_number", notnull = true, precision = 20, scale = 0, caption = "身份证号", datetype = Types.VARCHAR)
	public CField id_number; // 身份证号
	@CFieldinfo(fieldname = "employee_name", notnull = true, precision = 64, scale = 0, caption = "姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 姓名
	@CFieldinfo(fieldname = "degree", precision = 2, scale = 0, caption = "学历", datetype = Types.INTEGER)
	public CField degree; // 学历
	@CFieldinfo(fieldname = "hwc_namezl", precision = 32, scale = 0, caption = "职类", datetype = Types.VARCHAR)
	public CField hwc_namezl; // 职类
	@CFieldinfo(fieldname = "lv_id", precision = 10, scale = 0, caption = "职级ID", datetype = Types.INTEGER)
	public CField lv_id; // 职级ID
	@CFieldinfo(fieldname = "lv_num", precision = 4, scale = 1, caption = "职级", datetype = Types.DECIMAL)
	public CField lv_num; // 职级
	@CFieldinfo(fieldname = "hg_id", precision = 10, scale = 0, caption = "职等ID", datetype = Types.INTEGER)
	public CField hg_id; // 职等ID
	@CFieldinfo(fieldname = "hg_code", precision = 16, scale = 0, caption = "职等编码", datetype = Types.VARCHAR)
	public CField hg_code; // 职等编码
	@CFieldinfo(fieldname = "hg_name", precision = 128, scale = 0, caption = "职等名称", datetype = Types.VARCHAR)
	public CField hg_name; // 职等名称
	@CFieldinfo(fieldname = "ospid", precision = 20, scale = 0, caption = "职位ID", datetype = Types.INTEGER)
	public CField ospid; // 职位ID
	@CFieldinfo(fieldname = "ospcode", precision = 16, scale = 0, caption = "职位编码", datetype = Types.VARCHAR)
	public CField ospcode; // 职位编码
	@CFieldinfo(fieldname = "sp_name", precision = 128, scale = 0, caption = "职位名称", datetype = Types.VARCHAR)
	public CField sp_name; // 职位名称
	@CFieldinfo(fieldname = "hiredday", precision = 19, scale = 0, caption = "入职日期", datetype = Types.TIMESTAMP)
	public CField hiredday; // 入职日期
	@CFieldinfo(fieldname = "worksummary", precision = 1024, scale = 0, caption = "工作总结", datetype = Types.VARCHAR)
	public CField worksummary; // 工作总结
	@CFieldinfo(fieldname = "workeva", precision = 1024, scale = 0, caption = "工作评价", datetype = Types.VARCHAR)
	public CField workeva; // 工作评价
	@CFieldinfo(fieldname = "wfresult", precision = 2, scale = 0, dicid = 745, caption = "评审结果  1 同意转正 2 延长试用 3 试用不合格", datetype = Types.INTEGER)
	public CField wfresult; // 评审结果 1 同意转正 2 延长试用 3 试用不合格
	@CFieldinfo(fieldname = "wfpbprobation", precision = 2, scale = 0, dicid = 705, caption = "延长试用期", datetype = Types.INTEGER)
	public CField wfpbprobation; // 延长试用期
	@CFieldinfo(fieldname = "wfpbtdate", precision = 19, scale = 0, caption = "延长试用日期", datetype = Types.TIMESTAMP)
	public CField wfpbtdate; // 延长试用日期
	@CFieldinfo(fieldname = "oldstru_id", precision = 10, scale = 0, caption = "调薪前工资结构ID", datetype = Types.INTEGER)
	public CField oldstru_id; // 调薪前工资结构ID
	@CFieldinfo(fieldname = "oldstru_name", precision = 32, scale = 0, caption = "调薪前工资结构名", datetype = Types.VARCHAR)
	public CField oldstru_name; // 调薪前工资结构名
	@CFieldinfo(fieldname = "oldchecklev", precision = 1, scale = 0,dicid=1437, caption = "调薪前绩效考核层级", datetype = Types.INTEGER)
	public CField oldchecklev; // 调薪前绩效考核层级
	@CFieldinfo(fieldname = "oldattendtype", precision = 11, scale = 0,dicid=1399, caption = "调薪前出勤类别", datetype = Types.INTEGER)
	public CField oldattendtype; // 调薪前出勤类别
	@CFieldinfo(fieldname = "oldposition_salary", precision = 10, scale = 2, caption = "转正前职位工资", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField oldposition_salary; // 转正前职位工资
	@CFieldinfo(fieldname = "oldbase_salary", precision = 10, scale = 2, caption = "转正前基本工资", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField oldbase_salary; // 转正前基本工资
	@CFieldinfo(fieldname = "oldotwage", precision = 10, scale = 2, caption = "转正前固定加班工资", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField oldotwage; // 转正前固定加班工资
	@CFieldinfo(fieldname = "oldtech_salary", precision = 10, scale = 2, caption = "转正前技能工资", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField oldtech_salary; // 转正前技能工资
	@CFieldinfo(fieldname = "oldachi_salary", precision = 10, scale = 2, caption = "转正前绩效工资", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField oldachi_salary; // 转正前绩效工资
	@CFieldinfo(fieldname = "oldtech_allowance", precision = 10, scale = 2, caption = "转正前技术津贴", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField oldtech_allowance; // 转正前技术津贴
	@CFieldinfo(fieldname = "newstru_id", precision = 10, scale = 0, caption = "调薪后工资结构ID", datetype = Types.INTEGER)
	public CField newstru_id; // 调薪后工资结构ID
	@CFieldinfo(fieldname = "newstru_name", precision = 32, scale = 0, caption = "调薪后工资结构名", datetype = Types.VARCHAR)
	public CField newstru_name; // 调薪后工资结构名
	@CFieldinfo(fieldname = "newchecklev", precision = 1, scale = 0,dicid=1437, caption = "调薪后绩效考核层级", datetype = Types.INTEGER)
	public CField newchecklev; // 调薪后绩效考核层级
	@CFieldinfo(fieldname = "newattendtype", precision = 1, scale = 0,dicid=1399, caption = "调薪后出勤类别", datetype = Types.INTEGER)
	public CField newattendtype; // 调薪后出勤类别
	@CFieldinfo(fieldname = "newposition_salary", precision = 10, scale = 2, caption = "转正后职位工资", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField newposition_salary; // 转正后职位工资
	@CFieldinfo(fieldname = "newbase_salary", precision = 10, scale = 2, caption = "转正后基本工资", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField newbase_salary; // 转正后基本工资
	@CFieldinfo(fieldname = "newotwage", precision = 10, scale = 2, caption = "转正后固定加班工资", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField newotwage; // 转正后固定加班工资
	@CFieldinfo(fieldname = "newtech_salary", precision = 10, scale = 2, caption = "转正后技能工资", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField newtech_salary; // 转正后技能工资
	@CFieldinfo(fieldname = "newachi_salary", precision = 10, scale = 2, caption = "转正后绩效工资", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField newachi_salary; // 转正后绩效工资
	@CFieldinfo(fieldname = "newtech_allowance", precision = 10, scale = 2, caption = "转正后技术津贴", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField newtech_allowance; // 转正后技术津贴
	@CFieldinfo(fieldname = "chgposition_salary", precision = 10, scale = 2, caption = "调整幅度职位工资", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField chgposition_salary; // 调整幅度职位工资
	@CFieldinfo(fieldname = "chgbase_salary", precision = 10, scale = 2, caption = "调整幅度基本工资", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField chgbase_salary; // 调整幅度基本工资
	@CFieldinfo(fieldname = "chgotwage", precision = 10, scale = 2, caption = "调整幅度固定加班工资", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField chgotwage; // 调整幅度固定加班工资
	@CFieldinfo(fieldname = "chgtech_salary", precision = 10, scale = 2, caption = "调整幅度技能工资", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField chgtech_salary; // 调整幅度技能工资
	@CFieldinfo(fieldname = "chgachi_salary", precision = 10, scale = 2, caption = "调整幅度绩效工资", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField chgachi_salary; // 调整幅度绩效工资
	@CFieldinfo(fieldname = "chgtech_allowance", precision = 10, scale = 2, caption = "调整幅度技术津贴", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField chgtech_allowance; // 调整幅度技术津贴
	@CFieldinfo(fieldname = "salary_quotacode", precision = 16, scale = 0, caption = "可用工资额度证明编号", datetype = Types.VARCHAR)
	public CField salary_quotacode; // 可用工资额度证明编号
	@CFieldinfo(fieldname = "salary_quota_stand", precision = 10, scale = 2, caption = "标准工资额度", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField salary_quota_stand; // 标准工资额度
	@CFieldinfo(fieldname = "salary_quota_canuse", precision = 10, scale = 2, caption = "可用工资额度", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField salary_quota_canuse; // 可用工资额度
	@CFieldinfo(fieldname = "salary_quota_used", precision = 10, scale = 2, caption = "己用工资额度", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField salary_quota_used; // 己用工资额度
	@CFieldinfo(fieldname = "salary_quota_blance", precision = 10, scale = 2, caption = "可用工资余额", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField salary_quota_blance; // 可用工资余额
	@CFieldinfo(fieldname = "salary_quota_appy", precision = 10, scale = 2, caption = "申请工资额度", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField salary_quota_appy; // 申请工资额度
	@CFieldinfo(fieldname = "salary_quota_ovsp", precision = 32, scale = 0, dicid = 5, caption = "超额度审批", datetype = Types.VARCHAR)
	public CField salary_quota_ovsp; // 超额度审批
	@CFieldinfo(fieldname = "salarydate", precision = 10, scale = 0, caption = "核薪生效日期", datetype = Types.DATE)
	public CField salarydate; // 核薪生效日期
	@CFieldinfo(fieldname = "istp", precision = 1, scale = 0, caption = "是否特批", defvalue = "2", datetype = Types.INTEGER)
	public CField istp; // 是否特批
	@CFieldinfo(fieldname = "isdreamposition", precision = 1, scale = 0, caption = "是否梦职场转正", defvalue = "2", datetype = Types.INTEGER)
	public CField isdreamposition; // 是否梦职场转正
	@CFieldinfo(fieldname = "isdreamemploye", precision = 1, scale = 0, caption = "是否梦职场储备员工", defvalue = "2", datetype = Types.INTEGER)
	public CField isdreamemploye; // 是否梦职场储备员工
	@CFieldinfo(fieldname = "remark", precision = 512, scale = 0, caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "wfid", precision = 20, scale = 0, caption = "wfid", datetype = Types.INTEGER)
	public CField wfid; // wfid
	@CFieldinfo(fieldname = "attid", precision = 20, scale = 0, caption = "attid", datetype = Types.INTEGER)
	public CField attid; // attid
	@CFieldinfo(fieldname = "stat", notnull = true, precision = 2, scale = 0, caption = "表单状态", datetype = Types.INTEGER)
	public CField stat; // 表单状态
	@CFieldinfo(fieldname = "idpath", notnull = true, precision = 256, scale = 0, caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	@CFieldinfo(fieldname = "entid", notnull = true, precision = 5, scale = 0, caption = "entid", datetype = Types.INTEGER)
	public CField entid; // entid
	@CFieldinfo(fieldname = "creator", notnull = true, precision = 32, scale = 0, caption = "制单人", datetype = Types.VARCHAR)
	public CField creator; // 制单人
	@CFieldinfo(fieldname = "createtime", notnull = true, precision = 19, scale = 0, caption = "制单时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 制单时间
	@CFieldinfo(fieldname = "updator", precision = 32, scale = 0, caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", precision = 19, scale = 0, caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	@CFieldinfo(fieldname = "attribute1", precision = 32, scale = 0, caption = "备用字段1", datetype = Types.VARCHAR)
	public CField attribute1; // 备用字段1
	@CFieldinfo(fieldname = "attribute2", precision = 32, scale = 0, caption = "备用字段2", datetype = Types.VARCHAR)
	public CField attribute2; // 备用字段2
	@CFieldinfo(fieldname = "attribute3", precision = 32, scale = 0, caption = "备用字段3", datetype = Types.VARCHAR)
	public CField attribute3; // 备用字段3
	@CFieldinfo(fieldname = "attribute4", precision = 32, scale = 0, caption = "备用字段4", datetype = Types.VARCHAR)
	public CField attribute4; // 备用字段4
	@CFieldinfo(fieldname = "attribute5", precision = 32, scale = 0, caption = "备用字段5", datetype = Types.VARCHAR)
	public CField attribute5; // 备用字段5
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义
	@CLinkFieldInfo(jpaclass = Hr_entry_prob_kh.class, linkFields = { @LinkFieldItem(lfield = "pbtid", mfield = "pbtid") })
	public CJPALineData<Hr_entry_prob_kh> hr_entry_prob_khs;
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hr_entry_prob() throws Exception {
	}

	@Override
	public boolean InitObject() {// 类初始化调用的方法
		super.InitObject();
		return true;
	}

	@Override
	public boolean FinalObject() { // 类释放前调用的方法
		super.FinalObject();
		return true;
	}
}
