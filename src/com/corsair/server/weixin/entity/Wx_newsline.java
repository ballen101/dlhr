package com.corsair.server.weixin.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Wx_newsline extends CJPA {
	@CFieldinfo(fieldname = "newslid", iskey = true, notnull = true, precision = 10, scale = 0, caption = "ID", datetype = Types.INTEGER)
	public CField newslid; // ID
	@CFieldinfo(fieldname = "newsid", notnull = true, precision = 10, scale = 0, caption = "图文消息ID", datetype = Types.INTEGER)
	public CField newsid; // 图文消息ID
	@CFieldinfo(fieldname = "sortindex", notnull = true, precision = 1, scale = 0, caption = "序号", datetype = Types.INTEGER)
	public CField sortindex; // 序号
	@CFieldinfo(fieldname = "title", notnull = true, precision = 512, scale = 0, caption = "标题", datetype = Types.VARCHAR)
	public CField title; // 标题
	@CFieldinfo(fieldname = "description", notnull = true, precision = 1024, scale = 0, caption = "说明", datetype = Types.VARCHAR)
	public CField description; // 说明
	@CFieldinfo(fieldname = "url", precision = 512, scale = 0, caption = "点击后的连接", datetype = Types.VARCHAR)
	public CField url; // 点击后的连接
	@CFieldinfo(fieldname = "picurl", precision = 512, scale = 0, caption = "图片地址", datetype = Types.VARCHAR)
	public CField picurl; // 图片地址
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Wx_newsline() throws Exception {
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
