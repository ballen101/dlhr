package com.hr.canteen.ctr;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.hr.canteen.entity.Hr_canteen_mealsystem;

public class CtrHr_canteen_mealsystem extends JPAController{

	@Override
	public String OnCCoVoid(CDBConnection con, CJPA jpa) throws Exception {
		// TODO Auto-generated method stub
		Hr_canteen_mealsystem ms=(Hr_canteen_mealsystem)jpa;
		ms.usable.setAsInt(2);
		ms.save(con);
		return null;
	}
	

}
