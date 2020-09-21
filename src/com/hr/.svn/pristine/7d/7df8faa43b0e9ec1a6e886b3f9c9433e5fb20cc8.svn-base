package com.hr.util.hrmail;

import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.util.GetNewSystemCode;
import com.hr.attd.ctr.HrkqUtil;

public class DLHRMailCenterWS {
	// private static String wsurl = "http://116.6.55.46:81/EIPMail.svc";

	// 新建通知？
	public static String sendmailex(Hr_emailsend_log ml) throws Exception {
		EipMail em = new EipMail();
		em.setMailType(ml.mailtype.getValue());
		em.setAddress(ml.address.getValue());
		em.setAccount(ml.employee_code.getValue());
		em.setMailSubject(ml.mailsubject.getValue());
		em.setMailBody(ml.mailbody.getValue());
		em.setSystemSort("A5");
		em.setSystemName("HRMS");
		em.setMailID(ml.aynemid.getValue());
		em.setUrl(ml.wfurl.getValue());
		Date dt = ml.createtime.getAsDatetime();
		em.setCreateDate(Systemdate.getStrDateByFmt(dt, "yyyy-MM-dd") + "T" + Systemdate.getStrDateByFmt(dt, "HH:mm:ss.SSS") + "Z");
		em.setCreateMan(ml.creator.getValue());
		String synid = sendmail(em);
		return synid;
	}

	/**
	 * @param em
	 * @param tp 1 add 2 del 3 update
	 * @return
	 */
	private static String buildsoapxml(EipMail em, int tp) {
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("soapenv:Envelope");// 创建根节点
		root.addNamespace("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
		root.addNamespace("tem", "http://tempuri.org/");
		root.addNamespace("eip", "http://schemas.datacontract.org/2004/07/EipMail.Model");
		Element header = root.addElement("soapenv:Header");
		Element body = root.addElement("soapenv:Body");
		Element acemt = null;
		if (tp == 1)
			acemt = body.addElement("tem:DoAdd");
		if (tp == 3)
			acemt = body.addElement("tem:DoUpdate");

		if (tp == 2) {
			acemt = body.addElement("tem:DoDel");
			acemt.addElement("tem:MailID").setText(em.getMailID());
		} else {
			Element elemt = acemt.addElement("tem:EipMail");
			SOAPUtil.putField2XML(em, elemt);
		}
		String xmlstr = document.asXML();
		return xmlstr;
	}

	public static String sendmail(EipMail em) throws Exception {
		String vcharset = "UTF-8";
		String sendMsg = buildsoapxml(em, 1);
		Logsw.debug("SendMail SOAP:" + sendMsg);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		try {
			try {
				HttpPost httppost = new HttpPost(HrkqUtil.getParmValueErr("HRMSWSMAILURL"));
				HttpEntity re = new StringEntity(sendMsg, vcharset);
				httppost.setHeader("Content-Type", "text/xml; charset=UTF-8");
				httppost.setHeader("SOAPAction", "http://tempuri.org/IDO/DoAdd");
				httppost.setEntity(re);
				HttpResponse response = httpClient.execute(httppost);
				response.setHeader("Content-Type", "text/xml; charset=UTF-8");
				int statusCode = response.getStatusLine().getStatusCode();
				statusCode = (int) (Math.floor(statusCode / 100) * 100);// 2**都算成功
				if (statusCode != 200) {
					String rst = EntityUtils.toString(response.getEntity());
					httppost.abort();
					throw new RuntimeException("HttpClient,error status code :" + statusCode + "\n;rst:" + rst);
				} else {
					String rst = EntityUtils.toString(response.getEntity());
					Logsw.debug("创建代办失败:" + rst);
					Document document = DocumentHelper.parseText(rst);
					Element root = document.getRootElement();
					return root.getStringValue();
				}
			} finally {
				httpClient.getConnectionManager().shutdown();
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public static String getNewCode(int codeid) {
		GetNewSystemCode sc = new GetNewSystemCode();
		try {
			return sc.dogetnewsyscode(null, String.valueOf(codeid));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static void updatemail(String aynemid, String approvalman, String approvaldate) throws Exception {
		String vcharset = "UTF-8";
		EipMail em = new EipMail();
		em.setMailID(aynemid);
		em.setSystemSort("A5");
		em.setSystemName("HRMS");
		em.setApprovalMan(approvalman);
		em.setApprovalDate(approvaldate);
		String sendMsg = buildsoapxml(em, 3);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		Logsw.debug("UpdateMail SOAP:" + sendMsg);
		try {
			HttpPost httppost = new HttpPost(HrkqUtil.getParmValueErr("HRMSWSMAILURL"));
			HttpEntity re = new StringEntity(sendMsg, vcharset);
			httppost.setHeader("Content-Type", "text/xml; charset=UTF-8");
			httppost.setHeader("SOAPAction", "http://tempuri.org/IUpdate/DoUpdate");
			httppost.setEntity(re);
			HttpResponse response = httpClient.execute(httppost);
			response.setHeader("Content-Type", "text/xml; charset=UTF-8");
			int statusCode = response.getStatusLine().getStatusCode();
			statusCode = (int) (Math.floor(statusCode / 100) * 100);// 2**都算成功
			if (statusCode != 200) {
				String rst = EntityUtils.toString(response.getEntity());
				httppost.abort();
				System.out.println("UpdateMail 失败statusCode:" + statusCode + " aynemid:" + aynemid + ",approvalman:" + approvalman + ",approvaldate:" + approvaldate);
				throw new RuntimeException("HttpClient,error status code :" + statusCode + "\n;rst:" + rst);
			} else {
				String rst = EntityUtils.toString(response.getEntity());
				System.out.println("rst:" + rst);
				// <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/"><s:Body><DoUpdateResponse xmlns="http://tempuri.org/"><DoUpdateResult>false</DoUpdateResult></DoUpdateResponse></s:Body></s:Envelope>
				Document document = DocumentHelper.parseText(rst);
				Element root = document.getRootElement();
				Element erst = root.element("Body").element("DoUpdateResponse").element("DoUpdateResult");
				if (!Boolean.parseBoolean(erst.getTextTrim())) {
					throw new Exception("UpdateMail 失败:" + erst.getTextTrim() + " aynemid:" + aynemid + ",approvalman:" + approvalman + ",approvaldate:" + approvaldate);
				}
			}
			httpClient.getConnectionManager().shutdown();
		} catch (Exception e) {
			try {
				httpClient.getConnectionManager().shutdown();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw e;
		}
	}

	public static void delMail(String aynemid) throws Exception {
		String vcharset = "UTF-8";
		EipMail em = new EipMail();
		em.setMailID(aynemid);
		String sendMsg = buildsoapxml(em, 2);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		Logsw.debug("DelMail SOAP:" + sendMsg);
		try {
			HttpPost httppost = new HttpPost(HrkqUtil.getParmValueErr("HRMSWSMAILURL"));
			// HttpPost httppost = new HttpPost(wsurl);
			HttpEntity re = new StringEntity(sendMsg, vcharset);
			httppost.setHeader("Content-Type", "text/xml; charset=UTF-8");
			httppost.setHeader("SOAPAction", "http://tempuri.org/IDel/DoDel");
			httppost.setEntity(re);
			HttpResponse response = httpClient.execute(httppost);
			response.setHeader("Content-Type", "text/xml; charset=UTF-8");
			int statusCode = response.getStatusLine().getStatusCode();
			statusCode = (int) (Math.floor(statusCode / 100) * 100);// 2**都算成功
			if (statusCode != 200) {
				String rst = EntityUtils.toString(response.getEntity());
				httppost.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode + "\n;rst:" + rst);
			} else {
				String rst = EntityUtils.toString(response.getEntity());
				Document document = DocumentHelper.parseText(rst);
				Element root = document.getRootElement();
				System.out.println(document.asXML());
			}
			httpClient.getConnectionManager().shutdown();
		} catch (Exception e) {
			throw e;
		}
	}

	public static void main(String[] args) throws Exception {
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read("d://a.xml");
		Element root = document.getRootElement();
		Element erst = root.element("Body").element("DoDelResponse").element("DoDelResult");
		System.out.println(Boolean.parseBoolean(erst.getTextTrim()));

		// EipMail em = new EipMail();
		// em.setMailType("1");
		// em.setAddress("xianjianfa@donlim.com");
		// em.setAccount("373919");
		// em.setMailSubject("测试主题");
		// em.setMailBody("测试邮件内容");
		// em.setSystemSort("A5");
		// em.setSystemName("HRMS");
		// em.setID("1");
		// em.setUrl("http://www.baidu.com");
		// em.setCreateDate(Systemdate.getStrDate());
		// em.setCreateMan("HRMSDEV1");
		// sendmail(em);

		// updatemail("HREM1711170017", "审批人", "2017-01-01T12:12:12.000Z");
		// delMail("HREM1711170008");
	}
}
