package com.hr.salary.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption="总装拉线每月考核指标维护明细",tablename="Hr_salary_linestarget_line")
public class Hr_salary_linestarget_line extends CJPA {
	@CFieldinfo(fieldname ="sltl_id",iskey=true,notnull=true,precision=20,scale=0,caption="总装拉线每月考核指标维护明细id",datetype=Types.INTEGER)
	public CField sltl_id;  //总装拉线每月考核指标维护明细id
	@CFieldinfo(fieldname ="slt_id",notnull=true,precision=20,scale=0,caption="总装拉线每月考核指标维护id",datetype=Types.INTEGER)
	public CField slt_id;  //总装拉线每月考核指标维护id
	@CFieldinfo(fieldname ="orgid",notnull=true,precision=10,scale=0,caption="部门ID",datetype=Types.INTEGER)
	public CField orgid;  //部门ID
	@CFieldinfo(fieldname ="orgcode",notnull=true,precision=16,scale=0,caption="部门编码",datetype=Types.VARCHAR)
	public CField orgcode;  //部门编码
	@CFieldinfo(fieldname ="orgname",notnull=true,precision=128,scale=0,caption="部门名称",datetype=Types.VARCHAR)
	public CField orgname;  //部门名称
	@CFieldinfo(fieldname ="idpath",notnull=true,precision=256,scale=0,caption="idpath",datetype=Types.VARCHAR)
	public CField idpath;  //idpath
	@CFieldinfo(fieldname ="linetype",precision=1,scale=0,caption="拉线类型",datetype=Types.INTEGER)
	public CField linetype;  //拉线类型
	@CFieldinfo(fieldname ="targetoutput",precision=10,scale=1,caption="目标产量",datetype=Types.DECIMAL)
	public CField targetoutput;  //目标产量
	@CFieldinfo(fieldname ="realoutput",precision=10,scale=1,caption="实际产量",datetype=Types.DECIMAL)
	public CField realoutput;  //实际产量
	@CFieldinfo(fieldname ="plancomplete",precision=6,scale=2,caption="计划达成率",datetype=Types.DECIMAL)
	public CField plancomplete;  //计划达成率
	@CFieldinfo(fieldname ="goodsrate",precision=6,scale=2,caption="验货合格率",datetype=Types.DECIMAL)
	public CField goodsrate;  //验货合格率
	@CFieldinfo(fieldname ="staffrunoff",precision=6,scale=2,caption="人员流失率",datetype=Types.DECIMAL)
	public CField staffrunoff;  //人员流失率
	@CFieldinfo(fieldname ="runofftarget",precision=6,scale=2,caption="人员流失率目标值",datetype=Types.DECIMAL)
	public CField runofftarget;  //人员流失率目标值
	@CFieldinfo(fieldname ="workdays",notnull=true,precision=2,scale=0,caption="开拉天数",datetype=Types.INTEGER)
	public CField workdays;  //开拉天数
	@CFieldinfo(fieldname ="standard",precision=10,scale=2,caption="分配金额标准",datetype=Types.DECIMAL)
	public CField standard;  //分配金额标准
	@CFieldinfo(fieldname ="canpay",precision=10,scale=2,caption="可分配金额",datetype=Types.DECIMAL)
	public CField canpay;  //可分配金额
	@CFieldinfo(fieldname ="remark",precision=512,scale=0,caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hr_salary_linestarget_line() throws Exception {
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

