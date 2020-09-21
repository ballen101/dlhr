package com.corsair.server.weixin.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.ctrl.CtrShwwxmsgcfg;

import java.sql.Types;

@CEntity(controller = CtrShwwxmsgcfg.class, caption = "微信自动回复消息配置")
public class Shwwxmsgcfg extends CJPA {
	@CFieldinfo(fieldname = "mcid", iskey = true, notnull = true, precision = 10, scale = 0, caption = "id", datetype = Types.INTEGER)
	public CField mcid; // id
	@CFieldinfo(fieldname = "appid", notnull = true, precision = 10, scale = 0, caption = "微信ID", datetype = Types.INTEGER)
	public CField appid; // 微信ID
	@CFieldinfo(fieldname = "wxappid", notnull = true, precision = 50, scale = 0, caption = "微信appid", datetype = Types.VARCHAR)
	public CField wxappid; // 微信appid
	@CFieldinfo(fieldname = "acaption", precision = 100, scale = 0, caption = "标题", datetype = Types.VARCHAR)
	public CField acaption; // 标题
	@CFieldinfo(fieldname = "usable", precision = 1, scale = 0, caption = "可用", defvalue = "1", datetype = Types.INTEGER)
	public CField usable; // 可用
	@CFieldinfo(fieldname = "text_atrep", precision = 1, scale = 0, caption = "收到消息自动回复启用", defvalue = "1", datetype = Types.INTEGER)
	public CField text_atrep; // 收到消息自动回复启用
	@CFieldinfo(fieldname = "text_atrep_tp", precision = 32, scale = 0, caption = "自动回复内容方式 1 文本 2 连接 可多选", datetype = Types.VARCHAR)
	public CField text_atrep_tp; // 自动回复内容方式 1 文本 2 连接 可多选
	@CFieldinfo(fieldname = "text_atrep_msg", precision = 1024, scale = 0, caption = "自动回复内容", datetype = Types.VARCHAR)
	public CField text_atrep_msg; // 自动回复内容
	@CFieldinfo(fieldname = "text_newsid", precision = 10, scale = 0, caption = "图文消息ID", datetype = Types.INTEGER)
	public CField text_newsid; // 图文消息ID
	@CFieldinfo(fieldname = "text_newstitle", precision = 64, scale = 0, caption = "图文消息标题", datetype = Types.VARCHAR)
	public CField text_newstitle; // 图文消息标题
	@CFieldinfo(fieldname = "text_resend_openids", precision = 512, scale = 0, caption = "文本消息转发微信用户/24小时内联系过才可以？", datetype = Types.VARCHAR)
	public CField text_resend_openids; // 文本消息转发微信用户/24小时内联系过才可以？
	@CFieldinfo(fieldname = "text_atrep_key", precision = 1, scale = 0, caption = "启用关键字回复", defvalue = "1", datetype = Types.INTEGER)
	public CField text_atrep_key; // 启用关键字回复
	@CFieldinfo(fieldname = "text_atrep_key_type", precision = 2, scale = 0, caption = "多关键字检索方式:1 所有关键字匹配 2 按顺序匹配第一个关键字 ", defvalue = "1", datetype = Types.INTEGER)
	public CField text_atrep_key_type; // 多关键字检索方式:1 所有关键字匹配 2 按顺序匹配第一个关键字
	@CFieldinfo(fieldname = "subscribe_atrep", precision = 1, scale = 0, caption = "关注自动回复启用", defvalue = "1", datetype = Types.INTEGER)
	public CField subscribe_atrep; // 关注自动回复启用
	@CFieldinfo(fieldname = "subscribe_atrep_tp", precision = 32, scale = 0, caption = "关注自动回复内容方式 1 文本 2 连接 可多选", datetype = Types.VARCHAR)
	public CField subscribe_atrep_tp; // 关注自动回复内容方式 1 文本 2 连接 可多选
	@CFieldinfo(fieldname = "subscribe_atrep_msg", precision = 1024, scale = 0, caption = "关注自动回复内容", datetype = Types.VARCHAR)
	public CField subscribe_atrep_msg; // 关注自动回复内容
	@CFieldinfo(fieldname = "subscribe_newsid", precision = 64, scale = 0, caption = "关注回复图文消息ID", datetype = Types.INTEGER)
	public CField subscribe_newsid; // 关注回复图文消息ID
	@CFieldinfo(fieldname = "subscribe_newstitle", precision = 32, scale = 0, caption = "关注回复图文消息编码", datetype = Types.VARCHAR)
	public CField subscribe_newstitle; // 关注回复图文消息编码
	@CFieldinfo(fieldname = "attr1", precision = 1024, scale = 0, caption = "attr1", datetype = Types.VARCHAR)
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
	@CFieldinfo(fieldname = "idpath", notnull = true, precision = 512, scale = 0, caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	@CFieldinfo(fieldname = "update_time", precision = 19, scale = 0, caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField update_time; // 更新时间
	@CFieldinfo(fieldname = "entid", precision = 1, scale = 0, caption = "entid", defvalue = "0", datetype = Types.INTEGER)
	public CField entid; // entid
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义
	@CLinkFieldInfo(jpaclass = Shwwxmsgcfg_key.class, linkFields = { @LinkFieldItem(lfield = "mcid", mfield = "mcid") })
	public CJPALineData<Shwwxmsgcfg_key> shwwxmsgcfg_keys;

	public Shwwxmsgcfg() throws Exception {
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
