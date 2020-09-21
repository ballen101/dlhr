package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hrkq_bckqrst extends CJPA {
	@CFieldinfo(fieldname = "bckqid", iskey = true, notnull = true, caption = "结果ID", datetype = Types.INTEGER)
	public CField bckqid; // 结果ID
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "申请人档案ID", datetype = Types.INTEGER)
	public CField er_id; // 申请人档案ID
	@CFieldinfo(fieldname = "kqdate", notnull = true, caption = "考勤日期", datetype = Types.TIMESTAMP)
	public CField kqdate; // 考勤日期
	@CFieldinfo(fieldname = "scdname", notnull = true, caption = "班制", datetype = Types.VARCHAR)
	public CField scdname; // 班制
	@CFieldinfo(fieldname = "sclid", notnull = true, caption = "班次ID", datetype = Types.INTEGER)
	public CField sclid; // 班次ID
	@CFieldinfo(fieldname = "bcno", notnull = true, caption = "班次号", datetype = Types.INTEGER)
	public CField bcno; // 班次号
	@CFieldinfo(fieldname = "frtime", notnull = true, caption = "上班时间", datetype = Types.VARCHAR)
	public CField frtime; // 上班时间
	@CFieldinfo(fieldname = "totime", notnull = true, caption = "下班时间", datetype = Types.VARCHAR)
	public CField totime; // 下班时间
	@CFieldinfo(fieldname = "frsktime", notnull = false, caption = "上班打卡时间", datetype = Types.VARCHAR)
	public CField frsktime; // 上班打卡时间
	@CFieldinfo(fieldname = "tosktime", notnull = false, caption = "下班打卡时间", datetype = Types.VARCHAR)
	public CField tosktime; // 下班打卡时间
	@CFieldinfo(fieldname = "frst", notnull = true, caption = "上班考勤结果 1 正常 2 迟到 3早退 4未打卡 5 出差 6 请假 7 调休", datetype = Types.INTEGER)
	public CField frst; // 上班考勤结果 1 正常 2 迟到 3早退 4未打卡 5 出差 6 请假 7 调休 8;// 签卡 9;// 旷工(迟到) 10;// 旷工(早退)
	@CFieldinfo(fieldname = "trst", notnull = true, caption = "上班考勤结果 1 正常 2 迟到 3早退 4未打卡 5 出差 6 请假 7 调休", datetype = Types.INTEGER)
	public CField trst; // 上班考勤结果 1 正常 2 迟到 3早退 4未打卡 5 出差 6 请假 7 调休
	@CFieldinfo(fieldname = "mtslate", caption = "迟到时间分钟", datetype = Types.INTEGER)
	public CField mtslate; // 迟到时间分钟
	@CFieldinfo(fieldname = "mtslearly", caption = "早退时间分钟", datetype = Types.INTEGER)
	public CField mtslearly; // 早退时间分钟
	@CFieldinfo(fieldname = "lrst", caption = "班次考勤结果", defvalue = "0", datetype = Types.INTEGER)
	public CField lrst; // 班次考勤结果 //11;// 迟到早退 12;// 旷工(旷工)
	@CFieldinfo(fieldname = "extcode", caption = "如果是出差、请假、调休 则为对应单号", datetype = Types.VARCHAR)
	public CField extcode; // 如果是出差、请假、调休 则为对应单号
	@CFieldinfo(fieldname = "exttype", caption = "请假时：假期类型", datetype = Types.VARCHAR)
	public CField exttype; // 请假时：假期类型
	@CFieldinfo(fieldname = "isabnom", caption = "是否异常", datetype = Types.INTEGER)
	public CField isabnom; // 是否异常
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hrkq_bckqrst() throws Exception {
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
