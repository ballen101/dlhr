package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hrkq_special_holdayline extends CJPA {
	@CFieldinfo(fieldname = "sphlid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField sphlid; // ID
	@CFieldinfo(fieldname = "sphid", notnull = true, caption = "调休ID", datetype = Types.INTEGER)
	public CField sphid; // 调休ID
	@CFieldinfo(fieldname = "orgid", notnull = true, caption = "部门ID", datetype = Types.INTEGER)
	public CField orgid; // 部门ID
	@CFieldinfo(fieldname = "orgcode", notnull = true, caption = "机构编码", datetype = Types.VARCHAR)
	public CField orgcode; // 机构编码
	@CFieldinfo(fieldname = "orgname", notnull = true, caption = "机构名称", datetype = Types.VARCHAR)
	public CField orgname; // 机构名称
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField er_id; // ID
	@CFieldinfo(fieldname = "employee_code", notnull = true, caption = "工号", datetype = Types.VARCHAR)
	public CField employee_code; // 工号
	@CFieldinfo(fieldname = "employee_name", notnull = true, caption = "姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 姓名
	@CFieldinfo(fieldname = "sp_name", notnull = true, caption = "职位", datetype = Types.VARCHAR)
	public CField sp_name; // 职位
	@CFieldinfo(fieldname = "lv_num", notnull = true, caption = "职级", datetype = Types.DECIMAL)
	public CField lv_num; // 职级
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hrkq_special_holdayline() throws Exception {
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
