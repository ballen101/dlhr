package com.hr.base.entity;

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
public class Hrrl_exemption extends CJPA {
	@CFieldinfo(fieldname = "leid", iskey = true, notnull = true, caption = "豁免申报ID", datetype = Types.INTEGER)
	public CField leid; // 豁免申报ID
	@CFieldinfo(fieldname = "lecode", codeid = 111, notnull = true, caption = "豁免申报单号", datetype = Types.VARCHAR)
	public CField lecode; // 豁免申报单号
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "档案ID", datetype = Types.INTEGER)
	public CField er_id; // 档案ID
	@CFieldinfo(fieldname = "employee_code", notnull = true, caption = "工号", datetype = Types.VARCHAR)
	public CField employee_code; // 工号
	@CFieldinfo(fieldname = "employee_name", notnull = true, caption = "姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 姓名
	@CFieldinfo(fieldname = "sex", caption = "性别", datetype = Types.INTEGER)
	public CField sex; // 性别
	@CFieldinfo(fieldname = "hiredday", caption = "入职日期", datetype = Types.TIMESTAMP)
	public CField hiredday; // 入职日期
	@CFieldinfo(fieldname = "ljdate", caption = "离职日期", datetype = Types.TIMESTAMP)
	public CField ljdate; // 离职日期
	@CFieldinfo(fieldname = "orgid", notnull = true, caption = "部门ID", datetype = Types.INTEGER)
	public CField orgid; // 部门ID
	@CFieldinfo(fieldname = "orgcode", notnull = true, caption = "部门编码", datetype = Types.VARCHAR)
	public CField orgcode; // 部门编码
	@CFieldinfo(fieldname = "orgname", notnull = true, caption = "部门名称", datetype = Types.VARCHAR)
	public CField orgname; // 部门名称
	@CFieldinfo(fieldname = "ospid", caption = "职位ID", datetype = Types.INTEGER)
	public CField ospid; // 职位ID
	@CFieldinfo(fieldname = "ospcode", caption = "职位编码", datetype = Types.VARCHAR)
	public CField ospcode; // 职位编码
	@CFieldinfo(fieldname = "sp_name", caption = "职位", datetype = Types.VARCHAR)
	public CField sp_name; // 职位
	@CFieldinfo(fieldname = "hwc_namezl", caption = "职类", datetype = Types.VARCHAR)
	public CField hwc_namezl; // 职类
	@CFieldinfo(fieldname = "hg_name", caption = "职等", datetype = Types.VARCHAR)
	public CField hg_name; // 职等
	@CFieldinfo(fieldname = "lv_num", caption = "职级", datetype = Types.DECIMAL)
	public CField lv_num; // 职级
	@CFieldinfo(fieldname = "orghrlev", caption = "机构人事层级", defvalue = "0", datetype = Types.INTEGER)
	public CField orghrlev; // 机构人事层级
	@CFieldinfo(fieldname = "emplev", caption = "人事层级", datetype = Types.INTEGER)
	public CField emplev; // 人事层级
	@CFieldinfo(fieldname = "rer_id", notnull = true, caption = "档案ID", datetype = Types.INTEGER)
	public CField rer_id; // 档案ID
	@CFieldinfo(fieldname = "remployee_code", notnull = true, caption = "工号", datetype = Types.VARCHAR)
	public CField remployee_code; // 工号
	@CFieldinfo(fieldname = "remployee_name", notnull = true, caption = "姓名", datetype = Types.VARCHAR)
	public CField remployee_name; // 姓名
	@CFieldinfo(fieldname = "rsex", caption = "性别", datetype = Types.INTEGER)
	public CField rsex; // 性别
	@CFieldinfo(fieldname = "rhiredday", caption = "入职日期", datetype = Types.TIMESTAMP)
	public CField rhiredday; // 入职日期
	@CFieldinfo(fieldname = "rljdate", caption = "离职日期", datetype = Types.TIMESTAMP)
	public CField rljdate; // 离职日期
	@CFieldinfo(fieldname = "rorgid", notnull = true, caption = "部门ID", datetype = Types.INTEGER)
	public CField rorgid; // 部门ID
	@CFieldinfo(fieldname = "rorgcode", notnull = true, caption = "部门编码", datetype = Types.VARCHAR)
	public CField rorgcode; // 部门编码
	@CFieldinfo(fieldname = "rorgname", notnull = true, caption = "部门名称/推荐人工作单位", datetype = Types.VARCHAR)
	public CField rorgname; // 部门名称/推荐人工作单位
	@CFieldinfo(fieldname = "rospid", caption = "职位ID", datetype = Types.INTEGER)
	public CField rospid; // 职位ID
	@CFieldinfo(fieldname = "rospcode", caption = "职位编码", datetype = Types.VARCHAR)
	public CField rospcode; // 职位编码
	@CFieldinfo(fieldname = "rsp_name", caption = "职位/推荐人职务", datetype = Types.VARCHAR)
	public CField rsp_name; // 职位/推荐人职务
	@CFieldinfo(fieldname = "rhwc_namezl", caption = "职类", datetype = Types.VARCHAR)
	public CField rhwc_namezl; // 职类
	@CFieldinfo(fieldname = "rhg_name", caption = "职等", datetype = Types.VARCHAR)
	public CField rhg_name; // 职等
	@CFieldinfo(fieldname = "rlv_num", caption = "职级", datetype = Types.DECIMAL)
	public CField rlv_num; // 职级
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
	@CFieldinfo(fieldname = "rlmanagetype", caption = "管理关系类别", datetype = Types.INTEGER)
	public CField rlmanagetype; // 管理关系类别
	@CFieldinfo(fieldname = "letype", caption = "豁免类型 新入职 晋升 调动 新结成 其他", datetype = Types.INTEGER)
	public CField letype; // 豁免类型 新入职 晋升 调动 新结成 其他
	@CFieldinfo(fieldname = "lereason", caption = "豁免原因", datetype = Types.VARCHAR)
	public CField lereason; // 豁免原因
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

	public Hrrl_exemption() throws Exception {
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
