package com.hr.perm.entity;

import java.sql.Types;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

@CEntity()
public class Hr_quota_chglog extends CJPA {
	@CFieldinfo(fieldname = "qchgid", iskey = true, notnull = true, caption = "id", datetype = Types.INTEGER)
	public CField qchgid; // id
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
	@CFieldinfo(fieldname = "sourceid", notnull = true, caption = "原单ID", datetype = Types.INTEGER)
	public CField sourceid; // 原单ID
	@CFieldinfo(fieldname = "sourcelineid", notnull = true, caption = "原单行ID", datetype = Types.INTEGER)
	public CField sourcelineid; // 原单行ID
	@CFieldinfo(fieldname = "sourcecode", notnull = true, caption = "原单编码", datetype = Types.VARCHAR)
	public CField sourcecode; // 原单编码
	@CFieldinfo(fieldname = "sourcetype", notnull = true, caption = "原单类型", datetype = Types.INTEGER)
	public CField sourcetype; // 原单类型1 编制发布 2 编制调整 3 编制申请 4调动单 5批量调动单 6入职单
	@CFieldinfo(fieldname = "oldquota", caption = "原编制", datetype = Types.INTEGER)
	public CField oldquota; // 原编制
	@CFieldinfo(fieldname = "newquota", caption = "新编制", datetype = Types.INTEGER)
	public CField newquota; // 新编制
	@CFieldinfo(fieldname = "idpath", notnull = true, caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	@CFieldinfo(fieldname = "creator", caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hr_quota_chglog() throws Exception {
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
