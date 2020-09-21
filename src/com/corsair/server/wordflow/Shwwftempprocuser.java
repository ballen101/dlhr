package com.corsair.server.wordflow;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shwwftempprocuser extends CJPA {
	@CFieldinfo(fieldname = "wftpuid", iskey = true, notnull = true, caption = "流程模板节点用户ID", datetype = Types.INTEGER)
	public CField wftpuid; // 流程模板节点用户ID
	@CFieldinfo(fieldname = "wftempid", notnull = true, caption = "模板ID", datetype = Types.INTEGER)
	public CField wftempid; // 模板ID
	@CFieldinfo(fieldname = "proctempid", notnull = true, caption = "", datetype = Types.INTEGER)
	public CField proctempid; //
	@CFieldinfo(fieldname = "userid", notnull = true, caption = "", datetype = Types.VARCHAR)
	public CField userid; //
	@CFieldinfo(fieldname = "stat", caption = "状态", datetype = Types.INTEGER)
	public CField stat; // 状态
	@CFieldinfo(fieldname = "sortindex", caption = "", datetype = Types.INTEGER)
	public CField sortindex; //
	@CFieldinfo(fieldname = "isposition", caption = "", datetype = Types.INTEGER)
	public CField isposition; //
	@CFieldinfo(fieldname = "note", caption = "", datetype = Types.VARCHAR)
	public CField note; //
	@CFieldinfo(fieldname = "wftemprocuserid", iskey = true, notnull = true, caption = "", datetype = Types.INTEGER)
	public CField wftemprocuserid; //
	@CFieldinfo(fieldname = "displayname", caption = "", datetype = Types.VARCHAR)
	public CField displayname; //
	@CFieldinfo(fieldname = "jointype", notnull = true, caption = "参数方式： 普通参与  管理员", datetype = Types.INTEGER)
	public CField jointype; // 参数方式： 普通参与 管理员
	@CFieldinfo(fieldname = "userfindtype", caption = "岗位查找用户方式，1: 默认部门 2 默认部门上溯 3 默认部门下溯 4所有部门 ", datetype = Types.INTEGER)
	public CField userfindtype; // 岗位查找用户方式，1: 默认部门 2 默认部门上溯 3 默认部门下溯 4所有部门
	@CFieldinfo(fieldname = "userfindcdt", caption = "岗位查找条件：1 表单路径 2 登录用户 3 表单机构", datetype = Types.INTEGER)
	public CField userfindcdt; // 岗位查找条件：1 表单路径 2 登录用户 3 表单机构
	@CFieldinfo(fieldname = "userfindorgid", caption = "表单查询机构", datetype = Types.VARCHAR)
	public CField userfindorgid; // 表单查询机构
	@CFieldinfo(fieldname="recnotify",caption="recnotify",datetype=Types.INTEGER)
	public CField recnotify;  //recnotify
	@CFieldinfo(fieldname="recpress",caption="recpress",datetype=Types.INTEGER)
	public CField recpress;  //recpress
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwwftempprocuser() throws Exception {
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
