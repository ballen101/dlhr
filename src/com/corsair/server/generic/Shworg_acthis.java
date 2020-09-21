package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Shworg_acthis extends CJPA {
	@CFieldinfo(fieldname = "orgactid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField orgactid; // ID
	@CFieldinfo(fieldname = "orgid", notnull = true, caption = "orgid", datetype = Types.INTEGER)
	public CField orgid; // orgid
	@CFieldinfo(fieldname = "acttype", notnull = true, caption = "沿革类型 1 成立  2 合并 3 被合并 4 调整级别 5 注销", datetype = Types.INTEGER)
	public CField acttype; // 沿革类型 1 成立 2 合并 3 被合并 4 调整级别 5 注销
	@CFieldinfo(fieldname = "acttime", caption = "处理时间", datetype = Types.TIMESTAMP)
	public CField acttime; // 处理时间
	@CFieldinfo(fieldname = "actcommit", notnull = true, caption = "备注", datetype = Types.VARCHAR)
	public CField actcommit; // 备注
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shworg_acthis() throws Exception {
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
