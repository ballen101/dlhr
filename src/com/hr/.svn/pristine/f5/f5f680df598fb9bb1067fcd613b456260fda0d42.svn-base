package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption="调动生成购保调度表")
public class Hrkqtransfer2insurancetimer extends CJPA {
	@CFieldinfo(fieldname="timer_id",iskey=true,notnull=true,precision=20,scale=0,caption="ID",datetype=Types.INTEGER)
	public CField timer_id;  //ID
	@CFieldinfo(fieldname="er_id",precision=10,scale=0,caption="档案ID",datetype=Types.INTEGER)
	public CField er_id;  //档案ID
	@CFieldinfo(fieldname="employee_code",precision=16,scale=0,caption="工号",datetype=Types.VARCHAR)
	public CField employee_code;  //工号
	@CFieldinfo(fieldname="employee_name",precision=64,scale=0,caption="员工姓名",datetype=Types.VARCHAR)
	public CField employee_name;  //员工姓名
	@CFieldinfo(fieldname="tranfcmpdate",precision=19,scale=0,caption="调动生效日期",datetype=Types.TIMESTAMP)
	public CField tranfcmpdate;  //调动生效日期
	@CFieldinfo(fieldname="dobuyinsdate",precision=19,scale=0,caption="购保日期",datetype=Types.TIMESTAMP)
	public CField dobuyinsdate;  //购保日期
	@CFieldinfo(fieldname="isbuyins",precision=2,scale=0,caption="是否已购保",defvalue="2",datetype=Types.INTEGER)
	public CField isbuyins;  //是否已购保
	@CFieldinfo(fieldname="sourceid",precision=10,scale=0,caption="来源单ID",datetype=Types.INTEGER)
	public CField sourceid;  //来源单ID
	@CFieldinfo(fieldname="sourcecode",precision=16,scale=0,caption="来源单编码",datetype=Types.VARCHAR)
	public CField sourcecode;  //来源单编码
	@CFieldinfo(fieldname="buyins_id",precision=10,scale=0,caption="保险购买ID",datetype=Types.INTEGER)
	public CField buyins_id;  //保险购买ID
	@CFieldinfo(fieldname="buyins_code",precision=16,scale=0,caption="保险购买编码",datetype=Types.VARCHAR)
	public CField buyins_code;  //保险购买编码
	@CFieldinfo(fieldname="remark",precision=512,scale=0,caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hrkqtransfer2insurancetimer() throws Exception {
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
