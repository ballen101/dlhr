package com.hr.base.entity;

import java.sql.Types;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import com.hr.base.ctr.CtrHr_wclass;

@CEntity(controller = CtrHr_wclass.class)
public class Hr_wclass extends CJPA {
	@CFieldinfo(fieldname = "hwc_id", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField hwc_id; // ID
	@CFieldinfo(fieldname = "pid", notnull = true, caption = "上级ID", datetype = Types.INTEGER)
	public CField pid; // 上级ID
	@CFieldinfo(fieldname = "hw_code", codeid = 46, notnull = true, caption = "代码", datetype = Types.VARCHAR)
	public CField hw_code; // 代码
	@CFieldinfo(fieldname = "hwc_name", caption = "名称", datetype = Types.VARCHAR)
	public CField hwc_name; // 名称
	@CFieldinfo(fieldname = "type_id", notnull = true, caption = "类型:职类、职群、职种", datetype = Types.INTEGER)
	public CField type_id; // 类型:职类、职群、职种
	@CFieldinfo(fieldname = "isoffjob", caption = "脱产 职类特有", datetype = Types.INTEGER)
	public CField isoffjob; // 脱产 职类特有
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

	public Hr_wclass() throws Exception {
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
