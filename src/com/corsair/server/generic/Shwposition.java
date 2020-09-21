package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shwposition extends CJPA {
	@CFieldinfo(fieldname = "positionid", iskey = true, notnull = true, datetype = Types.FLOAT)
	public CField positionid; // ID
	@CFieldinfo(fieldname = "qty", datetype = Types.FLOAT)
	public CField qty; // 数量
	@CFieldinfo(fieldname = "superposition", datetype = Types.VARCHAR)
	public CField superposition; // 上级岗位
	@CFieldinfo(fieldname = "lowerposition", datetype = Types.VARCHAR)
	public CField lowerposition; // 下级岗位
	@CFieldinfo(fieldname = "innercontact", datetype = Types.VARCHAR)
	public CField innercontact; // 内部联系人
	@CFieldinfo(fieldname = "outercontact", datetype = Types.VARCHAR)
	public CField outercontact; // 外部联系人
	@CFieldinfo(fieldname = "positiondesc", datetype = Types.VARCHAR)
	public CField positiondesc; // 岗位说明
	@CFieldinfo(fieldname = "sex", datetype = Types.FLOAT)
	public CField sex; // 性别
	@CFieldinfo(fieldname = "age", datetype = Types.FLOAT)
	public CField age; // 年龄
	@CFieldinfo(fieldname = "experience", datetype = Types.VARCHAR)
	public CField experience; //
	@CFieldinfo(fieldname = "degree", datetype = Types.VARCHAR)
	public CField degree; // 学历
	@CFieldinfo(fieldname = "specialty", datetype = Types.VARCHAR)
	public CField specialty; //
	@CFieldinfo(fieldname = "lang", datetype = Types.VARCHAR)
	public CField lang; // 语言
	@CFieldinfo(fieldname = "mustreq", datetype = Types.VARCHAR)
	public CField mustreq; //
	@CFieldinfo(fieldname = "specialreq", datetype = Types.VARCHAR)
	public CField specialreq; //
	@CFieldinfo(fieldname = "duty", datetype = Types.VARCHAR)
	public CField duty; // 职责
	@CFieldinfo(fieldname = "isvisible", datetype = Types.FLOAT)
	public CField isvisible; //
	@CFieldinfo(fieldname = "rposition", datetype = Types.VARCHAR)
	public CField rposition; //
	@CFieldinfo(fieldname = "code", datetype = Types.VARCHAR)
	public CField code; //
	@CFieldinfo(fieldname = "creator", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	@CFieldinfo(fieldname = "orgid", datetype = Types.FLOAT)
	public CField orgid; //
	@CFieldinfo(fieldname = "dutyclas", datetype = Types.VARCHAR)
	public CField dutyclas; //
	@CFieldinfo(fieldname = "dutykindid", datetype = Types.FLOAT)
	public CField dutykindid; //
	@CFieldinfo(fieldname = "dutygrade", datetype = Types.VARCHAR)
	public CField dutygrade; //
	@CFieldinfo(fieldname = "dutyaim", datetype = Types.VARCHAR)
	public CField dutyaim; //
	@CFieldinfo(fieldname = "certificate", datetype = Types.VARCHAR)
	public CField certificate; //
	@CFieldinfo(fieldname = "entid", notnull = true, datetype = Types.INTEGER)
	public CField entid; //
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwposition() throws Exception {
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