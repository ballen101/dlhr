package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(tablename = "Shwjpaattr")
public class Shwjpaattr extends CJPA {
	@CFieldinfo(fieldname = "jaid", iskey = true, notnull = true, precision = 10, scale = 0, caption = "ID", datetype = Types.INTEGER)
	public CField jaid; // ID
	@CFieldinfo(fieldname = "jpaclassname", notnull = true, precision = 128, scale = 0, caption = "Class", datetype = Types.VARCHAR)
	public CField jpaclassname; // Class
	@CFieldinfo(fieldname = "jpaname", notnull = true, precision = 128, scale = 0, caption = "JPA名称", datetype = Types.VARCHAR)
	public CField jpaname; // JPA名称
	@CFieldinfo(fieldname="colct",notnull=true,precision=2,scale=0,caption="列数",defvalue="2",datetype=Types.INTEGER)
	public CField colct;  //列数
	@CFieldinfo(fieldname = "creator", precision = 16, scale = 0, caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", precision = 19, scale = 0, caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", precision = 16, scale = 0, caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", precision = 19, scale = 0, caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	@CFieldinfo(fieldname = "attribute1", precision = 32, scale = 0, caption = "备用1", datetype = Types.VARCHAR)
	public CField attribute1; // 备用1
	@CFieldinfo(fieldname = "attribute2", precision = 32, scale = 0, caption = "备用2", datetype = Types.VARCHAR)
	public CField attribute2; // 备用2
	@CFieldinfo(fieldname = "attribute3", precision = 32, scale = 0, caption = "备用3", datetype = Types.VARCHAR)
	public CField attribute3; // 备用3
	@CFieldinfo(fieldname = "attribute4", precision = 32, scale = 0, caption = "备用4", datetype = Types.VARCHAR)
	public CField attribute4; // 备用4
	@CFieldinfo(fieldname = "attribute5", precision = 32, scale = 0, caption = "备用5", datetype = Types.VARCHAR)
	public CField attribute5; // 备用5
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义
	@CLinkFieldInfo(jpaclass = Shwjpaattrline.class, linkFields = { @LinkFieldItem(lfield = "jaid", mfield = "jaid") })
	public CJPALineData<Shwjpaattrline> shwjpaattrlines;

	public Shwjpaattr() throws Exception {
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
