package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hr_empptjob_info extends CJPA {
	@CFieldinfo(fieldname = "ptjiid", iskey = true, notnull = true, caption = "兼职单ID", datetype = Types.INTEGER)
	public CField ptjiid; // 兼职单ID
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "人事ID", datetype = Types.INTEGER)
	public CField er_id; // 人事ID
	@CFieldinfo(fieldname = "odospid", notnull = true, caption = "主职位ID", datetype = Types.INTEGER)
	public CField odospid; // 主职位ID
	@CFieldinfo(fieldname = "newospid", notnull = true, caption = "兼职职位ID", datetype = Types.INTEGER)
	public CField newospid; // 兼职职位ID
	@CFieldinfo(fieldname = "startdate", notnull = true, caption = "开始时间", datetype = Types.TIMESTAMP)
	public CField startdate; // 开始时间
	@CFieldinfo(fieldname = "enddate", notnull = true, caption = "结束时间", datetype = Types.TIMESTAMP)
	public CField enddate; // 结束时间
	@CFieldinfo(fieldname = "ptjalev", notnull = true, caption = "兼职层级", datetype = Types.INTEGER)
	public CField ptjalev; // 兼职层级
	@CFieldinfo(fieldname = "ptjatype", notnull = true, caption = "兼职范围 1内部调用 2 跨单位 3 跨模块/制造群", datetype = Types.INTEGER)
	public CField ptjatype; // 兼职范围 1内部调用 2 跨单位 3 跨模块/制造群
	@CFieldinfo(fieldname = "issubsidy", caption = "申请补贴", datetype = Types.INTEGER)
	public CField issubsidy; // 申请补贴
	@CFieldinfo(fieldname = "subsidyarm", caption = "月度补贴金额", datetype = Types.DECIMAL)
	public CField subsidyarm; // 月度补贴金额
	@CFieldinfo(fieldname = "sourceid", notnull = true, caption = "申请单ID", datetype = Types.INTEGER)
	public CField sourceid; // 申请单ID
	@CFieldinfo(fieldname = "ptjstat", notnull = true, caption = "兼职状态   兼职中  完成", datetype = Types.INTEGER)
	public CField ptjstat; // 兼职状态 兼职中 完成
	@CFieldinfo(fieldname = "idpath", notnull = true, caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hr_empptjob_info() throws Exception {
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




