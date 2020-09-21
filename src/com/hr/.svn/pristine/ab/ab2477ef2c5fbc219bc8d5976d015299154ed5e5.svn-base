package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hrkq_holidayapp_cancel_month extends CJPA {
	@CFieldinfo(fieldname = "hacmid", iskey = true, notnull = true, caption = "销假分解ID", datetype = Types.INTEGER)
	public CField hacmid; // 销假分解ID
	@CFieldinfo(fieldname = "hamid", notnull = true, caption = "申请分解ID", datetype = Types.INTEGER)
	public CField hamid; // 申请分解ID
	@CFieldinfo(fieldname = "hacid", notnull = true, caption = "销假ID", datetype = Types.INTEGER)
	public CField hacid; // 销假ID
	@CFieldinfo(fieldname = "haid", notnull = true, caption = "申请ID", datetype = Types.INTEGER)
	public CField haid; // 申请ID
	@CFieldinfo(fieldname = "yearmonth", caption = "年月 YYYY-MM", datetype = Types.VARCHAR)
	public CField yearmonth; // 年月 YYYY-MM
	@CFieldinfo(fieldname = "lhdays", notnull = true, caption = "分解天数", datetype = Types.DECIMAL)
	public CField lhdays; // 分解天数
	@CFieldinfo(fieldname = "lhdaystrue", caption = "实际分解天数 如无销假则为分解天数", datetype = Types.DECIMAL)
	public CField lhdaystrue; // 实际分解天数 如无销假则为分解天数
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hrkq_holidayapp_cancel_month() throws Exception {
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
