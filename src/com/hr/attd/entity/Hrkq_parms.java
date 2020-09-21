package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(tablename = "Hrkq_parms")
public class Hrkq_parms extends CJPA {
	@CFieldinfo(fieldname = "parmid", iskey = true, notnull = true, precision = 5, scale = 0, caption = "ID", datetype = Types.INTEGER)
	public CField parmid; // ID
	@CFieldinfo(fieldname = "parmcode", precision = 32, scale = 0, caption = "编码", datetype = Types.VARCHAR)
	public CField parmcode; // 编码
	@CFieldinfo(fieldname = "parmname", precision = 64, scale = 0, caption = "名称", datetype = Types.VARCHAR)
	public CField parmname; // 名称
	@CFieldinfo(fieldname = "parmvalue", precision = 128, scale = 0, caption = "参数值", datetype = Types.VARCHAR)
	public CField parmvalue; // 参数值
	@CFieldinfo(fieldname = "canedit", precision = 1, scale = 0, caption = "允许界面修改", defvalue = "1", datetype = Types.INTEGER)
	public CField canedit; // 允许界面修改
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hrkq_parms() throws Exception {
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
