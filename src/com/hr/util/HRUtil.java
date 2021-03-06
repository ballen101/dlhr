package com.hr.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.CDBPool;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.generic.Shworg;
import com.corsair.server.generic.Shwuser;
import com.corsair.server.mail.CMailInfo;
import com.corsair.server.util.HttpTookit;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.corsair.server.wordflow.Shwwfprocuser;
import com.hr.perm.entity.Hr_employee_transfer;
import com.hr.perm.entity.Hr_entry_prob;
import com.hr.perm.entity.Hr_transfer_prob;
import com.hr.salary.entity.Hr_salary_specchgsa;
import com.hr.util.hrmail.DLHRMailCenterWS;
import com.hr.util.hrmail.Hr_emailsend_log;

public class HRUtil {

	public static boolean RunSendWxflag=true;//微信推送变量
	public static boolean RunSendWxPersonflag=true;//推送人员考勤数据
	public static boolean RunCalcKqDepartFlag=true;//运算部门考勤数据

	public static CDBPool getReadPool() {
		return DBPools.poolByName("dlhrread");
	}

	// 机构人事层级
	public static int getOrgHrLev(String orgid) throws Exception {
		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty()) {
			throw new Exception("ID为【" + orgid + "】的机构不存在");
		}
		if (org.idpath.isEmpty())
			throw new Exception("机构【" + org.code.getValue() + "】的IDPATH为空");

		String idps = org.idpath.getValue();
		idps = idps.substring(0, idps.length() - 1);
		String sqlstr = "select * from shworg where orgid in(" + idps + ")";
		CJPALineData<Shworg> orgs = new CJPALineData<Shworg>(Shworg.class);
		orgs.findDataBySQL(sqlstr, true, false);
		int hrlev = 1;
		for (CJPABase jpa : orgs) {
			Shworg o = (Shworg) jpa;
			if (o.orgtype.getAsIntDefault(0) == 6) {
				hrlev = 3;
				break;
			}
		}
		if (hrlev == 1) {
			for (CJPABase jpa : orgs) {
				Shworg o = (Shworg) jpa;
				if (o.orgtype.getAsIntDefault(0) == 4) {
					hrlev = 2;
					break;
				}
			}
		}
		return hrlev;
	}

	public static JSONArray getOrgsByType(String orgtypes) throws Exception {
		String sqlstr = "SELECT * FROM shworg WHERE usable=1 " + CSContext.getIdpathwhere()
		+ "AND orgtype IN (" + orgtypes + ") ORDER BY orgid ";
		Shworg org = new Shworg();
		return org.pool.opensql2json_O(sqlstr);
	}

	/**
	 * @param orgid
	 * @param includechild //是否包含子机构
	 * @return 获取机构数组
	 * @throws Exception
	 */
	public static JSONArray getOrgsByOrgid(String orgid, boolean includechild, List<String> yearmonths) throws Exception {
		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty())
			throw new Exception("ID为【" + orgid + "】的机构不存在");
		JSONArray orgs = new JSONArray();
		if (includechild) {
			String sqlstr = "SELECT * FROM shworg WHERE  usable=1 " + CSContext.getIdpathwhere()
			+ "AND superid=" + orgid + " ORDER BY orgid ";
			JSONArray orgjos = org.pool.opensql2json_O(sqlstr);
			for (int i = 0; i < orgjos.size(); i++) {
				orgjos.getJSONObject(i).put("_incc", 1);
			}
			orgs = orgjos;
		}
		JSONObject orgjo = org.toJsonObj();
		if (includechild) {
			orgjo.put("_incc", 2);
		} else {
			orgjo.put("_incc", 1);
		}
		orgs.add(0, orgjo);
		JSONArray rst = new JSONArray();
		for (String yearmonth : yearmonths) {
			for (int i = 0; i < orgs.size(); i++) {
				orgs.getJSONObject(i).put("yearmonth", yearmonth);
			}
			rst.addAll(orgs);
		}
		return rst;
	}

	public static List<String> buildYeatMonths(String yearmonth_begin, String yearmonth_end, int maxm) throws Exception {
		Date bgdate = Systemdate.getDateByStr(yearmonth_begin);
		Date eddate = Systemdate.getDateByStr(yearmonth_end);
		if (eddate.getTime() < bgdate.getTime())
			throw new Exception("开始月度大于截止月度");
		List<String> rst = new ArrayList<String>();
		Date tempDate = bgdate;
		while (tempDate.getTime() <= eddate.getTime()) {
			System.out.println("yearmonths:" + Systemdate.getStrDateByFmt(tempDate, "yyyy-MM"));
			rst.add(Systemdate.getStrDateByFmt(tempDate, "yyyy-MM"));
			tempDate = Systemdate.dateMonthAdd(tempDate, 1);
		}
		if (rst.size() > maxm)
			throw new Exception("不能超过" + maxm + "个月");
		return rst;
	}

	public static JSONArray getOrgsByPid(String superid) throws Exception {
		String sqlstr = "SELECT * FROM shworg WHERE usable=1 " + CSContext.getIdpathwhere()
		+ "AND superid=" + superid + " ORDER BY orgid ";
		Shworg org = new Shworg();
		return org.pool.opensql2json_O(sqlstr);
	}

	public static void OnArriveProcSendMail(Shwwf wf, Shwwfproc proc) {

	}

	public static void sendProcBrockMail(Shwwf wf) throws Exception {
		String sqlstr = "SELECT DISTINCT * FROM( SELECT l.* FROM hr_emailsend_log l, shwwfprocuser u WHERE l.extid=u.procid AND u.wfid=" + wf.wfid.getValue() + ") tb";
		CJPALineData<Hr_emailsend_log> els = new CJPALineData<Hr_emailsend_log>(Hr_emailsend_log.class);
		els.findDataBySQL(sqlstr);
		for (CJPABase jpa : els) {
			Hr_emailsend_log el = (Hr_emailsend_log) jpa;
			System.out.println("delMail:" + el.aynemid.getValue() + " sqlstr:" + sqlstr);
			DLHRMailCenterWS.delMail(el.aynemid.getValue());
			el.removed.setAsInt(1);
			el.save();
		}
	}

	private static void sendMail(CMailInfo minfo) {
		HttpTookit ht = new HttpTookit();
		String url = "http://192.168.117.65:5556/InterFaces/SendEmail";
		// ht.doPost(url, params)
	}

	/**
	 * 设置系统流程代理 如果原没有开始时间或原开始时间大于需要的设置的开始时间：设置开始时间 如果原没有截止时间或原截止时间小于需要设置的截止时间：设置截止时间；
	 * 
	 * @param con
	 * @param empcode
	 * @param bgdate
	 * @param eddate
	 */
	public static void setWFAgend(CDBConnection con, String empcode, Date bgdate, Date eddate) {
		try {
			Shwuser user = new Shwuser();
			String sqlstr = "select * from shwuser where username='" + empcode + "'";
			user.findBySQL(con, sqlstr, false);
			if (user.isEmpty())
				return;
			Date gst = user.gooutstarttime.getAsDatetime();
			Date ged = user.gooutendtime.getAsDatetime();
			if ((gst == null) || (gst.getTime() > bgdate.getTime()))
				user.gooutstarttime.setAsDatetime(bgdate);
			if ((ged == null) || (ged.getTime() < eddate.getTime()))
				user.gooutendtime.setAsDatetime(eddate);
			user.goout.setAsInt(1);
			user.save(con, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检查无效的流程通知
	 */

	public static void delMailByProceUser(Shwwfprocuser puser) {
		try {
			String sqlstr = "SELECT * FROM hr_emailsend_log  WHERE extid=" + puser.wfprocuserid.getValue();
			CJPALineData<Hr_emailsend_log> els = new CJPALineData<Hr_emailsend_log>(Hr_emailsend_log.class);
			els.findDataBySQL(sqlstr);
			for (CJPABase jpa : els) {
				Hr_emailsend_log el = (Hr_emailsend_log) jpa;
				System.out.println("delMail:" + el.aynemid.getValue() + " sqlstr:" + sqlstr);
				DLHRMailCenterWS.delMail(el.aynemid.getValue());
				el.removed.setAsInt(1);
				el.save();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param roleids 角色ID 用逗号分隔
	 * @return 登录用户是否包含角色;管理员包含任何角色
	 * @throws Exception
	 */
	public static boolean hasRoles(String roleids) throws Exception {
		String userid = CSContext.getUserIDNoErr();
		if ((userid == null) || (userid.isEmpty()))
			return false;
		Shwuser user = new Shwuser();
		user.findByID(userid, false);
		if (user.isEmpty())
			throw new Exception("获取当前登录用户错误!");
		if (user.usertype.getAsIntDefault(0) != 1) {// 不是管理员
			String sqlstr = "SELECT IFNULL(COUNT(*),0) ct FROM shwroleuser where roleid in (" + roleids + ") AND userid=" + userid; //
			return (Integer.valueOf(DBPools.defaultPool().openSql2List(sqlstr).get(0).get("ct")) > 0);
		} else
			return true;
	}

	/**
	 * @param roleids 岗位ID 用逗号分隔
	 * @return 登录用户是否包含岗位;
	 * @throws Exception
	 */
	public static boolean hasPostions(String positionids) throws Exception {
		String userid = CSContext.getUserIDNoErr();
		if ((userid == null) || (userid.isEmpty()))
			return false;
		Shwuser user = new Shwuser();
		user.findByID(userid, false);
		if (user.isEmpty())
			throw new Exception("获取当前登录用户错误!");
		String sqlstr = "SELECT IFNULL(COUNT(*),0) ct FROM shwpositionuser WHERE userid=" + userid + " AND positionid IN(" + positionids + ")"; //
		return (Integer.valueOf(DBPools.defaultPool().openSql2List(sqlstr).get(0).get("ct")) > 0);

	}

	/**
	 * @param tp 1 试用转正 2 调动 3调动转正
	 * @param salary_quotacode
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static void issalary_quotacode_used(int tp, String salary_quotacode, String id) throws Exception {
		if ((salary_quotacode == null) || salary_quotacode.trim().isEmpty())
			return;
		String sqlstr = null;
		sqlstr = "SELECT * FROM hr_entry_prob WHERE stat>1 AND stat<=9 AND salary_quotacode='" + salary_quotacode + "'";
		if (tp == 1)
			sqlstr = sqlstr + " and pbtid<>" + id;
		Hr_entry_prob jpa = new Hr_entry_prob();
		jpa.findBySQL(sqlstr);
		if (!jpa.isEmpty())
			throw new Exception("可用工资额度编号【" + salary_quotacode + "】已经被编号为【" + jpa.pbtcode.getValue() + "】的【入职转正】表单使用");

		sqlstr = "SELECT * FROM hr_employee_transfer WHERE stat>1 AND stat<=9 AND salary_quotacode='" + salary_quotacode + "'";
		if (tp == 2)
			sqlstr = sqlstr + " and emptranf_id<>" + id;
		Hr_employee_transfer jpa1 = new Hr_employee_transfer();
		jpa1.findBySQL(sqlstr);
		if (!jpa1.isEmpty())
			throw new Exception("可用工资额度编号【" + salary_quotacode + "】已经被编号为【" + jpa1.emptranfcode.getValue() + "】的【调动】表单使用");

		sqlstr = "SELECT * FROM hr_transfer_prob WHERE stat>1 AND stat<=9 AND salary_quotacode='" + salary_quotacode + "'";
		if (tp == 3)
			sqlstr = sqlstr + " and pbtid<>" + id;
		Hr_transfer_prob jpa2 = new Hr_transfer_prob();
		jpa2.findBySQL(sqlstr);
		if (!jpa2.isEmpty())
			throw new Exception("可用工资额度编号【" + salary_quotacode + "】已经被编号为【" + jpa2.pbtcode.getValue() + "】的【调动转正】表单使用");
		sqlstr = "SELECT * FROM Hr_salary_specchgsa WHERE stat>1 AND stat<=9 AND salary_quotacode='" + salary_quotacode + "'";
		if(tp==4)
			sqlstr = sqlstr + " and scsid<>" + id;
		Hr_salary_specchgsa jpa3=new Hr_salary_specchgsa();
		jpa3.findBySQL(sqlstr);
		if (!jpa3.isEmpty())
			throw new Exception("可用工资额度编号【" + salary_quotacode + "】已经被编号为【" + jpa3.scscode.getValue() + "】的【特殊调薪】表单使用");
	}

	/**
	 * 获取年龄 精确到日
	 * 
	 * @param birthday
	 * @return
	 * @throws Exception
	 * @throws NumberFormatException
	 */
	public static float getAgeByBirth(Date birthday) throws Exception {
		String sqlstr = "SELECT TIMESTAMPDIFF(YEAR, '" + Systemdate.getStrDateyyyy_mm_dd(birthday) + "', CURDATE()) age";
		return Integer.valueOf(DBPools.defaultPool().openSql2List(sqlstr).get(0).get("age").toString());

		// int age = 0;
		// try {
		// Calendar now = Calendar.getInstance();
		// now.setTime(new Date());// 当前时间
		// Calendar birth = Calendar.getInstance();
		// birth.setTime(birthday);
		// if (birth.after(now)) {// 如果传入的时间，在当前时间的后面，返回0岁
		// age = 0;
		// } else {
		// age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);// - 1;
		// System.out.println("now.get(Calendar.MONTH):" + now.get(Calendar.MONTH));
		// System.out.println("now.get(Calendar.MONTH):" + birth.get(Calendar.MONTH));
		// if (now.get(Calendar.MONTH) >= birth.get(Calendar.MONTH)) {
		// System.out.println("now.get(Calendar.DAY_OF_MONTH):" + now.get(Calendar.DAY_OF_MONTH));
		// System.out.println("now.get(Calendar.DAY_OF_MONTH):" + birth.get(Calendar.DAY_OF_MONTH));
		// if (now.get(Calendar.DAY_OF_MONTH) >= birth.get(Calendar.DAY_OF_MONTH)) {
		// age += 1;
		// }
		// }
		// }
		// if (age < 0)
		// age = 0;
		// return age;
		// } catch (Exception e) {// 兼容性更强,异常后返回数据
		// return 0;
		// }
	}


	/*
	 * 转换成SQL IN
	 */
	public  static String tranInSql(List<String>ids){
		StringBuffer idsStr = new StringBuffer();
		for (int i = 0; i < ids.size(); i++) {
			if (i > 0) {
				idsStr.append(",");
			}
			idsStr.append("'").append(ids.get(i)).append("'");
		}
		//System.out.print(idsStr.toString());
		return idsStr.toString();

	}

	/**
	 * 获取子级
	 * @param orgid 机构id
	 * @param level 下级层数
	 * @return
	 * @throws Exception 
	 */
	public static List<Shworg> getChildByOrg(Shworg orgHead,int level) throws Exception{
		List<Shworg>orglist=new ArrayList<Shworg>();
		orglist.add(orgHead);
		String sqlstr = "select * from shworg where usable='1' and  superid='" + orgHead.orgid.getValue() + "'";
		List<HashMap<String, String>> oneList = HRUtil.getReadPool().openSql2List(sqlstr);
		System.out.print("level="+level);
		if(level>0){
			for(HashMap<String, String>entity : oneList){
				Shworg org = new Shworg();
				org.extorgname.setValue(entity.get("extorgname"));
				org.idpath.setValue(entity.get("idpath"));
				org.orgid.setValue(entity.get("orgid"));
				org.code.setValue(entity.get("code"));
				orglist.add(org);
				//System.out.print("orgname="+org.extorgname.getValue());
				if(level>1){
					String twosqlstr = "select * from shworg  where usable='1' and  superid='" + org.orgid.getValue() + "'";
					List<HashMap<String, String>> twoList = HRUtil.getReadPool().openSql2List(twosqlstr);
					for(HashMap<String, String>entity2 : twoList){
						Shworg org2 = new Shworg();
						org2.extorgname.setValue(entity2.get("extorgname"));
						org2.idpath.setValue(entity2.get("idpath"));
						org2.orgid.setValue(entity2.get("orgid"));
						org2.code.setValue(entity2.get("code"));
						orglist.add(org2);
						//System.out.print("orgname="+org2.extorgname.getValue());
					}
				}
			}
		}
		return orglist;

	}
	

}


