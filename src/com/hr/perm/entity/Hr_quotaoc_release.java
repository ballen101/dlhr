package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.hr.perm.ctr.CtrHr_quotaoc_release;

import java.sql.Types;

@CEntity(controller = CtrHr_quotaoc_release.class)
public class Hr_quotaoc_release extends CJPA {
	@CFieldinfo(fieldname = "qocrlsid", iskey = true, notnull = true, caption = "发布单ID", datetype = Types.INTEGER)
	public CField qocrlsid; // 发布单ID
	@CFieldinfo(fieldname = "oocrlscode", codeid = 53, notnull = true, caption = "发布单编码", datetype = Types.VARCHAR)
	public CField oocrlscode; // 发布单编码
	@CFieldinfo(fieldname = "quota_year", notnull = true, caption = "编制年度", datetype = Types.VARCHAR)
	public CField quota_year; // 编制年度
	@CFieldinfo(fieldname = "quota_moth", caption = "编制月份", datetype = Types.VARCHAR)
	public CField quota_moth; // 编制月份
	@CFieldinfo(fieldname = "idpath", caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	@CFieldinfo(fieldname = "wfid", caption = "wfid", datetype = Types.INTEGER)
	public CField wfid; // wfid
	@CFieldinfo(fieldname = "attid", caption = "attid", datetype = Types.INTEGER)
	public CField attid; // attid
	@CFieldinfo(fieldname = "stat", caption = "stat", datetype = Types.INTEGER)
	public CField stat; // stat
	@CFieldinfo(fieldname = "entid", caption = "entid", datetype = Types.INTEGER)
	public CField entid; // entid
	@CFieldinfo(fieldname = "entname", caption = "entname", datetype = Types.VARCHAR)
	public CField entname; // entname
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "creator", caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", caption = "创建时间", datetype = Types.TIMESTAMP)
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
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义
	@CLinkFieldInfo(jpaclass = Hr_quotaoc_releaseline.class, linkFields = { @LinkFieldItem(lfield = "qocrlsid", mfield = "qocrlsid") })
	public CJPALineData<Hr_quotaoc_releaseline> hr_quotaoc_releaselines;

	public Hr_quotaoc_release() throws Exception {
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
