package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hr_employee_phexa extends CJPA {
	@CFieldinfo(fieldname = "emppe_id", iskey = true, notnull = true, caption = "检查记录ID", datetype = Types.INTEGER)
	public CField emppe_id; // 检查记录ID
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "员工档案ID", datetype = Types.INTEGER)
	public CField er_id; // 员工档案ID
	@CFieldinfo(fieldname = "examtime", notnull = true, caption = "检查时间", datetype = Types.TIMESTAMP)
	public CField examtime; // 检查时间
	@CFieldinfo(fieldname = "exahosptal", notnull = true, caption = "医院", datetype = Types.VARCHAR)
	public CField exahosptal; // 医院
	@CFieldinfo(fieldname = "examenu", notnull = true, caption = "检查项目", datetype = Types.VARCHAR)
	public CField examenu; // 检查项目
	@CFieldinfo(fieldname = "exarst", notnull = true, caption = "结果", datetype = Types.VARCHAR)
	public CField exarst; // 结果
	@CFieldinfo(fieldname = "picture_id", caption = "图片ID", datetype = Types.INTEGER)
	public CField picture_id; // 图片ID
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
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

	public Hr_employee_phexa() throws Exception {
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
