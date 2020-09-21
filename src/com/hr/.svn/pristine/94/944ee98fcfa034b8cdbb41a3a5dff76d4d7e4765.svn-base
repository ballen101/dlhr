package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.perm.ctr.CtrHr_empconbatch;

import java.sql.Types;

@CEntity(controller =CtrHr_empconbatch.class)
public class Hr_empconbatch extends CJPA {
	@CFieldinfo(fieldname="conbatch_id",iskey=true,notnull=true,caption="批量签订合同ID",datetype=Types.INTEGER)
	public CField conbatch_id;  //批量签订合同ID
	@CFieldinfo(fieldname="conbatch_code",codeid=74,notnull=true,caption="编码",datetype=Types.VARCHAR)
	public CField conbatch_code;  //编码
	@CFieldinfo(fieldname="orgid",notnull=true,caption="部门ID",datetype=Types.INTEGER)
	public CField orgid;  //部门ID
	@CFieldinfo(fieldname="orgcode",notnull=true,caption="部门编码",datetype=Types.VARCHAR)
	public CField orgcode;  //部门编码
	@CFieldinfo(fieldname="orgname",notnull=true,caption="部门名称",datetype=Types.VARCHAR)
	public CField orgname;  //部门名称
	@CFieldinfo(fieldname="contractstat",notnull=true,caption="合同状态",datetype=Types.INTEGER)
	public CField contractstat;  //合同状态
	@CFieldinfo(fieldname="signyears",caption="签订年限",datetype=Types.INTEGER)
	public CField signyears;  //签订年限
	@CFieldinfo(fieldname="sign_date",caption="签订日期",datetype=Types.TIMESTAMP)
	public CField sign_date;  //签订日期
	@CFieldinfo(fieldname="begin_date",caption="开始日期",datetype=Types.TIMESTAMP)
	public CField begin_date;  //开始日期
	@CFieldinfo(fieldname="end_date",caption="截止日期",datetype=Types.TIMESTAMP)
	public CField end_date;  //截止日期
	@CFieldinfo(fieldname="renew_date",caption="续约日期",datetype=Types.TIMESTAMP)
	public CField renew_date;  //续约日期
	@CFieldinfo(fieldname="is_remind",caption="是否预警",datetype=Types.INTEGER)
	public CField is_remind;  //是否预警
	@CFieldinfo(fieldname="usable",caption="可用",datetype=Types.DECIMAL)
	public CField usable;  //可用
	@CFieldinfo(fieldname="deadline_type",caption="期限类型",datetype=Types.INTEGER)
	public CField deadline_type;  //期限类型
	@CFieldinfo(fieldname="ispermanent",caption="是否正式员工。1、正式合同；2、实习生合同",datetype=Types.INTEGER)
	public CField ispermanent;  //是否正式员工。1、正式合同；2、实习生合同
	@CFieldinfo(fieldname="version",caption="版本号",datetype=Types.VARCHAR)
	public CField version;  //版本号
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
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义
	@CLinkFieldInfo(jpaclass = Hr_empconbatch_line.class, linkFields = { @LinkFieldItem(lfield = "conbatch_id", mfield = "conbatch_id") })
	public CJPALineData<Hr_empconbatch_line> hr_empconbatch_lines;
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hr_empconbatch() throws Exception {
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
