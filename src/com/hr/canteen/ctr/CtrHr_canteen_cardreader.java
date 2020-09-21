package com.hr.canteen.ctr;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.hr.canteen.entity.Hr_canteen_cardreader;

public class CtrHr_canteen_cardreader extends JPAController{

	@Override
	public String OnCCoVoid(CDBConnection con, CJPA jpa) throws Exception {
		// TODO Auto-generated method stub
		Hr_canteen_cardreader card=(Hr_canteen_cardreader)jpa;
		card.usable.setAsInt(2);
		card.save(con);
		return null;
	}
	

}
