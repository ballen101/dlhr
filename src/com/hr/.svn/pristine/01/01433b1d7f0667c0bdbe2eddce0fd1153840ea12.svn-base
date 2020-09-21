package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption = "请假表单test", tablename = "Hrkq_holidayapp_test")
public class Hrkq_holidayapp_test extends CJPA {
	@CFieldinfo(fieldname = "haid", iskey = true, notnull = true, precision = 10, scale = 0, caption = "申请ID", datetype = Types.INTEGER)
	public CField haid; // 申请ID
	@CFieldinfo(fieldname = "hacode", codeid = 141, notnull = true, precision = 16, scale = 0, caption = "编码", datetype = Types.VARCHAR)
	public CField hacode; // 编码
	@CFieldinfo(fieldname = "er_id", notnull = true, precision = 10, scale = 0, caption = "人事ID", datetype = Types.INTEGER)
	public CField er_id; // 人事ID
	@CFieldinfo(fieldname = "employee_code", notnull = true, precision = 16, scale = 0, caption = "工号", datetype = Types.VARCHAR)
	public CField employee_code; // 工号
	@CFieldinfo(fieldname = "employee_name", notnull = true, precision = 64, scale = 0, caption = "姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 姓名
	@CFieldinfo(fieldname = "orgid", notnull = true, precision = 10, scale = 0, caption = "部门ID", datetype = Types.INTEGER)
	public CField orgid; // 部门ID
	@CFieldinfo(fieldname = "orgcode", notnull = true, precision = 16, scale = 0, caption = "部门编码", datetype = Types.VARCHAR)
	public CField orgcode; // 部门编码
	@CFieldinfo(fieldname = "orgname", notnull = true, precision = 128, scale = 0, caption = "部门", datetype = Types.VARCHAR)
	public CField orgname; // 部门
	@CFieldinfo(fieldname = "ospid", notnull = true, precision = 20, scale = 0, caption = "职位ID", datetype = Types.INTEGER)
	public CField ospid; // 职位ID
	@CFieldinfo(fieldname = "ospcode", notnull = true, precision = 16, scale = 0, caption = "职位编码", datetype = Types.VARCHAR)
	public CField ospcode; // 职位编码
	@CFieldinfo(fieldname = "sp_name", notnull = true, precision = 128, scale = 0, caption = "职位", datetype = Types.VARCHAR)
	public CField sp_name; // 职位
	@CFieldinfo(fieldname = "lv_num", notnull = true, precision = 4, scale = 1, caption = "职级", datetype = Types.DECIMAL)
	public CField lv_num; // 职级
	@CFieldinfo(fieldname = "hdays", notnull = true, precision = 4, scale = 1, caption = "请假天数", datetype = Types.DECIMAL)
	public CField hdays; // 请假天数
	@CFieldinfo(fieldname = "hdaystrue", precision = 4, scale = 1, caption = "实际天数 如果没销假 则==销假天数", datetype = Types.DECIMAL)
	public CField hdaystrue; // 实际天数 如果没销假 则==销假天数
	@CFieldinfo(fieldname = "timebg", notnull = true, precision = 19, scale = 0, caption = "请假时间开始 yyyy-MM-dd hh:mm", datetype = Types.TIMESTAMP)
	public CField timebg; // 请假时间开始 yyyy-MM-dd hh:mm
	@CFieldinfo(fieldname = "timeed", notnull = true, precision = 19, scale = 0, caption = "请假时间截止 yyyy-MM-dd hh:mm", datetype = Types.TIMESTAMP)
	public CField timeed; // 请假时间截止 yyyy-MM-dd hh:mm
	@CFieldinfo(fieldname = "timeedtrue", notnull = true, precision = 19, scale = 0, caption = "实际截止时间 如果有销假 按销假时间 否则按申请截止时间 yyyy-MM-dd hh:mm", datetype = Types.TIMESTAMP)
	public CField timeedtrue; // 实际截止时间 如果有销假 按销假时间 否则按申请截止时间 yyyy-MM-dd hh:mm
	@CFieldinfo(fieldname = "htid", notnull = true, precision = 10, scale = 0, caption = "类型ID", datetype = Types.INTEGER)
	public CField htid; // 类型ID
	@CFieldinfo(fieldname = "htname", notnull = true, precision = 64, scale = 0, caption = "假期类型", datetype = Types.VARCHAR)
	public CField htname; // 假期类型
	@CFieldinfo(fieldname = "bhtype", precision = 2, scale = 0, caption = "假期类别", datetype = Types.INTEGER)
	public CField bhtype; // 假期类别
	@CFieldinfo(fieldname = "htreason", precision = 64, scale = 0, caption = "事由", datetype = Types.VARCHAR)
	public CField htreason; // 事由
	@CFieldinfo(fieldname = "htconfirm", precision = 16, scale = 0, caption = "假期确认", datetype = Types.VARCHAR)
	public CField htconfirm; // 假期确认
	@CFieldinfo(fieldname = "viodeal", precision = 16, scale = 0, caption = "违规处理", datetype = Types.VARCHAR)
	public CField viodeal; // 违规处理
	@CFieldinfo(fieldname = "timebk", precision = 19, scale = 0, caption = "销假时间 yyyy-MM-dd hh:mm", datetype = Types.TIMESTAMP)
	public CField timebk; // 销假时间 yyyy-MM-dd hh:mm
	@CFieldinfo(fieldname = "btconfirm", precision = 16, scale = 0, caption = "销假确认", datetype = Types.VARCHAR)
	public CField btconfirm; // 销假确认
	@CFieldinfo(fieldname = "orghrlev", notnull = true, precision = 1, scale = 0, caption = "机构人事层级", defvalue = "0", datetype = Types.INTEGER)
	public CField orghrlev; // 机构人事层级
	@CFieldinfo(fieldname = "iswfagent", precision = 1, scale = 0, caption = "启用流程代理", datetype = Types.INTEGER)
	public CField iswfagent; // 启用流程代理
	@CFieldinfo(fieldname = "emplev", notnull = true, precision = 2, scale = 0, caption = "人事层级", datetype = Types.INTEGER)
	public CField emplev; // 人事层级
	@CFieldinfo(fieldname = "remark", precision = 128, scale = 0, caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "wfid", precision = 20, scale = 0, caption = "wfid", datetype = Types.INTEGER)
	public CField wfid; // wfid
	@CFieldinfo(fieldname = "attid", precision = 20, scale = 0, caption = "attid", datetype = Types.INTEGER)
	public CField attid; // attid
	@CFieldinfo(fieldname = "stat", notnull = true, precision = 2, scale = 0, caption = "表单状态", datetype = Types.INTEGER)
	public CField stat; // 表单状态
	@CFieldinfo(fieldname = "idpath", notnull = true, precision = 256, scale = 0, caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	@CFieldinfo(fieldname = "entid", notnull = true, precision = 5, scale = 0, caption = "entid", datetype = Types.INTEGER)
	public CField entid; // entid
	@CFieldinfo(fieldname = "creator", notnull = true, precision = 32, scale = 0, caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", notnull = true, precision = 19, scale = 0, caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", precision = 32, scale = 0, caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", precision = 19, scale = 0, caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	@CFieldinfo(fieldname = "attribute1", precision = 32, scale = 0, caption = "备用字段1", datetype = Types.VARCHAR)
	public CField attribute1; // 备用字段1
	@CFieldinfo(fieldname = "attribute2", precision = 32, scale = 0, caption = "备用字段2", datetype = Types.VARCHAR)
	public CField attribute2; // 备用字段2
	@CFieldinfo(fieldname = "attribute3", precision = 32, scale = 0, caption = "备用字段3", datetype = Types.VARCHAR)
	public CField attribute3; // 备用字段3
	@CFieldinfo(fieldname = "attribute4", precision = 32, scale = 0, caption = "备用字段4", datetype = Types.VARCHAR)
	public CField attribute4; // 备用字段4
	@CFieldinfo(fieldname = "attribute5", precision = 32, scale = 0, caption = "备用字段5", datetype = Types.VARCHAR)
	public CField attribute5; // 备用字段5
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hrkq_holidayapp_test() throws Exception {
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
