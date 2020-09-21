package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shwwarehouse extends CJPA {
	@CFieldinfo(fieldname = "whid", iskey = true, notnull = true, caption = "仓库ID", datetype = Types.INTEGER)
	public CField whid; // 仓库ID
	@CFieldinfo(fieldname = "whcode", codeid = 2, notnull = true, caption = "仓库编码", datetype = Types.VARCHAR)
	public CField whcode; // 仓库编码
	@CFieldinfo(fieldname = "whname", notnull = true, caption = "仓库名称", datetype = Types.VARCHAR)
	public CField whname; // 仓库名称
	@CFieldinfo(fieldname = "orgid", notnull = true, caption = "机构ID", datetype = Types.INTEGER)
	public CField orgid; // 机构ID
	@CFieldinfo(fieldname = "orgname", notnull = true, caption = "机构名称", datetype = Types.VARCHAR)
	public CField orgname; // 机构名称
	@CFieldinfo(fieldname = "orgcode", caption = "机构编码", datetype = Types.VARCHAR)
	public CField orgcode; // 机构编码
	@CFieldinfo(fieldname = "whaddress", caption = "仓库地址", datetype = Types.VARCHAR)
	public CField whaddress; // 仓库地址
	@CFieldinfo(fieldname = "cname", caption = "联系人", datetype = Types.VARCHAR)
	public CField cname; // 联系人
	@CFieldinfo(fieldname = "whphone", caption = "联系电话", datetype = Types.VARCHAR)
	public CField whphone; // 联系电话
	@CFieldinfo(fieldname = "locatorctrl", caption = "库位控制", datetype = Types.INTEGER)
	public CField locatorctrl; // 库位控制
	@CFieldinfo(fieldname = "creator", caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", caption = "修改人", datetype = Types.VARCHAR)
	public CField updator; // 修改人
	@CFieldinfo(fieldname = "updatetime", caption = "修改时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 修改时间
	@CFieldinfo(fieldname = "usable", caption = "有效", datetype = Types.INTEGER)
	public CField usable; // 有效
	@CFieldinfo(fieldname = "note", caption = "备注", datetype = Types.VARCHAR)
	public CField note; // 备注
	@CFieldinfo(fieldname = "idpath", notnull = true, caption = "", datetype = Types.VARCHAR)
	public CField idpath; //
	@CFieldinfo(fieldname = "entid", notnull = true, caption = "组织ID", datetype = Types.INTEGER)
	public CField entid; // 组织ID
	@CFieldinfo(fieldname = "entname", caption = "组织名称", datetype = Types.VARCHAR)
	public CField entname; // 组织名称
	@CFieldinfo(fieldname = "whtype", caption = "仓库分类", datetype = Types.INTEGER)
	public CField whtype; // 仓库分类
	@CFieldinfo(fieldname = "areaid", caption = "区域ID", datetype = Types.INTEGER)
	public CField areaid; // 区域ID
	@CFieldinfo(fieldname = "areacode", caption = "区域编码", datetype = Types.VARCHAR)
	public CField areacode; // 区域编码
	@CFieldinfo(fieldname = "areaname", caption = "区域名称", datetype = Types.VARCHAR)
	public CField areaname; // 区域名称
	@CFieldinfo(fieldname = "allowninv", caption = "允许负库存 1, 是 2 否", datetype = Types.INTEGER)
	public CField allowninv; // 允许负库存 1, 是 2 否
	@CFieldinfo(fieldname = "property1", caption = "", datetype = Types.VARCHAR)
	public CField property1; //
	@CFieldinfo(fieldname = "property2", caption = "", datetype = Types.VARCHAR)
	public CField property2; //
	@CFieldinfo(fieldname = "property3", caption = "", datetype = Types.VARCHAR)
	public CField property3; //
	@CFieldinfo(fieldname = "property4", caption = "", datetype = Types.VARCHAR)
	public CField property4; //
	@CFieldinfo(fieldname = "property5", caption = "", datetype = Types.VARCHAR)
	public CField property5; //
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwwarehouse() throws Exception {
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