package com.corsair.server.weixin.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Wx_device_binding extends CJPA {
	@CFieldinfo(fieldname = "dbdid", iskey = true, notnull = true, caption = "", datetype = Types.INTEGER)
	public CField dbdid; //
	@CFieldinfo(fieldname = "did", notnull = true, caption = "", datetype = Types.INTEGER)
	public CField did; //
	@CFieldinfo(fieldname = "device_id", notnull = true, caption = "设备ID", datetype = Types.VARCHAR)
	public CField device_id; // 设备ID
	@CFieldinfo(fieldname = "device_type", caption = "1:WXDevice 2:DQDevice", datetype = Types.INTEGER)
	public CField device_type; // 1:WXDevice 2:DQDevice
	@CFieldinfo(fieldname = "device_nickname", caption = "设备昵称", datetype = Types.VARCHAR)
	public CField device_nickname; // 设备昵称
	@CFieldinfo(fieldname = "action_time", caption = "操作时间", datetype = Types.TIMESTAMP)
	public CField action_time; // 操作时间
	@CFieldinfo(fieldname = "wxuserid", caption = "ID", datetype = Types.INTEGER)
	public CField wxuserid; // ID
	@CFieldinfo(fieldname = "bing_opendid", caption = "openid", datetype = Types.VARCHAR)
	public CField bing_opendid; // openid
	@CFieldinfo(fieldname = "bind_stat", caption = "绑定状态 1 未绑定 2 已经绑定 3 取消绑定", datetype = Types.INTEGER)
	public CField bind_stat; // 绑定状态 1 未绑定 2 已经绑定 3 取消绑定
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Wx_device_binding() throws Exception {
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
