package com.hr.salary.ctr;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.salary.entity.Hr_salary_orgminstandard;
import com.hr.salary.entity.Hr_salary_positionwage;
import com.hr.salary.entity.Hr_salary_specchgsa_batch;
import com.hr.salary.entity.Hr_salary_specchgsa_batch_line;
import com.hr.salary.entity.Hr_salary_structure;

public class CtrHr_salary_specchgsa_batch extends JPAController{

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf,
			boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if(isFilished){
			Hr_salary_specchgsa_batch scsb=(Hr_salary_specchgsa_batch)jpa;
			CtrSalaryCommon.newSalaryChangeLog(con,scsb);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf,
			Shwwfproc proc, Shwwfproc nxtproc, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if(isFilished){
			Hr_salary_specchgsa_batch scsb=(Hr_salary_specchgsa_batch)jpa;
			CtrSalaryCommon.newSalaryChangeLog(con,scsb);
		}
	}

	@Override
	public void BeforeSave(CJPABase jpa, CDBConnection con, boolean selfLink)
			throws Exception {
		// TODO Auto-generated method stub
		Hr_salary_specchgsa_batch scsb=(Hr_salary_specchgsa_batch)jpa;
		CJPALineData<Hr_salary_specchgsa_batch_line> scsbls=scsb.hr_salary_specchgsa_batch_lines;
		for(int i=0;i<scsbls.size();i++){
			Hr_salary_specchgsa_batch_line scs=(Hr_salary_specchgsa_batch_line)scsbls.get(i);
			if((((scs.newposition_salary.getAsFloatDefault(0)>scs.oldposition_salary.getAsFloatDefault(0))
					||(scs.newtech_allowance.getAsFloatDefault(0)>scs.oldtech_allowance.getAsFloatDefault(0)))
					&&scs.pbtsarylev.getAsFloatDefault(0)==0)||
					(((scs.newposition_salary.getAsFloatDefault(0)>scs.oldposition_salary.getAsFloatDefault(0)))
							&&scs.overf_salary.getAsFloatDefault(0)==0)||((scs.newposition_salary.getAsFloatDefault(0)-scs.oldposition_salary.getAsFloatDefault(0))>scs.pbtsarylev.getAsFloatDefault(0))){
				resetsalaryinfo(scs);
				//throw new Exception("薪资有调整但调整额度为0，请检查单据或清理浏览器缓存后再制单。（清理浏览器缓存方法：同时按 ctr+shift+delete 即可弹出删除缓存对话框。）");
			}
		}
		
	}
	
	private void resetsalaryinfo(Hr_salary_specchgsa_batch_line scs) throws Exception{	
		System.out.println("重新核算-------------------");
		float np=scs.newposition_salary.getAsFloatDefault(0);
		float op=scs.oldposition_salary.getAsFloatDefault(0);
		if(((np>op)&scs.pbtsarylev.getAsFloatDefault(0)==0)||((np-op)>scs.pbtsarylev.getAsFloatDefault(0))){
			Hr_salary_structure  ss=new Hr_salary_structure();
			ss.findByID(scs.newstru_id.getValue());
			if(!ss.isEmpty()){
				if(ss.strutype.getAsInt()==1){
					float minstand=0;
					String sqlstr="SELECT * FROM `hr_salary_orgminstandard` WHERE stat=9 AND usable=1 AND INSTR('"+scs.idpath.getValue()+"',idpath)=1  ORDER BY idpath DESC  ";
					Hr_salary_orgminstandard oms=new Hr_salary_orgminstandard();
					oms.findBySQL(sqlstr);
					if(oms.isEmpty()){
						minstand=0;
					}else{
						minstand=oms.minstandard.getAsFloatDefault(0);
					}
					float bw=(np*ss.basewage.getAsFloatDefault(0))/100;
					float bow=(np*ss.otwage.getAsFloatDefault(0))/100;
					float sw=(np*ss.skillwage.getAsFloatDefault(0))/100;
					float pw=(np*ss.meritwage.getAsFloatDefault(0))/100;
					if(minstand>bw){
						if((bw+bow)>minstand){
							bow=bw+bow-minstand;
							bw=minstand;
						}else if((bw+bow+sw)>minstand){
							sw=bw+bow+sw-minstand;
							bow=0;
							bw=minstand;
						}else if((bw+bow+sw+pw)>minstand){
							pw=bw+bow+sw+pw-minstand;
							sw=0;
							bow=0;
							bw=minstand;
						}else{
							bw=np;
							pw=0;
							sw=0;
							bow=0;
						}
					}
					scs.newbase_salary.setAsFloat(bw);
					scs.newtech_salary.setAsFloat(sw);
					scs.newachi_salary.setAsFloat(pw);
					scs.newotwage.setAsFloat(bow);
					scs.chgbase_salary.setAsFloat(bw-scs.oldbase_salary.getAsFloatDefault(0));
					scs.chgtech_salary.setAsFloat(sw-scs.oldtech_salary.getAsFloatDefault(0));
					scs.chgachi_salary.setAsFloat(pw-scs.oldachi_salary.getAsFloatDefault(0));
					scs.chgotwage.setAsFloat(bow-scs.oldotwage.getAsFloatDefault(0));
				}
				scs.chgposition_salary.setAsFloat(np-scs.oldposition_salary.getAsFloatDefault(0));
				scs.chgtech_allowance.setAsFloat(scs.newtech_allowance.getAsFloatDefault(0)-scs.oldtech_allowance.getAsFloatDefault(0));
				scs.pbtsarylev.setAsFloat(np+scs.newtech_allowance.getAsFloatDefault(0)-scs.oldposition_salary.getAsFloatDefault(0)-scs.oldtech_allowance.getAsFloatDefault(0));
			}
		}
		if(np>0){
			Hr_salary_positionwage spw=new Hr_salary_positionwage();
			spw.findBySQL("SELECT pw.* FROM `hr_salary_positionwage` pw,`hr_orgposition` op WHERE pw.stat=9 AND pw.usable=1 AND pw.ospid=op.sp_id AND op.ospid="+scs.ospid.getValue());
			if(spw.isEmpty()){
				scs.overf_salary.setValue(0);
				scs.overf_salary_chgtech.setValue(0);
			}else{
				if(np>spw.psl5.getAsFloatDefault(0)){
					float spwage=spw.psl5.getAsFloatDefault(0);
					float chgspw=np-spwage;
					scs.overf_salary.setAsFloat(chgspw);
					float chgper=(chgspw/spwage)*100;
					scs.overf_salary_chgtech.setAsFloat(chgper);
				}
			}
		}
		
	}

}
