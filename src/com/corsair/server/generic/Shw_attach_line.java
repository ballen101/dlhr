package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shw_attach_line extends CJPA {
	@CFieldinfo(fieldname = "attlineid", iskey = true, notnull = true, caption = "附件文件ID", datetype = Types.INTEGER)
	public CField attlineid; // 附件文件ID
	@CFieldinfo(fieldname = "attid", caption = "附件ID", datetype = Types.INTEGER)
	public CField attid; // 附件ID
	@CFieldinfo(fieldname = "pfid", caption = "物理文件ID", datetype = Types.INTEGER)
	public CField pfid; // 物理文件ID
	@CFieldinfo(fieldname = "fdrid", caption = "文件夹ID 单据的附件 本属性为0", datetype = Types.INTEGER)
	public CField fdrid; // 文件夹ID 单据的附件 本属性为0
	@CFieldinfo(fieldname = "displayfname", caption = "显示的名称", datetype = Types.VARCHAR)
	public CField displayfname; // 显示的名称
	@CFieldinfo(fieldname = "extname", caption = "扩展文件名", datetype = Types.VARCHAR)
	public CField extname; // 扩展文件名
	@CFieldinfo(fieldname = "filesize", caption = "文件大小", datetype = Types.FLOAT)
	public CField filesize; // 文件大小
	@CFieldinfo(fieldname = "filevision", caption = "文件版本", datetype = Types.FLOAT)
	public CField filevision; // 文件版本
	@CFieldinfo(fieldname = "filecreator", caption = "所有者", datetype = Types.VARCHAR)
	public CField filecreator; // 所有者
	@CFieldinfo(fieldname = "filecreate_time", caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField filecreate_time; // 创建时间
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
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shw_attach_line() throws Exception {
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
