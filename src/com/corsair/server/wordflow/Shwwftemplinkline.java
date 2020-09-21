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
public class Shwwftemplinkline extends CJPA {
	@CFieldinfo(fieldname = "wftemllid", iskey = true, notnull = true, caption = "连线ID", datetype = Types.INTEGER)
	public CField wftemllid; // 连线ID
	@CFieldinfo(fieldname = "wftempid", notnull = true, caption = "流程模板ID", datetype = Types.INTEGER)
	public CField wftempid; // 流程模板ID
	@CFieldinfo(fieldname = "fromproctempid", notnull = true, caption = "源节点", datetype = Types.INTEGER)
	public CField fromproctempid; // 源节点
	@CFieldinfo(fieldname = "toproctempid", notnull = true, caption = "目标节点", datetype = Types.INTEGER)
	public CField toproctempid; // 目标节点
	@CFieldinfo(fieldname = "lltitle", caption = "标题", datetype = Types.VARCHAR)
	public CField lltitle; // 标题
	@CFieldinfo(fieldname = "idx", notnull = true, caption = "优先级别", datetype = Types.INTEGER)
	public CField idx; // 优先级别
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量
	@CLinkFieldInfo(jpaclass = Shwwftemplinklinecondition.class, linkFields = { @LinkFieldItem(lfield = "wftemllid", mfield = "wftemllid") })
	public CJPALineData<Shwwftemplinklinecondition> shwwftemplinklineconditions; // 条件明细

	// 自关联数据定义

	public Shwwftemplinkline() throws Exception {
	}

	public boolean InitObject() {// 类初始化调用的方法
		super.InitObject();
		// shwwftemplinklineconditions.addLinkField("wftemllid", "wftemllid");
		return true;
	}

	public boolean FinalObject() { // 类释放前调用的方法
		super.FinalObject();
		return true;
	}

}
