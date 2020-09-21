package com.hr.asset.co;

import java.util.Date;
import java.text.SimpleDateFormat; 
import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.CJPABase.CJPAStat;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.JSONParm;
import com.corsair.dbpool.util.PraperedSql;
import com.corsair.dbpool.util.PraperedValue;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.genco.COShwUser;
import com.corsair.server.generic.Shw_attach;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.generic.Shworg;
import com.corsair.server.generic.Shwuser;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CExcelField;
import com.corsair.server.util.CExcelUtil;
import com.corsair.server.util.CSearchForm;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.DictionaryTemp;
import com.corsair.server.util.UpLoadFileEx;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.base.entity.Hr_grade;
import com.hr.base.entity.Hr_orgposition;
import com.hr.base.entity.Hr_orgpositionkpi;
import com.hr.base.entity.Hr_standposition;
import com.hr.base.entity.Hr_wclass;
import com.hr.asset.entity.Hr_asset_allot_h;
import com.hr.asset.entity.Hr_asset_allot_d;
import com.hr.asset.entity.Hr_asset_item;
import com.hr.asset.entity.Hr_asset_register;
import com.hr.asset.entity.Hr_asset_statement;
import com.hr.asset.entity.Hr_asset_reject_h;
import com.hr.asset.entity.Hr_asset_reject_d;

@ACO(coname = "web.hr.Asset")

public class COHr_asset_reject extends JPAController{

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		checkQty((Hr_asset_reject_h)jpa,con);
		if (isFilished) {
			doUpdateAssetList(jpa, con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc nxtproc, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			doUpdateAssetList(jpa, con);
		}
	}

	@Override
	public void OnSave(CJPABase jpa, CDBConnection con, ArrayList<PraperedSql> sqllist, boolean selfLink) throws Exception{
		checkQty((Hr_asset_reject_h)jpa,con);
	}

	//流程完成时数据插入流水表
	private void doUpdateAssetList(CJPA jpa, CDBConnection con) throws Exception {
		Hr_asset_reject_h ar = (Hr_asset_reject_h) jpa;
		Hr_asset_statement as = new Hr_asset_statement();
		for (CJPABase jpa1:ar.hr_asset_reject_ds){
			Hr_asset_reject_d ard = (Hr_asset_reject_d) jpa1;
			Float qty = 0-ard.reject_qty.getAsFloat();
			as.clear();
			//插入报废数据
			as.source.setValue("asset_reject");		
			as.source_id.setValue(ar.asset_reject_id.getValue());	
			as.source_num.setValue(ar.asset_reject_num.getValue());	
			as.asset_item_id.setValue(ard.asset_item_id.getValue());	
			as.asset_item_code.setValue(ard.asset_item_code.getValue());	
			as.asset_item_name.setValue(ard.asset_item_name.getValue());	
			as.asset_type_id.setValue(ard.asset_type_id.getValue());	
			as.asset_type_code.setValue(ard.asset_type_code.getValue());	
			as.asset_type_name.setValue(ard.asset_type_name.getValue());	
			//as.brand.setValue(ard.brand.getValue());	
			//as.model.setValue(ard.model.getValue());	
			//as.original_value.setValue(ard.original_value.getValue());	
			//as.net_value.setValue(ard.net_value.getValue());	
			as.uom.setValue(ard.uom.getValue());	
			//as.service_life.setValue(ard.service_life.getValue());	
			//as.acquired_date.setValue(ard.acquired_date.getValue());	
			as.deploy_area.setValue(ar.deploy_area.getValue());	
			as.deploy_restaurant_id.setValue(ar.deploy_restaurant_id.getValue());
			as.deploy_restaurant.setValue(ar.deploy_restaurant.getValue());
			//as.keep_own.setValue(ard.keep_own.getValue());	
			as.adjust_qty.setAsFloat(qty);	
			as.adjust_date.setValue(ard.reject_date.getValue());
			as.save(con);
		}
	}
	
	//直接查询物料数据（暂停使用）
	@ACOAction(eventname = "finditemlist", Authentication = true, ispublic = false, notes = "物料查询")
	public String finditemlist() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String pctid = CorUtil.hashMap2Str(parms, "ctid","需要参数ctid");
		Hr_asset_item ai = new Hr_asset_item();
		String sqlstr = "SELECT * "+					
				"FROM hr_asset_item t "+
				"WHERE EXISTS (SELECT * "+
				"FROM hr_asset_sum s "+
				"WHERE s.deploy_restaurant_id = "+pctid+
				"  AND s.sum_qty > 0)";
		return ai.pool.opensql2json(sqlstr);
	}

	//检查调出数量是否超出该餐厅下的余额数量
	private void checkQty(Hr_asset_reject_h ar,CDBConnection con) throws Exception{
		for(CJPABase jpa:ar.hr_asset_reject_ds){
			Hr_asset_reject_d ard=(Hr_asset_reject_d) jpa;
			String restaurantid = ar.deploy_restaurant_id.getValue();
			String itemid = ard.asset_item_id.getValue();
			String sqlstr = "SELECT t.sum_qty "+
					" FROM hr_asset_sum t "+
					" WHERE t.deploy_restaurant_id = "+restaurantid+
					" AND t.asset_item_id = " +itemid;
			JSONArray result = con.opensql2json_o(sqlstr);
			if (!result.isEmpty()){
				int qty = result.getJSONObject(0).getInt("sum_qty");
				if(qty<ard.reject_qty.getAsInt()){
					throw new Exception("报废数量超过该餐厅可用余额数量("+qty+")，不允许保存和提交");
				}
			}else{
				throw new Exception("该资产在调出餐厅下没有配置，不允许报废");
			}
		}
	}

	//查询资产登记数据
	@ACOAction(eventname = "findAssetregisterlist", Authentication = true, ispublic = false, notes = "登记信息查询")
	public String findAssetregisterlist() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String pctid = CorUtil.hashMap2Str(parms, "pctid","需要参数pctid");
		Hr_asset_register ar = new Hr_asset_register();
		String sqlstr = "SELECT t.*"+
						"  FROM hr_asset_register t"+
						" WHERE t.stat = 9"+
						"   AND EXISTS (SELECT 1"+
						"          FROM hr_asset_sum a"+
						"         WHERE a.asset_item_id = t.asset_item_id"+
						"			AND a.deploy_restaurant_id = "+pctid+
						"           AND a.sum_qty > 0)";
		return ar.pool.opensql2json(sqlstr);
	}
}
