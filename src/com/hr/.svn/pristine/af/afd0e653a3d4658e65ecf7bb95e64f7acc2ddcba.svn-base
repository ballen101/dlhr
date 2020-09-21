package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hrkq_calcline extends CJPA {
	@CFieldinfo(fieldname = "clclid", iskey = true, notnull = true, caption = "行ID", datetype = Types.INTEGER)
	public CField clclid; // 行ID
	@CFieldinfo(fieldname = "clcid", notnull = true, caption = "计算ID", datetype = Types.INTEGER)
	public CField clcid; // 计算ID
	@CFieldinfo(fieldname = "tid", notnull = true, caption = "目标ID", datetype = Types.INTEGER)
	public CField tid; // 目标ID
	@CFieldinfo(fieldname = "tcode", notnull = true, caption = "目标编码", datetype = Types.VARCHAR)
	public CField tcode; // 目标编码
	@CFieldinfo(fieldname = "tname", notnull = true, caption = "目标名称", datetype = Types.VARCHAR)
	public CField tname; // 目标名称
	@CFieldinfo(fieldname = "ttype", notnull = true, caption = "类型:1 班制组 2 部门 3 个人", datetype = Types.INTEGER)
	public CField ttype; // 类型:1 班制组 2 部门 3 个人
	@CFieldinfo(fieldname = "incldchd", notnull = false, caption = "包含子机构", datetype = Types.INTEGER)
	public CField incldchd; // 包含子机构
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hrkq_calcline() throws Exception {
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
