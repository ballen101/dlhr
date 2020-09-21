package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shwuser_wf_agent extends CJPA {
	@CFieldinfo(fieldname = "wfagentid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField wfagentid; // ID
	@CFieldinfo(fieldname = "userid", notnull = true, caption = "出差用户ID", datetype = Types.INTEGER)
	public CField userid; // 出差用户ID
	@CFieldinfo(fieldname = "wftempid", notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField wftempid; // ID
	@CFieldinfo(fieldname = "wftempname", caption = "", datetype = Types.VARCHAR)
	public CField wftempname; //
	@CFieldinfo(fieldname = "auserid", notnull = true, caption = "代理用户ID", datetype = Types.INTEGER)
	public CField auserid; // 代理用户ID
	@CFieldinfo(fieldname = "ausername", notnull = true, caption = "代理用户名", datetype = Types.VARCHAR)
	public CField ausername; // 代理用户名
	@CFieldinfo(fieldname = "adisplayname", caption = "", datetype = Types.VARCHAR)
	public CField adisplayname; //
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwuser_wf_agent() throws Exception {
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