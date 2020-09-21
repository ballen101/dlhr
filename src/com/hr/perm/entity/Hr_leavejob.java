package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.perm.ctr.CtrHr_leavejob;

import java.sql.Types;

@CEntity(controller = CtrHr_leavejob.class)
public class Hr_leavejob extends CJPA {
	@CFieldinfo(fieldname = "ljid", iskey = true, notnull = true, caption = "离职ID", datetype = Types.INTEGER)
	public CField ljid; // 离职ID
	@CFieldinfo(fieldname = "ljcode", notnull = true, codeid = 64, caption = "离职编码", datetype = Types.VARCHAR)
	public CField ljcode; // 离职编码
	@CFieldinfo(fieldname = "ljtype", notnull = true, caption = "实习生或员工离职 1 实习生 2 员工", datetype = Types.INTEGER)
	public CField ljtype; // 实习生或员工离职 1 实习生 2 员工
	@CFieldinfo(fieldname = "orgid", notnull = true, caption = "部门ID", datetype = Types.INTEGER)
	public CField orgid; // 部门ID
	@CFieldinfo(fieldname = "orgcode", notnull = true, caption = "部门编码", datetype = Types.VARCHAR)
	public CField orgcode; // 部门编码
	@CFieldinfo(fieldname = "orgname", notnull = true, caption = "部门名称", datetype = Types.VARCHAR)
	public CField orgname; // 部门名称
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "档案ID", datetype = Types.INTEGER)
	public CField er_id; // 档案ID
	@CFieldinfo(fieldname = "er_code", caption = "档案编码", datetype = Types.VARCHAR)
	public CField er_code; // 档案编码
	@CFieldinfo(fieldname = "employee_code", notnull = true, caption = "工号", datetype = Types.VARCHAR)
	public CField employee_code; // 工号
	@CFieldinfo(fieldname = "sex", notnull = true, caption = "性别", datetype = Types.INTEGER)
	public CField sex; // 性别
	@CFieldinfo(fieldname = "id_number", notnull = true, caption = "身份证号", datetype = Types.VARCHAR)
	public CField id_number; // 身份证号
	@CFieldinfo(fieldname = "employee_name", notnull = true, caption = "姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 姓名
	@CFieldinfo(fieldname = "degree", caption = "学历", datetype = Types.INTEGER)
	public CField degree; // 学历
	@CFieldinfo(fieldname = "birthday", notnull = true, caption = "出生日期", datetype = Types.TIMESTAMP)
	public CField birthday; // 出生日期
	@CFieldinfo(fieldname = "registeraddress", caption = "户籍住址", datetype = Types.VARCHAR)
	public CField registeraddress; // 户籍住址
	@CFieldinfo(fieldname = "hiredday", caption = "入职日期", datetype = Types.TIMESTAMP)
	public CField hiredday; // 入职日期
	@CFieldinfo(fieldname = "ljappdate", notnull = true, caption = "申请离职日期", datetype = Types.TIMESTAMP)
	public CField ljappdate; // 申请离职日期
	@CFieldinfo(fieldname = "ljdate", notnull = true, caption = "离职日期", datetype = Types.TIMESTAMP)
	public CField ljdate; // 离职日期
	@CFieldinfo(fieldname = "kqdate_end", precision = 19, scale = 0, caption = "考勤截止日期(包含)", datetype = Types.TIMESTAMP)
	public CField kqdate_end; // 考勤截止日期(包含)
	@CFieldinfo(fieldname = "worktime", notnull = true, caption = "司龄", datetype = Types.VARCHAR)
	public CField worktime; // 司龄
	@CFieldinfo(fieldname = "ljtype1", notnull = true, caption = "离职类别 自离 辞职 等", datetype = Types.INTEGER)
	public CField ljtype1; // 离职类别 自离 辞职 等
	@CFieldinfo(fieldname = "ljtype2", caption = "离职类型 主动离职 被动离职 协商离职", datetype = Types.INTEGER)
	public CField ljtype2; // 离职类型 主动离职 被动离职 协商离职
	@CFieldinfo(fieldname = "ljreason", caption = "离职原因", datetype = Types.VARCHAR)
	public CField ljreason; // 离职原因
	@CFieldinfo(fieldname = "iscpst", notnull = true, caption = "是否补偿", defvalue = "2", datetype = Types.INTEGER)
	public CField iscpst; // 是否补偿
	@CFieldinfo(fieldname = "cpstarm", notnull = true, caption = "补偿金额", defvalue = "0.00", datetype = Types.DECIMAL)
	public CField cpstarm; // 补偿金额
	@CFieldinfo(fieldname = "iscpt", notnull = true, caption = "是否投诉", defvalue = "2", datetype = Types.INTEGER)
	public CField iscpt; // 是否投诉
	@CFieldinfo(fieldname = "isabrt", notnull = true, caption = "是否仲裁", defvalue = "2", datetype = Types.INTEGER)
	public CField isabrt; // 是否仲裁
	@CFieldinfo(fieldname = "islawsuit", notnull = true, caption = "是否诉讼", defvalue = "2", datetype = Types.INTEGER)
	public CField islawsuit; // 是否诉讼
	@CFieldinfo(fieldname = "casenum", caption = "案号", datetype = Types.VARCHAR)
	public CField casenum; // 案号
	@CFieldinfo(fieldname = "isblacklist", notnull = true, caption = "是否加入黑名单", defvalue = "2", datetype = Types.INTEGER)
	public CField isblacklist; // 是否加入黑名单
	@CFieldinfo(fieldname = "addtype", caption = "加封类型", datetype = Types.INTEGER)
	public CField addtype; // 加封类型
	@CFieldinfo(fieldname = "addtype1", caption = "加封类别", datetype = Types.INTEGER)
	public CField addtype1; // 加封类别
	@CFieldinfo(fieldname = "blackreason", caption = "加入黑名单原因", datetype = Types.VARCHAR)
	public CField blackreason; // 加入黑名单原因
	@CFieldinfo(fieldname = "iscanced", notnull = true, caption = "已撤销", defvalue = "2", datetype = Types.INTEGER)
	public CField iscanced; // 已撤销
	@CFieldinfo(fieldname = "pempstatid", notnull = true, caption = "离职前状态", datetype = Types.INTEGER)
	public CField pempstatid; // 离职前状态
	@CFieldinfo(fieldname = "lv_id", notnull = true, caption = "职级ID", datetype = Types.INTEGER)
	public CField lv_id; // 职级ID
	@CFieldinfo(fieldname = "lv_num", notnull = true, caption = "职级", datetype = Types.DECIMAL)
	public CField lv_num; // 职级
	@CFieldinfo(fieldname = "hg_id", notnull = true, caption = "职等ID", datetype = Types.INTEGER)
	public CField hg_id; // 职等ID
	@CFieldinfo(fieldname = "hg_code", notnull = true, caption = "职等编码", datetype = Types.VARCHAR)
	public CField hg_code; // 职等编码
	@CFieldinfo(fieldname = "hg_name", notnull = true, caption = "职等名称", datetype = Types.VARCHAR)
	public CField hg_name; // 职等名称
	@CFieldinfo(fieldname = "ospid", notnull = true, caption = "职位ID", datetype = Types.INTEGER)
	public CField ospid; // 职位ID
	@CFieldinfo(fieldname = "ospcode", notnull = true, caption = "职位编码", datetype = Types.VARCHAR)
	public CField ospcode; // 职位编码
	@CFieldinfo(fieldname = "sp_name", notnull = true, caption = "职位名称", datetype = Types.VARCHAR)
	public CField sp_name; // 职位名称
	@CFieldinfo(fieldname = "hwc_namezl", caption = "职类", datetype = Types.VARCHAR)
	public CField hwc_namezl; // 职类
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "wfid", caption = "wfid", datetype = Types.INTEGER)
	public CField wfid; // wfid
	@CFieldinfo(fieldname = "attid", caption = "attid", datetype = Types.INTEGER)
	public CField attid; // attid
	@CFieldinfo(fieldname = "stat", notnull = true, caption = "表单状态", datetype = Types.INTEGER)
	public CField stat; // 表单状态
	@CFieldinfo(fieldname = "idpath", notnull = true, caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	@CFieldinfo(fieldname = "entid", notnull = true, caption = "entid", datetype = Types.INTEGER)
	public CField entid; // entid
	@CFieldinfo(fieldname = "creator", notnull = true, caption = "制单人", datetype = Types.VARCHAR)
	public CField creator; // 制单人
	@CFieldinfo(fieldname = "createtime", notnull = true, caption = "制单时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 制单时间
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
	@CFieldinfo(fieldname = "attribute5", caption = "备用字段5", datetype = Types.VARCHAR)
	public CField attribute5; // 备用字段5
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hr_leavejob() throws Exception {
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
