package com.hr.asset.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import com.hr.asset.co.COHr_asset_statement;

import java.sql.Types;

@CEntity(controller=COHr_asset_statement.class)
public class Hr_asset_statement extends CJPA {
	@CFieldinfo(fieldname="asset_statement_id",iskey=true,notnull=true,caption="资产流水id",datetype=Types.INTEGER)
	public CField asset_statement_id;  //asset_statement_id
	@CFieldinfo(fieldname="source",caption="来源模块",datetype=Types.VARCHAR)
	public CField source;  //source
	@CFieldinfo(fieldname="source_id",caption="来源单据id",datetype=Types.INTEGER)
	public CField source_id;  //source_id
	@CFieldinfo(fieldname="source_num",caption="来源单号",datetype=Types.VARCHAR)
	public CField source_num;  //source_num
	@CFieldinfo(fieldname="asset_item_id",caption="物料id",datetype=Types.INTEGER)
	public CField asset_item_id;  //asset_item_id
	@CFieldinfo(fieldname="asset_item_code",caption="物料编码",datetype=Types.VARCHAR)
	public CField asset_item_code;  //asset_item_code
	@CFieldinfo(fieldname="asset_item_name",caption="物料名称",datetype=Types.VARCHAR)
	public CField asset_item_name;  //asset_item_name
	@CFieldinfo(fieldname="asset_type_id",caption="类型id",datetype=Types.INTEGER)
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
	@CFieldinfo(fieldname="acquired_date",caption="配置日期",datetype=Types.TIMESTAMP)
	public CField acquired_date;  //acquired_date
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
	@CFieldinfo(fieldname="creator",caption="创建人",datetype=Types.VARCHAR)
	public CField creator;  //creator
	@CFieldinfo(fieldname="createtime",caption="创建日期",datetype=Types.TIMESTAMP)
	public CField createtime;  //createtime
	@CFieldinfo(fieldname="updator",caption="修改人",datetype=Types.VARCHAR)
	public CField updator;  //updator
	@CFieldinfo(fieldname="updatetime",caption="修改日期",datetype=Types.TIMESTAMP)
	public CField updatetime;  //updatetime
	@CFieldinfo(fieldname="adjust_qty",caption="调整数量",datetype=Types.INTEGER)
	public CField adjust_qty;  //adjust_qty
	@CFieldinfo(fieldname="adjust_date",caption="调整日期",datetype=Types.TIMESTAMP)
	public CField adjust_date;  //adjust_date
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


	public Hr_asset_statement() throws Exception {
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

