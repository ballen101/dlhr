package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shwwftemtype extends CJPA {
	@CFieldinfo(fieldname = "wftpid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField wftpid; // ID
	@CFieldinfo(fieldname = "fdrname", caption = "名称", datetype = Types.VARCHAR)
	public CField fdrname; // 名称
	@CFieldinfo(fieldname = "stat", caption = "状态", datetype = Types.INTEGER)
	public CField stat; // 状态
	@CFieldinfo(fieldname = "flag", caption = "标志", datetype = Types.INTEGER)
	public CField flag; // 标志
	@CFieldinfo(fieldname = "typepath", caption = "类型路径", datetype = Types.VARCHAR)
	public CField typepath; // 类型路径
	@CFieldinfo(fieldname = "note", caption = "备注", datetype = Types.VARCHAR)
	public CField note; // 备注
	@CFieldinfo(fieldname = "superid", caption = "上级ID", datetype = Types.INTEGER)
	public CField superid; // 上级ID
	@CFieldinfo(fieldname = "creator", caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwwftemtype() throws Exception {
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