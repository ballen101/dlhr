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
public class Shwwf extends CJPA {
	@CFieldinfo(fieldname = "wfid", iskey = true, notnull = true, caption = "", datetype = Types.INTEGER)
	public CField wfid; //
	@CFieldinfo(fieldname = "wfname", caption = "流程名称", datetype = Types.VARCHAR)
	public CField wfname; // 流程名称
	@CFieldinfo(fieldname = "code", caption = "流程编码", datetype = Types.VARCHAR)
	public CField code; // 流程编码
	@CFieldinfo(fieldname = "codeid", caption = "编码ID", datetype = Types.INTEGER)
	public CField codeid; // 编码ID
	@CFieldinfo(fieldname = "frmclassname", caption = "", datetype = Types.VARCHAR)
	public CField frmclassname; //
	@CFieldinfo(fieldname = "clas", caption = "JPA Class", datetype = Types.VARCHAR)
	public CField clas; // JPA Class
	@CFieldinfo(fieldname = "stat", caption = "1 待审批 2 当前 3 完成", datetype = Types.INTEGER)
	public CField stat; // 1 待审批 2 当前 3 完成
	@CFieldinfo(fieldname = "submitctrl", caption = "提交控制", datetype = Types.INTEGER)
	public CField submitctrl; // 提交控制
	@CFieldinfo(fieldname = "actived", caption = "活动", datetype = Types.INTEGER)
	public CField actived; // 活动
	@CFieldinfo(fieldname = "period", caption = "", datetype = Types.INTEGER)
	public CField period; //
	@CFieldinfo(fieldname = "ojcectid", caption = "对象ID", datetype = Types.INTEGER)
	public CField ojcectid; // 对象ID
	@CFieldinfo(fieldname = "breakprocid", caption = "中断过程ID", datetype = Types.INTEGER)
	public CField breakprocid; // 中断过程ID
	@CFieldinfo(fieldname = "keephistory", caption = "保存历史", datetype = Types.INTEGER)
	public CField keephistory; // 保存历史
	@CFieldinfo(fieldname = "managers", caption = "管理员", datetype = Types.VARCHAR)
	public CField managers; // 管理员
	@CFieldinfo(fieldname = "canselect", caption = "允许选择", datetype = Types.INTEGER)
	public CField canselect; // 允许选择
	@CFieldinfo(fieldname = "submituserid", caption = "提交用户ID", datetype = Types.INTEGER)
	public CField submituserid; // 提交用户ID
	@CFieldinfo(fieldname = "issecret", caption = "", datetype = Types.INTEGER)
	public CField issecret; //
	@CFieldinfo(fieldname = "comporgid", caption = "机构ID", datetype = Types.INTEGER)
	public CField comporgid; // 机构ID
	@CFieldinfo(fieldname = "frmclass", caption = "窗体类名", datetype = Types.VARCHAR)
	public CField frmclass; // 窗体类名
	@CFieldinfo(fieldname = "breakfunc", caption = "中断函数", datetype = Types.VARCHAR)
	public CField breakfunc; // 中断函数
	@CFieldinfo(fieldname = "creator", caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	@CFieldinfo(fieldname = "updatereason", caption = "更新原因", datetype = Types.VARCHAR)
	public CField updatereason; // 更新原因
	@CFieldinfo(fieldname = "idpath", caption = "路径", datetype = Types.VARCHAR)
	public CField idpath; // 路径
	@CFieldinfo(fieldname = "typepath", caption = "类型路径", datetype = Types.VARCHAR)
	public CField typepath; // 类型路径
	@CFieldinfo(fieldname = "note", caption = "备注", datetype = Types.VARCHAR)
	public CField note; // 备注
	@CFieldinfo(fieldname = "modifyperiod", caption = "允许编辑", datetype = Types.SMALLINT)
	public CField modifyperiod; // 允许编辑
	@CFieldinfo(fieldname = "canmodifytime", caption = "允许边界时间", datetype = Types.INTEGER)
	public CField canmodifytime; // 允许边界时间
	@CFieldinfo(fieldname = "fdrid", caption = "文件ID", datetype = Types.INTEGER)
	public CField fdrid; // 文件ID
	@CFieldinfo(fieldname = "entid", caption = "组织", datetype = Types.INTEGER)
	public CField entid; // 组织
	@CFieldinfo(fieldname = "subject", caption = "主题", datetype = Types.VARCHAR)
	public CField subject; // 主题
	@CFieldinfo(fieldname = "wftemid", caption = "模板ID", datetype = Types.INTEGER)
	public CField wftemid; // 模板ID
	@CFieldinfo(fieldname = "formurl", caption = "窗体路径", datetype = Types.VARCHAR)
	public CField formurl; // 窗体路径
	@CFieldinfo(fieldname = "mwwfstype", caption = "流程中中心显示方式 1 流程+表单 2 流程 3 表单", defvalue = "1", datetype = Types.INTEGER)
	public CField mwwfstype; // 流程中中心显示方式 1 流程+表单 2 流程 3 表单
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量
	@CLinkFieldInfo(jpaclass = Shwwfproc.class, linkFields = { @LinkFieldItem(lfield = "wfid", mfield = "wfid") })
	public CJPALineData<Shwwfproc> shwwfprocs;
	@CLinkFieldInfo(jpaclass = Shwwflinkline.class, linkFields = { @LinkFieldItem(lfield = "wfid", mfield = "wfid") })
	public CJPALineData<Shwwflinkline> shwwflinklines;
	// @CLinkFieldInfo(jpaclass = Shwwfproclog.class, linkFields = { @LinkFieldItem(lfield = "wfid", mfield = "wfid") })
	// public CJPALineData<Shwwfproclog> shwwfproclogs;
	// 自关联数据定义

	public Shwwf() throws Exception {
	}

	public boolean InitObject() {// 类初始化调用的方法
		super.InitObject();
		return true;
	}

	public boolean FinalObject() { // 类释放前调用的方法
		super.FinalObject();
		return true;
	}
}
