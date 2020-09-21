package com.hr.util;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.ctrl.OnChangeOrgInfoListener;
import com.corsair.server.generic.Shworg;

public class OrgInfoChangeListener extends OnChangeOrgInfoListener {

	@Override
	public void OnOrgChg(CDBConnection con, JSONObject jorg, JSONObject jporg) throws Exception {
		// TODO Auto-generated method stub
		updateDataOnOrg2Org(con, jorg);
		// throw new Exception("就是不让你调整");
	}

	private void updateDataOnOrg2Org(CDBConnection con, JSONObject jorg) throws Exception {
		List<String> sqls = new ArrayList<String>();
		String idpath = jorg.getString("idpath");
		String orgid = jorg.getString("orgid");
		String eorgname = jorg.getString("extorgname");
		sqls.add("UPDATE hr_black_add SET idpath='" + idpath + "',orgname='" + eorgname + "' WHERE orgid=" + orgid);
		sqls.add("UPDATE hr_black_del SET idpath='" + idpath + "',orgname='" + eorgname + "' WHERE orgid=" + orgid);
		sqls.add("UPDATE hr_emploanbatch SET idpath='" + idpath + "',odorgname='" + eorgname + "' WHERE odorgid=" + orgid);
		sqls.add("UPDATE hr_emploanbatch SET neworgname='" + eorgname + "' WHERE neworgid=" + orgid);
		sqls.add("UPDATE hr_emploanbatch_line SET odorgname='" + eorgname + "' WHERE odorgid=" + orgid);
		sqls.add("UPDATE hr_emploanbatch_line SET neworgname='" + eorgname + "' WHERE neworgid=" + orgid);

		sqls.add("UPDATE hr_employee SET idpath='" + idpath + "',orgname='" + eorgname + "' WHERE orgid=" + orgid);
		sqls.add("UPDATE hr_employee_contract SET idpath='" + idpath + "',orgname='" + eorgname + "' WHERE orgid=" + orgid);
		sqls.add("UPDATE hr_employee_contract_change SET idpath='" + idpath + "',orgname='" + eorgname + "' WHERE orgid=" + orgid);
		sqls.add("UPDATE hr_employee_contract_stop SET idpath='" + idpath + "',orgname='" + eorgname + "'  WHERE orgid=" + orgid);
		sqls.add("UPDATE hr_employee_contract_relieve SET idpath='" + idpath + "',orgname='" + eorgname + "'  WHERE orgid=" + orgid);

		sqls.add("UPDATE hr_employee_transfer SET idpath='" + idpath + "',odorgname='" + eorgname + "' WHERE odorgid=" + orgid);
		sqls.add("UPDATE hr_employee_transfer SET neworgname='" + eorgname + "' WHERE neworgid=" + orgid);
		sqls.add("UPDATE hr_empptjob_app SET idpath='" + idpath + "',odorgname='" + eorgname + "' WHERE odorgid=" + orgid);
		sqls.add("UPDATE hr_empptjob_app SET neworgname='" + eorgname + "' WHERE neworgid=" + orgid);

		// sqls.add("UPDATE hr_empptjob_info SET idpath='"+idpath+"' WHERE odorgid="+orgid);
		sqls.add("UPDATE hr_emptransferbatch SET idpath='" + idpath + "',oldorgname='" + eorgname + "' WHERE olorgid=" + orgid);
		sqls.add("UPDATE hr_emptransferbatch SET neworgname='" + eorgname + "' WHERE neworgid=" + orgid);

		sqls.add("UPDATE hr_entry SET idpath='" + idpath + "' WHERE orgid=" + orgid);
		sqls.add("UPDATE hr_entry_prob SET idpath='" + idpath + "',orgname='" + eorgname + "' WHERE orgid=" + orgid);
		sqls.add("UPDATE hr_leavejob SET idpath='" + idpath + "',orgname='" + eorgname + "' WHERE orgid=" + orgid);
		sqls.add("UPDATE hr_leavejob_cancel SET idpath='" + idpath + "',orgname='" + eorgname + "' WHERE orgid=" + orgid);
		sqls.add("UPDATE hr_orgposition SET idpath='" + idpath + "',orgname='" + eorgname + "' WHERE orgid=" + orgid);
		sqls.add("UPDATE hr_quota_chglog SET idpath='" + idpath + "',orgname='" + eorgname + "' WHERE orgid=" + orgid);
		sqls.add("UPDATE hr_train_reg SET idpath='" + idpath + "' WHERE orgid=" + orgid);
		sqls.add("UPDATE hr_train_reg SET norgname='" + eorgname + "' WHERE norgid=" + orgid);

		sqls.add("UPDATE hr_transfer_prob SET idpath='" + idpath + "',neworgname='" + eorgname + "'  WHERE neworgid=" + orgid);
		sqls.add("UPDATE hr_transfer_prob SET odorgname='" + eorgname + "'  WHERE odorgid=" + orgid);
		con.execSqls(sqls);
	}

	@Override
	public void OnOrgData2Org(CDBConnection con, Shworg oldorg, JSONObject dorg_s) throws Exception {
		// TODO Auto-generated method stub
		// throw new Exception("还没写完，等几天");
		List<String> sqls = new ArrayList<String>();
		String newidpath = dorg_s.getString("idpath");
		String neworgid = dorg_s.getString("orgid");
		String neworgname = dorg_s.getString("extorgname");
		String newcode = dorg_s.getString("code");
		String oldorgid = oldorg.orgid.getValue();
		sqls.add("UPDATE hr_black_add SET orgid=" + neworgid + ",orgcode='" + newcode + "',orgname='" + neworgname + "',idpath='" + newidpath
				+ "' WHERE orgid=" + oldorgid);
		sqls.add("UPDATE hr_black_del SET orgid=" + neworgid + ",orgcode='" + newcode + "',orgname='" + neworgname + "',idpath='" + newidpath
				+ "' WHERE orgid=" + oldorgid);
		sqls.add("UPDATE hr_emploanbatch SET odorgid=" + neworgid + ",odorgcode='" + newcode + "',odorgname='" + neworgname + "',idpath='" + newidpath
				+ "' WHERE odorgid=" + oldorgid);
		sqls.add("UPDATE hr_emploanbatch SET neworgid=" + neworgid + ",neworgcode='" + newcode + "',neworgname='" + neworgname + "' WHERE neworgid=" + oldorgid);
		sqls.add("UPDATE hr_emploanbatch_line SET odorgid=" + neworgid + ",odorgcode='" + newcode + "',odorgname='" + neworgname + "' WHERE odorgid="
				+ oldorgid);
		sqls.add("UPDATE hr_emploanbatch_line SET neworgid=" + neworgid + ",neworgcode='" + newcode + "',neworgname='" + neworgname + "' WHERE neworgid="
				+ oldorgid);
		sqls.add("UPDATE hr_employee SET orgid=" + neworgid + ",orgcode='" + newcode + "',orgname='" + neworgname + "',idpath='" + newidpath + "' WHERE orgid="
				+ oldorgid);
		sqls.add("UPDATE hr_employee_contract SET orgid=" + neworgid + ",orgcode='" + newcode + "',orgname='" + neworgname + "',idpath='" + newidpath
				+ "' WHERE orgid=" + oldorgid);
		sqls.add("UPDATE hr_employee_contract_change SET orgid=" + neworgid + ",orgcode='" + newcode + "',orgname='" + neworgname + "',idpath='" + newidpath
				+ "' WHERE orgid=" + oldorgid);
		sqls.add("UPDATE hr_employee_contract_relieve SET orgid=" + neworgid + ",orgcode='" + newcode + "',orgname='" + neworgname + "',idpath='" + newidpath
				+ "' WHERE orgid=" + oldorgid);
		sqls.add("UPDATE hr_employee_contract_stop  SET orgid=" + neworgid + ",orgcode='" + newcode + "',orgname='" + neworgname + "',idpath='" + newidpath
				+ "' WHERE orgid=" + oldorgid);
		sqls.add("UPDATE hr_employee_transfer SET odorgid=" + neworgid + ",odorgcode='neworgcode',odorgname='" + neworgname
				+ "',idpath='newidpaht' WHERE odorgid=" + oldorgid);
		sqls.add("UPDATE hr_employee_transfer SET neworgid=" + neworgid + ",neworgcode='neworgcode',neworgname='" + neworgname + "' WHERE neworgid=" + oldorgid);
		sqls.add("UPDATE hr_empptjob_app SET odorgid=" + neworgid + ",odorgcode='neworgcode',odorgname='" + neworgname + "',idpath='newidpaht' WHERE odorgid="
				+ oldorgid);
		sqls.add("UPDATE hr_empptjob_app SET neworgid=" + neworgid + ",neworgcode='neworgcode',neworgname='" + neworgname + "' WHERE neworgid=" + oldorgid);
		sqls.add("UPDATE hr_emptransferbatch SET olorgid=" + neworgid + ",oldorgcode='neworgcode',oldorgname='" + neworgname
				+ "',idpath='newidpaht' WHERE olorgid=" + oldorgid);
		sqls.add("UPDATE hr_emptransferbatch SET neworgid=" + neworgid + ",neworgcode='neworgcode',neworgname='" + neworgname + "' WHERE neworgid=" + oldorgid);
		sqls.add("UPDATE hr_emptransferbatch_line SET odorgid=" + neworgid + ",odorgcode='neworgcode',odorgname='" + neworgname + "'  WHERE odorgid="
				+ oldorgid);
		sqls.add("UPDATE hr_entry SET orgid=" + neworgid + ",idpath='newidpaht' WHERE orgid=" + oldorgid);
		sqls.add("UPDATE hr_entry_prob SET orgid=" + neworgid + ",orgcode='neworgcode',orgname='" + neworgname + "',idpath='newidpaht' WHERE orgid=" + oldorgid);
		sqls.add("UPDATE hr_leavejob SET orgid=" + neworgid + ",orgcode='neworgcode',orgname='" + neworgname + "',idpath='newidpaht' WHERE orgid=" + oldorgid);
		sqls.add("UPDATE hr_leavejob_cancel SET orgid=" + neworgid + ",orgcode='neworgcode',orgname='" + neworgname + "',idpath='newidpaht' WHERE orgid="
				+ oldorgid);
		sqls.add("UPDATE hr_orgposition SET orgid=" + neworgid + ",orgcode='neworgcode',orgname='" + neworgname + "',idpath='newidpaht' WHERE orgid="
				+ oldorgid);
		sqls.add("UPDATE hr_quota_releaseline SET orgid=" + neworgid + ",orgcode='neworgcode',orgname='" + neworgname + "' WHERE orgid=" + oldorgid);
		sqls.add("UPDATE hr_quotaoc_chglog SET orgid=" + neworgid + ",orgcode='neworgcode',orgname='" + neworgname + "',idpath='newidpaht' WHERE orgid="
				+ oldorgid);
		sqls.add("UPDATE hr_quotaoc_releaseline SET orgid=" + neworgid + ",orgcode='neworgcode',orgname='" + neworgname + "' WHERE orgid=" + oldorgid);
		sqls.add("UPDATE hr_train_reg SET orgid=" + neworgid + ",idpath='newidpaht' WHERE orgid=" + oldorgid);
		sqls.add("UPDATE hr_train_reg SET norgid=" + neworgid + ",norgname='" + neworgname + "' WHERE norgid=" + oldorgid);
		sqls.add("UPDATE hr_traindisp_batchline SET orgid=" + neworgid + ",orgcode='neworgcode',orgname='" + neworgname + "' WHERE orgid=" + oldorgid);
		sqls.add("UPDATE hr_traindisp_batchline SET norgid=" + neworgid + ",norgname='" + neworgname + "' WHERE norgid=" + oldorgid);
		sqls.add("UPDATE hr_trainentry_batchline SET orgid=" + neworgid + ",orgcode='neworgcode',orgname='" + neworgname + "' WHERE orgid=" + oldorgid);
		sqls.add("UPDATE hr_trainentry_batchline SET norgid=" + neworgid + ",norgname='" + neworgname + "' WHERE norgid=" + oldorgid);
		sqls.add("UPDATE hr_traintran_batchline SET orgid=" + neworgid + ",orgcode='neworgcode',orgname='" + neworgname + "' WHERE orgid=" + oldorgid);
		sqls.add("UPDATE hr_traintran_batchline SET norgid=" + neworgid + ",norgname='" + neworgname + "' WHERE norgid=" + oldorgid);
		sqls.add("UPDATE hr_transfer_prob SET odorgid=" + neworgid + ",odorgcode='neworgcode',odorgname='" + neworgname + "' WHERE odorgid=" + oldorgid);
		sqls.add("UPDATE hr_transfer_prob SET neworgid=" + neworgid + ",neworgcode='neworgcode',neworgname='" + neworgname
				+ "',idpath='newidpaht' WHERE neworgid=" + oldorgid);
		con.execSqls(sqls);
	}
}
