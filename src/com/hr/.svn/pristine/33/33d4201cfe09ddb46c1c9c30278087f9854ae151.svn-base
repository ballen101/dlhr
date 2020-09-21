package com.hr.pm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hrpm_rstyear extends CJPA {
	@CFieldinfo(fieldname = "pmyrid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField pmyrid; // ID
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "人事ID", datetype = Types.INTEGER)
	public CField er_id; // 人事ID
	@CFieldinfo(fieldname = "pmyyear", notnull = true, caption = "年份", datetype = Types.INTEGER)
	public CField pmyyear; // 年份
	@CFieldinfo(fieldname = "m1", caption = "1月", datetype = Types.DECIMAL)
	public CField m1; // 1月
	@CFieldinfo(fieldname = "m2", caption = "2月", datetype = Types.DECIMAL)
	public CField m2; // 2月
	@CFieldinfo(fieldname = "m3", caption = "3月", datetype = Types.DECIMAL)
	public CField m3; // 3月
	@CFieldinfo(fieldname = "m4", caption = "4月", datetype = Types.DECIMAL)
	public CField m4; // 4月
	@CFieldinfo(fieldname = "m5", caption = "5月", datetype = Types.DECIMAL)
	public CField m5; // 5月
	@CFieldinfo(fieldname = "m6", caption = "6月", datetype = Types.DECIMAL)
	public CField m6; // 6月
	@CFieldinfo(fieldname = "m7", caption = "7月", datetype = Types.DECIMAL)
	public CField m7; // 7月
	@CFieldinfo(fieldname = "m8", caption = "8月", datetype = Types.DECIMAL)
	public CField m8; // 8月
	@CFieldinfo(fieldname = "m9", caption = "9月", datetype = Types.DECIMAL)
	public CField m9; // 9月
	@CFieldinfo(fieldname = "m10", caption = "10月", datetype = Types.DECIMAL)
	public CField m10; // 10月
	@CFieldinfo(fieldname = "m11", caption = "11月", datetype = Types.DECIMAL)
	public CField m11; // 11月
	@CFieldinfo(fieldname = "m12", caption = "12月", datetype = Types.DECIMAL)
	public CField m12; // 12月
	@CFieldinfo(fieldname = "mavg", caption = "月均", datetype = Types.DECIMAL)
	public CField mavg; // 月均
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "createtime", notnull = true, caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hrpm_rstyear() throws Exception {
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
