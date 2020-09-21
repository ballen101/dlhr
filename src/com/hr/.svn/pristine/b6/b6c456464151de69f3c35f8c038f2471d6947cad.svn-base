package com.hr.insurance.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hr_ins_insurancetype_line extends CJPA {
	@CFieldinfo(fieldname="insitl_id",iskey=true,notnull=true,caption="险种设置明细ID",datetype=Types.INTEGER)
	public CField insitl_id;  //险种设置明细ID
	@CFieldinfo(fieldname="insit_id",notnull=true,caption="险种设置ID",datetype=Types.INTEGER)
	public CField insit_id;  //险种设置ID
	@CFieldinfo(fieldname="paybase",notnull=true,caption="缴费基数",datetype=Types.DECIMAL)
	public CField paybase;  //缴费基数
	@CFieldinfo(fieldname="lpayment",caption="分类总金额",datetype=Types.DECIMAL)
	public CField lpayment;  //分类总金额
	@CFieldinfo(fieldname="linsurance",notnull=true,caption="保险占比类别",datetype=Types.VARCHAR)
	public CField linsurance;  //保险占比类别
	@CFieldinfo(fieldname="lselfratio",notnull=true,caption="个人缴费系数%",datetype=Types.DECIMAL)
	public CField lselfratio;  //个人缴费系数%
	@CFieldinfo(fieldname="lselfpay",notnull=true,caption="个人缴费金额",datetype=Types.DECIMAL)
	public CField lselfpay;  //个人缴费金额
	@CFieldinfo(fieldname="lcomratio",notnull=true,caption="公司缴费系数%",datetype=Types.DECIMAL)
	public CField lcomratio;  //公司缴费系数%
	@CFieldinfo(fieldname="lcompay",notnull=true,caption="公司缴费金额",datetype=Types.DECIMAL)
	public CField lcompay;  //公司缴费金额
	@CFieldinfo(fieldname="remark",caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hr_ins_insurancetype_line() throws Exception {
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
