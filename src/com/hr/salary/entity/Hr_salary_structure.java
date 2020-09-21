package com.hr.salary.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import com.hr.salary.ctr.CtrHr_salary_structure;

import java.sql.Types;

@CEntity(caption="工资结构设置",controller =CtrHr_salary_structure.class)
public class Hr_salary_structure extends CJPA {
	@CFieldinfo(fieldname="stru_id",iskey=true,notnull=true,precision=20,scale=0,caption="工资结构ID",datetype=Types.BIGINT)
	public CField stru_id;  //工资结构ID
	@CFieldinfo(fieldname="stru_code",codeid=123,notnull=true,precision=64,scale=0,caption="工资结构编码",datetype=Types.VARCHAR)
	public CField stru_code;  //工资结构编码
	@CFieldinfo(fieldname="stru_name",precision=64,scale=0,caption="工资结构名",datetype=Types.VARCHAR)
	public CField stru_name;  //工资结构名
	@CFieldinfo(fieldname="kqtype",precision=1,scale=0,caption="出勤机制",datetype=Types.INTEGER)
	public CField kqtype;  //出勤机制
	@CFieldinfo(fieldname="checklev",precision=1,scale=0,caption="绩效考核层级",datetype=Types.INTEGER)
	public CField checklev;  //绩效考核层级
	@CFieldinfo(fieldname="basewage",precision=5,scale=2,caption="基本工资",datetype=Types.DECIMAL)
	public CField basewage;  //基本工资
	@CFieldinfo(fieldname="otwage",precision=5,scale=2,caption="固定加班工资",datetype=Types.DECIMAL)
	public CField otwage;  //固定加班工资
	@CFieldinfo(fieldname="skillwage",precision=5,scale=2,caption="技能工资",datetype=Types.DECIMAL)
	public CField skillwage;  //技能工资
	@CFieldinfo(fieldname="meritwage",precision=5,scale=2,caption="绩效工资",datetype=Types.DECIMAL)
	public CField meritwage;  //绩效工资
	@CFieldinfo(fieldname="strutype",precision=1,scale=0,caption="工资结构类型",datetype=Types.INTEGER)
	public CField strutype;  //工资结构类型
	@CFieldinfo(fieldname="remark",precision=512,scale=0,caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	@CFieldinfo(fieldname="wfid",precision=20,scale=0,caption="wfid",datetype=Types.INTEGER)
	public CField wfid;  //wfid
	@CFieldinfo(fieldname="attid",precision=20,scale=0,caption="attid",datetype=Types.INTEGER)
	public CField attid;  //attid
	@CFieldinfo(fieldname="stat",notnull=true,precision=2,scale=0,caption="流程状态",datetype=Types.INTEGER)
	public CField stat;  //流程状态
	@CFieldinfo(fieldname="idpath",notnull=true,precision=256,scale=0,caption="idpath",datetype=Types.VARCHAR)
	public CField idpath;  //idpath
	@CFieldinfo(fieldname="entid",notnull=true,precision=5,scale=0,caption="entid",datetype=Types.INTEGER)
	public CField entid;  //entid
	@CFieldinfo(fieldname="creator",notnull=true,precision=32,scale=0,caption="创建人",datetype=Types.VARCHAR)
	public CField creator;  //创建人
	@CFieldinfo(fieldname="createtime",notnull=true,precision=19,scale=0,caption="创建时间",datetype=Types.TIMESTAMP)
	public CField createtime;  //创建时间
	@CFieldinfo(fieldname="updator",precision=32,scale=0,caption="更新人",datetype=Types.VARCHAR)
	public CField updator;  //更新人
	@CFieldinfo(fieldname="updatetime",precision=19,scale=0,caption="更新时间",datetype=Types.TIMESTAMP)
	public CField updatetime;  //更新时间
	@CFieldinfo(fieldname="attribute1",precision=32,scale=0,caption="备用字段1",datetype=Types.VARCHAR)
	public CField attribute1;  //备用字段1
	@CFieldinfo(fieldname="attribute2",precision=32,scale=0,caption="备用字段2",datetype=Types.VARCHAR)
	public CField attribute2;  //备用字段2
	@CFieldinfo(fieldname="attribute3",precision=32,scale=0,caption="备用字段3",datetype=Types.VARCHAR)
	public CField attribute3;  //备用字段3
	@CFieldinfo(fieldname="attribute4",precision=32,scale=0,caption="备用字段4",datetype=Types.VARCHAR)
	public CField attribute4;  //备用字段4
	@CFieldinfo(fieldname="attribute5",precision=32,scale=0,caption="备用字段5",datetype=Types.VARCHAR)
	public CField attribute5;  //备用字段5
	@CFieldinfo(fieldname="usable",notnull=true,precision=1,scale=0,caption="usable",defvalue="1",datetype=Types.INTEGER)
	public CField usable;  //usable
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hr_salary_structure() throws Exception {
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
