package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hr_employee_trainexp extends CJPA {
	@CFieldinfo(fieldname = "tranexp_id", iskey = true, notnull = true, caption = "学习经历ID", datetype = Types.INTEGER)
	public CField tranexp_id; // 学习经历ID
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "员工档案ID", datetype = Types.INTEGER)
	public CField er_id; // 员工档案ID
	@CFieldinfo(fieldname = "begintime", notnull = true, caption = "开始时间", datetype = Types.TIMESTAMP)
	public CField begintime; // 开始时间
	@CFieldinfo(fieldname = "endtime", notnull = true, caption = "结束时间", datetype = Types.TIMESTAMP)
	public CField endtime; // 结束时间
	@CFieldinfo(fieldname = "schoolname", notnull = true, caption = "培训机构", datetype = Types.VARCHAR)
	public CField schoolname; // 培训机构
	@CFieldinfo(fieldname = "lecturer", caption = "讲师", datetype = Types.VARCHAR)
	public CField lecturer; // 讲师
	@CFieldinfo(fieldname = "classh", caption = "课时", datetype = Types.VARCHAR)
	public CField classh; // 课时
	@CFieldinfo(fieldname = "major", caption = "培训内容", datetype = Types.VARCHAR)
	public CField major; // 培训内容
	@CFieldinfo(fieldname = "degree", caption = "学历", datetype = Types.INTEGER)
	public CField degree; // 学历
	@CFieldinfo(fieldname = "certname", caption = "证书名称", datetype = Types.VARCHAR)
	public CField certname; // 证书名称
	@CFieldinfo(fieldname = "certnum", caption = "证书编号", datetype = Types.VARCHAR)
	public CField certnum; // 证书编号
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

	public Hr_employee_trainexp() throws Exception {
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
