package com.hr.pm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hrpm_rstmonthex extends CJPA {
	@CFieldinfo(fieldname="pmmrid",iskey=true,notnull=true,caption="ID",datetype=Types.INTEGER)
	public CField pmmrid;  //ID
	@CFieldinfo(fieldname="er_id",caption="人事ID",datetype=Types.INTEGER)
	public CField er_id;  //人事ID
	@CFieldinfo(fieldname="employee_code",caption="工号",datetype=Types.VARCHAR)
	public CField employee_code;  //工号
	@CFieldinfo(fieldname="employee_name",caption="姓名",datetype=Types.VARCHAR)
	public CField employee_name;  //姓名
	@CFieldinfo(fieldname="pmtype",caption="1 主职 2 兼职",defvalue="1",datetype=Types.INTEGER)
	public CField pmtype;  //1 主职 2 兼职
	@CFieldinfo(fieldname="orgname",caption="机构",datetype=Types.VARCHAR)
	public CField orgname;  //机构
	@CFieldinfo(fieldname="sp_name",caption="职位",datetype=Types.VARCHAR)
	public CField sp_name;  //职位
	@CFieldinfo(fieldname="lv_num",caption="职级",datetype=Types.DECIMAL)
	public CField lv_num;  //职级
	@CFieldinfo(fieldname="pmyear",caption="年",datetype=Types.INTEGER)
	public CField pmyear;  //年
	@CFieldinfo(fieldname="pmonth",caption="月",datetype=Types.INTEGER)
	public CField pmonth;  //月
	@CFieldinfo(fieldname="qrst",caption="绩效",datetype=Types.DECIMAL)
	public CField qrst;  //绩效
	@CFieldinfo(fieldname="createtime",caption="创建时间",datetype=Types.TIMESTAMP)
	public CField createtime;  //创建时间
	@CFieldinfo(fieldname="remark",caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hrpm_rstmonthex() throws Exception {
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
