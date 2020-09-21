package com.hr.asset.co;

import java.util.Date;
import java.sql.Types;
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

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.CJPABase.CJPAStat;
import com.corsair.cjpa.util.CFieldinfo;
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
import com.hr.asset.entity.Hr_asset_register;
import com.hr.asset.entity.Hr_asset_statement;
import com.hr.asset.entity.Hr_asset_sum;

public class COHr_asset_statement extends JPAController{
	//流水表保存时，自动计算余额表中的数值
	@Override
	public void OnSave(CJPABase jpa, CDBConnection con, ArrayList<PraperedSql> sqllist, boolean selfLink) throws Exception {
		Hr_asset_statement as = (Hr_asset_statement) jpa;
		String sqlstr = "SELECT * FROM hr_asset_sum t WHERE t.asset_item_code= '"+as.asset_item_code.getValue()+
				"' AND t.deploy_restaurant= '"+as.deploy_restaurant.getValue()+
				"'";
		Hr_asset_sum ats=new Hr_asset_sum();
		ats.findBySQL4Update(con, sqlstr, false);//锁定更新记录
		ats.asset_item_id.setValue(as.asset_item_id.getValue()); 
		ats.asset_item_code.setValue(as.asset_item_code.getValue());
		ats.asset_item_name.setValue(as.asset_item_name.getValue());
		ats.asset_type_id.setValue(as.asset_type_id.getValue());
		ats.asset_type_code.setValue(as.asset_type_code.getValue());
		ats.asset_type_name.setValue(as.asset_type_name.getValue());
		ats.brand.setValue(as.brand.getValue());
		ats.model.setValue(as.model.getValue());
		ats.uom.setValue(as.uom.getValue());
		ats.deploy_area.setValue(as.deploy_area.getValue());
		ats.deploy_restaurant.setValue(as.deploy_restaurant.getValue());
		ats.deploy_restaurant_id.setValue(as.deploy_restaurant_id.getValue());
		ats.sum_qty.setAsFloat(ats.sum_qty.getAsFloatDefault(0)+as.adjust_qty.getAsFloatDefault(0));
		ats.creator.setValue("SYSTEM");  //creator
		ats.createtime.setAsDatetime(new Date());
		ats.save(con, false);
	}
}
