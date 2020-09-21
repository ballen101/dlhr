package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hrkq_wkoffsourse extends CJPA {
	@CFieldinfo(fieldname = "wolsid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField wolsid; // ID
	@CFieldinfo(fieldname = "woid", notnull = true, caption = "主ID", datetype = Types.INTEGER)
	public CField woid; // 主ID
	@CFieldinfo(fieldname = "lbid", notnull = false, caption = "可休流水ID", datetype = Types.INTEGER)
	public CField lbid; // 可休流水ID
	@CFieldinfo(fieldname = "lbname", notnull = true, caption = "标题", datetype = Types.VARCHAR)
	public CField lbname; // 标题
	@CFieldinfo(fieldname = "stype", notnull = true, caption = "源类型 1 年假 2 加班 3 值班 4出差", datetype = Types.INTEGER)
	public CField stype; // 源类型 1 年假 2 加班 3 值班 4出差
	@CFieldinfo(fieldname = "sccode", caption = "源编码/年假的年份", datetype = Types.VARCHAR)
	public CField sccode; // 源编码/年假的年份
	@CFieldinfo(fieldname = "bgtime", caption = "开始时间", datetype = Types.TIMESTAMP)
	public CField bgtime; // 开始时间
	@CFieldinfo(fieldname = "edtime", caption = "截止时间", datetype = Types.TIMESTAMP)
	public CField edtime; // 截止时间
	@CFieldinfo(fieldname = "alllbtime", notnull = true, caption = "可调休时间小时", datetype = Types.INTEGER)
	public CField alllbtime; // 可调休时间小时
	@CFieldinfo(fieldname = "usedlbtime", notnull = true, caption = "已调休时间小时", datetype = Types.INTEGER)
	public CField usedlbtime; // 已调休时间小时
	@CFieldinfo(fieldname = "wotime", notnull = true, caption = "本次调休时间", datetype = Types.INTEGER)
	public CField wotime; // 本次调休时间
	@CFieldinfo(fieldname = "valdate", caption = "有效期", datetype = Types.TIMESTAMP)
	public CField valdate; // 有效期
	@CFieldinfo(fieldname = "note", caption = "可调休备注", datetype = Types.VARCHAR)
	public CField note; // 可调休备注
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hrkq_wkoffsourse() throws Exception {
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
