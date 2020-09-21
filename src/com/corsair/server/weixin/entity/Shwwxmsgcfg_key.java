package com.corsair.server.weixin.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption = "微信自动回复消息关键字配置")
public class Shwwxmsgcfg_key extends CJPA {
	@CFieldinfo(fieldname="mcidkid",iskey=true,notnull=true,precision=10,scale=0,caption="配置关键字ID",datetype=Types.INTEGER)
	public CField mcidkid;  //配置关键字ID
	@CFieldinfo(fieldname="mcid",notnull=true,precision=10,scale=0,caption="配置ID",datetype=Types.INTEGER)
	public CField mcid;  //配置ID
	@CFieldinfo(fieldname="msgkey",notnull=true,precision=64,scale=0,caption="关键字",datetype=Types.VARCHAR)
	public CField msgkey;  //关键字
	@CFieldinfo(fieldname="cptype",notnull=true,precision=2,scale=0,caption="匹配方式:1 完全匹配 2 部分匹配 ",defvalue="1",datetype=Types.INTEGER)
	public CField cptype;  //匹配方式:1 完全匹配 2 部分匹配 
	@CFieldinfo(fieldname="text_atrep_tp",precision=32,scale=0,caption="自动回复内容方式 1 文本 2 连接 可多选",datetype=Types.VARCHAR)
	public CField text_atrep_tp;  //自动回复内容方式 1 文本 2 连接 可多选
	@CFieldinfo(fieldname="text_atrep_msg",precision=1024,scale=0,caption="自动回复内容",datetype=Types.VARCHAR)
	public CField text_atrep_msg;  //自动回复内容
	@CFieldinfo(fieldname="text_newsid",precision=10,scale=0,caption="图文消息ID",datetype=Types.INTEGER)
	public CField text_newsid;  //图文消息ID
	@CFieldinfo(fieldname="text_newstitle",precision=64,scale=0,caption="图文消息标题",datetype=Types.VARCHAR)
	public CField text_newstitle;  //图文消息标题
	@CFieldinfo(fieldname="idx",precision=3,scale=0,caption="序号",defvalue="0",datetype=Types.INTEGER)
	public CField idx;  //序号
	@CFieldinfo(fieldname="usable",notnull=true,precision=1,scale=0,caption="可用",defvalue="1",datetype=Types.INTEGER)
	public CField usable;  //可用
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量


	// 自关联数据定义

	public Shwwxmsgcfg_key() throws Exception {
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
