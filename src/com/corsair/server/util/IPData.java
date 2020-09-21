package com.corsair.server.util;

public class IPData {
	private String ctry;
	private String prvc;
	private String city;

	public IPData() {
	}

	public IPData(String ctry, String prvc, String city) {
		this.ctry = ctry;
		this.prvc = prvc;
		this.city = city;
	}

	public String getCtry() {
		return ctry;
	}

	public String getPrvc() {
		return prvc;
	}

	public String getCity() {
		return city;
	}

	public void setCtry(String ctry) {
		this.ctry = ctry;
	}

	public void setPrvc(String prvc) {
		this.prvc = prvc;
	}

	public void setCity(String city) {
		this.city = city;
	}

}
