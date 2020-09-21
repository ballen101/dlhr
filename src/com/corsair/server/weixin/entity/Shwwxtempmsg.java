package com.corsair.server.weixin.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption = "微信模板消息", tablename = "Shwwxtempmsg")
public class Shwwxtempmsg extends CJPA {
	@CFieldinfo(fieldname = "tmid", iskey = true, notnull = true, precision = 10, scale = 0, caption = "记录IDid", datetype = Types.INTEGER)
	public CField tmid; // 记录IDid
	@CFieldinfo(fieldname = "appid", notnull = true, precision = 10, scale = 0, caption = "微信ID", datetype = Types.INTEGER)
	public CField appid; // 微信ID
	@CFieldinfo(fieldname = "wxappid", notnull = true, precision = 50, scale = 0, caption = "微信appid", datetype = Types.VARCHAR)
	public CField wxappid; // 微信appid
	@CFieldinfo(fieldname = "template_id", notnull = true, precision = 64, scale = 0, caption = "微信模板ID", datetype = Types.VARCHAR)
	public CField template_id; // 微信模板ID
	@CFieldinfo(fieldname = "title", notnull = true, precision = 128, scale = 0, caption = "模板标题", datetype = Types.VARCHAR)
	public CField title; // 模板标题
	@CFieldinfo(fieldname = "primary_industry", notnull = false, precision = 256, scale = 0, caption = "主业", datetype = Types.VARCHAR)
	public CField primary_industry; // 主业
	@CFieldinfo(fieldname = "deputy_industry", notnull = false, precision = 256, scale = 0, caption = "副业", datetype = Types.VARCHAR)
	public CField deputy_industry; // 副业
	@CFieldinfo(fieldname = "content", notnull = true, precision = 512, scale = 0, caption = "内容", datetype = Types.VARCHAR)
	public CField content; // 内容
	@CFieldinfo(fieldname = "example", precision = 512, scale = 0, caption = "发送案例", datetype = Types.VARCHAR)
	public CField example; // 发送案例
	@CFieldinfo(fieldname = "usable", notnull = true, precision = 1, scale = 0, caption = "可用", defvalue = "1", datetype = Types.INTEGER)
	public CField usable; // 可用
	@CFieldinfo(fieldname = "allusersend", notnull = true, precision = 1, scale = 0, caption = "允许用户调用", defvalue = "1", datetype = Types.INTEGER)
	public CField allusersend; // 允许用户调用
	@CFieldinfo(fieldname = "disabletime", precision = 19, scale = 0, caption = "停用时间", datetype = Types.TIMESTAMP)
	public CField disabletime; // 停用时间
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

	public Shwwxtempmsg() throws Exception {
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
