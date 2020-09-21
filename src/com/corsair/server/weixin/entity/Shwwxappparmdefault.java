package com.corsair.server.weixin.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Shwwxappparmdefault extends CJPA {
	@CFieldinfo(fieldname = "pdid", precision = 20, scale = 0, caption = "pdid", datetype = Types.INTEGER)
	public CField pdid; // pdid
	@CFieldinfo(fieldname = "parmname", precision = 50, scale = 0, caption = "parmname", datetype = Types.VARCHAR)
	public CField parmname; // parmname
	@CFieldinfo(fieldname = "parmvalue", precision = 50, scale = 0, caption = "parmvalue", datetype = Types.VARCHAR)
	public CField parmvalue; // parmvalue
	@CFieldinfo(fieldname = "acaption", precision = 100, scale = 0, caption = "acaption", datetype = Types.VARCHAR)
	public CField acaption; // acaption
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwwxappparmdefault() throws Exception {
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
