package com.hr.base.entity;

import java.sql.Types;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import com.hr.base.ctr.CtrHr_relative;

@CEntity(controller = CtrHr_relative.class)
public class Hr_relative extends CJPA {
	@CFieldinfo(fieldname = "hrvid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField hrvid; // ID
	@CFieldinfo(fieldname = "hrvcode", codeid = 49, notnull = true, caption = "类型编码", datetype = Types.VARCHAR)
	public CField hrvcode; // 类型编码
	@CFieldinfo(fieldname = "hrvname", notnull = true, caption = "类型名称", datetype = Types.VARCHAR)
	public CField hrvname; // 类型名称
	@CFieldinfo(fieldname = "pid", notnull = true, caption = "上级ID", datetype = Types.INTEGER)
	public CField pid; // 上级ID
	@CFieldinfo(fieldname = "hrvlevel", notnull = true, caption = "分级", datetype = Types.INTEGER)
	public CField hrvlevel; // 分级
	@CFieldinfo(fieldname = "usable", caption = "有效状态", datetype = Types.INTEGER)
	public CField usable; // 有效状态
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "creator", caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", caption = "创建时间", datetype = Types.TIMESTAMP)
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
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hr_relative() throws Exception {
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
