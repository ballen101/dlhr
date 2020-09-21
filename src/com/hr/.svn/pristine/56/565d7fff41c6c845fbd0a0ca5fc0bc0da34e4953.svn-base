package com.hr.asset.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.asset.co.COHr_asset_register;

import java.sql.Types;

@CEntity(controller=COHr_asset_register.class)
public class Hr_asset_register extends CJPA {
	@CFieldinfo(fieldname="asset_register_id",iskey = true, notnull=true,caption="登记号ID",datetype=Types.INTEGER)
	public CField asset_register_id;  //asset_register_id
	@CFieldinfo(fieldname="asset_register_num",codeid=94,caption="登记号",datetype=Types.VARCHAR)
	public CField asset_register_num;  //asset_register_num
	@CFieldinfo(fieldname="asset_item_id",caption="资产物料ID",datetype=Types.INTEGER)
	public CField asset_item_id;  //asset_item_id
	@CFieldinfo(fieldname="asset_item_code",caption="物料编码",datetype=Types.VARCHAR)
	public CField asset_item_code;  //asset_item_code
	@CFieldinfo(fieldname="asset_item_name",caption="物料名称",datetype=Types.VARCHAR)
	public CField asset_item_name;  //asset_item_name
	@CFieldinfo(fieldname="asset_type_id",caption="类型ID",datetype=Types.INTEGER)
	public CField asset_type_id;  //asset_type_id
	@CFieldinfo(fieldname="asset_type_code",caption="类型编码",datetype=Types.VARCHAR)
	public CField asset_type_code;  //asset_type_code
	@CFieldinfo(fieldname="asset_type_name",caption="类型名称",datetype=Types.VARCHAR)
	public CField asset_type_name;  //asset_type_name
	@CFieldinfo(fieldname="brand",caption="品牌",datetype=Types.VARCHAR)
	public CField brand;  //brand
	@CFieldinfo(fieldname="model",caption="型号",datetype=Types.VARCHAR)
	public CField model;  //model
	@CFieldinfo(fieldname="original_value",caption="原值",datetype=Types.VARCHAR)
	public CField original_value;  //original_value
	@CFieldinfo(fieldname="net_value",caption="净值",datetype=Types.VARCHAR)
	public CField net_value;  //net_value
	@CFieldinfo(fieldname="uom",caption="单位",datetype=Types.VARCHAR)
	public CField uom;  //uom
	@CFieldinfo(fieldname="service_life",caption="报废年限",datetype=Types.INTEGER)
	public CField service_life;  //service_life
	@CFieldinfo(fieldname="acquired_date",caption="购置日期",datetype=Types.TIMESTAMP)
	public CField acquired_date;  //acquired_date
	@CFieldinfo(fieldname="deploy_qty",caption="配置数量",datetype=Types.INTEGER)
	public CField deploy_qty;  //deploy_qty
	@CFieldinfo(fieldname="deploy_area",caption="配置区域",datetype=Types.VARCHAR)
	public CField deploy_area;  //deploy_area
	@CFieldinfo(fieldname="deploy_restaurant",caption="配置餐厅",datetype=Types.VARCHAR)
	public CField deploy_restaurant;  //deploy_restaurant
	@CFieldinfo(fieldname="deploy_restaurant_id",caption="配置餐厅id",datetype=Types.INTEGER)
	public CField deploy_restaurant_id;  //deploy_restaurant_id
	@CFieldinfo(fieldname="keep_own",caption="保管人",datetype=Types.VARCHAR)
	public CField keep_own;  //keep_own
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

	public Hr_asset_register() throws Exception {
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

