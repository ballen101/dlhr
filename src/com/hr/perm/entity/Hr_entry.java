package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.perm.ctr.CtrHr_entry;

import java.sql.Types;

@CEntity(controller = CtrHr_entry.class)
public class Hr_entry extends CJPA {
    @CFieldinfo(fieldname = "entry_id", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
    public CField entry_id; // ID
    @CFieldinfo(fieldname = "entry_code", codeid = 58, notnull = true, caption = "入职表单编码", datetype = Types.VARCHAR)
    public CField entry_code; // 入职表单编码
    @CFieldinfo(fieldname = "er_id", notnull = true, caption = "人事档案ID", datetype = Types.INTEGER)
    public CField er_id; // 人事档案ID
    @CFieldinfo(fieldname = "employee_code", notnull = true, caption = "工号", datetype = Types.VARCHAR)
    public CField employee_code; // 工号
    @CFieldinfo(fieldname = "entrytype", notnull = true, caption = "入职类型 新招 重聘", datetype = Types.INTEGER)
    public CField entrytype; // 入职类型 新招 重聘
    @CFieldinfo(fieldname = "entrysourcr", notnull = true, caption = "人员来源 社会招聘/校园招聘/其它", datetype = Types.INTEGER)
    public CField entrysourcr; // 人员来源 社会招聘/校园招聘/其它
    @CFieldinfo(fieldname = "entrydate", caption = "入职日期", datetype = Types.TIMESTAMP)
    public CField entrydate; // 入职日期
    @CFieldinfo(fieldname = "probation", caption = "试用期", datetype = Types.INTEGER)
    public CField probation; // 试用期
    @CFieldinfo(fieldname = "promotionday", caption = "转正日期", datetype = Types.TIMESTAMP)
    public CField promotionday; // 转正日期
    @CFieldinfo(fieldname = "ispromotioned", caption = "已转正", datetype = Types.INTEGER)
    public CField ispromotioned; // 已转正
    @CFieldinfo(fieldname = "quota_over", caption = "是否超编", datetype = Types.INTEGER)
    public CField quota_over; // 是否超编
    @CFieldinfo(fieldname = "quota_over_rst", caption = "超编审批结果 1 允许增加编制入职 ps是否自动生成编制调整单 2 超编入职 3 不允许入职", datetype = Types.INTEGER)
    public CField quota_over_rst; // 超编审批结果 1 允许增加编制入职 ps是否自动生成编制调整单 2 超编入职 3
				  // 不允许入职
    @CFieldinfo(fieldname = "orgid", caption = "入职机构ID", datetype = Types.INTEGER)
    public CField orgid; // 入职机构ID
    @CFieldinfo(fieldname = "orghrlev", notnull = true, caption = "机构人事层级", datetype = Types.INTEGER)
    public CField orghrlev; // 机构人事层级
    @CFieldinfo(fieldname = "ospid", precision = 10, scale = 0, caption = "入职职位", datetype = Types.INTEGER)
    public CField ospid; // 入职职位
    @CFieldinfo(fieldname = "lv_num", caption = "入职职级", datetype = Types.DECIMAL)
    public CField lv_num; // 入职职级
    @CFieldinfo(fieldname = "rectname", caption = "招聘人", datetype = Types.VARCHAR)
    public CField rectname; // 招聘人
    @CFieldinfo(fieldname = "rectcode", caption = "招聘人工号", datetype = Types.VARCHAR)
    public CField rectcode; // 招聘人工号
    @CFieldinfo(fieldname = "quachk", caption = "资格比对 合格不合格", datetype = Types.INTEGER)
    public CField quachk; // 资格比对 合格不合格
    @CFieldinfo(fieldname = "quachknote", caption = "资格比对备注", datetype = Types.VARCHAR)
    public CField quachknote; // 资格比对备注
    @CFieldinfo(fieldname = "iview", caption = "面试 合格不合格", datetype = Types.INTEGER)
    public CField iview; // 面试 合格不合格
    @CFieldinfo(fieldname = "iviewnote", caption = "面试 备注", datetype = Types.VARCHAR)
    public CField iviewnote; // 面试 备注
    @CFieldinfo(fieldname = "forunit", caption = "应聘单位", datetype = Types.VARCHAR)
    public CField forunit; // 应聘单位
    @CFieldinfo(fieldname = "forunitnote", caption = "应聘单位备注", datetype = Types.VARCHAR)
    public CField forunitnote; // 应聘单位备注
    @CFieldinfo(fieldname = "cercheck", caption = "证件检查 合格 不合格", datetype = Types.INTEGER)
    public CField cercheck; // 证件检查 合格 不合格
    @CFieldinfo(fieldname = "cerchecknote", caption = "证件检查备注", datetype = Types.VARCHAR)
    public CField cerchecknote; // 证件检查备注
    @CFieldinfo(fieldname = "wtexam", caption = "笔试 合格不合格", datetype = Types.INTEGER)
    public CField wtexam; // 笔试 合格不合格
    @CFieldinfo(fieldname = "wtexamnote", caption = "笔试备注", datetype = Types.VARCHAR)
    public CField wtexamnote; // 笔试备注
    @CFieldinfo(fieldname = "transorg", caption = "输送机构", datetype = Types.VARCHAR)
    public CField transorg; // 输送机构
    @CFieldinfo(fieldname = "transorgnote", caption = "输送机构备注", datetype = Types.VARCHAR)
    public CField transorgnote; // 输送机构备注
    @CFieldinfo(fieldname = "formchk", caption = "形体检查 合格不合格", datetype = Types.INTEGER)
    public CField formchk; // 形体检查 合格不合格
    @CFieldinfo(fieldname = "formchknote", caption = "形体检查备注", datetype = Types.VARCHAR)
    public CField formchknote; // 形体检查备注
    @CFieldinfo(fieldname = "train", caption = "培训合格不合格", datetype = Types.INTEGER)
    public CField train; // 培训合格不合格
    @CFieldinfo(fieldname = "trainnote", caption = "培训备注", datetype = Types.VARCHAR)
    public CField trainnote; // 培训备注
    @CFieldinfo(fieldname = "transextime", caption = "输送期限", datetype = Types.VARCHAR)
    public CField transextime; // 输送期限
    @CFieldinfo(fieldname = "transextimenote", caption = "输送期限备注", datetype = Types.VARCHAR)
    public CField transextimenote; // 输送期限备注
    @CFieldinfo(fieldname = "tetype", caption = "人员类型 劳务工、临时工、正式工", datetype = Types.INTEGER)
    public CField tetype; // 人员类型 劳务工、临时工、正式工
    @CFieldinfo(fieldname = "tetypenote", caption = "人员类型备注", datetype = Types.VARCHAR)
    public CField tetypenote; // 人员类型备注
    @CFieldinfo(fieldname = "meexam", caption = "体检 合格不合格", datetype = Types.INTEGER)
    public CField meexam; // 体检 合格不合格
    @CFieldinfo(fieldname = "meexamnote", caption = "体检备注", datetype = Types.VARCHAR)
    public CField meexamnote; // 体检备注
    @CFieldinfo(fieldname = "dispunit", caption = "派遣机构", datetype = Types.VARCHAR)
    public CField dispunit; // 派遣机构
    @CFieldinfo(fieldname = "dispunitnote", caption = "派遣机构说明", datetype = Types.VARCHAR)
    public CField dispunitnote; // 派遣机构说明
    @CFieldinfo(fieldname = "ovtype", caption = "加班类型 有  无", datetype = Types.INTEGER)
    public CField ovtype; // 加班类型 有 无
    @CFieldinfo(fieldname = "ovtypenote", caption = "加班类型说明", datetype = Types.VARCHAR)
    public CField ovtypenote; // 加班类型说明
    @CFieldinfo(fieldname = "rcenl", caption = "招聘渠道", datetype = Types.VARCHAR)
    public CField rcenl; // 招聘渠道
    @CFieldinfo(fieldname = "rcenlnote", caption = "招聘渠道说明", datetype = Types.VARCHAR)
    public CField rcenlnote; // 招聘渠道说明
    @CFieldinfo(fieldname = "dispeextime", caption = "派遣期限", datetype = Types.VARCHAR)
    public CField dispeextime; // 派遣期限
    @CFieldinfo(fieldname = "dispeextimenote", caption = "派遣期限说明", datetype = Types.VARCHAR)
    public CField dispeextimenote; // 派遣期限说明
    @CFieldinfo(fieldname = "chkok", caption = "验证合格", datetype = Types.INTEGER)
    public CField chkok; // 验证合格
    @CFieldinfo(fieldname = "chkigmsg", caption = "验证不合格原因", datetype = Types.VARCHAR)
    public CField chkigmsg; // 验证不合格原因
    @CFieldinfo(fieldname = "salarydate", caption = "核薪生效日期", datetype = Types.DATE)
    public CField salarydate; // 核薪生效日期
    @CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
    public CField remark; // 备注
    @CFieldinfo(fieldname = "stru_id", precision = 10, scale = 0, caption = "工资结构ID", datetype = Types.INTEGER)
    public CField stru_id; // 工资结构ID
    @CFieldinfo(fieldname = "stru_name", precision = 32, scale = 0, caption = "工资结构名", datetype = Types.VARCHAR)
    public CField stru_name; // 工资结构名
    @CFieldinfo(fieldname = "position_salary", precision = 10, scale = 2, caption = "职位工资", defvalue = "0.00", datetype = Types.DECIMAL)
    public CField position_salary; // 职位工资
    @CFieldinfo(fieldname = "base_salary", precision = 10, scale = 2, caption = "基本工资", defvalue = "0.00", datetype = Types.DECIMAL)
    public CField base_salary; // 基本工资
    @CFieldinfo(fieldname = "tech_salary", precision = 10, scale = 2, caption = "技能工资", defvalue = "0.00", datetype = Types.DECIMAL)
    public CField tech_salary; // 技能工资
    @CFieldinfo(fieldname = "achi_salary", precision = 10, scale = 2, caption = "绩效工资", defvalue = "0.00", datetype = Types.DECIMAL)
    public CField achi_salary; // 绩效工资
    @CFieldinfo(fieldname = "otwage", precision = 10, scale = 2, caption = "固定加班工资", defvalue = "0.00", datetype = Types.DECIMAL)
    public CField otwage; // 固定加班工资
    @CFieldinfo(fieldname = "tech_allowance", precision = 10, scale = 2, caption = "技术津贴", defvalue = "0.00", datetype = Types.DECIMAL)
    public CField tech_allowance; // 技术津贴
    @CFieldinfo(fieldname = "postsubs", precision = 10, scale = 2, caption = "岗位津贴", defvalue = "0.00", datetype = Types.DECIMAL)
    public CField postsubs; // 岗位津贴
    @CFieldinfo(fieldname = "wfid", caption = "wfid", datetype = Types.INTEGER)
    public CField wfid; // wfid
    @CFieldinfo(fieldname = "attid", caption = "attid", datetype = Types.INTEGER)
    public CField attid; // attid
    @CFieldinfo(fieldname = "stat", notnull = true, caption = "流程状态", datetype = Types.INTEGER)
    public CField stat; // 流程状态
    @CFieldinfo(fieldname = "idpath", notnull = true, caption = "idpath", datetype = Types.VARCHAR)
    public CField idpath; // idpath
    @CFieldinfo(fieldname = "entid", notnull = true, caption = "entid", datetype = Types.INTEGER)
    public CField entid; // entid
    @CFieldinfo(fieldname = "creator", notnull = true, caption = "创建人", datetype = Types.VARCHAR)
    public CField creator; // 创建人
    @CFieldinfo(fieldname = "createtime", notnull = true, caption = "创建时间", datetype = Types.TIMESTAMP)
    public CField createtime; // 创建时间
    @CFieldinfo(fieldname = "updator", caption = "更新人", datetype = Types.VARCHAR)
    public CField updator; // 更新人
    @CFieldinfo(fieldname = "updatetime", caption = "更新时间", datetype = Types.TIMESTAMP)
    public CField updatetime; // 更新时间
    @CFieldinfo(fieldname = "attribute1", caption = "备用字段1", datetype = Types.VARCHAR)
    public CField attribute1; // 备用字段1 记录自动转正信息
    @CFieldinfo(fieldname = "attribute2", caption = "备用字段2", datetype = Types.VARCHAR)
    public CField attribute2; // 备用字段2
    @CFieldinfo(fieldname = "attribute3", caption = "备用字段3", datetype = Types.VARCHAR)
    public CField attribute3; // 备用字段3
    @CFieldinfo(fieldname = "attribute4", caption = "备用字段4", datetype = Types.VARCHAR)
    public CField attribute4; // 备用字段4
    @CFieldinfo(fieldname = "attribute5", caption = "备用字段5", datetype = Types.VARCHAR)
    public CField attribute5; // 备用字段5
	@CFieldinfo(fieldname = "pay_way", caption = "计薪方式", datetype = Types.VARCHAR)
	public CField pay_way; // 计薪方式
    public String SqlWhere; // 查询附加条件
    public int MaxCount; // 查询最大数量

    // 自关联数据定义
    @CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
    public CJPALineData<Shw_attach> shw_attachs;

    public Hr_entry() throws Exception {
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
