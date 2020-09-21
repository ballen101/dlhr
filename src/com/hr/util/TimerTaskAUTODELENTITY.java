package com.hr.util;

import java.util.TimerTask;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.server.cjpa.CJPA;
import com.hr.base.entity.Hr_auto_delenty;

public class TimerTaskAUTODELENTITY extends TimerTask {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			CJPALineData<Hr_auto_delenty> ads = new CJPALineData<Hr_auto_delenty>(Hr_auto_delenty.class);
			String sqlstr = "SELECT * FROM hr_auto_delenty WHERE useable=1";
			ads.findDataBySQL(sqlstr);
			for (CJPABase j : ads) {
				Hr_auto_delenty ad = (Hr_auto_delenty) j;
				if (ad.delwhere.isEmpty()) {
					throw new Exception("自动删除【" + ad.adename.getValue() + "】条件不允许为空");
				}
				CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(ad.adeclass.getValue());
				if (jpa != null) {
					while (true) {
						sqlstr = "select * from " + jpa.tablename + " where stat=1 and createtime is not null and createtime<DATE_ADD(NOW(), INTERVAL -" + ad.delwhere.getValue() + " DAY) ";
						jpa.clear();
						jpa.findBySQL(sqlstr);
						if (jpa.isEmpty())
							break;
						else
							jpa.delete();
					}
				}
			}
		} catch (Exception e) {
			System.out.println("自动删除实体错误");
			e.printStackTrace();
		}

	}
}
