package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hrkq_ohyear extends CJPA {
	@CFieldinfo(fieldname="ohid",iskey=true,notnull=true,caption="ID",datetype=Types.INTEGER)
	public CField ohid;  //ID
	@CFieldinfo(fieldname="ohyear",notnull=true,caption="年度",datetype=Types.VARCHAR)
	public CField ohyear;  //年度
	@CFieldinfo(fieldname="ohdate",notnull=true,caption="日期",datetype=Types.DATE)
	public CField ohdate;  //日期
	@CFieldinfo(fieldname="iswork",caption="工作",datetype=Types.INTEGER)
	public CField iswork;  //工作
	@CFieldinfo(fieldname="daydis",caption="显示",datetype=Types.VARCHAR)
	public CField daydis;  //显示
	@CFieldinfo(fieldname="daymeo",caption="备注",datetype=Types.VARCHAR)
	public CField daymeo;  //备注
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hrkq_ohyear() throws Exception {
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

