package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shworg_find extends CJPA {
	@CFieldinfo(fieldname = "forglineid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField forglineid; // ID
	@CFieldinfo(fieldname = "orgid", notnull = true, caption = "主表ID", datetype = Types.INTEGER)
	public CField orgid; // 主表ID
	@CFieldinfo(fieldname = "forgid", notnull = true, caption = "查询机构ID", datetype = Types.INTEGER)
	public CField forgid; // 查询机构ID
	@CFieldinfo(fieldname = "forgname", notnull = true, caption = "查询名称", datetype = Types.VARCHAR)
	public CField forgname; // 查询名称
	@CFieldinfo(fieldname = "fcode", notnull = true, caption = "查询编码", datetype = Types.VARCHAR)
	public CField fcode; // 查询编码
	@CFieldinfo(fieldname = "fidpath", notnull = true, caption = "查询路径", datetype = Types.VARCHAR)
	public CField fidpath; // 查询路径
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shworg_find() throws Exception {
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
