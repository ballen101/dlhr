package com.hr.util.wx;
import net.sf.json.JSONObject;

public class Mian {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//String departList=WeixinDepartUtil.GetUser();
		//System.out.print(departList);
		
		String retult=	SendMsgUtil.sendText("377614", "您好！");
		System.out.print(retult);

		









	}

}
