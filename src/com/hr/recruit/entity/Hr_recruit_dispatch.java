package com.hr.recruit.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.recruit.co.COHr_recruit_dispatch;
import com.hr.recruit.co.COHr_recruit_transport;

import java.sql.Types;

@CEntity(controller=COHr_recruit_dispatch.class)
public class Hr_recruit_dispatch extends CJPA {
	@CFieldinfo(fieldname="recruit_dispatch_id",iskey=true,notnull=true,precision=20,scale=0,caption="派遣机构ID",datetype=Types.INTEGER)
	public CField recruit_dispatch_id;  //派遣机构ID
	@CFieldinfo(fieldname="recruit_dispatch_code",precision=133,scale=0,caption="派遣机构编码",datetype=Types.VARCHAR)
	public CField recruit_dispatch_code;  //派遣机构编码
	@CFieldinfo(fieldname="recruit_dispatch_name",precision=341,scale=0,caption="派遣机构名称",datetype=Types.VARCHAR)
	public CField recruit_dispatch_name;  //派遣机构名称
	@CFieldinfo(fieldname="contacts_name",precision=133,scale=0,caption="联系人",datetype=Types.VARCHAR)
	public CField contacts_name;  //联系人
	@CFieldinfo(fieldname="cooperate_begin_date",precision=19,scale=0,caption="合作开始日期",datetype=Types.TIMESTAMP)
	public CField cooperate_begin_date;  //合作开始日期
	@CFieldinfo(fieldname="cooperate_end_date",precision=19,scale=0,caption="合作结束日期",datetype=Types.TIMESTAMP)
	public CField cooperate_end_date;  //合作结束日期
	@CFieldinfo(fieldname="maintenance_date",precision=19,scale=0,caption="维护时间",datetype=Types.TIMESTAMP)
	public CField maintenance_date;  //维护时间
	@CFieldinfo(fieldname="recruit_dispatch_stat",precision=26,scale=0,caption="派遣机构状态",datetype=Types.VARCHAR)
	public CField recruit_dispatch_stat;  //派遣机构状态
	@CFieldinfo(fieldname="remarks",precision=341,scale=0,caption="备注",datetype=Types.VARCHAR)
	public CField remarks;  //备注
	@CFieldinfo(fieldname="wfid",precision=20,scale=0,caption="wfid",datetype=Types.INTEGER)
	public CField wfid;  //wfid
	@CFieldinfo(fieldname="attid",precision=20,scale=0,caption="attid",datetype=Types.INTEGER)
	public CField attid;  //attid
	@CFieldinfo(fieldname="stat",precision=2,scale=0,caption="表单状态",datetype=Types.INTEGER)
	public CField stat;  //表单状态
	@CFieldinfo(fieldname="idpath",precision=341,scale=0,caption="idpath",datetype=Types.VARCHAR)
	public CField idpath;  //idpath
	@CFieldinfo(fieldname="entid",precision=20,scale=0,caption="entid",datetype=Types.INTEGER)
	public CField entid;  //entid
	@CFieldinfo(fieldname="creator",precision=42,scale=0,caption="创建人",datetype=Types.VARCHAR)
	public CField creator;  //创建人
	@CFieldinfo(fieldname="createtime",precision=19,scale=0,caption="创建时间",datetype=Types.TIMESTAMP)
	public CField createtime;  //创建时间
	@CFieldinfo(fieldname="updator",precision=42,scale=0,caption="更新人",datetype=Types.VARCHAR)
	public CField updator;  //更新人
	@CFieldinfo(fieldname="updatetime",precision=19,scale=0,caption="更新时间",datetype=Types.TIMESTAMP)
	public CField updatetime;  //更新时间
	@CFieldinfo(fieldname="attribute1",precision=170,scale=0,caption="attribute1",datetype=Types.VARCHAR)
	public CField attribute1;  //attribute1
	@CFieldinfo(fieldname="attribute2",precision=19,scale=0,caption="attribute2",datetype=Types.TIMESTAMP)
	public CField attribute2;  //attribute2
	@CFieldinfo(fieldname="attribute3",precision=170,scale=0,caption="attribute3",datetype=Types.VARCHAR)
	public CField attribute3;  //attribute3
	@CFieldinfo(fieldname="attribute4",precision=170,scale=0,caption="attribute4",datetype=Types.VARCHAR)
	public CField attribute4;  //attribute4
	@CFieldinfo(fieldname="attribute5",precision=170,scale=0,caption="attribute5",datetype=Types.VARCHAR)
	public CField attribute5;  //attribute5
	@CFieldinfo(fieldname="contacts_phone",precision=85,scale=0,caption="联系电话",datetype=Types.VARCHAR)
	public CField contacts_phone;  //联系电话
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

	// 自关联数据定义
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;


	public Hr_recruit_dispatch() throws Exception {
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

