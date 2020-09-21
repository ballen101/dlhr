package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;

import java.sql.Types;

@CEntity()
public class Hr_employee_cretl extends CJPA {
	@CFieldinfo(fieldname = "cert_id", iskey = true, notnull = true, caption = "证件ID", datetype = Types.INTEGER)
	public CField cert_id; // 证件ID
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "员工档案ID", datetype = Types.INTEGER)
	public CField er_id; // 员工档案ID
	@CFieldinfo(fieldname = "picture_id", caption = "图片ID", datetype = Types.INTEGER)
	public CField picture_id; // 图片ID
	@CFieldinfo(fieldname="attid",caption="附件",datetype=Types.INTEGER)
	public CField attid;  //附件
	@CFieldinfo(fieldname = "cert_type", caption = "证件类型", datetype = Types.INTEGER)
	public CField cert_type; // 证件类型
	@CFieldinfo(fieldname = "cert_name", notnull = true, caption = "证件名称", datetype = Types.VARCHAR)
	public CField cert_name; // 证件名称
	@CFieldinfo(fieldname = "sign_date", caption = "签发日期", datetype = Types.TIMESTAMP)
	public CField sign_date; // 签发日期
	@CFieldinfo(fieldname = "sign_org", notnull = true, caption = "签发单位", datetype = Types.VARCHAR)
	public CField sign_org; // 签发单位
	@CFieldinfo(fieldname = "expired_date", caption = "到期日期", datetype = Types.TIMESTAMP)
	public CField expired_date; // 到期日期
	@CFieldinfo(fieldname = "is_remind", caption = "是否预警", datetype = Types.INTEGER)
	public CField is_remind; // 是否预警
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "attribute1", caption = "备用字段1", datetype = Types.VARCHAR)
	public CField attribute1; // 备用字段1
	@CFieldinfo(fieldname = "attribute2", caption = "备用字段2", datetype = Types.VARCHAR)
	public CField attribute2; // 备用字段2
	@CFieldinfo(fieldname = "attribute3", caption = "备用字段3", datetype = Types.VARCHAR)
	public CField attribute3; // 备用字段3
	@CFieldinfo(fieldname = "attribute4", caption = "备用字段4", datetype = Types.VARCHAR)
	public CField attribute4; // 备用字段4
	@CFieldinfo(fieldname = "attribute5", caption = "备用字段5", datetype = Types.VARCHAR)
	public CField attribute5; // 备用字段5
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hr_employee_cretl() throws Exception {
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
