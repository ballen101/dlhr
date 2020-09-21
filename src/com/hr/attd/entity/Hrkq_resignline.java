package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hrkq_resignline extends CJPA {
	@CFieldinfo(fieldname = "reslid", iskey = true, notnull = true, caption = "补签行ID", datetype = Types.INTEGER)
	public CField reslid; // 补签行ID
	@CFieldinfo(fieldname = "resid", notnull = true, caption = "补签ID", datetype = Types.INTEGER)
	public CField resid; // 补签ID
	@CFieldinfo(fieldname = "kqdate", caption = "考勤日期", datetype = Types.DATE)
	public CField kqdate; // 考勤日期
	@CFieldinfo(fieldname = "ltype", caption = "1 正班 2 加班 3值班", defvalue = "1", datetype = Types.INTEGER)
	public CField ltype; // 1 正班 2 加班 3值班
	@CFieldinfo(fieldname = "bcno", caption = "班次号", datetype = Types.VARCHAR)
	public CField bcno; // 班次号
	@CFieldinfo(fieldname = "rgtime", notnull = true, caption = "打卡时间", datetype = Types.VARCHAR)
	public CField rgtime; // 打卡时间
	@CFieldinfo(fieldname = "isreg", notnull = true, caption = "是否签卡", datetype = Types.INTEGER)
	public CField isreg; // 是否签卡
	@CFieldinfo(fieldname = "resreson", caption = "补签原因", datetype = Types.VARCHAR)
	public CField resreson; // 补签原因
	@CFieldinfo(fieldname = "sclid", caption = "班次行ID", defvalue = "1", datetype = Types.INTEGER)
	public CField sclid; // 班次行ID
	@CFieldinfo(fieldname = "scid", caption = "班次ID", datetype = Types.INTEGER)
	public CField scid; // 班次ID
	@CFieldinfo(fieldname = "scdname", caption = "班次名", datetype = Types.VARCHAR)
	public CField scdname; // 班次名
	@CFieldinfo(fieldname = "sxb", caption = "1 上班 2 下班", defvalue = "1", datetype = Types.INTEGER)
	public CField sxb; // 1 上班 2 下班
	@CFieldinfo(fieldname = "ri_times", precision = 1, scale = 0, caption = "计入补签次数", defvalue = "1", datetype = Types.INTEGER)
	public CField ri_times; // 计入补签次数
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hrkq_resignline() throws Exception {
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
