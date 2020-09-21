package com.hr.attd.ctr;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class CalcDutyEnty extends CJPA {
	@CFieldinfo(fieldname = "od_id", notnull = true, caption = "od_id", datetype = Types.INTEGER)
	public CField od_id; // od_id
	@CFieldinfo(fieldname = "od_code", notnull = true, caption = "od_code", datetype = Types.VARCHAR)
	public CField od_code; // od_code
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "er_id", datetype = Types.INTEGER)
	public CField er_id; // er_id
	@CFieldinfo(fieldname = "duty_type", caption = "duty_type", datetype = Types.INTEGER)
	public CField duty_type; // duty_type
	@CFieldinfo(fieldname = "needchedksb", precision = 1, scale = 0, caption = "上班需要打卡", defvalue = "1", datetype = Types.INTEGER)
	public CField needchedksb; // 上班需要打卡
	@CFieldinfo(fieldname = "needchedkxb", precision = 1, scale = 0, caption = "下班需要打卡", defvalue = "1", datetype = Types.INTEGER)
	public CField needchedkxb; // 下班需要打卡
	@CFieldinfo(fieldname = "dealtype", caption = "dealtype", datetype = Types.INTEGER)
	public CField dealtype; // dealtype
	@CFieldinfo(fieldname = "odlid", notnull = true, caption = "odlid", datetype = Types.INTEGER)
	public CField odlid; // odlid
	@CFieldinfo(fieldname = "begin_date", notnull = true, caption = "begin_date", datetype = Types.TIMESTAMP)
	public CField begin_date; // begin_date
	@CFieldinfo(fieldname = "end_date", notnull = true, caption = "end_date", datetype = Types.TIMESTAMP)
	public CField end_date; // end_date
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public CalcDutyEnty() throws Exception {
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
