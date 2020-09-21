package com.hr.salary.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import com.hr.salary.ctr.CtrHr_salary_hotsub_qual_cancel;

import java.sql.Types;

@CEntity(caption="高温补贴资格终止",tablename="Hr_salary_hotsub_qual_cancel",controller=CtrHr_salary_hotsub_qual_cancel.class)
public class Hr_salary_hotsub_qual_cancel extends CJPA {
	@CFieldinfo(fieldname ="hsqc_id",iskey=true,notnull=true,precision=20,scale=0,caption="高温补贴资格终止ID",datetype=Types.INTEGER)
	public CField hsqc_id;  //高温补贴资格终止ID
	@CFieldinfo(fieldname ="hsqc_code",codeid=137,notnull=true,precision=16,scale=0,caption="高温补贴资格终止编码",datetype=Types.VARCHAR)
	public CField hsqc_code;  //高温补贴资格终止编码
	@CFieldinfo(fieldname ="canceldate",notnull=true,precision=10,scale=0,caption="终止时间",datetype=Types.DATE)
	public CField canceldate;  //终止时间
	@CFieldinfo(fieldname ="cancelreason",notnull=true,precision=512,scale=0,caption="终止原因",datetype=Types.VARCHAR)
	public CField cancelreason;  //终止原因
	@CFieldinfo(fieldname ="orgid",notnull=true,precision=10,scale=0,caption="部门ID",datetype=Types.INTEGER)
	public CField orgid;  //部门ID
	@CFieldinfo(fieldname ="orgcode",notnull=true,precision=16,scale=0,caption="部门编码",datetype=Types.VARCHAR)
	public CField orgcode;  //部门编码
	@CFieldinfo(fieldname ="orgname",notnull=true,precision=128,scale=0,caption="部门名称",datetype=Types.VARCHAR)
	public CField orgname;  //部门名称
	@CFieldinfo(fieldname ="substype",notnull=true,precision=1,scale=0,caption="津贴类型",defvalue="3",datetype=Types.INTEGER)
	public CField substype;  //津贴类型
	@CFieldinfo(fieldname ="startdate",precision=19,scale=0,caption="开始时间",datetype=Types.TIMESTAMP)
	public CField startdate;  //开始时间
	@CFieldinfo(fieldname ="enddate",precision=19,scale=0,caption="结束时间",datetype=Types.TIMESTAMP)
	public CField enddate;  //结束时间
	@CFieldinfo(fieldname ="applyreason",notnull=true,precision=512,scale=0,caption="申请原因",datetype=Types.VARCHAR)
	public CField applyreason;  //申请原因
	@CFieldinfo(fieldname ="sp_id",precision=20,scale=0,caption="标准职位ID",datetype=Types.INTEGER)
	public CField sp_id;  //标准职位ID
	@CFieldinfo(fieldname ="sp_code",precision=16,scale=0,caption="标准职位编码",datetype=Types.VARCHAR)
	public CField sp_code;  //标准职位编码
	@CFieldinfo(fieldname ="sp_name",precision=128,scale=0,caption="标准职位名称",datetype=Types.VARCHAR)
	public CField sp_name;  //标准职位名称
	@CFieldinfo(fieldname ="substand",precision=6,scale=2,caption="津贴标准",datetype=Types.DECIMAL)
	public CField substand;  //津贴标准
	@CFieldinfo(fieldname ="hsql_id",notnull=true,precision=20,scale=0,caption="高温补贴资格明细ID",datetype=Types.INTEGER)
	public CField hsql_id;  //高温补贴资格明细ID
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
	@CFieldinfo(fieldname ="idpath",notnull=true,precision=256,scale=0,caption="idpath",datetype=Types.VARCHAR)
	public CField idpath;  //idpath
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
	@CFieldinfo(fieldname ="usable",notnull=true,precision=1,scale=0,caption="有效",defvalue="1",datetype=Types.INTEGER)
	public CField usable;  //有效
	@CFieldinfo(fieldname ="orghrlev",precision=1,scale=0,caption="orghrlev",datetype=Types.INTEGER)
	public CField orghrlev;  //orghrlev
	@CFieldinfo(fieldname ="emplev",precision=2,scale=0,caption="emplev",datetype=Types.INTEGER)
	public CField emplev;  //emplev
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hr_salary_hotsub_qual_cancel() throws Exception {
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

