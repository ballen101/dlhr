package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hr_emptransferbatch_line extends CJPA {
	@CFieldinfo(fieldname = "emptranfbl_id", iskey = true, notnull = true, caption = "调动单行ID", datetype = Types.INTEGER)
	public CField emptranfbl_id; // 调动单行ID
	@CFieldinfo(fieldname = "emptranfb_id", notnull = true, caption = "调动单ID", datetype = Types.INTEGER)
	public CField emptranfb_id; // 调动单ID
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "人事ID", datetype = Types.INTEGER)
	public CField er_id; // 人事ID
	@CFieldinfo(fieldname = "employee_code", notnull = true, caption = "工号", datetype = Types.VARCHAR)
	public CField employee_code; // 工号
	@CFieldinfo(fieldname = "id_number", notnull = false, caption = "身份证号", datetype = Types.VARCHAR)
	public CField id_number; // 身份证号
	@CFieldinfo(fieldname = "employee_name", notnull = true, caption = "姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 姓名
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
	@CFieldinfo(fieldname = "probationdate", caption = "考察到期", datetype = Types.DATE)
	public CField probationdate; // 考察到期
	@CFieldinfo(fieldname = "odorgid", notnull = true, caption = "调动前部门ID", datetype = Types.INTEGER)
	public CField odorgid; // 调动前部门ID
	@CFieldinfo(fieldname = "odorgcode", notnull = true, caption = "调动前部门编码", datetype = Types.VARCHAR)
	public CField odorgcode; // 调动前部门编码
	@CFieldinfo(fieldname = "odorgname", notnull = true, caption = "调动前部门名称", datetype = Types.VARCHAR)
	public CField odorgname; // 调动前部门名称
	@CFieldinfo(fieldname = "odlv_id", caption = "调动前职级ID", datetype = Types.INTEGER)
	public CField odlv_id; // 调动前职级ID
	@CFieldinfo(fieldname = "odlv_num", caption = "调动前职级", datetype = Types.DECIMAL)
	public CField odlv_num; // 调动前职级
	@CFieldinfo(fieldname = "odhg_id", caption = "调动前职等ID", datetype = Types.INTEGER)
	public CField odhg_id; // 调动前职等ID
	@CFieldinfo(fieldname = "odhg_code", caption = "调动前职等编码", datetype = Types.VARCHAR)
	public CField odhg_code; // 调动前职等编码
	@CFieldinfo(fieldname = "odhg_name", caption = "调动前职等名称", datetype = Types.VARCHAR)
	public CField odhg_name; // 调动前职等名称
	@CFieldinfo(fieldname = "odospid", caption = "调动前职位ID", datetype = Types.INTEGER)
	public CField odospid; // 调动前职位ID
	@CFieldinfo(fieldname = "odospcode", caption = "调动前职位编码", datetype = Types.VARCHAR)
	public CField odospcode; // 调动前职位编码
	@CFieldinfo(fieldname = "odsp_name", caption = "调动前职位名称", datetype = Types.VARCHAR)
	public CField odsp_name; // 调动前职位名称
	@CFieldinfo(fieldname = "odattendtype", caption = "调动前出勤类别", datetype = Types.INTEGER)
	public CField odattendtype; // 调动前出勤类别
	@CFieldinfo(fieldname = "oldcalsalarytype", caption = "调动前计薪方式", datetype = Types.INTEGER)
	public CField oldcalsalarytype; // 调动前计薪方式
	@CFieldinfo(fieldname = "oldhwc_namezl", caption = "调动前职类", datetype = Types.VARCHAR)
	public CField oldhwc_namezl; // 调动前职类
	@CFieldinfo(fieldname = "oldzwxz", caption = "调动前职位性质", datetype = Types.VARCHAR)
	public CField oldzwxz; // 调动前职位性质
	@CFieldinfo(fieldname = "newzwxz", caption = "调动后职位性质", datetype = Types.VARCHAR)
	public CField newzwxz; // 调动后职位性质
	@CFieldinfo(fieldname = "newhwc_namezl", caption = "调动后职类", datetype = Types.VARCHAR)
	public CField newhwc_namezl; // 调动后职类
	@CFieldinfo(fieldname = "newcalsalarytype", caption = "调动后计薪方式", datetype = Types.VARCHAR)
	public CField newcalsalarytype; // 调动后计薪方式
	@CFieldinfo(fieldname = "newattendtype", caption = "调动后出勤类别", datetype = Types.INTEGER)
	public CField newattendtype; // 调动后出勤类别
	@CFieldinfo(fieldname = "newsp_name", caption = "调动后职位名称", datetype = Types.VARCHAR)
	public CField newsp_name; // 调动后职位名称
	@CFieldinfo(fieldname = "newospcode", caption = "调动后职位编码", datetype = Types.VARCHAR)
	public CField newospcode; // 调动后职位编码
	@CFieldinfo(fieldname = "newospid", caption = "调动后职位ID", datetype = Types.INTEGER)
	public CField newospid; // 调动后职位ID
	@CFieldinfo(fieldname = "newhg_code", caption = "调动后职等编码", datetype = Types.VARCHAR)
	public CField newhg_code; // 调动后职等编码
	@CFieldinfo(fieldname = "newhg_id", caption = "调动后职等ID", datetype = Types.INTEGER)
	public CField newhg_id; // 调动后职等ID
	@CFieldinfo(fieldname = "newlv_num", caption = "调动后职级", datetype = Types.DECIMAL)
	public CField newlv_num; // 调动后职级
	@CFieldinfo(fieldname = "newlv_id", caption = "调动后职级ID", datetype = Types.INTEGER)
	public CField newlv_id; // 调动后职级ID
	@CFieldinfo(fieldname = "neworgname", notnull = true, caption = "调动后部门名称", datetype = Types.VARCHAR)
	public CField neworgname; // 调动后部门名称
	@CFieldinfo(fieldname = "neworgcode", notnull = true, caption = "调动后部门编码", datetype = Types.VARCHAR)
	public CField neworgcode; // 调动后部门编码
	@CFieldinfo(fieldname = "neworgid", notnull = true, caption = "调动后部门ID", datetype = Types.INTEGER)
	public CField neworgid; // 调动后部门ID
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "newhg_name", caption = "调动后职等名称", datetype = Types.VARCHAR)
	public CField newhg_name; // 调动后职等名称
	@CFieldinfo(fieldname = "isexistsrl", caption = "关联关系 1 有 2 无", defvalue = "2", datetype = Types.INTEGER)
	public CField isexistsrl; // 关联关系 1 有 2 无
	@CFieldinfo(fieldname = "rlmgms", caption = "管控措施", defvalue = "1", datetype = Types.INTEGER)
	public CField rlmgms; // 管控措施
	@CFieldinfo(fieldname = "rccode", caption = "管控单编码", datetype = Types.VARCHAR)
	public CField rccode; // 管控单编码
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hr_emptransferbatch_line() throws Exception {
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
