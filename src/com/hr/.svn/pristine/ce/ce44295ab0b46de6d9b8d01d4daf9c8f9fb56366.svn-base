package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hr_emploanbatch_line extends CJPA {
	@CFieldinfo(fieldname = "loanlineid", iskey = true, notnull = true, caption = "行ID", datetype = Types.INTEGER)
	public CField loanlineid; // 行ID
	@CFieldinfo(fieldname = "loanid", notnull = true, caption = "借调单ID", datetype = Types.INTEGER)
	public CField loanid; // 借调单ID
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "人事ID", datetype = Types.INTEGER)
	public CField er_id; // 人事ID
	@CFieldinfo(fieldname = "employee_code", notnull = true, caption = "工号", datetype = Types.VARCHAR)
	public CField employee_code; // 工号
	@CFieldinfo(fieldname = "id_number", notnull = true, caption = "身份证号", datetype = Types.VARCHAR)
	public CField id_number; // 身份证号
	@CFieldinfo(fieldname = "employee_name", notnull = true, caption = "姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 姓名
	@CFieldinfo(fieldname = "sex", caption = "性别", datetype = Types.INTEGER)
	public CField sex; // 性别
	@CFieldinfo(fieldname = "mnemonic_code", caption = "助记码", datetype = Types.VARCHAR)
	public CField mnemonic_code; // 助记码
	@CFieldinfo(fieldname = "email", caption = "邮箱/微信", datetype = Types.VARCHAR)
	public CField email; // 邮箱/微信
	@CFieldinfo(fieldname = "empstatid", caption = "人员状态", datetype = Types.INTEGER)
	public CField empstatid; // 人员状态
	@CFieldinfo(fieldname = "telphone", caption = "电话", datetype = Types.VARCHAR)
	public CField telphone; // 电话
	@CFieldinfo(fieldname = "hiredday", caption = "聘用日期", datetype = Types.TIMESTAMP)
	public CField hiredday; // 聘用日期
	@CFieldinfo(fieldname = "degree", caption = "学历", datetype = Types.INTEGER)
	public CField degree; // 学历
	@CFieldinfo(fieldname = "probation", caption = "考察期", datetype = Types.INTEGER)
	public CField probation; // 考察期
	@CFieldinfo(fieldname = "odorgid", notnull = true, caption = "借调前部门ID", datetype = Types.INTEGER)
	public CField odorgid; // 借调前部门ID
	@CFieldinfo(fieldname = "odorgcode", notnull = true, caption = "借调前部门编码", datetype = Types.VARCHAR)
	public CField odorgcode; // 借调前部门编码
	@CFieldinfo(fieldname = "odorgname", notnull = true, caption = "借调前部门名称", datetype = Types.VARCHAR)
	public CField odorgname; // 借调前部门名称
	@CFieldinfo(fieldname = "odlv_id", caption = "借调前职级ID", datetype = Types.INTEGER)
	public CField odlv_id; // 借调前职级ID
	@CFieldinfo(fieldname = "odlv_num", caption = "借调前职级", datetype = Types.DECIMAL)
	public CField odlv_num; // 借调前职级
	@CFieldinfo(fieldname = "odhg_id", caption = "借调前职等ID", datetype = Types.INTEGER)
	public CField odhg_id; // 借调前职等ID
	@CFieldinfo(fieldname = "odhg_code", caption = "借调前职等编码", datetype = Types.VARCHAR)
	public CField odhg_code; // 借调前职等编码
	@CFieldinfo(fieldname = "odhg_name", caption = "借调前职等名称", datetype = Types.VARCHAR)
	public CField odhg_name; // 借调前职等名称
	@CFieldinfo(fieldname = "odospid", caption = "借调前职位ID", datetype = Types.INTEGER)
	public CField odospid; // 借调前职位ID
	@CFieldinfo(fieldname = "odospcode", caption = "借调前职位编码", datetype = Types.VARCHAR)
	public CField odospcode; // 借调前职位编码
	@CFieldinfo(fieldname = "odsp_name", caption = "借调前职位名称", datetype = Types.VARCHAR)
	public CField odsp_name; // 借调前职位名称
	@CFieldinfo(fieldname = "odattendtype", caption = "借调前出勤类别", datetype = Types.INTEGER)
	public CField odattendtype; // 借调前出勤类别
	@CFieldinfo(fieldname = "oldcalsalarytype", caption = "借调前计薪方式", datetype = Types.INTEGER)
	public CField oldcalsalarytype; // 借调前计薪方式
	@CFieldinfo(fieldname = "oldhwc_namezl", caption = "借调前职类", datetype = Types.VARCHAR)
	public CField oldhwc_namezl; // 借调前职类
	@CFieldinfo(fieldname = "neworgid", notnull = false, caption = "借调后部门ID", datetype = Types.INTEGER)
	public CField neworgid; // 借调后部门ID
	@CFieldinfo(fieldname = "neworgcode", notnull = false, caption = "借调后部门编码", datetype = Types.VARCHAR)
	public CField neworgcode; // 借调后部门编码
	@CFieldinfo(fieldname = "neworgname", notnull = false, caption = "借调后部门名称", datetype = Types.VARCHAR)
	public CField neworgname; // 借调后部门名称
	@CFieldinfo(fieldname = "note", caption = "备注", datetype = Types.VARCHAR)
	public CField note; // 备注
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hr_emploanbatch_line() throws Exception {
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
