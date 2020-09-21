package com.hr.base.entity;

import java.sql.Types;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

@CEntity()
public class Hr_orgpositionkpi extends CJPA {
	@CFieldinfo(fieldname = "opkpiid", iskey = true, notnull = true, caption = "职位KPI ID", datetype = Types.INTEGER)
	public CField opkpiid; // 职位KPI ID
	@CFieldinfo(fieldname = "ospid", notnull = true, caption = "职位ID", datetype = Types.INTEGER)
	public CField ospid; // 职位ID
	@CFieldinfo(fieldname = "kpiname", notnull = true, caption = "名称", datetype = Types.VARCHAR)
	public CField kpiname; // 名称
	@CFieldinfo(fieldname = "kpiaim", caption = "目的", datetype = Types.VARCHAR)
	public CField kpiaim; // 目的
	@CFieldinfo(fieldname = "kpidefin", caption = "定义", datetype = Types.VARCHAR)
	public CField kpidefin; // 定义
	@CFieldinfo(fieldname = "calcfmt", caption = "计算公式", datetype = Types.VARCHAR)
	public CField calcfmt; // 计算公式
	@CFieldinfo(fieldname = "specnote", caption = "特殊说明", datetype = Types.VARCHAR)
	public CField specnote; // 特殊说明
	@CFieldinfo(fieldname = "examorg", caption = "考核部门", datetype = Types.VARCHAR)
	public CField examorg; // 考核部门
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	
	
	
	public Hr_orgpositionkpi() throws Exception {
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
