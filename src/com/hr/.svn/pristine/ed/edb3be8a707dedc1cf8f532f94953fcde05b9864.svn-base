package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Hr_systemparms extends CJPA {
	public Hr_systemparms() throws Exception {
	}

	public Hr_systemparms(String sqlstr) throws Exception {
		super(sqlstr);
	}

	@CFieldinfo(fieldname = "hrspid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField hrspid; // ID
	@CFieldinfo(fieldname = "parmid", notnull = true, caption = "参数ID", datetype = Types.INTEGER)
	public CField parmid; // 参数ID
	@CFieldinfo(fieldname = "parmcode", notnull = true, caption = "参数编码", datetype = Types.VARCHAR)
	public CField parmcode; // 参数编码
	@CFieldinfo(fieldname = "description", notnull = true, caption = "参数说明", datetype = Types.VARCHAR)
	public CField description; // 参数说明
	@CFieldinfo(fieldname = "parmvalue", notnull = true, caption = "参数值", datetype = Types.INTEGER)
	public CField parmvalue; // 参数值
	@CFieldinfo(fieldname = "usable", caption = "可用", datetype = Types.INTEGER)
	public CField usable; // 可用
	@CFieldinfo(fieldname = "creator", caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	@CFieldinfo(fieldname = "note", caption = "备注", datetype = Types.VARCHAR)
	public CField note; // 备注
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
