package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import com.hr.perm.ctr.CtrHr_employee_reward;

import java.sql.Types;

@CEntity(controller = CtrHr_employee_reward.class)
public class Hr_employee_reward extends CJPA {
	@CFieldinfo(fieldname = "emprw_id", iskey = true, notnull = true, caption = "激励ID", datetype = Types.INTEGER)
	public CField emprw_id; // 激励ID
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "员工档案ID", datetype = Types.INTEGER)
	public CField er_id; // 员工档案ID
	@CFieldinfo(fieldname = "rewardtime", notnull = true, caption = "激励生效日期", datetype = Types.TIMESTAMP)
	public CField rewardtime; // 激励生效日期
	@CFieldinfo(fieldname = "rreason", notnull = false, caption = "激励事由", datetype = Types.VARCHAR)
	public CField rreason; // 激励事由
	@CFieldinfo(fieldname = "rwtype", notnull = true, caption = "激励类型", datetype = Types.INTEGER)
	public CField rwtype; // 激励类型
	@CFieldinfo(fieldname = "rwnature", caption = "激励性质", datetype = Types.INTEGER)
	public CField rwnature; // 激励性质 1 正 2 负
	@CFieldinfo(fieldname = "rwamount", caption = "激励金额", datetype = Types.DECIMAL)
	public CField rwamount; // 激励金额
	@CFieldinfo(fieldname = "rwscore", caption = "激励绩效系数", datetype = Types.DECIMAL)
	public CField rwscore; // 激励绩效系数
	@CFieldinfo(fieldname = "rwfile", caption = "激励支持文件", datetype = Types.VARCHAR)
	public CField rwfile; // 激励支持文件
	@CFieldinfo(fieldname = "rwnote", caption = "激励情况描述", datetype = Types.VARCHAR)
	public CField rwnote; // 激励情况描述
	@CFieldinfo(fieldname = "rwfilenum", caption = "激励文件字号", datetype = Types.VARCHAR)
	public CField rwfilenum; // 激励文件字号
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "creator", notnull = false, caption = "制单人", datetype = Types.VARCHAR)
	public CField creator; // 制单人
	@CFieldinfo(fieldname = "createtime", notnull = false, caption = "制单时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 制单时间
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

	public Hr_employee_reward() throws Exception {
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
