package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shw_attach extends CJPA {
	@CFieldinfo(fieldname = "attid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField attid; // ID
	@CFieldinfo(fieldname = "create_time", caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField create_time; // 创建时间
	@CFieldinfo(fieldname = "creator", caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "note", caption = "备注", datetype = Types.VARCHAR)
	public CField note; // 备注
	@CFieldinfo(fieldname = "property1", caption = "", datetype = Types.VARCHAR)
	public CField property1; //
	@CFieldinfo(fieldname = "property2", caption = "", datetype = Types.VARCHAR)
	public CField property2; //
	@CFieldinfo(fieldname = "property3", caption = "", datetype = Types.VARCHAR)
	public CField property3; //
	@CFieldinfo(fieldname = "property4", caption = "", datetype = Types.VARCHAR)
	public CField property4; //
	@CFieldinfo(fieldname = "property5", caption = "", datetype = Types.VARCHAR)
	public CField property5; //
	@CFieldinfo(fieldname = "stat", caption = "", datetype = Types.INTEGER)
	public CField stat; //
	@CFieldinfo(fieldname = "wfid", caption = "", datetype = Types.INTEGER)
	public CField wfid; //
	@CFieldinfo(fieldname = "idpath", caption = "", datetype = Types.VARCHAR)
	public CField idpath; //
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义
	@CLinkFieldInfo(jpaclass = Shw_attach_line.class, linkFields = {
			@LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach_line> shw_attach_lines;

	public Shw_attach() throws Exception {
	}

	@Override
	public boolean InitObject() {// 类初始化调用的方法
		shw_attach_lines.addLinkField("attid", "attid");
		super.InitObject();
		return true;
	}

	@Override
	public boolean FinalObject() { // 类释放前调用的方法
		super.FinalObject();
		return true;
	}
}
