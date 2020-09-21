package com.corsair.server.weixin.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Wx_received_msg extends CJPA {
	@CFieldinfo(fieldname = "mid", iskey = true, notnull = true, datetype = Types.INTEGER)
	public CField mid; // 表ID
	@CFieldinfo(fieldname = "msgid", datetype = Types.VARCHAR)
	public CField msgid; // 微信消息ID
	@CFieldinfo(fieldname = "fromusername", notnull = true, datetype = Types.VARCHAR)
	public CField fromusername; // 发送人
	@CFieldinfo(fieldname = "msgtype", datetype = Types.VARCHAR)
	public CField msgtype; // 消息类型
	@CFieldinfo(fieldname = "createtime", notnull = true, datetype = Types.INTEGER)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "content", datetype = Types.VARCHAR)
	public CField content; // 文本消息
	@CFieldinfo(fieldname = "picurl", datetype = Types.VARCHAR)
	public CField picurl; // 图片链接
	@CFieldinfo(fieldname = "mediaid", datetype = Types.VARCHAR)
	public CField mediaid; // 媒体ID
	@CFieldinfo(fieldname = "location_x", datetype = Types.VARCHAR)
	public CField location_x; // 地理位置维度
	@CFieldinfo(fieldname = "location_y", datetype = Types.VARCHAR)
	public CField location_y; // 地理位置经度
	@CFieldinfo(fieldname = "scale", datetype = Types.FLOAT)
	public CField scale; // 地图缩放大小
	@CFieldinfo(fieldname = "label", datetype = Types.VARCHAR)
	public CField label; // 地理位置信息
	@CFieldinfo(fieldname = "title", datetype = Types.VARCHAR)
	public CField title; // 链接标题
	@CFieldinfo(fieldname = "description", datetype = Types.VARCHAR)
	public CField description; // 链接描述
	@CFieldinfo(fieldname = "url", datetype = Types.VARCHAR)
	public CField url; // 链接地址
	@CFieldinfo(fieldname = "event", datetype = Types.VARCHAR)
	public CField event; // 事件类型
	@CFieldinfo(fieldname = "eventkey", datetype = Types.VARCHAR)
	public CField eventkey; // 事件子类型
	@CFieldinfo(fieldname = "latitude", datetype = Types.VARCHAR)
	public CField latitude; // 上报位置信息 事件 纬度
	@CFieldinfo(fieldname = "longitude", datetype = Types.VARCHAR)
	public CField longitude; // 上报位置信息 事件 经度
	@CFieldinfo(fieldname = "precision", datetype = Types.VARCHAR)
	public CField precision; // 上报位置信息 事件 精度
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Wx_received_msg() throws Exception {
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
