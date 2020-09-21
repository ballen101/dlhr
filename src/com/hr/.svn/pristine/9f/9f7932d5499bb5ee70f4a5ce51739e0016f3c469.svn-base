package com.hr.inface.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(dbpool = "mssqlpx", tablename = "v_TrainRecord")
public class V_TrainRecord extends CJPA {
	@CFieldinfo(fieldname = "RowNo", caption = "序号", datetype = Types.BIGINT)
	public CField RowNo; // 序号
	@CFieldinfo(fieldname = "UserCode", caption = "工号", datetype = Types.VARCHAR)
	public CField UserCode; // 工号
	@CFieldinfo(fieldname = "CourseType", caption = "课程类别", datetype = Types.NVARCHAR)
	public CField CourseType; // 课程类别
	@CFieldinfo(fieldname = "CourseName", caption = "课程名称", datetype = Types.NVARCHAR)
	public CField CourseName; // 课程名称
	@CFieldinfo(fieldname = "CardStartTime", caption = "培训开始时间", datetype = Types.TIMESTAMP)
	public CField CardStartTime; // 培训开始时间
	@CFieldinfo(fieldname = "CardEndTime", caption = "培训结束时间", datetype = Types.TIMESTAMP)
	public CField CardEndTime; // 培训结束时间
	@CFieldinfo(fieldname = "fHours", caption = "培训时数", datetype = Types.NUMERIC)
	public CField fHours; // 培训时数
	@CFieldinfo(fieldname = "TeacherOrgName", caption = "培训机构", datetype = Types.VARCHAR)
	public CField TeacherOrgName; // 培训机构
	@CFieldinfo(fieldname = "TeacherStaffName", caption = "培训讲师", datetype = Types.VARCHAR)
	public CField TeacherStaffName; // 培训讲师
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public V_TrainRecord() throws Exception {
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
