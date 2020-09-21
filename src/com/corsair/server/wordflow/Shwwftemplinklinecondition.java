package com.corsair.server.wordflow;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shwwftemplinklinecondition extends CJPA {
	@CFieldinfo(fieldname = "wftemllcid", iskey = true, notnull = true, caption = "连线分支条件ID", datetype = Types.INTEGER)
	public CField wftemllcid; // 连线分支条件ID
	@CFieldinfo(fieldname = "wftemllid", notnull = true, caption = "连线ID", datetype = Types.INTEGER)
	public CField wftemllid; // 连线ID
	@CFieldinfo(fieldname = "frmclassname", caption = "窗体类名", datetype = Types.VARCHAR)
	public CField frmclassname; // 窗体类名
	@CFieldinfo(fieldname = "frmcaption", caption = "窗体标题", datetype = Types.VARCHAR)
	public CField frmcaption; // 窗体标题
	@CFieldinfo(fieldname = "parmname", caption = "参数名", datetype = Types.VARCHAR)
	public CField parmname; // 参数名
	@CFieldinfo(fieldname = "reloper", caption = "运算符号", datetype = Types.VARCHAR)
	public CField reloper; // 运算符号
	@CFieldinfo(fieldname = "parmvalue", caption = "参数值", datetype = Types.VARCHAR)
	public CField parmvalue; // 参数值
	@CFieldinfo(fieldname = "note", caption = "备注", datetype = Types.VARCHAR)
	public CField note; // 备注
	@CFieldinfo(fieldname = "parm1", caption = "", datetype = Types.VARCHAR)
	public CField parm1; //
	@CFieldinfo(fieldname = "parm2", caption = "", datetype = Types.VARCHAR)
	public CField parm2; //
	@CFieldinfo(fieldname = "parm3", caption = "", datetype = Types.VARCHAR)
	public CField parm3; //
	@CFieldinfo(fieldname = "allowedit", caption = "1 允许编辑 2 不允许编辑", datetype = Types.INTEGER)
	public CField allowedit; // 1 允许编辑 2 不允许编辑
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwwftemplinklinecondition() throws Exception {
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