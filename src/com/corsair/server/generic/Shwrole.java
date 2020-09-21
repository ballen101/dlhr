package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shwrole extends CJPA {
	@CFieldinfo(fieldname = "roleid", iskey = true, notnull = true, datetype = Types.VARCHAR)
	public CField roleid; // ID
	@CFieldinfo(fieldname = "rolename", notnull = true, datetype = Types.VARCHAR)
	public CField rolename; // 名称
	@CFieldinfo(fieldname = "clas", datetype = Types.FLOAT)
	public CField clas; //
	@CFieldinfo(fieldname = "maxspace", datetype = Types.FLOAT)
	public CField maxspace; // 空间限制
	@CFieldinfo(fieldname = "note", datetype = Types.VARCHAR)
	public CField note; // 备注
	@CFieldinfo(fieldname = "maxemailspace", datetype = Types.FLOAT)
	public CField maxemailspace; // 邮件空间
	@CFieldinfo(fieldname = "fieldid", datetype = Types.FLOAT)
	public CField fieldid; //
	@CFieldinfo(fieldname = "maxattachsize", datetype = Types.FLOAT)
	public CField maxattachsize; // 附件限制
	@CFieldinfo(fieldname = "creator", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "updator", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "createtime", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updatetime", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	@CFieldinfo(fieldname = "entid", datetype = Types.INTEGER)
	public CField entid; // 组织
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwrole() throws Exception {
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
