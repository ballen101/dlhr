package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hr_entrybatch_probline extends CJPA {
	@CFieldinfo(fieldname = "ebptlid", iskey = true, notnull = true, caption = "转正行ID", datetype = Types.INTEGER)
	public CField ebptlid; // 转正行ID
	@CFieldinfo(fieldname = "ebptid", notnull = true, caption = "转正ID", datetype = Types.INTEGER)
	public CField ebptid; // 转正ID
	@CFieldinfo(fieldname="sourceid",caption="源单ID",datetype=Types.INTEGER)
	public CField sourceid;  //源单ID
	@CFieldinfo(fieldname="sourcecode",caption="源单编码",datetype=Types.VARCHAR)
	public CField sourcecode;  //源单编码
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "档案ID", datetype = Types.INTEGER)
	public CField er_id; // 档案ID
	@CFieldinfo(fieldname = "er_code", caption = "档案编码", datetype = Types.VARCHAR)
	public CField er_code; // 档案编码
	@CFieldinfo(fieldname = "employee_code", notnull = true, caption = "工号", datetype = Types.VARCHAR)
	public CField employee_code; // 工号
	@CFieldinfo(fieldname = "employee_name", notnull = true, caption = "姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 姓名
	@CFieldinfo(fieldname = "orgid", notnull = true, caption = "部门ID", datetype = Types.INTEGER)
	public CField orgid; // 部门ID
	@CFieldinfo(fieldname = "orgcode", notnull = true, caption = "部门编码", datetype = Types.VARCHAR)
	public CField orgcode; // 部门编码
	@CFieldinfo(fieldname = "orgname", notnull = true, caption = "部门名称", datetype = Types.VARCHAR)
	public CField orgname; // 部门名称
	@CFieldinfo(fieldname = "id_number", notnull = true, caption = "身份证号", datetype = Types.VARCHAR)
	public CField id_number; // 身份证号
	@CFieldinfo(fieldname = "degree", caption = "学历", datetype = Types.INTEGER)
	public CField degree; // 学历
	@CFieldinfo(fieldname = "hwc_namezl", caption = "职类", datetype = Types.VARCHAR)
	public CField hwc_namezl; // 职类
	@CFieldinfo(fieldname = "lv_id", caption = "职级ID", datetype = Types.INTEGER)
	public CField lv_id; // 职级ID
	@CFieldinfo(fieldname = "lv_num", caption = "职级", datetype = Types.DECIMAL)
	public CField lv_num; // 职级
	@CFieldinfo(fieldname = "hg_id", caption = "职等ID", datetype = Types.INTEGER)
	public CField hg_id; // 职等ID
	@CFieldinfo(fieldname = "hg_code", caption = "职等编码", datetype = Types.VARCHAR)
	public CField hg_code; // 职等编码
	@CFieldinfo(fieldname = "hg_name", caption = "职等名称", datetype = Types.VARCHAR)
	public CField hg_name; // 职等名称
	@CFieldinfo(fieldname = "ospid", caption = "职位ID", datetype = Types.INTEGER)
	public CField ospid; // 职位ID
	@CFieldinfo(fieldname = "ospcode", caption = "职位编码", datetype = Types.VARCHAR)
	public CField ospcode; // 职位编码
	@CFieldinfo(fieldname = "sp_name", caption = "职位名称", datetype = Types.VARCHAR)
	public CField sp_name; // 职位名称
	@CFieldinfo(fieldname = "hiredday", caption = "入职日期", datetype = Types.TIMESTAMP)
	public CField hiredday; // 入职日期
	@CFieldinfo(fieldname = "probation", notnull = true, caption = "试用期", datetype = Types.INTEGER)
	public CField probation; // 试用期
	@CFieldinfo(fieldname = "pbtdate", notnull = true, caption = "待转正时间", datetype = Types.TIMESTAMP)
	public CField pbtdate; // 待转正时间
	@CFieldinfo(fieldname = "wfresult", caption = "评审结果  1 同意转正 2 延长试用 3 试用不合格", datetype = Types.INTEGER)
	public CField wfresult; // 评审结果 1 同意转正 2 延长试用 3 试用不合格
	@CFieldinfo(fieldname = "wfpbprobation", caption = "延长试用期 月数 ", datetype = Types.INTEGER)
	public CField wfpbprobation; // 延长试用期 月数
	@CFieldinfo(fieldname = "wfpbtdate", caption = "延长试用日期", datetype = Types.TIMESTAMP)
	public CField wfpbtdate; // 延长试用日期
	@CFieldinfo(fieldname = "idpath", notnull = true, caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hr_entrybatch_probline() throws Exception {
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
