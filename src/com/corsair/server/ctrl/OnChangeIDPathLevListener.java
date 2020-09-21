package com.corsair.server.ctrl;

import net.sf.json.JSONObject;

import com.corsair.dbpool.CDBConnection;

/**
 * 没卵用
 * 
 * @author Administrator
 *
 */
public abstract class OnChangeIDPathLevListener {
	public abstract void OnChange(CDBConnection con, JSONObject jorg, JSONObject jporg) throws Exception;
}
