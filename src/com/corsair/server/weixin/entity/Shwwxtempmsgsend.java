package com.corsair.server.weixin.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption = "微信模板消息", tablename = "Shwwxtempmsgsend")
public class Shwwxtempmsgsend extends CJPA {
	@CFieldinfo(fieldname = "tmsdid", iskey = true, notnull = true, precision = 10, scale = 0, caption = "发送ID", datetype = Types.INTEGER)
	public CField tmsdid; // 发送ID
	@CFieldinfo(fieldname = "appid", notnull = true, precision = 10, scale = 0, caption = "微信ID", datetype = Types.INTEGER)
	public CField appid; // 微信ID
	@CFieldinfo(fieldname = "wxappid", notnull = true, precision = 50, scale = 0, caption = "微信appid", datetype = Types.VARCHAR)
	public CField wxappid; // 微信appid
	@CFieldinfo(fieldname = "template_id", notnull = true, precision = 64, scale = 0, caption = "微信模板ID", datetype = Types.VARCHAR)
	public CField template_id; // 微信模板ID
	@CFieldinfo(fieldname = "msgid", notnull = false, precision = 64, scale = 0, caption = "微信返回的消息ID", datetype = Types.VARCHAR)
	public CField msgid; // 微信返回的消息ID
	@CFieldinfo(fieldname = "touser", notnull = true, precision = 64, scale = 0, caption = "接收用户ID", datetype = Types.VARCHAR)
	public CField touser; // 接收用户ID
	@CFieldinfo(fieldname = "rdurl", precision = 512, scale = 0, caption = "跳转路径", datetype = Types.VARCHAR)
	public CField rdurl; // 跳转路径
	@CFieldinfo(fieldname = "sddata", precision = 1024, scale = 0, caption = "发送的数据", datetype = Types.VARCHAR)
	public CField sddata; // 发送的数据
	@CFieldinfo(fieldname = "sderrmsg", precision = 64, scale = 0, caption = "微信返回的发送状态", datetype = Types.VARCHAR)
	public CField sderrmsg; // 微信返回的发送状态
	@CFieldinfo(fieldname = "rcstate", precision = 64, scale = 0, caption = "微信用户接收状态", datetype = Types.VARCHAR)
	public CField rcstate; // 微信用户接收状态
	@CFieldinfo(fieldname = "attr1", precision = 32, scale = 0, caption = "attr1", datetype = Types.VARCHAR)
	public CField attr1; // attr1
	@CFieldinfo(fieldname = "attr2", precision = 32, scale = 0, caption = "attr2", datetype = Types.VARCHAR)
	public CField attr2; // attr2
	@CFieldinfo(fieldname = "attr3", precision = 32, scale = 0, caption = "attr3", datetype = Types.VARCHAR)
	public CField attr3; // attr3
	@CFieldinfo(fieldname = "attr4", precision = 32, scale = 0, caption = "attr4", datetype = Types.VARCHAR)
	public CField attr4; // attr4
	@CFieldinfo(fieldname = "attr5", precision = 32, scale = 0, caption = "attr5", datetype = Types.VARCHAR)
	public CField attr5; // attr5
	@CFieldinfo(fieldname = "sduserid", precision = 10, scale = 0, caption = "发送的用户(用来计次，将来大了可以收费)", datetype = Types.INTEGER)
	public CField sduserid; // 发送的用户(用来计次，将来大了可以收费)
	@CFieldinfo(fieldname = "creator", precision = 32, scale = 0, caption = "制单人", datetype = Types.VARCHAR)
	public CField creator; // 制单人
	@CFieldinfo(fieldname = "create_time", precision = 19, scale = 0, caption = "制单时间", datetype = Types.TIMESTAMP)
	public CField create_time; // 制单时间
	@CFieldinfo(fieldname = "updator", precision = 32, scale = 0, caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "update_time", precision = 19, scale = 0, caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField update_time; // 更新时间
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwwxtempmsgsend() throws Exception {
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
