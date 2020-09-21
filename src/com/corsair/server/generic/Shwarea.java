package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shwarea extends CJPA {
	@CFieldinfo(fieldname = "areaid", iskey = true, notnull = true, caption = "", datetype = Types.INTEGER)
	public CField areaid; //
	@CFieldinfo(fieldname = "areacode", caption = "", datetype = Types.VARCHAR)
	public CField areacode; //
	@CFieldinfo(fieldname = "areaname", caption = "", datetype = Types.VARCHAR)
	public CField areaname; //
	@CFieldinfo(fieldname = "assistcode", caption = "", datetype = Types.VARCHAR)
	public CField assistcode; //
	@CFieldinfo(fieldname = "telzone", caption = "", datetype = Types.VARCHAR)
	public CField telzone; //
	@CFieldinfo(fieldname = "postcode", caption = "", datetype = Types.VARCHAR)
	public CField postcode; //
	@CFieldinfo(fieldname = "superid", caption = "", datetype = Types.INTEGER)
	public CField superid; //
	@CFieldinfo(fieldname = "carmark", caption = "", datetype = Types.VARCHAR)
	public CField carmark; //
	@CFieldinfo(fieldname = "note", caption = "", datetype = Types.VARCHAR)
	public CField note; //
	@CFieldinfo(fieldname = "lvidx", precision = 1, scale = 0, caption = "lvidx", datetype = Types.INTEGER)
	public CField lvidx; // lvidx
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwarea() throws Exception {
	}

	public boolean InitObject() {// 类初始化调用的方法
		super.InitObject();
		return true;
	}

	public boolean FinalObject() { // 类释放前调用的方法
		super.FinalObject();
		return true;
	}
}