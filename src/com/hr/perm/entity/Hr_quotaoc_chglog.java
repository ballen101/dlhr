package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hr_quotaoc_chglog extends CJPA {
	@CFieldinfo(fieldname = "qocchgid", iskey = true, notnull = true, caption = "id", datetype = Types.INTEGER)
	public CField qocchgid; // id
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
	@CFieldinfo(fieldname = "orgcode", notnull = true, caption = "机构编码", datetype = Types.VARCHAR)
	public CField orgcode; // 机构编码
	@CFieldinfo(fieldname = "sourceid", notnull = true, caption = "原单ID", datetype = Types.INTEGER)
	public CField sourceid; // 原单ID
	@CFieldinfo(fieldname = "sourcelineid", notnull = true, caption = "原单行ID", datetype = Types.INTEGER)
	public CField sourcelineid; // 原单行ID
	@CFieldinfo(fieldname = "sourcecode", notnull = true, caption = "原单编码", datetype = Types.VARCHAR)
	public CField sourcecode; // 原单编码
	@CFieldinfo(fieldname = "sourcetype", notnull = true, caption = "原单类型1 编制发布 2 编制调整 3 编制申请", datetype = Types.INTEGER)
	public CField sourcetype; // 原单类型1 编制发布 2 编制调整 3 编制申请
	@CFieldinfo(fieldname = "oldquota", notnull = true, caption = "oldquota", datetype = Types.INTEGER)
	public CField oldquota; // oldquota
	@CFieldinfo(fieldname = "newquota", notnull = true, caption = "newquota", datetype = Types.INTEGER)
	public CField newquota; // newquota
	@CFieldinfo(fieldname = "idpath", notnull = true, caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hr_quotaoc_chglog() throws Exception {
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
