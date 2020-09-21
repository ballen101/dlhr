package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shwuserexeclog extends CJPA {
	@CFieldinfo(fieldname = "ecid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField ecid; // ID
	@CFieldinfo(fieldname = "userid", caption = "userID", datetype = Types.INTEGER)
	public CField userid; // userID
	@CFieldinfo(fieldname = "username", caption = "", datetype = Types.VARCHAR)
	public CField username; //
	@CFieldinfo(fieldname = "exectype", caption = "操作类型", datetype = Types.INTEGER)
	public CField exectype; // 操作类型
	@CFieldinfo(fieldname = "execname", caption = "操作名称", datetype = Types.VARCHAR)
	public CField execname; // 操作名称
	@CFieldinfo(fieldname = "exectime", caption = "操作时间", datetype = Types.TIMESTAMP)
	public CField exectime; // 操作时间
	@CFieldinfo(fieldname = "note", caption = "", datetype = Types.VARCHAR)
	public CField note; //
	@CFieldinfo(fieldname = "remoteip", caption = "", datetype = Types.VARCHAR)
	public CField remoteip; //
	@CFieldinfo(fieldname = "sessionid", caption = "", datetype = Types.VARCHAR)
	public CField sessionid; //
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwuserexeclog() throws Exception {
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
