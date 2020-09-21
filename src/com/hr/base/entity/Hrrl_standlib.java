package com.hr.base.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hrrl_standlib extends CJPA {
	@CFieldinfo(fieldname = "slid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField slid; // ID
	@CFieldinfo(fieldname = "lscode", codeid = 103, notnull = true, caption = "编码", datetype = Types.VARCHAR)
	public CField lscode; // 编码
	@CFieldinfo(fieldname = "rlname", notnull = true, caption = "名称", datetype = Types.VARCHAR)
	public CField rlname; // 名称
	@CFieldinfo(fieldname = "rllabel_b", notnull = true, caption = "称谓", datetype = Types.VARCHAR)
	public CField rllabel_b; // 称谓
	@CFieldinfo(fieldname = "rllabel_a", notnull = true, caption = "自称", datetype = Types.VARCHAR)
	public CField rllabel_a; // 自称
	@CFieldinfo(fieldname = "rltype1", notnull = true, caption = "类型", datetype = Types.VARCHAR)
	public CField rltype1; // 类型
	@CFieldinfo(fieldname = "rltype2", notnull = true, caption = "类别", datetype = Types.VARCHAR)
	public CField rltype2; // 类别
	@CFieldinfo(fieldname = "hrvlevel", notnull = true, caption = "分级", datetype = Types.INTEGER)
	public CField hrvlevel; // 分级
	@CFieldinfo(fieldname = "rlext", notnull = true, caption = "说明", datetype = Types.VARCHAR)
	public CField rlext; // 说明
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "usable", caption = "有效状态", defvalue = "1", datetype = Types.INTEGER)
	public CField usable; // 有效状态
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
	@CFieldinfo(fieldname = "attribute5", caption = "备用字段5", datetype = Types.VARCHAR)
	public CField attribute5; // 备用字段5
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hrrl_standlib() throws Exception {
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
