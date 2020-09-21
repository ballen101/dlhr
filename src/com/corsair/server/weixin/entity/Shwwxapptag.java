package com.corsair.server.weixin.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption = "微信tag")
public class Shwwxapptag extends CJPA {
	@CFieldinfo(fieldname = "tgid", iskey = true, notnull = true, precision = 10, scale = 0, caption = "平台ID", datetype = Types.INTEGER)
	public CField tgid; // 平台ID
	@CFieldinfo(fieldname = "appid", notnull = true, precision = 10, scale = 0, caption = "appid", datetype = Types.INTEGER)
	public CField appid; // appid
	@CFieldinfo(fieldname = "tagid", notnull = false, precision = 10, scale = 0, caption = "微信tagid", datetype = Types.INTEGER)
	public CField tagid; // 微信tagid
	@CFieldinfo(fieldname = "tagname", notnull = true, precision = 10, scale = 0, caption = "名称", datetype = Types.VARCHAR)
	public CField tagname; // 名称
	@CFieldinfo(fieldname = "remark", precision = 32, scale = 0, caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "usable", precision = 1, scale = 0, caption = "可用", defvalue = "1", datetype = Types.INTEGER)
	public CField usable; // 可用
	@CFieldinfo(fieldname = "wxusable", precision = 1, scale = 0, caption = "微信是否可用", defvalue = "1", datetype = Types.INTEGER)
	public CField wxusable; // 微信是否可用
	@CFieldinfo(fieldname = "tagmenujson", precision = 4096, scale = 0, caption = "个性化菜单", datetype = Types.VARCHAR)
	public CField tagmenujson; // 个性化菜单
	@CFieldinfo(fieldname = "uscount", precision = 6, scale = 0, caption = "粉丝数量", defvalue = "0", datetype = Types.INTEGER)
	public CField uscount; // 粉丝数量
	@CFieldinfo(fieldname="wxtagmenuid",precision=10,scale=0,caption="微信tagmenuid",datetype=Types.VARCHAR)
	public CField wxtagmenuid;  //微信tagmenuid
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwwxapptag() throws Exception {
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
