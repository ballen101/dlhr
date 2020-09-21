package com.hr.salary.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption="高温补贴明细")
public class Hr_salary_hotsub_line extends CJPA {
	@CFieldinfo(fieldname="hsl_id",iskey=true,notnull=true,precision=20,scale=0,caption="高温补贴明细ID",datetype=Types.INTEGER)
	public CField hsl_id;  //高温补贴明细ID
	@CFieldinfo(fieldname="hs_id",notnull=true,precision=20,scale=0,caption="高温补贴申请ID",datetype=Types.INTEGER)
	public CField hs_id;  //高温补贴申请ID
	@CFieldinfo(fieldname="er_id",notnull=true,precision=10,scale=0,caption="员工档案ID",datetype=Types.INTEGER)
	public CField er_id;  //员工档案ID
	@CFieldinfo(fieldname="employee_code",notnull=true,precision=16,scale=0,caption="工号",datetype=Types.VARCHAR)
	public CField employee_code;  //工号
	@CFieldinfo(fieldname="employee_name",notnull=true,precision=64,scale=0,caption="姓名",datetype=Types.VARCHAR)
	public CField employee_name;  //姓名
	@CFieldinfo(fieldname="orgid",notnull=true,precision=10,scale=0,caption="部门ID",datetype=Types.INTEGER)
	public CField orgid;  //部门ID
	@CFieldinfo(fieldname="orgcode",notnull=true,precision=16,scale=0,caption="部门编码",datetype=Types.VARCHAR)
	public CField orgcode;  //部门编码
	@CFieldinfo(fieldname="orgname",notnull=true,precision=128,scale=0,caption="部门名称",datetype=Types.VARCHAR)
	public CField orgname;  //部门名称
	@CFieldinfo(fieldname="idpath",notnull=true,precision=256,scale=0,caption="idpath",datetype=Types.VARCHAR)
	public CField idpath;  //idpath
	@CFieldinfo(fieldname="ospid",precision=20,scale=0,caption="职位ID",datetype=Types.INTEGER)
	public CField ospid;  //职位ID
	@CFieldinfo(fieldname="ospcode",precision=16,scale=0,caption="职位编码",datetype=Types.VARCHAR)
	public CField ospcode;  //职位编码
	@CFieldinfo(fieldname="sp_name",precision=128,scale=0,caption="标准职位名称",datetype=Types.VARCHAR)
	public CField sp_name;  //标准职位名称
	@CFieldinfo(fieldname="lv_id",notnull=true,precision=10,scale=0,caption="职级ID",datetype=Types.INTEGER)
	public CField lv_id;  //职级ID
	@CFieldinfo(fieldname="lv_num",notnull=true,precision=4,scale=1,caption="职级",datetype=Types.DECIMAL)
	public CField lv_num;  //职级
	@CFieldinfo(fieldname="hiredday",precision=19,scale=0,caption="入职日期",datetype=Types.TIMESTAMP)
	public CField hiredday;  //入职日期
	@CFieldinfo(fieldname="substand",precision=6,scale=2,caption="津贴标准",datetype=Types.DECIMAL)
	public CField substand;  //津贴标准
	@CFieldinfo(fieldname="shouldduty",precision=6,scale=1,caption="应出满勤",datetype=Types.DECIMAL)
	public CField shouldduty;  //应出满勤
	@CFieldinfo(fieldname="realduty",precision=6,scale=1,caption="应出满勤",datetype=Types.DECIMAL)
	public CField realduty;  //应出满勤
	@CFieldinfo(fieldname="shouldpay",precision=6,scale=2,caption="应发津贴",datetype=Types.DECIMAL)
	public CField shouldpay;  //应发津贴
	@CFieldinfo(fieldname="remark",precision=512,scale=0,caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hr_salary_hotsub_line() throws Exception {
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
