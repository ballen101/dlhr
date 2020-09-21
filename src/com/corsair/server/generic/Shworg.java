package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shworg extends CJPA {
	@CFieldinfo(fieldname = "orgid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField orgid; // ID
	@CFieldinfo(fieldname = "orgname", notnull = true, caption = "名称", datetype = Types.VARCHAR)
	public CField orgname; // 名称
	@CFieldinfo(fieldname = "code", codeid = 15, caption = "编码", datetype = Types.VARCHAR)
	public CField code; // 编码
	@CFieldinfo(fieldname = "superid", notnull = true, caption = "父ID", datetype = Types.DECIMAL)
	public CField superid; // 父ID
	@CFieldinfo(fieldname = "extorgname", caption = "全路径名称", datetype = Types.VARCHAR)
	public CField extorgname; // 全路径名称
	@CFieldinfo(fieldname = "manager", caption = "第一负责人", datetype = Types.VARCHAR)
	public CField manager; // 第一负责人
	@CFieldinfo(fieldname = "vicemanager", caption = "第二负责人", datetype = Types.VARCHAR)
	public CField vicemanager; // 第二负责人
	@CFieldinfo(fieldname = "managerpos", caption = "第一负责人岗位", datetype = Types.VARCHAR)
	public CField managerpos; // 第一负责人岗位
	@CFieldinfo(fieldname = "vicemanagerpos", caption = "第二负责人岗位", datetype = Types.VARCHAR)
	public CField vicemanagerpos; // 第二负责人岗位
	@CFieldinfo(fieldname = "emailmanager", caption = "邮件管理员", datetype = Types.VARCHAR)
	public CField emailmanager; // 邮件管理员
	@CFieldinfo(fieldname = "emailuser", caption = "emailuser", datetype = Types.VARCHAR)
	public CField emailuser; // emailuser
	// @CFieldinfo(fieldname = "stat", caption = "状态", datetype = Types.INTEGER)
	// public CField stat; // 状态
	@CFieldinfo(fieldname = "usable", caption = "可用", defvalue = "1", datetype = Types.INTEGER)
	public CField usable; // 可用
	@CFieldinfo(fieldname = "orgtype", notnull = true, caption = "类型", datetype = Types.INTEGER)
	public CField orgtype; // 类型数据字典
	@CFieldinfo(fieldname = "pubfdrid", caption = "pubfdrid", datetype = Types.INTEGER)
	public CField pubfdrid; // pubfdrid
	@CFieldinfo(fieldname = "focusfdrid1", caption = "焦点文件", datetype = Types.INTEGER)
	public CField focusfdrid1; // 焦点文件
	@CFieldinfo(fieldname = "focusfdrid2", caption = "焦点文件", datetype = Types.INTEGER)
	public CField focusfdrid2; // 焦点文件
	@CFieldinfo(fieldname = "mrporgid", caption = "计划部门", datetype = Types.VARCHAR)
	public CField mrporgid; // 计划部门
	@CFieldinfo(fieldname = "areaid", caption = "区域ID", datetype = Types.INTEGER)
	public CField areaid; // 区域ID
	@CFieldinfo(fieldname = "idpath", caption = "路径", datetype = Types.VARCHAR)
	public CField idpath; // 路径
	@CFieldinfo(fieldname = "typepath", caption = "typepath", datetype = Types.VARCHAR)
	public CField typepath; // typepath
	@CFieldinfo(fieldname = "creator", caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	@CFieldinfo(fieldname = "showinposition", caption = "showinposition", datetype = Types.INTEGER)
	public CField showinposition; // showinposition
	@CFieldinfo(fieldname = "note", caption = "备注", datetype = Types.VARCHAR)
	public CField note; // 备注
	@CFieldinfo(fieldname = "width", caption = "width", datetype = Types.INTEGER)
	public CField width; // width
	@CFieldinfo(fieldname = "height", caption = "height", datetype = Types.INTEGER)
	public CField height; // height
	@CFieldinfo(fieldname = "isfeecenter", caption = "isfeecenter", datetype = Types.INTEGER)
	public CField isfeecenter; // isfeecenter
	@CFieldinfo(fieldname = "sfeecenter", caption = "费用中心", datetype = Types.INTEGER)
	public CField sfeecenter; // 费用中心
	@CFieldinfo(fieldname = "isbc", caption = "isbc", datetype = Types.INTEGER)
	public CField isbc; // isbc
	@CFieldinfo(fieldname = "orguserid", caption = "机构管理员", datetype = Types.VARCHAR)
	public CField orguserid; // 机构管理员
	@CFieldinfo(fieldname = "orginvid", caption = "库存中心", datetype = Types.INTEGER)
	public CField orginvid; // 库存中心
	@CFieldinfo(fieldname = "entid", caption = "组织ID", datetype = Types.INTEGER)
	public CField entid; // 组织ID
	@CFieldinfo(fieldname = "visituser", caption = "visituser", datetype = Types.VARCHAR)
	public CField visituser; // visituser
	@CFieldinfo(fieldname = "orglevel", caption = "部门层级", datetype = Types.INTEGER)
	public CField orglevel; // 部门层级
	@CFieldinfo(fieldname = "orgmail", caption = "机构邮件", datetype = Types.VARCHAR)
	public CField orgmail; // 机构邮件
	@CFieldinfo(fieldname = "orgaddr", caption = "机构地址", datetype = Types.VARCHAR)
	public CField orgaddr; // 机构地址
	@CFieldinfo(fieldname = "webset", caption = "网址", datetype = Types.VARCHAR)
	public CField webset; // 网址
	@CFieldinfo(fieldname = "orgfrof", caption = "部门简介", datetype = Types.VARCHAR)
	public CField orgfrof; // 部门简介
	@CFieldinfo(fieldname = "orgduty", caption = "部门职责", datetype = Types.VARCHAR)
	public CField orgduty; // 部门职责
	@CFieldinfo(fieldname = "attribute1", precision = 32, scale = 0, caption = "扩展属性1", datetype = Types.VARCHAR)
	public CField attribute1; // 扩展属性1
	@CFieldinfo(fieldname = "attribute2", precision = 32, scale = 0, caption = "扩展属性2", datetype = Types.VARCHAR)
	public CField attribute2; // 扩展属性2
	@CFieldinfo(fieldname = "attribute3", precision = 32, scale = 0, caption = "扩展属性3", datetype = Types.VARCHAR)
	public CField attribute3; // 扩展属性3
	@CFieldinfo(fieldname = "attribute4", precision = 32, scale = 0, caption = "扩展属性4", datetype = Types.VARCHAR)
	public CField attribute4; // 扩展属性4
	@CFieldinfo(fieldname = "attribute5", precision = 32, scale = 0, caption = "扩展属性5", datetype = Types.VARCHAR)
	public CField attribute5; // 扩展属性5
	@CFieldinfo(fieldname = "attribute6", precision = 32, scale = 0, caption = "扩展属性6", datetype = Types.VARCHAR)
	public CField attribute6; // 扩展属性6
	@CFieldinfo(fieldname = "attribute7", precision = 32, scale = 0, caption = "扩展属性7", datetype = Types.VARCHAR)
	public CField attribute7; // 扩展属性7
	@CFieldinfo(fieldname = "attribute8", precision = 32, scale = 0, caption = "扩展属性8", datetype = Types.VARCHAR)
	public CField attribute8; // 扩展属性8
	@CFieldinfo(fieldname = "attribute9", precision = 32, scale = 0, caption = "扩展属性9", datetype = Types.VARCHAR)
	public CField attribute9; // 扩展属性9
	@CFieldinfo(fieldname = "attribute10", precision = 32, scale = 0, caption = "扩展属性10", datetype = Types.VARCHAR)
	public CField attribute10; // 扩展属性10
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义
	public CJPALineData<Shworg_find> shworg_finds;

	public Shworg() throws Exception {
	}

	@Override
	public boolean InitObject() {// 类初始化调用的方法
		super.InitObject();
		shworg_finds.addLinkField("orgid", "orgid");
		return true;
	}

	@Override
	public boolean FinalObject() { // 类释放前调用的方法
		super.FinalObject();
		return true;
	}
}