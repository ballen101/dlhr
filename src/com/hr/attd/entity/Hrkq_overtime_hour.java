package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hrkq_overtime_hour extends CJPA {
	@CFieldinfo(fieldname = "oth_id", iskey = true, notnull = true, caption = "加班时间明细ID", datetype = Types.INTEGER)
	public CField oth_id; // 加班时间明细ID
	@CFieldinfo(fieldname = "otl_id", notnull = true, caption = "加班申请明细ID", datetype = Types.INTEGER)
	public CField otl_id; // 加班申请明细ID
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "申请人档案ID", datetype = Types.INTEGER)
	public CField er_id; // 申请人档案ID
	@CFieldinfo(fieldname = "employee_code", notnull = true, caption = "申请人工号", datetype = Types.VARCHAR)
	public CField employee_code; // 申请人工号
	@CFieldinfo(fieldname = "employee_name", notnull = true, caption = "申请人姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 申请人姓名
	@CFieldinfo(fieldname = "applyhours", caption = "申请时数", datetype = Types.DECIMAL)
	public CField applyhours; // 申请时数
	@CFieldinfo(fieldname = "othours", caption = "加班时数", datetype = Types.DECIMAL)
	public CField othours; // 加班时数
	@CFieldinfo(fieldname = "deductoff", caption = "扣休息时数", datetype = Types.DECIMAL)
	public CField deductoff; // 扣休息时数
	@CFieldinfo(fieldname = "begin_date", caption = "申请加班开始时间", datetype = Types.TIMESTAMP)
	public CField begin_date; // 申请加班开始时间
	@CFieldinfo(fieldname = "end_date", caption = "申请加班结束时间", datetype = Types.TIMESTAMP)
	public CField end_date; // 申请加班结束时间
	@CFieldinfo(fieldname = "needchedksb", precision = 1, scale = 0, caption = "上班需要打卡", defvalue = "1", datetype = Types.INTEGER)
	public CField needchedksb; // 上班需要打卡
	@CFieldinfo(fieldname = "needchedkxb", precision = 1, scale = 0, caption = "下班需要打卡", defvalue = "1", datetype = Types.INTEGER)
	public CField needchedkxb; // 下班需要打卡
	@CFieldinfo(fieldname = "alterbegin_date", caption = "加班调整开始时间", datetype = Types.TIMESTAMP)
	public CField alterbegin_date; // 加班调整开始时间
	@CFieldinfo(fieldname = "alterend_date", caption = "加班调整结束时间", datetype = Types.TIMESTAMP)
	public CField alterend_date; // 加班调整结束时间
	@CFieldinfo(fieldname = "checkbegin_date", caption = "加班打卡上班时间", datetype = Types.TIMESTAMP)
	public CField checkbegin_date; // 加班打卡上班时间
	@CFieldinfo(fieldname = "checkend_date", caption = "加班打卡下班时间", datetype = Types.TIMESTAMP)
	public CField checkend_date; // 加班打卡下班时间
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hrkq_overtime_hour() throws Exception {
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
