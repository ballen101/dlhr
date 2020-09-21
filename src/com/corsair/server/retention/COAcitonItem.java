package com.corsair.server.retention;

public class COAcitonItem {
	private String fname;
	private String classname;
	private String methodname;
	private ACOAction cce;
	private String key;

	public COAcitonItem(String key, String classname, String methodname, ACOAction cce) {
		this.classname = classname;
		this.methodname = methodname;
		this.cce = cce;
		this.key = key;
	}

	public String getClassname() {
		return classname;
	}

	public String getMethodname() {
		return methodname;
	}

	public ACOAction getCce() {
		return cce;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public void setMethodname(String methodname) {
		this.methodname = methodname;
	}

	public void setCce(ACOAction cce) {
		this.cce = cce;
	}

	public String getKey() {
		return key;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}
}
