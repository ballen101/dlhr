package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hr_empconbatch_line extends CJPA {
	@CFieldinfo(fieldname="conbatchl_id",iskey=true,notnull=true,caption="批量签订行ID",datetype=Types.INTEGER)
	public CField conbatchl_id;  //批量签订行ID
	@CFieldinfo(fieldname="conbatch_id",notnull=true,caption="批量签订合同ID",datetype=Types.INTEGER)
	public CField conbatch_id;  //批量签订合同ID
	@CFieldinfo(fieldname="con_number",notnull=true,caption="合同编号",datetype=Types.VARCHAR)
	public CField con_number;  //合同编号
	@CFieldinfo(fieldname="er_id",notnull=true,caption="员工档案ID",datetype=Types.INTEGER)
	public CField er_id;  //员工档案ID
	@CFieldinfo(fieldname="employee_code",notnull=true,caption="工号",datetype=Types.VARCHAR)
	public CField employee_code;  //工号
	@CFieldinfo(fieldname="employee_name",notnull=true,caption="姓名",datetype=Types.VARCHAR)
	public CField employee_name;  //姓名
	@CFieldinfo(fieldname="orgid",notnull=true,caption="部门ID",datetype=Types.INTEGER)
	public CField orgid;  //部门ID
	@CFieldinfo(fieldname="orgcode",notnull=true,caption="部门编码",datetype=Types.VARCHAR)
	public CField orgcode;  //部门编码
	@CFieldinfo(fieldname="orgname",notnull=true,caption="部门名称",datetype=Types.VARCHAR)
	public CField orgname;  //部门名称
	@CFieldinfo(fieldname="lv_id",caption="职级ID",datetype=Types.INTEGER)
	public CField lv_id;  //职级ID
	@CFieldinfo(fieldname="lv_num",caption="职级",datetype=Types.DECIMAL)
	public CField lv_num;  //职级
	@CFieldinfo(fieldname="ospid",caption="职位ID",datetype=Types.INTEGER)
	public CField ospid;  //职位ID
	@CFieldinfo(fieldname="ospcode",caption="职位编码",datetype=Types.VARCHAR)
	public CField ospcode;  //职位编码
	@CFieldinfo(fieldname="sp_name",caption="职位名称",datetype=Types.VARCHAR)
	public CField sp_name;  //职位名称
	@CFieldinfo(fieldname="hiredday",caption="入职日期",datetype=Types.TIMESTAMP)
	public CField hiredday;  //入职日期
	@CFieldinfo(fieldname="picture_id",caption="图片",datetype=Types.INTEGER)
	public CField picture_id;  //图片
	@CFieldinfo(fieldname="sign_number",caption="签订次数",datetype=Types.INTEGER)
	public CField sign_number;  //签订次数
	@CFieldinfo(fieldname="contract_type",caption="合同类型",datetype=Types.INTEGER)
	public CField contract_type;  //合同类型
	@CFieldinfo(fieldname="contract_name",notnull=true,caption="合同名称",datetype=Types.VARCHAR)
	public CField contract_name;  //合同名称
	@CFieldinfo(fieldname="signreason",caption="签订原因",datetype=Types.VARCHAR)
	public CField signreason;  //签订原因
	@CFieldinfo(fieldname="remark",caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	@CFieldinfo(fieldname="sign_date",caption="签订日期",datetype=Types.TIMESTAMP)
	public CField sign_date;  //签订日期
	@CFieldinfo(fieldname="end_date",caption="截止日期",datetype=Types.TIMESTAMP)
	public CField end_date;  //截止日期
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hr_empconbatch_line() throws Exception {
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

