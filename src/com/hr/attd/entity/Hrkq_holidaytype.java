package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import com.hr.attd.ctr.CtrHrkq_holidaytype;

import java.sql.Types;

@CEntity(controller = CtrHrkq_holidaytype.class)
public class Hrkq_holidaytype extends CJPA {
	@CFieldinfo(fieldname = "htid", iskey = true, notnull = true, caption = "类型ID", datetype = Types.INTEGER)
	public CField htid; // 类型ID
	@CFieldinfo(fieldname = "htname", notnull = true, caption = "类型名称", datetype = Types.VARCHAR)
	public CField htname; // 类型名称
	@CFieldinfo(fieldname="bhtype",notnull=true,caption="假期大类",defvalue="1",datetype=Types.INTEGER)
	public CField bhtype;  //假期大类
	@CFieldinfo(fieldname = "daymin", notnull = true, caption = "最小天数", datetype = Types.DECIMAL)
	public CField daymin; // 最小天数
	@CFieldinfo(fieldname = "daymax", notnull = true, caption = "最大天数", datetype = Types.DECIMAL)
	public CField daymax; // 最大天数
	@CFieldinfo(fieldname = "dayymax", notnull = true, caption = "年度最大天数", datetype = Types.DECIMAL)
	public CField dayymax; // 年度最大天数
	@CFieldinfo(fieldname = "daymmax", notnull = true, caption = "月度最大天数", datetype = Types.DECIMAL)
	public CField daymmax; // 月度最大天数
	@CFieldinfo(fieldname = "daywmax", notnull = true, caption = "周度最大天数", datetype = Types.DECIMAL)
	public CField daywmax; // 周度最大天数
	@CFieldinfo(fieldname = "insaray", notnull = true, caption = "带薪假期", datetype = Types.INTEGER)
	public CField insaray; // 带薪假期
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname="isbuildin",caption="系统内置",defvalue="1",datetype=Types.INTEGER)
	public CField isbuildin;  //系统内置
	@CFieldinfo(fieldname = "entid", notnull = true, caption = "entid", datetype = Types.INTEGER)
	public CField entid; // entid
	@CFieldinfo(fieldname = "creator", notnull = true, caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", notnull = true, caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hrkq_holidaytype() throws Exception {
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
