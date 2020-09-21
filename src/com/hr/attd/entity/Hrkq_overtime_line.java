package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hrkq_overtime_line extends CJPA {
	@CFieldinfo(fieldname="otl_id",iskey=true,notnull=true,caption="加班申请行ID",datetype=Types.INTEGER)
	public CField otl_id;  //加班申请行ID
	@CFieldinfo(fieldname="ot_id",notnull=true,caption="加班申请ID",datetype=Types.INTEGER)
	public CField ot_id;  //加班申请ID
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
	@CFieldinfo(fieldname="emplev",caption="职位层级",datetype=Types.DECIMAL)
	public CField emplev;  //职位层级
	@CFieldinfo(fieldname="ospid",caption="职位ID",datetype=Types.INTEGER)
	public CField ospid;  //职位ID
	@CFieldinfo(fieldname="ospcode",caption="职位编码",datetype=Types.VARCHAR)
	public CField ospcode;  //职位编码
	@CFieldinfo(fieldname="sp_name",caption="职位名称",datetype=Types.VARCHAR)
	public CField sp_name;  //职位名称
	@CFieldinfo(fieldname="allothours",caption="合计加班时数",datetype=Types.DECIMAL)
	public CField allothours;  //合计加班时数
	@CFieldinfo(fieldname="shouldoffdays",caption="应休天数",datetype=Types.DECIMAL)
	public CField shouldoffdays;  //应休天数
	@CFieldinfo(fieldname="aleadyoffdays",caption="已休天数",datetype=Types.DECIMAL)
	public CField aleadyoffdays;  //已休天数
	@CFieldinfo(fieldname="employee_type",caption="加班人员类型",datetype=Types.INTEGER)
	public CField employee_type;  //加班人员类型
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义
	@CLinkFieldInfo(jpaclass = Hrkq_overtime_hour.class, linkFields = { @LinkFieldItem(lfield = "otl_id", mfield = "otl_id") })
	public CJPALineData<Hrkq_overtime_hour> hrkq_overtime_hours;

	public Hrkq_overtime_line() throws Exception {
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

