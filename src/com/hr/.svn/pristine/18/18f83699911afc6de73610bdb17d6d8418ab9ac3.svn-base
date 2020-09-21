package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.hr.attd.ctr.CtrHrkq_sched;

import java.sql.Types;

@CEntity(controller = CtrHrkq_sched.class)
public class Hrkq_sched extends CJPA {
	@CFieldinfo(fieldname = "scid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField scid; // ID
	@CFieldinfo(fieldname = "scdode", codeid = 75, notnull = true, caption = "班制编码", datetype = Types.VARCHAR)
	public CField scdode; // 班制编码
	@CFieldinfo(fieldname = "scdname", notnull = true, caption = "班制名称", datetype = Types.VARCHAR)
	public CField scdname; // 班制名称
	@CFieldinfo(fieldname = "orgid", notnull = true, caption = "机构ID", datetype = Types.INTEGER)
	public CField orgid; // 机构ID
	@CFieldinfo(fieldname = "orgcode", notnull = true, caption = "机构编码", datetype = Types.VARCHAR)
	public CField orgcode; // 机构编码
	@CFieldinfo(fieldname = "orgname", notnull = true, caption = "机构名称", datetype = Types.VARCHAR)
	public CField orgname; // 机构名称
	@CFieldinfo(fieldname = "backcolor", notnull = true, caption = "背景颜色", datetype = Types.VARCHAR)
	public CField backcolor; // 背景颜色
	@CFieldinfo(fieldname = "allworktime", caption = "合计时长H", datetype = Types.DECIMAL)
	public CField allworktime; // 合计时长H
	@CFieldinfo(fieldname = "lct", caption = "班次数", datetype = Types.INTEGER)
	public CField lct; // 班次数
	@CFieldinfo(fieldname = "sctype", precision = 1, scale = 0, caption = "1 白班 2 夜班", datetype = Types.INTEGER)
	public CField sctype; // 1 白班 2 夜班
	@CFieldinfo(fieldname = "note", caption = "备注", datetype = Types.VARCHAR)
	public CField note; // 备注
	@CFieldinfo(fieldname = "attid", caption = "attid", datetype = Types.INTEGER)
	public CField attid; // attid
	@CFieldinfo(fieldname = "entid", notnull = true, caption = "entid", datetype = Types.INTEGER)
	public CField entid; // entid
	@CFieldinfo(fieldname = "idpath", notnull = true, caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
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
	@CLinkFieldInfo(jpaclass = Hrkq_sched_line.class, linkFields = { @LinkFieldItem(lfield = "scid", mfield = "scid") })
	public CJPALineData<Hrkq_sched_line> hrkq_sched_lines;

	public Hrkq_sched() throws Exception {
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
