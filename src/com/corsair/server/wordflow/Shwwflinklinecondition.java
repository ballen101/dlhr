package com.corsair.server.wordflow;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shwwflinklinecondition extends CJPA {
	@CFieldinfo(fieldname = "wfllcid", iskey = true, notnull = true, caption = "", datetype = Types.INTEGER)
	public CField wfllcid; //
	@CFieldinfo(fieldname = "wfllid", notnull = true, caption = "", datetype = Types.INTEGER)
	public CField wfllid; //
	@CFieldinfo(fieldname = "frmclassname", caption = "", datetype = Types.VARCHAR)
	public CField frmclassname; //
	@CFieldinfo(fieldname = "frmcaption", caption = "", datetype = Types.VARCHAR)
	public CField frmcaption; //
	@CFieldinfo(fieldname = "parmname", caption = "", datetype = Types.VARCHAR)
	public CField parmname; //
	@CFieldinfo(fieldname = "reloper", caption = "", datetype = Types.VARCHAR)
	public CField reloper; //
	@CFieldinfo(fieldname = "parmvalue", caption = "", datetype = Types.VARCHAR)
	public CField parmvalue; //
	@CFieldinfo(fieldname = "note", caption = "", datetype = Types.VARCHAR)
	public CField note; //
	@CFieldinfo(fieldname = "parm1", caption = "", datetype = Types.VARCHAR)
	public CField parm1; //
	@CFieldinfo(fieldname = "parm2", caption = "", datetype = Types.VARCHAR)
	public CField parm2; //
	@CFieldinfo(fieldname = "parm3", caption = "", datetype = Types.VARCHAR)
	public CField parm3; //
	@CFieldinfo(fieldname = "allowedit", caption = "", datetype = Types.INTEGER)
	public CField allowedit; //
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwwflinklinecondition() throws Exception {
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
