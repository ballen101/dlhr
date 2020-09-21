package com.hr.salary.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption="团队奖拉线人员明细",tablename="Hr_salary_teamaward_line_emps")
public class Hr_salary_teamaward_line_emps extends CJPA {
	@CFieldinfo(fieldname ="twle_id",iskey=true,notnull=true,precision=20,scale=0,caption="团队奖拉线人员明细ID",datetype=Types.INTEGER)
	public CField twle_id;  //团队奖拉线人员明细ID
	@CFieldinfo(fieldname ="twl_id",notnull=true,precision=20,scale=0,caption="团队奖明细ID",datetype=Types.INTEGER)
	public CField twl_id;  //团队奖明细ID
	@CFieldinfo(fieldname ="er_id",notnull=true,precision=10,scale=0,caption="员工档案ID",datetype=Types.INTEGER)
	public CField er_id;  //员工档案ID
	@CFieldinfo(fieldname ="employee_code",notnull=true,precision=16,scale=0,caption="工号",datetype=Types.VARCHAR)
	public CField employee_code;  //工号
	@CFieldinfo(fieldname ="employee_name",notnull=true,precision=64,scale=0,caption="姓名",datetype=Types.VARCHAR)
	public CField employee_name;  //姓名
	@CFieldinfo(fieldname ="orgid",notnull=true,precision=10,scale=0,caption="部门ID",datetype=Types.INTEGER)
	public CField orgid;  //部门ID
	@CFieldinfo(fieldname ="orgcode",notnull=true,precision=16,scale=0,caption="部门编码",datetype=Types.VARCHAR)
	public CField orgcode;  //部门编码
	@CFieldinfo(fieldname ="orgname",notnull=true,precision=128,scale=0,caption="部门名称",datetype=Types.VARCHAR)
	public CField orgname;  //部门名称
	@CFieldinfo(fieldname ="idpath",notnull=true,precision=256,scale=0,caption="idpath",datetype=Types.VARCHAR)
	public CField idpath;  //idpath
	@CFieldinfo(fieldname ="ospid",precision=20,scale=0,caption="职位ID",datetype=Types.INTEGER)
	public CField ospid;  //职位ID
	@CFieldinfo(fieldname ="ospcode",precision=16,scale=0,caption="职位编码",datetype=Types.VARCHAR)
	public CField ospcode;  //职位编码
	@CFieldinfo(fieldname ="sp_name",precision=128,scale=0,caption="标准职位名称",datetype=Types.VARCHAR)
	public CField sp_name;  //标准职位名称
	@CFieldinfo(fieldname ="descriprev",precision=6,scale=2,caption="分配比例",datetype=Types.DECIMAL)
	public CField descriprev;  //分配比例
	@CFieldinfo(fieldname ="realpay",precision=10,scale=2,caption="实际分配金额",datetype=Types.DECIMAL)
	public CField realpay;  //实际分配金额
	@CFieldinfo(fieldname ="shouldpay",precision=10,scale=2,caption="可分配金额",datetype=Types.DECIMAL)
	public CField shouldpay;  //可分配金额
	@CFieldinfo(fieldname ="remark",precision=512,scale=0,caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	@CFieldinfo(fieldname ="isparttime",precision=1,scale=0,caption="是否兼职",defvalue="2",datetype=Types.INTEGER)
	public CField isparttime;  //是否兼职
	@CFieldinfo(fieldname ="ptnums",precision=2,scale=0,caption="兼职数量",defvalue="0",datetype=Types.INTEGER)
	public CField ptnums;  //兼职数量
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hr_salary_teamaward_line_emps() throws Exception {
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
