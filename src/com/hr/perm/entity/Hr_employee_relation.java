package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hr_employee_relation extends CJPA {
	@CFieldinfo(fieldname = "empr_id", iskey = true, notnull = true, caption = "关联关系ID", datetype = Types.INTEGER)
	public CField empr_id; // 关联关系ID
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "员工档案ID", datetype = Types.INTEGER)
	public CField er_id; // 员工档案ID
	@CFieldinfo(fieldname = "hrvid1", notnull = true, caption = "关系类别ID", datetype = Types.INTEGER)
	public CField hrvid1; // 关系类别ID
	@CFieldinfo(fieldname = "hrvid2", notnull = true, caption = "关联关系明细ID", datetype = Types.INTEGER)
	public CField hrvid2; // 关联关系明细ID
	@CFieldinfo(fieldname = "hrvname", caption = "与本人关系", datetype = Types.VARCHAR)
	public CField hrvname; // 与本人关系
	@CFieldinfo(fieldname = "hrvlevel", caption = "关系分级", datetype = Types.INTEGER)
	public CField hrvlevel; // 关系分级
	@CFieldinfo(fieldname = "rchengwei", caption = "关联人称谓", datetype = Types.VARCHAR)
	public CField rchengwei; // 关联人称谓
	@CFieldinfo(fieldname = "rtype", caption = "关联关系类别", datetype = Types.VARCHAR)
	public CField rtype; // 关联关系类别
	@CFieldinfo(fieldname = "rdetail", caption = "关联关系明细", datetype = Types.VARCHAR)
	public CField rdetail; // 关联关系明细
	@CFieldinfo(fieldname = "mgrtype", caption = "管理关联关系类别", datetype = Types.INTEGER)
	public CField mgrtype; // 管理关联关系类别
	@CFieldinfo(fieldname = "dectime", caption = "申报时间", datetype = Types.TIMESTAMP)
	public CField dectime; // 申报时间
	@CFieldinfo(fieldname = "isremitted", caption = "申请豁免", datetype = Types.INTEGER)
	public CField isremitted; // 申请豁免
	@CFieldinfo(fieldname = "rer_id", caption = "关联员工档案ID", datetype = Types.INTEGER)
	public CField rer_id; // 关联员工档案ID
	@CFieldinfo(fieldname = "rer_code", caption = "关联员工档案编码", datetype = Types.VARCHAR)
	public CField rer_code; // 关联员工档案编码
	@CFieldinfo(fieldname = "employee_code", caption = "关联员工工号", datetype = Types.VARCHAR)
	public CField employee_code; // 关联员工工号
	@CFieldinfo(fieldname = "remployee_name", caption = "关联人姓名", datetype = Types.VARCHAR)
	public CField remployee_name; // 关联人姓名
	@CFieldinfo(fieldname = "out_name", caption = "外部人员", datetype = Types.VARCHAR)
	public CField out_name; // 外部人员
	@CFieldinfo(fieldname = "rorgid", caption = "关联机构ID", datetype = Types.INTEGER)
	public CField rorgid; // 关联机构ID
	@CFieldinfo(fieldname = "rorgname", caption = "关联机构名称", datetype = Types.VARCHAR)
	public CField rorgname; // 关联机构名称
	@CFieldinfo(fieldname = "rorgcode", caption = "关联机构编码", datetype = Types.VARCHAR)
	public CField rorgcode; // 关联机构编码
	@CFieldinfo(fieldname = "rospid", caption = "机构职位ID", datetype = Types.INTEGER)
	public CField rospid; // 机构职位ID
	@CFieldinfo(fieldname = "rospcode", caption = "机构职位编码", datetype = Types.VARCHAR)
	public CField rospcode; // 机构职位编码
	@CFieldinfo(fieldname = "rsp_name", caption = "职位名称", datetype = Types.VARCHAR)
	public CField rsp_name; // 职位名称
	@CFieldinfo(fieldname = "intime", caption = "入职时间", datetype = Types.TIMESTAMP)
	public CField intime; // 入职时间
	@CFieldinfo(fieldname = "outtime", caption = "离职时间", datetype = Types.TIMESTAMP)
	public CField outtime; // 离职时间
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

	public Hr_employee_relation() throws Exception {
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
