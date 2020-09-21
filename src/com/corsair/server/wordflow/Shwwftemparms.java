package com.corsair.server.wordflow;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shwwftemparms extends CJPA {
	@CFieldinfo(fieldname = "wfpid", iskey = true, notnull = true, caption = "流程模板参数ID", datetype = Types.INTEGER)
	public CField wfpid; // 流程模板参数ID
	@CFieldinfo(fieldname = "wftempid", caption = "流程模板ID", datetype = Types.INTEGER)
	public CField wftempid; // 流程模板ID
	@CFieldinfo(fieldname = "frmclassname", caption = "窗体类名", datetype = Types.VARCHAR)
	public CField frmclassname; // 窗体类名
	@CFieldinfo(fieldname = "frmcaption", caption = "窗体标题", datetype = Types.VARCHAR)
	public CField frmcaption; // 窗体标题
	@CFieldinfo(fieldname = "parmname", caption = "参数名", datetype = Types.VARCHAR)
	public CField parmname; // 参数名
	@CFieldinfo(fieldname = "reloper", caption = "运算方式", datetype = Types.VARCHAR)
	public CField reloper; // 运算方式
	@CFieldinfo(fieldname = "rowreloper", caption = "行运算关系", datetype = Types.VARCHAR)
	public CField rowreloper; // 行运算关系
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
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwwftemparms() throws Exception {
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