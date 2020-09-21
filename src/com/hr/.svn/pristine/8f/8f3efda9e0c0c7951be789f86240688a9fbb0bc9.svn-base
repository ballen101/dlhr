package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hr_employee_trainwk extends CJPA {
	@CFieldinfo(fieldname = "twkid", iskey = true, notnull = true, caption = "培训ID", datetype = Types.INTEGER)
	public CField twkid; // 培训ID
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "员工档案ID", datetype = Types.INTEGER)
	public CField er_id; // 员工档案ID
	@CFieldinfo(fieldname = "employee_code", notnull = true, caption = "工号", datetype = Types.VARCHAR)
	public CField employee_code; // 工号
	@CFieldinfo(fieldname = "twktype", notnull = true, caption = "培训类别", datetype = Types.INTEGER)
	public CField twktype; // 培训类别
	@CFieldinfo(fieldname = "twktitle", notnull = true, caption = "培训课题", datetype = Types.VARCHAR)
	public CField twktitle; // 培训课题
	@CFieldinfo(fieldname = "begintime", notnull = true, caption = "培训起始日期", datetype = Types.TIMESTAMP)
	public CField begintime; // 培训起始日期
	@CFieldinfo(fieldname = "endtime", caption = "培训结束日期", datetype = Types.TIMESTAMP)
	public CField endtime; // 培训结束日期
	@CFieldinfo(fieldname = "classh", caption = "培训课时", datetype = Types.VARCHAR)
	public CField classh; // 培训课时
	@CFieldinfo(fieldname = "schoolname", notnull = true, caption = "培训机构", datetype = Types.VARCHAR)
	public CField schoolname; // 培训机构
	@CFieldinfo(fieldname = "lecturer", caption = "培训讲师", datetype = Types.VARCHAR)
	public CField lecturer; // 培训讲师
	@CFieldinfo(fieldname = "major", caption = "培训内容", datetype = Types.VARCHAR)
	public CField major; // 培训内容
	@CFieldinfo(fieldname = "certname", caption = "培训证书名称", datetype = Types.VARCHAR)
	public CField certname; // 培训证书名称
	@CFieldinfo(fieldname = "certnum", caption = "证书编号", datetype = Types.VARCHAR)
	public CField certnum; // 证书编号
	@CFieldinfo(fieldname = "cervtime", caption = "培训证书有效期", datetype = Types.TIMESTAMP)
	public CField cervtime; // 培训证书有效期
	@CFieldinfo(fieldname = "twkexamine", caption = "培训考核", datetype = Types.VARCHAR)
	public CField twkexamine; // 培训考核
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "odsno", caption = "原数据ID", datetype = Types.INTEGER)
	public CField odsno; // 原数据ID
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hr_employee_trainwk() throws Exception {
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
