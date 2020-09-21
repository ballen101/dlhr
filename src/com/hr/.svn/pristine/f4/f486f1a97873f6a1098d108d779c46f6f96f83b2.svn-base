package com.hr.recruit.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;

import java.sql.Types;

@CEntity(caption = "招募表单")
public class Hr_recruit_form extends CJPA {
	@CFieldinfo(fieldname = "recruit_id", iskey = true, notnull = true, precision = 20, scale = 0, caption = "ID", datetype = Types.INTEGER)
	public CField recruit_id; // ID
	@CFieldinfo(fieldname = "recruit_code", codeid = 121, notnull = true, precision = 21, scale = 0, caption = "招募表单编码", datetype = Types.VARCHAR)
	public CField recruit_code; // 招募表单编码
	@CFieldinfo(fieldname = "er_id", notnull = true, precision = 20, scale = 0, caption = "人事档案ID", datetype = Types.INTEGER)
	public CField er_id; // 人事档案ID
	@CFieldinfo(fieldname = "employee_code", notnull = true, precision = 21, scale = 0, caption = "工号", datetype = Types.VARCHAR)
	public CField employee_code; // 工号
	@CFieldinfo(fieldname = "entrytype", notnull = true, precision = 2, scale = 0, caption = "入职类型 新招 重聘", datetype = Types.INTEGER)
	public CField entrytype; // 入职类型 新招 重聘
	@CFieldinfo(fieldname = "entrysourcr", notnull = true, precision = 2, scale = 0, caption = "人员来源 社会招聘/校园招聘/其它", datetype = Types.INTEGER)
	public CField entrysourcr; // 人员来源 社会招聘/校园招聘/其它
	@CFieldinfo(fieldname = "entrydate", precision = 19, scale = 0, caption = "入职日期", datetype = Types.TIMESTAMP)
	public CField entrydate; // 入职日期
	@CFieldinfo(fieldname = "entryaddr", precision = 128, scale = 0, caption = "招募地点", datetype = Types.VARCHAR)
	public CField entryaddr; // 招募地点
	@CFieldinfo(fieldname = "probation", precision = 2, scale = 0, caption = "试用期", datetype = Types.INTEGER)
	public CField probation; // 试用期
	@CFieldinfo(fieldname = "promotionday", precision = 19, scale = 0, caption = "转正日期", datetype = Types.TIMESTAMP)
	public CField promotionday; // 转正日期
	@CFieldinfo(fieldname = "ispromotioned", precision = 1, scale = 0, caption = "已转正", defvalue = "2", datetype = Types.INTEGER)
	public CField ispromotioned; // 已转正
	@CFieldinfo(fieldname = "quota_over", precision = 1, scale = 0, caption = "是否超编", defvalue = "2", datetype = Types.INTEGER)
	public CField quota_over; // 是否超编
	@CFieldinfo(fieldname = "quota_over_rst", precision = 1, scale = 0, caption = "超编审批结果 1 允许增加编制入职 ps是否自动生成编制调整单 2 超编入职 3 不允许入职", defvalue = "2", datetype = Types.INTEGER)
	public CField quota_over_rst; // 超编审批结果 1 允许增加编制入职 ps是否自动生成编制调整单 2 超编入职 3 不允许入职
	@CFieldinfo(fieldname = "orgid", precision = 20, scale = 0, caption = "入职机构ID", datetype = Types.INTEGER)
	public CField orgid; // 入职机构ID
	@CFieldinfo(fieldname = "orghrlev", notnull = true, precision = 1, scale = 0, caption = "机构人事层级", defvalue = "0", datetype = Types.INTEGER)
	public CField orghrlev; // 机构人事层级
	@CFieldinfo(fieldname = "ospid", precision = 11, scale = 0, caption = "入职职位", datetype = Types.INTEGER)
	public CField ospid; // 入职职位
	@CFieldinfo(fieldname = "lv_num", precision = 4, scale = 1, caption = "入职职级", datetype = Types.DECIMAL)
	public CField lv_num; // 入职职级
	@CFieldinfo(fieldname = "rectname", precision = 170, scale = 0, caption = "招聘人", datetype = Types.VARCHAR)
	public CField rectname; // 招聘人
	@CFieldinfo(fieldname = "rectcode", precision = 42, scale = 0, caption = "招聘人工号", datetype = Types.VARCHAR)
	public CField rectcode; // 招聘人工号
	@CFieldinfo(fieldname = "quachk", precision = 1, scale = 0, caption = "资格比对 合格不合格", datetype = Types.INTEGER)
	public CField quachk; // 资格比对 合格不合格
	@CFieldinfo(fieldname = "quachknote", precision = 85, scale = 0, caption = "资格比对备注", datetype = Types.VARCHAR)
	public CField quachknote; // 资格比对备注
	@CFieldinfo(fieldname = "iview", precision = 1, scale = 0, caption = "面试 合格不合格", datetype = Types.INTEGER)
	public CField iview; // 面试 合格不合格
	@CFieldinfo(fieldname = "iviewnote", precision = 85, scale = 0, caption = "面试 备注", datetype = Types.VARCHAR)
	public CField iviewnote; // 面试 备注
	@CFieldinfo(fieldname = "forunit", precision = 85, scale = 0, caption = "应聘单位", datetype = Types.VARCHAR)
	public CField forunit; // 应聘单位
	@CFieldinfo(fieldname = "forunitnote", precision = 85, scale = 0, caption = "应聘单位备注", datetype = Types.VARCHAR)
	public CField forunitnote; // 应聘单位备注
	@CFieldinfo(fieldname = "cercheck", precision = 1, scale = 0, caption = "证件检查 合格 不合格", datetype = Types.INTEGER)
	public CField cercheck; // 证件检查 合格 不合格
	@CFieldinfo(fieldname = "cerchecknote", precision = 85, scale = 0, caption = "证件检查备注", datetype = Types.VARCHAR)
	public CField cerchecknote; // 证件检查备注
	@CFieldinfo(fieldname = "wtexam", precision = 1, scale = 0, caption = "笔试 合格不合格", datetype = Types.INTEGER)
	public CField wtexam; // 笔试 合格不合格
	@CFieldinfo(fieldname = "wtexamnote", precision = 85, scale = 0, caption = "笔试备注", datetype = Types.VARCHAR)
	public CField wtexamnote; // 笔试备注
	@CFieldinfo(fieldname = "transorg", precision = 85, scale = 0, caption = "输送机构", datetype = Types.VARCHAR)
	public CField transorg; // 输送机构
	@CFieldinfo(fieldname = "transorgnote", precision = 85, scale = 0, caption = "输送机构备注", datetype = Types.VARCHAR)
	public CField transorgnote; // 输送机构备注
	@CFieldinfo(fieldname = "formchk", precision = 1, scale = 0, caption = "形体检查 合格不合格", datetype = Types.INTEGER)
	public CField formchk; // 形体检查 合格不合格
	@CFieldinfo(fieldname = "formchknote", precision = 85, scale = 0, caption = "形体检查备注", datetype = Types.VARCHAR)
	public CField formchknote; // 形体检查备注
	@CFieldinfo(fieldname = "train", precision = 1, scale = 0, caption = "培训合格不合格", datetype = Types.INTEGER)
	public CField train; // 培训合格不合格
	@CFieldinfo(fieldname = "trainnote", precision = 85, scale = 0, caption = "培训备注", datetype = Types.VARCHAR)
	public CField trainnote; // 培训备注
	@CFieldinfo(fieldname = "reportfor", precision = 1, scale = 0, caption = "报到情况", datetype = Types.INTEGER)
	public CField reportfor; // 报到情况
	@CFieldinfo(fieldname = "reportfornote", precision = 64, scale = 0, caption = "报到情况备注", datetype = Types.VARCHAR)
	public CField reportfornote; // 报到情况备注
	@CFieldinfo(fieldname = "transextime", precision = 42, scale = 0, caption = "输送期限", datetype = Types.VARCHAR)
	public CField transextime;// 输送期限
	@CFieldinfo(fieldname = "transextimenote", precision = 85, scale = 0, caption = "输送期限备注", datetype = Types.VARCHAR)
	public CField transextimenote; // 输送期限备注
	@CFieldinfo(fieldname = "tetype", precision = 1, scale = 0, caption = "人员类型 劳务工、临时工、正式工", datetype = Types.INTEGER)
	public CField tetype; // 人员类型 劳务工、临时工、正式工
	@CFieldinfo(fieldname = "tetypenote", precision = 85, scale = 0, caption = "人员类型备注", datetype = Types.VARCHAR)
	public CField tetypenote; // 人员类型备注
	@CFieldinfo(fieldname = "meexam", precision = 1, scale = 0, caption = "体检 合格不合格", datetype = Types.INTEGER)
	public CField meexam; // 体检 合格不合格
	@CFieldinfo(fieldname = "meexamnote", precision = 85, scale = 0, caption = "体检备注", datetype = Types.VARCHAR)
	public CField meexamnote; // 体检备注
	@CFieldinfo(fieldname = "dispunit", precision = 85, scale = 0, caption = "派遣机构", datetype = Types.VARCHAR)
	public CField dispunit; // 派遣机构
	@CFieldinfo(fieldname = "dispunitnote", precision = 85, scale = 0, caption = "派遣机构说明", datetype = Types.VARCHAR)
	public CField dispunitnote; // 派遣机构说明
	@CFieldinfo(fieldname = "ovtype", precision = 1, scale = 0, caption = "加班类型 有  无", datetype = Types.INTEGER)
	public CField ovtype; // 加班类型 有 无
	@CFieldinfo(fieldname = "ovtypenote", precision = 85, scale = 0, caption = "加班类型说明", datetype = Types.VARCHAR)
	public CField ovtypenote; // 加班类型说明
	@CFieldinfo(fieldname = "rcenl", precision = 85, scale = 0, caption = "招聘渠道", datetype = Types.VARCHAR)
	public CField rcenl; // 招聘渠道
	@CFieldinfo(fieldname = "rcenlnote", precision = 85, scale = 0, caption = "招聘渠道说明", datetype = Types.VARCHAR)
	public CField rcenlnote; // 招聘渠道说明
	@CFieldinfo(fieldname = "dispeextime", precision = 85, scale = 0, caption = "派遣期限", datetype = Types.VARCHAR)
	public CField dispeextime; // 派遣期限
	@CFieldinfo(fieldname = "dispeextimenote", precision = 85, scale = 0, caption = "派遣期限说明", datetype = Types.VARCHAR)
	public CField dispeextimenote; // 派遣期限说明
	@CFieldinfo(fieldname = "chkok", precision = 1, scale = 0, caption = "验证合格", datetype = Types.INTEGER)
	public CField chkok; // 验证合格
	@CFieldinfo(fieldname = "chkigmsg", precision = 85, scale = 0, caption = "验证不合格原因", datetype = Types.VARCHAR)
	public CField chkigmsg; // 验证不合格原因
	@CFieldinfo(fieldname = "salarydate", precision = 10, scale = 0, caption = "核薪生效日期", datetype = Types.DATE)
	public CField salarydate; // 核薪生效日期
	@CFieldinfo(fieldname = "recruit_quachk_id", precision = 10, scale = 0, caption = "资格对比ID", datetype = Types.INTEGER)
	public CField recruit_quachk_id; // 资格对比ID
	@CFieldinfo(fieldname = "remark", precision = 682, scale = 0, caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "wfid", precision = 20, scale = 0, caption = "wfid", datetype = Types.INTEGER)
	public CField wfid; // wfid
	@CFieldinfo(fieldname = "attid", precision = 20, scale = 0, caption = "attid", datetype = Types.INTEGER)
	public CField attid; // attid
	@CFieldinfo(fieldname = "stat", notnull = true, precision = 2, scale = 0, caption = "流程状态", datetype = Types.INTEGER)
	public CField stat; // 流程状态
	@CFieldinfo(fieldname = "idpath", notnull = true, precision = 341, scale = 0, caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	@CFieldinfo(fieldname = "entid", notnull = true, precision = 5, scale = 0, caption = "entid", datetype = Types.INTEGER)
	public CField entid; // entid
	@CFieldinfo(fieldname = "creator", notnull = true, precision = 32, scale = 0, caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", notnull = true, precision = 19, scale = 0, caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", precision = 32, scale = 0, caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", precision = 19, scale = 0, caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	@CFieldinfo(fieldname = "attribute1", precision = 42, scale = 0, caption = "备用字段1", datetype = Types.VARCHAR)
	public CField attribute1; // 备用字段1
	@CFieldinfo(fieldname = "attribute2", precision = 42, scale = 0, caption = "备用字段2", datetype = Types.VARCHAR)
	public CField attribute2; // 备用字段2
	@CFieldinfo(fieldname = "attribute3", precision = 42, scale = 0, caption = "备用字段3", datetype = Types.VARCHAR)
	public CField attribute3; // 备用字段3
	@CFieldinfo(fieldname = "attribute4", precision = 42, scale = 0, caption = "备用字段4", datetype = Types.VARCHAR)
	public CField attribute4; // 备用字段4
	@CFieldinfo(fieldname = "attribute5", precision = 42, scale = 0, caption = "备用字段5", datetype = Types.VARCHAR)
	public CField attribute5; // 备用字段5
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hr_recruit_form() throws Exception {
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
