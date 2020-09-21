package com.corsair.server.wordflow;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

public class Shwwftemp_choice extends CJPA {
	public CField wftempid; //
	public CField wftempname; //
	public CField parmname; //
	public CField reloper; //
	public CField parmvalue; //
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwwftemp_choice() throws Exception {
	}

	public boolean InitObject() {// 类初始化调用的方法
		super.InitObject();
		wftempid.setIskey(true); //
		wftempid.setNullable(false); //
		parmname.setNullable(false); //
		reloper.setNullable(false); //
		parmvalue.setNullable(false); //
		wftempid.setFieldtype(Types.FLOAT);
		wftempname.setFieldtype(Types.VARCHAR);
		parmname.setFieldtype(Types.VARCHAR);
		reloper.setFieldtype(Types.VARCHAR);
		parmvalue.setFieldtype(Types.VARCHAR);
		return true;
	}

	public boolean FinalObject() { // 类释放前调用的方法
		super.FinalObject();
		return true;
	}
}
