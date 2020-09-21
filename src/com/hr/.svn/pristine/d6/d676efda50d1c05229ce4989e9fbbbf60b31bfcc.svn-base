package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hrkq_leave_blance extends CJPA {
	@CFieldinfo(fieldname = "lbid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField lbid; // ID
	@CFieldinfo(fieldname = "lbname", notnull = true, caption = "标题", datetype = Types.VARCHAR)
	public CField lbname; // 标题
	@CFieldinfo(fieldname = "stype", notnull = true, caption = "源类型 1 年假 2 加班 3 值班 4出差", datetype = Types.INTEGER)
	public CField stype; // 源类型 1 年假 2 加班 3 值班 4出差 5特殊
	@CFieldinfo(fieldname = "sid", caption = "源ID", datetype = Types.INTEGER)
	public CField sid; // 源ID
	@CFieldinfo(fieldname = "sccode", caption = "源编码/年假的年份", datetype = Types.VARCHAR)
	public CField sccode; // 源编码/年假的年份
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "人事ID", datetype = Types.INTEGER)
	public CField er_id; // 人事ID
	@CFieldinfo(fieldname = "employee_code", notnull = true, caption = "工号", datetype = Types.VARCHAR)
	public CField employee_code; // 工号
	@CFieldinfo(fieldname = "employee_name", caption = "姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 姓名
	@CFieldinfo(fieldname = "hiredday", precision = 19, scale = 0, caption = "入职日期", datetype = Types.TIMESTAMP)
	public CField hiredday; // 入职日期
	@CFieldinfo(fieldname = "wkym", notnull = true, precision = 8, scale = 0, caption = "工龄", defvalue = "0", datetype = Types.INTEGER)
	public CField wkym; // 工龄
	@CFieldinfo(fieldname = "hg_name", caption = "职等名称", datetype = Types.VARCHAR)
	public CField hg_name; // 职等名称
	@CFieldinfo(fieldname = "njsp_name", precision = 128, scale = 0, caption = "职位名称", datetype = Types.VARCHAR)
	public CField njsp_name; // 职位名称
	@CFieldinfo(fieldname = "lv_num", caption = "职级", datetype = Types.DECIMAL)
	public CField lv_num; // 职级
	@CFieldinfo(fieldname = "orgid", notnull = true, caption = "部门ID", datetype = Types.INTEGER)
	public CField orgid; // 部门ID
	@CFieldinfo(fieldname = "orgcode", notnull = true, caption = "部门编码", datetype = Types.VARCHAR)
	public CField orgcode; // 部门编码
	@CFieldinfo(fieldname = "orgname", notnull = true, caption = "部门名称", datetype = Types.VARCHAR)
	public CField orgname; // 部门名称
	@CFieldinfo(fieldname = "alllbtime", notnull = true, caption = "可调休时间小时", datetype = Types.DECIMAL)
	public CField alllbtime; // 可调休时间小时
	@CFieldinfo(fieldname = "usedlbtime", notnull = true, caption = "已调休时间小时", datetype = Types.DECIMAL)
	public CField usedlbtime; // 已调休时间小时
	@CFieldinfo(fieldname = "valdate", notnull = true, caption = "有效期", datetype = Types.TIMESTAMP)
	public CField valdate; // 有效期
	@CFieldinfo(fieldname = "extended", caption = "是否延期", defvalue = "2", datetype = Types.INTEGER)
	public CField extended; // 是否延期
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "note", caption = "备注2", datetype = Types.VARCHAR)
	public CField note; // 备注2
	@CFieldinfo(fieldname = "idpath", notnull = true, caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hrkq_leave_blance() throws Exception {
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
