package com.corsair.server.mail;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeUtility;

import com.corsair.dbpool.util.Logsw;
import com.corsair.server.base.ConstsSw;

public class CMail {

	public static void sendMail(CMailInfo mi) throws Exception {
		if (mi.getToMails().size() <= 0) {
			throw new Exception("没有设置收件人");
		}

		String SMTPHost = ConstsSw.geAppParmStr("SMTPHost");
		if ((SMTPHost == null) || (SMTPHost.isEmpty())) {
			throw new Exception("发邮件需要设置SMTPHost参数");
		}
		String LoginUser = ConstsSw.geAppParmStr("LoginUser");
		if ((LoginUser == null) || (LoginUser.isEmpty())) {
			throw new Exception("发邮件需要设置LoginUser参数");
		}
		String LoginPSW = ConstsSw.geAppParmStr("LoginPSW");
		if ((LoginPSW == null) || (LoginPSW.isEmpty())) {
			throw new Exception("发邮件需要设置LoginPSW参数");
		}

		String nick = ConstsSw.geAppParmStr("UserNick");
		// if ((nick == null) || (nick.isEmpty())) {
		// throw new Exception("发邮件需要设置UserNick参数");
		// }

		final Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", SMTPHost);
		props.put("mail.user", LoginUser);
		props.put("mail.password", LoginPSW);

		Authenticator authenticator = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// 用户名、密码
				String userName = props.getProperty("mail.user");
				String password = props.getProperty("mail.password");
				return new PasswordAuthentication(userName, password);
			}
		};
		String fromstr = formatAddress(nick, props.getProperty("mail.user"));
		Session mailSession = Session.getInstance(props, authenticator);
		mailSession.setDebug(false);
		MimeMessage message = new MimeMessage(mailSession);

		InternetAddress form = new InternetAddress(fromstr);
		message.setFrom(form);

		InternetAddress[] addresses = createMailAddrs(mi.getToMails());
		if (addresses.length > 0)
			message.setRecipients(RecipientType.TO, addresses);
		addresses = createMailAddrs(mi.getCcMails());
		if (addresses.length > 0)
			message.setRecipients(RecipientType.CC, addresses);
		addresses = createMailAddrs(mi.getBccMails());
		if (addresses.length > 0)
			message.setRecipients(RecipientType.BCC, addresses);
		message.setSubject(mi.getSubject());
		message.setContent(mi.getContent(), mi.getType());
		try {
			Transport.send(message);
		} catch (Exception e) {
			Logsw.error("邮件发送失败,SMTP【" + SMTPHost + "】user【" + LoginUser + "】pwd【" + LoginPSW + "】请核查", e);
			throw e;
		}
	}

	public static String formatAddress(String nick, String email) throws UnsupportedEncodingException {
		if ((nick != null) && (!nick.isEmpty())) {
			// String StringCode = ConstsSw.geAppParmStr("StringCode");
			// String ecdnick = null;
			// if (StringCode == null) {
			// ecdnick = nick;
			// } else {
			// ecdnick = MimeUtility.encodeText(nick, StringCode, null);
			// }
			String ecdnick = MimeUtility.encodeText(nick, null, null);
			String rst = String.format("%1$s <%2$s>", ecdnick, email);
			// System.out.println(rst);
			return rst;
		} else
			return email;
	}

	private static InternetAddress[] createMailAddrs(List<String> ms) throws Exception {
		InternetAddress[] addresses = new InternetAddress[ms.size()];
		for (int i = 0; i < addresses.length; i++) {
			addresses[i] = new InternetAddress(ms.get(i).toString());
		}
		return addresses;
	}

	public static void main(String[] args) throws Exception {
		CMailInfo cmi = new CMailInfo();
		cmi.addToMail("84287057@qq.com");
		cmi.setSubject("ceshiyoujian");
		cmi.setContent("范德萨范德萨");
		sendMail(cmi);
	}
}
