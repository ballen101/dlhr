package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption = "批量补签申请行")
public class Hrkq_resignbatchline extends CJPA {
	@CFieldinfo(fieldname = "resblid", iskey = true, notnull = true, precision = 10, scale = 0, caption = "补签行ID", datetype = Types.INTEGER)
	public CField resblid; // 补签行ID
	@CFieldinfo(fieldname = "resbid", notnull = true, precision = 10, scale = 0, caption = "补签ID", datetype = Types.INTEGER)
	public CField resbid; // 补签ID
	@CFieldinfo(fieldname = "er_id", notnull = true, precision = 10, scale = 0, caption = "申请人档案ID", datetype = Types.INTEGER)
	public CField er_id; // 申请人档案ID
	@CFieldinfo(fieldname = "employee_code", notnull = true, precision = 16, scale = 0, caption = "申请人工号", datetype = Types.VARCHAR)
	public CField employee_code; // 申请人工号
	@CFieldinfo(fieldname = "employee_name", notnull = true, precision = 64, scale = 0, caption = "申请人姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 申请人姓名
	@CFieldinfo(fieldname = "orgid", notnull = true, precision = 10, scale = 0, caption = "部门ID", datetype = Types.INTEGER)
	public CField orgid; // 部门ID
	@CFieldinfo(fieldname = "orgcode", notnull = true, precision = 16, scale = 0, caption = "部门编码", datetype = Types.VARCHAR)
	public CField orgcode; // 部门编码
	@CFieldinfo(fieldname = "orgname", notnull = true, precision = 128, scale = 0, caption = "部门名称", datetype = Types.VARCHAR)
	public CField orgname; // 部门名称
	@CFieldinfo(fieldname = "ospid", notnull = true, precision = 20, scale = 0, caption = "职位ID", datetype = Types.INTEGER)
	public CField ospid; // 职位ID
	@CFieldinfo(fieldname = "ospcode", notnull = true, precision = 16, scale = 0, caption = "职位编码", datetype = Types.VARCHAR)
	public CField ospcode; // 职位编码
	@CFieldinfo(fieldname = "sp_name", notnull = true, precision = 128, scale = 0, caption = "职位名称", datetype = Types.VARCHAR)
	public CField sp_name; // 职位名称
	@CFieldinfo(fieldname = "lv_num", notnull = true, precision = 4, scale = 1, caption = "职级", datetype = Types.DECIMAL)
	public CField lv_num; // 职级
	@CFieldinfo(fieldname = "kqdate", precision = 10, scale = 0, caption = "考勤日期", datetype = Types.DATE)
	public CField kqdate; // 考勤日期
	@CFieldinfo(fieldname = "bcno", precision = 64, scale = 0, caption = "班次号", datetype = Types.VARCHAR)
	public CField bcno; // 班次号
	@CFieldinfo(fieldname = "rgtime", notnull = true, precision = 5, scale = 0, caption = "打卡时间", datetype = Types.VARCHAR)
	public CField rgtime; // 打卡时间
	@CFieldinfo(fieldname = "sclid", precision = 10, scale = 0, caption = "班次行ID", defvalue = "1", datetype = Types.INTEGER)
	public CField sclid; // 班次行ID
	@CFieldinfo(fieldname = "scid", precision = 10, scale = 0, caption = "班次ID", datetype = Types.INTEGER)
	public CField scid; // 班次ID
	@CFieldinfo(fieldname = "scdname", precision = 16, scale = 0, caption = "班次名", datetype = Types.VARCHAR)
	public CField scdname; // 班次名
	@CFieldinfo(fieldname = "sxb", precision = 1, scale = 0, caption = "1 上班 2 下班", defvalue = "1", datetype = Types.INTEGER)
	public CField sxb; // 1 上班 2 下班
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hrkq_resignbatchline() throws Exception {
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
