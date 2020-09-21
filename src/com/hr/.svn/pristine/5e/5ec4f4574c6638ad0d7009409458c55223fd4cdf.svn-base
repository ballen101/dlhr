package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hr_employee_contract_line extends CJPA {
	@CFieldinfo(fieldname="conlineid",iskey=true,notnull=true,caption="明细行ID",datetype=Types.INTEGER)
	public CField conlineid;  //明细行ID
	@CFieldinfo(fieldname="con_id",notnull=true,caption="合同ID",datetype=Types.INTEGER)
	public CField con_id;  //合同ID
	@CFieldinfo(fieldname="changetype",caption="异动类型。1、终止；2解除；3变更",datetype=Types.INTEGER)
	public CField changetype;  //异动类型。1、终止；2解除；3变更
	@CFieldinfo(fieldname="description",notnull=true,caption="异动说明",datetype=Types.VARCHAR)
	public CField description;  //异动说明
	@CFieldinfo(fieldname="changetime",caption="异动时间",datetype=Types.TIMESTAMP)
	public CField changetime;  //异动时间
	@CFieldinfo(fieldname="note",caption="备注",datetype=Types.VARCHAR)
	public CField note;  //备注
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hr_employee_contract_line() throws Exception {
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
