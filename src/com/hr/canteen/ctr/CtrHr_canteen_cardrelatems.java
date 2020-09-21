package com.hr.canteen.ctr;

import com.corsair.cjpa.CJPABase;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.JPAController;
import com.hr.canteen.entity.Hr_canteen_cardrelatems;

public class CtrHr_canteen_cardrelatems extends JPAController{

	@Override
	public void BeforeSave(CJPABase jpa, CDBConnection con, boolean selfLink)
			throws Exception {
		// TODO Auto-generated method stub
		if(selfLink){
			checkCardRelates(jpa);
		}
	}
	
	private void checkCardRelates(CJPABase jpa) throws NumberFormatException, Exception{
		if(jpa instanceof Hr_canteen_cardrelatems){
			Hr_canteen_cardrelatems crms=(Hr_canteen_cardrelatems)jpa;
			if(!crms.crms_id.isEmpty()){
				return ;
			}
			String ctcr_id=crms.ctcr_id.getValue();
			String mc_id=crms.mc_id.getValue();
			String sqlstr="select count(*) ct from hr_canteen_cardrelatems where ctcr_id="+ctcr_id+" and mc_id="+mc_id;
			if(Integer.valueOf(crms.pool.openSql2List(sqlstr).get(0).get("ct"))!=0){
				throw new Exception("卡机：【"+crms.ctcr_name.getValue()+"】在餐类【"+crms.mc_name.getValue()+"】的关联餐标时重复！");
			}
		}
	}

}
