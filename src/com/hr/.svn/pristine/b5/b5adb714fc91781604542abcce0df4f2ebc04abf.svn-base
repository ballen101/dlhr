package com.hr.base.entity;

import java.sql.Types;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.hr.base.ctr.CtrHr_grade;

@CEntity(controller = CtrHr_grade.class)
public class Hr_grade extends CJPA {
	@CFieldinfo(fieldname = "hg_id", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField hg_id; // ID
	@CFieldinfo(fieldname = "hg_code", codeid = 47, notnull = true, caption = "职等编码", datetype = Types.VARCHAR)
	public CField hg_code; // 职等编码
	@CFieldinfo(fieldname = "hg_name", caption = "职等名称", datetype = Types.VARCHAR)
	public CField hg_name; // 职等名称
	@CFieldinfo(fieldname = "hwc_id", notnull = true, caption = "职类ID", datetype = Types.INTEGER)
	public CField hwc_id; // 职类ID
	@CFieldinfo(fieldname = "hw_code", notnull = true, caption = "职类代码", datetype = Types.VARCHAR)
	public CField hw_code; // 职类代码
	@CFieldinfo(fieldname = "hwc_name", caption = "职类名称", datetype = Types.VARCHAR)
	public CField hwc_name; // 职类名称
	@CFieldinfo(fieldname = "gtitle", caption = "职衔", datetype = Types.VARCHAR)
	public CField gtitle; // 职衔
	@CFieldinfo(fieldname="yhtype",caption="年假1 休年假 2 计算工资",datetype=Types.INTEGER)
	public CField yhtype;  //年假1 休年假 2 计算工资
	@CFieldinfo(fieldname = "usable", caption = "有效状态", datetype = Types.INTEGER)
	public CField usable; // 有效状态
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "creator", caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	@CFieldinfo(fieldname = "attribute1", caption = "备用字段1", datetype = Types.VARCHAR)
	public CField attribute1; // 备用字段1
	@CFieldinfo(fieldname = "attribute2", caption = "备用字段2", datetype = Types.VARCHAR)
	public CField attribute2; // 备用字段2
	@CFieldinfo(fieldname = "attribute3", caption = "备用字段3", datetype = Types.VARCHAR)
	public CField attribute3; // 备用字段3
	@CFieldinfo(fieldname = "attribute4", caption = "备用字段4", datetype = Types.VARCHAR)
	public CField attribute4; // 备用字段4
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义
	@CLinkFieldInfo(jpaclass = Hr_prolevel.class, linkFields = { @LinkFieldItem(lfield = "hg_id", mfield = "hg_id") })
	public CJPALineData<Hr_prolevel> hr_prolevels;

	public Hr_grade() throws Exception {
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
