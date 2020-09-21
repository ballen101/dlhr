package com.corsair.server.cjpa;

import java.util.HashMap;
import java.util.List;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.JPAControllerBase;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.JSONParm;
import com.corsair.server.cjpa.CWFNotify.NotityType;
import com.corsair.server.generic.Shworg;
import com.corsair.server.generic.Shwuser;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.corsair.server.wordflow.Shwwfprocuser;
import com.corsair.server.wordflow.Shwwftempproc;
import com.corsair.server.wordflow.Shwwftempprocuser;

/**
 * JPA控制器 CO通用控制实现
 * 
 * @author Administrator
 *
 */
public class JPAController extends JPAControllerBase {

	// JPA ///////////////////// 这里面代码可能会重复执行!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	public Shwuser OnWFFindUserByPosition(CJPA jpa, Shwwf wf, Shwwfproc proc, Shwwftempprocuser puser) throws Exception {
		return null;
	}

	/**
	 * 有流程才会触发
	 * 
	 * @param jpa
	 * @param wftempid
	 * @param con
	 * @throws Exception
	 */
	public void BeforeWFStart(CJPA jpa, String wftempid, CDBConnection con) throws Exception {
	}

	/**
	 * 有流程才会触发
	 * 
	 * @param jpa
	 * @param wf
	 * @param con
	 * @param isFilished
	 * @throws Exception
	 */
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
	}

	/**
	 * 不管是否有流程都会触发
	 * 
	 * @param jpa
	 * @param con
	 * @param wf
	 * @param isFilished
	 * @throws Exception
	 */
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished) throws Exception {
	}

	public void OnArriveProc(CJPA jpa, Shwwf wf, Shwwfproc proc, CDBConnection con, ArraiveType at) throws Exception {
	}

	public void OnLiveProc(CJPA jpa, Shwwf wf, Shwwfproc proc, CDBConnection con, ArraiveType at) throws Exception {

	}

	/**
	 * 节点完成才触发
	 * 
	 * @param jpa
	 * @param con
	 * @param wf
	 * @param proc
	 * @param nxtproc
	 * @param isFilished
	 * @throws Exception
	 */
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc, Shwwfproc nxtproc, boolean isFilished) throws Exception {

	}

	/**
	 * 只要用户提交就触发，不一定完成节点
	 * 
	 * @param jpa
	 * @param con
	 * @param wf
	 * @param proc
	 * @param puser
	 * @param nxtproc
	 * @param isFilished
	 */
	public void OnWfUserSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc, Shwwfprocuser puser, Shwwfproc nxtproc, boolean isFilished) {

	}

	/**
	 * 流程通过机构查找类型，获取机构列表时候
	 * 
	 * @param wftemproc
	 * @param temuser
	 * @param userfindcdt
	 * @param userid
	 * @param entid
	 * @return 返回NULL将调用默认方法,不为NULL的返回值将作为流程处理条件
	 */
	public CJPALineData<Shworg> OnWfFindCDTOrgs(CJPA jpa, Shwwftempproc wftemproc, Shwwftempprocuser temuser, int userfindcdt, String userid, String entid) throws Exception {
		return null;
	}

	public void OnWfReject(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc, Shwwfproc nxtproc) throws Exception {
	}

	public void OnWfBreak(CJPA jpa, Shwwf wf, CDBConnection con) throws Exception {
	}

	public void OnWfTransfer(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc, Shwuser fruser, Shwuser touser) throws Exception {
	}

	/**
	 * @param nt
	 * @param jpa
	 * @param wf
	 * @param proc
	 * @param uis
	 * @param puser
	 *            当 nt 为转办 时，为转办节点转办流程用户，此时已经新用户了
	 * @param actuserid
	 */
	public void OnWFNotityMail(NotityType nt, CJPABase jpa, Shwwf wf, Shwwfproc proc, List<CNotifyUEInfo> uis, Shwwfprocuser puser, String actuserid) {
		CWFNotify.sendStandMail(nt, jpa, wf, proc, uis, puser, actuserid);
	}

	public void OnWFNotitySms(NotityType nt, CJPABase jpa, Shwwf wf, Shwwfproc proc, List<CNotifyUEInfo> uis, String actuserid) {
		CWFNotify.sendStandSms(nt, jpa, wf, proc, uis, actuserid);
	}

	public void OnWFNotityWechat(NotityType nt, CJPABase jpa, Shwwf wf, Shwwfproc proc, List<CNotifyUEInfo> uis, String actuserid) {
		CWFNotify.sendStandWechat(nt, jpa, wf, proc, uis, actuserid);
	}

	// 通用CO 控制接口
	// edittps:{"isedit":true,"issubmit":true,"isview":true,"isupdate":false,"isfind":true}
	// 不为空的返回值，将作为查询结果返回给前台
	public String OnCCoFindList(Class<CJPA> jpaclass, List<JSONParm> jps, HashMap<String, String> edts, boolean enableIdpath, boolean selfline)
			throws Exception {
		return null;
	}

	// 不为空的返回值，将作为查询结果返回给前台 JOorJS JSONObject or JSONArray
	public String AfterCOFindList(Class<CJPA> jpaclass, Object JOorJS, int page, int pagesize) throws Exception {
		return null;
	}

	// 通用 CO 控制接口
	public String OnCCoFindBuildWhere(CJPA jpa, HashMap<String, String> urlparms) throws Exception {
		return null;
	}

	// 不为空的返回值，将作为查询结果返回给前台
	public String OnCCoFindByID(Class<CJPA> jpaclass, String id) throws Exception {
		return null;
	}

	/**
	 * 通用保存后，提交事务前触发
	 * 
	 * @param con
	 * @param jpa
	 * @return 不为空的返回值，将作为查询结果返回给前台
	 * @throws Exception
	 */
	public String AfterCCoSave(CDBConnection con, CJPA jpa) throws Exception {
		return null;
	}

	/**
	 * 通用保存前触发，如果是新数据，ID CODE 等系统自动生成的字段为空
	 * 
	 * @param con
	 * @param jpa
	 * @return 不为空的返回值，将作为查询结果返回给前台
	 * @throws Exception
	 */
	public String OnCCoSave(CDBConnection con, CJPA jpa) throws Exception {
		return null;
	}

	// 不为空的返回值，将作为查询结果返回给前台
	public String OnCCoCopy(Class<CJPA> jpaclass, HashMap<String, String> pparms) throws Exception {
		return null;
	}

	// 不为空的返回值，将作为查询结果返回给前台
	public String OnCCoDel(CDBConnection con, Class<CJPA> jpaclass, String id) throws Exception {
		return null;
	}

	// 不为空的返回值，将作为查询结果返回给前台
	public String OnCCoVoid(CDBConnection con, CJPA jpa) throws Exception {
		return null;
	}

	// 不为空的返回值，将作为查询结果返回给前台
	public String OnCCoUnSubmitWF(CDBConnection con, CJPA jpa) throws Exception {
		return null;
	}

	// 不为空的返回值，将作为查询结果返回给前台
	public String OnCCoCreateWF(CJPA jpa, String wftempid, String jpaid) throws Exception {
		return null;
	}

	// 不为空的返回值，将作为查询结果返回给前台
	public String OnCCoSubmitWF(CJPA jpa, String procid, String aoption) throws Exception {
		return null;
	}

	// 不为空的返回值，将作为查询结果返回给前台
	public String OnCCoRejectWF(CJPA jpa, String fprocid, String tprocid, String aoption) throws Exception {
		return null;
	}

	// 不为空的返回值，将作为查询结果返回给前台
	public String OnCCoBreakWF(CJPA jpa) throws Exception {
		return null;
	}

	// 不为空的返回值，将作为查询结果返回给前台
	public String OnCCoTransferWF(CJPA jpa, String tuserid, String aoption) throws Exception {
		return null;
	}

	// 不为空的返回值，将作为查询结果返回给前台
	public String OnCCoGetAttach(String id) throws Exception {
		return null;
	}

	// 不为空的返回值，将作为查询结果返回给前台
	public String OnCCoUploadAttach(String id) throws Exception {
		return null;
	}
	// end 通用Co
}
