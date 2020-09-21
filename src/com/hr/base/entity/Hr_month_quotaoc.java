package com.hr.base.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption = "机构职类编制月结")
public class Hr_month_quotaoc extends CJPA {
	@CFieldinfo(fieldname = "mid", iskey = true, autoinc = true, notnull = true, precision = 20, scale = 0, caption = "ID", datetype = Types.INTEGER)
	public CField mid; // ID
	@CFieldinfo(fieldname = "yearmonth", notnull = true, precision = 7, scale = 0, caption = "年月", datetype = Types.VARCHAR)
	public CField yearmonth; // 年月
	@CFieldinfo(fieldname = "ocqid", notnull = true, precision = 20, scale = 0, caption = "机构职类编制ID", datetype = Types.INTEGER)
	public CField ocqid; // 机构职类编制ID
	@CFieldinfo(fieldname = "orgid", notnull = true, precision = 20, scale = 0, caption = "机构ID", datetype = Types.INTEGER)
	public CField orgid; // 机构ID
	@CFieldinfo(fieldname = "orgname", notnull = true, precision = 64, scale = 0, caption = "机构名称", datetype = Types.VARCHAR)
	public CField orgname; // 机构名称
	@CFieldinfo(fieldname = "classid", notnull = true, precision = 20, scale = 0, caption = "职类ID", datetype = Types.INTEGER)
	public CField classid; // 职类ID
	@CFieldinfo(fieldname = "hwc_namezl", precision = 128, scale = 0, caption = "职类", datetype = Types.VARCHAR)
	public CField hwc_namezl; // 职类
	@CFieldinfo(fieldname = "quota", notnull = true, precision = 10, scale = 0, caption = "编制数量", defvalue = "0", datetype = Types.INTEGER)
	public CField quota; // 编制数量
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hr_month_quotaoc() throws Exception {
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
