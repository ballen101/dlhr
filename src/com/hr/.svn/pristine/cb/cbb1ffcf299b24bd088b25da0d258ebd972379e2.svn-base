package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.attd.ctr.CtrHrkq_holidayapp;

import java.sql.Types;

@CEntity(controller = CtrHrkq_holidayapp.class)
public class Hrkq_holidayapp extends CJPA {
	@CFieldinfo(fieldname = "haid", iskey = true, notnull = true, caption = "申请ID", datetype = Types.INTEGER)
	public CField haid; // 申请ID
	@CFieldinfo(fieldname = "hacode", codeid = 82, notnull = true, caption = "编码", datetype = Types.VARCHAR)
	public CField hacode; // 编码
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "人事ID", datetype = Types.INTEGER)
	public CField er_id; // 人事ID
	@CFieldinfo(fieldname = "employee_code", notnull = true, caption = "工号", datetype = Types.VARCHAR)
	public CField employee_code; // 工号
	@CFieldinfo(fieldname = "employee_name", notnull = true, caption = "姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 姓名
	@CFieldinfo(fieldname = "orgid", notnull = true, caption = "部门ID", datetype = Types.INTEGER)
	public CField orgid; // 部门ID
	@CFieldinfo(fieldname = "orgcode", notnull = true, caption = "部门编码", datetype = Types.VARCHAR)
	public CField orgcode; // 部门编码
	@CFieldinfo(fieldname = "orgname", notnull = true, caption = "部门", datetype = Types.VARCHAR)
	public CField orgname; // 部门
	@CFieldinfo(fieldname = "ospid", notnull = true, caption = "职位ID", datetype = Types.INTEGER)
	public CField ospid; // 职位ID
	@CFieldinfo(fieldname = "ospcode", notnull = true, caption = "职位编码", datetype = Types.VARCHAR)
	public CField ospcode; // 职位编码
	@CFieldinfo(fieldname = "sp_name", notnull = true, caption = "职位", datetype = Types.VARCHAR)
	public CField sp_name; // 职位
	@CFieldinfo(fieldname = "lv_num", notnull = true, caption = "职级", datetype = Types.DECIMAL)
	public CField lv_num; // 职级
	@CFieldinfo(fieldname = "hdays", notnull = true, caption = "请假天数", datetype = Types.DECIMAL)
	public CField hdays; // 请假天数
	@CFieldinfo(fieldname="hdaystrue",caption="实际天数 如果没销假 则==销假天数",datetype=Types.DECIMAL)
	public CField hdaystrue;  //实际天数 如果没销假 则==销假天数
	@CFieldinfo(fieldname = "timebg", notnull = true, caption = "请假时间开始", datetype = Types.TIMESTAMP)
	public CField timebg; // 请假时间开始 yyyy-MM-dd hh:mm
	@CFieldinfo(fieldname = "timeed", notnull = true, caption = "请假时间截止", datetype = Types.TIMESTAMP)
	public CField timeed; // 请假时间截止 yyyy-MM-dd hh:mm
	@CFieldinfo(fieldname = "timeedtrue", notnull = true, caption = "实际截止时间", datetype = Types.TIMESTAMP)
	public CField timeedtrue; // 实际截止时间 如果有销假 按销假时间 否则按申请截止时间 yyyy-MM-dd hh:mm
	@CFieldinfo(fieldname = "htid", notnull = true, caption = "类型ID", datetype = Types.INTEGER)
	public CField htid; // 类型ID
	@CFieldinfo(fieldname = "htname", notnull = true, caption = "假期类型", datetype = Types.VARCHAR)
	public CField htname; // 假期类型
	@CFieldinfo(fieldname="bhtype",precision=2,scale=0,caption="假期类别",datetype=Types.INTEGER)
	public CField bhtype;  //假期类别
	@CFieldinfo(fieldname = "htreason", caption = "事由", datetype = Types.VARCHAR)
	public CField htreason; // 事由
	@CFieldinfo(fieldname = "htconfirm", caption = "假期确认", datetype = Types.VARCHAR)
	public CField htconfirm; // 假期确认   //符合规则   违反规则  空
	@CFieldinfo(fieldname = "viodeal", caption = "违规处理", datetype = Types.VARCHAR)
	public CField viodeal; // 违规处理  //事假  旷工 空
	@CFieldinfo(fieldname = "timebk", caption = "销假时间 yyyy-MM-dd hh:mm", datetype = Types.TIMESTAMP)
	public CField timebk; // 销假时间 yyyy-MM-dd hh:mm
	@CFieldinfo(fieldname = "btconfirm", caption = "销假确认", datetype = Types.VARCHAR)
	public CField btconfirm; // 销假确认
	@CFieldinfo(fieldname = "orghrlev", notnull = true, caption = "机构人事层级", datetype = Types.INTEGER)
	public CField orghrlev; // 机构人事层级
	@CFieldinfo(fieldname="iswfagent",caption="启用流程代理",datetype=Types.INTEGER)
	public CField iswfagent;  //启用流程代理
	@CFieldinfo(fieldname = "emplev", notnull = true, caption = "人事层级", datetype = Types.INTEGER)
	public CField emplev; // 人事层级
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
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

	@CLinkFieldInfo(jpaclass = Hrkq_holidayapp_month.class, linkFields = { @LinkFieldItem(lfield = "haid", mfield = "haid") })
	public CJPALineData<Hrkq_holidayapp_month> hrkq_holidayapp_months;

	public Hrkq_holidayapp() throws Exception {
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
