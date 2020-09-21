package com.hr.recruit.co;

import com.corsair.dbpool.DBPools;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;

@ACO(coname = "web.hr.rcom")
public class COHr_reciuitcommon {
	@ACOAction(eventname = "getrtinfo", Authentication = true, notes = "getrtinfo")
	public String getrtinfo() throws Exception {
		String sqlstr = "SELECT recruit_transport_id id,recruit_transport_name text FROM Hr_recruit_transport WHERE recruit_transport_stat=1 AND cooperate_end_date>NOW()";
		return DBPools.defaultPool().opensql2json_O(sqlstr).toString();
	}
}
