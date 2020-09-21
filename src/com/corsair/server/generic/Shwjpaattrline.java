package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(tablename = "Shwjpaattrline")
public class Shwjpaattrline extends CJPA {
	@CFieldinfo(fieldname = "jalid", iskey = true, notnull = true, precision = 10, scale = 0, caption = "ID", datetype = Types.INTEGER)
	public CField jalid; // ID
	@CFieldinfo(fieldname = "jaid", notnull = true, precision = 10, scale = 0, caption = "主表ID", datetype = Types.INTEGER)
	public CField jaid; // 主表ID
	@CFieldinfo(fieldname = "uscaption", notnull = true, precision = 128, scale = 0, caption = "显示标题", datetype = Types.VARCHAR)
	public CField uscaption; // 显示标题
	@CFieldinfo(fieldname = "fdname", notnull = true, precision = 64, scale = 0, caption = "字段名", datetype = Types.VARCHAR)
	public CField fdname; // 字段名
	@CFieldinfo(fieldname = "fdcaption", notnull = true, precision = 64, scale = 0, caption = "字段标题", datetype = Types.VARCHAR)
	public CField fdcaption; // 字段标题
	@CFieldinfo(fieldname = "usable", notnull = true, precision = 1, scale = 0, caption = "可用", defvalue = "1", datetype = Types.INTEGER)
	public CField usable; // 可用
	@CFieldinfo(fieldname = "inputtype", notnull = true, precision = 1, scale = 0, caption = "输入类型 1 文本框 2 图片", datetype = Types.INTEGER)
	public CField inputtype; // 输入类型 1 文本框 2 图片
	@CFieldinfo(fieldname = "inputoption", precision = 128, scale = 0, caption = "文本框选项", datetype = Types.VARCHAR)
	public CField inputoption; // 文本框选项
	@CFieldinfo(fieldname = "datasourse", precision = 128, scale = 0, caption = "数据来源 dic/function", datetype = Types.VARCHAR)
	public CField datasourse; // 数据来源 dic/function
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwjpaattrline() throws Exception {
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
