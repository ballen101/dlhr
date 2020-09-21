package com.hr.insurance.entity;

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
public class Hr_ins_buyins extends CJPA {
	@CFieldinfo(fieldname="insbuy_id",iskey=true,notnull=true,caption="保险购买ID",datetype=Types.INTEGER)
	public CField insbuy_id;  //保险购买ID
	@CFieldinfo(fieldname="insbuy_code",codeid=104,notnull=true,caption="保险购买编码",datetype=Types.VARCHAR)
	public CField insbuy_code;  //保险购买编码
	@CFieldinfo(fieldname="buydday",caption="购买年月",datetype=Types.TIMESTAMP)
	public CField buydday;  //购买年月
	@CFieldinfo(fieldname="orgid",notnull=true,caption="部门ID",datetype=Types.INTEGER)
	public CField orgid;  //部门ID
	@CFieldinfo(fieldname="orgcode",notnull=true,caption="部门编码",datetype=Types.VARCHAR)
	public CField orgcode;  //部门编码
	@CFieldinfo(fieldname="orgname",notnull=true,caption="部门名称",datetype=Types.VARCHAR)
	public CField orgname;  //部门名称
	@CFieldinfo(fieldname="payment",caption="缴费总金额",datetype=Types.DECIMAL)
	public CField payment;  //缴费总金额
	@CFieldinfo(fieldname="tselfpay",caption="个人承担金额",datetype=Types.DECIMAL)
	public CField tselfpay;  //个人承担金额
	@CFieldinfo(fieldname="tcompay",caption="公司承担金额",datetype=Types.DECIMAL)
	public CField tcompay;  //公司承担金额
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
	@CFieldinfo(fieldname="ins_type",caption="险种类型",datetype=Types.INTEGER)
	public CField ins_type;  //险种类型
	@CFieldinfo(fieldname="insit_id",caption="险种ID",datetype=Types.INTEGER)
	public CField insit_id;  //险种ID
	@CFieldinfo(fieldname="insit_code",caption="险种编码",datetype=Types.VARCHAR)
	public CField insit_code;  //险种编码
	@CFieldinfo(fieldname="insname",caption="险种名称",datetype=Types.VARCHAR)
	public CField insname;  //险种名称
	@CFieldinfo(fieldname="insurancebase",caption="缴费基数",datetype=Types.DECIMAL)
	public CField insurancebase;  //缴费基数
	@CFieldinfo(fieldname="selfratio",caption="个人缴费系数%",datetype=Types.DECIMAL)
	public CField selfratio;  //个人缴费系数%
	@CFieldinfo(fieldname="selfpay",caption="个人缴费金额",datetype=Types.DECIMAL)
	public CField selfpay;  //个人缴费金额
	@CFieldinfo(fieldname="comratio",caption="公司缴费系数%",datetype=Types.DECIMAL)
	public CField comratio;  //公司缴费系数%
	@CFieldinfo(fieldname="compay",caption="公司缴费金额",datetype=Types.DECIMAL)
	public CField compay;  //公司缴费金额
	@CFieldinfo(fieldname="insurance_number",caption="参保号",datetype=Types.VARCHAR)
	public CField insurance_number;  //参保号
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;
	@CLinkFieldInfo(jpaclass = Hr_ins_buyins_line.class, linkFields = { @LinkFieldItem(lfield = "insbuy_id", mfield = "insbuy_id") })
	public CJPALineData<Hr_ins_buyins_line> hr_ins_buyins_lines;

	public Hr_ins_buyins() throws Exception {
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

