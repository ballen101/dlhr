package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.perm.ctr.CtrHr_employee;
import com.hr.perm.ctr.CtrHr_employee_linked;

import java.sql.Types;

@CEntity(controller = CtrHr_employee.class)
public class Hr_employee extends CJPA {
	@CFieldinfo(fieldname = "er_id", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField er_id; // ID
	@CFieldinfo(fieldname = "er_code", caption = "档案编码", datetype = Types.VARCHAR)
	public CField er_code; // 档案编码
	@CFieldinfo(fieldname = "employee_code", codeid = 54, notnull = true, caption = "工号", datetype = Types.VARCHAR)
	public CField employee_code; // 工号
	@CFieldinfo(fieldname = "idtype", precision = 1, scale = 0, caption = "证件类型", defvalue = "1", datetype = Types.INTEGER)
	public CField idtype; // 证件类型
	@CFieldinfo(fieldname = "id_number", notnull = false, caption = "身份证号", datetype = Types.VARCHAR)
	public CField id_number; // 身份证号
	@CFieldinfo(fieldname = "sign_org", caption = "发证机关", datetype = Types.VARCHAR)
	public CField sign_org; // 发证机关
	@CFieldinfo(fieldname = "sign_date", caption = "签发日期", datetype = Types.TIMESTAMP)
	public CField sign_date; // 签发日期
	@CFieldinfo(fieldname = "expired_date", caption = "到期日期", datetype = Types.TIMESTAMP)
	public CField expired_date; // 到期日期
	@CFieldinfo(fieldname = "card_number", caption = "卡号", datetype = Types.VARCHAR)
	public CField card_number; // 卡号
	@CFieldinfo(fieldname = "employee_name", notnull = true, caption = "姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 姓名
	@CFieldinfo(fieldname = "mnemonic_code", caption = "助记码", datetype = Types.VARCHAR)
	public CField mnemonic_code; // 助记码
	@CFieldinfo(fieldname = "english_name", caption = "英文名", datetype = Types.VARCHAR)
	public CField english_name; // 英文名
	@CFieldinfo(fieldname = "avatar_id1", caption = "身份证头像", datetype = Types.INTEGER)
	public CField avatar_id1; // 身份证头像
	@CFieldinfo(fieldname = "avatar_id2", caption = "拍照头像", datetype = Types.INTEGER)
	public CField avatar_id2; // 拍照头像
	@CFieldinfo(fieldname = "birthday", notnull = true, caption = "出生日期", datetype = Types.TIMESTAMP)
	public CField birthday; // 出生日期
	@CFieldinfo(fieldname = "sex", caption = "性别", datetype = Types.INTEGER)
	public CField sex; // 性别
	@CFieldinfo(fieldname = "bwday", caption = "参加工作时间", datetype = Types.TIMESTAMP)
	public CField bwday; // 参加工作时间
	@CFieldinfo(fieldname = "bwyear", caption = "参加工作年限", datetype = Types.INTEGER)
	public CField bwyear; // 参加工作年限
	@CFieldinfo(fieldname = "hiredday", caption = "入职日期", datetype = Types.TIMESTAMP)
	public CField hiredday; // 入职日期
	@CFieldinfo(fieldname = "ljdate", caption = "离职日期", datetype = Types.TIMESTAMP)
	public CField ljdate; // 离职日期
	@CFieldinfo(fieldname = "degree", caption = "学历", datetype = Types.INTEGER)
	public CField degree; // 学历
	@CFieldinfo(fieldname = "degreetype", precision = 2, scale = 0, caption = "学历类型", datetype = Types.INTEGER)
	public CField degreetype; // 学历类型
	@CFieldinfo(fieldname = "degreecheck", precision = 1, scale = 0, caption = "学历验证", datetype = Types.INTEGER)
	public CField degreecheck; // 学历验证
	@CFieldinfo(fieldname = "married", caption = "婚姻状况", datetype = Types.INTEGER)
	public CField married; // 婚姻状况
	@CFieldinfo(fieldname = "nationality", caption = "国籍", datetype = Types.VARCHAR)
	public CField nationality; // 国籍
	@CFieldinfo(fieldname = "nativeid", precision = 12, scale = 0, caption = "籍贯ID", datetype = Types.VARCHAR)
	public CField nativeid; // 籍贯ID
	@CFieldinfo(fieldname = "nativeplace", caption = "籍贯", datetype = Types.VARCHAR)
	public CField nativeplace; // 籍贯
	@CFieldinfo(fieldname = "address", caption = "现住址", datetype = Types.VARCHAR)
	public CField address; // 现住址
	@CFieldinfo(fieldname = "nation", caption = "民族", datetype = Types.VARCHAR)
	public CField nation; // 民族
	@CFieldinfo(fieldname = "email", caption = "邮箱/微信", datetype = Types.VARCHAR)
	public CField email; // 邮箱/微信
	@CFieldinfo(fieldname = "empstatid", notnull = true, caption = "人员状态", datetype = Types.INTEGER)
	public CField empstatid; // 人员状态
	@CFieldinfo(fieldname = "modify", caption = "修改史", datetype = Types.VARCHAR)
	public CField modify; // 修改史
	@CFieldinfo(fieldname = "usedname", caption = "曾用名", datetype = Types.VARCHAR)
	public CField usedname; // 曾用名
	@CFieldinfo(fieldname = "pldcp", caption = "政治面貌", datetype = Types.INTEGER)
	public CField pldcp; // 政治面貌
	@CFieldinfo(fieldname = "major", caption = "专业", datetype = Types.VARCHAR)
	public CField major; // 专业
	@CFieldinfo(fieldname = "registertype", caption = "户籍类型", datetype = Types.INTEGER)
	public CField registertype; // 户籍类型
	@CFieldinfo(fieldname = "registeraddress", caption = "户籍住址", datetype = Types.VARCHAR)
	public CField registeraddress; // 户籍住址
	@CFieldinfo(fieldname = "health", caption = "健康情况", datetype = Types.VARCHAR)
	public CField health; // 健康情况
	@CFieldinfo(fieldname = "medicalhistory", caption = "过往病史", datetype = Types.VARCHAR)
	public CField medicalhistory; // 过往病史
	@CFieldinfo(fieldname = "bloodtype", caption = "血型", datetype = Types.VARCHAR)
	public CField bloodtype; // 血型
	@CFieldinfo(fieldname = "height", caption = "身高", datetype = Types.VARCHAR)
	public CField height; // 身高
	@CFieldinfo(fieldname = "importway", caption = "引进途径", datetype = Types.INTEGER)
	public CField importway; // 引进途径
	@CFieldinfo(fieldname = "importor", caption = "引进人员", datetype = Types.VARCHAR)
	public CField importor; // 引进人员
	@CFieldinfo(fieldname = "cellphone", caption = "紧急联系电话", datetype = Types.VARCHAR)
	public CField cellphone; // 紧急联系电话
	@CFieldinfo(fieldname = "urgencycontact", caption = "紧急联系人", datetype = Types.VARCHAR)
	public CField urgencycontact; // 紧急联系人
	@CFieldinfo(fieldname = "urmail", caption = "紧急联系邮箱", datetype = Types.VARCHAR)
	public CField urmail; // 紧急联系邮箱
	@CFieldinfo(fieldname = "telphone", caption = "联系电话", datetype = Types.VARCHAR)
	public CField telphone; // 联系电话
	@CFieldinfo(fieldname = "introducer", caption = "介绍人", datetype = Types.VARCHAR)
	public CField introducer; // 介绍人
	@CFieldinfo(fieldname = "guarantor", caption = "担保人", datetype = Types.VARCHAR)
	public CField guarantor; // 担保人
	@CFieldinfo(fieldname = "skill", caption = "工作技能", datetype = Types.VARCHAR)
	public CField skill; // 工作技能
	@CFieldinfo(fieldname = "skillfullanguage", caption = "熟悉语言种类", datetype = Types.VARCHAR)
	public CField skillfullanguage; // 熟悉语言种类
	@CFieldinfo(fieldname = "speciality", caption = "兴趣特长", datetype = Types.VARCHAR)
	public CField speciality; // 兴趣特长
	@CFieldinfo(fieldname = "welfare", caption = "享受福利", datetype = Types.VARCHAR)
	public CField welfare; // 享受福利
	@CFieldinfo(fieldname = "talentstype", caption = "人才类型", datetype = Types.VARCHAR)
	public CField talentstype; // 人才类型
	@CFieldinfo(fieldname = "emnature", caption = "人员性质", datetype = Types.VARCHAR)
	public CField emnature; // 人员性质 脱产 非脱产
	@CFieldinfo(fieldname = "orgid", notnull = true, caption = "部门ID", datetype = Types.INTEGER)
	public CField orgid; // 部门ID
	@CFieldinfo(fieldname = "orgcode", notnull = true, caption = "部门编码", datetype = Types.VARCHAR)
	public CField orgcode; // 部门编码
	@CFieldinfo(fieldname = "orgname", notnull = true, caption = "部门名称", datetype = Types.VARCHAR)
	public CField orgname; // 部门名称
	@CFieldinfo(fieldname = "uorgid", precision = 10, scale = 0, caption = "用人部门ID", datetype = Types.INTEGER)
	public CField uorgid; // 用人部门ID
	@CFieldinfo(fieldname = "uorgcode", precision = 16, scale = 0, caption = "用人部门编码", datetype = Types.VARCHAR)
	public CField uorgcode; // 用人部门编码
	@CFieldinfo(fieldname = "uorgname", precision = 128, scale = 0, caption = "用人部门名称", datetype = Types.VARCHAR)
	public CField uorgname; // 用人部门名称
	@CFieldinfo(fieldname = "uidpath", precision = 256, scale = 0, caption = "用人部门idpath", datetype = Types.VARCHAR)
	public CField uidpath; // 用人部门idpath
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
	@CFieldinfo(fieldname = "iskey", caption = "关键岗位", datetype = Types.INTEGER)
	public CField iskey; // 关键岗位
	@CFieldinfo(fieldname = "hwc_namezq", caption = "职群", datetype = Types.VARCHAR)
	public CField hwc_namezq; // 职群
	@CFieldinfo(fieldname = "hwc_namezz", caption = "职种", datetype = Types.VARCHAR)
	public CField hwc_namezz; // 职种
	@CFieldinfo(fieldname = "usable", notnull = true, caption = "有效", datetype = Types.INTEGER)
	public CField usable; // 有效
	@CFieldinfo(fieldname = "sscurty_addr", caption = "社保购买地", datetype = Types.VARCHAR)
	public CField sscurty_addr; // 社保购买地
	@CFieldinfo(fieldname = "sscurty_startdate", caption = "社起始日期", datetype = Types.TIMESTAMP)
	public CField sscurty_startdate; // 社起始日期
	@CFieldinfo(fieldname = "sscurty_enddate", caption = "社保截止日期", datetype = Types.TIMESTAMP)
	public CField sscurty_enddate; // 社保截止日期
	@CFieldinfo(fieldname = "shoesize", caption = "鞋码", datetype = Types.VARCHAR)
	public CField shoesize; // 鞋码
	@CFieldinfo(fieldname = "pants_code", caption = "裤码", datetype = Types.VARCHAR)
	public CField pants_code; // 裤码
	@CFieldinfo(fieldname = "coat_code", caption = "上衣码", datetype = Types.VARCHAR)
	public CField coat_code; // 上衣码
	@CFieldinfo(fieldname = "needdrom", caption = "入住宿舍", datetype = Types.INTEGER)
	public CField needdrom; // 入住宿舍
	@CFieldinfo(fieldname = "dorm_bed", caption = "宿舍床位", datetype = Types.VARCHAR)
	public CField dorm_bed; // 宿舍床位
	@CFieldinfo(fieldname = "pay_way", caption = "计薪方式", datetype = Types.VARCHAR)
	public CField pay_way; // 计薪方式
	@CFieldinfo(fieldname = "schedtype", caption = "默认班制", datetype = Types.VARCHAR)
	public CField schedtype; // 默认班制
	@CFieldinfo(fieldname = "atdtype", caption = "出勤类别", datetype = Types.VARCHAR)
	public CField atdtype; // 出勤类别
	@CFieldinfo(fieldname = "resigntimes", caption = "月度因私签卡次数限制", defvalue = "3", datetype = Types.INTEGER)
	public CField resigntimes; // 月度因私签卡次数限制
	@CFieldinfo(fieldname = "wfbusyss", caption = "旺季不调休", defvalue = "1", datetype = Types.INTEGER)
	public CField wfbusyss; // 旺季不调休
	@CFieldinfo(fieldname = "noclock", caption = "免考勤打卡", defvalue = "2", datetype = Types.INTEGER)
	public CField noclock; // 免考勤打卡
	@CFieldinfo(fieldname = "promotionday", caption = "转正日期", datetype = Types.TIMESTAMP)
	public CField promotionday; // 转正日期
	@CFieldinfo(fieldname = "entrysourcr", caption = "人员来源", datetype = Types.INTEGER)
	public CField entrysourcr; // 人员来源
	@CFieldinfo(fieldname = "rectcode", caption = "招聘人工号", datetype = Types.VARCHAR)
	public CField rectcode; // 招聘人工号
	@CFieldinfo(fieldname = "rectname", caption = "招聘人", datetype = Types.VARCHAR)
	public CField rectname; // 招聘人
	@CFieldinfo(fieldname = "eovertype", precision = 2, scale = 0, caption = "加班类别", datetype = Types.INTEGER)
	public CField eovertype; // 加班类别
	@CFieldinfo(fieldname = "mlev", caption = "m层级", datetype = Types.INTEGER)
	public CField mlev; // m层级
	@CFieldinfo(fieldname = "atmid", precision = 6, scale = 0, caption = "高技模块ID", datetype = Types.INTEGER)
	public CField atmid; // 高技模块ID
	@CFieldinfo(fieldname = "mdname", precision = 64, scale = 0, caption = "高技模块名称", datetype = Types.VARCHAR)
	public CField mdname; // 高技模块名称
	@CFieldinfo(fieldname = "advtch_subsidy", precision = 10, scale = 2, caption = "津贴标准", datetype = Types.DECIMAL)
	public CField advtch_subsidy; // 津贴标准
	@CFieldinfo(fieldname = "isadvtch", precision = 1, scale = 0, caption = "是否属于高技人才", defvalue = "2", datetype = Types.INTEGER)
	public CField isadvtch; // 是否属于高技人才
	@CFieldinfo(fieldname = "note", caption = "备注", datetype = Types.VARCHAR)
	public CField note; // 备注
	@CFieldinfo(fieldname = "attid", caption = "attid", datetype = Types.INTEGER)
	public CField attid; // attid
	@CFieldinfo(fieldname = "entid", notnull = true, caption = "entid", datetype = Types.INTEGER)
	public CField entid; // entid
	@CFieldinfo(fieldname = "idpath", notnull = true, caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	@CFieldinfo(fieldname = "creator", notnull = true, caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", notnull = true, caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	@CFieldinfo(fieldname = "property1", caption = "备用字段1", datetype = Types.VARCHAR)
	public CField property1; // 备用字段1
	@CFieldinfo(fieldname = "property2", caption = "备用字段2", datetype = Types.VARCHAR)
	public CField property2; // 备用字段2
	@CFieldinfo(fieldname = "property3", caption = "备用字段3", datetype = Types.VARCHAR)
	public CField property3; // 备用字段3
	@CFieldinfo(fieldname = "property4", caption = "备用字段4", datetype = Types.VARCHAR)
	public CField property4; // 备用字段4
	@CFieldinfo(fieldname = "property5", caption = "备用字段5", datetype = Types.VARCHAR)
	public CField property5; // 备用字段5
	@CFieldinfo(fieldname = "insurancestat", caption = "社保状态", datetype = Types.INTEGER)
	public CField insurancestat; // 社保状态
	@CFieldinfo(fieldname = "advisercode", precision = 16, scale = 0, caption = "指导老师工号", datetype = Types.VARCHAR)
	public CField advisercode; // 指导老师工号
	@CFieldinfo(fieldname = "advisername", precision = 32, scale = 0, caption = "指导老师姓名", datetype = Types.VARCHAR)
	public CField advisername; // 指导老师姓名
	@CFieldinfo(fieldname = "adviserphone", precision = 16, scale = 0, caption = "指导老师电话", datetype = Types.VARCHAR)
	public CField adviserphone; // 指导老师电话
	@CFieldinfo(fieldname = "juridical", precision = 128, scale = 0, caption = "法人单位", datetype = Types.VARCHAR)
	public CField juridical; // 法人单位
	@CFieldinfo(fieldname = "transorg", precision = 64, scale = 0, caption = "输送机构", datetype = Types.VARCHAR)
	public CField transorg; // 输送机构
	@CFieldinfo(fieldname = "transextime", precision = 64, scale = 0, caption = "输送期限", datetype = Types.VARCHAR)
	public CField transextime; // 输送期限
	@CFieldinfo(fieldname = "transexbatch", precision = 64, scale = 0, caption = " 输送批次", datetype = Types.VARCHAR)
	public CField transexbatch; // 输送批次
	@CFieldinfo(fieldname = "dispunit", precision = 64, scale = 0, caption = "派遣机构", datetype = Types.VARCHAR)
	public CField dispunit; // 派遣机构
	@CFieldinfo(fieldname = "dispeextime", precision = 64, scale = 0, caption = "派遣期限", datetype = Types.VARCHAR)
	public CField dispeextime; // 派遣期限
	@CFieldinfo(fieldname = "kqdate_start", precision = 19, scale = 0, caption = "考勤开始日期(包含)", datetype = Types.TIMESTAMP)
	public CField kqdate_start; // 考勤开始日期(包含)
	@CFieldinfo(fieldname = "kqdate_end", precision = 19, scale = 0, caption = "考勤截止日期(包含)", datetype = Types.TIMESTAMP)
	public CField kqdate_end; // 考勤截止日期(包含)
	@CFieldinfo(fieldname = "hg_remark", precision = 64, scale = 0, caption = "备注职位", datetype = Types.VARCHAR)
	public CField hg_remark; // 备注职位
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hr_employee() throws Exception {
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
