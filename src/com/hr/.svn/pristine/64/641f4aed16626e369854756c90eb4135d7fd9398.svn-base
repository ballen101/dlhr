package com.hr.canteen.ctr;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.hr.canteen.entity.Hr_canteen_special;
import com.hr.util.HRUtil;

public class CtrHr_canteen_special extends JPAController{

	@Override
	public String OnCCoVoid(CDBConnection con, CJPA jpa) throws Exception {
		// TODO Auto-generated method stub
		if(!HRUtil.hasRoles("28")){
			throw new Exception("非饭堂管理员，不允许作废！");
		}
		Hr_canteen_special sp=(Hr_canteen_special)jpa;
		sp.usable.setAsInt(2);
		sp.save(con);
		return null;
	}
	

}
