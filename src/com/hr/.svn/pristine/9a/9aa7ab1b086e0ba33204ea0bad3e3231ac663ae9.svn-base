package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hrkq_overtime_qual_line extends CJPA {
	@CFieldinfo(fieldname="oql_id",iskey=true,notnull=true,caption="加班资格行ID",datetype=Types.INTEGER)
	public CField oql_id;  //加班资格行ID
	@CFieldinfo(fieldname="oq_id",notnull=true,caption="加班资格申请ID",datetype=Types.INTEGER)
	public CField oq_id;  //加班资格申请ID
	@CFieldinfo(fieldname="breaked",caption="已终止",defvalue="2",datetype=Types.INTEGER)
	public CField breaked;  //已终止
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
	@CFieldinfo(fieldname = "orghrlev", caption = "机构人事层级", datetype = Types.INTEGER)
	public CField orghrlev; // 机构人事层级
	@CFieldinfo(fieldname = "emplev", caption = "人事层级", datetype = Types.INTEGER)
	public CField emplev; // 人事层级
	@CFieldinfo(fieldname="remark",caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hrkq_overtime_qual_line() throws Exception {
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

