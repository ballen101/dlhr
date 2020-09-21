package com.corsair.server.ctrl;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.generic.Shworg;

import net.sf.json.JSONObject;

/**
 * 机构信息变动监听器
 * 
 * @author Administrator
 *
 */
public abstract class OnChangeOrgInfoListener {
	public abstract void OnOrgChg(CDBConnection con, JSONObject jorg, JSONObject jporg) throws Exception;

	public abstract void OnOrgData2Org(CDBConnection con, Shworg sorg, JSONObject dorg_s) throws Exception;
}
