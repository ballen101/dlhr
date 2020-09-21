package com.hr.recruit.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.recruit.ctr.CtrHr_recruit_distribution;

import java.sql.Types;

@CEntity(caption = "分配表单", controller = CtrHr_recruit_distribution.class)
public class Hr_recruit_distribution extends CJPA {
	@CFieldinfo(fieldname = "recruit_distribution_id", iskey = true, notnull = true, precision = 20, scale = 0, caption = "ID", datetype = Types.INTEGER)
	public CField recruit_distribution_id; // ID
	@CFieldinfo(fieldname = "recruit_distribution_code", codeid = 120, precision = 21, scale = 0, caption = "分配编码", datetype = Types.VARCHAR)
	public CField recruit_distribution_code; // 分配编码
	@CFieldinfo(fieldname = "recruit_quachk_id", precision = 10, scale = 0, caption = "资格对比ID", datetype = Types.INTEGER)
	public CField recruit_quachk_id; // 资格对比ID
	@CFieldinfo(fieldname = "er_id", notnull = true, precision = 20, scale = 0, caption = "人事ID", datetype = Types.INTEGER)
	public CField er_id; // 人事ID
	@CFieldinfo(fieldname = "er_code", precision = 21, scale = 0, caption = "档案编码", datetype = Types.VARCHAR)
	public CField er_code; // 档案编码
	@CFieldinfo(fieldname = "employee_code", notnull = true, precision = 21, scale = 0, caption = "工号", datetype = Types.VARCHAR)
	public CField employee_code; // 工号
	@CFieldinfo(fieldname = "employee_name", notnull = true, precision = 64, scale = 0, caption = "姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 姓名
	@CFieldinfo(fieldname = "hiredday", precision = 19, scale = 0, caption = "入职日期", datetype = Types.TIMESTAMP)
	public CField hiredday; // 入职日期
	@CFieldinfo(fieldname = "emnature", precision = 42, scale = 0, caption = "人员性质", datetype = Types.VARCHAR)
	public CField emnature; // 人员性质
	@CFieldinfo(fieldname = "orgid", notnull = true, precision = 10, scale = 0, caption = "部门ID", datetype = Types.INTEGER)
	public CField orgid; // 部门ID
	@CFieldinfo(fieldname = "orgcode", notnull = true, precision = 21, scale = 0, caption = "部门编码", datetype = Types.VARCHAR)
	public CField orgcode; // 部门编码
	@CFieldinfo(fieldname = "orgname", notnull = true, precision = 128, scale = 0, caption = "部门名称", datetype = Types.VARCHAR)
	public CField orgname; // 部门名称
	@CFieldinfo(fieldname = "hwc_namezl", notnull = true, precision = 42, scale = 0, caption = "职类", datetype = Types.VARCHAR)
	public CField hwc_namezl; // 职类
	@CFieldinfo(fieldname = "lv_id", precision = 10, scale = 0, caption = "职级ID", datetype = Types.INTEGER)
	public CField lv_id; // 职级ID
	@CFieldinfo(fieldname = "lv_num", notnull = true, precision = 4, scale = 1, caption = "职级", datetype = Types.DECIMAL)
	public CField lv_num; // 职级
	@CFieldinfo(fieldname = "hg_id", precision = 10, scale = 0, caption = "职等ID", datetype = Types.INTEGER)
	public CField hg_id; // 职等ID
	@CFieldinfo(fieldname = "hg_code", precision = 21, scale = 0, caption = "职等编码", datetype = Types.VARCHAR)
	public CField hg_code; // 职等编码
	@CFieldinfo(fieldname = "hg_name", notnull = true, precision = 128, scale = 0, caption = "职等名称", datetype = Types.VARCHAR)
	public CField hg_name; // 职等名称
	@CFieldinfo(fieldname = "ospid", precision = 20, scale = 0, caption = "职位ID", datetype = Types.INTEGER)
	public CField ospid; // 职位ID
	@CFieldinfo(fieldname = "ospcode", precision = 21, scale = 0, caption = "职位编码", datetype = Types.VARCHAR)
	public CField ospcode; // 职位编码
	@CFieldinfo(fieldname = "sp_name", notnull = true, precision = 128, scale = 0, caption = "职位名称", datetype = Types.VARCHAR)
	public CField sp_name; // 职位名称
	@CFieldinfo(fieldname = "norgid", notnull = true, precision = 10, scale = 0, caption = "分配部门ID", datetype = Types.INTEGER)
	public CField norgid; // 分配部门ID
	@CFieldinfo(fieldname = "norgcode", notnull = true, precision = 21, scale = 0, caption = "分配部门编码", datetype = Types.VARCHAR)
	public CField norgcode; // 分配部门编码
	@CFieldinfo(fieldname = "norgname", notnull = true, precision = 128, scale = 0, caption = "分配部门名称", datetype = Types.VARCHAR)
	public CField norgname; // 分配部门名称
	@CFieldinfo(fieldname = "nhwc_namezl", notnull = true, precision = 42, scale = 0, caption = "分配职类", datetype = Types.VARCHAR)
	public CField nhwc_namezl; // 分配职类
	@CFieldinfo(fieldname = "nlv_id", precision = 10, scale = 0, caption = "分配职级ID", datetype = Types.INTEGER)
	public CField nlv_id; // 分配职级ID
	@CFieldinfo(fieldname = "nlv_num", notnull = true, precision = 4, scale = 1, caption = "分配职级", datetype = Types.DECIMAL)
	public CField nlv_num; // 分配职级
	@CFieldinfo(fieldname = "nhg_id", precision = 10, scale = 0, caption = "分配职等ID", datetype = Types.INTEGER)
	public CField nhg_id; // 分配职等ID
	@CFieldinfo(fieldname = "nhg_code", precision = 21, scale = 0, caption = "分配职等编码", datetype = Types.VARCHAR)
	public CField nhg_code; // 分配职等编码
	@CFieldinfo(fieldname = "nhg_name", notnull = true, precision = 128, scale = 0, caption = "分配职等名称", datetype = Types.VARCHAR)
	public CField nhg_name; // 分配职等名称
	@CFieldinfo(fieldname = "nospid", precision = 20, scale = 0, caption = "分配职位ID", datetype = Types.INTEGER)
	public CField nospid; // 分配职位ID
	@CFieldinfo(fieldname = "nospcode", precision = 21, scale = 0, caption = "分配职位编码", datetype = Types.VARCHAR)
	public CField nospcode; // 分配职位编码
	@CFieldinfo(fieldname = "nsp_name", notnull = true, precision = 128, scale = 0, caption = "分配职位名称", datetype = Types.VARCHAR)
	public CField nsp_name; // 分配职位名称
	@CFieldinfo(fieldname = "note", precision = 682, scale = 0, caption = "备注", datetype = Types.VARCHAR)
	public CField note; // 备注
	@CFieldinfo(fieldname = "attid", precision = 20, scale = 0, caption = "attid", datetype = Types.INTEGER)
	public CField attid; // attid
	@CFieldinfo(fieldname = "entid", notnull = true, precision = 5, scale = 0, caption = "entid", datetype = Types.INTEGER)
	public CField entid; // entid
	@CFieldinfo(fieldname = "idpath", notnull = true, precision = 341, scale = 0, caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	@CFieldinfo(fieldname = "creator", notnull = true, precision = 32, scale = 0, caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", notnull = true, precision = 19, scale = 0, caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", precision = 32, scale = 0, caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", precision = 19, scale = 0, caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	@CFieldinfo(fieldname = "property1", precision = 42, scale = 0, caption = "备用字段1", datetype = Types.VARCHAR)
	public CField property1; // 备用字段1
	@CFieldinfo(fieldname = "property2", precision = 85, scale = 0, caption = "备用字段2", datetype = Types.VARCHAR)
	public CField property2; // 备用字段2
	@CFieldinfo(fieldname = "property3", precision = 42, scale = 0, caption = "备用字段3", datetype = Types.VARCHAR)
	public CField property3; // 备用字段3
	@CFieldinfo(fieldname = "property4", precision = 42, scale = 0, caption = "备用字段4", datetype = Types.VARCHAR)
	public CField property4; // 备用字段4
	@CFieldinfo(fieldname = "property5", precision = 42, scale = 0, caption = "备用字段5", datetype = Types.VARCHAR)
	public CField property5; // 备用字段5
	@CFieldinfo(fieldname = "stat", precision = 2, scale = 0, caption = "表单状态", datetype = Types.INTEGER)
	public CField stat; // 表单状态
	@CFieldinfo(fieldname = "wfid", precision = 20, scale = 0, caption = "wfid", datetype = Types.INTEGER)
	public CField wfid; // wfid
	@CFieldinfo(fieldname = "nemnature", precision = 42, scale = 0, caption = "分配人员性质", datetype = Types.VARCHAR)
	public CField nemnature; // 分配人员性质
	@CFieldinfo(fieldname = "distribution_type", precision = 2, scale = 0, caption = "分配类型", datetype = Types.INTEGER)
	public CField distribution_type; // 分配类型
	@CFieldinfo(fieldname = "distribution_date", precision = 19, scale = 0, caption = "分配时间", datetype = Types.TIMESTAMP)
	public CField distribution_date; // 分配时间
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hr_recruit_distribution() throws Exception {
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
