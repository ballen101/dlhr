package com.hr.msg.entity;

import java.sql.Types;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
@CEntity()
public class Hr_kq_day_report extends CJPA {

	@CFieldinfo(fieldname="kqdr_id",iskey=true,notnull=true,caption="考勤日报ID",datetype=Types.INTEGER)
	public CField kqdr_id;  //考勤日报ID
	@CFieldinfo(fieldname="er_id",iskey=false,notnull=true,caption="员工ID",datetype=Types.INTEGER)
	public CField er_id;  // 员工ID
	@CFieldinfo(fieldname="employee_code",iskey=false,notnull=true,caption="工号",datetype=Types.VARCHAR)
	public CField employee_code;  // 工号
	@CFieldinfo(fieldname="employee_name",iskey=false,notnull=true,caption="姓名",datetype=Types.VARCHAR)
	public CField employee_name;  // 姓名
	@CFieldinfo(fieldname="no_card_num",iskey=false,notnull=true,caption="没打卡次数",datetype=Types.INTEGER)
	public CField no_card_num;  //没打卡次数
	@CFieldinfo(fieldname="yer_no_card_num",iskey=false,notnull=true,caption="昨天没打卡次数",datetype=Types.INTEGER)
	public CField yer_no_card_num;  //昨天没打卡次数
	@CFieldinfo(fieldname="yer_no_card_times",iskey=false,notnull=false,caption="昨天没打卡时段",datetype=Types.VARCHAR)
	public CField yer_no_card_times;  //昨天没打卡时段
	
	@CFieldinfo(fieldname="make_up_num",iskey=false,notnull=true,caption="已补卡次数",datetype=Types.INTEGER)
	public CField make_up_num;  //当月已补签次数
	
	@CFieldinfo(fieldname="date",iskey=false,notnull=true,caption="统计日期",datetype=Types.DATE)
	public CField date;  //统计日期

	public Hr_kq_day_report() throws Exception {
	
	}

}
