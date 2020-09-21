package com.hr.base.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hr_entryold extends CJPA {
	@CFieldinfo(fieldname="ent_id",iskey=true,notnull=true,caption="入职ID",datetype=Types.INTEGER)
	public CField ent_id;  //入职ID
	@CFieldinfo(fieldname="ent_code",codeid=54,notnull=true,caption="入职代码",datetype=Types.VARCHAR)
	public CField ent_code;  //入职代码
	@CFieldinfo(fieldname="employee_code",notnull=true,caption="工号",datetype=Types.VARCHAR)
	public CField employee_code;  //工号
	@CFieldinfo(fieldname="ID_number",notnull=true,caption="身份证号",datetype=Types.VARCHAR)
	public CField ID_number;  //身份证号
	@CFieldinfo(fieldname="card_number",notnull=true,caption="卡号",datetype=Types.VARCHAR)
	public CField card_number;  //卡号
	@CFieldinfo(fieldname="employee_name",notnull=true,caption="姓名",datetype=Types.VARCHAR)
	public CField employee_name;  //姓名
	@CFieldinfo(fieldname="mnemonic_code",caption="助记码",datetype=Types.VARCHAR)
	public CField mnemonic_code;  //助记码
	@CFieldinfo(fieldname="english_name",caption="英文名",datetype=Types.VARCHAR)
	public CField english_name;  //英文名
	@CFieldinfo(fieldname="avatar_id",caption="头像ID",datetype=Types.INTEGER)
	public CField avatar_id;  //头像ID
	@CFieldinfo(fieldname="birthday",notnull=true,caption="出生日期",datetype=Types.TIMESTAMP)
	public CField birthday;  //出生日期
	@CFieldinfo(fieldname="paytype",caption="记薪方式",datetype=Types.INTEGER)
	public CField paytype;  //记薪方式
	@CFieldinfo(fieldname="sex",caption="性别",datetype=Types.INTEGER)
	public CField sex;  //性别
	@CFieldinfo(fieldname="isoffjob",caption="脱产",datetype=Types.INTEGER)
	public CField isoffjob;  //脱产
	@CFieldinfo(fieldname="swipecard",caption="刷卡",datetype=Types.INTEGER)
	public CField swipecard;  //刷卡
	@CFieldinfo(fieldname="probation",notnull=true,caption="试用期",datetype=Types.INTEGER)
	public CField probation;  //试用期
	@CFieldinfo(fieldname="hiredday",notnull=true,caption="聘用日期",datetype=Types.TIMESTAMP)
	public CField hiredday;  //聘用日期
	@CFieldinfo(fieldname="promotionday",notnull=true,caption="转正日期",datetype=Types.TIMESTAMP)
	public CField promotionday;  //转正日期
	@CFieldinfo(fieldname="degree",notnull=true,caption="学历",datetype=Types.INTEGER)
	public CField degree;  //学历
	@CFieldinfo(fieldname="married",notnull=true,caption="婚姻状况",datetype=Types.INTEGER)
	public CField married;  //婚姻状况
	@CFieldinfo(fieldname="nativeplace",notnull=true,caption="籍贯",datetype=Types.VARCHAR)
	public CField nativeplace;  //籍贯
	@CFieldinfo(fieldname="address",notnull=true,caption="现住址",datetype=Types.VARCHAR)
	public CField address;  //现住址
	@CFieldinfo(fieldname="nation",notnull=true,caption="民族",datetype=Types.INTEGER)
	public CField nation;  //民族
	@CFieldinfo(fieldname="Email",notnull=true,caption="邮箱/微信",datetype=Types.VARCHAR)
	public CField Email;  //邮箱/微信
	@CFieldinfo(fieldname="shifts",notnull=true,caption="班制",datetype=Types.INTEGER)
	public CField shifts;  //班制
	@CFieldinfo(fieldname="empstat",notnull=true,caption="人员状态",datetype=Types.INTEGER)
	public CField empstat;  //人员状态
	@CFieldinfo(fieldname="modify",notnull=true,caption="修改史",datetype=Types.VARCHAR)
	public CField modify;  //修改史
	@CFieldinfo(fieldname="ID_signtime",notnull=true,caption="身份证签发日期",datetype=Types.TIMESTAMP)
	public CField ID_signtime;  //身份证签发日期
	@CFieldinfo(fieldname="ID_invalidtime",notnull=true,caption="身份证失效日期",datetype=Types.TIMESTAMP)
	public CField ID_invalidtime;  //身份证失效日期
	@CFieldinfo(fieldname="ID_signplace",notnull=true,caption="身份证签发地",datetype=Types.VARCHAR)
	public CField ID_signplace;  //身份证签发地
	@CFieldinfo(fieldname="IDtype",notnull=true,caption="身份证类型",datetype=Types.INTEGER)
	public CField IDtype;  //身份证类型
	@CFieldinfo(fieldname="allot_company",notnull=true,caption="分配公司",datetype=Types.VARCHAR)
	public CField allot_company;  //分配公司
	@CFieldinfo(fieldname="allotday",caption="分配日期",datetype=Types.TIMESTAMP)
	public CField allotday;  //分配日期
	@CFieldinfo(fieldname="stay",notnull=true,caption="是否住宿",datetype=Types.INTEGER)
	public CField stay;  //是否住宿
	@CFieldinfo(fieldname="dormtype",caption="宿舍类别",datetype=Types.INTEGER)
	public CField dormtype;  //宿舍类别
	@CFieldinfo(fieldname="dormarea",caption="宿舍区域",datetype=Types.VARCHAR)
	public CField dormarea;  //宿舍区域
	@CFieldinfo(fieldname="room_bed_number",caption="房号床位",datetype=Types.VARCHAR)
	public CField room_bed_number;  //房号床位
	@CFieldinfo(fieldname="dormpaytype",caption="宿舍扣费类型",datetype=Types.INTEGER)
	public CField dormpaytype;  //宿舍扣费类型
	@CFieldinfo(fieldname="eatpermission",notnull=true,caption="用餐权限",datetype=Types.INTEGER)
	public CField eatpermission;  //用餐权限
	@CFieldinfo(fieldname="eatpaytime",notnull=true,caption="用餐扣费类型",datetype=Types.INTEGER)
	public CField eatpaytime;  //用餐扣费类型
	@CFieldinfo(fieldname="accesscontrol",notnull=true,caption="门禁权限",datetype=Types.INTEGER)
	public CField accesscontrol;  //门禁权限
	@CFieldinfo(fieldname="attendancearea",notnull=true,caption="考勤区域",datetype=Types.VARCHAR)
	public CField attendancearea;  //考勤区域
	@CFieldinfo(fieldname="checkwork",notnull=true,caption="考勤识别",datetype=Types.INTEGER)
	public CField checkwork;  //考勤识别
	@CFieldinfo(fieldname="usedname",caption="曾用名",datetype=Types.VARCHAR)
	public CField usedname;  //曾用名
	@CFieldinfo(fieldname="pldcp",notnull=true,caption="政治面貌",datetype=Types.INTEGER)
	public CField pldcp;  //政治面貌
	@CFieldinfo(fieldname="major",notnull=true,caption="专业",datetype=Types.VARCHAR)
	public CField major;  //专业
	@CFieldinfo(fieldname="registertype",notnull=true,caption="户籍类型",datetype=Types.INTEGER)
	public CField registertype;  //户籍类型
	@CFieldinfo(fieldname="registeraddress",notnull=true,caption="户籍住址",datetype=Types.VARCHAR)
	public CField registeraddress;  //户籍住址
	@CFieldinfo(fieldname="usedsp_id",caption="曾用职位ID",datetype=Types.INTEGER)
	public CField usedsp_id;  //曾用职位ID
	@CFieldinfo(fieldname="usedspcode",notnull=true,caption="曾用职位编码",datetype=Types.VARCHAR)
	public CField usedspcode;  //曾用职位编码
	@CFieldinfo(fieldname="usedsp_name",caption="曾用职位名称",datetype=Types.VARCHAR)
	public CField usedsp_name;  //曾用职位名称
	@CFieldinfo(fieldname="health",notnull=true,caption="健康情况",datetype=Types.INTEGER)
	public CField health;  //健康情况
	@CFieldinfo(fieldname="medicalhistory",notnull=true,caption="过往病史",datetype=Types.VARCHAR)
	public CField medicalhistory;  //过往病史
	@CFieldinfo(fieldname="bloodtype",notnull=true,caption="血型",datetype=Types.VARCHAR)
	public CField bloodtype;  //血型
	@CFieldinfo(fieldname="height",notnull=true,caption="身高",datetype=Types.VARCHAR)
	public CField height;  //身高
	@CFieldinfo(fieldname="attendancetype",notnull=true,caption="出勤类别",datetype=Types.INTEGER)
	public CField attendancetype;  //出勤类别
	@CFieldinfo(fieldname="overtimetype",notnull=true,caption="加班类别",datetype=Types.INTEGER)
	public CField overtimetype;  //加班类别
	@CFieldinfo(fieldname="importway",notnull=true,caption="引进途径",datetype=Types.INTEGER)
	public CField importway;  //引进途径
	@CFieldinfo(fieldname="importor",caption="引进人员",datetype=Types.VARCHAR)
	public CField importor;  //引进人员
	@CFieldinfo(fieldname="cellphone",notnull=true,caption="联系电话",datetype=Types.VARCHAR)
	public CField cellphone;  //联系电话
	@CFieldinfo(fieldname="urgencycontact",notnull=true,caption="紧急联系人",datetype=Types.VARCHAR)
	public CField urgencycontact;  //紧急联系人
	@CFieldinfo(fieldname="telphone",notnull=true,caption="电话",datetype=Types.VARCHAR)
	public CField telphone;  //电话
	@CFieldinfo(fieldname="transorg_id",notnull=true,caption="输送机构ID",datetype=Types.INTEGER)
	public CField transorg_id;  //输送机构ID
	@CFieldinfo(fieldname="transorg_code",notnull=true,caption="输送机构编码",datetype=Types.VARCHAR)
	public CField transorg_code;  //输送机构编码
	@CFieldinfo(fieldname="transorg_name",notnull=true,caption="输送机构名称",datetype=Types.VARCHAR)
	public CField transorg_name;  //输送机构名称
	@CFieldinfo(fieldname="relevance",caption="关联关系",datetype=Types.INTEGER)
	public CField relevance;  //关联关系
	@CFieldinfo(fieldname="introducer",caption="介绍人",datetype=Types.VARCHAR)
	public CField introducer;  //介绍人
	@CFieldinfo(fieldname="guarantor",caption="担保人",datetype=Types.VARCHAR)
	public CField guarantor;  //担保人
	@CFieldinfo(fieldname="skill",caption="工作技能",datetype=Types.VARCHAR)
	public CField skill;  //工作技能
	@CFieldinfo(fieldname="skillfullanguage",caption="熟悉语言种类",datetype=Types.VARCHAR)
	public CField skillfullanguage;  //熟悉语言种类
	@CFieldinfo(fieldname="speciality",caption="兴趣特长",datetype=Types.VARCHAR)
	public CField speciality;  //兴趣特长
	@CFieldinfo(fieldname="welfare",caption="享受福利",datetype=Types.VARCHAR)
	public CField welfare;  //享受福利
	@CFieldinfo(fieldname="talentstype",notnull=true,caption="人才类型",datetype=Types.INTEGER)
	public CField talentstype;  //人才类型
	@CFieldinfo(fieldname="superior",notnull=true,caption="上级领导",datetype=Types.VARCHAR)
	public CField superior;  //上级领导
	@CFieldinfo(fieldname="orgid",notnull=true,caption="部门ID",datetype=Types.INTEGER)
	public CField orgid;  //部门ID
	@CFieldinfo(fieldname="orgcode",notnull=true,caption="部门编码",datetype=Types.VARCHAR)
	public CField orgcode;  //部门编码
	@CFieldinfo(fieldname="orgname",notnull=true,caption="部门名称",datetype=Types.VARCHAR)
	public CField orgname;  //部门名称
	@CFieldinfo(fieldname="lv_id",notnull=true,caption="职级ID",datetype=Types.INTEGER)
	public CField lv_id;  //职级ID
	@CFieldinfo(fieldname="lv_num",notnull=true,caption="职级",datetype=Types.DECIMAL)
	public CField lv_num;  //职级
	@CFieldinfo(fieldname="hg_id",notnull=true,caption="职等ID",datetype=Types.INTEGER)
	public CField hg_id;  //职等ID
	@CFieldinfo(fieldname="hg_code",notnull=true,caption="职等编码",datetype=Types.VARCHAR)
	public CField hg_code;  //职等编码
	@CFieldinfo(fieldname="hg_name",caption="职等名称",datetype=Types.VARCHAR)
	public CField hg_name;  //职等名称
	@CFieldinfo(fieldname="hwc_idzl",notnull=true,caption="职类ID",datetype=Types.INTEGER)
	public CField hwc_idzl;  //职类ID
	@CFieldinfo(fieldname="hw_codezl",notnull=true,caption="职类代码",datetype=Types.VARCHAR)
	public CField hw_codezl;  //职类代码
	@CFieldinfo(fieldname="hwc_namezl",caption="职类名称",datetype=Types.VARCHAR)
	public CField hwc_namezl;  //职类名称
	@CFieldinfo(fieldname="hwc_idzq",notnull=true,caption="职群ID",datetype=Types.INTEGER)
	public CField hwc_idzq;  //职群ID
	@CFieldinfo(fieldname="hw_codezq",notnull=true,caption="职群代码",datetype=Types.VARCHAR)
	public CField hw_codezq;  //职群代码
	@CFieldinfo(fieldname="hwc_namezq",caption="职群名称",datetype=Types.VARCHAR)
	public CField hwc_namezq;  //职群名称
	@CFieldinfo(fieldname="hwc_idzz",notnull=true,caption="职种ID",datetype=Types.INTEGER)
	public CField hwc_idzz;  //职种ID
	@CFieldinfo(fieldname="hw_codezz",notnull=true,caption="职种代码",datetype=Types.VARCHAR)
	public CField hw_codezz;  //职种代码
	@CFieldinfo(fieldname="hwc_namezz",caption="职种名称",datetype=Types.VARCHAR)
	public CField hwc_namezz;  //职种名称
	@CFieldinfo(fieldname="ospid",notnull=true,caption="职位ID",datetype=Types.INTEGER)
	public CField ospid;  //职位ID
	@CFieldinfo(fieldname="ospcode",notnull=true,caption="职位编码",datetype=Types.VARCHAR)
	public CField ospcode;  //职位编码
	@CFieldinfo(fieldname="sp_name",caption="职位名称",datetype=Types.VARCHAR)
	public CField sp_name;  //职位名称
	@CFieldinfo(fieldname="sp_level",notnull=true,caption="职位序列",datetype=Types.VARCHAR)
	public CField sp_level;  //职位序列
	@CFieldinfo(fieldname="usable",caption="有效状态",datetype=Types.INTEGER)
	public CField usable;  //有效状态
	@CFieldinfo(fieldname="remark",caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	@CFieldinfo(fieldname="stat",notnull=true,caption="流程状态",datetype=Types.INTEGER)
	public CField stat;  //流程状态
	@CFieldinfo(fieldname="idpath",notnull=true,caption="idpath",datetype=Types.VARCHAR)
	public CField idpath;  //idpath
	@CFieldinfo(fieldname="creator",caption="创建人",datetype=Types.VARCHAR)
	public CField creator;  //创建人
	@CFieldinfo(fieldname="createtime",caption="创建时间",datetype=Types.TIMESTAMP)
	public CField createtime;  //创建时间
	@CFieldinfo(fieldname="updator",caption="更新人",datetype=Types.VARCHAR)
	public CField updator;  //更新人
	@CFieldinfo(fieldname="updatetime",caption="更新时间",datetype=Types.TIMESTAMP)
	public CField updatetime;  //更新时间
	@CFieldinfo(fieldname="attribute1",caption="备用字段1",datetype=Types.VARCHAR)
	public CField attribute1;  //备用字段1
	@CFieldinfo(fieldname="attribute2",caption="备用字段2",datetype=Types.VARCHAR)
	public CField attribute2;  //备用字段2
	@CFieldinfo(fieldname="attribute3",caption="备用字段3",datetype=Types.VARCHAR)
	public CField attribute3;  //备用字段3
	@CFieldinfo(fieldname="attribute4",caption="备用字段4",datetype=Types.VARCHAR)
	public CField attribute4;  //备用字段4
	@CFieldinfo(fieldname="attribute5",caption="备用字段5",datetype=Types.VARCHAR)
	public CField attribute5;  //备用字段5
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hr_entryold() throws Exception {
	}

	@Override
	public boolean InitObject() {//类初始化调用的方法
		super.InitObject();
		return true;
	} 

	@Override
	public boolean FinalObject() { //类释放前调用的方法
		super.FinalObject(); 
		return true; 
	} 
} 
