package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shwmenuco extends CJPA {
	@CFieldinfo(fieldname = "mcid", iskey = true, notnull = true, caption = "menuid", datetype = Types.INTEGER)
	public CField mcid; // menuid
	@CFieldinfo(fieldname = "menuid", notnull = true, caption = "menuid", datetype = Types.INTEGER)
	public CField menuid; // menuid
	@CFieldinfo(fieldname = "coname", notnull = true, caption = "coname", datetype = Types.VARCHAR)
	public CField coname; // coname
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwmenuco() throws Exception {
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