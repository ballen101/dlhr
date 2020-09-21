package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.attd.ctr.CtrHrkq_resignbatch;

import java.sql.Types;

@CEntity(controller = CtrHrkq_resignbatch.class, caption = "批量补签申请")
public class Hrkq_resignbatch extends CJPA {
	@CFieldinfo(fieldname = "resbid", iskey = true, notnull = true, precision = 10, scale = 0, caption = "补签ID", datetype = Types.INTEGER)
	public CField resbid; // 补签ID
	@CFieldinfo(fieldname = "resbcode", codeid = 118, notnull = true, precision = 16, scale = 0, caption = "补签编码", datetype = Types.VARCHAR)
	public CField resbcode; // 补签编码
	@CFieldinfo(fieldname = "resdate", notnull = true, precision = 19, scale = 0, caption = "申请日期", datetype = Types.TIMESTAMP)
	public CField resdate; // 申请日期
	@CFieldinfo(fieldname = "orgid", notnull = true, precision = 10, scale = 0, caption = "部门ID", datetype = Types.INTEGER)
	public CField orgid; // 部门ID
	@CFieldinfo(fieldname = "orgcode", notnull = true, precision = 16, scale = 0, caption = "部门编码", datetype = Types.VARCHAR)
	public CField orgcode; // 部门编码
	@CFieldinfo(fieldname = "orgname", notnull = true, precision = 128, scale = 0, caption = "部门名称", datetype = Types.VARCHAR)
	public CField orgname; // 部门名称
	@CFieldinfo(fieldname="orghrlev",precision=2,scale=0,caption="机构人事层级",datetype=Types.INTEGER)
	public CField orghrlev;  //机构层级
	@CFieldinfo(fieldname = "resreson", precision = 64, scale = 0, caption = "补签原因", datetype = Types.VARCHAR)
	public CField resreson; // 补签原因
	@CFieldinfo(fieldname = "remark", precision = 128, scale = 0, caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "wfid", precision = 20, scale = 0, caption = "wfid", datetype = Types.INTEGER)
	public CField wfid; // wfid
	@CFieldinfo(fieldname = "attid", precision = 20, scale = 0, caption = "attid", datetype = Types.INTEGER)
	public CField attid; // attid
	@CFieldinfo(fieldname = "stat", notnull = true, precision = 2, scale = 0, caption = "表单状态", datetype = Types.INTEGER)
	public CField stat; // 表单状态
	@CFieldinfo(fieldname = "idpath", notnull = true, precision = 256, scale = 0, caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	@CFieldinfo(fieldname = "entid", notnull = true, precision = 5, scale = 0, caption = "entid", datetype = Types.INTEGER)
	public CField entid; // entid
	@CFieldinfo(fieldname = "creator", notnull = true, precision = 32, scale = 0, caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", notnull = true, precision = 19, scale = 0, caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", precision = 32, scale = 0, caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", precision = 19, scale = 0, caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
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
	@CLinkFieldInfo(jpaclass = Hrkq_resignbatchline.class, linkFields = { @LinkFieldItem(lfield = "resbid", mfield = "resbid") })
	public CJPALineData<Hrkq_resignbatchline> hrkq_resignbatchlines;
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hrkq_resignbatch() throws Exception {
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
