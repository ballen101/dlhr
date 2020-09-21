package com.hr.canteen.ctr;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.hr.canteen.entity.Hr_canteen_guest;
import com.hr.util.HRUtil;

public class CtrHr_canteen_guest extends JPAController{

	@Override
	public String OnCCoVoid(CDBConnection con, CJPA jpa) throws Exception {
		// TODO Auto-generated method stub
		if(!HRUtil.hasRoles("28")){
			throw new Exception("非饭堂管理员，不允许作废！");
		}
		Hr_canteen_guest ctg=(Hr_canteen_guest)jpa;
		ctg.usable.setAsInt(2);
		//ctg.save(con);
		return null;
	}
	

}
