package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shwsystemparms extends CJPA {
	@CFieldinfo(fieldname = "sspid", iskey = true, notnull = true, caption = "", datetype = Types.INTEGER)
	public CField sspid; //
	@CFieldinfo(fieldname = "parmname", caption = "", datetype = Types.VARCHAR)
	public CField parmname; //
	@CFieldinfo(fieldname = "parmvalue", caption = "", datetype = Types.VARCHAR)
	public CField parmvalue; //
	@CFieldinfo(fieldname = "acaption", caption = "", datetype = Types.VARCHAR)
	public CField acaption; //
	@CFieldinfo(fieldname = "edtable", caption = "", datetype = Types.INTEGER)
	public CField edtable; //
	@CFieldinfo(fieldname = "useable", caption = "", datetype = Types.INTEGER)
	public CField useable; //
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwsystemparms() throws Exception {
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
