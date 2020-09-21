package com.hr.inface.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(dbpool = "oldtxmssql", tablename = "HRMS.[dbo].[view_ICCO_TimeRecords]")
public class View_ICCO_TimeRecords extends CJPA {
	@CFieldinfo(fieldname="ID",notnull=true,precision=10,scale=0,caption="ID",datetype=Types.INTEGER)
	public CField ID;  //ID
	@CFieldinfo(fieldname="join_id",precision=10,scale=0,caption="join_id",datetype=Types.INTEGER)
	public CField join_id;  //join_id员工ID
	@CFieldinfo(fieldname="code",notnull=true,precision=12,scale=0,caption="code",datetype=Types.CHAR)
	public CField code;  //code工号
	@CFieldinfo(fieldname="card_id",notnull=true,precision=16,scale=0,caption="card_id",datetype=Types.NVARCHAR)
	public CField card_id;  //card_id卡号
	@CFieldinfo(fieldname="clock_id",notnull=true,precision=10,scale=0,caption="clock_id",datetype=Types.INTEGER)
	public CField clock_id;  //clock_id机号
	@CFieldinfo(fieldname="card_sn",precision=20,scale=0,caption="card_sn",datetype=Types.VARCHAR)
	public CField card_sn;  //card_sn序列号
	@CFieldinfo(fieldname="sign_time",notnull=true,precision=23,scale=3,caption="sign_time",datetype=Types.TIMESTAMP)
	public CField sign_time;  //sign_time操作日期
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public View_ICCO_TimeRecords() throws Exception {
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

