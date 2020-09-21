package com.corsair.server.wordflow;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shwwftempproccondition extends CJPA {
	@CFieldinfo(fieldname = "wftempid", notnull = true, caption = "节点参数ID", datetype = Types.INTEGER)
	public CField wftempid; // 节点参数ID
	@CFieldinfo(fieldname = "proctempid", notnull = true, caption = "节点ID", datetype = Types.INTEGER)
	public CField proctempid; // 节点ID
	@CFieldinfo(fieldname = "wftempprocconditionid", iskey = true, notnull = true, caption = "wftempprocconditionid", datetype = Types.INTEGER)
	public CField wftempprocconditionid; // wftempprocconditionid
	@CFieldinfo(fieldname = "frmclassname", caption = "frmclassname", datetype = Types.VARCHAR)
	public CField frmclassname; // frmclassname
	@CFieldinfo(fieldname = "frmcaption", caption = "frmcaption", datetype = Types.VARCHAR)
	public CField frmcaption; // frmcaption
	@CFieldinfo(fieldname = "parmname", caption = "parmname", datetype = Types.VARCHAR)
	public CField parmname; // parmname
	@CFieldinfo(fieldname = "reloper", caption = "reloper", datetype = Types.VARCHAR)
	public CField reloper; // reloper
	@CFieldinfo(fieldname = "parmvalue", caption = "parmvalue", datetype = Types.VARCHAR)
	public CField parmvalue; // parmvalue
	@CFieldinfo(fieldname = "note", caption = "note", datetype = Types.VARCHAR)
	public CField note; // note
	@CFieldinfo(fieldname = "parm1", caption = "parm1", datetype = Types.VARCHAR)
	public CField parm1; // parm1
	@CFieldinfo(fieldname = "parm2", caption = "parm2", datetype = Types.VARCHAR)
	public CField parm2; // parm2
	@CFieldinfo(fieldname = "parm3", caption = "parm3", datetype = Types.VARCHAR)
	public CField parm3; // parm3
	@CFieldinfo(fieldname = "allowedit", caption = "allowedit", datetype = Types.INTEGER)
	public CField allowedit; // allowedit
	@CFieldinfo(fieldname = "visible", caption = "visible", datetype = Types.INTEGER)
	public CField visible; // visible
	@CFieldinfo(fieldname = "pdidx", caption = "序号", datetype = Types.INTEGER)
	public CField pdidx; // 序号
	@CFieldinfo(fieldname = "ppdidx", caption = "前置序号", datetype = Types.INTEGER)
	public CField ppdidx; // 前置序号
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwwftempproccondition() throws Exception {
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