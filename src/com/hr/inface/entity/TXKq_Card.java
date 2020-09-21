package com.hr.inface.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(dbpool = "oldtxmssql", tablename = "Kq_Card")
public class TXKq_Card extends CJPA {
	@CFieldinfo(fieldname = "ID", notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField ID; // ID
	@CFieldinfo(fieldname = "CardNo", notnull = true, caption = "CardNo", datetype = Types.CHAR)
	public CField CardNo; // CardNo
	@CFieldinfo(fieldname = "EmpID", notnull = true, caption = "EmpID", datetype = Types.INTEGER)
	public CField EmpID; // EmpID
	@CFieldinfo(fieldname = "Date0", notnull = true, caption = "Date0", datetype = Types.TIMESTAMP)
	public CField Date0; // Date0
	@CFieldinfo(fieldname = "Date1", notnull = true, caption = "Date1", datetype = Types.TIMESTAMP)
	public CField Date1; // Date1
	@CFieldinfo(fieldname = "FType", notnull = true, caption = "FType", datetype = Types.TINYINT)
	public CField FType; // FType
	@CFieldinfo(fieldname = "OldCardNo", caption = "OldCardNo", datetype = Types.CHAR)
	public CField OldCardNo; // OldCardNo
	@CFieldinfo(fieldname = "CardNoS", caption = "CardNoS", datetype = Types.CHAR)
	public CField CardNoS; // CardNoS
	@CFieldinfo(fieldname = "Note", caption = "Note", datetype = Types.VARCHAR)
	public CField Note; // Note
	@CFieldinfo(fieldname = "PassWord", caption = "PassWord", datetype = Types.VARCHAR)
	public CField PassWord; // PassWord
	@CFieldinfo(fieldname = "CardCost", caption = "CardCost", datetype = Types.NUMERIC)
	public CField CardCost; // CardCost
	@CFieldinfo(fieldname = "Ifzc", caption = "Ifzc", datetype = Types.BIT)
	public CField Ifzc; // Ifzc
	@CFieldinfo(fieldname = "MachList", caption = "MachList", datetype = Types.VARCHAR)
	public CField MachList; // MachList
	@CFieldinfo(fieldname = "ConIfZc", caption = "ConIfZc", datetype = Types.BIT)
	public CField ConIfZc; // ConIfZc
	@CFieldinfo(fieldname = "ConGroup", caption = "ConGroup", datetype = Types.TINYINT)
	public CField ConGroup; // ConGroup
	@CFieldinfo(fieldname = "FpCode", caption = "FpCode", datetype = Types.VARCHAR)
	public CField FpCode; // FpCode
	@CFieldinfo(fieldname = "PresentMoney", caption = "PresentMoney", datetype = Types.NUMERIC)
	public CField PresentMoney; // PresentMoney
	@CFieldinfo(fieldname = "IncMoney", caption = "IncMoney", datetype = Types.DECIMAL)
	public CField IncMoney; // IncMoney
	@CFieldinfo(fieldname = "IsInc", caption = "IsInc", datetype = Types.BIT)
	public CField IsInc; // IsInc
	@CFieldinfo(fieldname = "Czy", caption = "Czy", datetype = Types.VARCHAR)
	public CField Czy; // Czy
	@CFieldinfo(fieldname = "OperateDate", caption = "OperateDate", datetype = Types.TIMESTAMP)
	public CField OperateDate; // OperateDate
	@CFieldinfo(fieldname = "SetRightFlag", caption = "SetRightFlag", datetype = Types.NCHAR)
	public CField SetRightFlag; // SetRightFlag
	@CFieldinfo(fieldname = "logo1", caption = "logo1", datetype = Types.NCHAR)
	public CField logo1; // logo1
	@CFieldinfo(fieldname = "AttGroup", caption = "AttGroup", datetype = Types.INTEGER)
	public CField AttGroup; // AttGroup
	@CFieldinfo(fieldname = "PhysicsCardNo", caption = "PhysicsCardNo", datetype = Types.VARCHAR)
	public CField PhysicsCardNo; // PhysicsCardNo
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public TXKq_Card() throws Exception {
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
