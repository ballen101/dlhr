package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption = "记录访问量，每次启动读取，关闭保存")
public class Shwsessionct extends CJPA {
	@CFieldinfo(fieldname = "scid", iskey = true, notnull = true, precision = 10, scale = 0, caption = "ID", defvalue = "1", datetype = Types.INTEGER)
	public CField scid; // ID
	@CFieldinfo(fieldname = "ymhh", precision = 15, scale = 0, caption = "yyyy-MM-dd HH  每个小时访问量", datetype = Types.VARCHAR)
	public CField ymhh; // yyyy-MM-dd HH 每个小时访问量
	@CFieldinfo(fieldname = "scnum", precision = 10, scale = 0, caption = "访问人次", defvalue = "0", datetype = Types.INTEGER)
	public CField scnum; // 访问人次
	@CFieldinfo(fieldname = "acactive", precision = 10, scale = 0, caption = "在线数", defvalue = "0", datetype = Types.INTEGER)
	public CField acactive; // 在线数
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwsessionct() throws Exception {
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
