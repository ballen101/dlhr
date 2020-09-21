package com.hr.salary.ctr;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Logsw;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.attd.ctr.HrkqUtil;
import com.hr.salary.entity.Hr_salary_orgminstandard;
import com.hr.salary.entity.Hr_salary_positionwage;
import com.hr.salary.entity.Hr_salary_specchgsa_batch;
import com.hr.salary.entity.Hr_salary_specchgsa_batch_line;
import com.hr.salary.entity.Hr_salary_structure;
import com.hr.salary.entity.Hr_salary_yearraise;
import com.hr.salary.entity.Hr_salary_yearraise_line;
import com.hr.salary.entity.Hr_salary_yearraise_quota;

public class CtrHr_salary_yearraise extends JPAController{

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf,
			boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		Logsw.dblog("AfterWFStart");
		setYRQutaSubmitStat(jpa,con);
		if(isFilished){
			Hr_salary_yearraise yr=(Hr_salary_yearraise)jpa;
			CtrSalaryCommon.newSalaryChangeLog(con,yr);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf,
			Shwwfproc proc, Shwwfproc nxtproc, boolean isFilished)
			throws Exception {
		Logsw.dblog("OnWfSubmit");
		// TODO Auto-generated method stub
		setYRQutaSubmitStat(jpa,con);
		if(isFilished){
			Hr_salary_yearraise yr=(Hr_salary_yearraise)jpa;
			CtrSalaryCommon.newSalaryChangeLog(con,yr);
		}
	}

	@Override
	public void BeforeWFStart(CJPA jpa, String wftempid, CDBConnection con)
			throws Exception {
		Logsw.dblog("BeforeWFStart");
		// TODO Auto-generated method stub
		Hr_salary_yearraise yr=(Hr_salary_yearraise)jpa;
		Hr_salary_yearraise_quota yrq=new Hr_salary_yearraise_quota();
		yrq.findByID(yr.yrqid.getValue());
		if(!yrq.isEmpty()){
			if(yrq.usable.getAsInt()==2){
				throw new Exception("id为【"+yrq.yrqid.getValue()+"】的年度调薪额度【"+yrq.yrqname.getValue()+"】已失效");
			}
			if(yrq.isused.getAsInt()==1){
				throw new Exception("id为【"+yrq.yrqid.getValue()+"】的年度调薪额度【"+yrq.yrqname.getValue()+"】已被使用，不能再次提交");
			}
		}
		int highnosubmit = Integer.valueOf(HrkqUtil.getParmValue("SA_YEARRAISE_HIGHNOSUBMIT"));// 为1时调薪后职位工资高于该职位工资标准不准提交
		if(highnosubmit==1){
			CJPALineData<Hr_salary_yearraise_line> yrls=yr.hr_salary_yearraise_lines;
			for(int i=0;i<yrls.size();i++){
				Hr_salary_yearraise_line yrl=(Hr_salary_yearraise_line)yrls.get(i);
				Hr_salary_positionwage ptw=new Hr_salary_positionwage();
				ptw.findBySQL("SELECT pw.* FROM `hr_salary_positionwage` pw,`hr_orgposition` op WHERE pw.stat=9 AND pw.usable=1 AND pw.ospid=op.sp_id AND op.ospid="+yrl.ospid.getValue());
				if(!ptw.isEmpty()){
					if(yrl.newposition_salary.getAsFloatDefault(0)>ptw.psl5.getAsFloatDefault(0)){
						throw new Exception("员工【"+yrl.employee_code.getValue()+"】的调整后职位工资高于该职位工资标准");
					}
				}
			}
		}
	}
	
	private void setYRQutaSubmitStat(CJPA jpa,  CDBConnection con) throws Exception{
		Hr_salary_yearraise yr=(Hr_salary_yearraise)jpa;
		Hr_salary_yearraise_quota yrq=new Hr_salary_yearraise_quota();
		yrq.findByID(yr.yrqid.getValue());
		if(!yrq.isEmpty()){
			if(yrq.isused.getAsInt()==2){
				yrq.isused.setAsInt(1);
				yrq.save(con);
			}
		}
	}

	@Override
	public void OnWfBreak(CJPA jpa, Shwwf wf, CDBConnection con)
			throws Exception {
		// TODO Auto-generated method stub
		setYRQutaBreakStat(jpa,con);
	}
	
	private void setYRQutaBreakStat(CJPA jpa,  CDBConnection con) throws Exception{
		Hr_salary_yearraise yr=(Hr_salary_yearraise)jpa;
		Hr_salary_yearraise_quota yrq=new Hr_salary_yearraise_quota();
		yrq.findByID(yr.yrqid.getValue());
		if(!yrq.isEmpty()){
			if(yrq.isused.getAsInt()==1){
				yrq.isused.setAsInt(2);
				yrq.save(con);
			}
		}
	}	
	@Override
	public void BeforeSave(CJPABase jpa, CDBConnection con, boolean selfLink)
			throws Exception {
		// TODO Auto-generated method stub
		Logsw.dblog("BeforeSave");
		Hr_salary_yearraise scsb=(Hr_salary_yearraise)jpa;
		CJPALineData<Hr_salary_yearraise_line> scsbls=scsb.hr_salary_yearraise_lines;
		Logsw.dblog("BeforeSave子表数量为："+scsbls.size());
		for(int i=0;i<scsbls.size();i++){
			
			Hr_salary_yearraise_line scs=(Hr_salary_yearraise_line)scsbls.get(i);
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
	
	private void resetsalaryinfo(Hr_salary_yearraise_line scs) throws Exception{	
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
