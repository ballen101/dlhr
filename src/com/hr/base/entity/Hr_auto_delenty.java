package com.hr.base.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption = "自动删除实体")
public class Hr_auto_delenty extends CJPA {
	@CFieldinfo(fieldname = "adeid", iskey = true, notnull = true, precision = 10, scale = 0, caption = "ID", datetype = Types.INTEGER)
	public CField adeid; // ID
	@CFieldinfo(fieldname = "adename", notnull = true, precision = 128, scale = 0, caption = "实体名称", datetype = Types.VARCHAR)
	public CField adename; // 实体名称
	@CFieldinfo(fieldname = "adeclass", notnull = true, precision = 256, scale = 0, caption = "实体类名", datetype = Types.VARCHAR)
	public CField adeclass; // 实体类名
	@CFieldinfo(fieldname = "delwhere", notnull = true, precision = 2, scale = 0, caption = "提前天数", datetype = Types.INTEGER)
	public CField delwhere; // 提前天数
	@CFieldinfo(fieldname = "useable", notnull = true, precision = 1, scale = 0, caption = "可用", datetype = Types.INTEGER)
	public CField useable; // 可用
	@CFieldinfo(fieldname = "entid", precision = 20, scale = 0, caption = "entid", datetype = Types.INTEGER)
	public CField entid; // entid
	@CFieldinfo(fieldname = "creator", notnull = true, precision = 32, scale = 0, caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", notnull = true, precision = 19, scale = 0, caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", precision = 32, scale = 0, caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", precision = 19, scale = 0, caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hr_auto_delenty() throws Exception {
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
