package com.hr.portals.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import com.hr.access.co.COHr_access_card;
import com.hr.portals.co.COHr_portals_config;

import java.sql.Types;

@CEntity(controller=COHr_portals_config.class)
public class Hr_portals_config extends CJPA {
	@CFieldinfo(fieldname="portals_config_id",iskey=true,notnull=true,precision=20,scale=0,caption="配置项ID",datetype=Types.INTEGER)
	public CField portals_config_id;  //配置项ID
	@CFieldinfo(fieldname="config_name",precision=133,scale=0,caption="配置项名称",datetype=Types.VARCHAR)
	public CField config_name;  //配置项名称
	@CFieldinfo(fieldname="menu_id",precision=20,scale=0,caption="菜单ID",datetype=Types.INTEGER)
	public CField menu_id;  //菜单ID
	@CFieldinfo(fieldname="menu_name",precision=133,scale=0,caption="菜单名",datetype=Types.VARCHAR)
	public CField menu_name;  //菜单名
	@CFieldinfo(fieldname="config_type",precision=133,scale=0,caption="类型",datetype=Types.VARCHAR)
	public CField config_type;  //类型
	@CFieldinfo(fieldname="url",precision=133,scale=0,caption="URL链接",datetype=Types.VARCHAR)
	public CField url;  //URL链接
	@CFieldinfo(fieldname="useable",precision=13,scale=0,caption="是否可用",datetype=Types.VARCHAR)
	public CField useable;  //是否可用
	@CFieldinfo(fieldname="remarks",precision=341,scale=0,caption="备注",datetype=Types.VARCHAR)
	public CField remarks;  //备注
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
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hr_portals_config() throws Exception {
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

