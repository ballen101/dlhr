package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.perm.ctr.CtrHr_train_reg;

import java.sql.Types;

@CEntity(controller = CtrHr_train_reg.class)
public class Hr_train_reg extends CJPA {
	@CFieldinfo(fieldname = "treg_id", iskey = true, notnull = true, caption = "登记", datetype = Types.INTEGER)
	public CField treg_id; // 登记
	@CFieldinfo(fieldname = "treg_code", codeid = 68, notnull = true, caption = "登记编码", datetype = Types.VARCHAR)
	public CField treg_code; // 登记编码
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "人事档案ID", datetype = Types.INTEGER)
	public CField er_id; // 人事档案ID
	@CFieldinfo(fieldname = "employee_code", notnull = true, caption = "工号", datetype = Types.VARCHAR)
	public CField employee_code; // 工号
	@CFieldinfo(fieldname = "regtype", notnull = true, caption = "登记类型", datetype = Types.INTEGER)
	public CField regtype; // 登记类型 1:实习生 2：毕业生 实习生登记审批后 为实习状态 毕业生登记审批通过后为 实习试用状态
	@CFieldinfo(fieldname = "regdate", caption = "登记日期", datetype = Types.TIMESTAMP)
	public CField regdate; // 登记日期
	@CFieldinfo(fieldname = "enddatetrain", caption = "实习到期日期", datetype = Types.TIMESTAMP)
	public CField enddatetrain; // 实习到期日期
	@CFieldinfo(fieldname = "enddatetry", caption = "试用到期日期", datetype = Types.TIMESTAMP)
	public CField enddatetry; // 试用到期日期
	@CFieldinfo(fieldname = "jxdatetry", caption = "见习到期日期", datetype = Types.TIMESTAMP)
	public CField jxdatetry; // 见习到期日期
	@CFieldinfo(fieldname = "isfrainflish", caption = "是否完成实习", datetype = Types.INTEGER)
	public CField isfrainflish; // 是否完成实习
	@CFieldinfo(fieldname = "orgid", caption = "编制机构ID", datetype = Types.INTEGER)
	public CField orgid; // 编制机构ID
	@CFieldinfo(fieldname = "ospid", precision = 10, scale = 0, caption = "入职职位", datetype = Types.INTEGER)
	public CField ospid; // 入职职位
	@CFieldinfo(fieldname = "lv_num", precision = 4, scale = 1, caption = "入职职级", datetype = Types.DECIMAL)
	public CField lv_num; // 入职职级
	@CFieldinfo(fieldname = "norgid", caption = "拟用人部门ID", datetype = Types.INTEGER)
	public CField norgid; // 拟用人部门ID
	@CFieldinfo(fieldname = "norgname", caption = "拟用人部门名称", datetype = Types.VARCHAR)
	public CField norgname; // 拟用人部门名称
	@CFieldinfo(fieldname = "nospid", caption = "拟职位ID", datetype = Types.INTEGER)
	public CField nospid; // 拟职位ID
	@CFieldinfo(fieldname = "nsp_name", caption = "拟职位名称", datetype = Types.VARCHAR)
	public CField nsp_name; // 拟职位名称
	@CFieldinfo(fieldname = "nlv_num", precision = 4, scale = 1, caption = "拟职级", datetype = Types.DECIMAL)
	public CField nlv_num; // 拟职级
	@CFieldinfo(fieldname = "rectname", caption = "招聘人", datetype = Types.VARCHAR)
	public CField rectname; // 招聘人
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "newstru_id", precision = 10, scale = 0, caption = "工资结构ID", datetype = Types.INTEGER)
	public CField newstru_id; // 工资结构ID
	@CFieldinfo(fieldname = "newstru_name", precision = 32, scale = 0, caption = "工资结构名", datetype = Types.VARCHAR)
	public CField newstru_name; // 工资结构名
	@CFieldinfo(fieldname = "newposition_salary", precision = 10, scale = 2, caption = "职位工资", datetype = Types.DECIMAL)
	public CField newposition_salary; // 职位工资
	@CFieldinfo(fieldname = "newbase_salary", precision = 10, scale = 2, caption = "基本工资", datetype = Types.DECIMAL)
	public CField newbase_salary; // 基本工资
	@CFieldinfo(fieldname = "newtech_salary", precision = 10, scale = 2, caption = "技能工资", datetype = Types.DECIMAL)
	public CField newtech_salary; // 技能工资
	@CFieldinfo(fieldname = "newachi_salary", precision = 10, scale = 2, caption = "绩效工资", datetype = Types.DECIMAL)
	public CField newachi_salary; // 绩效工资
	@CFieldinfo(fieldname = "newotwage", precision = 10, scale = 2, caption = "固定加班工资", datetype = Types.DECIMAL)
	public CField newotwage; // 固定加班工资
	@CFieldinfo(fieldname = "newtech_allowance", precision = 10, scale = 2, caption = "技术津贴", datetype = Types.DECIMAL)
	public CField newtech_allowance; // 技术津贴
	@CFieldinfo(fieldname = "newpostsubs", precision = 10, scale = 2, caption = "岗位津贴", datetype = Types.DECIMAL)
	public CField newpostsubs; // 岗位津贴
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
	public CField attribute1; // 备用字段1
	@CFieldinfo(fieldname = "attribute2", caption = "备用字段2", datetype = Types.VARCHAR)
	public CField attribute2; // 备用字段2
	@CFieldinfo(fieldname = "attribute3", caption = "备用字段3", datetype = Types.VARCHAR)
	public CField attribute3; // 备用字段3
	@CFieldinfo(fieldname = "attribute4", caption = "备用字段4", datetype = Types.VARCHAR)
	public CField attribute4; // 备用字段4
	@CFieldinfo(fieldname = "attribute5", caption = "备用字段5", datetype = Types.VARCHAR)
	public CField attribute5; // 备用字段5
	@CFieldinfo(fieldname ="newchecklev",precision=2,scale=0,caption="绩效考核层级",datetype=Types.INTEGER)
	public CField newchecklev;  //调薪后绩效考核层级
	@CFieldinfo(fieldname ="newattendtype",precision=32,scale=0,caption="出勤类别",defvalue="0",datetype=Types.VARCHAR)
	public CField newattendtype;  //调薪后出勤类别
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hr_train_reg() throws Exception {
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
