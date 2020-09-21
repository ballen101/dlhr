package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.perm.ctr.CtrlHr_traintran_batch;

import java.sql.Types;

@CEntity(controller = CtrlHr_traintran_batch.class)
public class Hr_traintran_batch extends CJPA {
	@CFieldinfo(fieldname = "ttranb_id", iskey = true, notnull = true, caption = "批入ID", datetype = Types.INTEGER)
	public CField ttranb_id; // 批入ID
	@CFieldinfo(fieldname = "ttranb_code", codeid = 70, notnull = true, caption = "批转编码", datetype = Types.VARCHAR)
	public CField ttranb_code; // 批转编码
	@CFieldinfo(fieldname = "appdate", notnull = true, caption = "申请日期", datetype = Types.TIMESTAMP)
	public CField appdate; // 申请日期
	@CFieldinfo(fieldname = "ttranbdate", notnull = true, caption = "分配日期", datetype = Types.TIMESTAMP)
	public CField ttranbdate; // 分配日期
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "wfid", caption = "wfid", datetype = Types.INTEGER)
	public CField wfid; // wfid
	@CFieldinfo(fieldname = "attid", caption = "attid", datetype = Types.INTEGER)
	public CField attid; // attid
	@CFieldinfo(fieldname = "stat", notnull = true, caption = "流程状态", datetype = Types.INTEGER)
	public CField stat; // 流程状态
	@CFieldinfo(fieldname = "idpath", notnull = true, caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	@CFieldinfo(fieldname = "entid", notnull = true, caption = "entid", datetype = Types.INTEGER)
	public CField entid; // entid
	@CFieldinfo(fieldname = "creator", notnull = true, caption = "制单人", datetype = Types.VARCHAR)
	public CField creator; // 制单人
	@CFieldinfo(fieldname = "createtime", notnull = true, caption = "制单时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 制单时间
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
	@CFieldinfo(fieldname = "wfresult", caption = "评审结果1 通过 2 没通过", datetype = Types.INTEGER)
	public CField wfresult; // 评审结果1 通过 2 没通过
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义
	@CLinkFieldInfo(jpaclass = Hr_traintran_batchline.class, linkFields = { @LinkFieldItem(lfield = "ttranb_id", mfield = "ttranb_id") })
	public CJPALineData<Hr_traintran_batchline> hr_traintran_batchlines;
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hr_traintran_batch() throws Exception {
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
