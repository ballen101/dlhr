package com.corsair.server.util;

public class PackageClass {
	private String fname;// 文件名
	private String claname;// 类名

	public PackageClass(String fname, String claname) {
		this.fname = fname;
		this.claname = claname;
	}

	public String getClaname() {
		return claname;
	}

	public void setClaname(String claname) {
		this.claname = claname;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}
}
