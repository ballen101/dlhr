package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption = "批量出差申请行表", tablename = "Hrkq_business_trip_batchline")
public class Hrkq_business_trip_batchline extends CJPA {
	@CFieldinfo(fieldname = "btabl_id", iskey = true, notnull = true, precision = 10, scale = 0, caption = "批量出差申请ID", datetype = Types.INTEGER)
	public CField btabl_id; // 批量出差申请ID
	@CFieldinfo(fieldname = "btab_id", notnull = true, precision = 10, scale = 0, caption = "批量出差申请ID", datetype = Types.INTEGER)
	public CField btab_id; // 批量出差申请ID
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
	@CFieldinfo(fieldname = "lv_id", precision = 10, scale = 0, caption = "职级ID", datetype = Types.INTEGER)
	public CField lv_id; // 职级ID
	@CFieldinfo(fieldname = "lv_num", precision = 4, scale = 1, caption = "职级", datetype = Types.DECIMAL)
	public CField lv_num; // 职级
	@CFieldinfo(fieldname = "ospid", precision = 20, scale = 0, caption = "职位ID", datetype = Types.INTEGER)
	public CField ospid; // 职位ID
	@CFieldinfo(fieldname = "ospcode", precision = 16, scale = 0, caption = "职位编码", datetype = Types.VARCHAR)
	public CField ospcode; // 职位编码
	@CFieldinfo(fieldname = "sp_name", precision = 128, scale = 0, caption = "职位名称", datetype = Types.VARCHAR)
	public CField sp_name; // 职位名称
	@CFieldinfo(fieldname = "trip_type", precision = 2, scale = 0, caption = "出差类型", datetype = Types.INTEGER)
	public CField trip_type; // 出差类型
	@CFieldinfo(fieldname = "destination", notnull = true, precision = 256, scale = 0, caption = "出差地点", datetype = Types.VARCHAR)
	public CField destination; // 出差地点
	@CFieldinfo(fieldname = "begin_date", precision = 19, scale = 0, caption = "出差开始时间", datetype = Types.TIMESTAMP)
	public CField begin_date; // 出差开始时间
	@CFieldinfo(fieldname = "end_date", precision = 19, scale = 0, caption = "出差结束时间", datetype = Types.TIMESTAMP)
	public CField end_date; // 出差结束时间
	@CFieldinfo(fieldname = "tripdays", precision = 4, scale = 1, caption = "出差合计天数", datetype = Types.DECIMAL)
	public CField tripdays; // 出差合计天数
	@CFieldinfo(fieldname = "offdays", precision = 4, scale = 1, caption = "可调休天数", datetype = Types.DECIMAL)
	public CField offdays; // 可调休天数
	@CFieldinfo(fieldname = "tripreason", precision = 128, scale = 0, caption = "出差事由", datetype = Types.VARCHAR)
	public CField tripreason; // 出差事由
	@CFieldinfo(fieldname = "remark", precision = 128, scale = 0, caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "dayofftrip", precision = 1, scale = 0, caption = "休息日出差", datetype = Types.INTEGER)
	public CField dayofftrip; // 休息日出差
	@CFieldinfo(fieldname = "holidaytrip", precision = 1, scale = 0, caption = "法定节假日出差", datetype = Types.INTEGER)
	public CField holidaytrip; // 法定节假日出差
	@CFieldinfo(fieldname = "isoffday", precision = 1, scale = 0, caption = "是否已调休", datetype = Types.INTEGER)
	public CField isoffday; // 是否已调休
	@CFieldinfo(fieldname = "shouldoffdays", precision = 4, scale = 1, caption = "应休天数", datetype = Types.DECIMAL)
	public CField shouldoffdays; // 应休天数
	@CFieldinfo(fieldname = "aleadyoffdays", precision = 4, scale = 1, caption = "已休天数", datetype = Types.DECIMAL)
	public CField aleadyoffdays; // 已休天数
	@CFieldinfo(fieldname = "emplev", precision = 2, scale = 0, caption = "人事层级", datetype = Types.INTEGER)
	public CField emplev; // 人事层级
	@CFieldinfo(fieldname = "orghrlev", precision = 1, scale = 0, caption = "机构人事层级", datetype = Types.INTEGER)
	public CField orghrlev; // 机构人事层级
	@CFieldinfo(fieldname = "iswfagent", precision = 1, scale = 0, caption = "启用流程代理", datetype = Types.INTEGER)
	public CField iswfagent; // 启用流程代理
	@CFieldinfo(fieldname = "agent_er_id", precision = 10, scale = 0, caption = "代理人员工档案ID", datetype = Types.INTEGER)
	public CField agent_er_id; // 代理人员工档案ID
	@CFieldinfo(fieldname = "agent_employee_code", precision = 16, scale = 0, caption = "代理人工号", datetype = Types.VARCHAR)
	public CField agent_employee_code; // 代理人工号
	@CFieldinfo(fieldname = "agent_employee_name", precision = 64, scale = 0, caption = "代理人姓名", datetype = Types.VARCHAR)
	public CField agent_employee_name; // 代理人姓名
	@CFieldinfo(fieldname = "agent_orgid", precision = 10, scale = 0, caption = "代理人部门ID", datetype = Types.INTEGER)
	public CField agent_orgid; // 代理人部门ID
	@CFieldinfo(fieldname = "agent_orgcode", precision = 16, scale = 0, caption = "代理人部门编码", datetype = Types.VARCHAR)
	public CField agent_orgcode; // 代理人部门编码
	@CFieldinfo(fieldname = "agent_orgname", precision = 128, scale = 0, caption = "代理人部门名称", datetype = Types.VARCHAR)
	public CField agent_orgname; // 代理人部门名称
	@CFieldinfo(fieldname = "agent_lv_id", precision = 10, scale = 0, caption = "代理人职级ID", datetype = Types.INTEGER)
	public CField agent_lv_id; // 代理人职级ID
	@CFieldinfo(fieldname = "agent_lv_num", precision = 4, scale = 2, caption = "代理人职级", datetype = Types.DECIMAL)
	public CField agent_lv_num; // 代理人职级
	@CFieldinfo(fieldname = "agent_ospid", precision = 20, scale = 0, caption = "代理人职位ID", datetype = Types.INTEGER)
	public CField agent_ospid; // 代理人职位ID
	@CFieldinfo(fieldname = "agent_ospcode", precision = 16, scale = 0, caption = "代理人职位编码", datetype = Types.VARCHAR)
	public CField agent_ospcode; // 代理人职位编码
	@CFieldinfo(fieldname = "agent_sp_name", precision = 128, scale = 0, caption = "代理人职位名称", datetype = Types.VARCHAR)
	public CField agent_sp_name; // 代理人职位名称
	@CFieldinfo(fieldname = "is_agent", precision = 2, scale = 0, caption = "是否启用代理", datetype = Types.INTEGER)
	public CField is_agent; // 是否启用代理
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义
	

	public Hrkq_business_trip_batchline() throws Exception {
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
