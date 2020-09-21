package com.hr.access.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.access.co.COHr_access_special;

import java.sql.Types;

@CEntity(controller = COHr_access_special.class)
public class Hr_access_special extends CJPA {
	@CFieldinfo(fieldname = "access_special_id", iskey = true, notnull = true, caption = "特殊权限ID", datetype = Types.INTEGER)
	public CField access_special_id; // 特殊权限ID
	@CFieldinfo(fieldname = "access_special_num", caption = "特殊申请单号", codeid = 105, datetype = Types.VARCHAR)
	public CField access_special_num; // 特殊申请单号
	@CFieldinfo(fieldname = "employee_code", caption = "工号", datetype = Types.VARCHAR)
	public CField employee_code; // 工号
	@CFieldinfo(fieldname = "employee_name", caption = "员工姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 员工姓名
	@CFieldinfo(fieldname = "sex", caption = "性别", datetype = Types.INTEGER)
	public CField sex; // 性别
	@CFieldinfo(fieldname = "access_card_number", caption = "卡号", datetype = Types.VARCHAR)
	public CField access_card_number; // 卡号
	@CFieldinfo(fieldname = "access_card_seq", caption = "卡序列号", datetype = Types.VARCHAR)
	public CField access_card_seq; // 卡序列号
	@CFieldinfo(fieldname = "hiredday", caption = "入职日期", datetype = Types.TIMESTAMP)
	public CField hiredday; // 入职日期
	@CFieldinfo(fieldname = "orgid", caption = "机构ID", datetype = Types.INTEGER)
	public CField orgid; // 机构ID
	@CFieldinfo(fieldname = "orgname", caption = "机构名称", datetype = Types.VARCHAR)
	public CField orgname; // 机构名称
	@CFieldinfo(fieldname = "orgcode", caption = "机构编码", datetype = Types.VARCHAR)
	public CField orgcode; // 机构编码
	@CFieldinfo(fieldname = "extorgname", caption = "机构全称", datetype = Types.VARCHAR)
	public CField extorgname; // 机构全称
	@CFieldinfo(fieldname = "hwc_namezl", caption = "职类", datetype = Types.VARCHAR)
	public CField hwc_namezl; // 职类
	@CFieldinfo(fieldname = "lv_id", caption = "职级ID", datetype = Types.INTEGER)
	public CField lv_id; // 职级ID
	@CFieldinfo(fieldname = "lv_num", caption = "职级", datetype = Types.DECIMAL)
	public CField lv_num; // 职级
	@CFieldinfo(fieldname = "access_list_id", caption = "门禁ID", datetype = Types.INTEGER)
	public CField access_list_id; // 门禁ID
	@CFieldinfo(fieldname = "access_list_code", caption = "门禁编码", datetype = Types.VARCHAR)
	public CField access_list_code; // 门禁编码
	@CFieldinfo(fieldname = "access_list_model", caption = "门禁型号", datetype = Types.VARCHAR)
	public CField access_list_model; // 门禁型号
	@CFieldinfo(fieldname = "access_list_name", caption = "门禁名称", datetype = Types.VARCHAR)
	public CField access_list_name; // 门禁名称
	@CFieldinfo(fieldname = "deploy_area", caption = "门禁区域", datetype = Types.VARCHAR)
	public CField deploy_area; // 门禁区域
	@CFieldinfo(fieldname = "access_place", caption = "门禁位置", datetype = Types.VARCHAR)
	public CField access_place; // 门禁位置
	@CFieldinfo(fieldname = "aorgid", precision = 15, scale = 0, caption = "设备部门ID", datetype = Types.INTEGER)
	public CField aorgid; // 设备部门ID
	@CFieldinfo(fieldname = "aorgcode", precision = 32, scale = 0, caption = "设备部门编码", datetype = Types.VARCHAR)
	public CField aorgcode; // 设备部门编码
	@CFieldinfo(fieldname = "aextorgname", precision = 256, scale = 0, caption = "设备部门名称", datetype = Types.VARCHAR)
	public CField aextorgname; // 设备部门名称
	@CFieldinfo(fieldname = "access_status", caption = "状态", datetype = Types.VARCHAR)
	public CField access_status; // 状态
	@CFieldinfo(fieldname = "change_reason", caption = "申请原因", datetype = Types.VARCHAR)
	public CField change_reason; // 申请原因
	@CFieldinfo(fieldname = "accrediter", caption = "授权人", datetype = Types.VARCHAR)
	public CField accrediter; // 授权人
	@CFieldinfo(fieldname = "begin_date", caption = "授权开始时间", datetype = Types.TIMESTAMP)
	public CField begin_date; // 授权开始时间
	@CFieldinfo(fieldname = "end_date", caption = "授权结束时间", datetype = Types.TIMESTAMP)
	public CField end_date; // 授权结束时间
	@CFieldinfo(fieldname = "remarks", caption = "备注", datetype = Types.VARCHAR)
	public CField remarks; // 备注
	@CFieldinfo(fieldname = "orghrlev", precision = 1, scale = 0, caption = "机构人事层级", datetype = Types.INTEGER)
	public CField orghrlev; // 机构人事层级
	@CFieldinfo(fieldname = "wfid", caption = "wfid", datetype = Types.INTEGER)
	public CField wfid; // wfid
	@CFieldinfo(fieldname = "attid", caption = "attid", datetype = Types.INTEGER)
	public CField attid; // attid
	@CFieldinfo(fieldname = "stat", caption = "表单状态", datetype = Types.INTEGER)
	public CField stat; // 表单状态
	@CFieldinfo(fieldname = "idpath", caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	@CFieldinfo(fieldname = "entid", caption = "entid", datetype = Types.INTEGER)
	public CField entid; // entid
	@CFieldinfo(fieldname = "creator", caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	@CFieldinfo(fieldname = "attribute1", caption = "attribute1", datetype = Types.VARCHAR)
	public CField attribute1; // attribute1
	@CFieldinfo(fieldname = "attribute2", caption = "attribute2", datetype = Types.TIMESTAMP)
	public CField attribute2; // attribute2
	@CFieldinfo(fieldname = "attribute3", caption = "attribute3", datetype = Types.VARCHAR)
	public CField attribute3; // attribute3
	@CFieldinfo(fieldname = "attribute4", caption = "attribute4", datetype = Types.VARCHAR)
	public CField attribute4; // attribute4
	@CFieldinfo(fieldname = "attribute5", caption = "attribute5", datetype = Types.VARCHAR)
	public CField attribute5; // attribute5
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hr_access_special() throws Exception {
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
