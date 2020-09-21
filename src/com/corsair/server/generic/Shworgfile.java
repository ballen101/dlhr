package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption = "机构文件，文件管理扩展")
public class Shworgfile extends CJPA {
	@CFieldinfo(fieldname = "ofid", iskey = true, notnull = true, precision = 10, scale = 0, caption = "ID", datetype = Types.INTEGER)
	public CField ofid; // ID
	@CFieldinfo(fieldname = "pfid", notnull = true, precision = 10, scale = 0, caption = "物理文件ID", datetype = Types.INTEGER)
	public CField pfid; // 物理文件ID
	@CFieldinfo(fieldname = "orgid", notnull = true, precision = 10, scale = 0, caption = "机构ID", datetype = Types.INTEGER)
	public CField orgid; // 机构ID
	@CFieldinfo(fieldname = "orgname", notnull = true, precision = 64, scale = 0, caption = "机构名称", datetype = Types.VARCHAR)
	public CField orgname; // 机构名称
	@CFieldinfo(fieldname = "suri", precision = 128, scale = 0, caption = "抓取源", datetype = Types.VARCHAR)
	public CField suri; // 抓取源
	@CFieldinfo(fieldname = "stime", precision = 19, scale = 0, caption = "抓取时间", datetype = Types.TIMESTAMP)
	public CField stime; // 抓取时间
	@CFieldinfo(fieldname = "mask", precision = 32, scale = 0, caption = "备注", datetype = Types.VARCHAR)
	public CField mask; // 备注
	@CFieldinfo(fieldname = "idpath", notnull = true, precision = 256, scale = 0, caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	@CFieldinfo(fieldname = "ptype", precision = 2, scale = 0, caption = "类型 扩展", datetype = Types.INTEGER)
	public CField ptype; // 类型 扩展
	@CFieldinfo(fieldname = "entid", notnull = true, precision = 5, scale = 0, caption = "entid", datetype = Types.INTEGER)
	public CField entid; // entid
	@CFieldinfo(fieldname = "creator", notnull = true, precision = 32, scale = 0, caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", notnull = true, precision = 19, scale = 0, caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "attr1", precision = 32, scale = 0, caption = "备用字段1", datetype = Types.VARCHAR)
	public CField attr1; // 备用字段1
	@CFieldinfo(fieldname = "attr2", precision = 32, scale = 0, caption = "备用字段2", datetype = Types.VARCHAR)
	public CField attr2; // 备用字段2
	@CFieldinfo(fieldname = "attr3", precision = 32, scale = 0, caption = "备用字段3", datetype = Types.VARCHAR)
	public CField attr3; // 备用字段3
	@CFieldinfo(fieldname = "attr4", precision = 32, scale = 0, caption = "备用字段4", datetype = Types.VARCHAR)
	public CField attr4; // 备用字段4
	@CFieldinfo(fieldname = "attr5", precision = 32, scale = 0, caption = "备用字段5", datetype = Types.VARCHAR)
	public CField attr5; // 备用字段5
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shworgfile() throws Exception {
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
