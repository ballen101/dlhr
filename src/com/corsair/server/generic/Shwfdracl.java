package com.corsair.server.generic;

import java.sql.Types;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

public class Shwfdracl extends CJPA {
	@CFieldinfo(fieldname = "aclid", iskey = true, notnull = true, caption = "", datetype = Types.INTEGER)
	public CField aclid; //
	@CFieldinfo(fieldname = "ownerid", caption = "机构ID 或 用户ID", datetype = Types.INTEGER)
	public CField ownerid; // 机构ID 或 用户ID
	@CFieldinfo(fieldname = "ownername", caption = "机构名 或 用户名", datetype = Types.VARCHAR)
	public CField ownername; // 机构名 或 用户名
	@CFieldinfo(fieldname = "grantor", caption = "授权人", datetype = Types.VARCHAR)
	public CField grantor; // 授权人
	@CFieldinfo(fieldname = "granttime", caption = "授权时间", datetype = Types.TIMESTAMP)
	public CField granttime; // 授权时间
	@CFieldinfo(fieldname = "statime", caption = "开始时间", datetype = Types.TIMESTAMP)
	public CField statime; // 开始时间
	@CFieldinfo(fieldname = "endtime", caption = "结束时间", datetype = Types.TIMESTAMP)
	public CField endtime; // 结束时间
	@CFieldinfo(fieldname = "access", caption = "权限类型  管理、读写删、 读写、删写、读、拒绝", datetype = Types.INTEGER)
	public CField access; // 权限类型 管理、读写删、 读写、删写、读、拒绝
	@CFieldinfo(fieldname = "objid", caption = "文件夹ID 或 文件ID", datetype = Types.INTEGER)
	public CField objid; // 文件夹ID 或 文件ID
	@CFieldinfo(fieldname = "idpath", caption = "如果授权给机构，保存机构的IDPATH", datetype = Types.VARCHAR)
	public CField idpath; // 如果授权给机构，保存机构的IDPATH
	@CFieldinfo(fieldname = "fidpath", caption = "文件夹的IDPATH 及文件的扩展ID", datetype = Types.VARCHAR)
	public CField fidpath; // 文件夹的IDPATH 及文件的扩展ID
	@CFieldinfo(fieldname = "objpass", caption = "访问密码 特殊控制", datetype = Types.BIT)
	public CField objpass; // 访问密码 特殊控制
	@CFieldinfo(fieldname = "acltype", caption = "1:文件夹对机构 2 文件对机构 3 文件夹对个人 4文件对个人", datetype = Types.INTEGER)
	public CField acltype; // 1:文件夹对机构 2 文件对机构 3 文件夹对个人 4文件对个人
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwfdracl() throws Exception {
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
