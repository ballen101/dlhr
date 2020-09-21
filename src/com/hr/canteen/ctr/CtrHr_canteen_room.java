package com.hr.canteen.ctr;

import java.util.HashMap;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.base.CSContext;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.hr.canteen.entity.Hr_canteen_cardreader;
import com.hr.canteen.entity.Hr_canteen_room;
import com.hr.perm.entity.Hr_transfer_prob;
import com.hr.salary.co.Cosacommon;

import net.sf.json.JSONObject;

public class CtrHr_canteen_room extends JPAController{

	@Override
	public String OnCCoVoid(CDBConnection con, CJPA jpa) throws Exception {
		// TODO Auto-generated method stub
		Hr_canteen_room room=(Hr_canteen_room)jpa;
		room.usable.setAsInt(2);
		room.save(con);
		return null;
	}
	/**
	 * 通用保存后，提交事务前触发
	 * 
	 * @param con
	 * @param jpa
	 * @return 不为空的返回值，将作为查询结果返回给前台
	 * @throws Exception
	 */
	@Override
	public String AfterCCoSave(CDBConnection con, CJPA jpa) throws Exception {
		Hr_canteen_room ep = (Hr_canteen_room) jpa;
		save_canteen_room_chg(ep,con);
		return null;
	}
	private void save_canteen_room_chg(Hr_canteen_room jpa, CDBConnection con) throws Exception {
	String	sqlstr="update hr_canteen_cardreader set ctr_name='"+jpa.ctr_name.getValue()+"',area='"+jpa.address.getValue()+"' where ctr_id='"+ jpa.ctr_id.getValue()+"'";
//	sqlupdatea = "UPDATE shwuser_wf_agent SET auserid='" + agent_id + "',ausername='" + agent_code + "',adisplayname='" + agent_name + "' WHERE userid='" + employee_id + "'";
	con.execsql(sqlstr);
	}
	

}
