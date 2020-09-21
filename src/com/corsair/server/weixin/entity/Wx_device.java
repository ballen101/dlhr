package com.corsair.server.weixin.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Wx_device extends CJPA {
	@CFieldinfo(fieldname = "did", iskey = true, notnull = true, caption = "", datetype = Types.INTEGER)
	public CField did; //
	@CFieldinfo(fieldname = "appid", notnull = true, caption = "", datetype = Types.VARCHAR)
	public CField appid; //
	@CFieldinfo(fieldname = "device_id", notnull = true, caption = "", datetype = Types.VARCHAR)
	public CField device_id; //
	@CFieldinfo(fieldname = "device_type", caption = "1:WXDevice 2:DQDevice", datetype = Types.INTEGER)
	public CField device_type; // 1:WXDevice 2:DQDevice 5：咖啡机
	@CFieldinfo(fieldname = "ddversion", caption = "版本", datetype = Types.FLOAT)
	public CField ddversion; // 版本
	@CFieldinfo(fieldname = "qrticket", caption = "", datetype = Types.VARCHAR)
	public CField qrticket; //
	@CFieldinfo(fieldname = "item_id", caption = "", datetype = Types.FLOAT)
	public CField item_id; //
	@CFieldinfo(fieldname = "item_code", caption = "编码", datetype = Types.VARCHAR)
	public CField item_code; // 编码
	@CFieldinfo(fieldname = "item_name", caption = "名称", datetype = Types.VARCHAR)
	public CField item_name; // 名称
	@CFieldinfo(fieldname = "item_short_name", caption = "型号", datetype = Types.VARCHAR)
	public CField item_short_name; // 型号
	@CFieldinfo(fieldname = "spec", caption = "规格", datetype = Types.VARCHAR)
	public CField spec; // 规格
	@CFieldinfo(fieldname = "creator", caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "create_time", caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField create_time; // 创建时间
	@CFieldinfo(fieldname = "stat", caption = "状态", datetype = Types.INTEGER)
	public CField stat; // 状态
	@CFieldinfo(fieldname = "updator", caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "update_time", caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField update_time; // 更新时间
	@CFieldinfo(fieldname = "property1", caption = "", datetype = Types.VARCHAR)
	public CField property1; //
	@CFieldinfo(fieldname = "property2", caption = "", datetype = Types.VARCHAR)
	public CField property2; //
	@CFieldinfo(fieldname = "property3", caption = "", datetype = Types.VARCHAR)
	public CField property3; //
	@CFieldinfo(fieldname = "property4", caption = "", datetype = Types.VARCHAR)
	public CField property4; //
	@CFieldinfo(fieldname = "property5", caption = "", datetype = Types.VARCHAR)
	public CField property5; //
	@CFieldinfo(fieldname = "entid", notnull = true, caption = "", datetype = Types.INTEGER)
	public CField entid; //
	@CFieldinfo(fieldname = "entname", caption = "组织名称", datetype = Types.VARCHAR)
	public CField entname; // 组织名称
	@CFieldinfo(fieldname = "dvnkname", caption = "设备昵称", datetype = Types.VARCHAR)
	public CField dvnkname; // 设备昵称
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Wx_device() throws Exception {
	}

	@Override
	public boolean InitObject() {// 类初始化调用的方法
		super.InitObject();
		return true;
	}

	@Override
	public boolean FinalObject() { // 类释放前调用的方法
		super.FinalObject();
		return true;
	}
}