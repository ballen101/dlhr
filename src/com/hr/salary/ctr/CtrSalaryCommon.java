package com.hr.salary.ctr;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.util.HttpTookit;
import com.hr.attd.ctr.HrkqUtil;
import com.hr.base.entity.Hr_orgposition;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_employee_transfer;
import com.hr.perm.entity.Hr_empptjob_app;
import com.hr.perm.entity.Hr_entry;
import com.hr.perm.entity.Hr_entry_prob;
import com.hr.perm.entity.Hr_train_reg;
import com.hr.perm.entity.Hr_traindisp_batch;
import com.hr.perm.entity.Hr_traindisp_batchline;
import com.hr.perm.entity.Hr_trainentry_batch;
import com.hr.perm.entity.Hr_trainentry_batchline;
import com.hr.perm.entity.Hr_traintran_batch;
import com.hr.perm.entity.Hr_traintran_batchline;
import com.hr.perm.entity.Hr_transfer_prob;
import com.hr.salary.entity.Hr_salary_chgbill;
import com.hr.salary.entity.Hr_salary_chglg;
import com.hr.salary.entity.Hr_salary_postsub;
import com.hr.salary.entity.Hr_salary_postsub_line;
import com.hr.salary.entity.Hr_salary_specchgsa;
import com.hr.salary.entity.Hr_salary_specchgsa_batch;
import com.hr.salary.entity.Hr_salary_specchgsa_batch_line;
import com.hr.salary.entity.Hr_salary_structure;
import com.hr.salary.entity.Hr_salary_techsub;
import com.hr.salary.entity.Hr_salary_techsub_line;
import com.hr.salary.entity.Hr_salary_yearraise;
import com.hr.salary.entity.Hr_salary_yearraise_line;
import com.hr.util.HRUtil;

import net.sf.json.JSONObject;

public class CtrSalaryCommon {

	/**
	 * 薪资额度新接口
	 * 
	 * @param salary_quotacode
	 * @return
	 * @throws Exception
	 */
//	public static JSONObject getWageSys(String salary_quotacode) throws Exception {
//		String HRWAGE_SERVER_URL = HrkqUtil.getParmValueErr("HRWAGE_SERVER_URL");// 薪资额度接口地址
//		HttpTookit ht = new HttpTookit();
//		JSONObject jo = new JSONObject();
//		String url = HRWAGE_SERVER_URL;
//		buildSarrayCodeReq(jo, salary_quotacode);// 构造请求数据包
//		String rst = ht.doPostJSON(url, jo, "UTF-8");
//		JSONObject jr = JSONObject.fromObject(rst);
//		// 解析返回数据包
//		if (jr.containsKey("SvcHdr") && jr.getJSONObject("SvcHdr").containsKey("RCODE")) {
//			String r = jr.getJSONObject("SvcHdr").getString("RCODE");
//			if (!"S".equalsIgnoreCase(r)) {
//				throw new Exception(jr.getJSONObject("SvcHdr").getString("RDESC"));
//			}
//			JSONObject jwi = jr.getJSONObject("AppBody").getJSONObject("WageQuotaInfo_ITEM");
//			JSONObject jrst = new JSONObject();
//			jrst.put("wage", jwi.getString("Wage"));
//			jrst.put("usedwage", jwi.getString("UsedWage"));
//			jrst.put("balance", jwi.getString("Balance"));
//			jrst.put("money", jwi.getString("Money"));
//			return jrst;
//		} else
//			throw new Exception("返回数据格式错误:" + rst);
//
//	
//	}
	/**
	 * 获取编码额度
	 * @param salary_quotacode 编码
	 * @param employee_code   工号
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getWageSys(String salary_quotacode,String employee_code) throws Exception {
		//String HRWAGE_SERVER_URL = HrkqUtil.getParmValueErr("HRWAGE_SERVER_URL");// 薪资额度接口地址
		HttpTookit ht = new HttpTookit();
		String url = "http://192.168.117.122:8888/api/limit/prove/GetOrderNumber";
		//String url = "http://192.168.117.122:8888/api/limit/prove/GetOrderNumber";
		Map<String, String>params=new HashMap<String, String>();
		params.put("ordernumber", salary_quotacode);
		params.put("staffnumber", employee_code);
		String rst = ht.doGet(url, params);
		JSONObject jr = JSONObject.fromObject(rst);
		Logsw.dblog(jr.toString());
		// 解析返回数据包
		if (jr.containsKey("ordernumber") ) {
			JSONObject jrst = new JSONObject();
			jrst.put("wage", jr.getString("available"));//标准工资额度
			jrst.put("usedwage", jr.getString("used"));//已用工资额度
			jrst.put("balance", jr.getString("surplus"));//可用工资额度
			jrst.put("money", jr.getString("salary"));//申请使用额度
			return jrst;
		} else
			throw new Exception("返回数据格式错误:" + rst);
	}
	/**
	 * 流程结束后更新编码状态
	 * @param salary_quotacode
	 */
	public static void updateWageState(String salary_quotacode,String employee_code){
		HttpTookit ht = new HttpTookit();
		//String url = "http://192.168.117.122:8888/api/limit/prove/UpdateOrderStatus";
		String url = "http://192.168.117.122:8888/api/limit/prove/UpdateOrderStatus";
		Map<String, String>params=new HashMap<String, String>();
		params.put("ordernumber", salary_quotacode);
		params.put("staffnumber", employee_code);
		String rst = ht.doGet(url, params);
		JSONObject jr = JSONObject.fromObject(rst);
		if (jr.containsKey("RCODE") ) {
			if("Y".equals(jr.getString("RCODE"))){
				Logsw.dblog("调薪编码成功更新了");
			}else{
				Logsw.dblog("调薪编码更新失败了");
			}
		}
	}
	private static void buildSarrayCodeReq(JSONObject jo, String salary_quotacode) {
		JSONObject SvcHdr = new JSONObject();
		SvcHdr.put("SOURCEID", "HRMS");
		SvcHdr.put("DESTINATIONID", "EIP");
		SvcHdr.put("TYPE", "SELECT");
		SvcHdr.put("IPADDRESS", "127.0.0.1");
		SvcHdr.put("BO", "工资额度信息查询");
		jo.put("SvcHdr", SvcHdr);
		JSONObject AppBody = new JSONObject();
		AppBody.put("DocNo", salary_quotacode);
		jo.put("AppBody", AppBody);
	}

	public static Hr_salary_chglg getCur_salary_chglg(String er_id) throws Exception {
		String sqlstr = "SELECT * FROM hr_salary_chglg WHERE er_id=" + er_id + " AND useable=1 ORDER BY chgdate DESC";
		Hr_salary_chglg sc = new Hr_salary_chglg();
		sc.findBySQL(sqlstr, false);
		return sc;
	}

	/**
	 * 入职薪资变动
	 * 
	 * @param con
	 * @param entry
	 * @throws Exception
	 */
	public static void newSalaryChangeLog(CDBConnection con, Hr_entry entry) throws Exception {
		// TODO Auto-generated method stub
		String sqlstr = "SELECT * FROM hr_salary_chgbill WHERE scatype=1 AND stype=3 AND sid=" + entry.entry_id.getValue();
		Hr_salary_chgbill cb = new Hr_salary_chgbill();
		cb.findBySQL(con, sqlstr, false);
		if (cb.isEmpty())
			return;
		Hr_salary_chglg sc = new Hr_salary_chglg();

		sc.assignfield(cb, true);
		sc.createtime.setValue(null);
		sc.creator.setValue(null);
		sc.useable.setValue(1);
		sc.orgid.setValue(entry.orgid.getValue()); // 机构id
		sc.lv_num.setValue(entry.lv_num.getValue()); // 职级
		sc.ospid.setValue(entry.ospid.getValue()); // 职位id
		Hr_orgposition op=new Hr_orgposition();
		op.findByID(entry.ospid.getValue());
		if(!op.isEmpty()){
			sc.orgname.setValue(op.orgname.getValue()); // 机构
			sc.sp_name.setValue(op.sp_name.getValue()); // 职位
		}
		sc.save(con);

		// Hr_employee emp = new Hr_employee();
		// emp.findByID(entry.er_id.getValue());
		// Hr_salary_structure stuc = new Hr_salary_structure();
		// stuc.findByID(entry.stru_id.getValue());
		// if (stuc.isEmpty())
		// throw new Exception("工资结构【" + entry.stru_id.getValue() + "】不存在");
		//
		//
		// sc.er_id.setValue(entry.er_id.getValue());
		// sc.scatype.setValue(1);// 1 入职定薪、2 调动核薪、3 转正调薪、4 年度调薪、5 特殊调薪
		// sc.stype.setValue(3); // 来源类别 1 历史数据、2 实习登记、3 入职表单、4 调动表单、5 入职转正、6 调动转正....
		// sc.sid.setValue(entry.entry_id.getValue()); // 来源ID
		// sc.scode.setValue(entry.entry_code.getValue()); // 来源单号
		// sc.oldstru_id.setValue(null); // 调薪前工资结构ID
		// sc.oldstru_name.setValue(null); // 调薪前工资结构名
		// sc.oldchecklev.setValue(null);// 调薪前绩效考核层级
		// sc.oldattendtype.setValue(null); // 调薪前出勤类别
		// sc.oldcalsalarytype.setValue(null); // 调薪前计薪方式
		// sc.oldposition_salary.setValue(0); // 调薪前职位工资
		// sc.oldbase_salary.setValue(0); // 调薪前基本工资
		// sc.oldtech_salary.setValue(0); // 调薪前技能工资
		// sc.oldachi_salary.setValue(0); // 调薪前绩效工资
		// sc.oldotwage.setValue(0); // 调薪前固定加班工资
		// sc.oldtech_allowance.setValue(0); // 调薪前技术津贴
		// sc.oldparttimesubs.setValue(0); // 调薪前兼职津贴
		// sc.oldpostsubs.setValue(0); // 调薪前岗位津贴
		// sc.oldavg_salary.setValue(0); // 调薪前平均工资
		// sc.newstru_id.setValue(entry.stru_id.getValue()); // 调薪后工资结构ID
		// sc.newstru_name.setValue(entry.stru_name.getValue()); // 调薪后工资结构名
		// sc.newchecklev.setValue(stuc.checklev.getValue());// 调薪后绩效考核层级
		// sc.newattendtype.setValue(emp.atdtype.getValue()); // 调薪后出勤类别
		// sc.newcalsalarytype.setValue(emp.pay_way.getValue()); // 调薪后计薪方式
		// sc.newposition_salary.setValue(entry.position_salary.getValue()); // 调薪后职位工资
		// sc.newbase_salary.setValue(entry.base_salary.getValue()); // 调薪后基本工资
		// sc.newtech_salary.setValue(entry.tech_salary.getValue()); // 调薪后技能工资
		// sc.newachi_salary.setValue(entry.achi_salary.getValue()); // 调薪后绩效工资
		// sc.newotwage.setValue(entry.otwage.getValue()); // 调薪后固定加班工资
		// sc.newtech_allowance.setValue(entry.tech_allowance.getValue()); // 调薪后技术津贴
		// sc.newparttimesubs.setValue(0); // 调薪后兼职津贴
		// sc.newpostsubs.setValue(0); // 调薪后岗位津贴
		// sc.sacrage.setValue(0); // 调薪幅度
		// sc.chgdate.setValue(emp.hiredday.getValue()); // 调薪日期
		// sc.chgreason.setValue("入职定薪"); // 调薪原因
	}

	/**
	 * 入职转正薪资变动;
	 * 增量
	 * 
	 * @param con
	 * @param ep
	 * @throws Exception
	 */
	public static void newSalaryChangeLog(CDBConnection con, Hr_entry_prob ep) throws Exception {
		String sqlstr = "SELECT * FROM hr_salary_chgbill WHERE scatype=3 AND stype=5 AND sid=" + ep.pbtid.getValue();
		Hr_salary_chgbill cb = new Hr_salary_chgbill();
		cb.findBySQL(con, sqlstr, false);
		if (cb.isEmpty())
			return;
		Hr_salary_chglg sc = new Hr_salary_chglg();
		sc.assignfield(cb, true);
		sc.createtime.setValue(null);
		sc.creator.setValue(null);
		sc.useable.setValue(1);

		Hr_salary_chglg scold = getCur_salary_chglg(ep.er_id.getValue());// 获取现在薪资情况
		sc.oldstru_id.setValue(scold.newstru_id.getValue()); // 调薪前工资结构ID
		sc.oldstru_name.setValue(scold.newstru_name.getValue()); // 调薪前工资结构名
		sc.oldchecklev.setValue(scold.newchecklev.getValue());// 调薪前绩效考核层级
		sc.oldattendtype.setValue(scold.newattendtype.getValue()); // 调薪前出勤类别
		sc.oldcalsalarytype.setValue(scold.newcalsalarytype.getValue()); // 调薪前计薪方式
		sc.oldposition_salary.setValue(scold.newposition_salary.getValue()); // 调薪前职位工资
		sc.oldbase_salary.setValue(scold.newbase_salary.getValue()); // 调薪前基本工资
		sc.oldtech_salary.setValue(scold.newtech_salary.getValue()); // 调薪前技能工资
		sc.oldachi_salary.setValue(scold.newachi_salary.getValue()); // 调薪前绩效工资
		sc.oldotwage.setValue(scold.newotwage.getValue()); // 调薪前固定加班工资
		sc.oldtech_allowance.setValue(scold.newtech_allowance.getValue()); // 调薪前技术津贴
		sc.oldparttimesubs.setValue(scold.newparttimesubs.getValue()); // 调薪前兼职津贴
		sc.oldpostsubs.setValue(scold.newpostsubs.getValue()); // 调薪前岗位津贴
		sc.oldavg_salary.setValue(0); // 调薪前平均工资
		sc.orgid.setValue(ep.orgid.getValue()); // 机构id
		sc.orgname.setValue(ep.orgname.getValue()); // 机构
		sc.lv_num.setValue(ep.lv_num.getValue()); // 职级
		sc.ospid.setValue(ep.ospid.getValue()); // 职位id
		sc.sp_name.setValue(ep.sp_name.getValue()); // 职位
		sc.save(con);

		// Hr_salary_chglg sc = new Hr_salary_chglg();
		// sc.er_id.setValue(ep.er_id.getValue());
		// sc.scatype.setValue(3);// 1 入职定薪、2 调动核薪、3 转正调薪、4 年度调薪、5 特殊调薪
		// sc.stype.setValue(5); // 来源类别 1 历史数据、2 实习登记、3 入职表单、4 调动表单、5 入职转正、6 调动转正....
		// sc.sid.setValue(ep.pbtid.getValue()); // 来源ID
		// sc.scode.setValue(ep.pbtcode.getValue()); // 来源单号
		//
		// sc.oldstru_id.setValue(scold.newstru_id.getValue()); // 调薪前工资结构ID
		// sc.oldstru_name.setValue(scold.newstru_name.getValue()); // 调薪前工资结构名
		// sc.oldchecklev.setValue(scold.newchecklev.getValue());// 调薪前绩效考核层级
		// sc.oldattendtype.setValue(scold.newattendtype.getValue()); // 调薪前出勤类别
		// sc.oldcalsalarytype.setValue(scold.newcalsalarytype.getValue()); // 调薪前计薪方式
		// sc.oldposition_salary.setValue(scold.newposition_salary.getValue()); // 调薪前职位工资
		// sc.oldbase_salary.setValue(scold.newbase_salary.getValue()); // 调薪前基本工资
		// sc.oldtech_salary.setValue(scold.newtech_salary.getValue()); // 调薪前技能工资
		// sc.oldachi_salary.setValue(scold.newachi_salary.getValue()); // 调薪前绩效工资
		// sc.oldotwage.setValue(scold.newotwage.getValue()); // 调薪前固定加班工资
		// sc.oldtech_allowance.setValue(scold.newtech_allowance.getValue()); // 调薪前技术津贴
		// sc.oldparttimesubs.setValue(scold.newparttimesubs.getValue()); // 调薪前兼职津贴
		// sc.oldpostsubs.setValue(scold.newpostsubs.getValue()); // 调薪前岗位津贴
		// sc.oldavg_salary.setValue(0); // 调薪前平均工资
		//
		// sc.newstru_id.setValue(ep.newstru_id.getValue()); // 调薪后工资结构ID
		// sc.newstru_name.setValue(ep.newstru_name.getValue()); // 调薪后工资结构名
		// sc.newchecklev.setValue(ep.newchecklev.getValue());// 调薪后绩效考核层级
		// sc.newattendtype.setValue(ep.newattendtype.getValue()); // 调薪后出勤类别
		// sc.newcalsalarytype.setValue(null); // 调薪后计薪方式
		// sc.newposition_salary.setValue(scold.newposition_salary.getAsFloatDefault(0) + ep.chgposition_salary.getAsFloatDefault(0)); // 调薪后职位工资
		// sc.newbase_salary.setValue(scold.newbase_salary.getAsFloatDefault(0) + ep.chgbase_salary.getAsFloatDefault(0)); // 调薪后基本工资
		// sc.newtech_salary.setValue(scold.newtech_salary.getAsFloatDefault(0) + ep.chgtech_salary.getAsFloatDefault(0)); // 调薪后技能工资
		// sc.newachi_salary.setValue(scold.newachi_salary.getAsFloatDefault(0) + ep.chgachi_salary.getAsFloatDefault(0)); // 调薪后绩效工资
		// sc.newotwage.setValue(scold.newotwage.getAsFloatDefault(0) + ep.chgotwage.getAsFloatDefault(0)); // 调薪后固定加班工资
		// sc.newtech_allowance.setValue(scold.newtech_allowance.getAsFloatDefault(0) + ep.chgtech_allowance.getAsFloatDefault(0)); // 调薪后技术津贴
		// sc.newparttimesubs.setValue(scold.newparttimesubs.getAsFloatDefault(0)); // 调薪后兼职津贴
		// sc.newpostsubs.setValue(scold.newpostsubs.getAsFloatDefault(0)); // 调薪后岗位津贴
		// sc.sacrage.setValue(ep.pbtsarylev.getAsFloatDefault(0)); // 调薪幅度
		// sc.chgdate.setValue(ep.salarydate.getValue()); // 调薪日期
		// sc.chgreason.setValue("入职转正调薪"); // 调薪原因
	}

	/**
	 * 调动单薪资变动记录
	 * 
	 * @param con
	 * @param tr
	 * @throws Exception
	 */
	public static void newSalaryChangeLog(CDBConnection con, Hr_employee_transfer tr) throws Exception {

		String sqlstr = "SELECT * FROM hr_salary_chgbill WHERE scatype=2 AND stype=4 AND sid=" + tr.emptranf_id.getValue();
		Hr_salary_chgbill cb = new Hr_salary_chgbill();
		cb.findBySQL(con, sqlstr, false);
		if (cb.isEmpty())
			return;
		Hr_salary_chglg sc = new Hr_salary_chglg();
		sc.assignfield(cb, true);
		sc.createtime.setValue(null);
		sc.creator.setValue(null);
		sc.useable.setValue(1);

		Hr_salary_chglg scold = getCur_salary_chglg(tr.er_id.getValue());// 获取现在薪资情况
		sc.oldstru_id.setValue(scold.newstru_id.getValue()); // 调薪前工资结构ID
		sc.oldstru_name.setValue(scold.newstru_name.getValue()); // 调薪前工资结构名
		sc.oldchecklev.setValue(scold.newchecklev.getValue());// 调薪前绩效考核层级
		sc.oldattendtype.setValue(scold.newattendtype.getValue()); // 调薪前出勤类别
		sc.oldcalsalarytype.setValue(scold.newcalsalarytype.getValue()); // 调薪前计薪方式
		sc.oldposition_salary.setValue(scold.newposition_salary.getValue()); // 调薪前职位工资
		sc.oldbase_salary.setValue(scold.newbase_salary.getValue()); // 调薪前基本工资
		sc.oldtech_salary.setValue(scold.newtech_salary.getValue()); // 调薪前技能工资
		sc.oldachi_salary.setValue(scold.newachi_salary.getValue()); // 调薪前绩效工资
		sc.oldotwage.setValue(scold.newotwage.getValue()); // 调薪前固定加班工资
		sc.oldtech_allowance.setValue(scold.newtech_allowance.getValue()); // 调薪前技术津贴
		sc.oldparttimesubs.setValue(scold.newparttimesubs.getValue()); // 调薪前兼职津贴
		sc.oldpostsubs.setValue(scold.newpostsubs.getValue()); // 调薪前岗位津贴
		sc.oldavg_salary.setValue(tr.oldavg_salary.getValue()); // 调薪前平均工资

		// Hr_salary_chglg sc = new Hr_salary_chglg();
		// sc.er_id.setValue(tr.er_id.getValue());
		// sc.scatype.setValue(2);// 1 入职定薪、2 调动核薪、3 转正调薪、4 年度调薪、5 特殊调薪
		// sc.stype.setValue(4); // 来源类别 1 历史数据、2 实习登记、3 入职表单、4 调动表单、5 入职转正、6 调动转正....
		// sc.sid.setValue(tr.emptranf_id.getValue()); // 来源ID
		// sc.scode.setValue(tr.emptranfcode.getValue()); // 来源单号
		//
		// sc.newstru_id.setValue(tr.newstru_id.getValue()); // 调薪后工资结构ID
		// sc.newstru_name.setValue(tr.newstru_name.getValue()); // 调薪后工资结构名
		// sc.newchecklev.setValue(tr.newchecklev.getValue());// 调薪后绩效考核层级
		// sc.newattendtype.setValue(tr.newattendtype.getValue()); // 调薪后出勤类别
		// sc.newcalsalarytype.setValue(null); // 调薪后计薪方式
		// sc.newposition_salary.setValue(scold.newposition_salary.getAsFloatDefault(0) + tr.chgposition_salary.getAsFloatDefault(0)); // 调薪后职位工资
		// sc.newbase_salary.setValue(scold.newbase_salary.getAsFloatDefault(0) + tr.chgbase_salary.getAsFloatDefault(0)); // 调薪后基本工资
		// sc.newtech_salary.setValue(scold.newtech_salary.getAsFloatDefault(0) + tr.chgtech_salary.getAsFloatDefault(0)); // 调薪后技能工资
		// sc.newachi_salary.setValue(scold.newachi_salary.getAsFloatDefault(0) + tr.chgachi_salary.getAsFloatDefault(0)); // 调薪后绩效工资
		// sc.newotwage.setValue(scold.newotwage.getAsFloatDefault(0) + tr.chgotwage.getAsFloatDefault(0)); // 调薪后固定加班工资
		// sc.newtech_allowance.setValue(scold.newtech_allowance.getAsFloatDefault(0) + tr.chgtech_allowance.getAsFloatDefault(0)); // 调薪后技术津贴
		// sc.newparttimesubs.setValue(scold.newparttimesubs.getAsFloatDefault(0)); // 调薪后兼职津贴
		// sc.newpostsubs.setValue(scold.newpostsubs.getAsFloatDefault(0)); // 调薪后岗位津贴
		float sacrage = tr.chgposition_salary.getAsFloatDefault(0) + tr.chgtech_allowance.getAsFloatDefault(0);
		sc.sacrage.setValue(sacrage); // 调薪幅度
		sc.chgdate.setValue(tr.salarydate.getValue()); // 调薪日期
		sc.chgreason.setValue("调动调薪"); // 调薪原因
		
		sc.orgid.setValue(tr.neworgid.getValue()); // 机构id
		sc.orgname.setValue(tr.neworgname.getValue()); // 机构
		sc.lv_num.setValue(tr.newlv_num.getValue()); // 职级
		sc.ospid.setValue(tr.newospid.getValue()); // 职位id
		sc.sp_name.setValue(tr.newsp_name.getValue()); // 职位
		sc.save(con);
	}

	/**
	 * 个人特殊调薪 薪资变动;
	 * 增量
	 * 
	 * @param con
	 * @param ep
	 * @throws Exception
	 */
	public static void newSalaryChangeLog(CDBConnection con, Hr_salary_specchgsa scs) throws Exception {
		String sqlstr = "SELECT * FROM hr_salary_chgbill WHERE scatype=5 AND stype=7 AND sid=" + scs.scsid.getValue();
		Hr_salary_chgbill cb = new Hr_salary_chgbill();
		cb.findBySQL(con, sqlstr, false);
		if (cb.isEmpty())
			return;
		Hr_salary_chglg sc = new Hr_salary_chglg();
		sc.assignfield(cb, true);
		sc.createtime.setValue(null);
		sc.creator.setValue(null);
		sc.useable.setValue(1);
		
		Hr_salary_chglg scold = getCur_salary_chglg(scs.er_id.getValue());// 获取现在薪资情况
		// Hr_salary_structure stuc = new Hr_salary_structure();// 新的薪资结构
		// stuc.findByID(ep.newstru_id.getValue());
		// if (stuc.isEmpty())
		// throw new Exception("工资结构【" + ep.newstru_id.getValue() + "】不存在");

		
	/*	sc.er_id.setValue(scs.er_id.getValue());
		sc.scatype.setValue(5);// 1 入职定薪、2 调动核薪、3 转正调薪、4 年度调薪、5 特殊调薪
		sc.stype.setValue(7); // 来源类别 1 历史数据、2 实习登记、3 入职表单、4 调动表单、5 入职转正、6 调动转正、7个人特殊调薪....
		sc.sid.setValue(scs.scsid.getValue()); // 来源ID
		sc.scode.setValue(scs.scscode.getValue()); // 来源单号
*/
		sc.oldstru_id.setValue(scold.newstru_id.getValue()); // 调薪前工资结构ID
		sc.oldstru_name.setValue(scold.newstru_name.getValue()); // 调薪前工资结构名
		sc.oldchecklev.setValue(scold.newchecklev.getValue());// 调薪前绩效考核层级
		sc.oldattendtype.setValue(scold.newattendtype.getValue()); // 调薪前出勤类别
		sc.oldcalsalarytype.setValue(scold.newcalsalarytype.getValue()); // 调薪前计薪方式
		sc.oldposition_salary.setValue(scold.newposition_salary.getValue()); // 调薪前职位工资
		sc.oldbase_salary.setValue(scold.newbase_salary.getValue()); // 调薪前基本工资
		sc.oldtech_salary.setValue(scold.newtech_salary.getValue()); // 调薪前技能工资
		sc.oldachi_salary.setValue(scold.newachi_salary.getValue()); // 调薪前绩效工资
		sc.oldotwage.setValue(scold.newotwage.getValue()); // 调薪前固定加班工资
		sc.oldtech_allowance.setValue(scold.newtech_allowance.getValue()); // 调薪前技术津贴
		sc.oldparttimesubs.setValue(scold.newparttimesubs.getValue()); // 调薪前兼职津贴
		sc.oldpostsubs.setValue(scold.newpostsubs.getValue()); // 调薪前岗位津贴
		sc.oldavg_salary.setValue(0); // 调薪前平均工资
	/*	sc.newstru_id.setValue(scs.newstru_id.getValue()); // 调薪后工资结构ID
		sc.newstru_name.setValue(scs.newstru_name.getValue()); // 调薪后工资结构名
		sc.newchecklev.setValue(scs.newchecklev.getValue());// 调薪后绩效考核层级
		sc.newattendtype.setValue(scs.newkqtype.getValue()); // 调薪后出勤类别
		sc.newcalsalarytype.setValue(scold.newcalsalarytype.getValue()); // 调薪后计薪方式
		sc.newposition_salary.setValue(scold.newposition_salary.getAsFloatDefault(0) + scs.chgposition_salary.getAsFloatDefault(0)); // 调薪后职位工资
		sc.newbase_salary.setValue(scold.newbase_salary.getAsFloatDefault(0) + scs.chgbase_salary.getAsFloatDefault(0)); // 调薪后基本工资
		sc.newtech_salary.setValue(scold.newtech_salary.getAsFloatDefault(0) + scs.chgtech_salary.getAsFloatDefault(0)); // 调薪后技能工资
		sc.newachi_salary.setValue(scold.newachi_salary.getAsFloatDefault(0) + scs.chgachi_salary.getAsFloatDefault(0)); // 调薪后绩效工资
		sc.newotwage.setValue(scold.newotwage.getAsFloatDefault(0) + scs.chgotwage.getAsFloatDefault(0)); // 调薪后固定加班工资
		sc.newtech_allowance.setValue(scold.newtech_allowance.getAsFloatDefault(0) + scs.chgtech_allowance.getAsFloatDefault(0)); // 调薪后技术津贴*/
		sc.newparttimesubs.setValue(scold.newparttimesubs.getAsFloatDefault(0)); // 调薪后兼职津贴
		sc.newpostsubs.setValue(scold.newpostsubs.getAsFloatDefault(0)); // 调薪后岗位津贴
		float sacrage = scs.chgposition_salary.getAsFloatDefault(0) + scs.chgtech_allowance.getAsFloatDefault(0);
		sc.sacrage.setValue(sacrage); // 调薪幅度
		sc.chgdate.setValue(scs.salarydate.getValue()); // 调薪日期
		sc.chgreason.setValue("个人特殊调薪"); // 调薪原因
		sc.orgid.setValue(scs.orgid.getValue()); // 机构id
		sc.orgname.setValue(scs.orgname.getValue()); // 机构
		sc.lv_num.setValue(scs.lv_num.getValue()); // 职级
		sc.ospid.setValue(scs.ospid.getValue()); // 职位id
		sc.sp_name.setValue(scs.sp_name.getValue()); // 职位
		sc.save(con);
	}

	/**
	 * 调动转正薪资变动;
	 * 增量
	 * 
	 * @param con
	 * @param ep
	 * @throws Exception
	 */
	public static void newSalaryChangeLog(CDBConnection con, Hr_transfer_prob ep) throws Exception {

		String sqlstr = "SELECT * FROM hr_salary_chgbill WHERE scatype=3 AND stype=6 AND sid=" + ep.pbtid.getValue();
		Hr_salary_chgbill cb = new Hr_salary_chgbill();
		cb.findBySQL(con, sqlstr, false);
		if (cb.isEmpty())
			return;
		Hr_salary_chglg sc = new Hr_salary_chglg();
		sc.assignfield(cb, true);//复制调动单数据
		sc.createtime.setValue(null);
		sc.creator.setValue(null);
		sc.useable.setValue(1);

		Hr_salary_chglg scold = getCur_salary_chglg(ep.er_id.getValue());// 获取现在薪资情况
		sc.oldstru_id.setValue(scold.newstru_id.getValue()); // 调薪前工资结构ID
		sc.oldstru_name.setValue(scold.newstru_name.getValue()); // 调薪前工资结构名
		sc.oldchecklev.setValue(scold.newchecklev.getValue());// 调薪前绩效考核层级
		sc.oldattendtype.setValue(scold.newattendtype.getValue()); // 调薪前出勤类别
		sc.oldcalsalarytype.setValue(scold.newcalsalarytype.getValue()); // 调薪前计薪方式
		sc.oldposition_salary.setValue(scold.newposition_salary.getValue()); // 调薪前职位工资
		sc.oldbase_salary.setValue(scold.newbase_salary.getValue()); // 调薪前基本工资
		sc.oldtech_salary.setValue(scold.newtech_salary.getValue()); // 调薪前技能工资
		sc.oldachi_salary.setValue(scold.newachi_salary.getValue()); // 调薪前绩效工资
		sc.oldotwage.setValue(scold.newotwage.getValue()); // 调薪前固定加班工资
		sc.oldtech_allowance.setValue(scold.newtech_allowance.getValue()); // 调薪前技术津贴
		sc.oldparttimesubs.setValue(scold.newparttimesubs.getValue()); // 调薪前兼职津贴
		sc.oldpostsubs.setValue(scold.newpostsubs.getValue()); // 调薪前岗位津贴
		sc.oldavg_salary.setValue(0); // 调薪前平均工资
		sc.chgreason.setValue("调动转正调薪"); // 调薪原因
		sc.orgid.setValue(ep.neworgid.getValue()); // 机构id
		sc.orgname.setValue(ep.neworgname.getValue()); // 机构
		sc.lv_num.setValue(ep.newlv_num.getValue()); // 职级
		sc.ospid.setValue(ep.newospid.getValue()); // 职位id
		sc.sp_name.setValue(ep.newsp_name.getValue()); // 职位
		sc.save(con);

		// Hr_salary_chglg sc = new Hr_salary_chglg();
		// sc.er_id.setValue(ep.er_id.getValue());
		// sc.scatype.setValue(3);// 1 入职定薪、2 调动核薪、3 转正调薪、4 年度调薪、5 特殊调薪
		// sc.stype.setValue(6); // 来源类别 1 历史数据、2 实习登记、3 入职表单、4 调动表单、5 入职转正、6 调动转正....
		// sc.sid.setValue(ep.pbtid.getValue()); // 来源ID
		// sc.scode.setValue(ep.pbtcode.getValue()); // 来源单号
		//
		//
		// sc.newstru_id.setValue(ep.newstru_id.getValue()); // 调薪后工资结构ID
		// sc.newstru_name.setValue(ep.newstru_name.getValue()); // 调薪后工资结构名
		// sc.newchecklev.setValue(ep.newchecklev.getValue());// 调薪后绩效考核层级
		// sc.newattendtype.setValue(ep.newattendtype.getValue()); // 调薪后出勤类别
		// sc.newcalsalarytype.setValue(null); // 调薪后计薪方式
		// sc.newposition_salary.setValue(scold.newposition_salary.getAsFloatDefault(0) + ep.chgposition_salary.getAsFloatDefault(0)); // 调薪后职位工资
		// sc.newbase_salary.setValue(scold.newbase_salary.getAsFloatDefault(0) + ep.chgbase_salary.getAsFloatDefault(0)); // 调薪后基本工资
		// sc.newtech_salary.setValue(scold.newtech_salary.getAsFloatDefault(0) + ep.chgtech_salary.getAsFloatDefault(0)); // 调薪后技能工资
		// sc.newachi_salary.setValue(scold.newachi_salary.getAsFloatDefault(0) + ep.chgachi_salary.getAsFloatDefault(0)); // 调薪后绩效工资
		// sc.newotwage.setValue(scold.newotwage.getAsFloatDefault(0) + ep.chgotwage.getAsFloatDefault(0)); // 调薪后固定加班工资
		// sc.newtech_allowance.setValue(scold.newtech_allowance.getAsFloatDefault(0) + ep.chgtech_allowance.getAsFloatDefault(0)); // 调薪后技术津贴
		// sc.newparttimesubs.setValue(scold.newparttimesubs.getAsFloatDefault(0)); // 调薪后兼职津贴
		// sc.newpostsubs.setValue(scold.newpostsubs.getAsFloatDefault(0)); // 调薪后岗位津贴
		// sc.sacrage.setValue(ep.pbtsarylev.getAsFloatDefault(0)); // 调薪幅度
		// sc.chgdate.setValue(ep.salarydate.getValue()); // 调薪日期
		// sc.chgreason.setValue("调动转正调薪"); // 调薪原因

	}

	/**
	 * 兼职表单
	 * 
	 * @param con
	 * @param ep
	 * @param enORdis
	 * 生效或失效
	 * @throws Exception
	 */
	public static void newSalaryChangeLog(CDBConnection con, Hr_empptjob_app ep, boolean enORdis) throws Exception {
		float subsidyarm = ep.subsidyarm.getAsFloatDefault(0);
		if (subsidyarm == 0)
			return;
		if (!enORdis)
			subsidyarm = subsidyarm * -1;

		Hr_salary_chglg scold = getCur_salary_chglg(ep.er_id.getValue());// 获取现在薪资情况

		Hr_salary_chglg sc = new Hr_salary_chglg();
		sc.er_id.setValue(ep.er_id.getValue());
		sc.scatype.setValue(7);// 1 入职定薪、2调动核薪、3 转正调薪、4 年度调薪、5 特殊调薪 6调动转正 7 兼职
		sc.stype.setValue(8); // 来源类别 1 历史数据、2 实习登记、3 入职表单、4 调动表单、5 入职转正、6 调动转正、7个人特殊调薪 8兼职....
		sc.sid.setValue(ep.ptjaid.getValue()); // 来源ID
		sc.scode.setValue(ep.ptjacode.getValue()); // 来源单号

		sc.oldstru_id.setValue(scold.newstru_id.getValue()); // 调薪前工资结构ID
		sc.oldstru_name.setValue(scold.newstru_name.getValue()); // 调薪前工资结构名
		sc.oldchecklev.setValue(scold.newchecklev.getValue());// 调薪前绩效考核层级
		sc.oldattendtype.setValue(scold.newattendtype.getValue()); // 调薪前出勤类别
		sc.oldcalsalarytype.setValue(scold.newcalsalarytype.getValue()); // 调薪前计薪方式
		sc.oldposition_salary.setValue(scold.newposition_salary.getValue()); // 调薪前职位工资
		sc.oldbase_salary.setValue(scold.newbase_salary.getValue()); // 调薪前基本工资
		sc.oldtech_salary.setValue(scold.newtech_salary.getValue()); // 调薪前技能工资
		sc.oldachi_salary.setValue(scold.newachi_salary.getValue()); // 调薪前绩效工资
		sc.oldotwage.setValue(scold.newotwage.getValue()); // 调薪前固定加班工资
		sc.oldtech_allowance.setValue(scold.newtech_allowance.getValue()); // 调薪前技术津贴
		sc.oldparttimesubs.setValue(scold.newparttimesubs.getValue()); // 调薪前兼职津贴
		sc.oldpostsubs.setValue(scold.newpostsubs.getValue()); // 调薪前岗位津贴
		sc.oldavg_salary.setValue(0); // 调薪前平均工资

		sc.newstru_id.setValue(scold.newstru_id.getValue()); // 调薪后工资结构ID
		sc.newstru_name.setValue(scold.newstru_name.getValue()); // 调薪后工资结构名
		sc.newchecklev.setValue(scold.newchecklev.getValue());// 调薪后绩效考核层级
		sc.newattendtype.setValue(scold.newattendtype.getValue()); // 调薪后出勤类别
		sc.newcalsalarytype.setValue(scold.newcalsalarytype.getValue()); // 调薪后计薪方式
		sc.newposition_salary.setValue(scold.newposition_salary.getAsFloatDefault(0)); // 调薪后职位工资
		sc.newbase_salary.setValue(scold.newbase_salary.getAsFloatDefault(0)); // 调薪后基本工资
		sc.newtech_salary.setValue(scold.newtech_salary.getAsFloatDefault(0)); // 调薪后技能工资
		sc.newachi_salary.setValue(scold.newachi_salary.getAsFloatDefault(0)); // 调薪后绩效工资
		sc.newotwage.setValue(scold.newotwage.getAsFloatDefault(0)); // 调薪后固定加班工资
		sc.newtech_allowance.setValue(scold.newtech_allowance.getAsFloatDefault(0)); // 调薪后技术津贴
		float newparttimesubs = scold.newparttimesubs.getAsFloatDefault(0) + subsidyarm;
		if (newparttimesubs < 0)
			newparttimesubs = 0;
		sc.newparttimesubs.setValue(newparttimesubs); // 调薪后兼职津贴
		sc.newpostsubs.setValue(scold.newpostsubs.getAsFloatDefault(0)); // 调薪后岗位津贴
		sc.sacrage.setValue(subsidyarm); // 调薪幅度
		if (enORdis) {
			sc.chgdate.setValue(ep.substart.getValue()); // 调薪日期
			sc.chgreason.setValue("兼职审批通过调薪"); // 调薪原因
		} else {
			Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd());// 去除时分秒
			Date eddate = Systemdate.dateDayAdd(bgdate, 1);// 加一天
			sc.chgdate.setValue(Systemdate.getStrDateyyyy_mm_dd(eddate)); // 调薪日期
			sc.chgreason.setValue("兼职结束调薪"); // 调薪原因
		}
		sc.orgid.setValue(ep.odorgid.getValue()); // 机构id
		sc.orgname.setValue(ep.odorgname.getValue()); // 机构
		sc.lv_num.setValue(ep.odlv_num.getValue()); // 职级
		sc.ospid.setValue(ep.odospid.getValue()); // 职位id
		sc.sp_name.setValue(ep.odsp_name.getValue()); // 职位
		sc.save(con);
	}

	/**
	 * 批量特殊调薪 薪资变动;
	 * 增量
	 * 
	 * @param con
	 * @param ep
	 * @throws Exception
	 */
	public static void newSalaryChangeLog(CDBConnection con, Hr_salary_specchgsa_batch scsb) throws Exception {
		// Hr_salary_structure stuc = new Hr_salary_structure();// 新的薪资结构
		// stuc.findByID(ep.newstru_id.getValue());
		// if (stuc.isEmpty())
		// throw new Exception("工资结构【" + ep.newstru_id.getValue() + "】不存在");

		CJPALineData<Hr_salary_specchgsa_batch_line> scsbls = scsb.hr_salary_specchgsa_batch_lines;
		for (CJPABase scsbljpa : scsbls) {
			Hr_salary_specchgsa_batch_line scsbl = (Hr_salary_specchgsa_batch_line) scsbljpa;
			Hr_salary_chglg scold = getCur_salary_chglg(scsbl.er_id.getValue());// 获取现在薪资情况
			Hr_salary_chglg sc = new Hr_salary_chglg();
			sc.er_id.setValue(scsbl.er_id.getValue());
			sc.scatype.setValue(5);// 1 入职定薪、2 调动核薪、3 转正调薪、4 年度调薪、5 特殊调薪
			sc.stype.setValue(9); // 来源类别 1 历史数据、2 实习登记、3 入职表单、4 调动表单、5 入职转正、6 调动转正、7个人特殊调薪、8兼职、9批量特殊调薪....
			sc.sid.setValue(scsb.scsbid.getValue()); // 来源ID
			sc.scode.setValue(scsb.scsbcode.getValue()); // 来源单号

			sc.oldstru_id.setValue(scold.newstru_id.getValue()); // 调薪前工资结构ID
			sc.oldstru_name.setValue(scold.newstru_name.getValue()); // 调薪前工资结构名
			sc.oldchecklev.setValue(scold.newchecklev.getValue());// 调薪前绩效考核层级
			sc.oldattendtype.setValue(scold.newattendtype.getValue()); // 调薪前出勤类别
			sc.oldcalsalarytype.setValue(scold.newcalsalarytype.getValue()); // 调薪前计薪方式
			sc.oldposition_salary.setValue(scold.newposition_salary.getValue()); // 调薪前职位工资
			sc.oldbase_salary.setValue(scold.newbase_salary.getValue()); // 调薪前基本工资
			sc.oldtech_salary.setValue(scold.newtech_salary.getValue()); // 调薪前技能工资
			sc.oldachi_salary.setValue(scold.newachi_salary.getValue()); // 调薪前绩效工资
			sc.oldotwage.setValue(scold.newotwage.getValue()); // 调薪前固定加班工资
			sc.oldtech_allowance.setValue(scold.newtech_allowance.getValue()); // 调薪前技术津贴
			sc.oldparttimesubs.setValue(scold.newparttimesubs.getValue()); // 调薪前兼职津贴
			sc.oldpostsubs.setValue(scold.newpostsubs.getValue()); // 调薪前岗位津贴
			sc.oldavg_salary.setValue(0); // 调薪前平均工资
			sc.newstru_id.setValue(scsbl.newstru_id.getValue()); // 调薪后工资结构ID
			sc.newstru_name.setValue(scsbl.newstru_name.getValue()); // 调薪后工资结构名
			sc.newchecklev.setValue(scsbl.newchecklev.getValue());// 调薪后绩效考核层级
			sc.newattendtype.setValue(scsbl.newattendtype.getValue()); // 调薪后出勤类别
			sc.newcalsalarytype.setValue(scold.newcalsalarytype.getValue()); // 调薪后计薪方式
			sc.newposition_salary.setValue(scold.newposition_salary.getAsFloatDefault(0) + scsbl.chgposition_salary.getAsFloatDefault(0)); // 调薪后职位工资
			sc.newbase_salary.setValue(scold.newbase_salary.getAsFloatDefault(0) + scsbl.chgbase_salary.getAsFloatDefault(0)); // 调薪后基本工资
			sc.newtech_salary.setValue(scold.newtech_salary.getAsFloatDefault(0) + scsbl.chgtech_salary.getAsFloatDefault(0)); // 调薪后技能工资
			sc.newachi_salary.setValue(scold.newachi_salary.getAsFloatDefault(0) + scsbl.chgachi_salary.getAsFloatDefault(0)); // 调薪后绩效工资
			sc.newotwage.setValue(scold.newotwage.getAsFloatDefault(0) + scsbl.chgotwage.getAsFloatDefault(0)); // 调薪后固定加班工资
			sc.newtech_allowance.setValue(scold.newtech_allowance.getAsFloatDefault(0) + scsbl.chgtech_allowance.getAsFloatDefault(0)); // 调薪后技术津贴
			sc.newparttimesubs.setValue(scold.newparttimesubs.getAsFloatDefault(0)); // 调薪后兼职津贴
			sc.newpostsubs.setValue(scold.newpostsubs.getAsFloatDefault(0)); // 调薪后岗位津贴
			sc.sacrage.setValue(scsbl.pbtsarylev.getAsFloatDefault(0)); // 调薪幅度
			sc.chgdate.setValue(scsb.salarydate.getValue()); // 调薪日期
			sc.chgreason.setValue("批量特殊调薪"); // 调薪原因
			sc.orgid.setValue(scsbl.orgid.getValue()); // 机构id
			sc.orgname.setValue(scsbl.orgname.getValue()); // 机构
			sc.lv_num.setValue(scsbl.lv_num.getValue()); // 职级
			sc.ospid.setValue(scsbl.ospid.getValue()); // 职位id
			sc.sp_name.setValue(scsbl.sp_name.getValue()); // 职位
			sc.save(con);
		}
	}

	/**
	 * 技术津贴薪资变动
	 * 
	 * @param con
	 * @param ts
	 * @param enORdis
	 * @throws Exception
	 */
	public static void newSalaryChangeLog(CDBConnection con, Hr_salary_techsub ts, boolean enORdis) throws Exception {
		CJPALineData<Hr_salary_techsub_line> tsls = ts.hr_salary_techsub_lines;
		for (CJPABase tsljpa : tsls) {
			Hr_salary_techsub_line tsl = (Hr_salary_techsub_line) tsljpa;
			float subsidyarm = tsl.ntechsub.getAsFloatDefault(0);
			if (subsidyarm == 0)
				return;
			if (!enORdis)
				subsidyarm = subsidyarm * -1;
			Hr_salary_chglg scold = getCur_salary_chglg(tsl.er_id.getValue());// 获取现在薪资情况
			Hr_salary_chglg sc = new Hr_salary_chglg();
			sc.er_id.setValue(tsl.er_id.getValue());
			sc.scatype.setValue(8);// 1 入职定薪、2调动核薪、3 转正调薪、4 年度调薪、5 特殊调薪 6调动转正 7 兼职8津贴
			sc.stype.setValue(10); // 来源类别 1 历史数据、2 实习登记、3 入职表单、4 调动表单、5 入职转正、6 调动转正、7个人特殊调薪 8兼职、9批量特殊调薪、10技术津贴....
			sc.sid.setValue(ts.ts_id.getValue()); // 来源ID
			sc.scode.setValue(ts.ts_code.getValue()); // 来源单号

			sc.oldstru_id.setValue(scold.newstru_id.getValue()); // 调薪前工资结构ID
			sc.oldstru_name.setValue(scold.newstru_name.getValue()); // 调薪前工资结构名
			sc.oldchecklev.setValue(scold.newchecklev.getValue());// 调薪前绩效考核层级
			sc.oldattendtype.setValue(scold.newattendtype.getValue()); // 调薪前出勤类别
			sc.oldcalsalarytype.setValue(scold.newcalsalarytype.getValue()); // 调薪前计薪方式
			sc.oldposition_salary.setValue(scold.newposition_salary.getValue()); // 调薪前职位工资
			sc.oldbase_salary.setValue(scold.newbase_salary.getValue()); // 调薪前基本工资
			sc.oldtech_salary.setValue(scold.newtech_salary.getValue()); // 调薪前技能工资
			sc.oldachi_salary.setValue(scold.newachi_salary.getValue()); // 调薪前绩效工资
			sc.oldotwage.setValue(scold.newotwage.getValue()); // 调薪前固定加班工资
			sc.oldtech_allowance.setValue(scold.newtech_allowance.getValue()); // 调薪前技术津贴
			sc.oldparttimesubs.setValue(scold.newparttimesubs.getValue()); // 调薪前兼职津贴
			sc.oldpostsubs.setValue(scold.newpostsubs.getValue()); // 调薪前岗位津贴
			sc.oldavg_salary.setValue(0); // 调薪前平均工资

			sc.newstru_id.setValue(scold.newstru_id.getValue()); // 调薪后工资结构ID
			sc.newstru_name.setValue(scold.newstru_name.getValue()); // 调薪后工资结构名
			sc.newchecklev.setValue(scold.newchecklev.getValue());// 调薪后绩效考核层级
			sc.newattendtype.setValue(scold.newattendtype.getValue()); // 调薪后出勤类别
			sc.newcalsalarytype.setValue(scold.newcalsalarytype.getValue()); // 调薪后计薪方式
			sc.newposition_salary.setValue(scold.newposition_salary.getAsFloatDefault(0)); // 调薪后职位工资
			sc.newbase_salary.setValue(scold.newbase_salary.getAsFloatDefault(0)); // 调薪后基本工资
			sc.newtech_salary.setValue(scold.newtech_salary.getAsFloatDefault(0)); // 调薪后技能工资
			sc.newachi_salary.setValue(scold.newachi_salary.getAsFloatDefault(0)); // 调薪后绩效工资
			sc.newotwage.setValue(scold.newotwage.getAsFloatDefault(0)); // 调薪后固定加班工资
			sc.newparttimesubs.setValue(scold.newparttimesubs.getAsFloatDefault(0)); // 调薪后兼职津贴
			float newtech_allowance = scold.newtech_allowance.getAsFloatDefault(0) + subsidyarm;
			if (newtech_allowance < 0)
				newtech_allowance = 0;
			sc.newtech_allowance.setValue(newtech_allowance); // 调薪后技术津贴
			sc.newpostsubs.setValue(scold.newpostsubs.getAsFloatDefault(0)); // 调薪后岗位津贴
			sc.sacrage.setValue(subsidyarm); // 调薪幅度
			sc.chgdate.setValue(ts.subsdate.getValue()); // 调薪日期
			sc.chgreason.setValue("技术津贴申请审批通过调薪"); // 调薪原因
			sc.orgid.setValue(tsl.orgid.getValue()); // 机构id
			sc.orgname.setValue(tsl.orgname.getValue()); // 机构
			sc.lv_num.setValue(tsl.lv_num.getValue()); // 职级
			sc.ospid.setValue(tsl.ospid.getValue()); // 职位id
			sc.sp_name.setValue(tsl.sp_name.getValue()); // 职位
			sc.save(con);
		}
	}

	/**
	 * 岗位津贴薪资变动
	 * 
	 * @param con
	 * @param ps
	 * @param enORdis
	 * @throws Exception
	 */
	public static void newSalaryChangeLog(CDBConnection con, Hr_salary_postsub ps, boolean enORdis) throws Exception {
		CJPALineData<Hr_salary_postsub_line> psls = ps.hr_salary_postsub_lines;
		for (CJPABase psljpa : psls) {
			Hr_salary_postsub_line psl = (Hr_salary_postsub_line) psljpa;
			float subsidyarm = psl.npostsub.getAsFloatDefault(0);
			if (subsidyarm == 0)
				return;
			if (!enORdis)
				subsidyarm = subsidyarm * -1;

			Hr_salary_chglg scold = getCur_salary_chglg(psl.er_id.getValue());// 获取现在薪资情况

			Hr_salary_chglg sc = new Hr_salary_chglg();
			sc.er_id.setValue(psl.er_id.getValue());
			sc.scatype.setValue(8);// 1 入职定薪、2调动核薪、3 转正调薪、4 年度调薪、5 特殊调薪 6调动转正 7 兼职
			sc.stype.setValue(11); // 来源类别 1 历史数据、2 实习登记、3 入职表单、4 调动表单、5 入职转正、6 调动转正、7个人特殊调薪 8兼职、9批量特殊调薪、10技术津贴、11岗位津贴....
			sc.sid.setValue(ps.ps_id.getValue()); // 来源ID
			sc.scode.setValue(ps.ps_code.getValue()); // 来源单号

			sc.oldstru_id.setValue(scold.newstru_id.getValue()); // 调薪前工资结构ID
			sc.oldstru_name.setValue(scold.newstru_name.getValue()); // 调薪前工资结构名
			sc.oldchecklev.setValue(scold.newchecklev.getValue());// 调薪前绩效考核层级
			sc.oldattendtype.setValue(scold.newattendtype.getValue()); // 调薪前出勤类别
			sc.oldcalsalarytype.setValue(scold.newcalsalarytype.getValue()); // 调薪前计薪方式
			sc.oldposition_salary.setValue(scold.newposition_salary.getValue()); // 调薪前职位工资
			sc.oldbase_salary.setValue(scold.newbase_salary.getValue()); // 调薪前基本工资
			sc.oldtech_salary.setValue(scold.newtech_salary.getValue()); // 调薪前技能工资
			sc.oldachi_salary.setValue(scold.newachi_salary.getValue()); // 调薪前绩效工资
			sc.oldotwage.setValue(scold.newotwage.getValue()); // 调薪前固定加班工资
			sc.oldtech_allowance.setValue(scold.newtech_allowance.getValue()); // 调薪前技术津贴
			sc.oldparttimesubs.setValue(scold.newparttimesubs.getValue()); // 调薪前兼职津贴
			sc.oldpostsubs.setValue(scold.newpostsubs.getValue()); // 调薪前岗位津贴
			sc.oldavg_salary.setValue(0); // 调薪前平均工资

			sc.newstru_id.setValue(scold.newstru_id.getValue()); // 调薪后工资结构ID
			sc.newstru_name.setValue(scold.newstru_name.getValue()); // 调薪后工资结构名
			sc.newchecklev.setValue(scold.newchecklev.getValue());// 调薪后绩效考核层级
			sc.newattendtype.setValue(scold.newattendtype.getValue()); // 调薪后出勤类别
			sc.newcalsalarytype.setValue(scold.newcalsalarytype.getValue()); // 调薪后计薪方式
			sc.newposition_salary.setValue(scold.newposition_salary.getAsFloatDefault(0)); // 调薪后职位工资
			sc.newbase_salary.setValue(scold.newbase_salary.getAsFloatDefault(0)); // 调薪后基本工资
			sc.newtech_salary.setValue(scold.newtech_salary.getAsFloatDefault(0)); // 调薪后技能工资
			sc.newachi_salary.setValue(scold.newachi_salary.getAsFloatDefault(0)); // 调薪后绩效工资
			sc.newotwage.setValue(scold.newotwage.getAsFloatDefault(0)); // 调薪后固定加班工资
			sc.newtech_allowance.setValue(scold.newtech_allowance.getAsFloatDefault(0)); // 调薪后技术津贴
			sc.newparttimesubs.setValue(scold.newparttimesubs.getAsFloatDefault(0)); // 调薪后兼职津贴

			float newpostsubs = scold.newpostsubs.getAsFloatDefault(0) + subsidyarm;
			if (newpostsubs < 0)
				newpostsubs = 0;
			sc.newpostsubs.setValue(newpostsubs); // 调薪后兼职津贴
			sc.sacrage.setValue(subsidyarm); // 调薪幅度
			if (enORdis) {
				sc.chgdate.setValue(ps.startdate.getValue()); // 调薪日期
				sc.chgreason.setValue("岗位津贴审批通过调薪"); // 调薪原因
			} else {
				sc.chgdate.setValue(ps.enddate.getValue()); // 调薪日期
				sc.chgreason.setValue("岗位津贴结束调薪"); // 调薪原因
			}
			sc.orgid.setValue(psl.orgid.getValue()); // 机构id
			sc.orgname.setValue(psl.orgname.getValue()); // 机构
			sc.lv_num.setValue(psl.lv_num.getValue()); // 职级
			sc.ospid.setValue(psl.ospid.getValue()); // 职位id
			sc.sp_name.setValue(psl.sp_name.getValue()); // 职位
			sc.save(con);
		}
	}

	/**
	 * 年度调薪 薪资变动;
	 * 增量
	 * 
	 * @param con
	 * @param ep
	 * @throws Exception
	 */
	public static void newSalaryChangeLog(CDBConnection con, Hr_salary_yearraise yr) throws Exception {
		// Hr_salary_structure stuc = new Hr_salary_structure();// 新的薪资结构
		// stuc.findByID(ep.newstru_id.getValue());
		// if (stuc.isEmpty())
		// throw new Exception("工资结构【" + ep.newstru_id.getValue() + "】不存在");

		CJPALineData<Hr_salary_yearraise_line> yrls = yr.hr_salary_yearraise_lines;
		for (CJPABase yrljpa : yrls) {
			Hr_salary_yearraise_line yrl = (Hr_salary_yearraise_line) yrljpa;
			Hr_salary_chglg scold = getCur_salary_chglg(yrl.er_id.getValue());// 获取现在薪资情况
			Hr_salary_chglg sc = new Hr_salary_chglg();
			sc.er_id.setValue(yrl.er_id.getValue());
			sc.scatype.setValue(4);// 1 入职定薪、2 调动核薪、3 转正调薪、4 年度调薪、5 特殊调薪
			sc.stype.setValue(12); // 来源类别 1 历史数据、2 实习登记、3 入职表单、4 调动表单、5 入职转正、6 调动转正、7个人特殊调薪、8兼职、9批量特殊调薪、10技术津贴、11岗位津贴、12年度调薪....
			sc.sid.setValue(yr.yrid.getValue()); // 来源ID
			sc.scode.setValue(yr.yrcode.getValue()); // 来源单号

			sc.oldstru_id.setValue(scold.newstru_id.getValue()); // 调薪前工资结构ID
			sc.oldstru_name.setValue(scold.newstru_name.getValue()); // 调薪前工资结构名
			sc.oldchecklev.setValue(scold.newchecklev.getValue());// 调薪前绩效考核层级
			sc.oldattendtype.setValue(scold.newattendtype.getValue()); // 调薪前出勤类别
			sc.oldcalsalarytype.setValue(scold.newcalsalarytype.getValue()); // 调薪前计薪方式
			sc.oldposition_salary.setValue(scold.newposition_salary.getValue()); // 调薪前职位工资
			sc.oldbase_salary.setValue(scold.newbase_salary.getValue()); // 调薪前基本工资
			sc.oldtech_salary.setValue(scold.newtech_salary.getValue()); // 调薪前技能工资
			sc.oldachi_salary.setValue(scold.newachi_salary.getValue()); // 调薪前绩效工资
			sc.oldotwage.setValue(scold.newotwage.getValue()); // 调薪前固定加班工资
			sc.oldtech_allowance.setValue(scold.newtech_allowance.getValue()); // 调薪前技术津贴
			sc.oldparttimesubs.setValue(scold.newparttimesubs.getValue()); // 调薪前兼职津贴
			sc.oldpostsubs.setValue(scold.newpostsubs.getValue()); // 调薪前岗位津贴
			sc.oldavg_salary.setValue(0); // 调薪前平均工资
			sc.newstru_id.setValue(yrl.newstru_id.getValue()); // 调薪后工资结构ID
			sc.newstru_name.setValue(yrl.newstru_name.getValue()); // 调薪后工资结构名
			sc.newchecklev.setValue(yrl.newchecklev.getValue());// 调薪后绩效考核层级
			sc.newattendtype.setValue(yrl.newattendtype.getValue()); // 调薪后出勤类别
			sc.newcalsalarytype.setValue(scold.newcalsalarytype.getValue()); // 调薪后计薪方式
			sc.newposition_salary.setValue(scold.newposition_salary.getAsFloatDefault(0) + yrl.chgposition_salary.getAsFloatDefault(0)); // 调薪后职位工资
			
			sc.newbase_salary.setValue(scold.newbase_salary.getAsFloatDefault(0) + yrl.chgbase_salary.getAsFloatDefault(0)); // 调薪后基本工资
			Logsw.dblog("调薪前基本工资=newbase_salary"+scold.newbase_salary.getAsFloatDefault(0)+"调整chgbase_salary"+yrl.chgbase_salary.getAsFloatDefault(0)+"最终newbase_salary="+sc.newbase_salary.getValue());
			System.out.print("调薪前基本工资=newbase_salary"+scold.newbase_salary.getAsFloatDefault(0)+"调整chgbase_salary"+yrl.chgbase_salary.getAsFloatDefault(0)+"最终newbase_salary="+sc.newbase_salary.getValue());
			sc.newtech_salary.setValue(scold.newtech_salary.getAsFloatDefault(0) + yrl.chgtech_salary.getAsFloatDefault(0)); // 调薪后技能工资
			sc.newachi_salary.setValue(scold.newachi_salary.getAsFloatDefault(0) + yrl.chgachi_salary.getAsFloatDefault(0)); // 调薪后绩效工资
			sc.newotwage.setValue(scold.newotwage.getAsFloatDefault(0) + yrl.chgotwage.getAsFloatDefault(0)); // 调薪后固定加班工资
			sc.newtech_allowance.setValue(scold.newtech_allowance.getAsFloatDefault(0) + yrl.chgtech_allowance.getAsFloatDefault(0)); // 调薪后技术津贴
			sc.newparttimesubs.setValue(scold.newparttimesubs.getAsFloatDefault(0)); // 调薪后兼职津贴
			sc.newpostsubs.setValue(scold.newpostsubs.getAsFloatDefault(0)); // 调薪后岗位津贴
			sc.sacrage.setValue(yrl.pbtsarylev.getAsFloatDefault(0)); // 调薪幅度
			sc.chgdate.setValue(yr.salarydate.getValue()); // 调薪日期
			sc.chgreason.setValue("年度调薪"); // 调薪原因
			sc.orgid.setValue(yrl.orgid.getValue()); // 机构id
			sc.orgname.setValue(yrl.orgname.getValue()); // 机构
			sc.lv_num.setValue(yrl.lv_num.getValue()); // 职级
			sc.ospid.setValue(yrl.ospid.getValue()); // 职位id
			sc.sp_name.setValue(yrl.sp_name.getValue()); // 职位
			sc.save(con);
		}
	}

	public static void checkHotSubValidDate(Date bdate) throws Exception{
		if (HRUtil.hasRoles("19"))// 薪酬管理员
			return;
		int days = Integer.valueOf(HrkqUtil.getParmValue("SA_ALLOW_HOTSUBDAY"));// 次月X号以后就不能再补提上月的高温补贴申请单据
		if (days == 0)
			return;
		checkSalaryValidDate(days,bdate);
	}
	
	public static void checkLineTargetValidDate(Date bdate) throws Exception{
		if (HRUtil.hasRoles("19"))// 薪酬管理员
			return;
		int days = Integer.valueOf(HrkqUtil.getParmValue("SA_ALLOW_LINTTARGETDAY"));// 次月X号以后就不能再补提上月的总装拉线每月考核指标维护单据
		if (days == 0)
			return;
		checkSalaryValidDate(days,bdate);
	}
	
	public static void checkTeamWardValidDate(Date bdate) throws Exception{
		if (HRUtil.hasRoles("19"))// 薪酬管理员
			return;
		int days = Integer.valueOf(HrkqUtil.getParmValue("SA_ALLOW_TEAMWARDDAY"));// 次月X号以后就不能再补提上月的团队管理奖申请单据
		if (days == 0)
			return;
		checkSalaryValidDate(days,bdate);
	}
	
	public static void checkSalaryValidDate(int days,Date bdate)throws Exception{
		Date m01 = Systemdate.getDateByStr(Systemdate.getStrDateByFmt(new Date(), "yyyy-MM-01"));// 本月1号
		int day = Integer.valueOf(Systemdate.getStrDateByFmt(new Date(), "dd"));// 当前日
		if (day <= days) {
			Date pm01 = Systemdate.dateMonthAdd(m01, -1);
			if (bdate.getTime() < pm01.getTime()) {
				throw new Exception("不能提交【" + Systemdate.getStrDateByFmt(pm01, "yyyy-MM-dd") + "】之前的表单");
			}
		}else{
			if (bdate.getTime() < m01.getTime()) {
				throw new Exception(
						"本月【" + days + "】号以后不能提交【" + Systemdate.getStrDateByFmt(m01, "yyyy-MM-dd") + "】之前的表单");
			}
		}
	}
	
	/**
	 * 实习生入职 薪资变动;
	 * 增量
	 * 
	 * @param con
	 * @param ep
	 * @throws Exception
	 */
	public static void newSalaryChangeLog(CDBConnection con, Hr_trainentry_batch teb) throws Exception {
		// Hr_salary_structure stuc = new Hr_salary_structure();// 新的薪资结构
		// stuc.findByID(ep.newstru_id.getValue());
		// if (stuc.isEmpty())
		// throw new Exception("工资结构【" + ep.newstru_id.getValue() + "】不存在");

		CJPALineData<Hr_trainentry_batchline> tebls = teb.hr_trainentry_batchlines;
		for (CJPABase tebljpa : tebls) {
			Hr_trainentry_batchline tebl = (Hr_trainentry_batchline) tebljpa;
			Hr_salary_chglg scold = getCur_salary_chglg(tebl.er_id.getValue());// 获取现在薪资情况
			Hr_salary_chglg sc = new Hr_salary_chglg();
			sc.er_id.setValue(tebl.er_id.getValue());
			sc.scatype.setValue(6);// 1 入职定薪、2 调动核薪、3 转正调薪、4 年度调薪、5 特殊调薪、6实习生调薪
			sc.stype.setValue(13); // 来源类别 1 历史数据、2 实习登记、3 入职表单、4 调动表单、5 入职转正、6 调动转正、7个人特殊调薪、8兼职、9批量特殊调薪、10技术津贴、11岗位津贴、12年度调薪、13实习生入职、14实习生转正、15实习生分配....
			sc.sid.setValue(teb.tetyb_id.getValue()); // 来源ID
			sc.scode.setValue(teb.tetyb_code.getValue()); // 来源单号

			sc.oldstru_id.setValue(scold.newstru_id.getValue()); // 调薪前工资结构ID
			sc.oldstru_name.setValue(scold.newstru_name.getValue()); // 调薪前工资结构名
			if(scold.newstru_name.getValue()==null){
				sc.oldstru_name.setValue("0"); // 调薪前工资结构名
			}
			sc.oldchecklev.setAsFloat(scold.newchecklev.getAsFloatDefault(0));// 调薪前绩效考核层级
			sc.oldattendtype.setAsFloat(scold.newattendtype.getAsFloatDefault(0)); // 调薪前出勤类别
			sc.oldcalsalarytype.setValue(scold.newcalsalarytype.getValue()); // 调薪前计薪方式
			sc.oldposition_salary.setValue(scold.newposition_salary.getValue()); // 调薪前职位工资
			sc.oldbase_salary.setValue(scold.newbase_salary.getValue()); // 调薪前基本工资
			sc.oldtech_salary.setValue(scold.newtech_salary.getValue()); // 调薪前技能工资
			sc.oldachi_salary.setValue(scold.newachi_salary.getValue()); // 调薪前绩效工资
			sc.oldotwage.setValue(scold.newotwage.getValue()); // 调薪前固定加班工资
			sc.oldtech_allowance.setValue(scold.newtech_allowance.getValue()); // 调薪前技术津贴
			sc.oldparttimesubs.setValue(scold.newparttimesubs.getValue()); // 调薪前兼职津贴
			sc.oldpostsubs.setValue(scold.newpostsubs.getValue()); // 调薪前岗位津贴
			sc.oldavg_salary.setValue(0); // 调薪前平均工资
			sc.newstru_id.setValue(tebl.newstru_id.getValue()); // 调薪后工资结构ID
			sc.newstru_name.setValue(tebl.newstru_name.getValue()); // 调薪后工资结构名
			sc.newchecklev.setValue(tebl.newchecklev.getValue());// 调薪后绩效考核层级
			sc.newattendtype.setValue(tebl.newattendtype.getValue()); // 调薪后出勤类别
			sc.newcalsalarytype.setValue(scold.newcalsalarytype.getValue()); // 调薪后计薪方式
			sc.newposition_salary.setValue(tebl.newposition_salary.getAsFloatDefault(0) ); // 调薪后职位工资
			sc.newbase_salary.setValue(tebl.newbase_salary.getAsFloatDefault(0)); // 调薪后基本工资
			sc.newtech_salary.setValue(tebl.newtech_salary.getAsFloatDefault(0) ); // 调薪后技能工资
			sc.newachi_salary.setValue(tebl.newachi_salary.getAsFloatDefault(0)); // 调薪后绩效工资
			sc.newotwage.setValue(tebl.newotwage.getAsFloatDefault(0)); // 调薪后固定加班工资
			sc.newtech_allowance.setValue(tebl.newtech_allowance.getAsFloatDefault(0) ); // 调薪后技术津贴
			sc.newparttimesubs.setValue(0); // 调薪后兼职津贴
			sc.newpostsubs.setValue(tebl.newpostsubs.getAsFloatDefault(0)); // 调薪后岗位津贴
			sc.sacrage.setValue(tebl.newposition_salary.getAsFloatDefault(0)+tebl.newtech_allowance.getAsFloatDefault(0)); // 调薪幅度
			sc.chgdate.setValue(teb.tetybdate.getValue()); // 调薪日期
			sc.chgreason.setValue("实习生入职调薪"); // 调薪原因
			sc.orgid.setValue(tebl.orgid.getValue()); // 机构id
			sc.orgname.setValue(tebl.orgname.getValue()); // 机构
			sc.lv_num.setValue(tebl.lv_num.getValue()); // 职级
			sc.ospid.setValue(tebl.ospid.getValue()); // 职位id
			sc.sp_name.setValue(tebl.sp_name.getValue()); // 职位
			sc.save(con);
		}
	}
	
	/**
	 * 实习生转正 薪资变动;
	 * 增量
	 * 
	 * @param con
	 * @param ep
	 * @throws Exception
	 */
	public static void newSalaryChangeLog(CDBConnection con, Hr_traintran_batch ttb) throws Exception {
		// Hr_salary_structure stuc = new Hr_salary_structure();// 新的薪资结构
		// stuc.findByID(ep.newstru_id.getValue());
		// if (stuc.isEmpty())
		// throw new Exception("工资结构【" + ep.newstru_id.getValue() + "】不存在");

		CJPALineData<Hr_traintran_batchline> ttbls = ttb.hr_traintran_batchlines;
		for (CJPABase ttbljpa : ttbls) {
			Hr_traintran_batchline ttbl = (Hr_traintran_batchline) ttbljpa;
			Hr_salary_chglg scold = getCur_salary_chglg(ttbl.er_id.getValue());// 获取现在薪资情况
			Hr_salary_chglg sc = new Hr_salary_chglg();
			sc.er_id.setValue(ttbl.er_id.getValue());
			sc.scatype.setValue(6);// 1 入职定薪、2 调动核薪、3 转正调薪、4 年度调薪、5 特殊调薪、6实习生调薪
			sc.stype.setValue(14); // 来源类别 1 历史数据、2 实习登记、3 入职表单、4 调动表单、5 入职转正、6 调动转正、7个人特殊调薪、8兼职、9批量特殊调薪、10技术津贴、11岗位津贴、12年度调薪、13实习生入职、14实习生转正、15实习生分配....
			sc.sid.setValue(ttb.ttranb_id.getValue()); // 来源ID
			sc.scode.setValue(ttb.ttranb_code.getValue()); // 来源单号

			sc.oldstru_id.setValue(scold.newstru_id.getValue()); // 调薪前工资结构ID
			sc.oldstru_name.setValue(scold.newstru_name.getValue()); // 调薪前工资结构名
			sc.oldchecklev.setValue(scold.newchecklev.getValue());// 调薪前绩效考核层级
			sc.oldattendtype.setValue(scold.newattendtype.getValue()); // 调薪前出勤类别
			sc.oldcalsalarytype.setValue(scold.newcalsalarytype.getValue()); // 调薪前计薪方式
			sc.oldposition_salary.setValue(scold.newposition_salary.getValue()); // 调薪前职位工资
			sc.oldbase_salary.setValue(scold.newbase_salary.getValue()); // 调薪前基本工资
			sc.oldtech_salary.setValue(scold.newtech_salary.getValue()); // 调薪前技能工资
			sc.oldachi_salary.setValue(scold.newachi_salary.getValue()); // 调薪前绩效工资
			sc.oldotwage.setValue(scold.newotwage.getValue()); // 调薪前固定加班工资
			sc.oldtech_allowance.setValue(scold.newtech_allowance.getValue()); // 调薪前技术津贴
			sc.oldparttimesubs.setValue(scold.newparttimesubs.getValue()); // 调薪前兼职津贴
			sc.oldpostsubs.setValue(scold.newpostsubs.getValue()); // 调薪前岗位津贴
			sc.oldavg_salary.setValue(0); // 调薪前平均工资
			sc.newstru_id.setValue(ttbl.newstru_id.getValue()); // 调薪后工资结构ID
			sc.newstru_name.setValue(ttbl.newstru_name.getValue()); // 调薪后工资结构名
			sc.newchecklev.setValue(ttbl.newchecklev.getValue());// 调薪后绩效考核层级
			sc.newattendtype.setValue(ttbl.newattendtype.getValue()); // 调薪后出勤类别
			sc.newcalsalarytype.setValue(scold.newcalsalarytype.getValue()); // 调薪后计薪方式
			sc.newposition_salary.setValue(ttbl.newposition_salary.getAsFloatDefault(0) ); // 调薪后职位工资
			sc.newbase_salary.setValue(ttbl.newbase_salary.getAsFloatDefault(0)); // 调薪后基本工资
			sc.newtech_salary.setValue(ttbl.newtech_salary.getAsFloatDefault(0) ); // 调薪后技能工资
			sc.newachi_salary.setValue(ttbl.newachi_salary.getAsFloatDefault(0)); // 调薪后绩效工资
			sc.newotwage.setValue(ttbl.newotwage.getAsFloatDefault(0)); // 调薪后固定加班工资
			sc.newtech_allowance.setValue(ttbl.newtech_allowance.getAsFloatDefault(0) ); // 调薪后技术津贴
			sc.newparttimesubs.setValue(0); // 调薪后兼职津贴
			sc.newpostsubs.setValue(ttbl.newpostsubs.getAsFloatDefault(0)); // 调薪后岗位津贴
			sc.sacrage.setValue(ttbl.newposition_salary.getAsFloatDefault(0)-ttbl.oldposition_salary.getAsFloatDefault(0)); // 调薪幅度
			sc.chgdate.setValue(ttb.ttranbdate.getValue()); // 调薪日期
			sc.chgreason.setValue("实习生转正调薪"); // 调薪原因
			sc.orgid.setValue(ttbl.orgid.getValue()); // 机构id
			sc.orgname.setValue(ttbl.orgname.getValue()); // 机构
			sc.lv_num.setValue(ttbl.lv_num.getValue()); // 职级
			sc.ospid.setValue(ttbl.ospid.getValue()); // 职位id
			sc.sp_name.setValue(ttbl.sp_name.getValue()); // 职位
			sc.save(con);
		}
	}
	
	/**
	 * 实习生分配 薪资变动;
	 * 增量
	 * 
	 * @param con
	 * @param ep
	 * @throws Exception
	 */
	public static void newSalaryChangeLog(CDBConnection con, Hr_traindisp_batch tdb) throws Exception {
		// Hr_salary_structure stuc = new Hr_salary_structure();// 新的薪资结构
		// stuc.findByID(ep.newstru_id.getValue());
		// if (stuc.isEmpty())
		// throw new Exception("工资结构【" + ep.newstru_id.getValue() + "】不存在");

		CJPALineData<Hr_traindisp_batchline> tdbls = tdb.hr_traindisp_batchlines;
		for (CJPABase tdbljpa : tdbls) {
			Hr_traindisp_batchline tdbl = (Hr_traindisp_batchline) tdbljpa;
			Hr_salary_chglg scold = getCur_salary_chglg(tdbl.er_id.getValue());// 获取现在薪资情况
			Hr_salary_chglg sc = new Hr_salary_chglg();
			sc.er_id.setValue(tdbl.er_id.getValue());
			sc.scatype.setValue(6);// 1 入职定薪、2 调动核薪、3 转正调薪、4 年度调薪、5 特殊调薪、6实习生调薪
			sc.stype.setValue(15); // 来源类别 1 历史数据、2 实习登记、3 入职表单、4 调动表单、5 入职转正、6 调动转正、7个人特殊调薪、8兼职、9批量特殊调薪、10技术津贴、11岗位津贴、12年度调薪、13实习生入职、14实习生转正、15实习生分配....
			sc.sid.setValue(tdb.tdipb_id.getValue()); // 来源ID
			sc.scode.setValue(tdb.tdipb_code.getValue()); // 来源单号

			sc.oldstru_id.setValue(scold.newstru_id.getValue()); // 调薪前工资结构ID
			sc.oldstru_name.setValue(scold.newstru_name.getValue()); // 调薪前工资结构名
			sc.oldchecklev.setValue(scold.newchecklev.getValue());// 调薪前绩效考核层级
			sc.oldattendtype.setValue(scold.newattendtype.getValue()); // 调薪前出勤类别
			sc.oldcalsalarytype.setValue(scold.newcalsalarytype.getValue()); // 调薪前计薪方式
			sc.oldposition_salary.setValue(scold.newposition_salary.getValue()); // 调薪前职位工资
			sc.oldbase_salary.setValue(scold.newbase_salary.getValue()); // 调薪前基本工资
			sc.oldtech_salary.setValue(scold.newtech_salary.getValue()); // 调薪前技能工资
			sc.oldachi_salary.setValue(scold.newachi_salary.getValue()); // 调薪前绩效工资
			sc.oldotwage.setValue(scold.newotwage.getValue()); // 调薪前固定加班工资
			sc.oldtech_allowance.setValue(scold.newtech_allowance.getValue()); // 调薪前技术津贴
			sc.oldparttimesubs.setValue(scold.newparttimesubs.getValue()); // 调薪前兼职津贴
			sc.oldpostsubs.setValue(scold.newpostsubs.getValue()); // 调薪前岗位津贴
			sc.oldavg_salary.setValue(0); // 调薪前平均工资
			sc.newstru_id.setValue(tdbl.newstru_id.getValue()); // 调薪后工资结构ID
			sc.newstru_name.setValue(tdbl.newstru_name.getValue()); // 调薪后工资结构名
			sc.newchecklev.setValue(tdbl.newchecklev.getValue());// 调薪后绩效考核层级
			sc.newattendtype.setValue(tdbl.newattendtype.getValue()); // 调薪后出勤类别
			sc.newcalsalarytype.setValue(scold.newcalsalarytype.getValue()); // 调薪后计薪方式
			sc.newposition_salary.setValue(tdbl.newposition_salary.getAsFloatDefault(0) ); // 调薪后职位工资
			sc.newbase_salary.setValue(tdbl.newbase_salary.getAsFloatDefault(0)); // 调薪后基本工资
			sc.newtech_salary.setValue(tdbl.newtech_salary.getAsFloatDefault(0) ); // 调薪后技能工资
			sc.newachi_salary.setValue(tdbl.newachi_salary.getAsFloatDefault(0)); // 调薪后绩效工资
			sc.newotwage.setValue(tdbl.newotwage.getAsFloatDefault(0)); // 调薪后固定加班工资
			sc.newtech_allowance.setValue(tdbl.newtech_allowance.getAsFloatDefault(0) ); // 调薪后技术津贴
			sc.newparttimesubs.setValue(0); // 调薪后兼职津贴
			sc.newpostsubs.setValue(tdbl.newpostsubs.getAsFloatDefault(0)); // 调薪后岗位津贴
			sc.sacrage.setValue(tdbl.newposition_salary.getAsFloatDefault(0)-tdbl.oldposition_salary.getAsFloatDefault(0)); // 调薪幅度
			Date submitdate = Systemdate.getDateByStr(Systemdate.getStrDate());
			Date chgd;
			Calendar hday = Calendar.getInstance();
			hday.setTime(submitdate);
			int inday = hday.get(Calendar.DATE);
			if(inday<=15){
				chgd=Systemdate.getFirstAndLastOfMonth(submitdate).date1;
			}else{
				Date nowdate=Systemdate.dateMonthAdd(submitdate, 1);
				chgd=Systemdate.getFirstAndLastOfMonth(nowdate).date1;
			}
			sc.chgdate.setValue(Systemdate.getStrDate(chgd)); // 调薪日期
			sc.chgreason.setValue("实习生分配调薪"); // 调薪原因
			sc.orgid.setValue(tdbl.norgid.getValue()); // 机构id
			sc.orgname.setValue(tdbl.norgname.getValue()); // 机构
			sc.lv_num.setValue(tdbl.nlv_num.getValue()); // 职级
			sc.ospid.setValue(tdbl.nospid.getValue()); // 职位id
			sc.sp_name.setValue(tdbl.nsp_name.getValue()); // 职位
			sc.save(con);
		}
	}
	
	/**
	 * 实习生登记 毕业生 薪资变动;
	 * 增量
	 * 
	 * @param con
	 * @param ep
	 * @throws Exception
	 */
	public static void newSalaryChangeLog(CDBConnection con, Hr_train_reg tr) throws Exception {
		Hr_salary_chglg sc = new Hr_salary_chglg();
		Hr_employee emp = new Hr_employee();
		emp.findByID(tr.er_id.getValue());
		if (emp.isEmpty())
			throw new Exception("id为【" + tr.er_id.getValue() + "】的人事资料不存在");
		sc.er_id.setValue(tr.er_id.getValue());
		sc.scatype.setValue(6);// 1 入职定薪、2 调动核薪、3 转正调薪、4 年度调薪、5 特殊调薪、6实习生调薪
		sc.stype.setValue(13); // 来源类别 1 历史数据、2 实习登记、3 入职表单、4 调动表单、5 入职转正、6 调动转正、7个人特殊调薪、8兼职、9批量特殊调薪、10技术津贴、11岗位津贴、12年度调薪、13实习生入职、14实习生转正、15实习生分配....
		sc.sid.setValue(tr.treg_id.getValue()); // 来源ID
		sc.scode.setValue(tr.treg_code.getValue()); // 来源单号

		sc.oldstru_id.setValue(0); // 调薪前工资结构ID
		sc.oldstru_name.setValue(0); // 调薪前工资结构名
		sc.oldchecklev.setValue(0);// 调薪前绩效考核层级
		sc.oldattendtype.setValue(0); // 调薪前出勤类别
		sc.oldcalsalarytype.setValue(0); // 调薪前计薪方式
		sc.oldposition_salary.setValue(0); // 调薪前职位工资
		sc.oldbase_salary.setValue(0); // 调薪前基本工资
		sc.oldtech_salary.setValue(0); // 调薪前技能工资
		sc.oldachi_salary.setValue(0); // 调薪前绩效工资
		sc.oldotwage.setValue(0); // 调薪前固定加班工资
		sc.oldtech_allowance.setValue(0); // 调薪前技术津贴
		sc.oldparttimesubs.setValue(0); // 调薪前兼职津贴
		sc.oldpostsubs.setValue(0); // 调薪前岗位津贴
		sc.oldavg_salary.setValue(0); // 调薪前平均工资
		sc.newstru_id.setValue(tr.newstru_id.getValue()); // 调薪后工资结构ID
		sc.newstru_name.setValue(tr.newstru_name.getValue()); // 调薪后工资结构名
		sc.newchecklev.setValue(tr.newchecklev.getValue());// 调薪后绩效考核层级
		sc.newattendtype.setValue(tr.newattendtype.getValue()); // 调薪后出勤类别
		sc.newcalsalarytype.setValue(emp.pay_way.getValue()); // 调薪后计薪方式
		sc.newposition_salary.setValue(tr.newposition_salary.getAsFloatDefault(0) ); // 调薪后职位工资
		sc.newbase_salary.setValue(tr.newbase_salary.getAsFloatDefault(0)); // 调薪后基本工资
		sc.newtech_salary.setValue(tr.newtech_salary.getAsFloatDefault(0) ); // 调薪后技能工资
		sc.newachi_salary.setValue(tr.newachi_salary.getAsFloatDefault(0)); // 调薪后绩效工资
		sc.newotwage.setValue(tr.newotwage.getAsFloatDefault(0)); // 调薪后固定加班工资
		sc.newtech_allowance.setValue(tr.newtech_allowance.getAsFloatDefault(0) ); // 调薪后技术津贴
		sc.newparttimesubs.setValue(0); // 调薪后兼职津贴
		sc.newpostsubs.setValue(tr.newpostsubs.getAsFloatDefault(0)); // 调薪后岗位津贴
		sc.sacrage.setValue(tr.newposition_salary.getAsFloatDefault(0)+tr.newtech_allowance.getAsFloatDefault(0)); // 调薪幅度
		sc.chgdate.setValue(tr.regdate.getValue()); // 调薪日期
		sc.chgreason.setValue("实习生登记毕业生类型调薪"); // 调薪原因
		sc.orgid.setValue(tr.orgid.getValue()); // 机构id
		sc.lv_num.setValue(tr.lv_num.getValue()); // 职级
		sc.ospid.setValue(tr.ospid.getValue()); // 职位id
		Hr_orgposition op=new Hr_orgposition();
		op.findByID(tr.ospid.getValue());
		if(!op.isEmpty()){
			sc.orgname.setValue(op.orgname.getValue()); // 机构
			sc.sp_name.setValue(op.sp_name.getValue()); // 职位
		}
		sc.save(con);
	}
	
}
