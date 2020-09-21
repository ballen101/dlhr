package com.hr.util;

import com.corsair.dbpool.util.Logsw;
import com.corsair.server.util.HttpTookit;


import net.sf.json.JSONObject;

public class TestMain {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		JSONObject obj=getWageSys("20200626A001");
	}
	
	public static JSONObject getWageSys(String salary_quotacode) throws Exception {
		//String HRWAGE_SERVER_URL = HrkqUtil.getParmValueErr("HRWAGE_SERVER_URL");// 薪资额度接口地址
		HttpTookit ht = new HttpTookit();
		JSONObject jo = new JSONObject();
		String url = "http://192.168.117.121:8888/Service/LimitSalaryDataSyn.asmx";
		//buildSarrayCodeReq(jo, salary_quotacode);// 构造请求数据包
		jo.put("Code", salary_quotacode);
		String rst = ht.doPostJSON(url, jo, "UTF-8");
		JSONObject jr = JSONObject.fromObject(rst);
		Logsw.dblog(jr.toString());
		// 解析返回数据包
		if (jr.containsKey("Code") ) {
//			String r = jr.getJSONObject("SvcHdr").getString("RCODE");
//			if (!"S".equalsIgnoreCase(r)) {
//				throw new Exception(jr.getJSONObject("SvcHdr").getString("RDESC"));
//			}
			//JSONObject jwi = jr.getJSONObject("AppBody").getJSONObject("WageQuotaInfo_ITEM");
			JSONObject jrst = new JSONObject();
			jrst.put("wage", jr.getString("Salary"));
			jrst.put("usedwage", jr.getString("Used"));
			jrst.put("balance", jr.getString("Available"));
			jrst.put("money", jr.getString("Surplus"));
			return jrst;
		} else
			throw new Exception("返回数据格式错误:" + rst);
	}

	private static void buildSarrayCodeReq(JSONObject jo, String salary_quotacode) {
		JSONObject SvcHdr = new JSONObject();
		SvcHdr.put("SOURCEID", "HRMS");
		SvcHdr.put("DESTINATIONID", "EIP");
		SvcHdr.put("TYPE", "SELECT");
		SvcHdr.put("IPADDRESS", "127.0.0.1");
		SvcHdr.put("BO", "工资额度信息查询");
		jo.put("SvcHdr", SvcHdr);
		JSONObject AppBody = new JSONObject();
		AppBody.put("DocNo", salary_quotacode);
		jo.put("AppBody", AppBody);
	}

}
