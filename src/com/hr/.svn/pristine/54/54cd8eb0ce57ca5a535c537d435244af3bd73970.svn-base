package com.hr.card.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import com.hr.attd.ctr.CtrHr_ykt_card;

import java.sql.Types;

@CEntity(controller = CtrHr_ykt_card.class)
public class Hr_ykt_card extends CJPA {
	@CFieldinfo(fieldname = "card_id", iskey = true, notnull = true, precision = 10, scale = 0, caption = "卡ID", datetype = Types.INTEGER)
	public CField card_id; // 卡ID
	@CFieldinfo(fieldname = "card_sn", precision = 20, scale = 0, caption = "卡序列号", datetype = Types.VARCHAR)
	public CField card_sn; // 卡序列号
	@CFieldinfo(fieldname = "card_number", precision = 20, scale = 0, caption = "卡号", datetype = Types.VARCHAR)
	public CField card_number; // 卡号
	@CFieldinfo(fieldname = "finger_mark_no", precision = 32, scale = 0, caption = "指纹登记号", datetype = Types.VARCHAR)
	public CField finger_mark_no; // 指纹登记号
	@CFieldinfo(fieldname = "card_stat", notnull = true, precision = 10, scale = 0, caption = "卡状态 1 正常 2 挂失 3 清卡 4 报废", datetype = Types.INTEGER)
	public CField card_stat; // 卡状态 1 正常 2 挂失 3 清卡 4 报废
	@CFieldinfo(fieldname = "effective_date", precision = 19, scale = 0, caption = "生效时间", datetype = Types.TIMESTAMP)
	public CField effective_date; // 生效时间
	@CFieldinfo(fieldname = "disable_date", precision = 19, scale = 0, caption = "失效时间", datetype = Types.TIMESTAMP)
	public CField disable_date; // 失效时间
	@CFieldinfo(fieldname = "er_id", notnull = false, precision = 20, scale = 0, caption = "人事ID", datetype = Types.INTEGER)
	public CField er_id; // 人事ID
	@CFieldinfo(fieldname = "er_code", precision = 16, scale = 0, caption = "档案编码", datetype = Types.VARCHAR)
	public CField er_code; // 档案编码
	@CFieldinfo(fieldname = "employee_code", notnull = false, precision = 16, scale = 0, caption = "工号", datetype = Types.VARCHAR)
	public CField employee_code; // 工号
	@CFieldinfo(fieldname = "employee_name", precision = 64, scale = 0, caption = "姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 姓名
	@CFieldinfo(fieldname = "orgid", notnull = false, precision = 10, scale = 0, caption = "部门ID", datetype = Types.INTEGER)
	public CField orgid; // 部门ID
	@CFieldinfo(fieldname = "orgcode", notnull = false, precision = 16, scale = 0, caption = "部门编码", datetype = Types.VARCHAR)
	public CField orgcode; // 部门编码
	@CFieldinfo(fieldname = "orgname", notnull = false, precision = 128, scale = 0, caption = "部门名称", datetype = Types.VARCHAR)
	public CField orgname; // 部门名称
	@CFieldinfo(fieldname = "sp_name", precision = 128, scale = 0, caption = "职位", datetype = Types.VARCHAR)
	public CField sp_name; // 职位
	@CFieldinfo(fieldname = "hwc_namezl", precision = 32, scale = 0, caption = "职类", datetype = Types.VARCHAR)
	public CField hwc_namezl; // 职类
	@CFieldinfo(fieldname = "hwc_namezq", precision = 64, scale = 0, caption = "职群", datetype = Types.VARCHAR)
	public CField hwc_namezq; // 职群
	@CFieldinfo(fieldname = "hwc_namezz", precision = 64, scale = 0, caption = "职种", datetype = Types.VARCHAR)
	public CField hwc_namezz; // 职种
	@CFieldinfo(fieldname = "hg_name", precision = 64, scale = 0, caption = "职等", datetype = Types.VARCHAR)
	public CField hg_name; // 职等
	@CFieldinfo(fieldname = "lv_num", precision = 4, scale = 1, caption = "职级", datetype = Types.DECIMAL)
	public CField lv_num; // 职级
	@CFieldinfo(fieldname = "remark", precision = 512, scale = 0, caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "entid", notnull = true, precision = 5, scale = 0, caption = "entid", datetype = Types.INTEGER)
	public CField entid; // entid
	@CFieldinfo(fieldname = "idpath", notnull = true, precision = 256, scale = 0, caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	@CFieldinfo(fieldname = "creator", notnull = true, precision = 32, scale = 0, caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", notnull = true, precision = 19, scale = 0, caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", precision = 32, scale = 0, caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", precision = 19, scale = 0, caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hr_ykt_card() throws Exception {
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
