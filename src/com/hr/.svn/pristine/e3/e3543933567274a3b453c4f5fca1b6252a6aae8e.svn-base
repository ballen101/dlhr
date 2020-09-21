package com.hr.asset.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.asset.co.COHr_asset_allot;
import com.hr.asset.co.COHr_asset_reject;

import java.sql.Types;

@CEntity(controller=COHr_asset_reject.class)
public class Hr_asset_reject_h extends CJPA {
	@CFieldinfo(fieldname="asset_reject_id",iskey=true,notnull=true,caption="报废单ID",datetype=Types.INTEGER)
	public CField asset_reject_id;  //asset_reject_id
	@CFieldinfo(fieldname="asset_reject_num",codeid=96,caption="报废单号",datetype=Types.VARCHAR)
	public CField asset_reject_num;  //asset_reject_num
	@CFieldinfo(fieldname="deploy_area",caption="所属区域",datetype=Types.VARCHAR)
	public CField deploy_area;  //deploy_area
	@CFieldinfo(fieldname="deploy_restaurant",caption="所属餐厅",datetype=Types.VARCHAR)
	public CField deploy_restaurant;  //deploy_restaurant
	@CFieldinfo(fieldname="deploy_restaurant_id",caption="所属餐厅ID",datetype=Types.INTEGER)
	public CField deploy_restaurant_id;  //deploy_restaurant_id
	@CFieldinfo(fieldname="remark",caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //remark
	@CFieldinfo(fieldname="wfid",caption="wfid",datetype=Types.INTEGER)
	public CField wfid;  //wfid
	@CFieldinfo(fieldname="attid",caption="attid",datetype=Types.INTEGER)
	public CField attid;  //attid
	@CFieldinfo(fieldname="stat",caption="表单状态",datetype=Types.INTEGER)
	public CField stat;  //stat
	@CFieldinfo(fieldname="idpath",caption="idpath",datetype=Types.VARCHAR)
	public CField idpath;  //idpath
	@CFieldinfo(fieldname="entid",caption="entid",datetype=Types.INTEGER)
	public CField entid;  //entid
	@CFieldinfo(fieldname="creator",caption="创建人",datetype=Types.VARCHAR)
	public CField creator;  //creator
	@CFieldinfo(fieldname="createtime",caption="创建时间",datetype=Types.TIMESTAMP)
	public CField createtime;  //createtime
	@CFieldinfo(fieldname="updator",caption="更新人",datetype=Types.VARCHAR)
	public CField updator;  //updator
	@CFieldinfo(fieldname="updatetime",caption="更新时间",datetype=Types.TIMESTAMP)
	public CField updatetime;  //updatetime
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
	@CLinkFieldInfo(jpaclass = Hr_asset_reject_d.class, linkFields = { @LinkFieldItem(lfield = "asset_reject_id", mfield = "asset_reject_id") })
	public CJPALineData<Hr_asset_reject_d> hr_asset_reject_ds;

	public Hr_asset_reject_h() throws Exception {
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

