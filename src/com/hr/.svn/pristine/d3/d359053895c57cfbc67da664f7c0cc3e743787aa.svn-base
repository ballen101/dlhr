package com.hr.access.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.access.co.COHr_access_orauthority;
import com.hr.access.co.COHr_access_special;

import java.sql.Types;

@CEntity(controller = COHr_access_orauthority.class)
public class Hr_access_orauthority extends CJPA {
	@CFieldinfo(fieldname="access_orauthority_id",iskey=true,notnull=true,caption="机构门禁权限ID",datetype=Types.INTEGER)
	public CField access_orauthority_id;  //机构门禁权限ID
	@CFieldinfo(fieldname="orgid",caption="机构ID",datetype=Types.INTEGER)
	public CField orgid;  //机构ID
	@CFieldinfo(fieldname="orgname",caption="机构名称",datetype=Types.VARCHAR)
	public CField orgname;  //机构名称
	@CFieldinfo(fieldname="orgcode",caption="机构编码",datetype=Types.VARCHAR)
	public CField orgcode;  //机构编码
	@CFieldinfo(fieldname="extorgname",caption="机构全称",datetype=Types.VARCHAR)
	public CField extorgname;  //机构全称
	@CFieldinfo(fieldname="access_list_id",caption="门禁ID",datetype=Types.INTEGER)
	public CField access_list_id;  //门禁ID
	@CFieldinfo(fieldname="access_list_code",caption="门禁编码",datetype=Types.VARCHAR)
	public CField access_list_code;  //门禁编码
	@CFieldinfo(fieldname="access_list_model",caption="门禁型号",datetype=Types.VARCHAR)
	public CField access_list_model;  //门禁型号
	@CFieldinfo(fieldname="access_list_name",caption="门禁名称",datetype=Types.VARCHAR)
	public CField access_list_name;  //门禁名称
	@CFieldinfo(fieldname="deploy_area",caption="配置区域",datetype=Types.VARCHAR)
	public CField deploy_area;  //配置区域
	@CFieldinfo(fieldname="access_place",caption="门禁位置",datetype=Types.VARCHAR)
	public CField access_place;  //门禁位置
	@CFieldinfo(fieldname="remarks",caption="备注",datetype=Types.VARCHAR)
	public CField remarks;  //备注
	@CFieldinfo(fieldname="wfid",caption="wfid",datetype=Types.INTEGER)
	public CField wfid;  //wfid
	@CFieldinfo(fieldname="attid",caption="attid",datetype=Types.INTEGER)
	public CField attid;  //attid
	@CFieldinfo(fieldname="stat",caption="表单状态",datetype=Types.INTEGER)
	public CField stat;  //表单状态
	@CFieldinfo(fieldname="idpath",caption="idpath",datetype=Types.VARCHAR)
	public CField idpath;  //idpath
	@CFieldinfo(fieldname="entid",caption="entid",datetype=Types.INTEGER)
	public CField entid;  //entid
	@CFieldinfo(fieldname="creator",caption="创建人",datetype=Types.VARCHAR)
	public CField creator;  //创建人
	@CFieldinfo(fieldname="createtime",caption="创建时间",datetype=Types.TIMESTAMP)
	public CField createtime;  //创建时间
	@CFieldinfo(fieldname="updator",caption="更新人",datetype=Types.VARCHAR)
	public CField updator;  //更新人
	@CFieldinfo(fieldname="updatetime",caption="更新时间",datetype=Types.TIMESTAMP)
	public CField updatetime;  //更新时间
	@CFieldinfo(fieldname="attribute1",caption="attribute1",datetype=Types.VARCHAR)
	public CField attribute1;  //attribute1
	@CFieldinfo(fieldname="attribute2",caption="attribute2",datetype=Types.TIMESTAMP)
	public CField attribute2;  //attribute2
	@CFieldinfo(fieldname="attribute3",caption="attribute3",datetype=Types.VARCHAR)
	public CField attribute3;  //attribute3
	@CFieldinfo(fieldname="attribute4",caption="attribute4",datetype=Types.VARCHAR)
	public CField attribute4;  //attribute4
	@CFieldinfo(fieldname="attribute5",caption="attribute5",datetype=Types.VARCHAR)
	public CField attribute5;  //attribute5
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hr_access_orauthority() throws Exception {
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

