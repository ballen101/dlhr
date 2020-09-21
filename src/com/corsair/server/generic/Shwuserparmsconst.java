package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Shwuserparmsconst extends CJPA {
	@CFieldinfo(fieldname = "cstparmid", caption = "cstparmid", datetype = Types.DECIMAL)
	public CField cstparmid; // cstparmid
	@CFieldinfo(fieldname = "parmname", caption = "parmname", datetype = Types.VARCHAR)
	public CField parmname; // parmname
	@CFieldinfo(fieldname = "defaultvalue", caption = "defaultvalue", datetype = Types.VARCHAR)
	public CField defaultvalue; // defaultvalue
	@CFieldinfo(fieldname = "language1", caption = "language1", datetype = Types.VARCHAR)
	public CField language1; // language1
	@CFieldinfo(fieldname = "language2", caption = "language2", datetype = Types.VARCHAR)
	public CField language2; // language2
	@CFieldinfo(fieldname = "language3", caption = "language3", datetype = Types.VARCHAR)
	public CField language3; // language3
	@CFieldinfo(fieldname = "notelanguage1", caption = "notelanguage1", datetype = Types.VARCHAR)
	public CField notelanguage1; // notelanguage1
	@CFieldinfo(fieldname = "notelanguage2", caption = "notelanguage2", datetype = Types.VARCHAR)
	public CField notelanguage2; // notelanguage2
	@CFieldinfo(fieldname = "notelanguage3", caption = "notelanguage3", datetype = Types.VARCHAR)
	public CField notelanguage3; // notelanguage3
	@CFieldinfo(fieldname = "action", caption = "action", datetype = Types.VARCHAR)
	public CField action; // action
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwuserparmsconst() throws Exception {
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
