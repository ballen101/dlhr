package com.corsair.server.mail;

import java.util.ArrayList;
import java.util.List;

public class CMailInfo {

	private List<String> toMails = new ArrayList<String>();
	private List<String> ccMails = new ArrayList<String>();
	private List<String> bccMails = new ArrayList<String>();
	private String subject;
	private String content;
	private String type = "text/html;charset=utf-8";

	public List<String> getToMails() {
		return toMails;
	}

	public List<String> getCcMails() {
		return ccMails;
	}

	public List<String> getBccMails() {
		return bccMails;
	}

	public String getType() {
		return type;
	}

	public void setToMails(List<String> toMails) {
		this.toMails = toMails;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void addToMail(String value) {
		toMails.add(value);
	}

	public void addCCMail(String value) {
		ccMails.add(value);
	}

	public void addBCCMail(String value) {
		bccMails.add(value);
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
