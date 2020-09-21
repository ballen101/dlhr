package com.hr.perm.entity;

import java.sql.Types;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

@CEntity()
public class Hr_quota_adjustline extends CJPA {
	@CFieldinfo(fieldname = "qadjlineid", iskey = true, notnull = true, caption = "调整单行ID", datetype = Types.INTEGER)
	public CField qadjlineid; // 调整单行ID
	@CFieldinfo(fieldname = "qadjid", notnull = true, caption = "调整单ID", datetype = Types.INTEGER)
	public CField qadjid; // 调整单ID
	@CFieldinfo(fieldname = "ospid", notnull = true, caption = "职位ID", datetype = Types.INTEGER)
	public CField ospid; // 职位ID
	@CFieldinfo(fieldname = "ospcode", notnull = true, caption = "职位编码", datetype = Types.VARCHAR)
	public CField ospcode; // 职位编码
	@CFieldinfo(fieldname = "sp_name", notnull = true, caption = "职位名称", datetype = Types.VARCHAR)
	public CField sp_name; // 职位名称
	@CFieldinfo(fieldname = "orgid", notnull = true, caption = "机构ID", datetype = Types.INTEGER)
	public CField orgid; // 机构ID
	@CFieldinfo(fieldname = "orgname", notnull = true, caption = "机构名称", datetype = Types.VARCHAR)
	public CField orgname; // 机构名称
	@CFieldinfo(fieldname = "orgcode", notnull = true, caption = "机构编码", datetype = Types.VARCHAR)
	public CField orgcode; // 机构编码
	@CFieldinfo(fieldname = "oldquota", notnull = true, caption = "现有编制", defvalue = "0", datetype = Types.INTEGER)
	public CField oldquota; // 现有编制
	@CFieldinfo(fieldname = "adjtype", notnull = true, caption = "增减类型", datetype = Types.INTEGER)
	public CField adjtype; // 增减类型
	@CFieldinfo(fieldname = "adjquota", notnull = true, caption = "调整数量", datetype = Types.INTEGER)
	public CField adjquota; // 调整数量
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
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hr_quota_adjustline() throws Exception {
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
