package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hrkq_overtime_adjust_line extends CJPA {
	@CFieldinfo(fieldname="otadl_id",iskey=true,notnull=true,caption="加班调整行ID",datetype=Types.INTEGER)
	public CField otadl_id;  //加班调整行ID
	@CFieldinfo(fieldname="otad_id",notnull=true,caption="加班调整ID",datetype=Types.INTEGER)
	public CField otad_id;  //加班调整ID
	@CFieldinfo(fieldname="deductoff",caption="扣休息时数",datetype=Types.DECIMAL)
	public CField deductoff;  //扣休息时数
	@CFieldinfo(fieldname="othours",caption="加班时数",datetype=Types.DECIMAL)
	public CField othours;  //加班时数
	@CFieldinfo(fieldname="begin_date",caption="申请加班开始时间",datetype=Types.TIMESTAMP)
	public CField begin_date;  //申请加班开始时间
	@CFieldinfo(fieldname="end_date",caption="申请加班结束时间",datetype=Types.TIMESTAMP)
	public CField end_date;  //申请加班结束时间
	@CFieldinfo(fieldname="alterbegin_date",caption="加班调整开始时间",datetype=Types.TIMESTAMP)
	public CField alterbegin_date;  //加班调整开始时间
	@CFieldinfo(fieldname="alterend_date",caption="加班调整结束时间",datetype=Types.TIMESTAMP)
	public CField alterend_date;  //加班调整结束时间
	@CFieldinfo(fieldname="remark",caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	@CFieldinfo(fieldname="oth_id",caption="加班时间明细ID",datetype=Types.INTEGER)
	public CField oth_id;  //加班时间明细ID
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hrkq_overtime_adjust_line() throws Exception {
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
