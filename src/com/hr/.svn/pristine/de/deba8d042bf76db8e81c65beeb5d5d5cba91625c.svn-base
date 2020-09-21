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

@CEntity(caption="团队奖报批",tablename="Hr_salary_teamawardsubmit")
public class Hr_salary_teamawardsubmit extends CJPA {
	@CFieldinfo(fieldname ="tws_id",iskey=true,notnull=true,precision=20,scale=0,caption="团队奖报批ID",datetype=Types.INTEGER)
	public CField tws_id;  //团队奖报批ID
	@CFieldinfo(fieldname ="tws_code",codeid=145,notnull=true,precision=16,scale=0,caption="团队奖报批编码",datetype=Types.VARCHAR)
	public CField tws_code;  //团队奖报批编码
	@CFieldinfo(fieldname ="tws_name",precision=128,scale=0,caption="名称",datetype=Types.VARCHAR)
	public CField tws_name;  //名称
	@CFieldinfo(fieldname ="orgid",notnull=true,precision=10,scale=0,caption="部门ID",datetype=Types.INTEGER)
	public CField orgid;  //部门ID
	@CFieldinfo(fieldname ="orgcode",notnull=true,precision=16,scale=0,caption="部门编码",datetype=Types.VARCHAR)
	public CField orgcode;  //部门编码
	@CFieldinfo(fieldname ="orgname",notnull=true,precision=128,scale=0,caption="部门名称",datetype=Types.VARCHAR)
	public CField orgname;  //部门名称
	@CFieldinfo(fieldname ="submitdate",precision=10,scale=0,caption="提报月份",datetype=Types.DATE)
	public CField submitdate;  //提报月份
	@CFieldinfo(fieldname ="ttcanpay",precision=16,scale=2,caption="可分配总金额",defvalue="0.00",datetype=Types.DECIMAL)
	public CField ttcanpay;  //可分配总金额
	@CFieldinfo(fieldname ="ttshouldpay",precision=16,scale=2,caption="实际分配总金额",defvalue="0.00",datetype=Types.DECIMAL)
	public CField ttshouldpay;  //实际分配总金额
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
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义
	@CLinkFieldInfo(jpaclass = Hr_salary_teamawardsubmit_line.class, linkFields = { @LinkFieldItem(lfield = "tws_id", mfield = "tws_id") })
	public CJPALineData<Hr_salary_teamawardsubmit_line> hr_salary_teamawardsubmit_lines;
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hr_salary_teamawardsubmit() throws Exception {
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
