package com.corsair.server.generic;

import java.sql.Types;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

@CEntity()
public class Shwfdr extends CJPA {
	@CFieldinfo(fieldname = "fdrid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField fdrid; // ID
	@CFieldinfo(fieldname = "fdrname", caption = "名称", datetype = Types.VARCHAR)
	public CField fdrname; // 名称
	@CFieldinfo(fieldname = "revlabel", caption = "标签", datetype = Types.VARCHAR)
	public CField revlabel; // 标签
	@CFieldinfo(fieldname = "clas", caption = "类型", datetype = Types.VARCHAR)
	public CField clas; // 类型
	@CFieldinfo(fieldname = "stat", caption = "状态", datetype = Types.INTEGER)
	public CField stat; // 状态
	@CFieldinfo(fieldname = "flag", caption = "标志", datetype = Types.INTEGER)
	public CField flag; // 标志
	@CFieldinfo(fieldname = "locked", caption = "锁定", datetype = Types.INTEGER)
	public CField locked; // 锁定
	@CFieldinfo(fieldname = "inspect", caption = "安全", datetype = Types.INTEGER)
	public CField inspect; // 安全
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
	@CFieldinfo(fieldname = "publishor", caption = "发布人", datetype = Types.VARCHAR)
	public CField publishor; // 发布人
	@CFieldinfo(fieldname = "publishtime", caption = "发布时间", datetype = Types.TIMESTAMP)
	public CField publishtime; // 发布时间
	@CFieldinfo(fieldname = "publishstatus", caption = "发布状态", datetype = Types.INTEGER)
	public CField publishstatus; // 发布状态
	@CFieldinfo(fieldname = "publishstage", caption = "发布介质", datetype = Types.INTEGER)
	public CField publishstage; // 发布介质
	@CFieldinfo(fieldname = "issecret", caption = "私密", datetype = Types.INTEGER)
	public CField issecret; // 私密
	@CFieldinfo(fieldname = "fidpath", caption = "路径", datetype = Types.VARCHAR)
	public CField fidpath; // 路径
	@CFieldinfo(fieldname = "typepath", caption = "类型路径", datetype = Types.VARCHAR)
	public CField typepath; // 类型路径
	@CFieldinfo(fieldname = "note", caption = "备注", datetype = Types.VARCHAR)
	public CField note; // 备注
	@CFieldinfo(fieldname = "actived", caption = "活动", datetype = Types.INTEGER)
	public CField actived; // 活动
	@CFieldinfo(fieldname = "attribute", caption = "文件夹属性：1普通；2系统", datetype = Types.INTEGER)
	public CField attribute; // 文件夹属性：1普通；2系统
	@CFieldinfo(fieldname = "sourceid", caption = "源ID", datetype = Types.INTEGER)
	public CField sourceid; // 源ID
	@CFieldinfo(fieldname = "fdtype", caption = "类型4 部门文件  3 个人文件", datetype = Types.INTEGER)
	public CField fdtype; // 类型4 部门文件 2 审批流程 3 个人文件
	@CFieldinfo(fieldname = "superid", caption = "上级ID", datetype = Types.INTEGER)
	public CField superid; // 上级ID
	@CFieldinfo(fieldname = "entid", caption = "", datetype = Types.INTEGER)
	public CField entid; //
	@CFieldinfo(fieldname = "sysbuildin", caption = "系统内建", datetype = Types.INTEGER)
	public CField sysbuildin; // 系统内建
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwfdr() throws Exception {
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