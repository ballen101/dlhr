package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shwusertoken extends CJPA {
	@CFieldinfo(fieldname = "userid", notnull = true, caption = "userid", datetype = Types.INTEGER)
	public CField userid; // userid
	@CFieldinfo(fieldname = "token", iskey = true, notnull = true, caption = "token", datetype = Types.VARCHAR)
	public CField token; // token
	@CFieldinfo(fieldname = "sessionid", caption = "sessionid", datetype = Types.VARCHAR)
	public CField sessionid; // sessionid
	@CFieldinfo(fieldname = "username", notnull = true, caption = "用户名", datetype = Types.VARCHAR)
	public CField username; // 用户名
	@CFieldinfo(fieldname = "userpass", caption = "密码", datetype = Types.VARCHAR)
	public CField userpass; // 密码
	@CFieldinfo(fieldname = "starttime", caption = "创建时间", datetype = Types.INTEGER)
	public CField starttime; // 创建时间
	@CFieldinfo(fieldname = "timeout", caption = "超时时间", datetype = Types.INTEGER)
	public CField timeout; // 超时时间
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwusertoken() throws Exception {
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
