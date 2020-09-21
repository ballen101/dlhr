package com.hr.util;

import java.net.InetAddress;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.JSONParm;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.PraperedSql;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.CNotifyUEInfo;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.cjpa.CWFNotify.NotityType;
import com.corsair.server.csession.CToken;
import com.corsair.server.generic.Shworg;
import com.corsair.server.generic.Shwuser;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.corsair.server.wordflow.Shwwfprocuser;
import com.corsair.server.wordflow.Shwwftempproc;
import com.corsair.server.wordflow.Shwwftempprocuser;
import com.hr.attd.entity.Hrkq_holidayapp;
import com.hr.base.ctr.CtrBaseOrg;
import com.hr.base.ctr.CtrHr_standposition;
import com.hr.base.entity.Hr_standposition;
import com.hr.salary.entity.Hr_salary_hotsub_qual;
import com.hr.salary.entity.Hr_salary_linestarget;
import com.hr.salary.entity.Hr_salary_teamaward;
import com.hr.util.hrmail.DLHRMailCenterWS;
import com.hr.util.hrmail.Hr_emailsend_log;
import com.hr.util.hrmail.Thread_HRMailCenter;

//只有一个实例
public class HRJPAEventListener extends JPAController {
	@Override
	public String OngetNewCode(CJPABase jpa, int codeid) {
		if (jpa instanceof Hr_standposition) {
			return CtrHr_standposition.getNewCode((Hr_standposition) jpa);
		}
		return null;
	}

	@Override
	public void BeforeSave(CJPABase jpa, CDBConnection con, boolean selfLink) throws Exception {
		// Logsw.debug("BeforeSave " + jpa.getClass().toString());
		if (jpa instanceof Shworg)
			CtrBaseOrg.BeforeSave(con, (Shworg) jpa);
		// if (jpa instanceof Hr_standposition)
		// CtrHr_standposition.PBeforeSave(con, (Hr_standposition) jpa);
		if (jpa instanceof Hrkq_holidayapp) {
			Hrkq_holidayapp ha = (Hrkq_holidayapp) jpa;
			if (ha.timeedtrue.isEmpty())
				ha.timeedtrue.setValue(ha.timeed.getValue());
		}
	}

	@Override
	public void OnSave(CJPABase jpa, CDBConnection con, ArrayList<PraperedSql> sqllist, boolean selfLink) throws Exception {
		// Logsw.debug("OnSave " + jpa.getClass().toString());
	}

	@Override
	public void OnDelete(CJPABase jpa, CDBConnection con, boolean selfLink) throws Exception {
		// Logsw.debug("OnDelete " + jpa.getClass().toString());
		if (jpa instanceof Shworg)
			CtrBaseOrg.BeforeDelete(con, (Shworg) jpa);
	}

	@Override
	public List<HashMap<String, String>> OnPrintDBData2Excel(CJPABase jpa, String mdfname) {
		// Logsw.debug("OnPrintDBData2Excel " + jpa.getClass().toString());
		return null;
	}

	// JPA /////////////////////
	@Override
	public Shwuser OnWFFindUserByPosition(CJPA jpa, Shwwf wf, Shwwfproc proc, Shwwftempprocuser puser) throws Exception {
		// Logsw.debug("OnWFFindUserByPosition " + jpa.getClass().toString());
		return null;
	}

	@Override
	public void BeforeWFStart(CJPA jpa, String wftempid, CDBConnection con) throws Exception {
		// Logsw.debug("BeforeWFStart " + jpa.getClass().toString());
	}

	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		// Logsw.debug("BeforeWFStartSave " + jpa.getClass().toString());
	}

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished) throws Exception {
		// Logsw.debug("AfterWFStart " + jpa.getClass().toString());
	}

	@Override
	public void OnArriveProc(CJPA jpa, Shwwf wf, Shwwfproc proc, CDBConnection con, ArraiveType at) throws Exception {
		// Logsw.debug("OnArriveProc " + jpa.getClass().toString());
		HRUtil.OnArriveProcSendMail(wf, proc);
	}

	@Override
	public void OnLiveProc(CJPA jpa, Shwwf wf, Shwwfproc proc, CDBConnection con, ArraiveType at) throws Exception {
		// Logsw.debug("OnLiveProc " + jpa.getClass().toString());
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc, Shwwfproc nxtproc, boolean isFilished) throws Exception {
		// Logsw.debug("OnWfSubmit " + jpa.getClass().toString());
		if (isFilished) {
			try {
				// checkwfpuem(con, wf);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 流程通过机构查找类型，获取机构列表时候
	 * 
	 * @param wftemproc
	 * @param temuser
	 * @param userfindcdt
	 * @param userid
	 * @param entid
	 * @return 返回NULL将调用默认方法,不为NULL的返回值将作为流程处理条件
	 */
	@Override
	public CJPALineData<Shworg> OnWfFindCDTOrgs(CJPA jpa, Shwwftempproc wftemproc, Shwwftempprocuser temuser, int userfindcdt, String userid, String entid) throws Exception {
		return HRJPAWFFindOrg.OnWfFindCDTOrgs(jpa, wftemproc, temuser, userfindcdt, userid, entid);
	}

	// 流程完成 检查所有通知是否需要处理
//	private void checkwfpuem(CDBConnection con, Shwwf wf) throws Exception {
//		String sqlstr = "SELECT * FROM hr_emailsend_log l WHERE mailtype='待办' AND approvaldate IS NULL "
//				+ " AND EXISTS(SELECT 1 wfprocuserid FROM shwwfprocuser WHERE l.extid=shwwfprocuser.wfprocuserid AND wfid=" + wf.wfid.getValue() + " AND userid IS NOT NULL)";
//		CJPALineData<Hr_emailsend_log> els = new CJPALineData<Hr_emailsend_log>(Hr_emailsend_log.class);
//		els.findDataBySQL(con, sqlstr, false, false, -1, 1000, false);
//		Date dt = new Date();
//		String approvaldate = Systemdate.getStrDateByFmt(dt, "yyyy-MM-dd") + "T" + Systemdate.getStrDateByFmt(dt, "HH:mm:ss.SSS") + "Z";
//		for (CJPABase jpa : els) {
//			try {
//				Hr_emailsend_log el = (Hr_emailsend_log) jpa;
//				el.approvalman.setValue("HRMS");
//				el.approvaldate.setValue(approvaldate);
//				DLHRMailCenterWS.updatemail(el.aynemid.getValue(), "HRMS", approvaldate);
//				el.pushsuccess.setAsInt(4);
//				el.save(con);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}

	// 提交
	@Override
	public void OnWfUserSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc, Shwwfprocuser puser, Shwwfproc nxtproc, boolean isFilished) {
		try {
			if (puser != null) {
				// Hr_emailsend_log el = new Hr_emailsend_log();
				// String sqlstr = "select * from hr_emailsend_log where extid='" + puser.wfprocuserid.getValue() + "' FOR UPDATE";
				// el.findBySQL4Update(con, sqlstr, false);
				// if (!el.isEmpty()) {
				Shwuser user = new Shwuser();
				user.clear();
				user.findByID(puser.userid.getValue());
				if (!user.isEmpty()) {
					Date dt = new Date();
					String approvaldate = Systemdate.getStrDateByFmt(dt, "yyyy-MM-dd") + "T" + Systemdate.getStrDateByFmt(dt, "HH:mm:ss.SSS") + "Z";
					Thread_HRMailCenter tc = new Thread_HRMailCenter(2, puser.wfprocuserid.getValue(), user.displayname.getValue(), approvaldate);
					tc.start();
					// el.pushsuccess.setAsInt(3);// pushsuccess 推送状态 1 新建未推送 2 新建推送完成 3 更新失败 4 更新完成
//						el.approvalman.setValue(user.displayname.getValue());
//						el.approvaldate.setValue(approvaldate);
//						el.save(con);
					// 给调度发
//						DLHRMailCenterWS.updatemail(el.aynemid.getValue(), user.displayname.getValue(), approvaldate);
//						el.pushsuccess.setAsInt(4);
//						el.save(con);
				}
				// }
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void OnWfReject(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc, Shwwfproc nxtproc) throws Exception {
		// Logsw.debug("OnWfReject " + jpa.getClass().toString());
	}

	@Override
	public void OnWfBreak(CJPA jpa, Shwwf wf, CDBConnection con) throws Exception {
		// Logsw.debug("OnWfBreak " + jpa.getClass().toString());
		try {
			HRUtil.sendProcBrockMail(wf);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void OnWfTransfer(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc, Shwuser fruser, Shwuser touser) throws Exception {

	}

	// WF通知
	@Override
	public void OnWFNotityMail(NotityType nt, CJPABase jpa, Shwwf wf, Shwwfproc proc, List<CNotifyUEInfo> uis, Shwwfprocuser puser, String actuserid) {
		int ljavm = 525600;// 24*60*365; 1年
		try {
			if (nt == NotityType.ntPress) {
				return; // 催办不发邮件
			}
			Shwuser actuser = new Shwuser();
			actuser.findByID(actuserid);
			if (actuser.displayname.isEmpty())
				actuser.displayname.setValue("SYSTEM");
			String subject = "";
			String emtp = "";
			if (nt == NotityType.ntTransfer) {// 转办，删除原来的通知
				HRUtil.delMailByProceUser(puser);
			}

			if ((nt == NotityType.ntArrive) || (nt == NotityType.ntTransfer)) { // 转办也要发送代办通知
				subject = "您在HRMS系统有工作流【" + wf.wfname.getValue() + "】:" + wf.subject.getValue() + "需要您审核，请及时处理！FM:HRMS系统";
				emtp = "待办";
			}
			if (nt == NotityType.ntFinish) {
				subject = "您在HRMS系统工作流【" + wf.wfname.getValue() + "】:" + wf.subject.getValue() + "已完成审批!FM:HRMS系统";
				emtp = "通知";
			}
			if (nt == NotityType.ntBreak) {
				subject = "您在HRMS系统工作流【" + wf.wfname.getValue() + "】已中断,执行人【" + actuser.displayname.getValue() + "】:"
						+ wf.subject.getValue() + "!FM:HRMS系统";
				emtp = "通知";
			}
			if (nt == NotityType.ntRefuse) {
				subject = "您在HRMS系统工作流【" + wf.wfname.getValue() + "】被驳回,执行人【" + actuser.displayname.getValue() + "】:"
						+ wf.subject.getValue() + "!FM:HRMS系统";
				emtp = "通知";
			}
			if (nt == NotityType.ntPress) {
				subject = "您在HRMS系统工作流【" + wf.wfname.getValue() + "】:" + wf.subject.getValue() + "到期催办，请及时处理!FM:HRMS系统";
				emtp = "催办";
			}

			String sysurl = ConstsSw.getSysParm("APPURL");
			String url = sysurl.substring(0, sysurl.lastIndexOf("/") + 1);
			Shwuser user = new Shwuser();
			for (CNotifyUEInfo ui : uis) {
				user.clear();
				user.findByID(ui.getUserid());
				if (!user.email.isEmpty()) {
					int validmit = 525600;// 24*60*365; 1年
					String utoken = CToken.createUToken(user, validmit);
					String purl = url + "common/shwwf.html?wfid=" + wf.wfid.getValue() + "&showtype=" + ((wf.mwwfstype.isEmpty()) ? 1 : wf.mwwfstype.getValue())
							+ "&utoken=" + utoken + "&sc=xtbg";
					//System.out.println("purl:" + purl);
					String content = buildMailContent(nt, wf, user, subject, actuser.displayname.getValue(), ljavm, purl);
					Hr_emailsend_log ml = new Hr_emailsend_log();
					ml.aynemid.setValue(DLHRMailCenterWS.getNewCode(117));// 同步ID
					ml.employee_code.setValue(user.username.getValue()); // 工号
					ml.employee_name.setValue((user.displayname.isEmpty()) ? user.username.getValue() : user.displayname.getValue()); // 姓名
					ml.address.setValue(user.email.getValue()); // 收件人
					ml.extid.setValue(ui.getWfprocuserid()); // 扩展ID 保存wfprocuserid
					ml.address_bcc.setValue(null); // 抄送
					ml.address_cc.setValue(null); // 秘送
					ml.approvaldate.setValue(null); // 回写审批时间
					ml.approvalman.setValue(null); // 回写审批人
					ml.mailbody.setValue(content); // 邮件内容
					ml.mailsubject.setValue("Dear " + user.displayname.getValue() + "," + subject); // 邮件标题
					ml.mailtype.setValue(emtp); // 邮件类型
					ml.wfurl.setValue(purl); // 审批URL
					ml.entid.setValue("1"); // entid
					ml.creator.setValue("HRMS"); // 创建人
					ml.createtime.setAsDatetime(new Date()); // 创建时间
					ml.pushsuccess.setAsInt(1);// 推送状态 1 新建未推送 2 新建推送完成 3 更新失败 4 更新完成

					Thread_HRMailCenter tc = new Thread_HRMailCenter(1, ml);
					tc.start();
					// ml.save(); //保存慢 放线程

					// 给调度发
//					String synid = DLHRMailCenterWS.sendmailex(ml);
//					if (synid != null) {
//						ml.emsdid.setValue(synid);// ID
//						ml.pushsuccess.setAsInt(2);
//						ml.save();
//					}
					// CMailInfo mi = new CMailInfo();
					// mi.setSubject("Dear " + user.displayname.getValue() + "," + subject);
					// String tostr = CMail.formatAddress(user.displayname.getValue(), user.email.getValue());
					// mi.getToMails().add(tostr);
					// String content = buildMailContent(nt, wf, user, subject, actuser.displayname.getValue(), ljavm);
					// mi.setContent(content);
					// SendMailThread sm = new SendMailThread(mi);
					// sm.start();
					Logsw.debug("发送流程提醒邮件【" + user.displayname.getValue() + "】");
				} else
					Logsw.debug("发送流程提醒邮件【" + user.displayname.getValue() + "】没设置邮件地址");

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String buildMailContent(NotityType nt, Shwwf wf, Shwuser user, String subject, String actusername, int ljavm, String purl)
			throws Exception {
		String sign = ConstsSw.getSysParm("WFNotifySign");
		String content = "<div style='font-family:\"Microsoft Yahei\";font-size:12px;'>";
		// String
		content = "Dear " + user.displayname.getValue() + ":<br><br>";
		content = content + "   您好!<br>";
		content = content + "   您在HRMS系统工作流【" + wf.wfname.getValue() + "】:" + wf.subject.getValue();
		if ((nt == NotityType.ntArrive) || (nt == NotityType.ntTransfer)) { // 转办也要发送代办通知
			content = content + "需要您审核,";
		}
		if (nt == NotityType.ntFinish) {
			content = content + "审批完成,";
		}
		if (nt == NotityType.ntBreak) {
			content = content + "被中断,执行人【" + actusername + "】,";
		}
		if (nt == NotityType.ntRefuse) {
			content = content + "被驳回,执行人【" + actusername + "】,";
		}
		if (nt == NotityType.ntPress) {
			content = content + "催办,执行人【" + actusername + "】,";
		}
		content = content + "请点击以下链接进入系统处理！<br>";

		content = content + "   <a href='" + purl + "'>" + wf.subject.getValue() + "</a>";
		content = content + "<br>  如果以上链接不能正常打开，请拷贝此链接地址至浏览器打开执行：<br>";
		content = content + "  http://192.168.117.132/dlhr/webapp<br><br>";
		content = content + "以上，请知悉！ <br><br>";
		content = content + "发件人:" + ((sign == null) ? "" : sign) + "<br>";
		// content = content + "邮件由系统自动发送，请勿直接回复<br>";
		InetAddress ia = InetAddress.getLocalHost();
		content = content + "<div style='display: none'>" + ia.getHostAddress() + "</div></div>";
		return content;
	}

	// @Override
	// public void OnWFNotitySms(NotityType nt, CJPABase jpa, Shwwf wf, Shwwfproc proc, List<String> uids) {
	// Logsw.debug("OnWFNotityMail " + jpa.getClass().toString());
	// }
	//
	// @Override
	// public void OnWFNotityWechat(NotityType nt, CJPABase jpa, Shwwf wf, Shwwfproc proc, List<String> uids) {
	// Logsw.debug("OnWFNotityMail " + jpa.getClass().toString());
	// }

	// 通用CO 控制接口
	// edittps:{"isedit":true,"issubmit":true,"isview":true,"isupdate":false,"isfind":true}
	// 不为空的返回值，将作为查询结果返回给前台
	@Override
	public String OnCCoFindList(Class<CJPA> jpaclass, List<JSONParm> jps, HashMap<String, String> edts, boolean enableIdpath, boolean selfline)
			throws Exception {
		// Logsw.debug("OnCCoFindList " + jpaclass.toString());
		return null;
	}

	// 通用 CO 控制接口
	@Override
	public String OnCCoFindBuildWhere(CJPA jpa, HashMap<String, String> urlparms) throws Exception {
		if (urlparms.containsKey("spcetype")) {// 特殊数据 0||undef 非特殊 1 个人所属 2 个人创建
			int spcetype = Integer.valueOf(urlparms.get("spcetype"));
			if (spcetype == 0)
				return null;
			else
				return HRSpecDataFilter.getspsqlwhere(jpa, spcetype);
		}
		return null;
	}

	// 不为空的返回值，将作为查询结果返回给前台
	@Override
	public String OnCCoFindByID(Class<CJPA> jpaclass, String id) throws Exception {
		// Logsw.debug("OnCCoFindByID " + jpaclass.toString());
		return null;
	}

	// 不为空的返回值，将作为查询结果返回给前台
	@Override
	public String OnCCoSave(CDBConnection con, CJPA jpa) throws Exception {
		// Logsw.debug("OnCCoSave " + jpa.getClass().toString());
		HRJPAWFFindOrg.OnJPASaveChg(con, jpa);
		return null;
	}

	// 不为空的返回值，将作为查询结果返回给前台
	@Override
	public String OnCCoCopy(Class<CJPA> jpaclass, HashMap<String, String> pparms) throws Exception {
		// Logsw.debug("OnCCoCopy " + jpaclass.toString());
		return null;
	}

	// 不为空的返回值，将作为查询结果返回给前台
	@Override
	public String OnCCoDel(CDBConnection con, Class<CJPA> jpaclass, String id) throws Exception {
		// Logsw.debug("OnCCoDel " + jpaclass.toString());
		return null;
	}

	// 不为空的返回值，将作为查询结果返回给前台
	@Override
	public String OnCCoCreateWF(CJPA jpa, String wftempid, String jpaid) throws Exception {
		// Logsw.debug("OnCCoCreateWF " + jpa.getClass().toString());
		return null;
	}

	// 不为空的返回值，将作为查询结果返回给前台
	@Override
	public String OnCCoSubmitWF(CJPA jpa, String procid, String aoption) throws Exception {
		// Logsw.debug("OnCCoSubmitWF " + jpa.getClass().toString());
		return null;
	}

	// 不为空的返回值，将作为查询结果返回给前台
	@Override
	public String OnCCoRejectWF(CJPA jpa, String fprocid, String tprocid, String aoption) throws Exception {
		// Logsw.debug("OnCCoRejectWF " + jpa.getClass().toString());
		return null;
	}

	// 不为空的返回值，将作为查询结果返回给前台
	@Override
	public String OnCCoBreakWF(CJPA jpa) throws Exception {
		// Logsw.debug("OnCCoBreakWF " + jpa.getClass().toString());
		return null;
	}

	// 不为空的返回值，将作为查询结果返回给前台
	@Override
	public String OnCCoTransferWF(CJPA jpa, String tuserid, String aoption) throws Exception {
		// Logsw.debug("OnCCoTransferWF " + jpa.getClass().toString());
		return null;
	}

	@Override
	public String OnCCoVoid(CDBConnection con, CJPA jpa) throws Exception {
		if((jpa instanceof Hr_salary_hotsub_qual)||(jpa instanceof Hr_salary_linestarget)||(jpa instanceof Hr_salary_teamaward)){
			return null;
		}
		if (!hasaccrole3Or14()) {
			throw new Exception("您无作废此单据权限");
		}
		return null;
	}

	// end 通用Co

	// 是否是人事经理或专员
	public static boolean hasaccrole3Or14() throws Exception {
		String userid = CSContext.getUserID();
		Shwuser user = new Shwuser();
		user.findByID(userid, false);
		if (user.isEmpty())
			throw new Exception("获取当前登录用户错误!");
		if (user.usertype.getAsIntDefault(0) != 1) {// 不是管理员
			String sqlstr = "SELECT IFNULL(COUNT(*),0) ct FROM shwroleuser where (roleid=3 or roleid=14) AND userid=" + userid; //
			return (Integer.valueOf(DBPools.defaultPool().openSql2List(sqlstr).get(0).get("ct")) > 0);
		} else
			return true;
	}

}
