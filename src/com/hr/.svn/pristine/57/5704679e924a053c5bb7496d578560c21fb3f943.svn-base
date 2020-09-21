package com.hr.asset.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.asset.co.COHr_asset_type;

import java.sql.Types;

@CEntity(controller=COHr_asset_type.class)//绑定控制器
public class Hr_asset_type extends CJPA {
	@CFieldinfo(fieldname="asset_type_id", iskey = true, notnull = true, caption="类型ID",datetype=Types.INTEGER)
	public CField asset_type_id;  //asset_type_id
	@CFieldinfo(fieldname="asset_type_code",codeid=106,caption="类型编码",datetype=Types.VARCHAR)
	public CField asset_type_code;  //asset_type_code
	@CFieldinfo(fieldname="asset_type_name",caption="类型名称",datetype=Types.VARCHAR)
	public CField asset_type_name;  //asset_type_name
	@CFieldinfo(fieldname="remarks",caption="备注",datetype=Types.VARCHAR)
	public CField remarks;  //remarks
	@CFieldinfo(fieldname="effective_flag",caption="生效标识",datetype=Types.VARCHAR)
	public CField effective_flag;  //effective_flag
	@CFieldinfo(fieldname="effective_date",caption="生效日期",datetype=Types.TIMESTAMP)
	public CField effective_date;  //effective_date
	@CFieldinfo(fieldname="invalidation_date",caption="失效日期",datetype=Types.TIMESTAMP)
	public CField invalidation_date;  //invalidation_date
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
	@CFieldinfo(fieldname = "creator", notnull = true, caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", notnull = true, caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
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

	public Hr_asset_type() throws Exception {
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
