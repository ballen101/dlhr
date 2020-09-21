package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hr_traintran_batchline extends CJPA {
	@CFieldinfo(fieldname = "ttranbl_id", iskey = true, notnull = true, caption = "批入行ID", datetype = Types.INTEGER)
	public CField ttranbl_id; // 批入行ID
	@CFieldinfo(fieldname = "ttranb_id", notnull = true, caption = "批入ID", datetype = Types.INTEGER)
	public CField ttranb_id; // 批入ID
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "人事档案ID", datetype = Types.INTEGER)
	public CField er_id; // 人事档案ID
	@CFieldinfo(fieldname = "employee_code", notnull = true, caption = "工号", datetype = Types.VARCHAR)
	public CField employee_code; // 工号
	@CFieldinfo(fieldname = "sex", notnull = true, caption = "性别", datetype = Types.INTEGER)
	public CField sex; // 性别
	@CFieldinfo(fieldname = "id_number", notnull = true, caption = "身份证号", datetype = Types.VARCHAR)
	public CField id_number; // 身份证号
	@CFieldinfo(fieldname = "employee_name", notnull = true, caption = "姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 姓名
	@CFieldinfo(fieldname = "degree", caption = "学历", datetype = Types.INTEGER)
	public CField degree; // 学历
	@CFieldinfo(fieldname = "hiredday", notnull = true, caption = "入职日期", datetype = Types.TIMESTAMP)
	public CField hiredday; // 入职日期
	@CFieldinfo(fieldname = "enddatetry", notnull = true, caption = "试用到期日期", datetype = Types.TIMESTAMP)
	public CField enddatetry; // 试用到期日期
	@CFieldinfo(fieldname = "jxdatetry", notnull = true, caption = "见习到期日期", datetype = Types.TIMESTAMP)
	public CField jxdatetry; // 见习到期日期
	@CFieldinfo(fieldname = "orgid", notnull = true, caption = "部门ID", datetype = Types.INTEGER)
	public CField orgid; // 部门ID
	@CFieldinfo(fieldname = "orgcode", notnull = true, caption = "部门编码", datetype = Types.VARCHAR)
	public CField orgcode; // 部门编码
	@CFieldinfo(fieldname = "orgname", notnull = true, caption = "部门名称", datetype = Types.VARCHAR)
	public CField orgname; // 部门名称
	@CFieldinfo(fieldname = "ospid", notnull = true, caption = "职位ID", datetype = Types.INTEGER)
	public CField ospid; // 职位ID
	@CFieldinfo(fieldname = "ospcode", notnull = true, caption = "职位编码", datetype = Types.VARCHAR)
	public CField ospcode; // 职位编码
	@CFieldinfo(fieldname = "sp_name", notnull = true, caption = "职位名称", datetype = Types.VARCHAR)
	public CField sp_name; // 职位名称
	@CFieldinfo(fieldname = "hg_name", caption = "职等", datetype = Types.VARCHAR)
	public CField hg_name; // 职等
	@CFieldinfo(fieldname = "lv_num", caption = "职级", datetype = Types.DECIMAL)
	public CField lv_num; // 职级
	@CFieldinfo(fieldname = "norgid", caption = "拟用人部门ID", datetype = Types.INTEGER)
	public CField norgid; // 拟用人部门ID
	@CFieldinfo(fieldname = "norgname", caption = "拟用人部门名称", datetype = Types.VARCHAR)
	public CField norgname; // 拟用人部门名称
	@CFieldinfo(fieldname = "nospid", caption = "拟职位ID", datetype = Types.INTEGER)
	public CField nospid; // 拟职位ID
	@CFieldinfo(fieldname = "nsp_name", caption = "拟职位名称", datetype = Types.VARCHAR)
	public CField nsp_name; // 拟职位名称
	@CFieldinfo(fieldname = "exam_title", caption = "考试课题", datetype = Types.VARCHAR)
	public CField exam_title; // 考试课题
	@CFieldinfo(fieldname = "exam_time", caption = "考试时间", datetype = Types.TIMESTAMP)
	public CField exam_time; // 考试时间
	@CFieldinfo(fieldname = "exam_score", caption = "考试分数", datetype = Types.DECIMAL)
	public CField exam_score; // 考试分数
	@CFieldinfo(fieldname = "psalary", caption = "转正前薪资", datetype = Types.DECIMAL)
	public CField psalary; // 转正前薪资
	@CFieldinfo(fieldname = "nsalary", caption = "转正后薪资", datetype = Types.DECIMAL)
	public CField nsalary; // 转正后薪资
	@CFieldinfo(fieldname = "sypj", caption = "试用期评价", datetype = Types.VARCHAR)
	public CField sypj; // 试用期评价
	@CFieldinfo(fieldname = "wfresult", caption = "评审结果1 通过 2 没通过", datetype = Types.INTEGER)
	public CField wfresult; // 评审结果1 通过 2 没通过
	@CFieldinfo(fieldname ="oldstru_id",precision=10,scale=0,caption="转正前工资结构ID",datetype=Types.INTEGER)
	public CField oldstru_id;  //转正前工资结构ID
	@CFieldinfo(fieldname ="oldstru_name",precision=32,scale=0,caption="转正前工资结构名",datetype=Types.VARCHAR)
	public CField oldstru_name;  //转正前工资结构名
	@CFieldinfo(fieldname ="oldposition_salary",precision=10,scale=2,caption="转正前职位工资",defvalue="0.00",datetype=Types.DECIMAL)
	public CField oldposition_salary;  //转正前职位工资
	@CFieldinfo(fieldname ="oldbase_salary",precision=10,scale=2,caption="转正前基本工资",defvalue="0.00",datetype=Types.DECIMAL)
	public CField oldbase_salary;  //转正前基本工资
	@CFieldinfo(fieldname ="oldtech_salary",precision=10,scale=2,caption="转正前技能工资",defvalue="0.00",datetype=Types.DECIMAL)
	public CField oldtech_salary;  //转正前技能工资
	@CFieldinfo(fieldname ="oldachi_salary",precision=10,scale=2,caption="转正前绩效工资",defvalue="0.00",datetype=Types.DECIMAL)
	public CField oldachi_salary;  //转正前绩效工资
	@CFieldinfo(fieldname ="oldotwage",precision=10,scale=2,caption="转正前固定加班工资",defvalue="0.00",datetype=Types.DECIMAL)
	public CField oldotwage;  //转正前固定加班工资
	@CFieldinfo(fieldname ="oldtech_allowance",precision=10,scale=2,caption="转正前技术津贴",defvalue="0.00",datetype=Types.DECIMAL)
	public CField oldtech_allowance;  //转正前技术津贴
	@CFieldinfo(fieldname ="oldpostsubs",precision=10,scale=2,caption="转正前岗位津贴",defvalue="0.00",datetype=Types.DECIMAL)
	public CField oldpostsubs;  //转正前岗位津贴
	@CFieldinfo(fieldname ="newstru_id",precision=10,scale=0,caption="转正后工资结构ID",datetype=Types.INTEGER)
	public CField newstru_id;  //转正后工资结构ID
	@CFieldinfo(fieldname ="newstru_name",precision=32,scale=0,caption="转正后工资结构名",datetype=Types.VARCHAR)
	public CField newstru_name;  //转正后工资结构名
	@CFieldinfo(fieldname ="newposition_salary",precision=10,scale=2,caption="转正后职位工资",defvalue="0.00",datetype=Types.DECIMAL)
	public CField newposition_salary;  //转正后职位工资
	@CFieldinfo(fieldname ="newbase_salary",precision=10,scale=2,caption="转正后基本工资",defvalue="0.00",datetype=Types.DECIMAL)
	public CField newbase_salary;  //转正后基本工资
	@CFieldinfo(fieldname ="newtech_salary",precision=10,scale=2,caption="转正后技能工资",defvalue="0.00",datetype=Types.DECIMAL)
	public CField newtech_salary;  //转正后技能工资
	@CFieldinfo(fieldname ="newachi_salary",precision=10,scale=2,caption="转正后绩效工资",defvalue="0.00",datetype=Types.DECIMAL)
	public CField newachi_salary;  //转正后绩效工资
	@CFieldinfo(fieldname ="newotwage",precision=10,scale=2,caption="转正后固定加班工资",defvalue="0.00",datetype=Types.DECIMAL)
	public CField newotwage;  //转正后固定加班工资
	@CFieldinfo(fieldname ="newtech_allowance",precision=10,scale=2,caption="转正后技术津贴",defvalue="0.00",datetype=Types.DECIMAL)
	public CField newtech_allowance;  //转正后技术津贴
	@CFieldinfo(fieldname ="newpostsubs",precision=10,scale=2,caption="转正后岗位津贴",defvalue="0.00",datetype=Types.DECIMAL)
	public CField newpostsubs;  //转正后岗位津贴
	@CFieldinfo(fieldname ="oldchecklev",precision=2,scale=0,caption="调薪前绩效考核层级",datetype=Types.INTEGER)
	public CField oldchecklev;  //调薪前绩效考核层级
	@CFieldinfo(fieldname ="oldattendtype",precision=32,scale=0,caption="调薪前出勤类别",datetype=Types.VARCHAR)
	public CField oldattendtype;  //调薪前出勤类别
	@CFieldinfo(fieldname ="newchecklev",precision=2,scale=0,caption="调薪后绩效考核层级",datetype=Types.INTEGER)
	public CField newchecklev;  //调薪后绩效考核层级
	@CFieldinfo(fieldname ="newattendtype",precision=32,scale=0,caption="调薪后出勤类别",defvalue="0",datetype=Types.VARCHAR)
	public CField newattendtype;  //调薪后出勤类别
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hr_traintran_batchline() throws Exception {
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
