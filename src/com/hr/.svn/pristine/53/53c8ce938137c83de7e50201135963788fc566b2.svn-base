package com.hr.recruit.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.card.co.COHr_ykt_card_clean;
import com.hr.perm.entity.Hr_empconbatch_line;
import com.hr.recruit.co.COHr_recruit_quachk;

import java.sql.Types;

@CEntity(caption="资格对比",controller = COHr_recruit_quachk.class)
public class Hr_recruit_quachk extends CJPA {
	@CFieldinfo(fieldname="recruit_quachk_id",iskey=true,notnull=true,precision=20,scale=0,caption="ID",datetype=Types.INTEGER)
	public CField recruit_quachk_id;  //ID
	@CFieldinfo(fieldname="recruit_quachk_code",codeid = 122,precision=21,scale=0,caption="资格对比编码",datetype=Types.VARCHAR)
	public CField recruit_quachk_code;  //资格对比编码
	@CFieldinfo(fieldname="id_number",precision=26,scale=0,caption="身份证号",datetype=Types.VARCHAR)
	public CField id_number;  //身份证号
	@CFieldinfo(fieldname="sign_org",notnull=true,precision=85,scale=0,caption="发证机关",datetype=Types.VARCHAR)
	public CField sign_org;  //发证机关
	@CFieldinfo(fieldname="sign_date",notnull=true,precision=19,scale=0,caption="签发日期",datetype=Types.TIMESTAMP)
	public CField sign_date;  //签发日期
	@CFieldinfo(fieldname="expired_date",notnull=true,precision=19,scale=0,caption="到期日期",datetype=Types.TIMESTAMP)
	public CField expired_date;  //到期日期
	@CFieldinfo(fieldname="card_number",precision=26,scale=0,caption="卡号",datetype=Types.VARCHAR)
	public CField card_number;  //卡号
	@CFieldinfo(fieldname="employee_name",notnull=true,precision=64,scale=0,caption="姓名",datetype=Types.VARCHAR)
	public CField employee_name;  //姓名
	@CFieldinfo(fieldname="english_name",precision=128,scale=0,caption="英文名",datetype=Types.VARCHAR)
	public CField english_name;  //英文名
	@CFieldinfo(fieldname="birthday",notnull=true,precision=19,scale=0,caption="出生日期",datetype=Types.TIMESTAMP)
	public CField birthday;  //出生日期
	@CFieldinfo(fieldname="sex",precision=1,scale=0,caption="性别",datetype=Types.INTEGER)
	public CField sex;  //性别
	@CFieldinfo(fieldname="address",precision=256,scale=0,caption="现住址",datetype=Types.VARCHAR)
	public CField address;  //现住址
	@CFieldinfo(fieldname="nation",precision=42,scale=0,caption="民族",datetype=Types.VARCHAR)
	public CField nation;  //民族
	@CFieldinfo(fieldname="compar_result",notnull=true,precision=21,scale=0,caption="验证结果",datetype=Types.VARCHAR)
	public CField compar_result;  //验证结果
	@CFieldinfo(fieldname="reason",precision=682,scale=0,caption="原因",datetype=Types.VARCHAR)
	public CField reason;  //原因
	@CFieldinfo(fieldname="special_item",precision=682,scale=0,caption="特殊项目",datetype=Types.VARCHAR)
	public CField special_item;  //特殊项目
	@CFieldinfo(fieldname="situation",precision=682,scale=0,caption="特殊项目",datetype=Types.VARCHAR)
	public CField situation;  //特殊项目
	@CFieldinfo(fieldname="remark",precision=682,scale=0,caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	@CFieldinfo(fieldname="wfid",precision=20,scale=0,caption="wfid",datetype=Types.INTEGER)
	public CField wfid;  //wfid
	@CFieldinfo(fieldname="attid",precision=20,scale=0,caption="attid",datetype=Types.INTEGER)
	public CField attid;  //attid
	@CFieldinfo(fieldname="stat",notnull=true,precision=2,scale=0,caption="流程状态",datetype=Types.INTEGER)
	public CField stat;  //流程状态
	@CFieldinfo(fieldname="idpath",notnull=true,precision=341,scale=0,caption="idpath",datetype=Types.VARCHAR)
	public CField idpath;  //idpath
	@CFieldinfo(fieldname="entid",notnull=true,precision=5,scale=0,caption="entid",datetype=Types.INTEGER)
	public CField entid;  //entid
	@CFieldinfo(fieldname="creator",notnull=true,precision=32,scale=0,caption="创建人",datetype=Types.VARCHAR)
	public CField creator;  //创建人
	@CFieldinfo(fieldname="createtime",notnull=true,precision=19,scale=0,caption="创建时间",datetype=Types.TIMESTAMP)
	public CField createtime;  //创建时间
	@CFieldinfo(fieldname="updator",precision=32,scale=0,caption="更新人",datetype=Types.VARCHAR)
	public CField updator;  //更新人
	@CFieldinfo(fieldname="updatetime",precision=19,scale=0,caption="更新时间",datetype=Types.TIMESTAMP)
	public CField updatetime;  //更新时间
	@CFieldinfo(fieldname="attribute1",precision=42,scale=0,caption="备用字段1",datetype=Types.VARCHAR)
	public CField attribute1;  //备用字段1
	@CFieldinfo(fieldname="attribute2",precision=42,scale=0,caption="备用字段2",datetype=Types.VARCHAR)
	public CField attribute2;  //备用字段2
	@CFieldinfo(fieldname="attribute3",precision=42,scale=0,caption="备用字段3",datetype=Types.VARCHAR)
	public CField attribute3;  //备用字段3
	@CFieldinfo(fieldname="attribute4",precision=42,scale=0,caption="备用字段4",datetype=Types.VARCHAR)
	public CField attribute4;  //备用字段4
	@CFieldinfo(fieldname="attribute5",precision=42,scale=0,caption="备用字段5",datetype=Types.VARCHAR)
	public CField attribute5;  //备用字段5
	@CFieldinfo(fieldname="compar_result1",precision=21,scale=0,caption="验证结果1",datetype=Types.VARCHAR)
	public CField compar_result1;  //验证结果1
	@CFieldinfo(fieldname="compar_result2",precision=21,scale=0,caption="验证结果2",datetype=Types.VARCHAR)
	public CField compar_result2;  //验证结果2
	@CFieldinfo(fieldname="compar_result3",precision=21,scale=0,caption="验证结果3",datetype=Types.VARCHAR)
	public CField compar_result3;  //验证结果3
	@CFieldinfo(fieldname="compar_result4",precision=21,scale=0,caption="验证结果4",datetype=Types.VARCHAR)
	public CField compar_result4;  //验证结果4
	@CFieldinfo(fieldname="compar_result5",precision=21,scale=0,caption="验证结果5",datetype=Types.VARCHAR)
	public CField compar_result5;  //验证结果5
	@CFieldinfo(fieldname="compar_result6",precision=21,scale=0,caption="验证结果6",datetype=Types.VARCHAR)
	public CField compar_result6;  //验证结果6
	@CFieldinfo(fieldname="compar_result7",precision=21,scale=0,caption="验证结果7",datetype=Types.VARCHAR)
	public CField compar_result7;  //验证结果7
	@CFieldinfo(fieldname="compar_result8",precision=21,scale=0,caption="验证结果8",datetype=Types.VARCHAR)
	public CField compar_result8;  //验证结果8
	@CFieldinfo(fieldname="compar_result9",precision=21,scale=0,caption="验证结果9",datetype=Types.VARCHAR)
	public CField compar_result9;  //验证结果9
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;
	@CLinkFieldInfo(jpaclass = Hr_recruit_quachkline.class, linkFields = { @LinkFieldItem(lfield = "recruit_quachk_id", mfield = "recruit_quachk_id") })
	public CJPALineData<Hr_recruit_quachkline> hr_recruit_quachklines;


	public Hr_recruit_quachk() throws Exception {
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

