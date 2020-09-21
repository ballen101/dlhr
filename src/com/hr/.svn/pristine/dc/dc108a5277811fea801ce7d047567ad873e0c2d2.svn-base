package com.hr.perm.entity;

import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.hr.perm.ctr.CtrHr_employee_linked;

@CEntity(tablename = "hr_employee", controller = CtrHr_employee_linked.class)
public class Hr_employee_linked extends Hr_employee {

	@CLinkFieldInfo(jpaclass = Hr_employee_work.class, linkFields = { @LinkFieldItem(lfield = "er_id", mfield = "er_id") })
	public CJPALineData<Hr_employee_work> hr_employee_works;
	@CLinkFieldInfo(jpaclass = Hr_employee_reward.class, linkFields = { @LinkFieldItem(lfield = "er_id", mfield = "er_id") })
	public CJPALineData<Hr_employee_reward> hr_employee_rewards;
	//@CLinkFieldInfo(jpaclass = Hr_employee_relation.class, linkFields = { @LinkFieldItem(lfield = "er_id", mfield = "er_id") })
	//public CJPALineData<Hr_employee_relation> hr_employee_relations;
	@CLinkFieldInfo(jpaclass = Hr_employee_phexa.class, linkFields = { @LinkFieldItem(lfield = "er_id", mfield = "er_id") })
	public CJPALineData<Hr_employee_phexa> hr_employee_phexas;
	@CLinkFieldInfo(jpaclass = Hr_employee_leanexp.class, linkFields = { @LinkFieldItem(lfield = "er_id", mfield = "er_id") })
	public CJPALineData<Hr_employee_leanexp> hr_employee_leanexps;
	@CLinkFieldInfo(jpaclass = Hr_employee_family.class, linkFields = { @LinkFieldItem(lfield = "er_id", mfield = "er_id") })
	public CJPALineData<Hr_employee_family> hr_employee_familys;
	@CLinkFieldInfo(jpaclass = Hr_employee_cretl.class, linkFields = { @LinkFieldItem(lfield = "er_id", mfield = "er_id") })
	public CJPALineData<Hr_employee_cretl> hr_employee_cretls;
	@CLinkFieldInfo(jpaclass = Hr_employee_trainexp.class, linkFields = { @LinkFieldItem(lfield = "er_id", mfield = "er_id") })
	public CJPALineData<Hr_employee_trainexp> hr_employee_trainexps;

	public Hr_employee_linked() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

}
