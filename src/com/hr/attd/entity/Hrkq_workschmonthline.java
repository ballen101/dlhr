package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hrkq_workschmonthline extends CJPA {
	@CFieldinfo(fieldname = "wklid", iskey = true, notnull = true, caption = "LID", datetype = Types.INTEGER)
	public CField wklid; // LID
	@CFieldinfo(fieldname = "wkid", notnull = true, caption = "主表ID", datetype = Types.INTEGER)
	public CField wkid; // 主表ID
	@CFieldinfo(fieldname = "tid", notnull = true, caption = "目标ID", datetype = Types.INTEGER)
	public CField tid; // 目标ID
	@CFieldinfo(fieldname = "tcode", notnull = true, caption = "目标编码", datetype = Types.VARCHAR)
	public CField tcode; // 目标编码
	@CFieldinfo(fieldname = "tname", notnull = true, caption = "目标名称", datetype = Types.VARCHAR)
	public CField tname; // 目标名称
	@CFieldinfo(fieldname = "sp_name", caption = "职位名称", datetype = Types.VARCHAR)
	public CField sp_name; // 职位名称 ttype==3
	@CFieldinfo(fieldname = "lv_num", caption = "职级", datetype = Types.DECIMAL)
	public CField lv_num; // 职级 ttype==3
	@CFieldinfo(fieldname = "ttype", notnull = true, caption = "类型:1 班制组 2 部门 3 个人", datetype = Types.INTEGER)
	public CField ttype; // 类型:1 班制组 2 部门 3 个人
	@CFieldinfo(fieldname = "scid1", caption = "班制ID1", datetype = Types.INTEGER)
	public CField scid1; // 班制ID1
	@CFieldinfo(fieldname = "scid2", caption = "班制ID2", datetype = Types.INTEGER)
	public CField scid2; // 班制ID2
	@CFieldinfo(fieldname = "scid3", caption = "班制ID3", datetype = Types.INTEGER)
	public CField scid3; // 班制ID3
	@CFieldinfo(fieldname = "scid4", caption = "班制ID4", datetype = Types.INTEGER)
	public CField scid4; // 班制ID4
	@CFieldinfo(fieldname = "scid5", caption = "班制ID5", datetype = Types.INTEGER)
	public CField scid5; // 班制ID5
	@CFieldinfo(fieldname = "scid6", caption = "班制ID6", datetype = Types.INTEGER)
	public CField scid6; // 班制ID6
	@CFieldinfo(fieldname = "scid7", caption = "班制ID7", datetype = Types.INTEGER)
	public CField scid7; // 班制ID7
	@CFieldinfo(fieldname = "scid8", caption = "班制ID8", datetype = Types.INTEGER)
	public CField scid8; // 班制ID8
	@CFieldinfo(fieldname = "scid9", caption = "班制ID9", datetype = Types.INTEGER)
	public CField scid9; // 班制ID9
	@CFieldinfo(fieldname = "scid10", caption = "班制ID10", datetype = Types.INTEGER)
	public CField scid10; // 班制ID10
	@CFieldinfo(fieldname = "scid11", caption = "班制ID11", datetype = Types.INTEGER)
	public CField scid11; // 班制ID11
	@CFieldinfo(fieldname = "scid12", caption = "班制ID12", datetype = Types.INTEGER)
	public CField scid12; // 班制ID12
	@CFieldinfo(fieldname = "scid13", caption = "班制ID13", datetype = Types.INTEGER)
	public CField scid13; // 班制ID13
	@CFieldinfo(fieldname = "scid14", caption = "班制ID14", datetype = Types.INTEGER)
	public CField scid14; // 班制ID14
	@CFieldinfo(fieldname = "scid15", caption = "班制ID15", datetype = Types.INTEGER)
	public CField scid15; // 班制ID15
	@CFieldinfo(fieldname = "scid16", caption = "班制ID16", datetype = Types.INTEGER)
	public CField scid16; // 班制ID16
	@CFieldinfo(fieldname = "scid17", caption = "班制ID17", datetype = Types.INTEGER)
	public CField scid17; // 班制ID17
	@CFieldinfo(fieldname = "scid18", caption = "班制ID18", datetype = Types.INTEGER)
	public CField scid18; // 班制ID18
	@CFieldinfo(fieldname = "scid19", caption = "班制ID19", datetype = Types.INTEGER)
	public CField scid19; // 班制ID19
	@CFieldinfo(fieldname = "scid20", caption = "班制ID20", datetype = Types.INTEGER)
	public CField scid20; // 班制ID20
	@CFieldinfo(fieldname = "scid21", caption = "班制ID21", datetype = Types.INTEGER)
	public CField scid21; // 班制ID21
	@CFieldinfo(fieldname = "scid22", caption = "班制ID22", datetype = Types.INTEGER)
	public CField scid22; // 班制ID22
	@CFieldinfo(fieldname = "scid23", caption = "班制ID23", datetype = Types.INTEGER)
	public CField scid23; // 班制ID23
	@CFieldinfo(fieldname = "scid24", caption = "班制ID24", datetype = Types.INTEGER)
	public CField scid24; // 班制ID24
	@CFieldinfo(fieldname = "scid25", caption = "班制ID25", datetype = Types.INTEGER)
	public CField scid25; // 班制ID25
	@CFieldinfo(fieldname = "scid26", caption = "班制ID26", datetype = Types.INTEGER)
	public CField scid26; // 班制ID26
	@CFieldinfo(fieldname = "scid27", caption = "班制ID27", datetype = Types.INTEGER)
	public CField scid27; // 班制ID27
	@CFieldinfo(fieldname = "scid28", caption = "班制ID28", datetype = Types.INTEGER)
	public CField scid28; // 班制ID28
	@CFieldinfo(fieldname = "scid29", caption = "班制ID29", datetype = Types.INTEGER)
	public CField scid29; // 班制ID29
	@CFieldinfo(fieldname = "scid30", caption = "班制ID30", datetype = Types.INTEGER)
	public CField scid30; // 班制ID30
	@CFieldinfo(fieldname = "scid31", caption = "班制ID31", datetype = Types.INTEGER)
	public CField scid31; // 班制ID31
	@CFieldinfo(fieldname = "scdname1", caption = "班制名称1", datetype = Types.VARCHAR)
	public CField scdname1; // 班制名称1
	@CFieldinfo(fieldname = "scdname2", caption = "班制名称2", datetype = Types.VARCHAR)
	public CField scdname2; // 班制名称2
	@CFieldinfo(fieldname = "scdname3", caption = "班制名称3", datetype = Types.VARCHAR)
	public CField scdname3; // 班制名称3
	@CFieldinfo(fieldname = "scdname4", caption = "班制名称4", datetype = Types.VARCHAR)
	public CField scdname4; // 班制名称4
	@CFieldinfo(fieldname = "scdname5", caption = "班制名称5", datetype = Types.VARCHAR)
	public CField scdname5; // 班制名称5
	@CFieldinfo(fieldname = "scdname6", caption = "班制名称6", datetype = Types.VARCHAR)
	public CField scdname6; // 班制名称6
	@CFieldinfo(fieldname = "scdname7", caption = "班制名称7", datetype = Types.VARCHAR)
	public CField scdname7; // 班制名称7
	@CFieldinfo(fieldname = "scdname8", caption = "班制名称8", datetype = Types.VARCHAR)
	public CField scdname8; // 班制名称8
	@CFieldinfo(fieldname = "scdname9", caption = "班制名称9", datetype = Types.VARCHAR)
	public CField scdname9; // 班制名称9
	@CFieldinfo(fieldname = "scdname10", caption = "班制名称10", datetype = Types.VARCHAR)
	public CField scdname10; // 班制名称10
	@CFieldinfo(fieldname = "scdname11", caption = "班制名称11", datetype = Types.VARCHAR)
	public CField scdname11; // 班制名称11
	@CFieldinfo(fieldname = "scdname12", caption = "班制名称12", datetype = Types.VARCHAR)
	public CField scdname12; // 班制名称12
	@CFieldinfo(fieldname = "scdname13", caption = "班制名称13", datetype = Types.VARCHAR)
	public CField scdname13; // 班制名称13
	@CFieldinfo(fieldname = "scdname14", caption = "班制名称14", datetype = Types.VARCHAR)
	public CField scdname14; // 班制名称14
	@CFieldinfo(fieldname = "scdname15", caption = "班制名称15", datetype = Types.VARCHAR)
	public CField scdname15; // 班制名称15
	@CFieldinfo(fieldname = "scdname16", caption = "班制名称16", datetype = Types.VARCHAR)
	public CField scdname16; // 班制名称16
	@CFieldinfo(fieldname = "scdname17", caption = "班制名称17", datetype = Types.VARCHAR)
	public CField scdname17; // 班制名称17
	@CFieldinfo(fieldname = "scdname18", caption = "班制名称18", datetype = Types.VARCHAR)
	public CField scdname18; // 班制名称18
	@CFieldinfo(fieldname = "scdname19", caption = "班制名称19", datetype = Types.VARCHAR)
	public CField scdname19; // 班制名称19
	@CFieldinfo(fieldname = "scdname20", caption = "班制名称20", datetype = Types.VARCHAR)
	public CField scdname20; // 班制名称20
	@CFieldinfo(fieldname = "scdname21", caption = "班制名称21", datetype = Types.VARCHAR)
	public CField scdname21; // 班制名称21
	@CFieldinfo(fieldname = "scdname22", caption = "班制名称22", datetype = Types.VARCHAR)
	public CField scdname22; // 班制名称22
	@CFieldinfo(fieldname = "scdname23", caption = "班制名称23", datetype = Types.VARCHAR)
	public CField scdname23; // 班制名称23
	@CFieldinfo(fieldname = "scdname24", caption = "班制名称24", datetype = Types.VARCHAR)
	public CField scdname24; // 班制名称24
	@CFieldinfo(fieldname = "scdname25", caption = "班制名称25", datetype = Types.VARCHAR)
	public CField scdname25; // 班制名称25
	@CFieldinfo(fieldname = "scdname26", caption = "班制名称26", datetype = Types.VARCHAR)
	public CField scdname26; // 班制名称26
	@CFieldinfo(fieldname = "scdname27", caption = "班制名称27", datetype = Types.VARCHAR)
	public CField scdname27; // 班制名称27
	@CFieldinfo(fieldname = "scdname28", caption = "班制名称28", datetype = Types.VARCHAR)
	public CField scdname28; // 班制名称28
	@CFieldinfo(fieldname = "scdname29", caption = "班制名称29", datetype = Types.VARCHAR)
	public CField scdname29; // 班制名称29
	@CFieldinfo(fieldname = "scdname30", caption = "班制名称30", datetype = Types.VARCHAR)
	public CField scdname30; // 班制名称30
	@CFieldinfo(fieldname = "scdname31", caption = "班制名称31", datetype = Types.VARCHAR)
	public CField scdname31; // 班制名称31
	@CFieldinfo(fieldname = "backcolor1", caption = "背景颜色1", datetype = Types.VARCHAR)
	public CField backcolor1; // 背景颜色1
	@CFieldinfo(fieldname = "backcolor2", caption = "背景颜色2", datetype = Types.VARCHAR)
	public CField backcolor2; // 背景颜色2
	@CFieldinfo(fieldname = "backcolor3", caption = "背景颜色3", datetype = Types.VARCHAR)
	public CField backcolor3; // 背景颜色3
	@CFieldinfo(fieldname = "backcolor4", caption = "背景颜色4", datetype = Types.VARCHAR)
	public CField backcolor4; // 背景颜色4
	@CFieldinfo(fieldname = "backcolor5", caption = "背景颜色5", datetype = Types.VARCHAR)
	public CField backcolor5; // 背景颜色5
	@CFieldinfo(fieldname = "backcolor6", caption = "背景颜色6", datetype = Types.VARCHAR)
	public CField backcolor6; // 背景颜色6
	@CFieldinfo(fieldname = "backcolor7", caption = "背景颜色7", datetype = Types.VARCHAR)
	public CField backcolor7; // 背景颜色7
	@CFieldinfo(fieldname = "backcolor8", caption = "背景颜色8", datetype = Types.VARCHAR)
	public CField backcolor8; // 背景颜色8
	@CFieldinfo(fieldname = "backcolor9", caption = "背景颜色9", datetype = Types.VARCHAR)
	public CField backcolor9; // 背景颜色9
	@CFieldinfo(fieldname = "backcolor10", caption = "背景颜色10", datetype = Types.VARCHAR)
	public CField backcolor10; // 背景颜色10
	@CFieldinfo(fieldname = "backcolor11", caption = "背景颜色11", datetype = Types.VARCHAR)
	public CField backcolor11; // 背景颜色11
	@CFieldinfo(fieldname = "backcolor12", caption = "背景颜色12", datetype = Types.VARCHAR)
	public CField backcolor12; // 背景颜色12
	@CFieldinfo(fieldname = "backcolor13", caption = "背景颜色13", datetype = Types.VARCHAR)
	public CField backcolor13; // 背景颜色13
	@CFieldinfo(fieldname = "backcolor14", caption = "背景颜色14", datetype = Types.VARCHAR)
	public CField backcolor14; // 背景颜色14
	@CFieldinfo(fieldname = "backcolor15", caption = "背景颜色15", datetype = Types.VARCHAR)
	public CField backcolor15; // 背景颜色15
	@CFieldinfo(fieldname = "backcolor16", caption = "背景颜色16", datetype = Types.VARCHAR)
	public CField backcolor16; // 背景颜色16
	@CFieldinfo(fieldname = "backcolor17", caption = "背景颜色17", datetype = Types.VARCHAR)
	public CField backcolor17; // 背景颜色17
	@CFieldinfo(fieldname = "backcolor18", caption = "背景颜色18", datetype = Types.VARCHAR)
	public CField backcolor18; // 背景颜色18
	@CFieldinfo(fieldname = "backcolor19", caption = "背景颜色19", datetype = Types.VARCHAR)
	public CField backcolor19; // 背景颜色19
	@CFieldinfo(fieldname = "backcolor20", caption = "背景颜色20", datetype = Types.VARCHAR)
	public CField backcolor20; // 背景颜色20
	@CFieldinfo(fieldname = "backcolor21", caption = "背景颜色21", datetype = Types.VARCHAR)
	public CField backcolor21; // 背景颜色21
	@CFieldinfo(fieldname = "backcolor22", caption = "背景颜色22", datetype = Types.VARCHAR)
	public CField backcolor22; // 背景颜色22
	@CFieldinfo(fieldname = "backcolor23", caption = "背景颜色23", datetype = Types.VARCHAR)
	public CField backcolor23; // 背景颜色23
	@CFieldinfo(fieldname = "backcolor24", caption = "背景颜色24", datetype = Types.VARCHAR)
	public CField backcolor24; // 背景颜色24
	@CFieldinfo(fieldname = "backcolor25", caption = "背景颜色25", datetype = Types.VARCHAR)
	public CField backcolor25; // 背景颜色25
	@CFieldinfo(fieldname = "backcolor26", caption = "背景颜色26", datetype = Types.VARCHAR)
	public CField backcolor26; // 背景颜色26
	@CFieldinfo(fieldname = "backcolor27", caption = "背景颜色27", datetype = Types.VARCHAR)
	public CField backcolor27; // 背景颜色27
	@CFieldinfo(fieldname = "backcolor28", caption = "背景颜色28", datetype = Types.VARCHAR)
	public CField backcolor28; // 背景颜色28
	@CFieldinfo(fieldname = "backcolor29", caption = "背景颜色29", datetype = Types.VARCHAR)
	public CField backcolor29; // 背景颜色29
	@CFieldinfo(fieldname = "backcolor30", caption = "背景颜色30", datetype = Types.VARCHAR)
	public CField backcolor30; // 背景颜色30
	@CFieldinfo(fieldname = "backcolor31", caption = "背景颜色31", datetype = Types.VARCHAR)
	public CField backcolor31; // 背景颜色31
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hrkq_workschmonthline() throws Exception {
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
