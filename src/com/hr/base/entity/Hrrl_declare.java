package com.hr.base.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import com.hr.base.ctr.CtrHrrl_declare;

import java.sql.Types;

@CEntity(controller = CtrHrrl_declare.class)
public class Hrrl_declare extends CJPA {
	@CFieldinfo(fieldname = "ldid", iskey = true, notnull = true, caption = "申报ID", datetype = Types.INTEGER)
	public CField ldid; // 申报ID
	@CFieldinfo(fieldname = "ldcode", codeid = 104, notnull = true, caption = "申报单号", datetype = Types.VARCHAR)
	public CField ldcode; // 申报单号
	@CFieldinfo(fieldname = "ldtype", notnull = true, caption = "申报类型 1内部员工 2客户 3供应商", datetype = Types.INTEGER)
	public CField ldtype; // 申报类型 1内部员工 2客户 3供应商
	@CFieldinfo(fieldname = "dcldate", notnull = true, caption = "申报日期", datetype = Types.TIMESTAMP)
	public CField dcldate; // 申报日期
	@CFieldinfo(fieldname = "slid", notnull = true, caption = "关联关系ID", datetype = Types.INTEGER)
	public CField slid; // 关联关系ID
	@CFieldinfo(fieldname = "rlname", notnull = true, caption = "关联关系", datetype = Types.VARCHAR)
	public CField rlname; // 关联关系
	@CFieldinfo(fieldname = "rllabel_b", notnull = true, caption = "联关系称谓", datetype = Types.VARCHAR)
	public CField rllabel_b; // 联关系称谓
	@CFieldinfo(fieldname = "rllabel_a", notnull = true, caption = "联关系自称", datetype = Types.VARCHAR)
	public CField rllabel_a; // 联关系自称
	@CFieldinfo(fieldname = "rltype1", notnull = true, caption = "联关系类型", datetype = Types.VARCHAR)
	public CField rltype1; // 联关系类型
	@CFieldinfo(fieldname = "rltype2", notnull = true, caption = "联关系类别", datetype = Types.VARCHAR)
	public CField rltype2; // 联关系类别
	@CFieldinfo(fieldname = "hrvlevel", notnull = true, caption = "联关系分级", datetype = Types.INTEGER)
	public CField hrvlevel; // 联关系分级
	@CFieldinfo(fieldname = "rlext", notnull = true, caption = "联关系说明", datetype = Types.VARCHAR)
	public CField rlext; // 联关系说明
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "档案ID", datetype = Types.INTEGER)
	public CField er_id; // 档案ID
	@CFieldinfo(fieldname = "employee_code", notnull = true, caption = "工号", datetype = Types.VARCHAR)
	public CField employee_code; // 工号
	@CFieldinfo(fieldname = "employee_name", notnull = true, caption = "姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 姓名
	@CFieldinfo(fieldname = "sex", notnull = true, caption = "性别", datetype = Types.INTEGER)
	public CField sex; // 性别
	@CFieldinfo(fieldname = "hiredday", notnull = true, caption = "入职日期", datetype = Types.TIMESTAMP)
	public CField hiredday; // 入职日期
	@CFieldinfo(fieldname = "ljdate", caption = "离职日期", datetype = Types.TIMESTAMP)
	public CField ljdate; // 离职日期
	@CFieldinfo(fieldname = "orgid", notnull = true, caption = "部门ID", datetype = Types.INTEGER)
	public CField orgid; // 部门ID
	@CFieldinfo(fieldname = "orgcode", notnull = true, caption = "部门编码", datetype = Types.VARCHAR)
	public CField orgcode; // 部门编码
	@CFieldinfo(fieldname = "orgname", notnull = true, caption = "部门名称", datetype = Types.VARCHAR)
	public CField orgname; // 部门名称
	@CFieldinfo(fieldname = "ospid", notnull = true, caption = "职位ID", datetype = Types.INTEGER)
	public CField ospid; // 职位ID
	@CFieldinfo(fieldname = "ospcode", notnull = true, caption = "职位编码", datetype = Types.VARCHAR)
	public CField ospcode; // 职位编码
	@CFieldinfo(fieldname = "sp_name", notnull = true, caption = "职位", datetype = Types.VARCHAR)
	public CField sp_name; // 职位
	@CFieldinfo(fieldname = "hwc_namezl", notnull = true, caption = "职类", datetype = Types.VARCHAR)
	public CField hwc_namezl; // 职类
	@CFieldinfo(fieldname = "hg_name", notnull = true, caption = "职等", datetype = Types.VARCHAR)
	public CField hg_name; // 职等
	@CFieldinfo(fieldname = "lv_num", notnull = true, caption = "职级", datetype = Types.DECIMAL)
	public CField lv_num; // 职级
	@CFieldinfo(fieldname = "orghrlev", caption = "机构人事层级", defvalue = "0", datetype = Types.INTEGER)
	public CField orghrlev; // 机构人事层级
	@CFieldinfo(fieldname = "emplev", caption = "人事层级", datetype = Types.INTEGER)
	public CField emplev; // 人事层级
	@CFieldinfo(fieldname = "rer_id", caption = "关联档案ID", datetype = Types.INTEGER)
	public CField rer_id; // 关联档案ID
	@CFieldinfo(fieldname = "remployee_code", caption = "关联工号", datetype = Types.VARCHAR)
	public CField remployee_code; // 关联工号
	@CFieldinfo(fieldname = "rname", caption = "推荐客户/供应商", datetype = Types.VARCHAR)
	public CField rname; // 推荐客户/供应商
	@CFieldinfo(fieldname = "raddr", caption = "客户/供应商地址", datetype = Types.VARCHAR)
	public CField raddr; // 客户/供应商地址
	@CFieldinfo(fieldname = "remployee_name", caption = "关联姓名/推荐人姓名", datetype = Types.VARCHAR)
	public CField remployee_name; // 关联姓名/推荐人姓名
	@CFieldinfo(fieldname = "rsex", caption = "关联性别", datetype = Types.INTEGER)
	public CField rsex; // 关联性别
	@CFieldinfo(fieldname = "rhiredday", caption = "关联入职日期", datetype = Types.TIMESTAMP)
	public CField rhiredday; // 关联入职日期
	@CFieldinfo(fieldname = "rljdate", caption = "关联离职日期", datetype = Types.TIMESTAMP)
	public CField rljdate; // 关联离职日期
	@CFieldinfo(fieldname = "rorgid", caption = "关联部门ID", datetype = Types.INTEGER)
	public CField rorgid; // 关联部门ID
	@CFieldinfo(fieldname = "rorgcode", caption = "关联部门编码", datetype = Types.VARCHAR)
	public CField rorgcode; // 关联部门编码
	@CFieldinfo(fieldname = "rorgname", caption = "关联部门名称/推荐人工作单位", datetype = Types.VARCHAR)
	public CField rorgname; // 关联部门名称/推荐人工作单位
	@CFieldinfo(fieldname = "rospid", caption = "关联职位ID", datetype = Types.INTEGER)
	public CField rospid; // 关联职位ID
	@CFieldinfo(fieldname = "rospcode", caption = "关联职位编码", datetype = Types.VARCHAR)
	public CField rospcode; // 关联职位编码
	@CFieldinfo(fieldname = "rsp_name", caption = "关联职位/推荐人职务", datetype = Types.VARCHAR)
	public CField rsp_name; // 关联职位/推荐人职务
	@CFieldinfo(fieldname = "rhwc_namezl", caption = "关联职类", datetype = Types.VARCHAR)
	public CField rhwc_namezl; // 关联职类
	@CFieldinfo(fieldname = "rhg_name", caption = "关联职等", datetype = Types.VARCHAR)
	public CField rhg_name; // 关联职等
	@CFieldinfo(fieldname = "rlv_num", caption = "关联职级", datetype = Types.DECIMAL)
	public CField rlv_num; // 关联职级
	@CFieldinfo(fieldname = "rmobile", caption = "推荐人手机号码", datetype = Types.VARCHAR)
	public CField rmobile; // 推荐人手机号码
	@CFieldinfo(fieldname = "rbusiness", caption = "商业厉害关系 1构成 2不构成", datetype = Types.INTEGER)
	public CField rbusiness; // 商业厉害关系 1构成 2不构成
	@CFieldinfo(fieldname = "rlmanagetype", caption = "管理关系类别", datetype = Types.INTEGER)
	public CField rlmanagetype; // 管理关系类别
	@CFieldinfo(fieldname = "rctrms", caption = "管控措施 1 不管控、2调动处理 3申请豁免 4、其他", datetype = Types.INTEGER)
	public CField rctrms; // 管控措施 1 不管控、2调动处理 3申请豁免 4、其他
	@CFieldinfo(fieldname = "rccode", caption = "管控单编码", datetype = Types.VARCHAR)
	public CField rccode; // 管控单编码
	@CFieldinfo(fieldname = "useable", caption = "有效", defvalue = "1", datetype = Types.INTEGER)
	public CField useable; // 有效
	@CFieldinfo(fieldname = "disusetime", caption = "失效时间", datetype = Types.TIMESTAMP)
	public CField disusetime; // 失效时间
	@CFieldinfo(fieldname = "disuseremark", caption = "失效备注", datetype = Types.VARCHAR)
	public CField disuseremark; // 失效备注
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
	@CFieldinfo(fieldname = "creator", notnull = true, caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", notnull = true, caption = "创建时间", datetype = Types.TIMESTAMP)
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
	@CFieldinfo(fieldname = "attribute5", caption = "备用字段5", datetype = Types.VARCHAR)
	public CField attribute5; // 备用字段5
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hrrl_declare() throws Exception {
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
