package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hrkq_sched_line extends CJPA {
	@CFieldinfo(fieldname = "sclid", iskey = true, notnull = true, precision = 20, scale = 0, caption = "LID", datetype = Types.INTEGER)
	public CField sclid; // LID
	@CFieldinfo(fieldname = "scid", notnull = true, precision = 20, scale = 0, caption = "ID", datetype = Types.INTEGER)
	public CField scid; // ID
	@CFieldinfo(fieldname = "bcno", notnull = true, precision = 32, scale = 0, caption = "班次号", datetype = Types.VARCHAR)
	public CField bcno; // 班次号
	@CFieldinfo(fieldname = "dayratio", precision = 5, scale = 2, caption = "天比例", datetype = Types.DECIMAL)
	public CField dayratio; // 天比例
	@CFieldinfo(fieldname = "frtime", notnull = true, precision = 6, scale = 0, caption = "上班时间", datetype = Types.VARCHAR)
	public CField frtime; // 上班时间
	@CFieldinfo(fieldname = "totime", notnull = true, precision = 6, scale = 0, caption = "下班时间", datetype = Types.VARCHAR)
	public CField totime; // 下班时间
	@CFieldinfo(fieldname = "frvtimebg", notnull = true, precision = 6, scale = 0, caption = "上班打卡有效时间开始", datetype = Types.VARCHAR)
	public CField frvtimebg; // 上班打卡有效时间开始
	@CFieldinfo(fieldname = "frvtimeed", notnull = true, precision = 6, scale = 0, caption = "上班打卡有效时间结束", datetype = Types.VARCHAR)
	public CField frvtimeed; // 上班打卡有效时间结束
	@CFieldinfo(fieldname = "tovtimebg", notnull = true, precision = 6, scale = 0, caption = "下班打卡有效时间开始", datetype = Types.VARCHAR)
	public CField tovtimebg; // 下班打卡有效时间开始
	@CFieldinfo(fieldname = "tovtimeed", notnull = true, precision = 6, scale = 0, caption = "下班打卡有效时间结束", datetype = Types.VARCHAR)
	public CField tovtimeed; // 下班打卡有效时间结束
	@CFieldinfo(fieldname = "frnoclock", notnull = true, precision = 1, scale = 0, caption = "上班免打卡", defvalue = "2", datetype = Types.INTEGER)
	public CField frnoclock; // 上班免打卡
	@CFieldinfo(fieldname = "tonoclock", notnull = true, precision = 1, scale = 0, caption = "上班免打卡", defvalue = "2", datetype = Types.INTEGER)
	public CField tonoclock; // 上班免打卡
	@CFieldinfo(fieldname = "noclockabs", notnull = true, precision = 1, scale = 0, caption = "未打卡是否记旷工", defvalue = "1", datetype = Types.INTEGER)
	public CField noclockabs; // 未打卡是否记旷工
	@CFieldinfo(fieldname = "frbfwe", notnull = true, precision = 1, scale = 0, caption = "上班提前打卡记加班", defvalue = "2", datetype = Types.INTEGER)
	public CField frbfwe; // 上班提前打卡记加班
	@CFieldinfo(fieldname = "frbfwemin", notnull = true, precision = 4, scale = 0, caption = "上班提前打卡记加班最少", defvalue = "2", datetype = Types.INTEGER)
	public CField frbfwemin; // 上班提前打卡记加班最少
	@CFieldinfo(fieldname = "frbfwemax", notnull = true, precision = 4, scale = 0, caption = "上班提前打卡记加班最长", defvalue = "2", datetype = Types.INTEGER)
	public CField frbfwemax; // 上班提前打卡记加班最长
	@CFieldinfo(fieldname = "frafabn", notnull = true, precision = 1, scale = 0, caption = "上班推迟打卡记是否记迟到", defvalue = "1", datetype = Types.INTEGER)
	public CField frafabn; // 上班推迟打卡记是否记迟到
	@CFieldinfo(fieldname = "frafextimeabn", notnull = true, precision = 3, scale = 0, caption = "上班推迟打卡超过 分钟记迟到", defvalue = "0", datetype = Types.INTEGER)
	public CField frafextimeabn; // 上班推迟打卡超过 分钟记迟到
	@CFieldinfo(fieldname = "frafextimeabt", notnull = true, precision = 3, scale = 0, caption = "上班推迟打卡超过 分钟记旷工", defvalue = "30", datetype = Types.INTEGER)
	public CField frafextimeabt; // 上班推迟打卡超过 分钟记旷工
	@CFieldinfo(fieldname = "toafwe", notnull = true, precision = 1, scale = 0, caption = "下班推迟打卡记加班", defvalue = "2", datetype = Types.INTEGER)
	public CField toafwe; // 下班推迟打卡记加班
	@CFieldinfo(fieldname = "toafwemin", notnull = true, precision = 4, scale = 0, caption = "下班推迟打卡记加班最少", defvalue = "2", datetype = Types.INTEGER)
	public CField toafwemin; // 下班推迟打卡记加班最少
	@CFieldinfo(fieldname = "toafwemax", notnull = true, precision = 4, scale = 0, caption = "下班推迟打卡记加班最长", defvalue = "2", datetype = Types.INTEGER)
	public CField toafwemax; // 下班推迟打卡记加班最长
	@CFieldinfo(fieldname = "tobfabn", notnull = true, precision = 1, scale = 0, caption = "下班提前打卡记是否记早退", defvalue = "1", datetype = Types.INTEGER)
	public CField tobfabn; // 下班提前打卡记是否记早退
	@CFieldinfo(fieldname = "tobfextimeabn", notnull = true, precision = 3, scale = 0, caption = "下班提前打卡超过时间记早退 分钟", defvalue = "0", datetype = Types.INTEGER)
	public CField tobfextimeabn; // 下班提前打卡超过时间记早退 分钟
	@CFieldinfo(fieldname = "tobfextimeabt", notnull = true, precision = 3, scale = 0, caption = "下班提前打卡超过 分钟记旷工", defvalue = "30", datetype = Types.INTEGER)
	public CField tobfextimeabt; // 下班提前打卡超过 分钟记旷工
	@CFieldinfo(fieldname = "worktime", notnull = true, precision = 4, scale = 0, caption = "合计时长H", defvalue = "0", datetype = Types.INTEGER)
	public CField worktime; // 合计时长H
	@CFieldinfo(fieldname = "exworktime", notnull = true, precision = 4, scale = 0, caption = "加班时长H", defvalue = "0", datetype = Types.INTEGER)
	public CField exworktime; // 加班时长H
	@CFieldinfo(fieldname = "exdealtype", precision = 1, scale = 0, caption = "加班处理 1 调休 2 计加班费", defvalue = "1", datetype = Types.INTEGER)
	public CField exdealtype; // 加班处理 1 调休 2 计加班费
	@CFieldinfo(fieldname = "exfreetime", notnull = true, precision = 4, scale = 0, caption = "扣休息时长H", defvalue = "0", datetype = Types.INTEGER)
	public CField exfreetime; // 扣休息时长H
	@CFieldinfo(fieldname = "realworktime", notnull = true, precision = 4, scale = 0, caption = "合计时长H", defvalue = "0", datetype = Types.INTEGER)
	public CField realworktime; // 合计时长H
	@CFieldinfo(fieldname = "over_type", notnull = true, precision = 1, scale = 0, caption = "加班类型", defvalue = "1", datetype = Types.INTEGER)
	public CField over_type; // 加班类型
	@CFieldinfo(fieldname = "attribute1", precision = 32, scale = 0, caption = "备用字段1", datetype = Types.VARCHAR)
	public CField attribute1; // 备用字段1
	@CFieldinfo(fieldname = "attribute2", precision = 32, scale = 0, caption = "备用字段2", datetype = Types.VARCHAR)
	public CField attribute2; // 备用字段2
	@CFieldinfo(fieldname = "attribute3", precision = 32, scale = 0, caption = "备用字段3", datetype = Types.VARCHAR)
	public CField attribute3; // 备用字段3
	@CFieldinfo(fieldname = "attribute4", precision = 32, scale = 0, caption = "备用字段4", datetype = Types.VARCHAR)
	public CField attribute4; // 备用字段4
	@CFieldinfo(fieldname = "attribute5", precision = 32, scale = 0, caption = "备用字段5", datetype = Types.VARCHAR)
	public CField attribute5; // 备用字段5
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hrkq_sched_line() throws Exception {
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
