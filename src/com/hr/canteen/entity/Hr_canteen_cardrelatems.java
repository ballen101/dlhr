package com.hr.canteen.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import com.hr.canteen.ctr.CtrHr_canteen_cardrelatems;

import java.sql.Types;

@CEntity(caption="卡机餐类餐制关联表",controller =CtrHr_canteen_cardrelatems.class)
public class Hr_canteen_cardrelatems extends CJPA {
	@CFieldinfo(fieldname="crms_id",iskey=true,notnull=true,precision=20,scale=0,caption="卡机关联餐制id",datetype=Types.INTEGER)
	public CField crms_id;  //卡机关联餐制id
	@CFieldinfo(fieldname="ctcr_id",notnull=true,precision=20,scale=0,caption="卡机ID",datetype=Types.INTEGER)
	public CField ctcr_id;  //卡机ID
	@CFieldinfo(fieldname="ctcr_code",notnull=true,precision=16,scale=0,caption="卡机编号",datetype=Types.VARCHAR)
	public CField ctcr_code;  //卡机编号
	@CFieldinfo(fieldname="ctcr_name",notnull=true,precision=64,scale=0,caption="卡机名称",datetype=Types.VARCHAR)
	public CField ctcr_name;  //卡机名称
	@CFieldinfo(fieldname="ctr_id",precision=20,scale=0,caption="配置餐厅ID",datetype=Types.INTEGER)
	public CField ctr_id;  //配置餐厅ID
	@CFieldinfo(fieldname="ctr_code",precision=16,scale=0,caption="配置餐厅编码",datetype=Types.VARCHAR)
	public CField ctr_code;  //配置餐厅编码
	@CFieldinfo(fieldname="ctr_name",precision=64,scale=0,caption="配置餐厅名称",datetype=Types.VARCHAR)
	public CField ctr_name;  //配置餐厅名称
	@CFieldinfo(fieldname="mc_id",notnull=true,precision=20,scale=0,caption="餐类ID",datetype=Types.INTEGER)
	public CField mc_id;  //餐类ID
	@CFieldinfo(fieldname="mc_name",notnull=true,precision=64,scale=0,caption="餐类名称",datetype=Types.VARCHAR)
	public CField mc_name;  //餐类名称
	@CFieldinfo(fieldname="mealbegin",precision=64,scale=0,caption="用餐开始时间",datetype=Types.VARCHAR)
	public CField mealbegin;  //用餐开始时间
	@CFieldinfo(fieldname="mealend",precision=64,scale=0,caption="用餐结束时间",datetype=Types.VARCHAR)
	public CField mealend;  //用餐结束时间
	@CFieldinfo(fieldname="ctms_id",precision=20,scale=0,caption="餐制ID",datetype=Types.INTEGER)
	public CField ctms_id;  //餐制ID
	@CFieldinfo(fieldname="ctms_code",precision=16,scale=0,caption="餐制编码",datetype=Types.VARCHAR)
	public CField ctms_code;  //餐制编码
	@CFieldinfo(fieldname="ctms_name",precision=64,scale=0,caption="餐制名称",datetype=Types.VARCHAR)
	public CField ctms_name;  //餐制名称
	@CFieldinfo(fieldname="price",precision=4,scale=1,caption="餐标",datetype=Types.DECIMAL)
	public CField price;  //餐标
	@CFieldinfo(fieldname="subsidies",precision=4,scale=1,caption="补贴金额",datetype=Types.DECIMAL)
	public CField subsidies;  //补贴金额
	@CFieldinfo(fieldname="remark",precision=512,scale=0,caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	@CFieldinfo(fieldname="usable",precision=1,scale=0,caption="usable",defvalue="1",datetype=Types.INTEGER)
	public CField usable;  //usable
	@CFieldinfo(fieldname="creator",notnull=true,precision=32,scale=0,caption="创建人",datetype=Types.VARCHAR)
	public CField creator;  //创建人
	@CFieldinfo(fieldname="createtime",notnull=true,precision=19,scale=0,caption="创建时间",datetype=Types.TIMESTAMP)
	public CField createtime;  //创建时间
	@CFieldinfo(fieldname="updator",precision=32,scale=0,caption="更新人",datetype=Types.VARCHAR)
	public CField updator;  //更新人
	@CFieldinfo(fieldname="updatetime",precision=19,scale=0,caption="更新时间",datetype=Types.TIMESTAMP)
	public CField updatetime;  //更新时间
	@CFieldinfo(fieldname="attribute1",precision=32,scale=0,caption="备用字段1",datetype=Types.VARCHAR)
	public CField attribute1;  //备用字段1
	@CFieldinfo(fieldname="attribute2",precision=32,scale=0,caption="备用字段2",datetype=Types.VARCHAR)
	public CField attribute2;  //备用字段2
	@CFieldinfo(fieldname="attribute3",precision=32,scale=0,caption="备用字段3",datetype=Types.VARCHAR)
	public CField attribute3;  //备用字段3
	@CFieldinfo(fieldname="attribute4",precision=32,scale=0,caption="备用字段4",datetype=Types.VARCHAR)
	public CField attribute4;  //备用字段4
	@CFieldinfo(fieldname="attribute5",precision=32,scale=0,caption="备用字段5",datetype=Types.VARCHAR)
	public CField attribute5;  //备用字段5
	@CFieldinfo(fieldname="emplev",caption="人事层级",datetype=Types.INTEGER)
	public CField emplev;  //人事层级
	@CFieldinfo(fieldname ="classtype",precision=1,scale=0,caption="餐类类型",datetype=Types.INTEGER)
	public CField classtype;  //餐类类型
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hr_canteen_cardrelatems() throws Exception {
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

