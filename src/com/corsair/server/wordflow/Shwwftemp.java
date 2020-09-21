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
public class Shwwftemp extends CJPA {
	@CFieldinfo(fieldname = "wftempid", iskey = true, notnull = true, caption = "", datetype = Types.INTEGER)
	public CField wftempid; //
	@CFieldinfo(fieldname = "wftempname", caption = "", datetype = Types.VARCHAR)
	public CField wftempname; //
	@CFieldinfo(fieldname = "code", caption = "", datetype = Types.VARCHAR)
	public CField code; //
	@CFieldinfo(fieldname = "codeid", caption = "", datetype = Types.INTEGER)
	public CField codeid; //
	@CFieldinfo(fieldname = "clas", caption = "JPA Class", datetype = Types.VARCHAR)
	public CField clas; // JPA Class
	@CFieldinfo(fieldname = "frmclassname", caption = "", datetype = Types.VARCHAR)
	public CField frmclassname; //
	@CFieldinfo(fieldname = "stat", caption = "", datetype = Types.INTEGER)
	public CField stat; //
	@CFieldinfo(fieldname = "submitctrl", caption = "", datetype = Types.INTEGER)
	public CField submitctrl; //
	@CFieldinfo(fieldname = "actived", caption = "", datetype = Types.INTEGER)
	public CField actived; //
	@CFieldinfo(fieldname = "period", caption = "", datetype = Types.INTEGER)
	public CField period; //
	@CFieldinfo(fieldname = "breakprocid", caption = "", datetype = Types.INTEGER)
	public CField breakprocid; //
	@CFieldinfo(fieldname = "keephistory", caption = "", datetype = Types.INTEGER)
	public CField keephistory; //
	@CFieldinfo(fieldname = "managers", caption = "", datetype = Types.VARCHAR)
	public CField managers; //
	@CFieldinfo(fieldname = "canselect", caption = "", datetype = Types.INTEGER)
	public CField canselect; //
	@CFieldinfo(fieldname = "issecret", caption = "", datetype = Types.INTEGER)
	public CField issecret; //
	@CFieldinfo(fieldname = "comporgid", caption = "", datetype = Types.INTEGER)
	public CField comporgid; //
	@CFieldinfo(fieldname = "breakfunc", caption = "", datetype = Types.VARCHAR)
	public CField breakfunc; //
	@CFieldinfo(fieldname = "creator", caption = "", datetype = Types.VARCHAR)
	public CField creator; //
	@CFieldinfo(fieldname = "createtime", caption = "", datetype = Types.TIMESTAMP)
	public CField createtime; //
	@CFieldinfo(fieldname = "updator", caption = "", datetype = Types.VARCHAR)
	public CField updator; //
	@CFieldinfo(fieldname = "updatetime", caption = "", datetype = Types.TIMESTAMP)
	public CField updatetime; //
	@CFieldinfo(fieldname = "updatereason", caption = "", datetype = Types.VARCHAR)
	public CField updatereason; //
	@CFieldinfo(fieldname = "idpath", caption = "", datetype = Types.VARCHAR)
	public CField idpath; //
	@CFieldinfo(fieldname = "typepath", caption = "", datetype = Types.VARCHAR)
	public CField typepath; //
	@CFieldinfo(fieldname = "note", caption = "", datetype = Types.VARCHAR)
	public CField note; //
	@CFieldinfo(fieldname = "modifyperiod", caption = "", datetype = Types.INTEGER)
	public CField modifyperiod; //
	@CFieldinfo(fieldname = "canmodifytime", caption = "", datetype = Types.INTEGER)
	public CField canmodifytime; //
	@CFieldinfo(fieldname = "fdrid", caption = "", datetype = Types.INTEGER)
	public CField fdrid; //
	@CFieldinfo(fieldname = "entid", caption = "", datetype = Types.INTEGER)
	public CField entid; //
	@CFieldinfo(fieldname = "formurl", caption = "页面路径", datetype = Types.VARCHAR)
	public CField formurl; // 页面路径
	@CFieldinfo(fieldname = "mwwfstype", caption = "流程中中心显示方式 1 流程+表单 2 流程 3 表单", defvalue = "1", datetype = Types.INTEGER)
	public CField mwwfstype; // 流程中中心显示方式 1 流程+表单 2 流程 3 表单
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量
	@CLinkFieldInfo(jpaclass = Shwwftemparms.class, linkFields = { @LinkFieldItem(lfield = "wftempid", mfield = "wftempid") })
	public CJPALineData<Shwwftemparms> shwwftemparmss; // 流程参数列表
	@CLinkFieldInfo(jpaclass = Shwwftempproc.class, linkFields = { @LinkFieldItem(lfield = "wftempid", mfield = "wftempid") })
	public CJPALineData<Shwwftempproc> shwwftempprocs; // 流程模板的节点列表
	@CLinkFieldInfo(jpaclass = Shwwftemplinkline.class, linkFields = { @LinkFieldItem(lfield = "wftempid", mfield = "wftempid") })
	public CJPALineData<Shwwftemplinkline> shwwftemplinklines; // 流程节点间连线

	// 自关联数据定义

	public Shwwftemp() throws Exception {
	}

	public boolean InitObject() {// 类初始化调用的方法
		super.InitObject();
		/*
		 * shwwftemparmss.addLinkField("wftempid", "wftempid");
		 * shwwftempprocs.addLinkField("wftempid", "wftempid");
		 * shwwftemplinklines.addLinkField("wftempid", "wftempid");
		 */
		return true;
	}

	public boolean FinalObject() { // 类释放前调用的方法
		super.FinalObject();
		return true;
	}
}