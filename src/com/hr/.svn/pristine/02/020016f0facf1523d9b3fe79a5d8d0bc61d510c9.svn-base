package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.perm.ctr.CtrHr_entrybatch_prob;

import java.sql.Types;

@CEntity(controller = CtrHr_entrybatch_prob.class)
public class Hr_entrybatch_prob extends CJPA {
	@CFieldinfo(fieldname = "ebptid", iskey = true, notnull = true, caption = "转正ID", datetype = Types.INTEGER)
	public CField ebptid; // 转正ID
	@CFieldinfo(fieldname = "ebpcode", codeid = 110, notnull = true, caption = "转正编码", datetype = Types.VARCHAR)
	public CField ebpcode; // 转正编码
	@CFieldinfo(fieldname = "pbttype1", notnull = true, caption = "转正类型 入职转正", defvalue = "2", datetype = Types.VARCHAR)
	public CField pbttype1; // 转正类型 入职转正
	@CFieldinfo(fieldname = "pbttype2", notnull = true, caption = "转正类别 OO类批量转正", defvalue = "2", datetype = Types.VARCHAR)
	public CField pbttype2; // 转正类别 OO类批量转正
	@CFieldinfo(fieldname = "orgid", notnull = true, caption = "部门ID", datetype = Types.INTEGER)
	public CField orgid; // 部门ID
	@CFieldinfo(fieldname = "orgcode", notnull = true, caption = "部门编码", datetype = Types.VARCHAR)
	public CField orgcode; // 部门编码
	@CFieldinfo(fieldname = "orgname", notnull = true, caption = "部门名称", datetype = Types.VARCHAR)
	public CField orgname; // 部门名称
	@CFieldinfo(fieldname = "orghrlev", notnull = true, caption = "机构人事层级", defvalue = "0", datetype = Types.INTEGER)
	public CField orghrlev; // 机构人事层级
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
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义
	@CLinkFieldInfo(jpaclass = Hr_entrybatch_probline.class, linkFields = { @LinkFieldItem(lfield = "ebptid", mfield = "ebptid") })
	public CJPALineData<Hr_entrybatch_probline> hr_entrybatch_problines;
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hr_entrybatch_prob() throws Exception {
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
