package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shwkv extends CJPA {
	@CFieldinfo(fieldname = "kvid", iskey = true, notnull = true, caption = "kvid", datetype = Types.INTEGER)
	public CField kvid; // kvid
	@CFieldinfo(fieldname = "kvcode", notnull = true, caption = "kvcode", datetype = Types.VARCHAR)
	public CField kvcode; // kvcode
	@CFieldinfo(fieldname = "kvvalue", notnull = true, caption = "kvvalue", datetype = Types.VARCHAR)
	public CField kvvalue; // kvvalue
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwkv() throws Exception {
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

	public static String getkvvalue(String kvcode) throws Exception {
		String sqlstr = "select * from shwkv where kvcode='" + kvcode + "'";
		Shwkv kv = new Shwkv();
		kv.findBySQL(sqlstr);
		if (kv.isEmpty())
			return null;
		else
			return kv.kvvalue.getValue();
	}

	public static void setkvvalue(CDBConnection con, String kvcode, String value) throws Exception {
		String sqlstr = "select * from shwkv where kvcode='" + kvcode + "'";
		Shwkv kv = new Shwkv();
		kv.findBySQL(sqlstr);
		kv.kvcode.setValue(kvcode);
		kv.kvvalue.setValue(value);
		kv.save(con);
	}
}
