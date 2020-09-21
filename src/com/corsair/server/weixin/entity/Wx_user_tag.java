package com.corsair.server.weixin.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption = "微信用户tag")
public class Wx_user_tag extends CJPA {
	@CFieldinfo(fieldname = "utid", iskey = true, notnull = true, precision = 10, scale = 0, caption = "ID", datetype = Types.INTEGER)
	public CField utid; // ID
	@CFieldinfo(fieldname = "wxuserid", notnull = true, precision = 10, scale = 0, caption = "wxuserid", datetype = Types.INTEGER)
	public CField wxuserid; // wxuserid
	@CFieldinfo(fieldname = "openid", notnull = true, precision = 64, scale = 0, caption = "openid", datetype = Types.VARCHAR)
	public CField openid; // openid
	@CFieldinfo(fieldname = "tgid", notnull = true, precision = 10, scale = 0, caption = "平台ID", datetype = Types.INTEGER)
	public CField tgid; // 平台ID
	@CFieldinfo(fieldname = "appid", notnull = true, precision = 10, scale = 0, caption = "wxappid", datetype = Types.INTEGER)
	public CField appid; // wxappid
	@CFieldinfo(fieldname = "tagid", notnull = true, precision = 10, scale = 0, caption = "微信tagid 一开始允许为空，同步后不能为空", datetype = Types.INTEGER)
	public CField tagid; // 微信tagid 一开始允许为空，同步后不能为空
	@CFieldinfo(fieldname = "tagname", notnull = true, precision = 10, scale = 0, caption = "tag名称", datetype = Types.VARCHAR)
	public CField tagname; // tag名称
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Wx_user_tag() throws Exception {
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
