package com.hr.util;

public class HRQuotaUsedInfo {
	private int sumq = 0;//总编制
	private int usedq = 0;//已使用编制

	public HRQuotaUsedInfo(int sumq, int usedq) {
		this.sumq = sumq;
		this.usedq = usedq;
	}

	public int getUsedq() {
		return usedq;
	}

	public void setUsedq(int usedq) {
		this.usedq = usedq;
	}

	public int getSumq() {
		return sumq;
	}

	public void setSumq(int sumq) {
		this.sumq = sumq;
	}

}
