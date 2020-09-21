package com.hr.salary.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;

import java.sql.Types;

@CEntity(caption="机构指标维护",tablename="Hr_salary_projecttarget")
public class Hr_salary_projecttarget extends CJPA {
	@CFieldinfo(fieldname ="pjt_id",iskey=true,notnull=true,precision=20,scale=0,caption="机构指标维护id",datetype=Types.INTEGER)
	public CField pjt_id;  //机构指标维护id
	@CFieldinfo(fieldname ="pjtcode",notnull=true,codeid=142,precision=16,scale=0,caption="机构指标维护编码",datetype=Types.VARCHAR)
	public CField pjtcode;  //机构指标维护编码
	@CFieldinfo(fieldname ="startdate",precision=10,scale=0,caption="开始月份",datetype=Types.DATE)
	public CField startdate;  //开始月份
	@CFieldinfo(fieldname ="enddate",precision=10,scale=0,caption="结束月份",datetype=Types.DATE)
	public CField enddate;  //结束月份
	@CFieldinfo(fieldname ="standardprice",precision=10,scale=2,caption="分配金额标准",datetype=Types.DECIMAL)
	public CField standardprice;  //分配金额标准
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
	@CFieldinfo(fieldname ="idpath",precision=256,scale=0,caption="idpath",datetype=Types.VARCHAR)
	public CField idpath;  //idpath
	@CFieldinfo(fieldname ="creator",notnull=true,precision=32,scale=0,caption="创建人",datetype=Types.VARCHAR)
	public CField creator;  //创建人
	@CFieldinfo(fieldname ="createtime",notnull=true,precision=19,scale=0,caption="创建时间",datetype=Types.TIMESTAMP)
	public CField createtime;  //创建时间
	@CFieldinfo(fieldname ="updator",precision=32,scale=0,caption="更新人",datetype=Types.VARCHAR)
	public CField updator;  //更新人
	@CFieldinfo(fieldname ="updatetime",precision=19,scale=0,caption="更新时间",datetype=Types.TIMESTAMP)
	public CField updatetime;  //更新时间
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;
	@CLinkFieldInfo(jpaclass = Hr_salary_projecttarget_line.class, linkFields = { @LinkFieldItem(lfield = "pjt_id", mfield = "pjt_id") })
	public CJPALineData<Hr_salary_projecttarget_line> hr_salary_projecttarget_lines;

	public Hr_salary_projecttarget() throws Exception {
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

