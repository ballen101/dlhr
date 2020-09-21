package com.corsair.server.ctrl;

import com.corsair.cjpa.CJPABase;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.weixin.WXAppParms;
import com.corsair.server.weixin.entity.Shwwxapp;

/**
 * 微信APP
 * 
 * @author Administrator
 *
 */
public class CtrlShwwxapp extends JPAController {
	@Override
	public void AfterSave(CJPABase jpa, CDBConnection con, boolean selfLink) {
		try {
			WXAppParms.reloadAllParms(con);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 删除前 貌似列表JPA被干掉时候无法监控到
	 * 
	 * @param jpa还未保存到数据库
	 *            ，JPA状态未变
	 * @param con
	 * @param selfLink
	 * @throws Exception
	 */
	@Override
	public void OnDelete(CJPABase jpa, CDBConnection con, boolean selfLink) throws Exception {
	}
}
