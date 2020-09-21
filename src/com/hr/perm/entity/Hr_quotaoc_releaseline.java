package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hr_quotaoc_releaseline extends CJPA {
	@CFieldinfo(fieldname = "qocrlslineid", iskey = true, notnull = true, caption = "发布单行ID", datetype = Types.INTEGER)
	public CField qocrlslineid; // 发布单行ID
	@CFieldinfo(fieldname = "qocrlsid", notnull = true, caption = "发布单ID", datetype = Types.INTEGER)
	public CField qocrlsid; // 发布单ID
	@CFieldinfo(fieldname = "hwc_idzl", notnull = true, caption = "职类ID", datetype = Types.INTEGER)
	public CField hwc_idzl; // 职类ID
	@CFieldinfo(fieldname = "hw_codezl", notnull = true, caption = "职类代码", datetype = Types.VARCHAR)
	public CField hw_codezl; // 职类代码
	@CFieldinfo(fieldname = "hwc_namezl", caption = "职类名称", datetype = Types.VARCHAR)
	public CField hwc_namezl; // 职类名称
	@CFieldinfo(fieldname = "orgid", notnull = true, caption = "机构ID", datetype = Types.INTEGER)
	public CField orgid; // 机构ID
	@CFieldinfo(fieldname = "orgname", notnull = true, caption = "机构名称", datetype = Types.VARCHAR)
	public CField orgname; // 机构名称
	@CFieldinfo(fieldname = "orgcode", caption = "机构编码", datetype = Types.VARCHAR)
	public CField orgcode; // 机构编码
	@CFieldinfo(fieldname = "quota", notnull = true, caption = "编制数量", datetype = Types.INTEGER)
	public CField quota; // 编制数量
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

	public Hr_quotaoc_releaseline() throws Exception {
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
