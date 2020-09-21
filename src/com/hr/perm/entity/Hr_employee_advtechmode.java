package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption = "高技模块", tablename = "Hr_employee_advtechmode")
public class Hr_employee_advtechmode extends CJPA {
	@CFieldinfo(fieldname = "atmid", iskey = true, notnull = true, precision = 20, scale = 0, caption = "ID", datetype = Types.INTEGER)
	public CField atmid; // ID
	@CFieldinfo(fieldname = "mdcode", codeid = 140, notnull = true, precision = 16, scale = 0, caption = "编码", datetype = Types.VARCHAR)
	public CField mdcode; // 编码
	@CFieldinfo(fieldname = "mdname", notnull = true, precision = 64, scale = 0, caption = "高技模块名称", datetype = Types.VARCHAR)
	public CField mdname; // 高技模块名称
	@CFieldinfo(fieldname = "disabletime", precision = 19, scale = 0, caption = "失效时间", datetype = Types.TIMESTAMP)
	public CField disabletime; // 失效时间
	@CFieldinfo(fieldname = "usable", notnull = true, precision = 1, scale = 0, caption = "可用", defvalue = "1", datetype = Types.INTEGER)
	public CField usable; // 可用
	@CFieldinfo(fieldname = "creator", notnull = true, precision = 16, scale = 0, caption = "制单人", datetype = Types.VARCHAR)
	public CField creator; // 制单人
	@CFieldinfo(fieldname = "create_time", notnull = true, precision = 19, scale = 0, caption = "制单时间", datetype = Types.TIMESTAMP)
	public CField create_time; // 制单时间
	@CFieldinfo(fieldname = "updator", precision = 16, scale = 0, caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "update_time", precision = 19, scale = 0, caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField update_time; // 更新时间
	@CFieldinfo(fieldname = "mask", precision = 256, scale = 0, caption = "备注", datetype = Types.VARCHAR)
	public CField mask; // 备注
	@CFieldinfo(fieldname = "attr1", precision = 32, scale = 0, caption = "扩展1", datetype = Types.VARCHAR)
	public CField attr1; // 扩展1
	@CFieldinfo(fieldname = "attr2", precision = 32, scale = 0, caption = "扩展2", datetype = Types.VARCHAR)
	public CField attr2; // 扩展2
	@CFieldinfo(fieldname = "attr3", precision = 32, scale = 0, caption = "扩展3", datetype = Types.VARCHAR)
	public CField attr3; // 扩展3
	@CFieldinfo(fieldname = "attr4", precision = 32, scale = 0, caption = "扩展4", datetype = Types.VARCHAR)
	public CField attr4; // 扩展4
	@CFieldinfo(fieldname = "attr5", precision = 32, scale = 0, caption = "扩展5", datetype = Types.VARCHAR)
	public CField attr5; // 扩展5
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hr_employee_advtechmode() throws Exception {
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
