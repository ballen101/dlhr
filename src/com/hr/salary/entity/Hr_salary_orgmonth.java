package com.hr.salary.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.hr.salary.ctr.CtrHr_salary_orgmonth;

import java.sql.Types;

@CEntity(caption = "机构月薪表", tablename = "Hr_salary_orgmonth",controller=CtrHr_salary_orgmonth.class)
public class Hr_salary_orgmonth extends CJPA {
	@CFieldinfo(fieldname = "somid", iskey = true, notnull = true, precision = 10, scale = 0, caption = "ID", datetype = Types.INTEGER)
	public CField somid; // ID
	@CFieldinfo(fieldname = "somcode", notnull = true, codeid = 132, precision = 16, scale = 0, caption = "编码", datetype = Types.VARCHAR)
	public CField somcode; // 编码
	@CFieldinfo(fieldname = "orgid", notnull = true, precision = 10, scale = 0, caption = "部门ID", datetype = Types.INTEGER)
	public CField orgid; // 部门ID
	@CFieldinfo(fieldname = "orgcode", notnull = true, precision = 16, scale = 0, caption = "部门编码", datetype = Types.VARCHAR)
	public CField orgcode; // 部门编码
	@CFieldinfo(fieldname = "orgname", notnull = true, precision = 128, scale = 0, caption = "部门名称", datetype = Types.VARCHAR)
	public CField orgname; // 部门名称
	@CFieldinfo(fieldname = "yearmonth", notnull = true, precision = 7, scale = 0, caption = "年月", datetype = Types.VARCHAR)
	public CField yearmonth; // 年月
	@CFieldinfo(fieldname = "wagetype", notnull = true, precision = 1, scale = 0, caption = "月薪类型：1、月薪人员；2、非月薪人员", defvalue = "1", datetype = Types.INTEGER)
	public CField wagetype; // 月薪类型：1、月薪人员；2、非月薪人员
	@CFieldinfo(fieldname = "idpath", notnull = true, precision = 256, scale = 0, caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	@CFieldinfo(fieldname = "remark", precision = 512, scale = 0, caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "stat", notnull = true, precision = 2, scale = 0, caption = "流程状态", datetype = Types.INTEGER)
	public CField stat; // 流程状态
	@CFieldinfo(fieldname = "creator", notnull = true, precision = 32, scale = 0, caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", notnull = true, precision = 19, scale = 0, caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", precision = 32, scale = 0, caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", precision = 19, scale = 0, caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	@CFieldinfo(fieldname = "property1", precision = 32, scale = 0, caption = "备用字段1", datetype = Types.VARCHAR)
	public CField property1; // 备用字段1
	@CFieldinfo(fieldname = "property2", precision = 64, scale = 0, caption = "备用字段2", datetype = Types.VARCHAR)
	public CField property2; // 备用字段2
	@CFieldinfo(fieldname = "property3", precision = 32, scale = 0, caption = "备用字段3", datetype = Types.VARCHAR)
	public CField property3; // 备用字段3
	@CFieldinfo(fieldname = "property4", precision = 32, scale = 0, caption = "备用字段4", datetype = Types.VARCHAR)
	public CField property4; // 备用字段4
	@CFieldinfo(fieldname = "property5", precision = 32, scale = 0, caption = "备用字段5", datetype = Types.VARCHAR)
	public CField property5; // 备用字段5
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义
	@CLinkFieldInfo(jpaclass = Hr_salary_orgmonth_line.class, linkFields = { @LinkFieldItem(lfield = "somid", mfield = "somid") })
	public CJPALineData<Hr_salary_orgmonth_line> hr_salary_orgmonth_lines;

	public Hr_salary_orgmonth() throws Exception {
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
