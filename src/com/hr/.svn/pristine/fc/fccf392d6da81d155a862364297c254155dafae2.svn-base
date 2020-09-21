package com.hr.canteen.ctr;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.hr.canteen.entity.Hr_canteen_mealclass;

public class CtrHr_canteen_mealclass extends JPAController{

	@Override
	public String OnCCoVoid(CDBConnection con, CJPA jpa) throws Exception {
		// TODO Auto-generated method stub
		Hr_canteen_mealclass mc=(Hr_canteen_mealclass)jpa;
		mc.usable.setAsInt(2);
		mc.save(con);
		return null;
	}
	

}
