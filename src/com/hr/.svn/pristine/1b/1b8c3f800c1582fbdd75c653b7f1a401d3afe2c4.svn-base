package com.hr.util;

import java.util.Date;
import java.util.HashMap;
import java.util.TimerTask;

import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.Systemdate;
import com.hr.canteen.co.COHr_canteen_costrecords;
import com.hr.canteen.entity.Hr_canteen_costrecordscount;

public class TimerTaskHRCountRecords extends TimerTask{
	@Override
	public void run() {
		Logsw.debug("统计计算消费记录");
		try{
			Date lastdate = Systemdate.getFirstAndLastOfMonth(Systemdate.getDateByStr(Systemdate.getStrDate())).date1;//本月第一天
			Date firstdate=Systemdate.dateMonthAdd(lastdate, -1);//上月第一天
			 String sqlstr = "DELETE FROM hr_canteen_costrecordscount WHERE costtime>='" + Systemdate.getStrDateyyyy_mm_dd(firstdate) + "' AND costtime<'" + Systemdate.getStrDateyyyy_mm_dd(lastdate) + "'  ";
			Hr_canteen_costrecordscount crcs = new Hr_canteen_costrecordscount();
			crcs.pool.execsql(sqlstr);// 删除已经存在的数据
			String empo=null;
			HashMap<String, String> empmealnums=new HashMap<String, String>(1048576);
			while (true) {
				int countct = COHr_canteen_costrecords.countCostRecords( firstdate, lastdate,empo,empmealnums);
				System.out.println("统计计算消费记录【" + countct + "】条【" + Systemdate.getStrDate() + "】");
				if (countct == 0)
					break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

}
