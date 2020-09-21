package com.corsair.server.wordflow;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shwwfprocuser extends CJPA {
	@CFieldinfo(fieldname = "wfid", notnull = true, caption = "", datetype = Types.INTEGER)
	public CField wfid; //
	@CFieldinfo(fieldname = "procid", notnull = true, caption = "", datetype = Types.INTEGER)
	public CField procid; //
	@CFieldinfo(fieldname = "wfprocuserid", iskey = true, notnull = true, caption = "", datetype = Types.INTEGER)
	public CField wfprocuserid; //
	@CFieldinfo(fieldname = "userid", notnull = true, caption = "", datetype = Types.VARCHAR)
	public CField userid; //
	@CFieldinfo(fieldname = "stat", caption = "1 待审批 2 已完成", datetype = Types.INTEGER)
	public CField stat; // 1 待审批 2 已完成 3 驳回
	@CFieldinfo(fieldname = "sortindex", caption = "", datetype = Types.INTEGER)
	public CField sortindex; //
	@CFieldinfo(fieldname = "isposition", caption = "", datetype = Types.INTEGER)
	public CField isposition; //
	@CFieldinfo(fieldname = "note", caption = "", datetype = Types.VARCHAR)
	public CField note; //
	@CFieldinfo(fieldname = "displayname", caption = "", datetype = Types.VARCHAR)
	public CField displayname; //
	@CFieldinfo(fieldname = "opinion", caption = "", datetype = Types.VARCHAR)
	public CField opinion; //
	@CFieldinfo(fieldname = "jointype", notnull = true, caption = "", datetype = Types.INTEGER)
	public CField jointype; //
	@CFieldinfo(fieldname = "audittime", caption = "", datetype = Types.TIMESTAMP)
	public CField audittime; // 最后处理时间 提交 驳回
	@CFieldinfo(fieldname = "recnotify", caption = "recnotify", datetype = Types.INTEGER)
	public CField recnotify; // recnotify
	@CFieldinfo(fieldname = "recpress", caption = "recpress", datetype = Types.INTEGER)
	public CField recpress; // recpress
	@CFieldinfo(fieldname = "pressnum", caption = "催办次数", datetype = Types.INTEGER)
	public CField pressnum; // 催办次数
	@CFieldinfo(fieldname = "nextpresstime", caption = "待催办时间", datetype = Types.TIMESTAMP)
	public CField nextpresstime; // 待催办时间
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwwfprocuser() throws Exception {
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
