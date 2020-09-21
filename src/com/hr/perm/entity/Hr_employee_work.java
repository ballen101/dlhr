package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hr_employee_work extends CJPA {
	@CFieldinfo(fieldname = "empl_id", iskey = true, notnull = true, caption = "履历表ID", datetype = Types.INTEGER)
	public CField empl_id; // 履历表ID
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "员工档案ID", datetype = Types.INTEGER)
	public CField er_id; // 员工档案ID
	@CFieldinfo(fieldname = "company", notnull = false, caption = "工作单位", datetype = Types.VARCHAR)
	public CField company; // 工作单位
	@CFieldinfo(fieldname = "start_date", notnull = false, caption = "起始日期", datetype = Types.TIMESTAMP)
	public CField start_date; // 起始日期
	@CFieldinfo(fieldname = "end_date", notnull = false, caption = "结束日期", datetype = Types.TIMESTAMP)
	public CField end_date; // 结束日期
	@CFieldinfo(fieldname = "func", caption = "职务", datetype = Types.VARCHAR)
	public CField func; // 职务
	@CFieldinfo(fieldname = "work_type", caption = "工种", datetype = Types.VARCHAR)
	public CField work_type; // 工种
	@CFieldinfo(fieldname = "jobexp", caption = "工作内容", datetype = Types.VARCHAR)
	public CField jobexp; // 工作内容
	@CFieldinfo(fieldname = "endsarary", caption = "离职薪资", datetype = Types.DECIMAL)
	public CField endsarary; // 离职薪资
	@CFieldinfo(fieldname = "position_desc", notnull = false, caption = "任职岗位", datetype = Types.VARCHAR)
	public CField position_desc; // 任职岗位
	@CFieldinfo(fieldname = "witness", caption = "证明人", datetype = Types.VARCHAR)
	public CField witness; // 证明人
	@CFieldinfo(fieldname = "witness_tel", caption = "联系电话", datetype = Types.VARCHAR)
	public CField witness_tel; // 联系电话
	@CFieldinfo(fieldname = "is_group_experience", caption = "是否集团经历", datetype = Types.INTEGER)
	public CField is_group_experience; // 是否集团经历
	@CFieldinfo(fieldname = "is_vocation_experience", caption = "是否行业经历", datetype = Types.INTEGER)
	public CField is_vocation_experience; // 是否行业经历
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "creator", notnull = false, caption = "制单人", datetype = Types.VARCHAR)
	public CField creator; // 制单人
	@CFieldinfo(fieldname = "createtime", notnull = false, caption = "制单时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 制单时间
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
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hr_employee_work() throws Exception {
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
