package com.corsair.server.weixin.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.ctrl.CtrlShwwxapp;

import java.sql.Types;

@CEntity(controller = CtrlShwwxapp.class)
public class Shwwxapp extends CJPA {
	@CFieldinfo(fieldname = "appid", iskey = true, notnull = true, precision = 20, scale = 0, caption = "appid", datetype = Types.INTEGER)
	public CField appid; // appid
	@CFieldinfo(fieldname = "wxappid", precision = 50, scale = 0, caption = "wxappid", datetype = Types.VARCHAR)
	public CField wxappid; // wxappid
	@CFieldinfo(fieldname = "acaption", precision = 100, scale = 0, caption = "acaption", datetype = Types.VARCHAR)
	public CField acaption; // acaption
	@CFieldinfo(fieldname = "menujson", precision = 2048, scale = 0, caption = "微信菜单JOSN", datetype = Types.VARCHAR)
	public CField menujson; // 微信菜单JOSN
	@CFieldinfo(fieldname = "entid", precision = 10, scale = 0, caption = "entid", datetype = Types.INTEGER)
	public CField entid; // entid
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	/**
	 * 微信参数
	 */
	@CLinkFieldInfo(jpaclass = Shwwxappparm.class, linkFields = { @LinkFieldItem(lfield = "appid", mfield = "appid") })
	public CJPALineData<Shwwxappparm> shwwxappparms;
	/**
	 * 微信tag
	 */
	@CLinkFieldInfo(jpaclass = Shwwxapptag.class, linkFields = { @LinkFieldItem(lfield = "appid", mfield = "appid") })
	public CJPALineData<Shwwxapptag> shwwxapptags;

	private Shwwxmsgcfg wxmsgcfg = new Shwwxmsgcfg();// 微信自动回复消息配置参数

	// 自关联数据定义

	public Shwwxapp() throws Exception {
		
	}

	@Override
	public boolean InitObject() {// 类初始化调用的方法
		super.InitObject();

		shwwxappparms.addLinkField("appid", "appid");
		return true;
	}

	@Override
	public boolean FinalObject() { // 类释放前调用的方法
		super.FinalObject();
		return true;
	}

	public Shwwxmsgcfg getWxmsgcfg() {
		return wxmsgcfg;
	}

}