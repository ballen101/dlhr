package com.hr.salary.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.salary.ctr.CtrHr_salary_techsub;

import java.sql.Types;

@CEntity(caption="技术津贴申请",tablename="Hr_salary_techsub",controller=CtrHr_salary_techsub.class)
public class Hr_salary_techsub extends CJPA {
	@CFieldinfo(fieldname ="ts_id",iskey=true,notnull=true,precision=20,scale=0,caption="技术津贴申请ID",datetype=Types.INTEGER)
	public CField ts_id;  //技术津贴申请ID
	@CFieldinfo(fieldname ="ts_code",notnull=true,codeid=128,precision=16,scale=0,caption="技术津贴申请编码",datetype=Types.VARCHAR)
	public CField ts_code;  //技术津贴申请编码
	@CFieldinfo(fieldname ="substype",notnull=true,precision=1,scale=0,caption="津贴类型",defvalue="2",datetype=Types.INTEGER)
	public CField substype;  //津贴类型
	@CFieldinfo(fieldname ="subsdate",precision=19,scale=0,caption="生效月份",datetype=Types.TIMESTAMP)
	public CField subsdate;  //生效月份
	@CFieldinfo(fieldname ="applyreason",notnull=true,precision=512,scale=0,caption="申请原因",datetype=Types.VARCHAR)
	public CField applyreason;  //申请原因
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
	@CFieldinfo(fieldname="appsys",precision=2,scale=0,caption="所属系统",datetype=Types.INTEGER)
	public CField appsys;  //所属系统
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;
	@CLinkFieldInfo(jpaclass = Hr_salary_techsub_line.class, linkFields = { @LinkFieldItem(lfield = "ts_id", mfield = "ts_id") })
	public CJPALineData<Hr_salary_techsub_line> hr_salary_techsub_lines;

	public Hr_salary_techsub() throws Exception {
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
