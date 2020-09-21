package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hr_train_regex extends CJPA {
	@CFieldinfo(fieldname = "treg_id", notnull = true, caption = "treg_id", datetype = Types.INTEGER)
	public CField treg_id; // treg_id
	@CFieldinfo(fieldname = "treg_code", notnull = true, caption = "treg_code", datetype = Types.VARCHAR)
	public CField treg_code; // treg_code
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "er_id", datetype = Types.INTEGER)
	public CField er_id; // er_id
	@CFieldinfo(fieldname = "employee_code", notnull = true, caption = "employee_code", datetype = Types.VARCHAR)
	public CField employee_code; // employee_code
	@CFieldinfo(fieldname = "regtype", notnull = true, caption = "regtype", datetype = Types.INTEGER)
	public CField regtype; // regtype
	@CFieldinfo(fieldname = "regdate", caption = "登记日期", datetype = Types.TIMESTAMP)
	public CField regdate; // 登记日期
	@CFieldinfo(fieldname = "nationality", caption = "国籍", datetype = Types.VARCHAR)
	public CField nationality; // 国籍
	@CFieldinfo(fieldname = "bwday", caption = "参加工作时间", datetype = Types.TIMESTAMP)
	public CField bwday; // 参加工作时间
	@CFieldinfo(fieldname = "enddatetrain", caption = "enddatetrain", datetype = Types.TIMESTAMP)
	public CField enddatetrain; // enddatetrain
	@CFieldinfo(fieldname = "enddatetry", caption = "enddatetry", datetype = Types.TIMESTAMP)
	public CField enddatetry; // enddatetry
	@CFieldinfo(fieldname = "jxdatetry", caption = "jxdatetry", datetype = Types.TIMESTAMP)
	public CField jxdatetry; // jxdatetry
	@CFieldinfo(fieldname = "isfrainflish", caption = "isfrainflish", datetype = Types.INTEGER)
	public CField isfrainflish; // isfrainflish
	@CFieldinfo(fieldname = "norgid", caption = "norgid", datetype = Types.INTEGER)
	public CField norgid; // norgid
	@CFieldinfo(fieldname = "norgname", caption = "norgname", datetype = Types.VARCHAR)
	public CField norgname; // norgname
	@CFieldinfo(fieldname = "nospid", caption = "nospid", datetype = Types.INTEGER)
	public CField nospid; // nospid
	@CFieldinfo(fieldname = "nsp_name", caption = "nsp_name", datetype = Types.VARCHAR)
	public CField nsp_name; // nsp_name
	@CFieldinfo(fieldname = "remark", caption = "remark", datetype = Types.VARCHAR)
	public CField remark; // remark
	@CFieldinfo(fieldname = "wfid", caption = "wfid", datetype = Types.INTEGER)
	public CField wfid; // wfid
	@CFieldinfo(fieldname = "stat", notnull = true, caption = "stat", datetype = Types.INTEGER)
	public CField stat; // stat
	@CFieldinfo(fieldname = "idpath", notnull = true, caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	@CFieldinfo(fieldname = "creator", notnull = true, caption = "creator", datetype = Types.VARCHAR)
	public CField creator; // creator
	@CFieldinfo(fieldname = "createtime", notnull = true, caption = "createtime", datetype = Types.TIMESTAMP)
	public CField createtime; // createtime
	@CFieldinfo(fieldname = "updator", caption = "updator", datetype = Types.VARCHAR)
	public CField updator; // updator
	@CFieldinfo(fieldname = "updatetime", caption = "updatetime", datetype = Types.TIMESTAMP)
	public CField updatetime; // updatetime
	@CFieldinfo(fieldname = "attribute1", caption = "attribute1", datetype = Types.VARCHAR)
	public CField attribute1; // attribute1
	@CFieldinfo(fieldname = "attribute2", caption = "attribute2", datetype = Types.VARCHAR)
	public CField attribute2; // attribute2
	@CFieldinfo(fieldname = "attribute3", caption = "attribute3", datetype = Types.VARCHAR)
	public CField attribute3; // attribute3
	@CFieldinfo(fieldname = "attribute4", caption = "attribute4", datetype = Types.VARCHAR)
	public CField attribute4; // attribute4
	@CFieldinfo(fieldname = "attribute5", caption = "attribute5", datetype = Types.VARCHAR)
	public CField attribute5; // attribute5
	@CFieldinfo(fieldname = "id_number", notnull = true, caption = "id_number", datetype = Types.VARCHAR)
	public CField id_number; // id_number
	@CFieldinfo(fieldname = "card_number", caption = "card_number", datetype = Types.VARCHAR)
	public CField card_number; // card_number
	@CFieldinfo(fieldname = "employee_name", notnull = true, caption = "employee_name", datetype = Types.VARCHAR)
	public CField employee_name; // employee_name
	@CFieldinfo(fieldname = "mnemonic_code", caption = "mnemonic_code", datetype = Types.VARCHAR)
	public CField mnemonic_code; // mnemonic_code
	@CFieldinfo(fieldname = "english_name", caption = "english_name", datetype = Types.VARCHAR)
	public CField english_name; // english_name
	@CFieldinfo(fieldname = "avatar_id1", caption = "身份证头像", datetype = Types.INTEGER)
	public CField avatar_id1; // 身份证头像
	@CFieldinfo(fieldname = "avatar_id2", caption = "拍照头像", datetype = Types.INTEGER)
	public CField avatar_id2; // 拍照头像
	@CFieldinfo(fieldname = "birthday", notnull = true, caption = "birthday", datetype = Types.TIMESTAMP)
	public CField birthday; // birthday
	@CFieldinfo(fieldname = "sex", caption = "sex", datetype = Types.INTEGER)
	public CField sex; // sex
	@CFieldinfo(fieldname = "hiredday", caption = "hiredday", datetype = Types.TIMESTAMP)
	public CField hiredday; // hiredday
	@CFieldinfo(fieldname = "degree", caption = "degree", datetype = Types.INTEGER)
	public CField degree; // degree
	@CFieldinfo(fieldname = "married", caption = "married", datetype = Types.INTEGER)
	public CField married; // married
	@CFieldinfo(fieldname = "nativeplace", caption = "nativeplace", datetype = Types.VARCHAR)
	public CField nativeplace; // nativeplace
	@CFieldinfo(fieldname = "address", caption = "address", datetype = Types.VARCHAR)
	public CField address; // address
	@CFieldinfo(fieldname = "nation", caption = "nation", datetype = Types.VARCHAR)
	public CField nation; // nation
	@CFieldinfo(fieldname = "email", caption = "email", datetype = Types.VARCHAR)
	public CField email; // email
	@CFieldinfo(fieldname = "empstatid", notnull = true, caption = "empstatid", datetype = Types.INTEGER)
	public CField empstatid; // empstatid
	@CFieldinfo(fieldname = "modify", caption = "modify", datetype = Types.VARCHAR)
	public CField modify; // modify
	@CFieldinfo(fieldname = "usedname", caption = "usedname", datetype = Types.VARCHAR)
	public CField usedname; // usedname
	@CFieldinfo(fieldname = "pldcp", caption = "pldcp", datetype = Types.INTEGER)
	public CField pldcp; // pldcp
	@CFieldinfo(fieldname = "major", caption = "major", datetype = Types.VARCHAR)
	public CField major; // major
	@CFieldinfo(fieldname = "registertype", caption = "registertype", datetype = Types.INTEGER)
	public CField registertype; // registertype
	@CFieldinfo(fieldname = "registeraddress", caption = "registeraddress", datetype = Types.VARCHAR)
	public CField registeraddress; // registeraddress
	@CFieldinfo(fieldname = "health", caption = "health", datetype = Types.VARCHAR)
	public CField health; // health
	@CFieldinfo(fieldname = "medicalhistory", caption = "medicalhistory", datetype = Types.VARCHAR)
	public CField medicalhistory; // medicalhistory
	@CFieldinfo(fieldname = "bloodtype", caption = "bloodtype", datetype = Types.VARCHAR)
	public CField bloodtype; // bloodtype
	@CFieldinfo(fieldname = "height", caption = "height", datetype = Types.VARCHAR)
	public CField height; // height
	@CFieldinfo(fieldname = "importway", caption = "importway", datetype = Types.INTEGER)
	public CField importway; // importway
	@CFieldinfo(fieldname = "importor", caption = "importor", datetype = Types.VARCHAR)
	public CField importor; // importor
	@CFieldinfo(fieldname = "cellphone", caption = "cellphone", datetype = Types.VARCHAR)
	public CField cellphone; // cellphone
	@CFieldinfo(fieldname = "urgencycontact", caption = "urgencycontact", datetype = Types.VARCHAR)
	public CField urgencycontact; // urgencycontact
	@CFieldinfo(fieldname = "telphone", caption = "telphone", datetype = Types.VARCHAR)
	public CField telphone; // telphone
	@CFieldinfo(fieldname = "introducer", caption = "introducer", datetype = Types.VARCHAR)
	public CField introducer; // introducer
	@CFieldinfo(fieldname = "guarantor", caption = "guarantor", datetype = Types.VARCHAR)
	public CField guarantor; // guarantor
	@CFieldinfo(fieldname = "skill", caption = "skill", datetype = Types.VARCHAR)
	public CField skill; // skill
	@CFieldinfo(fieldname = "skillfullanguage", caption = "skillfullanguage", datetype = Types.VARCHAR)
	public CField skillfullanguage; // skillfullanguage
	@CFieldinfo(fieldname = "speciality", caption = "speciality", datetype = Types.VARCHAR)
	public CField speciality; // speciality
	@CFieldinfo(fieldname = "welfare", caption = "welfare", datetype = Types.VARCHAR)
	public CField welfare; // welfare
	@CFieldinfo(fieldname = "talentstype", caption = "talentstype", datetype = Types.INTEGER)
	public CField talentstype; // talentstype
	@CFieldinfo(fieldname = "orgid", notnull = true, caption = "orgid", datetype = Types.INTEGER)
	public CField orgid; // orgid
	@CFieldinfo(fieldname = "orgcode", notnull = true, caption = "orgcode", datetype = Types.VARCHAR)
	public CField orgcode; // orgcode
	@CFieldinfo(fieldname = "orgname", notnull = true, caption = "orgname", datetype = Types.VARCHAR)
	public CField orgname; // orgname
	@CFieldinfo(fieldname = "lv_id", caption = "lv_id", datetype = Types.INTEGER)
	public CField lv_id; // lv_id
	@CFieldinfo(fieldname = "lv_num", caption = "lv_num", datetype = Types.DECIMAL)
	public CField lv_num; // lv_num
	@CFieldinfo(fieldname = "hg_id", caption = "hg_id", datetype = Types.INTEGER)
	public CField hg_id; // hg_id
	@CFieldinfo(fieldname = "hg_code", caption = "hg_code", datetype = Types.VARCHAR)
	public CField hg_code; // hg_code
	@CFieldinfo(fieldname = "hg_name", caption = "hg_name", datetype = Types.VARCHAR)
	public CField hg_name; // hg_name
	@CFieldinfo(fieldname = "ospid", notnull = true, caption = "ospid", datetype = Types.INTEGER)
	public CField ospid; // ospid
	@CFieldinfo(fieldname = "ospcode", caption = "ospcode", datetype = Types.VARCHAR)
	public CField ospcode; // ospcode
	@CFieldinfo(fieldname = "sp_name", caption = "sp_name", datetype = Types.VARCHAR)
	public CField sp_name; // sp_name
	@CFieldinfo(fieldname = "iskey", caption = "关键岗位", datetype = Types.INTEGER)
	public CField iskey; // 关键岗位
	@CFieldinfo(fieldname = "hwc_namezq", caption = "职群", datetype = Types.VARCHAR)
	public CField hwc_namezq; // 职群
	@CFieldinfo(fieldname = "hwc_namezz", caption = "职种", datetype = Types.VARCHAR)
	public CField hwc_namezz; // 职种
	@CFieldinfo(fieldname = "usable", notnull = true, caption = "usable", datetype = Types.INTEGER)
	public CField usable; // usable
	@CFieldinfo(fieldname = "sscurty_addr", caption = "sscurty_addr", datetype = Types.VARCHAR)
	public CField sscurty_addr; // sscurty_addr
	@CFieldinfo(fieldname = "sscurty_startdate", caption = "sscurty_startdate", datetype = Types.TIMESTAMP)
	public CField sscurty_startdate; // sscurty_startdate
	@CFieldinfo(fieldname = "shoesize", caption = "shoesize", datetype = Types.VARCHAR)
	public CField shoesize; // shoesize
	@CFieldinfo(fieldname = "pants_code", caption = "pants_code", datetype = Types.VARCHAR)
	public CField pants_code; // pants_code
	@CFieldinfo(fieldname = "coat_code", caption = "coat_code", datetype = Types.VARCHAR)
	public CField coat_code; // coat_code
	@CFieldinfo(fieldname = "dorm_bed", caption = "dorm_bed", datetype = Types.VARCHAR)
	public CField dorm_bed; // dorm_bed
	@CFieldinfo(fieldname = "pay_way", caption = "pay_way", datetype = Types.VARCHAR)
	public CField pay_way; // pay_way
	@CFieldinfo(fieldname = "schedtype", caption = "schedtype", datetype = Types.VARCHAR)
	public CField schedtype; // schedtype
	@CFieldinfo(fieldname = "note", caption = "note", datetype = Types.VARCHAR)
	public CField note; // note
	@CFieldinfo(fieldname = "attid", caption = "attid", datetype = Types.INTEGER)
	public CField attid; // attid
	@CFieldinfo(fieldname = "rectname", caption = "招聘人", datetype = Types.VARCHAR)
	public CField rectname; // 招聘人
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hr_train_regex() throws Exception {
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
