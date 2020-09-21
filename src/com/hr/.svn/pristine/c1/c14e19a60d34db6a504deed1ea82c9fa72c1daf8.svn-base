package com.hr.util;

import com.hr.attd.ctr.CacalKQData;

public class CalcThread extends Thread {
	private String clcid;

	public CalcThread(String clcid) {
		this.clcid = clcid;
	}

	public void run() {
		try {
			(new CacalKQData()).calcKQReq(clcid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
