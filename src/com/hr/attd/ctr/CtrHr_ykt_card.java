package com.hr.attd.ctr;

import java.util.Date;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.hr.card.entity.Hr_ykt_card;
import com.hr.perm.entity.Hr_employee;

public class CtrHr_ykt_card extends JPAController {
	/**
	 * 通用保存后，提交事务前触发
	 * 
	 * @param con
	 * @param jpa
	 * @return 不为空的返回值，将作为查询结果返回给前台
	 * @throws Exception
	 */
	public String AfterCCoSave(CDBConnection con, CJPA jpa) throws Exception {
		Hr_ykt_card card = (Hr_ykt_card) jpa;
		Hr_employee emp = new Hr_employee();
		emp.findByID(card.er_id.getValue());
		emp.updatetime.setAsDatetime(new Date());
		emp.save(con, false);
		return null;
	}
}
