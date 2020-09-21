package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Shwusermessage extends CJPA {
	@CFieldinfo(fieldname = "mid", iskey = true, notnull = true, caption = "分配ID", datetype = Types.INTEGER)
	public CField mid; // 分配ID
	@CFieldinfo(fieldname = "senduserid", caption = "发送用户ID", datetype = Types.INTEGER)
	public CField senduserid; // 发送用户ID
	@CFieldinfo(fieldname = "sendusername", caption = "发送用户名", datetype = Types.VARCHAR)
	public CField sendusername; // 发送用户名
	@CFieldinfo(fieldname = "sendip", caption = "发送IP", datetype = Types.VARCHAR)
	public CField sendip; // 发送IP
	@CFieldinfo(fieldname = "touserid", notnull = true, caption = "接收用户ID", datetype = Types.INTEGER)
	public CField touserid; // 接收用户ID
	@CFieldinfo(fieldname = "tousername", notnull = true, caption = "接收用户名", datetype = Types.VARCHAR)
	public CField tousername; // 接收用户名
	@CFieldinfo(fieldname = "token", caption = "接收者token 用于自动登录", datetype = Types.VARCHAR)
	public CField token; // 接收者token 用于自动登录
	@CFieldinfo(fieldname = "msgtype", notnull = true, caption = "消息类型 1 文本 2 URL ", datetype = Types.INTEGER)
	public CField msgtype; // 消息类型 1 文本 2 URL
	@CFieldinfo(fieldname = "msgtitle", notnull = true, caption = "消息标题", datetype = Types.VARCHAR)
	public CField msgtitle; // 消息标题
	@CFieldinfo(fieldname = "msgcontext", notnull = true, caption = "消息内容", datetype = Types.VARCHAR)
	public CField msgcontext; // 消息内容
	@CFieldinfo(fieldname = "msgurl", caption = "消息连接", datetype = Types.VARCHAR)
	public CField msgurl; // 消息连接
	@CFieldinfo(fieldname = "senddate", notnull = true, caption = "发送时间", datetype = Types.TIMESTAMP)
	public CField senddate; // 发送时间
	@CFieldinfo(fieldname = "readdate", caption = "阅读时间", datetype = Types.TIMESTAMP)
	public CField readdate; // 阅读时间
	@CFieldinfo(fieldname = "isreaded", caption = "是否已读", datetype = Types.INTEGER)
	public CField isreaded; // 是否已读
	@CFieldinfo(fieldname = "readip", notnull = false, caption = "发送IP", datetype = Types.VARCHAR)
	public CField readip; // 发送IP
	@CFieldinfo(fieldname = "attid", caption = "attid", datetype = Types.INTEGER)
	public CField attid; // attid
	@CFieldinfo(fieldname = "creator", notnull = true, caption = "制单人", datetype = Types.VARCHAR)
	public CField creator; // 制单人
	@CFieldinfo(fieldname = "createtime", notnull = true, caption = "制单时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 制单时间
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwusermessage() throws Exception {
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
