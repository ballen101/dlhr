package com.hr.card.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.card.co.COHr_ykt_card_loss;

import java.sql.Types;

@CEntity(controller = COHr_ykt_card_loss.class)
public class Hr_ykt_card_loss extends CJPA {
	@CFieldinfo(fieldname = "card_loss_id", iskey = true, notnull = true, caption = "挂失单ID", datetype = Types.INTEGER)
	public CField card_loss_id; // 挂失单ID
	@CFieldinfo(fieldname = "card_loss_no", codeid = 114, caption = "挂失单号", datetype = Types.VARCHAR)
	public CField card_loss_no; // 挂失单号
	@CFieldinfo(fieldname = "card_id", caption = "卡ID", datetype = Types.INTEGER)
	public CField card_id; // 卡ID
	@CFieldinfo(fieldname = "card_sn", caption = "卡序列号", datetype = Types.VARCHAR)
	public CField card_sn; // 卡序列号
	@CFieldinfo(fieldname = "card_number", caption = "卡号", datetype = Types.VARCHAR)
	public CField card_number; // 卡号
	@CFieldinfo(fieldname = "card_stat", notnull = true, caption = "卡状态 1 正常 2 挂失 3 清卡 4 报废", datetype = Types.INTEGER)
	public CField card_stat; // 卡状态 1 正常 2 挂失 3 清卡 4 报废
	@CFieldinfo(fieldname = "employee_code", notnull = true, caption = "工号", datetype = Types.VARCHAR)
	public CField employee_code; // 工号
	@CFieldinfo(fieldname = "employee_name", caption = "姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 姓名
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "人事ID", datetype = Types.INTEGER)
	public CField er_id; // 人事ID
	@CFieldinfo(fieldname = "er_code", caption = "档案编码", datetype = Types.VARCHAR)
	public CField er_code; // 档案编码
	@CFieldinfo(fieldname = "orgid", notnull = true, caption = "部门ID", datetype = Types.INTEGER)
	public CField orgid; // 部门ID
	@CFieldinfo(fieldname = "orgcode", notnull = true, caption = "部门编码", datetype = Types.VARCHAR)
	public CField orgcode; // 部门编码
	@CFieldinfo(fieldname = "orgname", notnull = true, caption = "部门名称", datetype = Types.VARCHAR)
	public CField orgname; // 部门名称
	@CFieldinfo(fieldname = "sp_name", caption = "职位", datetype = Types.VARCHAR)
	public CField sp_name; // 职位
	@CFieldinfo(fieldname = "hwc_namezl", caption = "职类", datetype = Types.VARCHAR)
	public CField hwc_namezl; // 职类
	@CFieldinfo(fieldname = "hwc_namezq", caption = "职群", datetype = Types.VARCHAR)
	public CField hwc_namezq; // 职群
	@CFieldinfo(fieldname = "hwc_namezz", caption = "职种", datetype = Types.VARCHAR)
	public CField hwc_namezz; // 职种
	@CFieldinfo(fieldname = "hg_name", caption = "职等", datetype = Types.VARCHAR)
	public CField hg_name; // 职等
	@CFieldinfo(fieldname = "lv_num", caption = "职级", datetype = Types.DECIMAL)
	public CField lv_num; // 职级
	@CFieldinfo(fieldname = "loss_date", caption = "挂失时间", datetype = Types.TIMESTAMP)
	public CField loss_date; // 挂失时间
	@CFieldinfo(fieldname = "loss_reason", caption = "挂失原因", datetype = Types.VARCHAR)
	public CField loss_reason; // 挂失原因
	@CFieldinfo(fieldname = "disable_date", caption = "失效时间", datetype = Types.TIMESTAMP)
	public CField disable_date; // 失效时间
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
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
	@CFieldinfo(fieldname = "creator", notnull = true, caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", notnull = true, caption = "创建时间", datetype = Types.TIMESTAMP)
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

	public Hr_ykt_card_loss() throws Exception {
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
