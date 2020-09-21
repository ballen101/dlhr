package com.corsair.server.ctrl;

import java.util.ArrayList;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPABase.JPAAction;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.PraperedSql;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.weixin.WXAppParms;
import com.corsair.server.weixin.entity.Shwwxapp;
import com.corsair.server.weixin.entity.Shwwxmsgcfg;

/**
 * 微信消息控制器
 * 
 * @author Administrator
 *
 */
public class CtrShwwxmsgcfg extends JPAController {
	@Override
	public void OnJAPAction(CJPABase jpa, CDBConnection con, JPAAction act) throws Exception {

	}

	@Override
	public void OnSave(CJPABase jpa, CDBConnection con, ArrayList<PraperedSql> sqllist, boolean selfLink) throws Exception {
		Shwwxmsgcfg wc = (Shwwxmsgcfg) jpa;
		if ((!wc.mcid.isEmpty()) && (wc.usable.getAsIntDefault(0) == 1)) {
			String sqlstr = "SELECT IFNULL(COUNT(*),0) ct FROM shwwxmsgcfg WHERE mcid <> " + wc.mcid.getValue() + " AND appid = " + wc.appid.getValue() + " AND usable=1";
			if (Integer.valueOf(con.openSql2List(sqlstr).get(0).get("ct").toString()) > 0) {
				throw new Exception("每个微信公众号只允许配置一个可用自动回复消息参数");
			}
		}
	}

	/**
	 * 完成保存
	 * 
	 * @param jpa
	 *            已经实例化到数据库，JPA变为 RSLOAD4DB 状态
	 * @param con
	 * @param selfLink
	 */
	@Override
	public void AfterSave(CJPABase jpa, CDBConnection con, boolean selfLink) {
		try {
			Shwwxmsgcfg wc = (Shwwxmsgcfg) jpa;
			WXAppParms.InitParms(con);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
