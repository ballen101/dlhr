package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shwdict extends CJPA {
	@CFieldinfo(fieldname = "dictid", iskey = true, notnull = true, datetype = Types.FLOAT)
	public CField dictid; // ID
	@CFieldinfo(fieldname = "dictcode", notnull = true, datetype = Types.VARCHAR)
	public CField dictcode; // 编码
	@CFieldinfo(fieldname = "dictname", notnull = true, datetype = Types.VARCHAR)
	public CField dictname; // 名称
	@CFieldinfo(fieldname = "dictvalue", notnull = true, datetype = Types.VARCHAR)
	public CField dictvalue; // 词汇值
	@CFieldinfo(fieldname = "pid", datetype = Types.FLOAT)
	public CField pid; // 父ID
	@CFieldinfo(fieldname = "dtype", datetype = Types.INTEGER)
	public CField dtype; // 类别0 根 1 分类 2 词汇 3 词汇值
	@CFieldinfo(fieldname = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // 路径
	@CFieldinfo(fieldname = "usable", datetype = Types.FLOAT)
	public CField usable; // 可用
	@CFieldinfo(fieldname = "creator", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	@CFieldinfo(fieldname = "note", datetype = Types.VARCHAR)
	public CField note; // 备注
	@CFieldinfo(fieldname = "language1", datetype = Types.VARCHAR)
	public CField language1; //
	@CFieldinfo(fieldname = "language2", datetype = Types.VARCHAR)
	public CField language2; //
	@CFieldinfo(fieldname = "language3", datetype = Types.VARCHAR)
	public CField language3; //
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwdict() throws Exception {
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
