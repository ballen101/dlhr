package com.hr.inface.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(dbpool = "oldtxmssql", caption = "工资额度接口", tablename = "dbo.wageSysTohr")
public class WageSysTohr extends CJPA {
	@CFieldinfo(fieldname = "UNID", notnull = true, precision = 50, scale = 0, caption = "UNID", datetype = Types.VARCHAR)
	public CField UNID; // UNID
	@CFieldinfo(fieldname = "DocNo", precision = 20, scale = 0, caption = "单号", datetype = Types.VARCHAR)
	public CField DocNo; // DocNo
	@CFieldinfo(fieldname = "UserID", precision = 10, scale = 0, caption = "UserID", datetype = Types.VARCHAR)
	public CField UserID; // UserID
	@CFieldinfo(fieldname = "UserName", precision = 20, scale = 0, caption = "UserName", datetype = Types.NVARCHAR)
	public CField UserName; // UserName
	@CFieldinfo(fieldname = "UnitName", precision = 100, scale = 0, caption = "UnitName", datetype = Types.NVARCHAR)
	public CField UnitName; // UnitName
	@CFieldinfo(fieldname = "RequestID", precision = 50, scale = 0, caption = "RequestID", datetype = Types.VARCHAR)
	public CField RequestID; // RequestID
	@CFieldinfo(fieldname = "RequestName", precision = 20, scale = 0, caption = "RequestName", datetype = Types.NVARCHAR)
	public CField RequestName; // RequestName
	@CFieldinfo(fieldname = "DeptName", precision = 100, scale = 0, caption = "DeptName", datetype = Types.NVARCHAR)
	public CField DeptName; // DeptName
	@CFieldinfo(fieldname = "Money", precision = 15, scale = 0, caption = "申请额度", datetype = Types.DOUBLE)
	public CField Money; // Money
	@CFieldinfo(fieldname = "Year", precision = 10, scale = 0, caption = "Year", datetype = Types.VARCHAR)
	public CField Year; // Year
	@CFieldinfo(fieldname = "Month", precision = 10, scale = 0, caption = "Month", datetype = Types.VARCHAR)
	public CField Month; // Month
	@CFieldinfo(fieldname = "Wage", precision = 15, scale = 0, caption = "标准额度", datetype = Types.DOUBLE)
	public CField Wage; // Wage
	@CFieldinfo(fieldname = "UsedWage", precision = 15, scale = 0, caption = "已用额度", datetype = Types.DOUBLE)
	public CField UsedWage; // UsedWage
	@CFieldinfo(fieldname = "Balance", precision = 15, scale = 0, caption = "可用额度", datetype = Types.DOUBLE)
	public CField Balance; // Balance
	@CFieldinfo(fieldname = "ReqPosition", precision = 50, scale = 0, caption = "ReqPosition", datetype = Types.NVARCHAR)
	public CField ReqPosition; // ReqPosition
	@CFieldinfo(fieldname = "ReqLevel", precision = 50, scale = 0, caption = "ReqLevel", datetype = Types.NVARCHAR)
	public CField ReqLevel; // ReqLevel
	@CFieldinfo(fieldname = "Interviewer1", precision = 20, scale = 0, caption = "Interviewer1", datetype = Types.NVARCHAR)
	public CField Interviewer1; // Interviewer1
	@CFieldinfo(fieldname = "Position1", precision = 50, scale = 0, caption = "Position1", datetype = Types.NVARCHAR)
	public CField Position1; // Position1
	@CFieldinfo(fieldname = "Tel1", precision = 30, scale = 0, caption = "Tel1", datetype = Types.VARCHAR)
	public CField Tel1; // Tel1
	@CFieldinfo(fieldname = "Interviewer2", precision = 20, scale = 0, caption = "Interviewer2", datetype = Types.NVARCHAR)
	public CField Interviewer2; // Interviewer2
	@CFieldinfo(fieldname = "Position2", precision = 50, scale = 0, caption = "Position2", datetype = Types.NVARCHAR)
	public CField Position2; // Position2
	@CFieldinfo(fieldname = "Tel2", precision = 30, scale = 0, caption = "Tel2", datetype = Types.VARCHAR)
	public CField Tel2; // Tel2
	@CFieldinfo(fieldname = "Category", precision = 20, scale = 0, caption = "Category", datetype = Types.NVARCHAR)
	public CField Category; // Category
	@CFieldinfo(fieldname = "Property", precision = 20, scale = 0, caption = "Property", datetype = Types.NVARCHAR)
	public CField Property; // Property
	@CFieldinfo(fieldname = "FormType", precision = 20, scale = 0, caption = "FormType", datetype = Types.VARCHAR)
	public CField FormType; // FormType
	@CFieldinfo(fieldname = "Status", precision = 20, scale = 0, caption = "Status", datetype = Types.NVARCHAR)
	public CField Status; // Status
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// Money-Balance 超额额度
	// 自关联数据定义

	public WageSysTohr() throws Exception {
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
