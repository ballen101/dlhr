package com.hr.perm.ctr;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shworg;
import com.corsair.server.wordflow.Shwwfprocuser;
import com.hr.perm.entity.Hr_employee_transfer;
import com.hr.perm.entity.Hr_entry;
import com.hr.perm.entity.Hr_entry_prob;

public class WfProcProcoption {

	// 调出
	public void afterFindTranUser(CJPA jpa, CJPALineData<Shwwfprocuser> procusers) throws Exception {
		Hr_employee_transfer tr = (Hr_employee_transfer) jpa;
		if (isqunOrg(tr.odorgid.getValue())) {// 是群机构
			java.util.Iterator<CJPABase> iter = procusers.iterator();
			while (iter.hasNext()) {
				Shwwfprocuser pu = (Shwwfprocuser) iter.next();
				if (!isqunUser(pu)) {// 不是群审批用户
					iter.remove();
				}
			}
		}
	}

	// 检查编制
	public void beforeSubmitCheckQuota(CJPA jpa) throws Exception {
		Hr_employee_transfer ep = (Hr_employee_transfer) jpa;
		if (ep.quota_over.getAsInt() == 1) {// 超编
			if (ep.quota_over_rst.isEmpty())
				throw new Exception("超编的调动需要评审超编处理方式");
		}
	}

	// 调入
	public void afterFindTrantoUser(CJPA jpa, CJPALineData<Shwwfprocuser> procusers) throws Exception {
		Hr_employee_transfer tr = (Hr_employee_transfer) jpa;
		if (isqunOrg(tr.neworgid.getValue())) {// 是群机构
			java.util.Iterator<CJPABase> iter = procusers.iterator();
			while (iter.hasNext()) {
				Shwwfprocuser pu = (Shwwfprocuser) iter.next();
				if (!isqunUser(pu)) {// 不是群审批用户
					iter.remove();
				}
			}
		}
	}

	// 是否制造群下面的机构
	private boolean isqunOrg(String orgid) throws Exception {
		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty()) {
			throw new Exception("ID为【" + orgid + "】的机构不存在");
		}
		if (org.idpath.isEmpty())
			return false;
		String idps = org.idpath.getValue();
		idps = idps.substring(0, idps.length() - 1);
		String sqlstr = "select * from shworg where orgid in(" + idps + ")";
		CJPALineData<Shworg> orgs = new CJPALineData<Shworg>(Shworg.class);
		orgs.findDataBySQL(sqlstr, true, false);
		for (CJPABase jpa : orgs) {
			Shworg o = (Shworg) jpa;
			if (o.orgtype.getAsIntDefault(0) == 4) {
				return true;
			}
		}
		return false;
	}

	// 是否工厂下面的机构
	private boolean isGCOrg(String orgid) throws Exception {
		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty()) {
			throw new Exception("ID为【" + orgid + "】的机构不存在");
		}
		if (org.idpath.isEmpty())
			return false;
		String idps = org.idpath.getValue();
		idps = idps.substring(0, idps.length() - 1);
		String sqlstr = "select * from shworg where orgid in(" + idps + ")";
		CJPALineData<Shworg> orgs = new CJPALineData<Shworg>(Shworg.class);
		orgs.findDataBySQL(sqlstr, true, false);
		for (CJPABase jpa : orgs) {
			Shworg o = (Shworg) jpa;
			if (o.orgtype.getAsIntDefault(0) == 6) {
				return true;
			}
		}
		return false;
	}

	// 是否是制造群下面的用户
	private boolean isqunUser(Shwwfprocuser pu) throws Exception {
		String sqlstr = "SELECT o.* FROM shworguser ou,shworg o WHERE ou.orgid=o.orgid AND ou.userid=" + pu.userid.getValue();
		CJPALineData<Shworg> orgs = new CJPALineData<Shworg>(Shworg.class);
		orgs.findDataBySQL(sqlstr, true, false);
		for (CJPABase jpa : orgs) {
			Shworg o = (Shworg) jpa;
			if (isqunOrg(o.orgid.getValue()))
				return true;
		}
		return false;
	}

	// 是否是工厂下面的用户
	private boolean isGCUser(Shwwfprocuser pu) throws Exception {
		String sqlstr = "SELECT o.* FROM shworguser ou,shworg o WHERE ou.orgid=o.orgid AND ou.userid=" + pu.userid.getValue();
		CJPALineData<Shworg> orgs = new CJPALineData<Shworg>(Shworg.class);
		orgs.findDataBySQL(sqlstr, true, false);
		for (CJPABase jpa : orgs) {
			Shworg o = (Shworg) jpa;
			if (isGCOrg(o.orgid.getValue()))
				return true;
		}
		return false;
	}

	public void afterFindEntryUser1(CJPA jpa, CJPALineData<Shwwfprocuser> procusers) throws Exception {
		Hr_entry et = (Hr_entry) jpa;
		if (isqunOrg(et.orgid.getValue())) {
			java.util.Iterator<CJPABase> iter = procusers.iterator();
			while (iter.hasNext()) {
				Shwwfprocuser pu = (Shwwfprocuser) iter.next();
				if (!isqunUser(pu)) {// 不是群审批用户
					iter.remove();
				}
			}
			return;
		}
	}

	public void afterFindEntryUser2(CJPA jpa, CJPALineData<Shwwfprocuser> procusers) throws Exception {
		Hr_entry et = (Hr_entry) jpa;
		if (isGCOrg(et.orgid.getValue())) {
			java.util.Iterator<CJPABase> iter = procusers.iterator();
			while (iter.hasNext()) {
				Shwwfprocuser pu = (Shwwfprocuser) iter.next();
				if (!isGCUser(pu)) {// 不是工厂审批用户
					iter.remove();
				}
			}
			return;
		} else if (isqunOrg(et.orgid.getValue())) {
			java.util.Iterator<CJPABase> iter = procusers.iterator();
			while (iter.hasNext()) {
				Shwwfprocuser pu = (Shwwfprocuser) iter.next();
				if (!isqunUser(pu)) {// 不是群审批用户
					iter.remove();
				}
			}
			return;
		}
	}

	public void afterFindEntryProbUser1(CJPA jpa, CJPALineData<Shwwfprocuser> procusers) throws Exception {
		Hr_entry_prob et = (Hr_entry_prob) jpa;

		if (isGCOrg(et.orgid.getValue())) {
			java.util.Iterator<CJPABase> iter = procusers.iterator();
			while (iter.hasNext()) {
				Shwwfprocuser pu = (Shwwfprocuser) iter.next();
				if (!isGCUser(pu)) {// 不是工厂审批用户
					iter.remove();
				}
			}
			return;
		} else if (isqunOrg(et.orgid.getValue())) {
			java.util.Iterator<CJPABase> iter = procusers.iterator();
			while (iter.hasNext()) {
				Shwwfprocuser pu = (Shwwfprocuser) iter.next();
				if (!isqunUser(pu)) {// 不是群审批用户
					iter.remove();
				}
			}
			return;
		} else { 
			java.util.Iterator<CJPABase> iter = procusers.iterator();
			while (iter.hasNext()) {
				Shwwfprocuser pu = (Shwwfprocuser) iter.next();
				if (isqunUser(pu) || (isGCUser(pu))) {// 是群或工厂
					iter.remove();
				}
			}
			return;
		}

	}

	// [{"acttion":"create","stage":"after","funcname":"com.hr.perm.ctr.WfProcProcoption.afterFindEntryUser","prams":"jpa,procusers"}]

}
