package com.hr.recruit.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(tablename = "Hr_recruit_quachkline")
public class Hr_recruit_quachkline extends CJPA {
	@CFieldinfo(fieldname = "rqlid", iskey = true, notnull = true, precision = 10, scale = 0, caption = "行ID", datetype = Types.INTEGER)
	public CField rqlid; // 行ID
	@CFieldinfo(fieldname = "recruit_quachk_id", notnull = true, precision = 10, scale = 0, caption = "比对ID", datetype = Types.INTEGER)
	public CField recruit_quachk_id; // 比对ID
	@CFieldinfo(fieldname = "hiredday", notnull = true, precision = 19, scale = 0, caption = "入职日期", datetype = Types.TIMESTAMP)
	public CField hiredday; // 入职日期
	@CFieldinfo(fieldname = "id_number", notnull = true, precision = 32, scale = 0, caption = "身份证号", datetype = Types.VARCHAR)
	public CField id_number; // 身份证号
	@CFieldinfo(fieldname = "employee_name", notnull = true, precision = 64, scale = 0, caption = "姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 姓名
	@CFieldinfo(fieldname = "orgname", notnull = true, precision = 128, scale = 0, caption = "部门名称", datetype = Types.VARCHAR)
	public CField orgname; // 部门名称
	@CFieldinfo(fieldname = "empstatid", notnull = true, precision = 1, scale = 0, caption = "人员状态", datetype = Types.INTEGER)
	public CField empstatid; // 人员状态
	@CFieldinfo(fieldname = "dates", precision = 7, scale = 0, caption = "离职天数", datetype = Types.INTEGER)
	public CField dates; // 离职天数
	@CFieldinfo(fieldname = "ljdate", precision = 19, scale = 0, caption = "离职日期", datetype = Types.TIMESTAMP)
	public CField ljdate; // 离职日期
	@CFieldinfo(fieldname = "ljtype1", precision = 2, scale = 0, caption = "离职类别 自离 辞职 等", datetype = Types.INTEGER)
	public CField ljtype1; // 离职类别 自离 辞职 等
	@CFieldinfo(fieldname = "ljtype2", precision = 2, scale = 0, caption = "离职类型 主动离职 被动离职 协商离职", datetype = Types.INTEGER)
	public CField ljtype2; // 离职类型 主动离职 被动离职 协商离职
	@CFieldinfo(fieldname = "ljreason", precision = 128, scale = 0, caption = "离职原因", datetype = Types.VARCHAR)
	public CField ljreason; // 离职原因
	@CFieldinfo(fieldname = "leaveremark", precision = 512, scale = 0, caption = "备注", datetype = Types.VARCHAR)
	public CField leaveremark; // 备注
	@CFieldinfo(fieldname = "ljid", precision = 20, scale = 0, caption = "离职ID", datetype = Types.INTEGER)
	public CField ljid; // 离职ID
	@CFieldinfo(fieldname = "baid", precision = 20, scale = 0, caption = "加封ID", datetype = Types.INTEGER)
	public CField baid; // 加封ID
	@CFieldinfo(fieldname = "addtype", precision = 2, scale = 0, caption = "加封类型", datetype = Types.INTEGER)
	public CField addtype; // 加封类型
	@CFieldinfo(fieldname = "addtype1", precision = 2, scale = 0, caption = "加封类别", datetype = Types.INTEGER)
	public CField addtype1; // 加封类别
	@CFieldinfo(fieldname = "addnum", precision = 2, scale = 0, caption = "加封次数", datetype = Types.INTEGER)
	public CField addnum; // 加封次数
	@CFieldinfo(fieldname = "blackreason", precision = 128, scale = 0, caption = "加入黑名单原因", datetype = Types.VARCHAR)
	public CField blackreason; // 加入黑名单原因
	@CFieldinfo(fieldname = "addappdate", precision = 19, scale = 0, caption = "加封申请日期", datetype = Types.TIMESTAMP)
	public CField addappdate; // 加封申请日期
	@CFieldinfo(fieldname = "adddate", precision = 19, scale = 0, caption = "加封生效日期", datetype = Types.TIMESTAMP)
	public CField adddate; // 加封生效日期
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hr_recruit_quachkline() throws Exception {
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
