package com.corsair.server.wordflow;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shwwflinkline extends CJPA {
	@CFieldinfo(fieldname = "wfllid", iskey = true, notnull = true, caption = "", datetype = Types.INTEGER)
	public CField wfllid; //
	@CFieldinfo(fieldname = "wfid", notnull = true, caption = "", datetype = Types.INTEGER)
	public CField wfid; //
	@CFieldinfo(fieldname = "fromprocid", notnull = true, caption = "", datetype = Types.INTEGER)
	public CField fromprocid; //
	@CFieldinfo(fieldname = "toprocid", notnull = true, caption = "", datetype = Types.INTEGER)
	public CField toprocid; //
	@CFieldinfo(fieldname = "lltitle", caption = "", datetype = Types.VARCHAR)
	public CField lltitle; //
	@CFieldinfo(fieldname = "idx", notnull = true, caption = "优先级别", datetype = Types.INTEGER)
	public CField idx; // 优先级别
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量
	@CLinkFieldInfo(jpaclass = Shwwflinklinecondition.class, linkFields = { @LinkFieldItem(lfield = "wfllid", mfield = "wfllid") })
	public CJPALineData<Shwwflinklinecondition> shwwflinklineconditions;

	// 自关联数据定义

	public Shwwflinkline() throws Exception {
	}

	public boolean InitObject() {// 类初始化调用的方法
		super.InitObject();
		// shwwflinklineconditions.addLinkField("wfllid", "wfllid");
		return true;
	}

	public boolean FinalObject() { // 类释放前调用的方法
		super.FinalObject();
		return true;
	}

}