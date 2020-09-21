package com.hr.util.hrmail;

import com.corsair.dbpool.CDBConnection;

public class Thread_HRMailCenter extends Thread {
	private int acttype = 0; // 1: new 2: update 3:del
	private Hr_emailsend_log ml = null;
	private String extid;
	private String approvalman;
	private String approvaldate;

	public Thread_HRMailCenter(int acttype, Hr_emailsend_log ml) {
		this.acttype = acttype;
		this.ml = ml;
	}

	public Thread_HRMailCenter(int acttype, String extid, String approvalman, String approvaldate) {
		this.acttype = acttype;
		this.extid = extid;
		this.approvalman = approvalman;
		this.approvaldate = approvaldate;
	}

	public void run() {
		try {
			if (acttype == 1) {
				ml.save();
			}
			if (acttype == 2) {
				Hr_emailsend_log el = new Hr_emailsend_log();
				CDBConnection con = el.pool.getCon(this);
				con.startTrans();
				try {
					String sqlstr = "select * from hr_emailsend_log where extid='" + extid + "' FOR UPDATE";
					el.findBySQL4Update(con, sqlstr, false);
					el.pushsuccess.setAsInt(3);// pushsuccess 推送状态 1 新建未推送 2 新建推送完成 3 更新失败 4 更新完成
					el.approvalman.setValue(approvalman);
					el.approvaldate.setValue(approvaldate);
					el.save(con);
					con.submit();
				} catch (Exception e) {
					con.rollback();
					throw e;
				} finally {
					con.close();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
