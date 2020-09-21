package com.hr.asset.co;

import java.util.HashMap;

import javax.naming.Context;

import com.corsair.server.base.CSContext;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CReport;

@ACO(coname = "web.hr.Asset.rpt")
public class CORpt {

	@ACOAction(eventname = "getassedtconfig", Authentication = true, notes = "资产配置流水表")
	public String getassedtconfig() throws Exception {
		//HashMap<String,String> parms= CSContext.get_pjdataparms();
		//parms.get("a").toString();
		String sqlstr = "SELECT "+
				"  ar.asset_register_num,"+
				"  ar.asset_item_id,"+
				"  ar.asset_item_code,"+
				"  ar.asset_item_name,"+
				"  ar.asset_type_id,"+
				"  ar.asset_type_code,"+
				"  ar.asset_type_name,"+
				"  ar.brand,"+
				"  ar.model,"+
				"  ar.original_value,"+
				"  ar.net_value,"+
				"  ar.uom,"+
				"  ar.service_life,"+
				"  ar.acquired_date,"+
				"  ar.deploy_qty,"+
				"  ar.deploy_area,"+
				"  ar.deploy_restaurant,"+
				"  ar.deploy_restaurant_id,"+
				"  ar.keep_own,"+
				"  ar.remark,"+
				"  aa.asset_allot_num,"+
				"  aa.asset_item_name allot_item_namefrom,"+
				"  aa.deploy_restaurant_from,"+
				"  aa.deploy_area_from,"+
				"  aa.deploy_qty deploy_qty_from,"+
				"  aa.acquired_date acquired_date_from,"+
				"  aa.remarkah,"+
				"  aa.asset_item_name allot_item_nameto,"+
				"  aa.deploy_restaurant_to,"+
				"  aa.deploy_area_to,"+
				"  aa.deploy_qty deploy_qty_to,"+
				"  aa.acquired_date acquired_date_to,"+
				"  aa.remarkad,"+
				"  har.asset_reject_num,"+
				"  har.asset_item_name asset_item_namerj,"+
				"  har.reject_qty,"+
				"  har.reject_date,"+
				"  har.remarkrd "+
				"FROM"+
				"  hr_asset_register ar "+
				"  LEFT JOIN "+
				"    (SELECT "+
				"      ah.asset_allot_id,"+
				"      ah.asset_allot_num,"+
				"      ah.allot_type,"+
				"      ah.deploy_area_from,"+
				"      ah.deploy_restaurant_from,"+
				"      ah.deploy_restaurantid_from,"+
				"      ah.deploy_area_to,"+
				"      ah.deploy_restaurant_to,"+
				"      ah.deploy_restaurantid_to,"+
				"      ah.remark remarkah,"+
				"      ad.asset_allot_line_id,"+
				"      ad.asset_item_id,"+
				"      ad.asset_item_code,"+
				"      ad.asset_item_name,"+
				"      ad.asset_type_id,"+
				"      ad.asset_type_code,"+
				"      ad.asset_type_name,"+
				"      ad.acquired_date,"+
				"      ad.deploy_qty,"+
				"      ad.remark remarkad "+
				"    FROM"+
				"      hr_asset_allot_h ah,"+
				"      hr_asset_allot_d ad "+
				"    WHERE ah.asset_allot_id = ad.asset_allot_id) aa "+
				"    ON aa.deploy_restaurantid_from = ar.deploy_restaurant_id "+
				"    AND aa.asset_item_id = ar.asset_item_id "+
				"  LEFT JOIN "+
				"    (SELECT "+
				"      rh.asset_reject_id,"+
				"      rh.asset_reject_num,"+
				"      rh.deploy_area,"+
				"      rh.deploy_restaurant,"+
				"      rh.deploy_restaurant_id,"+
				"      rh.remark remarkrd,"+
				"      rd.asset_reject_line_id,"+
				"      rd.asset_item_id,"+
				"      rd.asset_item_code,"+
				"      rd.asset_item_name,"+
				"      rd.asset_type_id,"+
				"      rd.asset_type_code,"+
				"      rd.asset_type_name,"+
				"      rd.uom,"+
				"      rd.reject_qty,"+
				"      rd.reject_date "+
				"    FROM"+
				"      hr_asset_reject_h rh,"+
				"      hr_asset_reject_d rd "+
				"    WHERE rh.asset_reject_id = rd.asset_reject_id) har "+
				"    ON har.deploy_restaurant_id = ar.deploy_restaurant_id "+
				"    AND har.asset_item_id = ar.asset_item_id ";
		return new CReport(sqlstr, null).findReport();
	}


}
