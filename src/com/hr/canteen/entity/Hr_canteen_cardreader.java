package com.hr.canteen.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.canteen.ctr.CtrHr_canteen_cardreader;

import java.sql.Types;

@CEntity(caption="卡机设置",controller =CtrHr_canteen_cardreader.class)
public class Hr_canteen_cardreader extends CJPA {
	@CFieldinfo(fieldname="ctcr_id",iskey=true,notnull=true,caption="卡机ID",datetype=Types.INTEGER)
	public CField ctcr_id;  //卡机ID
	@CFieldinfo(fieldname="ctcr_code",codeid=98,notnull=true,caption="卡机编号",datetype=Types.VARCHAR)
	public CField ctcr_code;  //卡机编号
	@CFieldinfo(fieldname="ctcr_name",notnull=true,caption="名称",datetype=Types.VARCHAR)
	public CField ctcr_name;  //名称
	@CFieldinfo(fieldname="brand",caption="品牌",datetype=Types.VARCHAR)
	public CField brand;  //品牌
	@CFieldinfo(fieldname="model",caption="型号",datetype=Types.VARCHAR)
	public CField model;  //型号
	@CFieldinfo(fieldname="buydate",caption="购置日期",datetype=Types.TIMESTAMP)
	public CField buydate;  //购置日期
	@CFieldinfo(fieldname="ctr_id",notnull=true,caption="配置餐厅ID",datetype=Types.INTEGER)
	public CField ctr_id;  //配置餐厅ID
	@CFieldinfo(fieldname="ctr_code",notnull=true,caption="配置餐厅编码",datetype=Types.VARCHAR)
	public CField ctr_code;  //配置餐厅编码
	@CFieldinfo(fieldname="ctr_name",notnull=true,caption="配置餐厅名称",datetype=Types.VARCHAR)
	public CField ctr_name;  //配置餐厅名称
	@CFieldinfo(fieldname="areaid",caption="区域ID",datetype=Types.INTEGER)
	public CField areaid;  //区域ID
	@CFieldinfo(fieldname="area",caption="配置区域",datetype=Types.VARCHAR)
	public CField area;  //配置区域
	@CFieldinfo(fieldname="numbers",caption="配置数量",datetype=Types.INTEGER)
	public CField numbers;  //配置数量
	@CFieldinfo(fieldname="remark",caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	@CFieldinfo(fieldname="wfid",caption="wfid",datetype=Types.INTEGER)
	public CField wfid;  //wfid
	@CFieldinfo(fieldname="attid",caption="attid",datetype=Types.INTEGER)
	public CField attid;  //attid
	@CFieldinfo(fieldname="stat",notnull=true,caption="流程状态",datetype=Types.INTEGER)
	public CField stat;  //流程状态
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
	@CFieldinfo(fieldname="sync_sn",caption="关联id",datetype=Types.VARCHAR)
	public CField sync_sn;  //关联id
	@CFieldinfo(fieldname="usable",precision=1,scale=0,caption="是否可用",datetype=Types.INTEGER)
	public CField usable;  //是否可用
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hr_canteen_cardreader() throws Exception {
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

