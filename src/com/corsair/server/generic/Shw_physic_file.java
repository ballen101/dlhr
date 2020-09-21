package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shw_physic_file extends CJPA {
	@CFieldinfo(fieldname = "pfid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField pfid; // ID
	@CFieldinfo(fieldname = "fsid", caption = "文件服务器ID", datetype = Types.INTEGER)
	public CField fsid; // 文件服务器ID
	@CFieldinfo(fieldname = "ppath", caption = "物理路径", datetype = Types.VARCHAR)
	public CField ppath; // 物理路径
	@CFieldinfo(fieldname = "pfname", caption = "物理文件名", datetype = Types.VARCHAR)
	public CField pfname; // 物理文件名
	@CFieldinfo(fieldname = "note", caption = "", datetype = Types.VARCHAR)
	public CField note; //
	@CFieldinfo(fieldname = "create_time", caption = "", datetype = Types.TIMESTAMP)
	public CField create_time; //
	@CFieldinfo(fieldname = "creator", caption = "", datetype = Types.VARCHAR)
	public CField creator; //
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
	@CFieldinfo(fieldname = "displayfname", caption = "", datetype = Types.VARCHAR)
	public CField displayfname; //
	@CFieldinfo(fieldname = "extname", caption = "", datetype = Types.VARCHAR)
	public CField extname; //
	@CFieldinfo(fieldname = "filesize", caption = "", datetype = Types.FLOAT)
	public CField filesize; //
	@CFieldinfo(fieldname = "filevision", caption = "", datetype = Types.VARCHAR)
	public CField filevision; //
	@CFieldinfo(fieldname = "ppfid", caption = "父文件ID", datetype = Types.INTEGER)
	public CField ppfid; // 父文件ID
	@CFieldinfo(fieldname = "ptype", caption = "扩展文件类型 1： 缩略图", datetype = Types.INTEGER)
	public CField ptype; // 扩展文件类型 1： 缩略图
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shw_attach_line getNewShw_attach_line() throws Exception {
		Shw_attach_line rst = new Shw_attach_line();
		rst.pfid.setValue(this.pfid.getValue());
		rst.fdrid.setAsInt(0);
		rst.displayfname.setValue(this.displayfname.getValue());
		rst.extname.setValue(this.extname.getValue());
		rst.filesize.setValue(this.filesize.getValue());
		return rst;
	}

	public Shw_physic_file() throws Exception {
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
