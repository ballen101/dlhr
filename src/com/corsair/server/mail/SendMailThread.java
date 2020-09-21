package com.corsair.server.mail;

public class SendMailThread extends Thread {
	private CMailInfo mi;

	public SendMailThread(CMailInfo mi) {
		this.mi = mi;
	}

	public void run() {
		try {
			CMail.sendMail(mi);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
