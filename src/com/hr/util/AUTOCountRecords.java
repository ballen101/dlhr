package com.hr.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Systemdate;
import com.hr.base.entity.Hr_orgposition;
import com.hr.canteen.co.COHr_canteen_costrecords;
import com.hr.canteen.entity.Hr_canteen_costrecordscount;
import com.hr.perm.entity.Hr_employee;
import com.hr.salary.entity.Hr_salary_chglg;
import com.hr.salary.entity.Hr_salary_list;
import com.hr.util.hrmail.DLHRMailCenterWS;
import com.hr.util.hrmail.Hr_emailsend_log;

public class AUTOCountRecords extends Thread {
	public boolean stoped = false;
	private int timespec = 86400000;

	public void run() {
		while (true) {
			if (stoped)
				break;
			try {
				Calendar cal = Calendar.getInstance();
				int countday=cal.get(Calendar.DAY_OF_MONTH);
				if(countday==5){
					docountrecords();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(timespec);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}

	private void docountrecords() throws Exception {
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
