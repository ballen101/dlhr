package com.hr.salary.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption="技术津贴职位维护",tablename="Hr_salary_techsub_post")
public class Hr_salary_techsub_post extends CJPA {
	@CFieldinfo(fieldname ="tsp_id",iskey=true,notnull=true,precision=20,scale=0,caption="技术津贴申请ID",datetype=Types.INTEGER)
	public CField tsp_id;  //技术津贴申请ID
	@CFieldinfo(fieldname ="tsp_code",notnull=true,codeid=138,precision=16,scale=0,caption="技术津贴申请编码",datetype=Types.VARCHAR)
	public CField tsp_code;  //技术津贴申请编码
	@CFieldinfo(fieldname ="tsp_name",precision=128,scale=0,caption="名称",datetype=Types.VARCHAR)
	public CField tsp_name;  //名称
	@CFieldinfo(fieldname ="appsys",precision=2,scale=0,caption="所属系统",datetype=Types.INTEGER)
	public CField appsys;  //所属系统
	@CFieldinfo(fieldname ="sp_id",precision=20,scale=0,caption="职位ID",datetype=Types.INTEGER)
	public CField sp_id;  //职位ID
	@CFieldinfo(fieldname ="sp_code",precision=16,scale=0,caption="职位编码",datetype=Types.VARCHAR)
	public CField sp_code;  //职位编码
	@CFieldinfo(fieldname ="sp_name",precision=128,scale=0,caption="标准职位名称",datetype=Types.VARCHAR)
	public CField sp_name;  //标准职位名称
	@CFieldinfo(fieldname ="usable",precision=1,scale=0,caption="有效",defvalue="1",datetype=Types.INTEGER)
	public CField usable;  //有效
	@CFieldinfo(fieldname ="remark",precision=512,scale=0,caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	@CFieldinfo(fieldname ="wfid",precision=20,scale=0,caption="wfid",datetype=Types.INTEGER)
	public CField wfid;  //wfid
	@CFieldinfo(fieldname ="attid",precision=20,scale=0,caption="attid",datetype=Types.INTEGER)
	public CField attid;  //attid
	@CFieldinfo(fieldname ="stat",notnull=true,precision=2,scale=0,caption="流程状态",datetype=Types.INTEGER)
	public CField stat;  //流程状态
	@CFieldinfo(fieldname ="entid",notnull=true,precision=5,scale=0,caption="entid",datetype=Types.INTEGER)
	public CField entid;  //entid
	@CFieldinfo(fieldname ="creator",notnull=true,precision=32,scale=0,caption="创建人",datetype=Types.VARCHAR)
	public CField creator;  //创建人
	@CFieldinfo(fieldname ="createtime",notnull=true,precision=19,scale=0,caption="创建时间",datetype=Types.TIMESTAMP)
	public CField createtime;  //创建时间
	@CFieldinfo(fieldname ="updator",precision=32,scale=0,caption="更新人",datetype=Types.VARCHAR)
	public CField updator;  //更新人
	@CFieldinfo(fieldname ="updatetime",precision=19,scale=0,caption="更新时间",datetype=Types.TIMESTAMP)
	public CField updatetime;  //更新时间
	@CFieldinfo(fieldname ="property1",precision=32,scale=0,caption="备用字段1",datetype=Types.VARCHAR)
	public CField property1;  //备用字段1
	@CFieldinfo(fieldname ="property2",precision=64,scale=0,caption="备用字段2",datetype=Types.VARCHAR)
	public CField property2;  //备用字段2
	@CFieldinfo(fieldname ="property3",precision=32,scale=0,caption="备用字段3",datetype=Types.VARCHAR)
	public CField property3;  //备用字段3
	@CFieldinfo(fieldname ="property4",precision=32,scale=0,caption="备用字段4",datetype=Types.VARCHAR)
	public CField property4;  //备用字段4
	@CFieldinfo(fieldname ="property5",precision=32,scale=0,caption="备用字段5",datetype=Types.VARCHAR)
	public CField property5;  //备用字段5
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hr_salary_techsub_post() throws Exception {
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

