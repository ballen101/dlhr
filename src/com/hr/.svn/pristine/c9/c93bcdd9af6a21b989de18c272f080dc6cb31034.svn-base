package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.attd.ctr.CtrHrkq_business_trip;

import java.sql.Types;

@CEntity(controller = CtrHrkq_business_trip.class)
public class Hrkq_business_trip extends CJPA {
	@CFieldinfo(fieldname = "bta_id", iskey = true, notnull = true, caption = "出差申请ID", datetype = Types.INTEGER)
	public CField bta_id; // 出差申请ID
	@CFieldinfo(fieldname = "bta_code", codeid = 77, notnull = true, caption = "出差申请编码", datetype = Types.VARCHAR)
	public CField bta_code; // 出差申请编码
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "员工档案ID", datetype = Types.INTEGER)
	public CField er_id; // 员工档案ID
	@CFieldinfo(fieldname = "employee_code", notnull = true, caption = "工号", datetype = Types.VARCHAR)
	public CField employee_code; // 工号
	@CFieldinfo(fieldname = "employee_name", notnull = true, caption = "姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 姓名
	@CFieldinfo(fieldname = "orgid", notnull = true, caption = "部门ID", datetype = Types.INTEGER)
	public CField orgid; // 部门ID
	@CFieldinfo(fieldname = "orgcode", notnull = true, caption = "部门编码", datetype = Types.VARCHAR)
	public CField orgcode; // 部门编码
	@CFieldinfo(fieldname = "orgname", notnull = true, caption = "部门名称", datetype = Types.VARCHAR)
	public CField orgname; // 部门名称
	@CFieldinfo(fieldname = "lv_id", caption = "职级ID", datetype = Types.INTEGER)
	public CField lv_id; // 职级ID
	@CFieldinfo(fieldname = "lv_num", caption = "职级", datetype = Types.DECIMAL)
	public CField lv_num; // 职级
	@CFieldinfo(fieldname = "ospid", caption = "职位ID", datetype = Types.INTEGER)
	public CField ospid; // 职位ID
	@CFieldinfo(fieldname = "ospcode", caption = "职位编码", datetype = Types.VARCHAR)
	public CField ospcode; // 职位编码
	@CFieldinfo(fieldname = "sp_name", caption = "职位名称", datetype = Types.VARCHAR)
	public CField sp_name; // 职位名称
	@CFieldinfo(fieldname = "trip_type", caption = "出差类型", datetype = Types.INTEGER)
	public CField trip_type; // 出差类型
	@CFieldinfo(fieldname = "destination", notnull = true, caption = "出差地点", datetype = Types.VARCHAR)
	public CField destination; // 出差地点
	@CFieldinfo(fieldname = "begin_date", caption = "出差开始时间", datetype = Types.TIMESTAMP)
	public CField begin_date; // 出差开始时间
	@CFieldinfo(fieldname = "end_date", caption = "出差结束时间", datetype = Types.TIMESTAMP)
	public CField end_date; // 出差结束时间
	@CFieldinfo(fieldname = "tripdays", caption = "出差合计天数", datetype = Types.INTEGER)
	public CField tripdays; // 出差合计天数
	@CFieldinfo(fieldname = "offdays", caption = "可调休天数", datetype = Types.INTEGER)
	public CField offdays; // 可调休天数
	@CFieldinfo(fieldname = "tripreason", caption = "出差事由", datetype = Types.VARCHAR)
	public CField tripreason; // 出差事由
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "dayofftrip", caption = "休息日出差", datetype = Types.INTEGER)
	public CField dayofftrip; // 休息日出差
	@CFieldinfo(fieldname = "holidaytrip", caption = "法定节假日出差", datetype = Types.INTEGER)
	public CField holidaytrip; // 法定节假日出差
	@CFieldinfo(fieldname = "isoffday", caption = "是否已调休", datetype = Types.INTEGER)
	public CField isoffday; // 是否已调休
	@CFieldinfo(fieldname = "shouldoffdays", caption = "应休天数", datetype = Types.DECIMAL)
	public CField shouldoffdays; // 应休天数
	@CFieldinfo(fieldname = "aleadyoffdays", caption = "已休天数", datetype = Types.DECIMAL)
	public CField aleadyoffdays; // 已休天数
	@CFieldinfo(fieldname = "emplev", caption = "人事层级", datetype = Types.INTEGER)
	public CField emplev; // 人事层级
	@CFieldinfo(fieldname = "orghrlev", caption = "机构人事层级", datetype = Types.INTEGER)
	public CField orghrlev; // 机构人事层级
	@CFieldinfo(fieldname="iswfagent",caption="启用流程代理",datetype=Types.INTEGER)
	public CField iswfagent;  //启用流程代理
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
	@CFieldinfo(fieldname = "agent_er_id", caption = "代理人员工档案ID", datetype = Types.INTEGER)
	public CField agent_er_id; // 代理人员工档案ID
	@CFieldinfo(fieldname = "agent_employee_code", caption = "代理人工号", datetype = Types.VARCHAR)
	public CField agent_employee_code; // 代理人工号
	@CFieldinfo(fieldname = "agent_employee_name", caption = "代理人姓名", datetype = Types.VARCHAR)
	public CField agent_employee_name; // 代理人姓名
	@CFieldinfo(fieldname = "agent_orgid", caption = "代理人部门ID", datetype = Types.INTEGER)
	public CField agent_orgid; // 代理人部门ID
	@CFieldinfo(fieldname = "agent_orgcode", caption = "代理人部门编码", datetype = Types.VARCHAR)
	public CField agent_orgcode; // 代理人部门编码
	@CFieldinfo(fieldname = "agent_orgname", caption = "代理人部门名称", datetype = Types.VARCHAR)
	public CField agent_orgname; // 代理人部门名称
	@CFieldinfo(fieldname = "agent_lv_id", caption = "代理人职级ID", datetype = Types.INTEGER)
	public CField agent_lv_id; // 代理人职级ID
	@CFieldinfo(fieldname = "agent_lv_num", caption = "代理人职级", datetype = Types.DECIMAL)
	public CField agent_lv_num; // 代理人职级
	@CFieldinfo(fieldname = "agent_ospid", caption = "代理人职位ID", datetype = Types.INTEGER)
	public CField agent_ospid; // 代理人职位ID
	@CFieldinfo(fieldname = "agent_ospcode", caption = "代理人职位编码", datetype = Types.VARCHAR)
	public CField agent_ospcode; // 代理人职位编码
	@CFieldinfo(fieldname = "agent_sp_name", caption = "代理人职位名称", datetype = Types.VARCHAR)
	public CField agent_sp_name; // 代理人职位名称
	@CFieldinfo(fieldname = "is_agent", caption = "是否启用代理", datetype = Types.INTEGER)
	public CField is_agent; // 是否启用代理
	@CFieldinfo(fieldname = "creator", notnull = true, caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", notnull = true, caption = "创建时间", datetype = Types.TIMESTAMP)
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
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hrkq_business_trip() throws Exception {
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
