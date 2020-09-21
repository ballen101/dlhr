package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shwnotice extends CJPA {
	@CFieldinfo(fieldname = "noticeid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField noticeid; // ID
	@CFieldinfo(fieldname = "noticecode", codeid = 29, notnull = true, caption = "通知编号", datetype = Types.VARCHAR)
	public CField noticecode; // 通知编号
	@CFieldinfo(fieldname = "noticetype", caption = "类别", datetype = Types.INTEGER)
	public CField noticetype; // 类别
	@CFieldinfo(fieldname = "title", notnull = true, caption = "标题", datetype = Types.VARCHAR)
	public CField title; // 标题
	@CFieldinfo(fieldname = "note", caption = "备注", datetype = Types.VARCHAR)
	public CField note; // 备注
	@CFieldinfo(fieldname = "fpname", caption = "通知文件名称", datetype = Types.VARCHAR)
	public CField fpname; // 通知文件名称
	@CFieldinfo(fieldname = "pfid", caption = "物理文件ID", datetype = Types.FLOAT)
	public CField pfid; // 物理文件ID
	@CFieldinfo(fieldname = "usable", caption = "可用", datetype = Types.FLOAT)
	public CField usable; // 可用
	@CFieldinfo(fieldname = "attid", caption = "附件ID", datetype = Types.INTEGER)
	public CField attid; // 附件ID
	@CFieldinfo(fieldname = "stat", caption = "状态", datetype = Types.INTEGER)
	public CField stat; // 状态
	@CFieldinfo(fieldname = "wfid", caption = "", datetype = Types.INTEGER)
	public CField wfid; //
	@CFieldinfo(fieldname = "creator", caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "create_time", caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField create_time; // 创建时间
	@CFieldinfo(fieldname = "updator", caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "update_time", caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField update_time; // 更新时间
	@CFieldinfo(fieldname = "entid", notnull = true, caption = "组织ID", datetype = Types.FLOAT)
	public CField entid; // 组织ID
	@CFieldinfo(fieldname = "entname", caption = "组织名称", datetype = Types.VARCHAR)
	public CField entname; // 组织名称
	@CFieldinfo(fieldname = "property1", caption = "", datetype = Types.FLOAT)
	public CField property1; //
	@CFieldinfo(fieldname = "property2", caption = "", datetype = Types.FLOAT)
	public CField property2; //
	@CFieldinfo(fieldname = "property3", caption = "", datetype = Types.FLOAT)
	public CField property3; //
	@CFieldinfo(fieldname = "property4", caption = "", datetype = Types.FLOAT)
	public CField property4; //
	@CFieldinfo(fieldname = "property5", caption = "", datetype = Types.FLOAT)
	public CField property5; //
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义
	public CJPALineData<Shw_attach> shw_attachs;

	public Shwnotice() throws Exception {
	}

	@Override
	public boolean InitObject() {// 类初始化调用的方法
		super.InitObject();
		shw_attachs.addLinkField("attid", "attid");
		return true;
	}

	@Override
	public boolean FinalObject() { // 类释放前调用的方法
		super.FinalObject();
		return true;
	}
}
