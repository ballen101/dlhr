package com.hr.salary.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption = "薪资明细表")
public class Hr_salary_list extends CJPA {
	@CFieldinfo(fieldname = "sl_id", iskey = true, notnull = true, precision = 20, scale = 0, caption = "工资明细ID", datetype = Types.INTEGER)
	public CField sl_id; // 工资明细ID
	@CFieldinfo(fieldname = "yearmonth", notnull = true, precision = 7, scale = 0, caption = "年月", datetype = Types.VARCHAR)
	public CField yearmonth; // 年月
	@CFieldinfo(fieldname = "er_id", notnull = true, precision = 10, scale = 0, caption = "员工档案ID", datetype = Types.INTEGER)
	public CField er_id; // 员工档案ID
	@CFieldinfo(fieldname = "employee_code", notnull = true, precision = 16, scale = 0, caption = "工号", datetype = Types.VARCHAR)
	public CField employee_code; // 工号
	@CFieldinfo(fieldname = "employee_name", notnull = true, precision = 64, scale = 0, caption = "姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 姓名
	@CFieldinfo(fieldname = "orgid", notnull = true, precision = 10, scale = 0, caption = "部门ID", datetype = Types.INTEGER)
	public CField orgid; // 部门ID
	@CFieldinfo(fieldname = "orgcode", notnull = true, precision = 16, scale = 0, caption = "部门编码", datetype = Types.VARCHAR)
	public CField orgcode; // 部门编码
	@CFieldinfo(fieldname = "orgname", notnull = true, precision = 128, scale = 0, caption = "部门名称", datetype = Types.VARCHAR)
	public CField orgname; // 部门名称
	@CFieldinfo(fieldname = "ospid", precision = 20, scale = 0, caption = "职位ID", datetype = Types.INTEGER)
	public CField ospid; // 职位ID
	@CFieldinfo(fieldname = "ospcode", precision = 16, scale = 0, caption = "职位编码", datetype = Types.VARCHAR)
	public CField ospcode; // 职位编码
	@CFieldinfo(fieldname = "sp_name", precision = 128, scale = 0, caption = "标准职位名称", datetype = Types.VARCHAR)
	public CField sp_name; // 标准职位名称
	@CFieldinfo(fieldname = "lv_id", notnull = true, precision = 10, scale = 0, caption = "职级ID", datetype = Types.INTEGER)
	public CField lv_id; // 职级ID
	@CFieldinfo(fieldname = "lv_num", notnull = true, precision = 4, scale = 1, caption = "职级", datetype = Types.DECIMAL)
	public CField lv_num; // 职级
	@CFieldinfo(fieldname = "hg_id", notnull = true, precision = 10, scale = 0, caption = "职等ID", datetype = Types.INTEGER)
	public CField hg_id; // 职等ID
	@CFieldinfo(fieldname = "hg_name", precision = 128, scale = 0, caption = "职等名称", datetype = Types.VARCHAR)
	public CField hg_name; // 职等名称
	@CFieldinfo(fieldname = "hwc_idzl", notnull = true, precision = 20, scale = 0, caption = "职类ID", datetype = Types.INTEGER)
	public CField hwc_idzl; // 职类ID
	@CFieldinfo(fieldname = "hwc_namezl", precision = 128, scale = 0, caption = "职类名称", datetype = Types.VARCHAR)
	public CField hwc_namezl; // 职类名称
	@CFieldinfo(fieldname = "hwc_idzq", notnull = true, precision = 20, scale = 0, caption = "职群ID", datetype = Types.INTEGER)
	public CField hwc_idzq; // 职群ID
	@CFieldinfo(fieldname = "hwc_namezq", precision = 128, scale = 0, caption = "职群名称", datetype = Types.VARCHAR)
	public CField hwc_namezq; // 职群名称
	@CFieldinfo(fieldname = "hwc_idzz", notnull = true, precision = 20, scale = 0, caption = "职种ID", datetype = Types.INTEGER)
	public CField hwc_idzz; // 职种ID
	@CFieldinfo(fieldname = "hwc_namezz", precision = 128, scale = 0, caption = "职种名称", datetype = Types.VARCHAR)
	public CField hwc_namezz; // 职种名称
	@CFieldinfo(fieldname = "hiredday", precision = 19, scale = 0, caption = "入职日期", datetype = Types.TIMESTAMP)
	public CField hiredday; // 入职日期
	@CFieldinfo(fieldname = "wagetype", precision = 1, scale = 0, caption = "月薪类型：1、月薪人员；2、非月薪人员", defvalue = "1", datetype = Types.INTEGER)
	public CField wagetype; // 月薪类型：1、月薪人员；2、非月薪人员
	@CFieldinfo(fieldname = "poswage", precision = 10, scale = 2, caption = "职位工资", datetype = Types.DECIMAL)
	public CField poswage; // 职位工资
	@CFieldinfo(fieldname = "basewage", precision = 10, scale = 2, caption = "基本工资", datetype = Types.DECIMAL)
	public CField basewage; // 基本工资
	@CFieldinfo(fieldname = "baseotwage", precision = 10, scale = 2, caption = "固定加班工资", datetype = Types.DECIMAL)
	public CField baseotwage; // 固定加班工资
	@CFieldinfo(fieldname = "skillwage", precision = 10, scale = 2, caption = "技能工资", datetype = Types.DECIMAL)
	public CField skillwage; // 技能工资
	@CFieldinfo(fieldname = "perforwage", precision = 10, scale = 2, caption = "绩效工资", datetype = Types.DECIMAL)
	public CField perforwage; // 绩效工资
	@CFieldinfo(fieldname = "skillsubs", precision = 10, scale = 2, caption = "技术津贴", datetype = Types.DECIMAL)
	public CField skillsubs; // 技术津贴
	@CFieldinfo(fieldname = "parttimesubs", precision = 10, scale = 2, caption = "兼职津贴", datetype = Types.DECIMAL)
	public CField parttimesubs; // 兼职津贴
	@CFieldinfo(fieldname = "postsubs", precision = 10, scale = 2, caption = "岗位津贴", datetype = Types.DECIMAL)
	public CField postsubs; // 岗位津贴
	@CFieldinfo(fieldname = "isfullattend", precision = 1, scale = 0, caption = "是否满勤", defvalue = "1", datetype = Types.INTEGER)
	public CField isfullattend; // 是否满勤
	@CFieldinfo(fieldname = "baseattend", precision = 6, scale = 2, caption = "基本出勤工时", datetype = Types.DECIMAL)
	public CField baseattend; // 基本出勤工时
	@CFieldinfo(fieldname = "normalot", precision = 6, scale = 2, caption = "平时加班工时", datetype = Types.DECIMAL)
	public CField normalot; // 平时加班工时
	@CFieldinfo(fieldname = "restot", precision = 6, scale = 2, caption = "休息加班工时", datetype = Types.DECIMAL)
	public CField restot; // 休息加班工时
	@CFieldinfo(fieldname = "tworkhours", precision = 8, scale = 2, caption = "总工时", datetype = Types.DECIMAL)
	public CField tworkhours; // 总工时
	@CFieldinfo(fieldname = "paynosubs", precision = 10, scale = 2, caption = "应发合计。不含津贴", datetype = Types.DECIMAL)
	public CField paynosubs; // 应发合计。不含津贴
	@CFieldinfo(fieldname = "paywsubs", precision = 10, scale = 2, caption = "应发合计。含津贴", datetype = Types.DECIMAL)
	public CField paywsubs; // 应发合计。含津贴
	@CFieldinfo(fieldname = "remark", precision = 512, scale = 0, caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "stat", notnull = true, precision = 2, scale = 0, caption = "流程状态", datetype = Types.INTEGER)
	public CField stat; // 流程状态
	@CFieldinfo(fieldname = "idpath", notnull = true, precision = 256, scale = 0, caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	@CFieldinfo(fieldname = "creator", notnull = true, precision = 32, scale = 0, caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", notnull = true, precision = 19, scale = 0, caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", precision = 32, scale = 0, caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", precision = 19, scale = 0, caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	@CFieldinfo(fieldname = "property1", precision = 32, scale = 0, caption = "备用字段1", datetype = Types.VARCHAR)
	public CField property1; // 备用字段1
	@CFieldinfo(fieldname = "property2", precision = 64, scale = 0, caption = "备用字段2", datetype = Types.VARCHAR)
	public CField property2; // 备用字段2
	@CFieldinfo(fieldname = "property3", precision = 32, scale = 0, caption = "备用字段3", datetype = Types.VARCHAR)
	public CField property3; // 备用字段3
	@CFieldinfo(fieldname = "property4", precision = 32, scale = 0, caption = "备用字段4", datetype = Types.VARCHAR)
	public CField property4; // 备用字段4
	@CFieldinfo(fieldname = "property5", precision = 32, scale = 0, caption = "备用字段5", datetype = Types.VARCHAR)
	public CField property5; // 备用字段5
	@CFieldinfo(fieldname = "usable", notnull = true, precision = 1, scale = 0, caption = "有效", defvalue = "1", datetype = Types.INTEGER)
	public CField usable; // 有效
	@CFieldinfo(fieldname = "stru_id", precision = 10, scale = 0, caption = "工资结构ID", datetype = Types.INTEGER)
	public CField stru_id; // 工资结构ID
	@CFieldinfo(fieldname = "stru_name", precision = 32, scale = 0, caption = "工资结构名", datetype = Types.VARCHAR)
	public CField stru_name; // 工资结构名
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hr_salary_list() throws Exception {
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
