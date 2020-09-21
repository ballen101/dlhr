package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.hr.attd.ctr.CtrHrkq_wkoff;

import java.sql.Types;

@CEntity(controller = CtrHrkq_wkoff.class)
public class Hrkq_wkoff extends CJPA {
	@CFieldinfo(fieldname = "woid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField woid; // ID
	@CFieldinfo(fieldname = "wocode", codeid = 85, notnull = true, caption = "调休编码", datetype = Types.VARCHAR)
	public CField wocode; // 调休编码
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField er_id; // ID
	@CFieldinfo(fieldname = "employee_code", notnull = true, caption = "工号", datetype = Types.VARCHAR)
	public CField employee_code; // 工号
	@CFieldinfo(fieldname = "employee_name", notnull = true, caption = "姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 姓名
	@CFieldinfo(fieldname = "sp_name", caption = "职位", datetype = Types.VARCHAR)
	public CField sp_name; // 职位
	@CFieldinfo(fieldname = "hg_name", caption = "职等", datetype = Types.VARCHAR)
	public CField hg_name; // 职等
	@CFieldinfo(fieldname = "lv_num", caption = "职级", datetype = Types.DECIMAL)
	public CField lv_num; // 职级
	@CFieldinfo(fieldname = "orgid", notnull = true, caption = "部门ID", datetype = Types.INTEGER)
	public CField orgid; // 部门ID
	@CFieldinfo(fieldname = "orgcode", notnull = true, caption = "部门编码", datetype = Types.VARCHAR)
	public CField orgcode; // 部门编码
	@CFieldinfo(fieldname = "orgname", notnull = true, caption = "部门名称", datetype = Types.VARCHAR)
	public CField orgname; // 部门名称
	@CFieldinfo(fieldname = "orghrlev", notnull = true, caption = "机构人事层级", datetype = Types.INTEGER)
	public CField orghrlev; // 机构人事层级
	@CFieldinfo(fieldname="iswfagent",caption="启用流程代理",datetype=Types.INTEGER)
	public CField iswfagent;  //启用流程代理
	@CFieldinfo(fieldname = "emplev", notnull = true, caption = "人事层级", datetype = Types.INTEGER)
	public CField emplev; // 人事层级
	@CFieldinfo(fieldname = "wodays", notnull = true, caption = "调休天数", datetype = Types.INTEGER)
	public CField wodays; // 调休天数
	@CFieldinfo(fieldname = "begin_date", notnull = true, caption = "开始时间", datetype = Types.TIMESTAMP)
	public CField begin_date; // 开始时间
	@CFieldinfo(fieldname = "end_date", notnull = true, caption = "结束时间", datetype = Types.TIMESTAMP)
	public CField end_date; // 结束时间
	@CFieldinfo(fieldname = "reason", notnull = true, caption = "调休原因", datetype = Types.VARCHAR)
	public CField reason; // 调休原因
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "stat", notnull = true, caption = "表单状态", datetype = Types.INTEGER)
	public CField stat; // 表单状态
	@CFieldinfo(fieldname="wfid",caption="wfid",datetype=Types.INTEGER)
	public CField wfid;  //wfid
	@CFieldinfo(fieldname = "attid", caption = "attid", datetype = Types.INTEGER)
	public CField attid; // attid
	@CFieldinfo(fieldname = "idpath", notnull = true, caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	@CFieldinfo(fieldname = "entid", notnull = true, caption = "entid", datetype = Types.INTEGER)
	public CField entid; // entid
	@CFieldinfo(fieldname = "creator", caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", caption = "创建时间", datetype = Types.TIMESTAMP)
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
	@CLinkFieldInfo(jpaclass = Hrkq_wkoffsourse.class, linkFields = { @LinkFieldItem(lfield = "woid", mfield = "woid") })
	public CJPALineData<Hrkq_wkoffsourse> hrkq_wkoffsourses;

	public Hrkq_wkoff() throws Exception {
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
