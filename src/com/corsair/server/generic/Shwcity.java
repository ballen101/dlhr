package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Shwcity extends CJPA {
	@CFieldinfo(fieldname = "cid", iskey = true, notnull = true, precision = 10, scale = 0, caption = "城市ID", datetype = Types.INTEGER)
	public CField cid; // 城市ID
	@CFieldinfo(fieldname = "cname", notnull = true, precision = 128, scale = 0, caption = "城市名称", datetype = Types.VARCHAR)
	public CField cname; // 城市名称
	@CFieldinfo(fieldname = "pym", precision = 256, scale = 0, caption = "拼音码", datetype = Types.VARCHAR)
	public CField pym; // 拼音码
	@CFieldinfo(fieldname = "fpym", precision = 2, scale = 0, caption = "拼音首字母", datetype = Types.VARCHAR)
	public CField fpym; // 拼音首字母
	@CFieldinfo(fieldname = "pid", notnull = true, precision = 10, scale = 0, caption = "省份ID", datetype = Types.INTEGER)
	public CField pid; // 省份ID
	@CFieldinfo(fieldname = "pname", notnull = true, precision = 128, scale = 0, caption = "省份名称", datetype = Types.VARCHAR)
	public CField pname; // 省份名称
	@CFieldinfo(fieldname = "zcode", notnull = true, precision = 10, scale = 0, caption = "邮编", datetype = Types.VARCHAR)
	public CField zcode; // 邮编
	@CFieldinfo(fieldname = "ctid", notnull = true, precision = 3, scale = 0, caption = "国家ID", datetype = Types.INTEGER)
	public CField ctid; // 国家ID
	@CFieldinfo(fieldname = "ctname", notnull = true, precision = 128, scale = 0, caption = "国家名称", datetype = Types.VARCHAR)
	public CField ctname; // 国家名称
	@CFieldinfo(fieldname = "areaid", precision = 20, scale = 0, caption = "对应area", datetype = Types.INTEGER)
	public CField areaid; // 对应area
	@CFieldinfo(fieldname = "ishot", precision = 1, scale = 0, caption = "是否热门城市", defvalue = "0", datetype = Types.BIT)
	public CField ishot; // 是否热门城市
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
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwcity() throws Exception {
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
