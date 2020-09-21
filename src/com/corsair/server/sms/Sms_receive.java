package com.corsair.server.sms;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Sms_receive extends CJPA {
	@CFieldinfo(fieldname = "rid", iskey = true, notnull = true, datetype = Types.FLOAT)
	public CField rid; // 短信ID
	@CFieldinfo(fieldname = "smstype", datetype = Types.INTEGER)
	public CField smstype; // 短信类型
	@CFieldinfo(fieldname = "mobile", datetype = Types.VARCHAR)
	public CField mobile; // 手机号
	@CFieldinfo(fieldname = "content", datetype = Types.VARCHAR)
	public CField content; // 短信内容
	@CFieldinfo(fieldname = "flag", datetype = Types.INTEGER)
	public CField flag; // 状态
	@CFieldinfo(fieldname = "receivetime", datetype = Types.TIMESTAMP)
	public CField receivetime; // 接收时间
	@CFieldinfo(fieldname = "createtime", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "creator", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "entid", notnull = true, datetype = Types.FLOAT)
	public CField entid; // 组织ID
	@CFieldinfo(fieldname = "entname", datetype = Types.VARCHAR)
	public CField entname; // 组织名称
	@CFieldinfo(fieldname = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // 路径
	@CFieldinfo(fieldname = "property1", datetype = Types.VARCHAR)
	public CField property1; //
	@CFieldinfo(fieldname = "property2", datetype = Types.VARCHAR)
	public CField property2; //
	@CFieldinfo(fieldname = "property3", datetype = Types.VARCHAR)
	public CField property3; //
	@CFieldinfo(fieldname = "property4", datetype = Types.VARCHAR)
	public CField property4; //
	@CFieldinfo(fieldname = "property5", datetype = Types.VARCHAR)
	public CField property5; //
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Sms_receive() throws Exception {
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