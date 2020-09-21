package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hr_employee_nreward extends CJPA {
	@CFieldinfo(fieldname = "nemprw_id", iskey = true, notnull = true, caption = "激励ID", datetype = Types.INTEGER)
	public CField nemprw_id; // 激励ID
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "员工档案ID", datetype = Types.INTEGER)
	public CField er_id; // 员工档案ID
	@CFieldinfo(fieldname = "rewardtime", notnull = true, caption = "激励日期", datetype = Types.TIMESTAMP)
	public CField rewardtime; // 激励日期
	@CFieldinfo(fieldname = "rreason", notnull = true, caption = "激励事由", datetype = Types.VARCHAR)
	public CField rreason; // 激励事由
	@CFieldinfo(fieldname = "rwlev", notnull = true, caption = "级别", datetype = Types.INTEGER)
	public CField rwlev; // 级别
	@CFieldinfo(fieldname = "rwscore", caption = "绩效系数", datetype = Types.DECIMAL)
	public CField rwscore; // 绩效系数
	@CFieldinfo(fieldname = "rwfile", caption = "激励支持文件", datetype = Types.VARCHAR)
	public CField rwfile; // 激励支持文件
	@CFieldinfo(fieldname = "rwfilenum", caption = "文件编号", datetype = Types.VARCHAR)
	public CField rwfilenum; // 文件编号
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

	public Hr_employee_nreward() throws Exception {
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
