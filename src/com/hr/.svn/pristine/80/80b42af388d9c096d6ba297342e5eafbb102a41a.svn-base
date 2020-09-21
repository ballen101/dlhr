package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hrkq_ondutyline extends CJPA {
	@CFieldinfo(fieldname = "odlid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField odlid; // ID
	@CFieldinfo(fieldname = "od_id", notnull = true, caption = "主ID", datetype = Types.INTEGER)
	public CField od_id; // 主ID
	@CFieldinfo(fieldname = "begin_date", notnull = true, caption = "开始时间", datetype = Types.TIMESTAMP)
	public CField begin_date; // 开始时间
	@CFieldinfo(fieldname = "end_date", notnull = true, caption = "结束时间", datetype = Types.TIMESTAMP)
	public CField end_date; // 结束时间
	@CFieldinfo(fieldname = "checkbegin_date", caption = "上班打卡时间", datetype = Types.TIMESTAMP)
	public CField checkbegin_date; // 上班打卡时间
	@CFieldinfo(fieldname = "checkend_date", caption = "下班打卡时间", datetype = Types.TIMESTAMP)
	public CField checkend_date; // 下班打卡时间
	@CFieldinfo(fieldname = "frst", caption = "上班考勤结果 1 正常 2 迟到 3早退 4未打卡", datetype = Types.INTEGER)
	public CField frst; // 上班考勤结果 1 正常 2 迟到 3早退 4未打卡
	@CFieldinfo(fieldname = "trst", caption = "下班考勤结果 1 正常 2 迟到 3早退 4未打卡", datetype = Types.INTEGER)
	public CField trst; // 下班考勤结果 1 正常 2 迟到 3早退 4未打卡
	@CFieldinfo(fieldname = "dttimelong", caption = "值班时长H", datetype = Types.DECIMAL)
	public CField dttimelong; // 值班时长H
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hrkq_ondutyline() throws Exception {
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
