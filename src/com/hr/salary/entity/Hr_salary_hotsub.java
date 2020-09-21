package com.hr.salary.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.salary.ctr.CtrHr_salary_hotsub;

import java.sql.Types;

@CEntity(caption="高温补贴申请",tablename="hr_salary_hotsub",controller=CtrHr_salary_hotsub.class)
public class Hr_salary_hotsub extends CJPA {
	@CFieldinfo(fieldname="hs_id",iskey=true,notnull=true,precision=20,scale=0,caption="高温补贴申请ID",datetype=Types.INTEGER)
	public CField hs_id;  //高温补贴申请ID
	@CFieldinfo(fieldname="hs_code",notnull=true,codeid=127,precision=16,scale=0,caption="高温补贴申请编码",datetype=Types.VARCHAR)
	public CField hs_code;  //高温补贴申请编码
	@CFieldinfo(fieldname="orgid",notnull=true,precision=10,scale=0,caption="部门ID",datetype=Types.INTEGER)
	public CField orgid;  //部门ID
	@CFieldinfo(fieldname="orgcode",notnull=true,precision=16,scale=0,caption="部门编码",datetype=Types.VARCHAR)
	public CField orgcode;  //部门编码
	@CFieldinfo(fieldname="orgname",notnull=true,precision=128,scale=0,caption="部门名称",datetype=Types.VARCHAR)
	public CField orgname;  //部门名称
	@CFieldinfo(fieldname="substype",notnull=true,precision=1,scale=0,caption="津贴类型",defvalue="3",datetype=Types.INTEGER)
	public CField substype;  //津贴类型
	@CFieldinfo(fieldname="applydate",precision=19,scale=0,caption="申请月份",datetype=Types.TIMESTAMP)
	public CField applydate;  //申请月份
	@CFieldinfo(fieldname="sp_id",precision=20,scale=0,caption="职位ID",datetype=Types.INTEGER)
	public CField sp_id;  //职位ID
	@CFieldinfo(fieldname="sp_code",precision=16,scale=0,caption="职位编码",datetype=Types.VARCHAR)
	public CField sp_code;  //职位编码
	@CFieldinfo(fieldname="sp_name",precision=128,scale=0,caption="标准职位名称",datetype=Types.VARCHAR)
	public CField sp_name;  //标准职位名称
	@CFieldinfo(fieldname="remark",precision=512,scale=0,caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	@CFieldinfo(fieldname="wfid",precision=20,scale=0,caption="wfid",datetype=Types.INTEGER)
	public CField wfid;  //wfid
	@CFieldinfo(fieldname="attid",precision=20,scale=0,caption="attid",datetype=Types.INTEGER)
	public CField attid;  //attid
	@CFieldinfo(fieldname="stat",notnull=true,precision=2,scale=0,caption="流程状态",datetype=Types.INTEGER)
	public CField stat;  //流程状态
	@CFieldinfo(fieldname="entid",notnull=true,precision=5,scale=0,caption="entid",datetype=Types.INTEGER)
	public CField entid;  //entid
	@CFieldinfo(fieldname="idpath",notnull=true,precision=256,scale=0,caption="idpath",datetype=Types.VARCHAR)
	public CField idpath;  //idpath
	@CFieldinfo(fieldname="creator",notnull=true,precision=32,scale=0,caption="创建人",datetype=Types.VARCHAR)
	public CField creator;  //创建人
	@CFieldinfo(fieldname="createtime",notnull=true,precision=19,scale=0,caption="创建时间",datetype=Types.TIMESTAMP)
	public CField createtime;  //创建时间
	@CFieldinfo(fieldname="updator",precision=32,scale=0,caption="更新人",datetype=Types.VARCHAR)
	public CField updator;  //更新人
	@CFieldinfo(fieldname="updatetime",precision=19,scale=0,caption="更新时间",datetype=Types.TIMESTAMP)
	public CField updatetime;  //更新时间
	@CFieldinfo(fieldname="property1",precision=32,scale=0,caption="备用字段1",datetype=Types.VARCHAR)
	public CField property1;  //备用字段1
	@CFieldinfo(fieldname="property2",precision=64,scale=0,caption="备用字段2",datetype=Types.VARCHAR)
	public CField property2;  //备用字段2
	@CFieldinfo(fieldname="property3",precision=32,scale=0,caption="备用字段3",datetype=Types.VARCHAR)
	public CField property3;  //备用字段3
	@CFieldinfo(fieldname="property4",precision=32,scale=0,caption="备用字段4",datetype=Types.VARCHAR)
	public CField property4;  //备用字段4
	@CFieldinfo(fieldname="property5",precision=32,scale=0,caption="备用字段5",datetype=Types.VARCHAR)
	public CField property5;  //备用字段5
	@CFieldinfo(fieldname="usable",notnull=true,precision=1,scale=0,caption="有效",defvalue="1",datetype=Types.INTEGER)
	public CField usable;  //有效
	@CFieldinfo(fieldname="orghrlev",caption="机构人事层级",datetype=Types.INTEGER)
	public CField orghrlev;  //机构人事层级
    @CFieldinfo(fieldname = "emplev", caption = "人事层级", datetype = Types.INTEGER)
	public CField emplev; // 人事层级
    @CFieldinfo(fieldname = "isqual", caption = "是否资格内", datetype = Types.INTEGER)
    public CField isqual; // 是否资格内
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;
	@CLinkFieldInfo(jpaclass = Hr_salary_hotsub_line.class, linkFields = { @LinkFieldItem(lfield = "hs_id", mfield = "hs_id") })
	public CJPALineData<Hr_salary_hotsub_line> hr_salary_hotsub_lines;

	public Hr_salary_hotsub() throws Exception {
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

