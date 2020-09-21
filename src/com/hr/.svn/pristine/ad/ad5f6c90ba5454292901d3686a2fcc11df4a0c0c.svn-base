package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;

import java.sql.Types;

@CEntity()
public class Hr_contract_version extends CJPA {
	@CFieldinfo(fieldname="ver_id",iskey=true,notnull=true,caption="合同ID",datetype=Types.INTEGER)
	public CField ver_id;  //合同ID
	@CFieldinfo(fieldname="ver_code",codeid=72,notnull=true,caption="编码",datetype=Types.VARCHAR)
	public CField ver_code;  //编码
	@CFieldinfo(fieldname="version_describe",notnull=true,caption="版本描述",datetype=Types.VARCHAR)
	public CField version_describe;  //版本描述
	@CFieldinfo(fieldname="version",caption="版本号",datetype=Types.VARCHAR)
	public CField version;  //版本号
	@CFieldinfo(fieldname="contract_type",caption="合同类型",datetype=Types.INTEGER)
	public CField contract_type;  //合同类型
	@CFieldinfo(fieldname="contractstat",caption="合同状态",datetype=Types.INTEGER)
	public CField contractstat;  //合同状态
	@CFieldinfo(fieldname="contract_name",caption="合同名称",datetype=Types.VARCHAR)
	public CField contract_name;  //合同名称
	@CFieldinfo(fieldname="use_date",caption="生效日期",datetype=Types.TIMESTAMP)
	public CField use_date;  //生效日期
	@CFieldinfo(fieldname="invaliddate",caption="失效日期",datetype=Types.TIMESTAMP)
	public CField invaliddate;  //生效日期
	@CFieldinfo(fieldname="usable",caption="可用",datetype=Types.DECIMAL)
	public CField usable;  //可用
	@CFieldinfo(fieldname="remark",caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	@CFieldinfo(fieldname="wfid",caption="wfid",datetype=Types.INTEGER)
	public CField wfid;  //wfid
	@CFieldinfo(fieldname="attid",caption="attid",datetype=Types.INTEGER)
	public CField attid;  //attid
	@CFieldinfo(fieldname="stat",notnull=true,caption="表单状态",datetype=Types.INTEGER)
	public CField stat;  //表单状态
	@CFieldinfo(fieldname="idpath",notnull=true,caption="idpath",datetype=Types.VARCHAR)
	public CField idpath;  //idpath
	@CFieldinfo(fieldname="entid",notnull=true,caption="entid",datetype=Types.INTEGER)
	public CField entid;  //entid
	@CFieldinfo(fieldname="creator",notnull=true,caption="创建人",datetype=Types.VARCHAR)
	public CField creator;  //创建人
	@CFieldinfo(fieldname="createtime",notnull=true,caption="创建时间",datetype=Types.TIMESTAMP)
	public CField createtime;  //创建时间
	@CFieldinfo(fieldname="updator",caption="更新人",datetype=Types.VARCHAR)
	public CField updator;  //更新人
	@CFieldinfo(fieldname="updatetime",caption="更新时间",datetype=Types.TIMESTAMP)
	public CField updatetime;  //更新时间
	@CFieldinfo(fieldname="attribute1",caption="备用字段1",datetype=Types.VARCHAR)
	public CField attribute1;  //备用字段1
	@CFieldinfo(fieldname="attribute2",caption="备用字段2",datetype=Types.VARCHAR)
	public CField attribute2;  //备用字段2
	@CFieldinfo(fieldname="attribute3",caption="备用字段3",datetype=Types.VARCHAR)
	public CField attribute3;  //备用字段3
	@CFieldinfo(fieldname="attribute4",caption="备用字段4",datetype=Types.VARCHAR)
	public CField attribute4;  //备用字段4
	@CFieldinfo(fieldname="attribute5",caption="备用字段5",datetype=Types.VARCHAR)
	public CField attribute5;  //备用字段5
	@CFieldinfo(fieldname="invalidreason",caption="作废原因",datetype=Types.VARCHAR)
	public CField invalidreason;  //作废原因
	@CFieldinfo(fieldname="orgid",caption="机构ID",datetype=Types.INTEGER)
	public CField orgid;  //机构ID
	@CFieldinfo(fieldname="orgcode",caption="机构编码",datetype=Types.VARCHAR)
	public CField orgcode;  //机构编码
	@CFieldinfo(fieldname="orgname",caption="机构名称",datetype=Types.VARCHAR)
	public CField orgname;  //机构名称
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hr_contract_version() throws Exception {
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
