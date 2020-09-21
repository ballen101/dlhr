package com.hr.util;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shworg;
import com.corsair.server.wordflow.Shwwftempproc;
import com.corsair.server.wordflow.Shwwftempprocuser;
import com.hr.attd.entity.Hrkq_business_trip;
import com.hr.attd.entity.Hrkq_holidayapp;
import com.hr.attd.entity.Hrkq_onduty;
import com.hr.attd.entity.Hrkq_overtime;
import com.hr.attd.entity.Hrkq_resign;
import com.hr.attd.entity.Hrkq_wkoff;
import com.hr.canteen.entity.Hr_canteen_guest;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_entry_prob;
import com.hr.perm.entity.Hr_transfer_prob;

public class HRJPAWFFindOrg {

	private static JPAE[] CHGJAPS = { // 需要改变审批机构的实体
			new JPAE(Hrkq_overtime.class, "er_id"), new JPAE(Hrkq_business_trip.class, "er_id"),
			new JPAE(Hrkq_holidayapp.class, "er_id"), new JPAE(Hrkq_onduty.class, "er_id"),
			new JPAE(Hrkq_resign.class, "er_id"), new JPAE(Hrkq_wkoff.class, "er_id"),
			new JPAE(Hr_canteen_guest.class, "er_id"), new JPAE(Hr_entry_prob.class, "er_id"),
			new JPAE(Hr_transfer_prob.class, "er_id")
	};

	public static CJPALineData<Shworg> OnWfFindCDTOrgs(CJPA jpa, Shwwftempproc wftemproc, Shwwftempprocuser temuser, int userfindcdt, String userid, String entid) throws Exception {
		JPAE jpae = JPAIsINClasses(jpa);
		if (jpae == null)// 不需要转换的实体
			return null;

		CField fd = jpa.cfield(jpae.eridfdname);
		if (fd == null)
			return null;
		if (fd.isEmpty())
			return null;

		Hr_employee emp = new Hr_employee();
		emp.findByID(fd.getValue());
		if (emp.isEmpty())
			return null;
		if (emp.uorgid.isEmpty())
			return null;
		CJPALineData<Shworg> curorgs = new CJPALineData<Shworg>(Shworg.class);
		if ((userfindcdt == 1) || (userfindcdt == 3)) {// 表单路径机构 表单机构字段
			Shworg org = new Shworg();
			org.findByID(emp.uorgid.getValue());
			curorgs.add(org);
		} else
			return null;
		return curorgs;
	}

	private static JPAE JPAIsINClasses(CJPA jpa) {
		for (JPAE jpae : CHGJAPS) {
			if (jpa.getClass().equals(jpae.clszz)) {
				return jpae;
			}
		}
		return null;
	}

	/**
	 * 公共保存时 重新计算机构人事层级
	 * 
	 * @param con
	 * @param jpa
	 * @throws Exception
	 */
	public static void OnJPASaveChg(CDBConnection con, CJPA jpa) throws Exception {
		CField olfd = jpa.cfield("orghrlev");
		if (olfd == null)
			return;

		JPAE jpae = JPAIsINClasses(jpa);
		if (jpae == null)// 不需要转换的实体
			return;

		CField fd = jpa.cfield(jpae.eridfdname);
		if (fd == null)
			return;
		if (fd.isEmpty())
			return;

		Hr_employee emp = new Hr_employee();
		emp.findByID(fd.getValue());
		if (emp.isEmpty())
			return;
		if (emp.uorgid.isEmpty())
			return;
		olfd.setAsInt(HRUtil.getOrgHrLev(emp.uorgid.getValue()));
	}
}
