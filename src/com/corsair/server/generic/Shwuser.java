package com.corsair.server.generic;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shwuser extends CJPA {
	@CFieldinfo(fieldname = "userid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField userid; // ID
	@CFieldinfo(fieldname = "username", notnull = true, caption = "用户名", datetype = Types.VARCHAR)
	public CField username; // 用户名
	@CFieldinfo(fieldname = "userpass", caption = "密码", datetype = Types.VARCHAR)
	public CField userpass; // 密码
	@CFieldinfo(fieldname = "email", caption = "邮件", datetype = Types.VARCHAR)
	public CField email; // 邮件
	@CFieldinfo(fieldname = "telo", caption = "办公室电话", datetype = Types.VARCHAR)
	public CField telo; // 办公室电话
	@CFieldinfo(fieldname = "telh", caption = "家庭电话", datetype = Types.VARCHAR)
	public CField telh; // 家庭电话
	@CFieldinfo(fieldname = "mobil", caption = "手机", datetype = Types.VARCHAR)
	public CField mobil; // 手机
	@CFieldinfo(fieldname = "actived", caption = "可用", datetype = Types.INTEGER)
	public CField actived; // 可用
	@CFieldinfo(fieldname = "enablemmsg", caption = "", datetype = Types.INTEGER)
	public CField enablemmsg; //
	@CFieldinfo(fieldname = "hideip", caption = "", datetype = Types.INTEGER)
	public CField hideip; //
	@CFieldinfo(fieldname = "goout", caption = "1 出差中 2 未出差", datetype = Types.INTEGER)
	public CField goout; // 1 出差中 2 未出差
	@CFieldinfo(fieldname = "gooutstarttime", caption = "", datetype = Types.TIMESTAMP)
	public CField gooutstarttime; //
	@CFieldinfo(fieldname = "gooutendtime", caption = "", datetype = Types.TIMESTAMP)
	public CField gooutendtime; //
	@CFieldinfo(fieldname = "maxspace", caption = "", datetype = Types.FLOAT)
	public CField maxspace; //
	@CFieldinfo(fieldname = "noticeent", caption = "", datetype = Types.VARCHAR)
	public CField noticeent; //
	@CFieldinfo(fieldname = "noticeuser", caption = "", datetype = Types.VARCHAR)
	public CField noticeuser; //
	@CFieldinfo(fieldname = "mapdocid", caption = "", datetype = Types.INTEGER)
	public CField mapdocid; //
	@CFieldinfo(fieldname = "feecenterid", caption = "", datetype = Types.INTEGER)
	public CField feecenterid; //
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
	@CFieldinfo(fieldname = "shortmobil", caption = "短号", datetype = Types.VARCHAR)
	public CField shortmobil; // 短号
	@CFieldinfo(fieldname = "custid", caption = "", datetype = Types.INTEGER)
	public CField custid; //
	@CFieldinfo(fieldname = "feecenter", caption = "", datetype = Types.INTEGER)
	public CField feecenter; //
	@CFieldinfo(fieldname = "limited", caption = "", datetype = Types.INTEGER)
	public CField limited; //
	@CFieldinfo(fieldname = "maxemailspace", caption = "", datetype = Types.FLOAT)
	public CField maxemailspace; //
	@CFieldinfo(fieldname = "maxattachsize", caption = "", datetype = Types.FLOAT)
	public CField maxattachsize; //
	@CFieldinfo(fieldname = "usertype", caption = "用户类型", datetype = Types.INTEGER)
	public CField usertype; // 用户类型
	@CFieldinfo(fieldname = "md5sn", caption = "", datetype = Types.VARCHAR)
	public CField md5sn; //
	@CFieldinfo(fieldname = "displayname", caption = "", datetype = Types.VARCHAR)
	public CField displayname; //
	@CFieldinfo(fieldname = "idcardno", caption = "", datetype = Types.VARCHAR)
	public CField idcardno; //
	@CFieldinfo(fieldname = "attr1", precision = 32, scale = 0, caption = "扩展1", datetype = Types.VARCHAR)
	public CField attr1; // 扩展1
	@CFieldinfo(fieldname = "attr2", precision = 32, scale = 0, caption = "扩展2", datetype = Types.VARCHAR)
	public CField attr2; // 扩展2
	@CFieldinfo(fieldname = "attr3", precision = 32, scale = 0, caption = "扩展3", datetype = Types.VARCHAR)
	public CField attr3; // 扩展3
	@CFieldinfo(fieldname = "attr4", precision = 32, scale = 0, caption = "扩展4", datetype = Types.VARCHAR)
	public CField attr4; // 扩展4
	@CFieldinfo(fieldname = "attr5", precision = 32, scale = 0, caption = "扩展5", datetype = Types.VARCHAR)
	public CField attr5; // 扩展5
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwuser() throws Exception {
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
