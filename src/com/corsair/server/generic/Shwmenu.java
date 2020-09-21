package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.genco.COMenu;

import java.sql.Types;

@CEntity(controller = COMenu.class)
public class Shwmenu extends CJPA {
	@CFieldinfo(fieldname = "menuid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField menuid; // ID
	@CFieldinfo(fieldname = "menucode", notnull = true, caption = "编码", datetype = Types.VARCHAR)
	public CField menucode; // 编码
	@CFieldinfo(fieldname = "menuname", notnull = true, caption = "名称", datetype = Types.VARCHAR)
	public CField menuname; // 名称
	@CFieldinfo(fieldname = "menupid", caption = "父ID", datetype = Types.INTEGER)
	public CField menupid; // 父ID
	@CFieldinfo(fieldname = "menuidpath", caption = "", datetype = Types.VARCHAR)
	public CField menuidpath; //
	@CFieldinfo(fieldname = "flag", caption = "标志", datetype = Types.INTEGER)
	public CField flag; // 标志
	@CFieldinfo(fieldname = "isitem", caption = "页节点", datetype = Types.INTEGER)
	public CField isitem; // 页节点
	@CFieldinfo(fieldname = "clas", caption = "类别", datetype = Types.INTEGER)
	public CField clas; // 类别
	@CFieldinfo(fieldname = "menutag", caption = "tag", datetype = Types.INTEGER)
	public CField menutag; // tag
	@CFieldinfo(fieldname = "refaddr", caption = "链接", datetype = Types.VARCHAR)
	public CField refaddr; // 链接
	@CFieldinfo(fieldname = "formstyle", caption = "窗体类型", datetype = Types.INTEGER)
	public CField formstyle; // 窗体类型
	@CFieldinfo(fieldname = "sortindex", caption = "序号", datetype = Types.FLOAT)
	public CField sortindex; // 序号
	@CFieldinfo(fieldname = "creator", caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	@CFieldinfo(fieldname = "note", caption = "备注", datetype = Types.VARCHAR)
	public CField note; // 备注
	@CFieldinfo(fieldname = "frmclass", caption = "窗体类名", datetype = Types.VARCHAR)
	public CField frmclass; // 窗体类名
	@CFieldinfo(fieldname = "frmcaption", caption = "窗体标题", datetype = Types.VARCHAR)
	public CField frmcaption; // 窗体标题
	@CFieldinfo(fieldname = "state", caption = "状态", datetype = Types.INTEGER)
	public CField state; // 状态
	@CFieldinfo(fieldname = "language1", caption = "", datetype = Types.VARCHAR)
	public CField language1; //
	@CFieldinfo(fieldname = "language2", caption = "", datetype = Types.VARCHAR)
	public CField language2; //
	@CFieldinfo(fieldname = "language3", caption = "", datetype = Types.VARCHAR)
	public CField language3; //
	@CFieldinfo(fieldname = "issystem", caption = "系统内置", datetype = Types.INTEGER)
	public CField issystem; // 系统内置
	@CFieldinfo(fieldname = "frmedttype", caption = "编辑类型", datetype = Types.INTEGER)
	public CField frmedttype; // 编辑类型
	@CFieldinfo(fieldname = "weburl", caption = "weburl", datetype = Types.VARCHAR)
	public CField weburl; // weburl
	@CFieldinfo(fieldname = "flatform", caption = "1:delphi 2:web 3:both", datetype = Types.INTEGER)
	public CField flatform; // 1:delphi 2:web 3:both
	@CFieldinfo(fieldname = "isedit", caption = "编辑", datetype = Types.INTEGER)
	public CField isedit; // 编辑
	@CFieldinfo(fieldname = "issubmit", caption = "审批", datetype = Types.INTEGER)
	public CField issubmit; // 审批
	@CFieldinfo(fieldname = "isview", caption = "浏览", datetype = Types.INTEGER)
	public CField isview; // 浏览
	@CFieldinfo(fieldname = "showtype", caption = "显示类型 1 嵌入 2 新窗口", datetype = Types.INTEGER)
	public CField showtype; // 显示类型 1 嵌入 2 新窗口
	@CFieldinfo(fieldname = "isupdate", caption = "变更", datetype = Types.INTEGER)
	public CField isupdate; // 变更
	@CFieldinfo(fieldname = "ico", caption = "图标", datetype = Types.VARCHAR)
	public CField ico; // 图标
	@CFieldinfo(fieldname = "level", caption = "", datetype = Types.INTEGER)
	public CField level; //
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义s

	public CJPALineData<Shwmenuco> shwmenucos;

	public Shwmenu() throws Exception {
	}

	@Override
	public boolean InitObject() {// 类初始化调用的方法
		super.InitObject();
		shwmenucos.addLinkField("menuid", "menuid");
		return true;
	}

	@Override
	public boolean FinalObject() { // 类释放前调用的方法
		super.FinalObject();
		return true;
	}
}
