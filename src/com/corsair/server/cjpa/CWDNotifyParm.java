package com.corsair.server.cjpa;

import com.corsair.server.base.ConstsSw;
import com.corsair.server.cjpa.CWFNotify.NotityType;

/**
 * 流程通知参数
 * 
 * @author Administrator
 *
 */
public class CWDNotifyParm {
	// 到达通知 0 不通知 1 邮件通知 2 短信通知 3 微信推送 A 提交人 B审批过的人 C所有人 D当前节点参与人 如:12AC代表邮件短信通知提交人、所有参与人
	private boolean notity = false;// 是否通知
	private boolean mail = false;
	private boolean sms = false;
	private boolean wechat = false;
	private boolean creator = false;
	private boolean subeduser = false;
	private boolean alluser = false;
	private boolean curprocuser = false;

	public CWDNotifyParm(NotityType nt) {
		String af = null;
		if (nt == NotityType.ntArrive)
			af = ConstsSw.getSysParm("WFArriveNotify");
		if (nt == NotityType.ntFinish)
			af = ConstsSw.getSysParm("WFFinishNotify");
		if (nt == NotityType.ntBreak)
			af = ConstsSw.getSysParm("WFBreakNotify");
		if (nt == NotityType.ntRefuse)
			af = ConstsSw.getSysParm("WFRefuseNotify");
		if (nt == NotityType.ntPress) {
			af = ConstsSw.getSysParm("WFPressNotify");
		}
		if (nt == NotityType.ntTransfer) {
			af = ConstsSw.getSysParm("WFTransferNotify");
		}
		if ((af == null) || (af.isEmpty()))
			return;
		char[] cs = af.toCharArray();

		for (char c : cs) {
			if (c == '0') {
				setallfalse();
				return;
			}
			if (c == '1')
				mail = true;
			if (c == '2')
				sms = true;
			if (c == '3')
				wechat = true;
			if (c == 'A')
				creator = true;
			if (c == 'B')
				subeduser = true;
			if (c == 'C')
				alluser = true;
			if (c == 'D')
				curprocuser = true;
		}
		notity = ((mail || sms || wechat) && (creator || alluser || curprocuser));
		if (nt == NotityType.ntPress) {
			notity = (mail || sms || wechat);
		}
	}

	private void setallfalse() {
		notity = false;// 是否通知
		mail = false;
		sms = false;
		wechat = false;
		creator = false;
		subeduser = false;
		alluser = false;
		curprocuser = false;
	}

	public boolean isNotity() {
		return notity;
	}

	public boolean isMail() {
		return mail;
	}

	public boolean isCreator() {
		return creator;
	}

	public boolean isSubeduser() {
		return subeduser;
	}

	public boolean isAlluser() {
		return alluser;
	}

	public boolean isCurprocuser() {
		return curprocuser;
	}

	public boolean isSms() {
		return sms;
	}

	public boolean isWechat() {
		return wechat;
	}
}
