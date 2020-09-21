package com.hr.inface.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(dbpool = "oldtxmssql", tablename = "dbo.View_ICCO_OpLog")
public class View_ICCO_OpLog extends CJPA {
	@CFieldinfo(fieldname = "id", notnull = true, caption = "门禁序号", datetype = Types.INTEGER)
	public CField id; // 门禁序号
	@CFieldinfo(fieldname = "opTime", notnull = true, caption = "opTime", datetype = Types.INTEGER)
	public CField opTime; // opTime
	@CFieldinfo(fieldname = "BeWrite", caption = "BeWrite", datetype = Types.NVARCHAR)
	public CField BeWrite; // BeWrite
	@CFieldinfo(fieldname = "opUser", caption = "opUser", datetype = Types.NVARCHAR)
	public CField opUser; // opUser
	@CFieldinfo(fieldname = "opComPuter", caption = "opComPuter", datetype = Types.NVARCHAR)
	public CField opComPuter; // opComPuter
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public View_ICCO_OpLog() throws Exception {
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
