package com.hr.salary.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption="机构指标维护明细",tablename="Hr_salary_projecttarget_line")
public class Hr_salary_projecttarget_line extends CJPA {
	@CFieldinfo(fieldname ="pjtl_id",iskey=true,notnull=true,precision=20,scale=0,caption="机构指标维护明细id",datetype=Types.INTEGER)
	public CField pjtl_id;  //机构指标维护明细id
	@CFieldinfo(fieldname ="pjt_id",notnull=true,precision=20,scale=0,caption="机构指标维护id",datetype=Types.INTEGER)
	public CField pjt_id;  //机构指标维护id
	@CFieldinfo(fieldname ="orgid",notnull=true,precision=10,scale=0,caption="部门ID",datetype=Types.INTEGER)
	public CField orgid;  //部门ID
	@CFieldinfo(fieldname ="orgcode",notnull=true,precision=16,scale=0,caption="部门编码",datetype=Types.VARCHAR)
	public CField orgcode;  //部门编码
	@CFieldinfo(fieldname ="orgname",notnull=true,precision=128,scale=0,caption="部门名称",datetype=Types.VARCHAR)
	public CField orgname;  //部门名称
	@CFieldinfo(fieldname ="idpath",notnull=true,precision=256,scale=0,caption="idpath",datetype=Types.VARCHAR)
	public CField idpath;  //idpath
	@CFieldinfo(fieldname ="projecttype",precision=1,scale=0,caption="指标",datetype=Types.INTEGER)
	public CField projecttype;  //指标
	@CFieldinfo(fieldname ="operators",precision=1,scale=0,caption="运算符",datetype=Types.INTEGER)
	public CField operators;  //运算符
	@CFieldinfo(fieldname ="targets",precision=10,scale=1,caption="目标值",datetype=Types.DECIMAL)
	public CField targets;  //目标值
	@CFieldinfo(fieldname ="weight",precision=10,scale=1,caption="权重",datetype=Types.DECIMAL)
	public CField weight;  //权重
	@CFieldinfo(fieldname ="reachprice",precision=10,scale=2,caption="达成金额",datetype=Types.DECIMAL)
	public CField reachprice;  //达成金额
	@CFieldinfo(fieldname ="remark",precision=512,scale=0,caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hr_salary_projecttarget_line() throws Exception {
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

