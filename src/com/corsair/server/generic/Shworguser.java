package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shworguser extends CJPA {
	@CFieldinfo(fieldname = "orgid", iskey = true, notnull = true, caption = "", datetype = Types.INTEGER)
	public CField orgid; //
	@CFieldinfo(fieldname = "isdefault", caption = "", datetype = Types.INTEGER)
	public CField isdefault; //
	@CFieldinfo(fieldname = "inheritrole", caption = "", datetype = Types.INTEGER)
	public CField inheritrole; //
	@CFieldinfo(fieldname = "userid", iskey = true, notnull = true, caption = "", datetype = Types.INTEGER)
	public CField userid; //
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shworguser() throws Exception {
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