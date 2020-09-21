package com.hr.salary.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption="高温补贴资格明细")
public class Hr_salary_hotsub_qual_line extends CJPA {
	@CFieldinfo(fieldname="hsql_id",iskey=true,notnull=true,precision=20,scale=0,caption="补贴资格明细ID",datetype=Types.INTEGER)
	public CField hsql_id;  //补贴资格明细ID
	@CFieldinfo(fieldname="hsq_id",notnull=true,precision=20,scale=0,caption="高温补贴资格申请ID",datetype=Types.INTEGER)
	public CField hsq_id;  //高温补贴资格申请ID
	@CFieldinfo(fieldname="sp_id",precision=20,scale=0,caption="标准职位ID",datetype=Types.INTEGER)
	public CField sp_id;  //标准职位ID
	@CFieldinfo(fieldname="sp_code",precision=16,scale=0,caption="标准职位编码",datetype=Types.VARCHAR)
	public CField sp_code;  //标准职位编码
	@CFieldinfo(fieldname="sp_name",precision=128,scale=0,caption="标准职位名称",datetype=Types.VARCHAR)
	public CField sp_name;  //标准职位名称
	@CFieldinfo(fieldname="substand",precision=6,scale=2,caption="津贴标准",datetype=Types.DECIMAL)
	public CField substand;  //津贴标准
	@CFieldinfo(fieldname="usable",precision=1,scale=0,caption="有效",defvalue="1",datetype=Types.INTEGER)
	public CField usable;  //有效
	@CFieldinfo(fieldname ="canceldate",precision=10,scale=0,caption="终止时间",datetype=Types.DATE)
	public CField canceldate;  //终止时间
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hr_salary_hotsub_qual_line() throws Exception {
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

