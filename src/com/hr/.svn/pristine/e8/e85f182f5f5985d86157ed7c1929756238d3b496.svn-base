package com.hr.access.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.access.co.COHr_access_card;

import java.sql.Types;

@CEntity(controller=COHr_access_card.class)
public class Hr_access_card extends CJPA {
	@CFieldinfo(fieldname="access_card_id",iskey=true,notnull=true,caption="门禁卡ID",datetype=Types.INTEGER)
	public CField access_card_id;  //门禁卡ID
	@CFieldinfo(fieldname="access_card_seq",caption="卡序列号",datetype=Types.VARCHAR)
	public CField access_card_seq;  //卡序列号
	@CFieldinfo(fieldname="access_card_number",caption="卡号",datetype=Types.VARCHAR)
	public CField access_card_number;  //卡号
	@CFieldinfo(fieldname="employee_code",caption="工号",datetype=Types.VARCHAR)
	public CField employee_code;  //工号
	@CFieldinfo(fieldname="employee_name",caption="员工姓名",datetype=Types.VARCHAR)
	public CField employee_name;  //员工姓名
	@CFieldinfo(fieldname="sex",caption="性别",datetype=Types.INTEGER)
	public CField sex;  //性别
	@CFieldinfo(fieldname="hiredday",caption="入职日期",datetype=Types.TIMESTAMP)
	public CField hiredday;  //入职日期
	@CFieldinfo(fieldname="orgid",caption="机构ID",datetype=Types.INTEGER)
	public CField orgid;  //机构ID
	@CFieldinfo(fieldname="orgname",caption="机构名称",datetype=Types.VARCHAR)
	public CField orgname;  //机构名称
	@CFieldinfo(fieldname="orgcode",caption="机构编码",datetype=Types.VARCHAR)
	public CField orgcode;  //机构编码
	@CFieldinfo(fieldname="extorgname",caption="机构全称",datetype=Types.VARCHAR)
	public CField extorgname;  //机构全称
	@CFieldinfo(fieldname="hwc_namezl",caption="职类",datetype=Types.VARCHAR)
	public CField hwc_namezl;  //职类
	@CFieldinfo(fieldname="lv_id",caption="职级ID",datetype=Types.INTEGER)
	public CField lv_id;  //职级ID
	@CFieldinfo(fieldname="lv_num",caption="职级",datetype=Types.DECIMAL)
	public CField lv_num;  //职级
	@CFieldinfo(fieldname = "ospid", caption = "职位ID", datetype = Types.INTEGER)
	public CField ospid; // 职位ID
	@CFieldinfo(fieldname = "ospcode", caption = "职位编码", datetype = Types.VARCHAR)
	public CField ospcode; // 职位编码
	@CFieldinfo(fieldname = "sp_name", caption = "职位名称", datetype = Types.VARCHAR)
	public CField sp_name; // 职位名称
	@CFieldinfo(fieldname="publish_date",caption="发卡时间",datetype=Types.TIMESTAMP)
	public CField publish_date;  //发卡时间
	@CFieldinfo(fieldname = "pass_door", caption = "可通行门", datetype = Types.VARCHAR)
	public CField pass_door; // 职位名称
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

	// 自关联数据定义
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;


	public Hr_access_card() throws Exception {
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

