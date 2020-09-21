package com.corsair.server.cjpa;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.corsair.cjpa.CJPABase;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.csession.CToken;
import com.corsair.server.generic.Shwuser;
import com.corsair.server.generic.Shwusertoken;
import com.corsair.server.mail.CMailInfo;
import com.corsair.server.mail.SendMailThread;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.corsair.server.wordflow.Shwwfprocuser;

/**
 * 流程通知
 * 
 * @author Administrator
 *
 */
public class CWFNotify {

	public enum NotityType {
		ntArrive, ntFinish, ntBreak, ntRefuse, ntPress, ntTransfer,
	}

	private static CWDNotifyParm arrNotity = null;// 到达通知 参数
	private static CWDNotifyParm fnshNotity = null;// 完成 通知参数
	private static CWDNotifyParm brkNotity = null;// 中断通知参数
	private static CWDNotifyParm rfsNotity = null;// 驳回通知参数
	private static CWDNotifyParm prsNotity = null;// 催办 通知参数
	private static CWDNotifyParm transNotity = null;// 转办 通知参数

	public static void initNotityInfo() {
		arrNotity = new CWDNotifyParm(NotityType.ntArrive);
		fnshNotity = new CWDNotifyParm(NotityType.ntFinish);
		brkNotity = new CWDNotifyParm(NotityType.ntBreak);
		rfsNotity = new CWDNotifyParm(NotityType.ntRefuse);
		prsNotity = new CWDNotifyParm(NotityType.ntPress);
		transNotity = new CWDNotifyParm(NotityType.ntTransfer);
	}

	public static void procArivedNotify(CJPABase jpa, Shwwf wf, Shwwfproc proc, String actuserid) {
		try {
			sendMessage(NotityType.ntArrive, arrNotity, jpa, wf, proc, null, actuserid);
		} catch (Exception e) {
			Logsw.debug(e);
		}
	}

	public static void procFlishNotify(CJPABase jpa, Shwwfproc proc) {

	}

	/**
	 * 流程驳回通知
	 * 
	 * @param jpa
	 * @param wf
	 * @param fproc
	 * @param tproc
	 * @param actuserid
	 */
	public static void procRefuseNotify(CJPABase jpa, Shwwf wf, Shwwfproc fproc, Shwwfproc tproc, String actuserid) {
		try {
			sendMessage(NotityType.ntRefuse, rfsNotity, jpa, wf, fproc, null, actuserid);
		} catch (Exception e) {
			Logsw.debug(e);
		}
	}

	/**
	 * 流程完成通知
	 * 
	 * @param jpa
	 * @param wf
	 * @param actuserid
	 */
	public static void wfFlishNotify(CJPABase jpa, Shwwf wf, String actuserid) {
		try {
			sendMessage(NotityType.ntFinish, fnshNotity, jpa, wf, null, null, actuserid);
		} catch (Exception e) {
			Logsw.debug(e);
		}
	}

	/**
	 * 流程中断通知
	 * 
	 * @param jpa
	 * @param wf
	 * @param actuserid
	 */
	public static void wfBreakNotify(CJPABase jpa, Shwwf wf, String actuserid) {
		try {
			sendMessage(NotityType.ntBreak, brkNotity, jpa, wf, null, null, actuserid);
		} catch (Exception e) {
			Logsw.debug(e);
		}
	}

	public static void wfTransferNotify(CJPABase jpa, Shwwf wf, Shwwfproc proc, Shwwfprocuser puser, Shwuser fruser, Shwuser touser, String actuserid) {
		try {
			sendMessage(NotityType.ntTransfer, transNotity, jpa, wf, proc, puser, actuserid);
		} catch (Exception e) {
			Logsw.debug(e);
		}
	}

	/**
	 * 流程催办 通知
	 * 
	 * @param jpa
	 * @param wf
	 * @param userid
	 *            收件人
	 */
	public static void wfPressNotify(CJPABase jpa, Shwwf wf, String userid, String wfprocuserid) {
		try {
			if (!needNotity(prsNotity)) {
				Logsw.debug("wfPressNotify 无需发送提醒，取消");
				return;
			}
			List<CNotifyUEInfo> uis = new ArrayList<CNotifyUEInfo>();
			uis.add(new CNotifyUEInfo(userid, wfprocuserid));
			if (prsNotity.isMail()) {
				SendMail(NotityType.ntPress, jpa, wf, null, uis, null, null);
			}
			if (prsNotity.isSms()) {
				SendSms(NotityType.ntPress, jpa, wf, null, uis, null, null);
			}
			if (prsNotity.isWechat()) {
				SendWechat(NotityType.ntPress, jpa, wf, null, uis, null, null);
			}

		} catch (Exception e) {
			Logsw.debug(e);
		}
	}

	/**
	 * @param nt
	 * @param notify
	 * @param jpa
	 * @param wf
	 * @param proc
	 * @param puser
	 *            当 notify 为转办 时候，puser不为null，记录转办的审批用户行
	 * @param actuserid
	 * @throws Exception
	 */
	private static void sendMessage(NotityType nt, CWDNotifyParm notify, CJPABase jpa, Shwwf wf, Shwwfproc proc,
			Shwwfprocuser puser, String actuserid) throws Exception {
		if (!needNotity(notify)) {
			Logsw.debug("sendMessage 无需发送提醒，取消");
			return;
		}

		List<CNotifyUEInfo> uis = getUsIDs(notify, wf, proc);
		if (uis.isEmpty()) {
			Logsw.debug("sendMessage 用户ID列表为空，取消");
			return;
		}

		if (notify.isMail()) {
			SendMail(nt, jpa, wf, proc, uis, puser, actuserid);
		}
		if (notify.isSms()) {
			SendSms(nt, jpa, wf, proc, uis, puser, actuserid);
		}
		if (notify.isWechat()) {
			SendWechat(nt, jpa, wf, proc, uis, puser, actuserid);
		}
	}

	/**
	 * @param nt
	 *            通知类型
	 * @param jpa
	 *            流程对应的业务实体
	 * @param wf
	 *            流程实例
	 * @param proc
	 *            流程节点
	 * @param uids
	 *            通知接收人
	 *            * @param puser
	 *            通知类型为转办时 ，为当前流程用户
	 * @param actuserid
	 *            流程操作人
	 */
	private static void SendMail(NotityType nt, CJPABase jpa, Shwwf wf, Shwwfproc proc, List<CNotifyUEInfo> uis, Shwwfprocuser puser, String actuserid) {
		if (ConstsSw.publicJPAController == null) {
			sendStandMail(nt, jpa, wf, proc, uis, puser, actuserid);
		} else {
			((JPAController) ConstsSw.publicJPAController).OnWFNotityMail(nt, jpa, wf, proc, uis, puser, actuserid);
		}
	}

	private static void SendSms(NotityType nt, CJPABase jpa, Shwwf wf, Shwwfproc proc, List<CNotifyUEInfo> uis, Shwwfprocuser puser, String actuserid) throws Exception {
		if (ConstsSw.publicJPAController == null) {
			sendStandSms(nt, jpa, wf, proc, uis, actuserid);
		} else {
			((JPAController) ConstsSw.publicJPAController).OnWFNotitySms(nt, jpa, wf, proc, uis, actuserid);
		}
	}

	private static void SendWechat(NotityType nt, CJPABase jpa, Shwwf wf, Shwwfproc proc, List<CNotifyUEInfo> uis, Shwwfprocuser puser, String actuserid) throws Exception {
		if (ConstsSw.publicJPAController == null) {
			sendStandWechat(nt, jpa, wf, proc, uis, actuserid);
		} else {
			((JPAController) ConstsSw.publicJPAController).OnWFNotityWechat(nt, jpa, wf, proc, uis, actuserid);
		}
	}

	/**
	 * @param nt
	 * @param jpa
	 * @param wf
	 * @param proc
	 * @param uids
	 * @param actuserid
	 */
	public static void sendStandMail(NotityType nt, CJPABase jpa, Shwwf wf, Shwwfproc proc, List<CNotifyUEInfo> uis, Shwwfprocuser puser, String actuserid) {
		Logsw.debug("send_Stand_WFNotifyMail:【" + wf.wfname.getValue() + "】" + "【" + wf.subject.getValue() + "】");
		int ljavm = 60 * 24;// token有效期 60分钟
		String subject = "";
		if ((nt == NotityType.ntArrive) || (nt == NotityType.ntTransfer)) {
			subject = "您有新的工作流【" + wf.wfname.getValue() + "】" + "【" + wf.subject.getValue() + "】到达，请及时处理";
		}
		if (nt == NotityType.ntFinish) {
			subject = "工作流【" + wf.wfname.getValue() + "】" + "【" + wf.subject.getValue() + "】已经完成审批";
		}
		if (nt == NotityType.ntBreak) {
			subject = "工作流【" + wf.wfname.getValue() + "】" + "【" + wf.subject.getValue() + "】已经中断";
		}
		if (nt == NotityType.ntRefuse) {
			subject = "工作流【" + wf.wfname.getValue() + "】" + "【" + wf.subject.getValue() + "】被驳回";
		}
		if (nt == NotityType.ntPress) {
			subject = "工作流【" + wf.wfname.getValue() + "】到期催办，请及时处理";
		}
		try {
			Shwuser actuser = new Shwuser();
			actuser.findByID(actuserid);

			Shwuser user = new Shwuser();
			for (CNotifyUEInfo ui : uis) {
				user.clear();
				user.findByID(ui.getUserid());
				if (!user.email.isEmpty()) {
					CMailInfo mi = new CMailInfo();
					mi.setSubject(subject);
					mi.getToMails().add(user.email.getValue());
					String content = buildMailContent(nt, wf, user, subject, actuser.displayname.getValue(), ljavm);
					mi.setContent(content);
					SendMailThread sm = new SendMailThread(mi);
					sm.start();
				} else
					Logsw.debug("发送流程提醒邮件【" + user.displayname.getValue() + "】没设置邮件地址");

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param nt
	 * @param wfid
	 * @param user
	 * @param subject
	 * @param actusername
	 *            执行人姓名
	 * @param ljavm
	 *            Token有效期
	 * @return
	 * @throws Exception
	 */
	public static String buildMailContent(NotityType nt, Shwwf wf, Shwuser user, String subject, String actusername, int ljavm)
			throws Exception {
		String sysurl = ConstsSw.getSysParm("APPURL");
		String url = sysurl.substring(0, sysurl.lastIndexOf("/") + 1);
		String sign = ConstsSw.getSysParm("WFNotifySign");

		String content = "尊敬的" + user.displayname.getValue() + ":<br>";
		if (nt == NotityType.ntArrive) {
			content = content + subject;
		}
		if (nt == NotityType.ntFinish) {
			content = content + subject;
		}
		if (nt == NotityType.ntBreak) {
			content = content + subject + ",执行人【" + actusername + "】";
		}
		if (nt == NotityType.ntRefuse) {
			content = content + subject + ",执行人【" + actusername + "】";
		}
		if (nt == NotityType.ntPress) {
			content = content + subject;
		}
		String sessionid = (CSContext.getSession() == null) ? "" : CSContext.getSession().getId();
		Shwusertoken atoken = CToken.createToken(user, null, sessionid, 60);
		String purl = url + "common/shwwf.html?wfid=" + wf.wfid.getValue() + "&showtype=" + ((wf.mwwfstype.isEmpty()) ? 1 : wf.mwwfstype.getValue())
				+ "&token=" + atoken.token.getValue();
		content = content + "<br>请点击以下地址登录系统<br>";
		content = content + "<a href='" + purl + "'>" + purl + "</a>";
		content = content + "<br><br>";
		content = content + ((sign == null) ? "" : sign) + "<br>";
		content = content + Systemdate.getStrDate(new Date()) + "<br>";
		content = content + "注意:登录有效期(北京时间, "
				+ Systemdate.getStrDate(Systemdate.dateMinuteAdd(new Date(), ljavm))
				+ ")。<br>";
		content = content + "(本邮件由系统自动发送，请勿直接回复)<br>";
		InetAddress ia = InetAddress.getLocalHost();
		content = content + "<div style='display: none'>" + ia.getHostAddress() + "</div>";
		return content;
	}

	// 标准短信
	public static void sendStandSms(NotityType nt, CJPABase jpa, Shwwf wf, Shwwfproc proc, List<CNotifyUEInfo> uids, String actuserid) {
	}

	// 标准微信
	public static void sendStandWechat(NotityType nt, CJPABase jpa, Shwwf wf, Shwwfproc proc, List<CNotifyUEInfo> uids, String actuserid) {
	}

	private static boolean needNotity(CWDNotifyParm notify) {
		if (!notify.isNotity())
			return false;
		if (notify.isMail() || notify.isSms() && notify.isWechat())
			return true;
		return false;
	}

	private static List<CNotifyUEInfo> getUsIDs(CWDNotifyParm notify, Shwwf wf, Shwwfproc proc) throws Exception {
		List<CNotifyUEInfo> uis = new ArrayList<CNotifyUEInfo>();
		if (notify.isAlluser()) {
			getAllUser(uis, wf);
		} else {
			if (notify.isSubeduser()) {
				getSubmitedUser(uis, wf);
			}
			if (notify.isCurprocuser()) {
				getCurProcUser(uis, proc);
			}
		}
		if (notify.isCreator()) {
			getCreatorUser(uis, wf);
		}
		return uis;
	}

	/**
	 * 检查 指定 userid wfprocuserid的数据是否存在于列表
	 * 
	 * @param uis
	 * @param userid
	 * @param wfprocuserid
	 * @return
	 */
	private static boolean isuserInList(List<CNotifyUEInfo> uis, String userid, String wfprocuserid) {
		for (CNotifyUEInfo ui : uis) {
			if (ui.getUserid().equalsIgnoreCase(userid)) {
				String owpu = ui.getWfprocuserid();
				if (owpu == null) {
					if (wfprocuserid == null)
						return true;
				} else {
					if (owpu.equalsIgnoreCase(wfprocuserid))
						return true;
				}
			}
		}
		return false;
	}

	private static void getAllUser(List<CNotifyUEInfo> uis, Shwwf wf) throws Exception {
		if (wf == null)
			return;
		for (CJPABase j1 : wf.shwwfprocs) {
			Shwwfproc proc = (Shwwfproc) j1;
			for (CJPABase j2 : proc.shwwfprocusers) {
				Shwwfprocuser user = (Shwwfprocuser) j2;
				if ((!user.userid.isEmpty()) && (user.recnotify.getAsIntDefault(0) == 1)) {
					if (!isuserInList(uis, user.userid.getValue(), user.wfprocuserid.getValue())) {
						CNotifyUEInfo cnuei = new CNotifyUEInfo(user.userid.getValue(), user.wfprocuserid.getValue());
						uis.add(cnuei);
					}
				}
			}
		}
	}

	private static void getCreatorUser(List<CNotifyUEInfo> uis, Shwwf wf) throws Exception {
		if (wf == null)
			return;
		if (!wf.submituserid.isEmpty()) {
			if (!isuserInList(uis, wf.submituserid.getValue(), null)) {
				CNotifyUEInfo cnuei = new CNotifyUEInfo(wf.submituserid.getValue(), null);
				uis.add(cnuei);
			}
		}
	}

	private static void getSubmitedUser(List<CNotifyUEInfo> uis, Shwwf wf) throws Exception {
		if (wf == null)
			return;
		for (CJPABase j1 : wf.shwwfprocs) {
			Shwwfproc proc = (Shwwfproc) j1;
			for (CJPABase j2 : proc.shwwfprocusers) {
				Shwwfprocuser user = (Shwwfprocuser) j2;
				if (user.stat.getAsIntDefault(0) == 2) {
					if ((!user.userid.isEmpty()) && (user.recnotify.getAsIntDefault(0) == 1)) {
						if (!isuserInList(uis, user.userid.getValue(), user.wfprocuserid.getValue())) {
							CNotifyUEInfo cnuei = new CNotifyUEInfo(user.userid.getValue(), user.wfprocuserid.getValue());
							uis.add(cnuei);
						}
					}
				}
			}
		}
	}

	// 获取当前节点所有人
	private static void getCurProcUser(List<CNotifyUEInfo> uis, Shwwfproc proc) throws Exception {
		if (proc == null)
			return;
		for (CJPABase j2 : proc.shwwfprocusers) {
			Shwwfprocuser user = (Shwwfprocuser) j2;
			if ((!user.userid.isEmpty()) && (user.recnotify.getAsIntDefault(0) == 1)) {
				if (!isuserInList(uis, user.userid.getValue(), user.wfprocuserid.getValue())) {
					CNotifyUEInfo cnuei = new CNotifyUEInfo(user.userid.getValue(), user.wfprocuserid.getValue());
					uis.add(cnuei);
				}
			}
		}
	}

	// 获取当前节点 某个状态的人 ProcUserStat
	private static void getCurProcUser(List<String> uids, Shwwfproc proc, int stat) {
		if (proc == null)
			return;
		for (CJPABase j2 : proc.shwwfprocusers) {
			Shwwfprocuser user = (Shwwfprocuser) j2;
			if (user.stat.getAsIntDefault(0) == stat) {
				if ((!user.userid.isEmpty()) && (user.recnotify.getAsIntDefault(0) == 1)) {
					if (uids.indexOf(user.userid.getValue()) < 0)
						uids.add(user.userid.getValue());
				}
			}
		}
	}

}
