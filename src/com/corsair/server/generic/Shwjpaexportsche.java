package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption = "jpa导出列表过滤方案", tablename = "Shwjpaexportsche")
public class Shwjpaexportsche extends CJPA {
	@CFieldinfo(fieldname = "jesid", iskey = true, notnull = true, precision = 10, scale = 0, caption = "ID", datetype = Types.INTEGER)
	public CField jesid; // ID
	@CFieldinfo(fieldname = "jesname", notnull = true, precision = 32, scale = 0, caption = "方案名称", datetype = Types.VARCHAR)
	public CField jesname; // 方案名称
	@CFieldinfo(fieldname = "ispublic", notnull = true, precision = 1, scale = 0, caption = "是否公有方案", defvalue = "1", datetype = Types.INTEGER)
	public CField ispublic; // 是否公有方案
	@CFieldinfo(fieldname = "jpaclassname", notnull = true, precision = 128, scale = 0, caption = "Class", datetype = Types.VARCHAR)
	public CField jpaclassname; // Class
	@CFieldinfo(fieldname = "jpaname", notnull = true, precision = 128, scale = 0, caption = "JPA名称", datetype = Types.VARCHAR)
	public CField jpaname; // JPA名称
	@CFieldinfo(fieldname = "ownuserid", precision = 20, scale = 0, caption = "用户ID", datetype = Types.INTEGER)
	public CField ownuserid; // 用户ID
	@CFieldinfo(fieldname = "ownusername", precision = 32, scale = 0, caption = "用户名", datetype = Types.VARCHAR)
	public CField ownusername; // 用户名
	@CFieldinfo(fieldname = "owndisplayname", precision = 32, scale = 0, caption = "用户姓名", datetype = Types.VARCHAR)
	public CField owndisplayname; // 用户姓名
	@CFieldinfo(fieldname = "expfields", notnull = true, precision = 1024, scale = 0, caption = "导出的字段 字符串数组", datetype = Types.VARCHAR)
	public CField expfields; // 导出的字段 字符串数组
	@CFieldinfo(fieldname = "creator", precision = 16, scale = 0, caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", precision = 19, scale = 0, caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", precision = 16, scale = 0, caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", precision = 19, scale = 0, caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	@CFieldinfo(fieldname = "attr1", precision = 32, scale = 0, caption = "备用1", datetype = Types.VARCHAR)
	public CField attr1; // 备用1
	@CFieldinfo(fieldname = "attr2", precision = 32, scale = 0, caption = "备用2", datetype = Types.VARCHAR)
	public CField attr2; // 备用2
	@CFieldinfo(fieldname = "attr3", precision = 32, scale = 0, caption = "备用3", datetype = Types.VARCHAR)
	public CField attr3; // 备用3
	@CFieldinfo(fieldname = "attr4", precision = 32, scale = 0, caption = "备用4", datetype = Types.VARCHAR)
	public CField attr4; // 备用4
	@CFieldinfo(fieldname = "attr5", precision = 32, scale = 0, caption = "备用5", datetype = Types.VARCHAR)
	public CField attr5; // 备用5
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwjpaexportsche() throws Exception {
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
