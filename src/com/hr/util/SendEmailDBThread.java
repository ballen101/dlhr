package com.hr.util;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.hr.util.hrmail.DLHRMailCenterWS;
import com.hr.util.hrmail.Hr_emailsend_log;

public class SendEmailDBThread extends Thread {
	public boolean stoped = false;
	private int maxday = 7;
	private int timespec = 120000;

	public void run() {
		while (true) {
			if (stoped)
				break;
			try {
				dosend();
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

	private void dosend() throws Exception {
		try {
			// pushsuccess 推送状态 1 新建未推送 2 新建推送完成 3 更新失败 4 更新完成
			String sqlstr = "SELECT * FROM Hr_emailsend_log WHERE createtime>DATE_SUB(NOW(), INTERVAL " + maxday + " DAY) AND pushsuccess=1 AND removed=2 ";
			CJPALineData<Hr_emailsend_log> els = new CJPALineData<Hr_emailsend_log>(Hr_emailsend_log.class);
			els.findDataBySQL(sqlstr, true, false);
			for (CJPABase jpa : els) {
				String emid = ((Hr_emailsend_log) jpa).emid.getValue();
				Hr_emailsend_log el = new Hr_emailsend_log();
				CDBConnection con = el.pool.getCon(this);
				con.startTrans();
				try {
					el.findByID4Update(con, emid, false);
					int pushsuccess = el.pushsuccess.getAsInt(0);// pushsuccess 推送状态 1 新建未推送 2 新建推送完成 3 更新失败 4 更新完成
					if (pushsuccess != 1) {
						con.rollback();
						continue;
					}
					String synid = DLHRMailCenterWS.sendmailex(el);
					if (synid != null) {
						el.emsdid.setValue(synid);// ID
						el.pushsuccess.setAsInt(2);
						el.save(con);
					}
					con.submit();
				} catch (Exception e) {
					con.rollback();
					e.printStackTrace();
				} finally {
					con.close();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			String sqlstr = "SELECT * FROM Hr_emailsend_log WHERE createtime>DATE_SUB(NOW(), INTERVAL " + maxday + " DAY) AND pushsuccess=3 AND removed=2 AND emsdid IS NOT NULL";
			CJPALineData<Hr_emailsend_log> els = new CJPALineData<Hr_emailsend_log>(Hr_emailsend_log.class);
			els.findDataBySQL(sqlstr, true, false);
			for (CJPABase jpa : els) {
				String emid = ((Hr_emailsend_log) jpa).emid.getValue();
				Hr_emailsend_log el = new Hr_emailsend_log();
				CDBConnection con = el.pool.getCon(this);
				con.startTrans();
				try {
					el.findByID4Update(con, emid, false);
					int pushsuccess = el.pushsuccess.getAsInt(0);// pushsuccess 推送状态 1 新建未推送 2 新建推送完成 3 更新失败 4 更新完成
					if ((pushsuccess != 2) && (pushsuccess != 3)) {
						con.rollback();
						continue;
					}
					DLHRMailCenterWS.updatemail(el.aynemid.getValue(), el.approvalman.getValue(), el.approvaldate.getValue());
					el.pushsuccess.setAsInt(4);
					el.save(con);
					con.submit();
				} catch (Exception e) {
					con.rollback();
					e.printStackTrace();
				} finally {
					con.close();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		checkMailNotify();// 旧方法
	}

	private void checkMailNotify() {
		try {
			// 检查中断的，
			String sqlstr = "SELECT * FROM hr_emailsend_log WHERE approvaldate IS NULL and removed=2 AND mailtype='待办' "
					+ " AND NOT EXISTS(SELECT 1 FROM shwwf w,shwwfproc p, shwwfprocuser u WHERE w.stat=1 "
					+ " AND  p.procid=u.procid AND w.wfid=p.wfid AND u.wfprocuserid=Hr_emailsend_log.extid) LIMIT 0,100";
			CJPALineData<Hr_emailsend_log> els = new CJPALineData<Hr_emailsend_log>(Hr_emailsend_log.class);
			els.findDataBySQL(sqlstr);
			for (CJPABase jpa : els) {
				try {
					Hr_emailsend_log el = (Hr_emailsend_log) jpa;
					System.out.println("delMail:" + el.aynemid.getValue() + " sqlstr:" + sqlstr);
					DLHRMailCenterWS.delMail(el.aynemid.getValue());
					el.removed.setAsInt(1);
					el.save();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// 检查已经完成的流程，发送日志没有完成的，直接删除掉
			sqlstr = "SELECT DISTINCT m.* FROM hr_emailsend_log m,shwwfprocuser u,shwwf wf WHERE u.wfid=wf.wfid AND wf.stat=3 AND m.extid=u.wfprocuserid AND  m.removed=2 AND m.pushsuccess<>4 LIMIT 0,100 ";
			els = new CJPALineData<Hr_emailsend_log>(Hr_emailsend_log.class);
			els.findDataBySQL(sqlstr);
			for (CJPABase jpa : els) {
				Hr_emailsend_log el = (Hr_emailsend_log) jpa;
				System.out.println("delMail:" + el.aynemid.getValue() + " sqlstr:" + sqlstr);
				DLHRMailCenterWS.delMail(el.aynemid.getValue());
				el.removed.setAsInt(1);
				el.save();
			}
			// 没有推送成功重新推送 没有更新成功的重新更新
			sqlstr = "SELECT * FROM hr_emailsend_log WHERE removed=2 AND pushsuccess in (1,3) AND repuhstimes<5 ORDER BY createtime ASC  LIMIT 0,100";
			els.clear();
			els.findDataBySQL(sqlstr);
			for (CJPABase jpa : els) {
				Hr_emailsend_log ml = (Hr_emailsend_log) jpa;
				if (ml.pushsuccess.getAsInt() == 1) {
					try {
						String synid = DLHRMailCenterWS.sendmailex(ml);
						if (synid != null) {
							ml.emsdid.setValue(synid);// ID
							ml.pushsuccess.setAsInt(2);
							// System.out.println(ml.tojson());
						} else {
							ml.repuhstimes.setAsInt(ml.repuhstimes.getAsIntDefault(0) + 1);
						}
						ml.save();
					} catch (Exception e) {
						if (e.getMessage().lastIndexOf("具有唯一索引 'EipMail_MailID'") >= 0) {
							ml.pushsuccess.setAsInt(2);
							ml.save();
						}
						e.printStackTrace();
					}
				}

				if (ml.pushsuccess.getAsInt() == 3) {
					try {
						DLHRMailCenterWS.updatemail(ml.emsdid.getValue(), ml.approvalman.getValue(), ml.approvaldate.getValue());
						ml.pushsuccess.setAsInt(4);
						ml.save();
					} catch (Exception e) {
						e.printStackTrace();
						ml.repuhstimes.setAsInt(ml.repuhstimes.getAsIntDefault(0) + 1);
						ml.save();
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
