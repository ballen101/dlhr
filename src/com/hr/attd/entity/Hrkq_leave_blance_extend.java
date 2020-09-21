package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import com.hr.attd.ctr.CtrHrkq_leave_blance_extend;

import java.sql.Types;

@CEntity(controller = CtrHrkq_leave_blance_extend.class)
public class Hrkq_leave_blance_extend extends CJPA {
	@CFieldinfo(fieldname = "lbeid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField lbeid; // ID
	@CFieldinfo(fieldname = "lbecode", codeid = 92, caption = "展期编码", datetype = Types.VARCHAR)
	public CField lbecode; // 展期编码
	@CFieldinfo(fieldname = "newvaldate", notnull = true, caption = "展期有效期", datetype = Types.TIMESTAMP)
	public CField newvaldate; // 展期有效期
	@CFieldinfo(fieldname = "ext_reason", caption = "展期原因", datetype = Types.VARCHAR)
	public CField ext_reason; // 展期原因
	@CFieldinfo(fieldname = "lbid", notnull = true, caption = "调休流水", datetype = Types.INTEGER)
	public CField lbid; // 调休流水
	@CFieldinfo(fieldname = "lbname", notnull = true, caption = "标题", datetype = Types.VARCHAR)
	public CField lbname; // 标题
	@CFieldinfo(fieldname = "stype", notnull = true, caption = "源类型 1 年假 2 加班 3 值班 4出差 5特殊", datetype = Types.INTEGER)
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
	@CFieldinfo(fieldname = "hg_name", caption = "职等名称", datetype = Types.VARCHAR)
	public CField hg_name; // 职等名称
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
	@CFieldinfo(fieldname = "valdate", notnull = true, caption = "原有效期", datetype = Types.TIMESTAMP)
	public CField valdate; // 原有效期
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "wfid", caption = "wfid", datetype = Types.INTEGER)
	public CField wfid; // wfid
	@CFieldinfo(fieldname = "attid", caption = "attid", datetype = Types.INTEGER)
	public CField attid; // attid
	@CFieldinfo(fieldname = "stat", notnull = true, caption = "表单状态", datetype = Types.INTEGER)
	public CField stat; // 表单状态
	@CFieldinfo(fieldname = "idpath", notnull = true, caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	@CFieldinfo(fieldname = "entid", notnull = true, caption = "entid", datetype = Types.INTEGER)
	public CField entid; // entid
	@CFieldinfo(fieldname = "creator", notnull = true, caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", notnull = true, caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	@CFieldinfo(fieldname = "attribute1", caption = "备用字段1", datetype = Types.VARCHAR)
	public CField attribute1; // 备用字段1
	@CFieldinfo(fieldname = "attribute2", caption = "备用字段2", datetype = Types.VARCHAR)
	public CField attribute2; // 备用字段2
	@CFieldinfo(fieldname = "attribute3", caption = "备用字段3", datetype = Types.VARCHAR)
	public CField attribute3; // 备用字段3
	@CFieldinfo(fieldname = "attribute4", caption = "备用字段4", datetype = Types.VARCHAR)
	public CField attribute4; // 备用字段4
	@CFieldinfo(fieldname = "attribute5", caption = "备用字段5", datetype = Types.VARCHAR)
	public CField attribute5; // 备用字段5
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hrkq_leave_blance_extend() throws Exception {
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
