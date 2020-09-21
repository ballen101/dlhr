package com.hr.recruit.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import com.hr.recruit.ctr.CtrHr_recruit_ecodes;

import java.sql.Types;

@CEntity(caption = "可用人事编码段", controller = CtrHr_recruit_ecodes.class, tablename = "Hr_recruit_ecodes")
public class Hr_recruit_ecodes extends CJPA {
	@CFieldinfo(fieldname = "reid", iskey = true, notnull = true, precision = 10, scale = 0, caption = "id", datetype = Types.INTEGER)
	public CField reid; // id
	@CFieldinfo(fieldname = "codebegin", notnull = true, precision = 16, scale = 0, caption = "开始工号", datetype = Types.INTEGER)
	public CField codebegin; // 开始工号
	@CFieldinfo(fieldname = "codeend", notnull = true, precision = 16, scale = 0, caption = "截止工号", datetype = Types.INTEGER)
	public CField codeend; // 截止工号
	@CFieldinfo(fieldname = "codecur", notnull = true, precision = 16, scale = 0, caption = "当前工号", datetype = Types.INTEGER)
	public CField codecur; // 当前工号
	@CFieldinfo(fieldname = "codelen", notnull = true, precision = 2, scale = 0, caption = "工号长度", datetype = Types.INTEGER)
	public CField codelen; // 工号长度
	@CFieldinfo(fieldname = "usable", notnull = true, precision = 1, scale = 0, caption = "是否有效", datetype = Types.INTEGER)
	public CField usable; // 是否有效
	@CFieldinfo(fieldname = "orgid", precision = 10, scale = 0, caption = "适用机构", datetype = Types.INTEGER)
	public CField orgid; // 适用机构
	@CFieldinfo(fieldname = "orgcode", precision = 16, scale = 0, caption = "部门编码", datetype = Types.VARCHAR)
	public CField orgcode; // 部门编码
	@CFieldinfo(fieldname = "orgname", precision = 128, scale = 0, caption = "部门名称", datetype = Types.VARCHAR)
	public CField orgname; // 部门名称
	@CFieldinfo(fieldname = "sp_id", precision = 10, scale = 0, caption = "适用职位", datetype = Types.INTEGER)
	public CField sp_id; // 适用职位
	@CFieldinfo(fieldname = "sp_code", precision = 16, scale = 0, caption = "职位编码", datetype = Types.VARCHAR)
	public CField sp_code; // 职位编码
	@CFieldinfo(fieldname = "sp_name", precision = 128, scale = 0, caption = "职位名称", datetype = Types.VARCHAR)
	public CField sp_name; // 职位名称
	@CFieldinfo(fieldname = "remark", precision = 128, scale = 0, caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "wfid", precision = 20, scale = 0, caption = "wfid", datetype = Types.INTEGER)
	public CField wfid; // wfid
	@CFieldinfo(fieldname = "attid", precision = 20, scale = 0, caption = "attid", datetype = Types.INTEGER)
	public CField attid; // attid
	@CFieldinfo(fieldname = "stat", notnull = true, precision = 2, scale = 0, caption = "表单状态", datetype = Types.INTEGER)
	public CField stat; // 表单状态
	@CFieldinfo(fieldname = "creator", notnull = true, precision = 32, scale = 0, caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", notnull = true, precision = 19, scale = 0, caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", precision = 32, scale = 0, caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", precision = 19, scale = 0, caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	@CFieldinfo(fieldname = "attr1", precision = 32, scale = 0, caption = "备用字段1", datetype = Types.VARCHAR)
	public CField attr1; // 备用字段1
	@CFieldinfo(fieldname = "attr2", precision = 32, scale = 0, caption = "备用字段2", datetype = Types.VARCHAR)
	public CField attr2; // 备用字段2
	@CFieldinfo(fieldname = "attr3", precision = 32, scale = 0, caption = "备用字段3", datetype = Types.VARCHAR)
	public CField attr3; // 备用字段3
	@CFieldinfo(fieldname = "attr4", precision = 32, scale = 0, caption = "备用字段4", datetype = Types.VARCHAR)
	public CField attr4; // 备用字段4
	@CFieldinfo(fieldname = "attr5", precision = 32, scale = 0, caption = "备用字段5", datetype = Types.VARCHAR)
	public CField attr5; // 备用字段5
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hr_recruit_ecodes() throws Exception {
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
