package com.hr.inface.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(dbpool = "oldtxmssql", tablename = "dbo.view_ICCO_AssignEmp")
public class View_ICCO_AssignEmp extends CJPA {
	@CFieldinfo(fieldname="card_id",notnull=true,caption="card_id",datetype=Types.NVARCHAR)
	public CField card_id;  //card_id
	@CFieldinfo(fieldname="clock_id",notnull=true,caption="clock_id",datetype=Types.INTEGER)
	public CField clock_id;  //clock_id
	@CFieldinfo(fieldname="opDate",caption="opDate",datetype=Types.TIMESTAMP)
	public CField opDate;  //opDate
	@CFieldinfo(fieldname="join_id",caption="join_id",datetype=Types.INTEGER)
	public CField join_id;  //join_id
	@CFieldinfo(fieldname="card_sn",caption="card_sn",datetype=Types.NVARCHAR)
	public CField card_sn;  //card_sn
	@CFieldinfo(fieldname="depart_id",caption="depart_id",datetype=Types.VARCHAR)
	public CField depart_id;  //depart_id
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public View_ICCO_AssignEmp() throws Exception {
	}

	@Override
	public boolean InitObject() {//类初始化调用的方法
		super.InitObject();
		return true;
	} 

	@Override
	public boolean FinalObject() { //类释放前调用的方法
		super.FinalObject(); 
		return true; 
	} 
} 

