package com.corsair.server.weixin.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Wx_news extends CJPA {
	@CFieldinfo(fieldname = "newsid", iskey = true, notnull = true, precision = 10, scale = 0, caption = "ID", datetype = Types.INTEGER)
	public CField newsid; // ID
	@CFieldinfo(fieldname = "newscode", notnull = true, precision = 20, scale = 0, caption = "图文消息编码", datetype = Types.VARCHAR)
	public CField newscode; // 图文消息编码
	@CFieldinfo(fieldname = "newstitle", precision = 64, scale = 0, caption = "图文消息标题", datetype = Types.VARCHAR)
	public CField newstitle; // 图文消息标题
	@CFieldinfo(fieldname = "clas", precision = 1, scale = 0, caption = "类型", defvalue = "0", datetype = Types.INTEGER)
	public CField clas; // 类型
	@CFieldinfo(fieldname = "useable", precision = 1, scale = 0, caption = "可用", defvalue = "0", datetype = Types.INTEGER)
	public CField useable; // 可用
	@CFieldinfo(fieldname = "creator", precision = 64, scale = 0, caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", precision = 19, scale = 0, caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", precision = 64, scale = 0, caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", precision = 19, scale = 0, caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	@CLinkFieldInfo(jpaclass = Wx_newsline.class, linkFields = { @LinkFieldItem(lfield = "newsid", mfield = "newsid") })
	public CJPALineData<Wx_newsline> wx_newslines;

	// 自关联数据定义

	public Wx_news() throws Exception {
	}

	@Override
	public boolean InitObject() {// 类初始化调用的方法
		super.InitObject();
		this.wx_newslines.addLinkField("newsid", "newsid");
		wx_newslines.setSqlOrderBystr("sortindex");
		return true;
	}

	@Override
	public boolean FinalObject() { // 类释放前调用的方法
		super.FinalObject();
		return true;
	}
}
