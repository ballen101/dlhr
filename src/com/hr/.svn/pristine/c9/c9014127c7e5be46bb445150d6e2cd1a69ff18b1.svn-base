package com.hr.salary.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption="团队奖批明细",tablename="Hr_salary_teamawardsubmit_line")
public class Hr_salary_teamawardsubmit_line extends CJPA {
	@CFieldinfo(fieldname ="twsl_id",iskey=true,notnull=true,precision=20,scale=0,caption="团队奖报批明细ID",datetype=Types.INTEGER)
	public CField twsl_id;  //团队奖报批明细ID
	@CFieldinfo(fieldname ="tws_id",notnull=true,precision=20,scale=0,caption="团队奖报批ID",datetype=Types.INTEGER)
	public CField tws_id;  //团队奖报批ID
	@CFieldinfo(fieldname ="orgid",notnull=true,precision=10,scale=0,caption="部门ID",datetype=Types.INTEGER)
	public CField orgid;  //部门ID
	@CFieldinfo(fieldname ="orgcode",notnull=true,precision=16,scale=0,caption="部门编码",datetype=Types.VARCHAR)
	public CField orgcode;  //部门编码
	@CFieldinfo(fieldname ="orgname",notnull=true,precision=128,scale=0,caption="部门名称",datetype=Types.VARCHAR)
	public CField orgname;  //部门名称
	@CFieldinfo(fieldname ="idpath",notnull=true,precision=256,scale=0,caption="idpath",datetype=Types.VARCHAR)
	public CField idpath;  //idpath
	@CFieldinfo(fieldname ="linenums",notnull=true,precision=10,scale=0,caption="拉线数",datetype=Types.INTEGER)
	public CField linenums;  //拉线数
	@CFieldinfo(fieldname ="payempnums",notnull=true,precision=10,scale=0,caption="本月参与分配人数",datetype=Types.INTEGER)
	public CField payempnums;  //本月参与分配人数
	@CFieldinfo(fieldname ="tcanpay",precision=10,scale=2,caption="本月可分配总金额",defvalue="0.00",datetype=Types.DECIMAL)
	public CField tcanpay;  //本月可分配总金额
	@CFieldinfo(fieldname ="tshouldpay",precision=10,scale=2,caption="实际分配金额",defvalue="0.00",datetype=Types.DECIMAL)
	public CField tshouldpay;  //实际分配金额
	@CFieldinfo(fieldname ="avgpay",precision=10,scale=2,caption="人均分配金额",defvalue="0.00",datetype=Types.DECIMAL)
	public CField avgpay;  //人均分配金额
	@CFieldinfo(fieldname ="useable",precision=1,scale=0,caption="有效",defvalue="1",datetype=Types.INTEGER)
	public CField useable;  //有效
	@CFieldinfo(fieldname ="remark",precision=512,scale=0,caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hr_salary_teamawardsubmit_line() throws Exception {
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

