package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hr_employee_leanexp extends CJPA {
	@CFieldinfo(fieldname = "emple_id", iskey = true, notnull = true, caption = "学习经历ID", datetype = Types.INTEGER)
	public CField emple_id; // 学习经历ID
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "员工档案ID", datetype = Types.INTEGER)
	public CField er_id; // 员工档案ID
	@CFieldinfo(fieldname = "begintime", notnull = false, caption = "入学时间", datetype = Types.TIMESTAMP)
	public CField begintime; // 入学时间
	@CFieldinfo(fieldname = "endtime", notnull = false, caption = "毕业时间", datetype = Types.TIMESTAMP)
	public CField endtime; // 毕业时间
	@CFieldinfo(fieldname = "schoolname", notnull = false, caption = "学校名称", datetype = Types.VARCHAR)
	public CField schoolname; // 学校名称
	@CFieldinfo(fieldname = "major", caption = "院系专业", datetype = Types.VARCHAR)
	public CField major; // 院系专业
	@CFieldinfo(fieldname = "degree", notnull = false, caption = "学历", datetype = Types.INTEGER)
	public CField degree; // 学历
	@CFieldinfo(fieldname = "certnum", caption = "学位证编号", datetype = Types.VARCHAR)
	public CField certnum; // 学位证编号
	@CFieldinfo(fieldname = "gcernum", caption = "毕业证编号", datetype = Types.VARCHAR)
	public CField gcernum; // 毕业证编号
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

	public Hr_employee_leanexp() throws Exception {
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
