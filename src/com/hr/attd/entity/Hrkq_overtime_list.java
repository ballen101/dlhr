package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hrkq_overtime_list extends CJPA {
	@CFieldinfo(fieldname = "otlistid", iskey = true, notnull = true, caption = "加班列表ID", datetype = Types.INTEGER)
	public CField otlistid; // 加班列表ID
	@CFieldinfo(fieldname = "oth_id", caption = "加班时间明细ID/班次ID", datetype = Types.INTEGER)
	public CField oth_id; // 加班时间明细ID/班次ID
	@CFieldinfo(fieldname = "otl_id", caption = "加班申请明细ID/班制ID", datetype = Types.INTEGER)
	public CField otl_id; // 加班申请明细ID/班制ID
	@CFieldinfo(fieldname = "otltype", notnull = true, caption = "源单类型 1 批量申请单", datetype = Types.INTEGER)
	public CField otltype; // 源单类型 1 批量申请单 2 单独申请 3 班次固定加班 4上班加班 5 下班加班
	@CFieldinfo(fieldname = "ot_id", notnull = true, caption = "加班申请ID", datetype = Types.INTEGER)
	public CField ot_id; // 加班申请ID
	@CFieldinfo(fieldname = "ot_code", notnull = true, caption = "加班申请编码", datetype = Types.VARCHAR)
	public CField ot_code; // 加班申请编码
	@CFieldinfo(fieldname = "kqdate", caption = "考勤日期", datetype = Types.TIMESTAMP)
	public CField kqdate; // 考勤日期 针对考勤计算自动生成的加班信息
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "申请人档案ID", datetype = Types.INTEGER)
	public CField er_id; // 申请人档案ID
	@CFieldinfo(fieldname = "employee_code", notnull = true, caption = "申请人工号", datetype = Types.VARCHAR)
	public CField employee_code; // 申请人工号
	@CFieldinfo(fieldname = "employee_name", notnull = true, caption = "申请人姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 申请人姓名
	@CFieldinfo(fieldname = "dealtype", notnull = true, caption = "加班处理 1 调休 2 计加班费", datetype = Types.INTEGER)
	public CField dealtype; // 加班处理 1 调休 2 计加班费
	@CFieldinfo(fieldname = "over_type", caption = "加班类型", datetype = Types.INTEGER)
	public CField over_type; // 加班类型
	@CFieldinfo(fieldname = "needchedksb", precision = 1, scale = 0, caption = "上班需要打卡", defvalue = "1", datetype = Types.INTEGER)
	public CField needchedksb; // 上班需要打卡
	@CFieldinfo(fieldname = "needchedkxb", precision = 1, scale = 0, caption = "下班需要打卡", defvalue = "1", datetype = Types.INTEGER)
	public CField needchedkxb; // 下班需要打卡
	@CFieldinfo(fieldname = "bgtime", notnull = false, caption = "上班时间", datetype = Types.TIMESTAMP)
	public CField bgtime; // 上班时间 当otltype 为3,4,5 时候可能为空
	@CFieldinfo(fieldname = "edtime", notnull = false, caption = "下班时间", datetype = Types.TIMESTAMP)
	public CField edtime; // 上班时间 当otltype 为3,4,5 时候可能为空
	@CFieldinfo(fieldname = "bgpktime", caption = "上班打卡时间", datetype = Types.TIMESTAMP)
	public CField bgpktime; // 上班打卡时间
	@CFieldinfo(fieldname = "edpktime", caption = "下班打卡时间", datetype = Types.TIMESTAMP)
	public CField edpktime; // 下班打卡时间
	@CFieldinfo(fieldname = "frst", caption = "上班考勤结果 1 正常 2 迟到 3早退 4未打卡", datetype = Types.INTEGER)
	public CField frst; // 上班考勤结果 1 正常 2 迟到 3早退 4未打卡
	@CFieldinfo(fieldname = "trst", caption = "下班考勤结果 1 正常 2 迟到 3早退 4未打卡", datetype = Types.INTEGER)
	public CField trst; // 下班考勤结果 1 正常 2 迟到 3早退 4未打卡
	@CFieldinfo(fieldname = "applyhours", caption = "申请时数", datetype = Types.DECIMAL)
	public CField applyhours; // 申请时数
	@CFieldinfo(fieldname = "otrate", notnull = true, caption = "加班倍率", datetype = Types.DECIMAL)
	public CField otrate; // 加班倍率
	@CFieldinfo(fieldname = "othours", caption = "加班时数", datetype = Types.DECIMAL)
	public CField othours; // 加班时数
	@CFieldinfo(fieldname = "deductoff", caption = "扣休息时数", datetype = Types.DECIMAL)
	public CField deductoff; // 扣休息时数
	@CFieldinfo(fieldname = "allfreetime", caption = "调休时长小时", datetype = Types.DECIMAL)
	public CField allfreetime; // 调休时长小时
	@CFieldinfo(fieldname = "freeedtime", caption = "已休息时间小时", datetype = Types.DECIMAL)
	public CField freeedtime; // 已休息时间小时
	@CFieldinfo(fieldname = "allotamont", caption = "加班费", datetype = Types.DECIMAL)
	public CField allotamont; // 加班费
	@CFieldinfo(fieldname = "payedotamont", caption = "已发放加班费", datetype = Types.DECIMAL)
	public CField payedotamont; // 已发放加班费
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hrkq_overtime_list() throws Exception {
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
