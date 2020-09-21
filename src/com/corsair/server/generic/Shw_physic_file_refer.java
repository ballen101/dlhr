package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shw_physic_file_refer extends CJPA {
	@CFieldinfo(fieldname = "pfrid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField pfrid; // ID
	@CFieldinfo(fieldname = "pfid", notnull = true, caption = "物理文件ID", datetype = Types.INTEGER)
	public CField pfid; // 物理文件ID
	@CFieldinfo(fieldname = "referid", caption = "引用对象ID", datetype = Types.INTEGER)
	public CField referid; // 引用对象ID
	@CFieldinfo(fieldname = "pfrtype", caption = "引用类型 1：文件夹 2：附件 3：审批流程", datetype = Types.INTEGER)
	public CField pfrtype; // 引用类型 1：文件夹 2：附件 3：审批流程
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shw_physic_file_refer() throws Exception {
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
