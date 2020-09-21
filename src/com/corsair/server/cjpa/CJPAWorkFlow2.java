package com.corsair.server.cjpa;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPACommon;
import com.corsair.cjpa.CJPAForDelphi;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.JPAControllerBase;
import com.corsair.cjpa.JPAControllerBase.ArraiveType;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.exception.CWFException;
import com.corsair.server.generic.Shworg;
import com.corsair.server.generic.Shworg_find;
import com.corsair.server.generic.Shwuser;
import com.corsair.server.util.CorUtil;
import com.corsair.server.wordflow.FindWfTemp;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwflinkline;
import com.corsair.server.wordflow.Shwwflinklinecondition;
import com.corsair.server.wordflow.Shwwfproc;
import com.corsair.server.wordflow.Shwwfproccondition;
import com.corsair.server.wordflow.Shwwfproclog;
import com.corsair.server.wordflow.Shwwfprocuser;
import com.corsair.server.wordflow.Shwwftemp;
import com.corsair.server.wordflow.Shwwftemplinkline;
import com.corsair.server.wordflow.Shwwftemplinklinecondition;
import com.corsair.server.wordflow.Shwwftempproc;
import com.corsair.server.wordflow.Shwwftempproccondition;
import com.corsair.server.wordflow.Shwwftempprocuser;

/**
 * JPA的工作流实现
 * 
 * @author shangwen
 * 
 */
public abstract class CJPAWorkFlow2 extends CJPACommon {
	// 流程状态
	private class WFStat {
		public final static int waitfor = 1;// 待审批
		public final static int focued = 2;// 活动节点
		public final static int finished = 3;// 完成
	}

	// 节点状态
	private class ProcStat {
		public final static int waitfor = 1;// 待审批
		public final static int focued = 2;// 活动节点
		public final static int finished = 3;// 完成
	}

	// 节点用户状态
	private class ProcUserStat {
		public final static int waitfor = 1;// 1 待审批
		public final static int finished = 2;// 2 已完成
		public final static int refused = 3;// 3 驳回
	}

	private class EOAction {
		// acttion create:创建节点实例 submit:提交 break:中断 refus 驳回 transfer 转办
		public final static String _create = "create";
		public final static String _submit = "submit";
		public final static String _break = "break";
		public final static String _refus = "refus";
		public final static String _transfer = "transfer";
	}

	private class EOStage {
		// stage: 'before',// before after
		public final static String _before = "before";
		public final static String _after = "after";
	}

	private class EOParmsstr {
		public final static String _dbcon = "dbcon";
		public final static String _jpa = "jpa";
		public final static String _wftemp = "wftemp";
		public final static String _tempproc = "tempproc";
		public final static String _tempprocusers = "tempprocusers";
		public final static String _wf = "wf";
		public final static String _proc = "proc";
		public final static String _procusers = "procusers";
		public final static String _fromproc = "fromproc";
		public final static String _toproc = "toproc";
		public final static String _touser = "touser";
	}

	/**
	 * 扩展函数处理
	 * 
	 * @author shangwen
	 *
	 */
	// private class EOParms {
	// public CDBConnection dbcon = null;
	// public CJPA jpa = null;
	// public Shwwftemp wftemp = null;
	// public Shwwftempproc tempproc = null;
	// public CJPALineData<Shwwftempprocuser> tempprocusers = null;
	// public Shwwf wf = null;
	// public Shwwfproc proc = null;
	// public CJPALineData<Shwwfprocuser> procusers = null;
	// public Shwwfproc fromproc = null;
	// public Shwwfproc toproc = null;
	// public Shwuser touser = null;
	// }

	public CJPAWorkFlow2() throws Exception {

	}

	public CJPAWorkFlow2(String sqlstr) throws Exception {
		super(sqlstr);
	}

	/**
	 * 启动流程 创建流程实例
	 * 
	 * @param wftempid
	 * @return
	 * @throws Exception
	 */
	public CJPA wfcreate(String wftempid) throws Exception {
		return wfcreate(wftempid, new String[] {});
	}

	/**
	 * 启动流程 创建流程实例
	 * 自动匹配流程模板的提交流程，
	 * 如果匹配到多个模板提示错误
	 * 如果没有匹配到模板直接提交
	 * 
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public CJPA wfcreateByDefaultWftemp(CDBConnection con) throws Exception {
		CJPALineData<Shwwftemp> wfts = FindWfTemp.findwftemp((CJPA) this);
		if (wfts.size() > 1)
			throw new Exception("自动提交流程发现多个适配的流程模板时,需要指定流程模板");
		String wftempid = (wfts.size() == 1) ? ((Shwwftemp) wfts.get(0)).wftempid.getValue() : null;
		return wfcreate(wftempid, con, new String[] {});
	}

	/**
	 * 启动流程 创建流程实例
	 * 子类继承的函数 ，用来接受流程信息
	 * 启动流程 创建流程实例
	 * 
	 * @param wftempid
	 * @param ckuserids
	 * @return
	 * @throws Exception
	 */
	public CJPA wfcreate(String wftempid, String[] ckuserids) throws Exception {
		CDBConnection con = pool.getCon(this);
		con.startTrans();// 开始事务
		try {
			wfcreate(wftempid, con, CSContext.getUserID(), CSContext.getCurEntID(), ckuserids);
			con.submit();
			return (CJPA) this;
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}
	
	/**移动端用
	 * 启动流程 创建流程实例
	 * 子类继承的函数 ，用来接受流程信息
	 * 启动流程 创建流程实例
	 * 
	 * @param wftempid 流程模板ID
	 * @param ckuserids 签批人
	 * @return
	 * @throws Exception
	 */
	public CJPA wfcreateForMobile(String wftempid,String userid, String entid, String[] ckuserids) throws Exception {
		CDBConnection con = pool.getCon(this);
		con.startTrans();// 开始事务
		try {
			wfcreate(wftempid, con, userid,entid, ckuserids);
			con.submit();
			return (CJPA) this;
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	/**
	 * 启动流程 创建流程实例
	 * 
	 * @param wftempid
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public CJPA wfcreate(String wftempid, CDBConnection con) throws Exception {
		return wfcreate(wftempid, con, new String[] {});
	}

	/**
	 * 启动流程 创建流程实例
	 * 
	 * @param wftempid
	 * @param con
	 * @param ckuserids
	 * @return
	 * @throws Exception
	 */
	public CJPA wfcreate(String wftempid, CDBConnection con, String[] ckuserids) throws Exception {
		return wfcreate(wftempid, con, CSContext.getUserID(), CSContext.getCurEntID(), ckuserids);
	}

	/**
	 * 启动流程 创建流程实例
	 * 将创建流程实例与提交首节点分开
	 * 
	 * @param wftempid 
	 * @param con
	 * @param userid
	 * @param entid
	 * @param ckuserids
	 * @return
	 * @throws Exception
	 */
	public CJPA wfcreate(String wftempid, CDBConnection con, String userid, String entid, String[] ckuserids) throws Exception {
		Shwuser cuser = new Shwuser();
		cuser.findByID(userid);
		findByID4Update(con); // 锁定业务单据
		if (getPublicControllerBase() != null)
			((JPAController) getPublicControllerBase()).BeforeWFStart((CJPA) this, wftempid, con);
		JPAControllerBase ctr = getController();
		if (ctr != null)
			((JPAController) ctr).BeforeWFStart((CJPA) this, wftempid, con);

		Shwwftemp wftem = new Shwwftemp();
		Shwwf wf = null;
		Shwwfproc procnext = null;
		CField StatField = cfield("stat");
		if (StatField == null)
			throw new Exception("对象没有找到stat字段，不允许创建流程!");
		if (StatField.getAsIntDefault(0) != 1)
			throw new Exception("只有制单状态的对象，才允许创建流程,当前状态为【" + StatField.getAsIntDefault(0) + "】");
		boolean filished = false;
		if ((wftempid == null) || wftempid.isEmpty()) {
			StatField.setAsInt(9);
			filished = true;
			save(con);
		} else {
			wf = new Shwwf();
			if (getJpaStat() != CJPAStat.RSLOAD4DB)
				throw new Exception("未保存的对象，不允许创建流程!");
			CField WfidField = cfield("wfid");
			if (WfidField == null)
				throw new Exception("对象没有找到wfid字段，不允许创建流程!");

			wftem.findByID(wftempid, true);
			if (wftem.isEmpty())
				throw new Exception("ID为【" + wftempid + "】的流程模板不存在");

			createNewWFObjByTemID(con, wf, wftem, cuser, entid);//根据模板分明建相关对像

			CreateProcsRes(wf, wftem, con);// 创建流程关联关系
			Shwwfproc bgproc = getBeginProc(wf);
			if (bgproc == null) {
				throw new Exception("查找开始节点错误！");
			}

			String sbjt = null;
			for (CField sfd : wfsubjectfields) {
				sbjt = sbjt + sfd.getValue() + ".";
			}
			if ((sbjt != null) && (!sbjt.isEmpty()))
				sbjt = sbjt.substring(1, sbjt.length() - 1);
			if (sbjt == null)
				wf.subject.setValue(wftem.wftempname.getValue());
			else
				wf.subject.setValue(wftem.wftempname.getValue() + "(" + sbjt + ")");
			wf.ojcectid.setValue(this.getid());
			WfidField.setValue(wf.wfid.getValue());

			if (getPublicControllerBase() != null)
				((JPAController) getPublicControllerBase()).BeforeWFStartSave((CJPA) this, wf, con, filished);
			ctr = getController();
			if (ctr != null)
				((JPAController) ctr).BeforeWFStartSave((CJPA) this, wf, con, filished);
			// newwflog(wf, bgproc, 6, cuser.userid.getValue(), cuser.displayname.getValue(), "起草");// 插入起草日志
			bgproc.stat.setAsInt(ProcStat.focued);// 设置头节点为活动节点
			StatField.setAsInt(2);// 设置单据为审批中
			wf.save(con);
			save(con);
			wfsubmit(con, bgproc.procid.getValue(), "起草", userid, entid, ckuserids, false);
		}
		// save(con);
		if (getPublicControllerBase() != null)
			((JPAController) getPublicControllerBase()).AfterWFStart((CJPA) this, con, wf, filished);
		ctr = getController();
		if (ctr != null)
			((JPAController) ctr).AfterWFStart((CJPA) this, con, wf, filished);
		save(con);
		if (wf != null) {
			if (filished) {
				CWFNotify.wfFlishNotify(this, wf, userid);
			} else {
				CWFNotify.procArivedNotify(this, wf, procnext, userid);
			}
		}
		return (CJPA) this;
	}

	private Shwwfproc getBeginProc(Shwwf wf) {
		CJPALineData<Shwwfproc> wfprocs = wf.shwwfprocs;
		for (CJPABase jpa : wfprocs) {
			Shwwfproc proc = (Shwwfproc) jpa;
			if (proc.isbegin.getAsIntDefault(0) == 1)
				return proc;
		}
		return null;
	}

	/**
	 * 根据流程模板创建流程实例，并将每个节点岗位替换成实际用户
	 * 
	 * @param con
	 * @param wf
	 * @param wftem
	 * @param cuser
	 * @param entid
	 * @throws Exception
	 */
	private void createNewWFObjByTemID(CDBConnection con, Shwwf wf, Shwwftemp wftem, Shwuser cuser, String entid) throws Exception {
		wf.assignfield(wftem, true);// 给相同字段赋值
		wf.wftemid.setValue(wftem.wftempid.getValue());
		wf.wfname.setValue(wftem.wftempname.getValue());
		wf.creator.setValue(cuser.displayname.getValue());
		wf.createtime.setAsDatetime(new Date());
		wf.entid.setValue(entid);
		wf.submituserid.setValue(cuser.userid.getValue());
		createProcsBytemProcs(con, wf, wf.shwwfprocs, wftem.shwwftempprocs, cuser, entid);
		CreateWFLinksByTemp(wf.shwwflinklines, wftem.shwwftemplinklines);
	}

	private void CreateProcsRes(Shwwf wf, Shwwftemp wftem, CDBConnection con) throws Exception {
		// wf.checkOrCreateIDCode(con, "shwwf");
		wf.checkOrCreateIDCode(con, true);
		for (CJPABase jpaproc : wf.shwwfprocs) {
			Shwwfproc proc = (Shwwfproc) jpaproc;
			for (CJPABase jpaproctem : wftem.shwwftempprocs) {
				Shwwftempproc proctem = (Shwwftempproc) jpaproctem;
				if (proc.proctempid.getValue().equalsIgnoreCase(proctem.proctempid.getValue())) {
					setprocnext(wf, proc, proctem, con);
				}
			}
		}
	}

	private void setprocnext(Shwwf wf, Shwwfproc proc, Shwwftempproc proctem, CDBConnection con) throws Exception {
		for (CJPABase jpaline : wf.shwwflinklines) {
			Shwwflinkline line = (Shwwflinkline) jpaline;
			if (line.fromprocid.getValue().equals(proctem.proctempid.getValue())) {
				line.fromprocid.setValue(proc.procid.getValue());
				for (CJPABase japprocnxt : wf.shwwfprocs) {
					Shwwfproc procnext = (Shwwfproc) japprocnxt;
					if (line.toprocid.getValue().equals(procnext.proctempid.getValue())) {
						line.toprocid.setValue(procnext.procid.getValue());
						break;
					}
				}
			}
		}
	}

	private void createProcsBytemProcs(CDBConnection con, Shwwf wf, CJPALineData<Shwwfproc> wfprocs,
			CJPALineData<Shwwftempproc> wftemprocs, Shwuser cuser, String entid) throws Exception {
		// EOParms eoparms = new EOParms();
		// eoparms.wf = wf;
		// eoparms.dbcon = con;
		// eoparms.jpa = (CJPA) this;
		for (CJPABase jpawftemproc : wftemprocs) {
			Shwwftempproc wftemproc = (Shwwftempproc) jpawftemproc;
			// eoparms.tempproc = wftemproc;
			// dealProcExtOptions(wftemproc, EOAction._create, EOStage._before, eoparms);
			Shwwfproc wfproc = new Shwwfproc();
			wfproc.assignfield(wftemproc, true);// 给相同字段赋值
			wfproc.wfid.setValue(null);
			wfproc.procid.setValue(null);
			wfproc.procname.setValue(wftemproc.proctempname.getValue());
			wfproc.stat.setAsInt(1);// 待审批
			wfproc.nextprocs.setValue(null);// 用以标记实际审批过程中流程流向
			if (wfproc.proctempid.isEmpty())
				throw new Exception("流程模板节点id获取错误!");
			wfprocs.add(wfproc);
			createProcUsersByTem(wf, wfproc, wfproc.shwwfprocusers, wftemproc, cuser, entid);// 创建用户
			if ((wfproc.allowemptyuser.getAsInt() == 2) && (wfproc.proctype.getAsIntDefault(0) != 4)) {// 不允许空用户 不是自动节点
				if ((wfproc.shwwfprocusers.size() == 0) && (wfproc.isend.getAsIntDefault(0) != 1) && (wfproc.isbegin.getAsIntDefault(0) != 1)) {
					throw new Exception("流程节点【" + wftemproc.proctempname.getValue() + "】无执行人");
				}
			}
			CreateProcconditionsByTemp(wfproc.shwwfprocconditions, wftemproc.shwwftempprocconditions);// 创建条件
			// eoparms.procusers = wfproc.shwwfprocusers;
			// dealProcExtOptions(wftemproc, EOAction._create, EOStage._after, eoparms);
			if ((wfproc.allowemptyuser.getAsInt() == 2) && (wfproc.proctype.getAsIntDefault(0) != 4)) {// 不允许空用户 不是自动节点
				if ((wfproc.shwwfprocusers.size() == 0) && (wfproc.isend.getAsIntDefault(0) != 1) && (wfproc.isbegin.getAsIntDefault(0) != 1)) {
					throw new Exception("流程节点【" + wftemproc.proctempname.getValue() + "】无执行人");
				}
			}
		}
	}

	private void createProcUsersByTem(Shwwf wf, Shwwfproc wfproc, CJPALineData<Shwwfprocuser> users,
			Shwwftempproc wftemproc, Shwuser cuser, String entid) throws Exception {
		CJPALineData<Shwwftempprocuser> temusers = wftemproc.shwwftempprocusers;
		if (wfproc.isbegin.getAsIntDefault(0) == 1) {
			Shwwfprocuser user = new Shwwfprocuser();
			user.userid.setValue(cuser.userid.getValue());
			user.displayname.setValue(cuser.displayname.getValue());
			user.isposition.setAsInt(2);
			user.sortindex.setAsInt(1);
			user.stat.setAsInt(ProcUserStat.finished);// 已经完成 2
			user.jointype.setAsInt(1);
			wfproc.stat.setAsInt(ProcStat.finished);// 头结点设置成已经完成3
			users.add(user);
			return;
		}
		for (CJPABase jpatemuser : temusers) {
			Shwwftempprocuser temuser = (Shwwftempprocuser) jpatemuser;
			if (temuser.isposition.getAsIntDefault(0) == 2) {// 真人
				if (temuser.userid.getAsIntDefault(0) == -1) {// 提交者
					Shwwfprocuser user = new Shwwfprocuser();
					user.assignfield(jpatemuser, true);
					user.userid.setValue(cuser.userid.getValue());
					user.displayname.setValue(cuser.displayname.getValue());
					user.stat.setAsInt(1);// 待审批
					users.add(user);
				} else if (temuser.userid.getAsIntDefault(0) == -2) {// 管理员
					// 根据流程管理员ID载入用户信息
				} else {// 普通老百姓
					Shwwfprocuser user = new Shwwfprocuser();
					user.assignfield(jpatemuser, true);
					users.add(user);
				}
			} else {// 岗位
				CJPALineData<Shwuser> orgusers = findUserByPosition(wftemproc, wf, wfproc, temuser, cuser.userid.getValue(), entid);
				for (CJPABase jpauser : orgusers) {
					Shwuser user = (Shwuser) jpauser;
					Shwwfprocuser procuser = new Shwwfprocuser();
					procuser.assignfield(temuser, true);
					procuser.userid.setValue(user.userid.getValue());
					procuser.displayname.setValue(user.displayname.getValue());
					users.add(procuser);
				}
			}
		} // for
	}

	private CJPALineData<Shwuser> findUserByPosition(Shwwftempproc wftemproc, Shwwf wf, Shwwfproc proc, Shwwftempprocuser temuser, String userid, String entid)
			throws Exception {
		String PostionID = temuser.userid.getValue();
		if (temuser.userfindcdt.isEmpty())
			throw new Exception("审批岗位【" + temuser.displayname.getValue() + "】【岗位查找类型】不允许为空");
		int userfindcdt = temuser.userfindcdt.getAsIntDefault(1);// 1 表单路径机构 2 登录用户所在机构 3 表单机构字段 4 所有机构 5 JPA控制器
		int userfindtype = temuser.userfindtype.getAsIntDefault(0);// 1 当前机构 2 机构上朔 3机构下朔 4 所有机构 5 JPA控制器 6 兼管机构 7上朔最近 8 下朔最近 9兼管最近

		CJPALineData<Shworg> orgs = null;
		JPAControllerBase ctr0 = getController();// 先以实体对应的控制器为主
		if (ctr0 != null)
			orgs = ((JPAController) ctr0).OnWfFindCDTOrgs((CJPA) this, wftemproc, temuser, userfindcdt, userid, entid);
		if (orgs == null)
			if (getPublicControllerBase() != null)
				orgs = ((JPAController) getPublicControllerBase()).OnWfFindCDTOrgs((CJPA) this, wftemproc, temuser, userfindcdt, userid, entid);
		if (orgs == null)
			orgs = getfindcdtOrgs(wftemproc, temuser, userfindcdt, userid, entid);// 根据参与岗位配置，查找机构

		String sqlstr = "SELECT  a.*  " // a.userid,a.username,a.displayname
				+ "  FROM shwuser a, shwposition b, shwpositionuser c  "
				+ "  where a.userid = c.userid and b.positionid = c.positionid "
				+ "  and a.actived = 1 and b.isvisible = 1 and b.positionid = " + PostionID;
		CJPALineData<Shwuser> fdusers = new CJPALineData<Shwuser>(null, Shwuser.class);
		fdusers.findDataBySQL(sqlstr, true, true);
		if (fdusers.size() == 0) {
			// 所有机构都木有该岗位所对应的用户，说明流程模板设置有问题，抛出错误中断处理
			throw new Exception("节点【" + wftemproc.proctempname.getValue() + "】岗位【" + temuser.displayname.getValue() + "】未找到执行人!");
		}
		if (userfindcdt == 4)// 所有机构
			return fdusers;

		if (userfindtype == 7) {// 上溯最近
			List<Shwuser> us = new ArrayList<Shwuser>();
			CJPALineData<Shworg> os = new CJPALineData<Shworg>(Shworg.class);
			for (CJPABase ujpa : fdusers) {
				Shwuser u = (Shwuser) ujpa;
				for (CJPABase jpa : orgs) {
					Shworg org = (Shworg) jpa;
					Shworg o = getUserSuperOrgMin(u, org);// 获取 最长 IDpath 的一个机构
					if (!o.isEmpty()) {
						os.add(o);
						us.add(u);
					}
				}
			}
			CJPALineData<Shwuser> rst = new CJPALineData<Shwuser>(null, Shwuser.class);
			if (os.size() == 0) {
				if (proc.allowemptyuser.getAsIntDefault(0) == 2)// 允许空执行人
					throw new Exception("节点【" + wftemproc.proctempname.getValue() + "】岗位【" + temuser.displayname.getValue()
							+ "】【上溯最近】未找到执行人!");
			}
			CJPALineData<Shworg> morgs = getOrgIdpathMax(os);
			for (CJPABase mojpa : morgs) {
				Shworg morg = (Shworg) mojpa;
				int idx = os.indexOf(morg);
				rst.add(us.get(idx));
			}
			return rst;
		}

		java.util.Iterator<CJPABase> iter = fdusers.listIterator();
		while (iter.hasNext()) {
			Shwuser u = (Shwuser) iter.next();
			switch (userfindtype) {
			case 1:// 当前机构
				if (!isUserInCurOrgs(u, orgs)) { // 失败则取当前用户所在机构（包括默认机构）
					iter.remove();
				}
				break;
			case 2:// 机构上朔
				if (!isUserInSuperOrgs(u, orgs))
					iter.remove();
				break;
			case 3:// 机构下朔
				if (!isUserInChildOrgs(u, orgs, entid))
					iter.remove();
				break;
			case 4:// 所有机构
				break;
			case 5:// JPA控制器
				if (getPublicControllerBase() != null) {
					Shwuser utem = ((JPAController) getPublicControllerBase()).OnWFFindUserByPosition((CJPA) this, wf, proc, temuser);
					if ((utem == null) || (utem.userid.getAsInt() != u.userid.getAsInt())) {
						iter.remove();
					}
				}
				JPAControllerBase ctr = getController();
				if (ctr != null) {
					Shwuser utem = ((JPAController) ctr).OnWFFindUserByPosition((CJPA) this, wf, proc, temuser);
					if ((utem == null) || (utem.userid.getAsInt() != u.userid.getAsInt())) {
						iter.remove();
					}
				}
				break;
			case 6:// 兼管机构
				if (!isUserInFindOrgs(u, orgs))
					iter.remove();
				break;
			case 8:// 下朔最近
				throw new Exception("节点【" + wftemproc.proctempname.getValue() + "】岗位【" + temuser.displayname.getValue() + "】暂不支持【下朔最近】选项!");
			default:
			}

		} // while
		return fdusers;
	}

	private CJPALineData<Shworg> getfindcdtOrgs(Shwwftempproc wftemproc, Shwwftempprocuser temuser, int userfindcdt, String userid, String entid)
			throws Exception {
		CJPALineData<Shworg> curorgs = new CJPALineData<Shworg>(Shworg.class);
		if (userfindcdt == 1) {// 表单路径机构
			CField idpathfd = cfield("idpath");
			if (idpathfd == null)
				throw new Exception("流程模板节点岗位查找条件【userfindcdt】标识为【表单路径】,但是表单未包含【idpath】字段");
			Shworg org = new Shworg();
			org.findBySQL("select * from shworg where idpath='" + idpathfd.getValue() + "'", false);
			if (org.isEmpty())
				throw new Exception("没有找到路径为" + idpathfd.getValue() + "的机构!");
			curorgs.add(org);
		} else if (userfindcdt == 2) {// 登录用户所在机构
			curorgs = CSContext.getUserOrgs(userid, entid);
		} else if (userfindcdt == 3) {// 表单机构字段
			if (temuser.userfindorgid.isEmpty())
				throw new Exception("流程模板节点岗位查找条件【userfindcdt】标识为【表单机构字段】,但是流程节点用户【userfindorgid】字段未设置");
			CField orgfd = cfield(temuser.userfindorgid.getValue());
			if (orgfd == null)
				throw new Exception("JPA不存在字段【" + temuser.userfindorgid.getValue() + "】,该字段在【userfindorgid】中定义,出错流程节点为【" + wftemproc.proctempname.getValue()
						+ "】");
			Shworg org = new Shworg();
			org.findByID(orgfd.getValue(), false);
			if (org.isEmpty())
				throw new Exception("没有找到ID为【" + orgfd.getValue() + "】的机构!");
			curorgs.add(org);
		} else if (userfindcdt == 4) {// 所有机构
			String sqlstr = "SELECT o.* FROM shworg o,shwpositionuser pu,shworguser ou"
					+ " WHERE o.orgid=ou.orgid AND pu.userid=ou.userid AND pu.positionid=" + temuser.userid.getValue();
			curorgs.findDataBySQL(sqlstr, true, false);
		} else if (userfindcdt == 5) {// JPA控制器
			throw new Exception("流程模板节点【" + wftemproc.proctempname.getValue() + "】岗位【" + temuser.displayname.getValue() + "】暂不支持JPA控制器");
		} else
			throw new Exception("流程模板节点【" + wftemproc.proctempname.getValue() + "】岗位【" + temuser.displayname.getValue() + "】查找条件【userfindcdt】数据只允许为1,2,3,4,5");
		return curorgs;
	}

	/**
	 * @param os
	 *            机构列表
	 * @return 返回机构层次最大的所有机构
	 * @throws Exception
	 */
	private CJPALineData<Shworg> getOrgIdpathMax(CJPALineData<Shworg> os) throws Exception {//
		int rl = 0;
		CJPALineData<Shworg> morgs = new CJPALineData<Shworg>(Shworg.class);
		for (CJPABase jpa : os) {
			Shworg org = (Shworg) jpa;
			if (org.idpath.isEmpty())
				throw new Exception("机构【" + org.orgname.getValue() + "】【idpath】为空");
			int l = org.idpath.getValue().split(",").length;
			rl = (rl < l) ? l : rl;
		}
		for (CJPABase jpa : os) {
			Shworg org = (Shworg) jpa;
			int l = org.idpath.getValue().split(",").length;
			if (l == rl)
				morgs.add(org);
		}
		return morgs;
	}

	private Shworg getOrgIdpathMin(CJPALineData<Shworg> os) {//
		int rl = 99999;
		for (CJPABase jpa : os) {
			Shworg org = (Shworg) jpa;
			int l = org.idpath.getValue().split(",").length;
			rl = (rl > l) ? l : rl;
		}
		for (CJPABase jpa : os) {
			Shworg org = (Shworg) jpa;
			int l = org.idpath.getValue().split(",").length;
			if (l == rl)
				return org;
		}
		return null;
	}

	private Shworg getUserSuperOrgMin(Shwuser u, Shworg org) throws Exception {// 机构上溯用户 所在的最近机构
		String idp = org.idpath.getValue();
		if ((idp == null) || (idp.isEmpty()))
			throw new Exception("机构【" + org.orgname.getValue() + "】IDPATH为空");
		idp = idp.substring(0, idp.length() - 1);
		String sqlstr = "SELECT DISTINCT * from( SELECT o.* FROM shworg o,shworguser ou "
				+ " WHERE o.orgid=ou.orgid AND ou.`userid`=" + u.userid.getValue() + " AND o.orgid IN(" + idp + ") AND LENGTH(o.idpath)=("
				+ " SELECT MAX(LENGTH(o.idpath)) FROM shworg o,shworguser ou "
				+ " WHERE o.orgid=ou.orgid AND  ou.userid=" + u.userid.getValue() + " AND o.orgid IN(" + idp + ")"
				+ " )) tb where 1=1 ";
		Shworg o = new Shworg();
		o.findBySQL(sqlstr);
		return o;
	}

	private Shworg getUserSuperOrgMax(Shwuser u, Shworg org) throws Exception {// 机构上溯用户 所在的最远机构
		String idp = org.idpath.getValue();
		if ((idp == null) || (idp.isEmpty()))
			throw new Exception("机构【" + org.orgname.getValue() + "】IDPATH为空");
		idp = idp.substring(0, idp.length() - 1);

		String sqlstr = "SELECT DISTINCT * from(SELECT o.* FROM shworg o,shworguser ou "
				+ " WHERE o.orgid=ou.orgid AND ou.`userid`=" + u.userid.getValue() + " AND o.orgid IN(" + idp + ") AND LENGTH(o.idpath)=("
				+ " SELECT MIN(LENGTH(o.idpath)) FROM shworg o,shworguser ou "
				+ " WHERE o.orgid=ou.orgid AND  ou.userid=" + u.userid.getValue() + " AND o.orgid IN(" + idp + ")"
				+ " )) tb where 1=1 ";
		Shworg o = new Shworg();
		o.findBySQL(sqlstr);
		return o;
	}

	private boolean isUserInCurOrgs(Shwuser u, CJPALineData<Shworg> curorgs) throws Exception {
		String orgids = "";
		for (CJPABase jpaorg : curorgs) {
			Shworg org = (Shworg) jpaorg;
			orgids = orgids + org.orgid.getValue() + ",";
		}
		if (orgids.isEmpty()) {
			throw new Exception("检查用户所属机构时，机构为空!");
		}
		orgids = orgids.substring(0, orgids.length() - 1);
		orgids = "(" + orgids + ")";
		String sqlstr = "select  count(*) ct from shworguser a where a.userid=" + u.userid.getValue() + " and a.orgid in" + orgids;
		return (Integer.valueOf(curorgs.get(0).pool.openSql2List(sqlstr).get(0).get("ct")) != 0);
	}

	private boolean isIDinIDs(String id, List<String> ids) {
		if ((id == null) || id.isEmpty())
			return true;
		for (String idstr : ids) {
			if (id.equalsIgnoreCase(idstr))
				return true;
		}
		return false;
	}

	private boolean isUserInSuperOrgs(Shwuser u, CJPALineData<Shworg> curorgs) throws Exception {
		String orgidpaths = "";
		for (CJPABase jpaorg : curorgs) {
			Shworg org = (Shworg) jpaorg;
			orgidpaths = orgidpaths + org.idpath.getValue() + ",";
		}
		String[] arrids = orgidpaths.split(",");
		List<String> ids = new ArrayList<String>();
		for (String id : arrids) {
			if ((id != null) && (!id.isEmpty()) && (!isIDinIDs(id, ids)))
				ids.add(id);
		}
		String orgids = "";
		for (String id : ids) {
			orgids = orgids + id + ",";
		}
		if (orgids.isEmpty()) {
			throw new Exception("检查用户所属机构时，机构为空!");
		}
		orgids = orgids.substring(0, orgids.length() - 1);
		orgids = "(" + orgids + ")";
		String sqlstr = "select  count(*) ct from shworguser a where a.userid=" + u.userid.getValue() + " and a.orgid in" + orgids;
		return (Integer.valueOf(curorgs.get(0).pool.openSql2List(sqlstr).get(0).get("ct")) != 0);
	}

	private boolean isUserInChildOrgs(Shwuser u, CJPALineData<Shworg> curorgs, String entid) throws Exception {
		CJPALineData<Shworg> usorgs = new CJPALineData<Shworg>(Shworg.class);
		String sqlstr = "select a.* from shworg a,shworguser b where a.orgid=b.orgid and a.entid=" + entid + " and b.userid="
				+ u.userid.getValue();
		usorgs.findDataBySQL(sqlstr, true, false);
		for (CJPABase jpa : usorgs) {
			Shworg uorg = (Shworg) jpa;
			for (CJPABase cjpa : curorgs) {
				Shworg corg = (Shworg) cjpa;
				String uidpath = uorg.idpath.getValue();
				String cidpath = corg.idpath.getValue();
				if (uidpath.substring(0, cidpath.length()).equalsIgnoreCase(cidpath))
					return true;
			}
		}
		return false;
	}

	private boolean isUserInFindOrgs(Shwuser u, CJPALineData<Shworg> curorgs) throws Exception {
		CJPALineData<Shworg_find> fds = new CJPALineData<Shworg_find>(Shworg_find.class);
		String sqlstr = "SELECT f.* FROM  shworg_find f,shworguser u WHERE f.orgid=u.orgid AND u.userid=" + u.userid.getValue();
		fds.findDataBySQL(sqlstr, true, false);
		for (CJPABase jpa : fds) {
			Shworg_find ofd = (Shworg_find) jpa;
			for (CJPABase cjpa : curorgs) {
				Shworg corg = (Shworg) cjpa;
				String cidpath = ofd.fidpath.getValue();
				String uidpath = corg.idpath.getValue();
				if (uidpath.length() < cidpath.length())
					continue;
				if (uidpath.substring(0, cidpath.length()).equalsIgnoreCase(cidpath))
					return true;
			}
		}
		return false;
	}

	private void CreateProcconditionsByTemp(CJPALineData<Shwwfproccondition> cdns, CJPALineData<Shwwftempproccondition> cdntems) throws Exception {
		for (CJPABase jpa : cdntems) {
			Shwwftempproccondition cndtem = (Shwwftempproccondition) jpa;
			Shwwfproccondition cdn = new Shwwfproccondition();
			cdn.assignfield(cndtem, true);
			cdns.add(cdn);
		}
	}

	private boolean CreateLinkCdtn(CJPALineData<Shwwflinklinecondition> cdtns, CJPALineData<Shwwftemplinklinecondition> cdtntems) throws Exception {
		for (CJPABase jpact : cdtntems) {
			Shwwftemplinklinecondition ct = (Shwwftemplinklinecondition) jpact;
			Shwwflinklinecondition c = new Shwwflinklinecondition();
			c.assignfield(ct, true);
			cdtns.add(c);
		}
		return true;
	}

	private boolean CreateWFLinksByTemp(CJPALineData<Shwwflinkline> links, CJPALineData<Shwwftemplinkline> linktems) throws Exception {
		for (CJPABase jpalt : linktems) {
			Shwwftemplinkline linktem = (Shwwftemplinkline) jpalt;
			Shwwflinkline link = new Shwwflinkline();
			link.assignfield(linktem, true);
			// System.out.println(linktem.fromproctempid.getValue() +
			// " CreateWFLinksByTemp " + linktem.toproctempid.getValue());
			link.fromprocid.setValue(linktem.fromproctempid.getValue());// ///后期生成流程实例关系时候会需要用到
			link.toprocid.setValue(linktem.toproctempid.getValue());
			if (!(CreateLinkCdtn(link.shwwflinklineconditions, linktem.shwwftemplinklineconditions)))
				return false;
			links.add(link);
		}
		return true;
	}

	// create wftemp end
	/*
	 * 提交工作流节点
	 */

	public CJPA wfsubmit(String procid, String aoption, String[] ckuserids) throws Exception {
		return wfsubmit(procid, aoption, CSContext.getUserID(), CSContext.getCurEntID(), ckuserids, false);
	}

	/**
	 * 提交工作流节点
	 * 
	 * @param procid
	 * @param aoption
	 * @param userid
	 * @param entid
	 * @param ckuserids
	 * @param isAuto
	 * @return
	 * @throws Exception
	 */
	public CJPA wfsubmit(String procid, String aoption, String userid, String entid, String[] ckuserids, boolean isAuto) throws Exception {
		CDBConnection con = pool.getCon(this);
		con.startTrans();// 开始事务
		try {
			wfsubmit(con, procid, aoption, userid, entid, ckuserids, false);
			String npass = ConstsSw.getSysParm("WFNextProcAutoSubmit");
			int npas = (npass == null) ? 2 : Integer.valueOf(npass);
			if ((npas == 1) && (!isAuto)) {
				doAutoSubmitNextProc(con, procid, aoption, userid, entid);
			}
			con.submit();
			return (CJPA) this;
		} catch (Exception e) {
			con.rollback();
			Logsw.error("提交流程错误:", e);
			throw e;
		} finally {
			con.close();
		}

	}

	/**
	 * 同一个人相邻节点自动审批
	 * 
	 * @param procid
	 * @param aoption
	 * @param userid
	 * @param entid
	 * @return
	 * @throws Exception
	 */
	public CJPA doAutoSubmitNextProc(CDBConnection con, String procid, String aoption, String userid, String entid) throws Exception {
		Shwwfproc cproc = (Shwwfproc) (new Shwwfproc()).findByID(procid, false);
		Shwwf wf = new Shwwf();
		wf.findByID(con, cproc.wfid.getValue(), false);
		if (wf.stat.getAsIntDefault(1) == WFStat.finished)// 流程已经完成了
			return (CJPA) this;
		Shwwfproc nproc = new Shwwfproc();// 下一个活动节点
		String sqlstr = "SELECT * FROM shwwfproc WHERE wfid=" + wf.wfid.getValue() + " AND stat=2 AND isbegin=2 AND isend=2";
		nproc.findBySQL(con, sqlstr, false);
		if (nproc.isEmpty())// 没有下个节点
			return (CJPA) this;
		sqlstr = "SELECT IFNULL(COUNT(*),0) ct  FROM shwwfprocuser WHERE procid=" + nproc.procid.getValue() + " AND stat<>2 AND userid=" + userid;
		if (Integer.valueOf(con.openSql2List(sqlstr).get(0).get("ct")) > 0) {
			return wfsubmit(con, nproc.procid.getValue(), aoption, userid, entid, null, true);
		} else
			// 下个节点没有指定用户
			return (CJPA) this;
	}

	/**
	 * 提交工作流节点
	 * 
	 * @param con
	 * @param procid
	 *            提交的节点ID ；用外部参数为了兼容将来并行流程 可能 会有多个“当前节点”
	 * @param aoption
	 *            意见
	 * @param userid
	 *            提交的用户
	 * @param entid
	 *            提交的组织
	 * @param ckuserids
	 *            选定的审批用户
	 * @param isAuto
	 *            是否自动审批（自动审批的，不要再次调用自动处理）
	 * @return
	 * @throws Exception
	 */
	private CJPA wfsubmit(CDBConnection con, String procid, String aoption, String userid, String entid, String[] ckuserids, boolean isAuto) throws Exception {
		findByID4Update(con); // 锁定业务单据
		CField StatField = cfield("stat");
		if (StatField == null)
			throw new Exception("对象没有找到stat字段，不允许提交流程!");
		if (StatField.getAsIntDefault(0) != 2) {
			System.out.println("jpa:" + this.tojson());
			throw new Exception("创建流程后，才允许提交流程!");
		}
		if (getJpaStat() != CJPAStat.RSLOAD4DB)
			throw new Exception("未保存的对象，不允许提交流程!");
		CField WfidField = cfield("wfid");
		if ((WfidField == null) || WfidField.isEmpty())
			throw new Exception("对象没有找到wfid字段，不允许提交流程!");

		Shwwf wf = new Shwwf();
		wf.shwwflinklines.setSqlOrderBystr(" idx asc ");
		wf.findByID4Update(con, WfidField.getValue(), true);
		// Shwwfproc proc = getProcByID(wf, procid);
		// if (proc == null)
		// throw new Exception("根据节点ID【" + procid + "】未能找到节点!");

		Shwwfproc proc = getActiveProc(wf);

		Shwuser cuser = new Shwuser();
		cuser.findByID(userid);
		Shwwfprocuser puser = checkProc(wf, proc, userid);
		if ((puser == null) && (proc.proctype.getAsIntDefault(0) != 4) && (proc.isbegin.getAsIntDefault(0) != 1))// 不是自动节点/开始节点
			throw new Exception("当前用户未参与节点<" + proc.procname.getValue() + ">审批");

		// EOParms eoparms = new EOParms();
		// eoparms.wf = wf;
		// eoparms.dbcon = con;
		// eoparms.proc = proc;
		// eoparms.jpa = (CJPA) this;

		Shwwftempproc wftemproc = new Shwwftempproc();
		wftemproc.findByID(proc.proctempid.getValue());
		// if (!wftemproc.isEmpty())
		// dealProcExtOptions(wftemproc, EOAction._submit, EOStage._before, eoparms);

		// 检查当前节点条件是否满足提交
		checkProcConditions(proc);
		if (puser != null) {
			puser.opinion.setValue(aoption);
			puser.stat.setAsInt(ProcUserStat.finished);// 用户审批完成2
			puser.audittime.setAsDatetime(new Date());
		}
		Shwwfproc nxtproc = null;
		if (CheckProcFinished(proc)) {
			proc.stat.setAsInt(ProcStat.finished);// 节点完成3
			nxtproc = setNextsActive(wf, proc);// 设置后续节点 同时检查分支条件
		}

		// if (proc.proctype.getAsIntDefault(0) == 4) {
		// System.out.println("1`11111111111111111111111111" + nxtproc.procname.getValue());
		// }

		boolean isFilishedWF = (wf.stat.getAsIntDefault(0) == WFStat.finished);
		if (isFilishedWF) {
			StatField.setAsInt(9);
			save(con);
		}
		// wf.save(con);//moyh 17-04-11
		if (proc.stat.getAsIntDefault(0) == ProcStat.finished) {// 节点完成
			proc.livetime.setAsDatetime(new Date());// 记录当前节点离开时间
			nxtproc.arivetime.setAsDatetime(new Date());// 记录下个节点到达时间
			setProcUserPressTime(nxtproc);// 设置下个节点所有参与人 催办次数和时间
			if (getPublicControllerBase() != null) {
				((JPAController) getPublicControllerBase()).OnArriveProc((CJPA) this, wf, nxtproc, con, ArraiveType.atSubmit);
				((JPAController) getPublicControllerBase()).OnLiveProc((CJPA) this, wf, proc, con, ArraiveType.atSubmit);
				((JPAController) getPublicControllerBase()).OnWfSubmit((CJPA) this, con, wf, proc, nxtproc, isFilishedWF);
			}
			JPAControllerBase ctr = getController();
			if (ctr != null) {
				((JPAController) ctr).OnArriveProc((CJPA) this, wf, nxtproc, con, ArraiveType.atSubmit);
				((JPAController) ctr).OnLiveProc((CJPA) this, wf, proc, con, ArraiveType.atSubmit);
				((JPAController) ctr).OnWfSubmit((CJPA) this, con, wf, proc, nxtproc, isFilishedWF);
			}
		}

		if (getPublicControllerBase() != null) {
			((JPAController) getPublicControllerBase()).OnWfUserSubmit((CJPA) this, con, wf, proc, puser, nxtproc, isFilishedWF);
		}
		JPAControllerBase ctr = getController();
		if (ctr != null) {
			((JPAController) ctr).OnWfUserSubmit((CJPA) this, con, wf, proc, puser, nxtproc, isFilishedWF);
		}

		// if (!wftemproc.isEmpty())// 处理自定义参数
		// dealProcExtOptions(wftemproc, EOAction._submit, EOStage._after, eoparms);
		if (!proc.submitfunc.isEmpty()) {
			// String submf = proc.submitfunc.getValue();
			// System.out.println(submf);
			// proc.submitfunc.getValue().调用定义的函数
		}

		if (proc.isbegin.getAsIntDefault(0) == 1) {// 首节点
			newwflog(con, wf, proc, 6, cuser.userid.getValue(), cuser.displayname.getValue(), aoption);
		} else if (proc.proctype.getAsIntDefault(0) == 4) {// 自动节点
			newwflog(con, wf, proc, 1, "0", "SYSTEM", "自动节点");
		} else {
			newwflog(con, wf, proc, 1, puser.userid.getValue(), puser.displayname.getValue(), puser.opinion.getValue());// 插入提交日志
		}

		if (isFilishedWF)
			newwflogover(con, wf);
		wf.save(con);
		save(con);
		if (proc.stat.getAsIntDefault(0) == ProcStat.finished) {// 节点完成
			// System.out.println("【" + proc.procname.getValue() + "】节点完成,到 【" + nxtproc.procname.getValue() + "】");
			if ((nxtproc != null) && (!isFilishedWF)) {// 下个节点不为空，且流程未完成
				if (nxtproc.proctype.getAsIntDefault(0) == 4) {// 自动
					wfsubmit(con, nxtproc.procid.getValue(), "自动执行", userid, entid, ckuserids, false);
				} else {
					if (nxtproc.selectuser.getAsIntDefault(0) == 1) {
						if (((ckuserids == null) || (ckuserids.length == 0)) && (nxtproc.shwwfprocusers.size() != 1)) {
							// 如何将选择的用户返回给前端？？？？？
							CWFException wfe = new CWFException(CWFException.ERR_NEED_SELECT_USER, proc.procid.getValue(), "需要选择参与用户");
							wfe.setUserobj(nxtproc);
							throw wfe;
						} else { // } else if (nxtproc.shwwfprocusers.size() > 1) {
							if ((ckuserids != null) && (ckuserids.length > 0)) {
								setSelectedUser2ProcNotCheck(nxtproc, ckuserids);
								// System.out.println("wf1:"+wf.tojson());
								wf.save(con);
								// System.out.println("wf2:"+wf.tojson());
							}
							if (((ckuserids == null) || (ckuserids.length == 0)) && (nxtproc.shwwfprocusers.size() == 0)) {
								throw new Exception("下个节点【" + nxtproc.procname.getValue() + "】无审批人");
							}
						}
					} else {
						if (nxtproc.shwwfprocusers.size() == 0)
							throw new Exception("节点【" + nxtproc.procname.getValue() + "】无审批人");
					}
				}
			}
			CWFNotify.procFlishNotify(this, proc);
			// System.out.println("发送邮件 proc :" + nxtproc.toJPAInfoJson());
			// System.out.println("发送邮件 wf:" + wf.toJPAInfoJson());
			CWFNotify.procArivedNotify(this, wf, nxtproc, userid);
		}
		if (puser != null) {

		}
		if (isFilishedWF) {
			CWFNotify.wfFlishNotify(this, wf, userid);
		}
		// //检查是否当前节点是首节点，报错 待实现
		return (CJPA) this;
	}

	/**
	 * 设置流程参与审批的人
	 * 检测源流程节点里面没有直接添加
	 * 
	 * @param nxtproc
	 * @param ckuserids
	 * @throws Exception
	 */
	private void setSelectedUser2ProcNotCheck(Shwwfproc proc, String[] ckuserids) throws Exception {
		CJPALineData<Shwwfprocuser> users = proc.shwwfprocusers;
		Shwuser user = new Shwuser();
		for (String userid : ckuserids) {
			if (!isUseridInProcUsers(userid, users)) {
				user.clear();
				user.findByID(userid, false);
				if (user.isEmpty())
					throw new Exception("ID为【" + userid + "】的用户不存在");
				Shwwfprocuser puser = new Shwwfprocuser();
				puser.userid.setValue(user.userid.getValue());
				puser.displayname.setValue(user.displayname.getValue());
				puser.isposition.setAsInt(2);
				puser.stat.setAsInt(ProcUserStat.waitfor);
				puser.wfid.setValue(proc.wfid.getValue());
				puser.procid.setValue(proc.procid.getValue());
				puser.jointype.setValue("2");
				puser.recnotify.setAsInt(1);
				puser.recpress.setAsInt(1);
				puser.pressnum.setAsInt(0);
				users.add(puser);
			}
		}

		Iterator<CJPABase> it1 = users.iterator();
		while (it1.hasNext()) {
			Shwwfprocuser puser = (Shwwfprocuser) it1.next();
			if (!isexistsidinids(ckuserids, puser.userid.getValue())) {
				puser.setJpaStat(CJPAStat.RSDEL);// 标记为删除
			}
		}

		if (users.size() == 0)
			throw new Exception("选择流程审批人在节点【" + proc.procname.getValue() + "】不存在");
		proc.shwwfprocusers = users;
		if (proc.shwwfprocusers.size() == 0)
			throw new Exception("设置了用户怎么搞的还是空？？？");
	}

	private boolean isUseridInProcUsers(String userid, CJPALineData<Shwwfprocuser> users) {
		if (userid == null)
			return true;
		for (CJPABase jpa : users) {
			Shwwfprocuser user = (Shwwfprocuser) jpa;
			if (userid.equalsIgnoreCase(user.userid.getValue()))
				return true;
		}
		return false;
	}

	/**
	 * 设置流程参与审批的人
	 * 选择的人必须在原流程节点里面有的
	 * 
	 * @param nxtproc
	 * @param ckuserids
	 * @throws Exception
	 */
	private void setSelectedUser2Proc(Shwwfproc nxtproc, String[] ckuserids) throws Exception {
		int chek = ConstsSw.getSysParmIntDefault("WFProcSelectedUserCheck", 1);
		CJPALineData<Shwwfprocuser> users = nxtproc.shwwfprocusers;
		Iterator<CJPABase> it1 = users.iterator();
		while (it1.hasNext()) {
			Shwwfprocuser user = (Shwwfprocuser) it1.next();
			if (!isexistsidinids(ckuserids, user.userid.getValue())) {
				user.setJpaStat(CJPAStat.RSDEL);// 标记为删除
				// it1.remove();
			}
		}
		if (users.size() == 0)
			throw new Exception("选择流程审批人在节点【" + nxtproc.procname.getValue() + "】不存在");
		nxtproc.shwwfprocusers = users;
	}

	private boolean isexistsidinids(String[] ckuserids, String userid) {
		for (String id : ckuserids) {
			if (id.equalsIgnoreCase(userid))
				return true;
		}
		return false;
	}

	private void setProcUserPressTime(Shwwfproc proc) {
		int wpc = ConstsSw.getSysParmIntDefault("WFPressCycle", 0);
		if (wpc == 0)
			return;
		Date nextpdate = Systemdate.dateDayAdd(new Date(), wpc);
		for (CJPABase jpa : proc.shwwfprocusers) {
			Shwwfprocuser pu = (Shwwfprocuser) jpa;
			pu.pressnum.setAsInt(0);
			pu.nextpresstime.setAsDatetime(nextpdate);
		}
	}

	// class SortedShwwflinkline extends Shwwflinkline implements Comparable<Shwwflinkline> {
	//
	// public SortedShwwflinkline() throws Exception {
	// super();
	// // TODO Auto-generated constructor stub
	// }
	//
	// @Override
	// public int compareTo(Shwwflinkline o) {
	// // TODO Auto-generated method stub
	// return (this.idx.getAsFloat() > o.idx.getAsFloat()) ? 1 : -1;
	// }
	//
	// }

	/**
	 * @param wf
	 * @param proc当前节点
	 * @return 判断所有分支，寻找下一个节点，如果下个节点是尾巴，流程完成；
	 * @throws Exception
	 */
	private Shwwfproc setNextsActive(Shwwf wf, Shwwfproc proc) throws Exception {
		// List<SortedShwwflinkline> slines = new ArrayList<SortedShwwflinkline>();
		// CJPALineData<Shwwflinkline> ylines = new CJPALineData<Shwwflinkline>(Shwwflinkline.class);
		// for (CJPABase jpaline : wf.shwwflinklines) {
		// Shwwflinkline l = (Shwwflinkline) jpaline;
		// if (l.fromprocid.getValue().equalsIgnoreCase(proc.procid.getValue())) {
		// SortedShwwflinkline sl = new SortedShwwflinkline();
		// sl.assignfield(l, true);
		// slines.add(sl);
		// ylines.add(l);
		// }
		// }
		// Collections.sort(slines);// lines 排序
		// CJPALineData<Shwwflinkline> lines = new CJPALineData<Shwwflinkline>(Shwwflinkline.class);
		// for (SortedShwwflinkline sl : slines) {
		// for (CJPABase jpa : ylines) {
		// Shwwflinkline l = (Shwwflinkline) jpa;
		// if (sl.wfllid.getAsInt() == l.wfllid.getAsInt()) {
		// lines.add(l);
		// break;
		// }
		// }
		// }
		// 采用数据库排序，去掉排序算法
		CJPALineData<Shwwflinkline> lines = new CJPALineData<Shwwflinkline>(Shwwflinkline.class);
		for (CJPABase jpaline : wf.shwwflinklines) {
			Shwwflinkline l = (Shwwflinkline) jpaline;
			if (l.fromprocid.getValue().equalsIgnoreCase(proc.procid.getValue())) {
				lines.add(l);
			}
		}

		String err = null;
		Shwwflinkline line = checkLinkLineConditions(lines, err);
		// if (lines.size() == 0)
		// throw new Exception("没有找到后续节点无法提交:" + err);
		// if (lines.size() > 1)
		// throw new Exception("找到多个后续节点，请检查流程跳转条件设置是否合理?");
		// Shwwflinkline line = (Shwwflinkline) lines.get(0);
		System.out.println("2222222222222222222222222111:" + line.tojson());
		if ((line == null) || (line.isEmpty()))
			throw new Exception("没有找到后续节点无法提交:" + err);
		for (CJPABase jpaprc : wf.shwwfprocs) {
			Shwwfproc nxtproc = (Shwwfproc) jpaprc;
			if (line.toprocid.getValue().equalsIgnoreCase(nxtproc.procid.getValue())) {
				if ("1".equalsIgnoreCase(nxtproc.isend.getValue())) {
					wf.stat.setAsInt(WFStat.finished);// 流程完成
					for (CJPABase jpap : wf.shwwfprocs) {
						((Shwwfproc) jpap).stat.setAsInt(ProcStat.finished);// 每个节点完成
					}
				} else {
					proc.nextprocs.setValue(nxtproc.procid.getValue());
					nxtproc.stat.setAsInt(ProcStat.focued);// 后续节点设置为当前节点2
				}
				return nxtproc;
			}
		}
		throw new Exception("没有找到后续节点无法提交!");
	}

	private Shwwfproccondition getNextCondition(Shwwfproccondition pd, CJPALineData<Shwwfproccondition> pds) {
		for (CJPABase jpa : pds) {
			Shwwfproccondition pcd = (Shwwfproccondition) jpa;
			if ((pcd != pd) && (pcd.ppdidx.getAsIntDefault(0) != 0) && (pcd.ppdidx.getAsIntDefault(0) == pd.pdidx.getAsIntDefault(0))
					&& (pcd.parmname.getValue() != null) && (!pcd.parmname.getValue().isEmpty()))
				return pcd;
		}
		return null;
	}

	private void paiseConditions(CJPALineData<Shwwfproccondition> pds, List<Shwwfproccondition> ptpds,
			List<List<Shwwfproccondition>> linkpds) throws Exception {
		// System.out.println("paiseConditions pds:" + pds.size());
		for (CJPABase jpa : pds) {
			Shwwfproccondition pcd = (Shwwfproccondition) jpa;
			String fdname = pcd.parmname.getValue();
			if ((fdname == null) || fdname.isEmpty())
				continue;
			Shwwfproccondition nextPcd = getNextCondition(pcd, pds);
			// System.out.println(pcd.tojson() + " nexppcd:" + nextPcd);
			if ((pcd.ppdidx.getAsIntDefault(0) == 0) && (nextPcd == null)) {// 普通节点； 无前 无后
				ptpds.add(pcd);
			}
			if ((nextPcd != null) && (pcd.ppdidx.getAsIntDefault(0) == 0)) {// 首节点：有后无前
				List<Shwwfproccondition> linkpd = new ArrayList<Shwwfproccondition>();
				linkpd.add(pcd);// 每个链表首节点
				linkpds.add(linkpd);
			}
		}
		for (List<Shwwfproccondition> linkpd : linkpds) {
			if (linkpd.size() != 1)
				continue;
			Shwwfproccondition fpd = linkpd.get(0);
			while (true) {
				Shwwfproccondition npd = getNextCondition(fpd, pds);
				if (npd == null)
					break;
				linkpd.add(npd);
				fpd = npd;
			}
		}
	}

	private void checkProcConditions(Shwwfproc proc) throws Exception {
		List<Shwwfproccondition> ptpds = new ArrayList<Shwwfproccondition>();
		List<List<Shwwfproccondition>> linkpds = new ArrayList<List<Shwwfproccondition>>();
		paiseConditions(proc.shwwfprocconditions, ptpds, linkpds);

		// 检查普通条件
		for (CJPABase jpa : ptpds) {
			Shwwfproccondition pcd = (Shwwfproccondition) jpa;
			String fdname = pcd.parmname.getValue();
			CField cfd = cfield(fdname);
			if (cfd == null)
				throw new Exception("<" + fdname + ">字段在DPA中未发现!");
			if (!CorUtil.isStrMath(cfd.getValue(), pcd.reloper.getValue(), pcd.parmvalue.getValue())) {
				throw new Exception("未满足条件:" + cfd.getCaption() + " " + cfd.getValue() + pcd.reloper.getValue() + pcd.parmvalue.getValue());
			}
		}
		// 检查链表条件
		for (List<Shwwfproccondition> linkpd : linkpds) {
			for (int i = 0; i <= linkpd.size() - 1; i++) {
				Shwwfproccondition pcd = linkpd.get(i);
				String fdname = pcd.parmname.getValue();
				CField cfd = cfield(fdname);
				if (cfd == null)
					throw new Exception("<" + fdname + ">字段在DPA中未发现!");
				boolean isok = CorUtil.isStrMath(cfd.getValue(), pcd.reloper.getValue(), pcd.parmvalue.getValue());
				if (isok) {
					if (i < (linkpd.size() - 1)) {// 不是尾 ，继续检查
						continue;
					} else {
						break;// 是尾，说明通过链表条件检查
					}
				} else {
					if (i < (linkpd.size() - 1)) {
						break;// 不是尾，条件不满足 不执行后置条件 ；链表检查通过
					} else {// 是尾，条件不满足；链表条件检查失败 终止提交
						throw new Exception("未满足条件:" + cfd.getCaption() + " " + cfd.getValue() + pcd.reloper.getValue() + pcd.parmvalue.getValue());
					}
				}
			}
		}

	}

	// 修改策略：按优先级，返回满足条件的第一条分支；最后一条不验证直接返回
	private Shwwflinkline checkLinkLineConditions(CJPALineData<Shwwflinkline> lines, String err) throws Exception {
		for (int i = 0; i < lines.size(); i++) {
			Shwwflinkline line = (Shwwflinkline) lines.get(i);
			System.out.println("分支数量：" + lines.size() + " 11111111111111####检查条件wfllid：" + line.wfllid.getValue());
			if (i == (lines.size() - 1)) {// 最后一条
				return line;
			}
			boolean ismz = true;
			for (CJPABase jpalc : line.shwwflinklineconditions) {
				Shwwflinklinecondition lc = (Shwwflinklinecondition) jpalc;
				String fdname = lc.parmname.getValue();// DPAEDT_Tdrp_item_price_cust_cust_id
				if ((fdname == null) || fdname.isEmpty())
					continue;
				if (fdname.startsWith("DPAEDT_")) {
					fdname = fdname.substring(9 + getClass().getSimpleName().length(), fdname.length());
					// ustfordelphi
				}
				CField cfd = cfield(fdname);
				if (cfd == null)
					throw new Exception("<" + fdname + ">字段在DPA中未发现!");
				// System.out.println(lc.frmcaption.getValue() +
				// ";cfd.getValue():" + cfd.getValue() + ";" +
				// lc.reloper.getValue() + lc.parmvalue.getValue());
				if (!CorUtil.isStrMath(cfd.getValue(), lc.reloper.getValue(), lc.parmvalue.getValue())) {
					err = err + "未满足条件:" + cfd.getValue() + lc.reloper.getValue() + lc.parmvalue.getValue();
					Logsw.debug(err);
					ismz = false;
					break;
				}
			}
			if (ismz)
				return line;
		}
		return null;
		// Iterator<CJPABase> linesit = lines.iterator();
		// while (linesit.hasNext()) {
		// CJPABase jpaline = linesit.next();
		// Shwwflinkline line = (Shwwflinkline) jpaline;
		// boolean ismz = true;
		// for (CJPABase jpalc : line.shwwflinklineconditions) {
		// Shwwflinklinecondition lc = (Shwwflinklinecondition) jpalc;
		// String fdname = lc.parmname.getValue();// DPAEDT_Tdrp_item_price_cust_cust_id
		// if ((fdname == null) || fdname.isEmpty())
		// continue;
		// if (fdname.startsWith("DPAEDT_")) {
		// fdname = fdname.substring(9 + getClass().getSimpleName().length(), fdname.length());
		// // ustfordelphi
		// }
		// CField cfd = cfield(fdname);
		// if (cfd == null)
		// throw new Exception("<" + fdname + ">字段在DPA中未发现!");
		// // System.out.println(lc.frmcaption.getValue() +
		// // ";cfd.getValue():" + cfd.getValue() + ";" +
		// // lc.reloper.getValue() + lc.parmvalue.getValue());
		// if (!CorUtil.isStrMath(cfd.getValue(), lc.reloper.getValue(), lc.parmvalue.getValue())) {
		// err = err + "未满足条件:" + cfd.getValue() + lc.reloper.getValue() + lc.parmvalue.getValue();
		// Logsw.debug(err);
		// ismz = false;
		// break;
		// }
		// }
		// if (!ismz) {
		// err = err + "分支:<" + line.lltitle.getValue() + ">未能满足条件:" + err;
		// linesit.remove();
		// }
		// }
	}

	private boolean CheckProcFinished(Shwwfproc proc) {
		boolean FD_JCZC = false;
		for (CJPABase jpapu : proc.shwwfprocusers) {
			Shwwfprocuser pu = (Shwwfprocuser) jpapu;
			if (pu.jointype.getAsIntDefault(0) == 1) {
				if (pu.stat.getAsIntDefault(0) == 1) {// 有一个决策者未完成则认为不可以完成
					return false;
				}
				FD_JCZC = true;// 有决策者
			}
		}
		if (FD_JCZC)// 有决策者，说明 所有决策者已经审批通过
			return true;
		int minperson = proc.minperson.getAsIntDefault(0);
		// System.out.println("minperson:" + minperson);
		int cp = 0;
		for (CJPABase jpapu : proc.shwwfprocusers) {
			Shwwfprocuser pu = (Shwwfprocuser) jpapu;
			// System.out.println("pu.stat:" + pu.stat.getAsIntDefault(0) +
			// " displayname:" + pu.displayname.getValue());
			if (pu.stat.getAsIntDefault(0) == 2) {
				cp++;
				// System.out.println("cp:" + cp);
				if ((minperson != 0) && (cp >= minperson)) {// 设置了最小人数 且超过最少人数
					return true;
				}
			} else {
				if (minperson == 0)// 没有设置最小通过人数 只要有没审批通过的人 判断节点没完成
					return false;
			}
		}
		return true;
	}

	private Shwwfproc getProcByID(Shwwf wf, String procid) {
		for (CJPABase jpaproc : wf.shwwfprocs) {
			Shwwfproc proc = (Shwwfproc) jpaproc;
			if (procid.equalsIgnoreCase(proc.procid.getValue()))
				return proc;
		}
		return null;
	}

	private Shwwfproc getActiveProc(Shwwf wf) {
		for (CJPABase jpaproc : wf.shwwfprocs) {
			Shwwfproc proc = (Shwwfproc) jpaproc;
			if (proc.stat.getAsIntDefault(0) == ProcStat.focued) {
				return proc;
			}
		}
		return null;
	}

	private void newwflogover(CDBConnection con, Shwwf wf) throws Exception {
		Shwwfproclog log = new Shwwfproclog();
		log.objectid.setValue(this.getid());
		log.objectname.setValue(this.getClass().getSimpleName());
		log.objectdisname.setValue(wf.subject.getValue());
		log.wfid.setValue(wf.wfid.getValue());
		log.wfname.setValue(wf.wfname.getValue());
		log.procid.setAsInt(0);
		log.userid.setValue("-1");
		log.displayname.setValue("SYSTEM");
		log.opinion.setValue("自动");
		log.actiontype.setAsInt(5);
		log.actiontime.setAsDatetime(new Date());
		log.save(con);
		// wf.shwwfproclogs.add(log);
	}

	/**
	 * @param wf
	 * @param proc
	 * @param puser
	 * @param acttype
	 *            提交 驳回 中断 转办 完成 起草
	 * @throws Exception
	 */
	private void newwflog(CDBConnection con, Shwwf wf, Shwwfproc proc, int acttype, String userid, String displayname, String option) throws Exception {
		Shwwfproclog log = new Shwwfproclog();
		log.objectid.setValue(this.getid());
		log.objectname.setValue(this.getClass().getSimpleName());
		log.objectdisname.setValue(wf.subject.getValue());
		log.wfid.setValue(wf.wfid.getValue());
		log.wfname.setValue(wf.wfname.getValue());
		log.procid.setValue(proc.procid.getValue());
		log.arivetime.setValue(proc.arivetime.getValue());
		log.userid.setValue(userid);
		log.displayname.setValue(displayname);
		log.opinion.setValue(option);
		log.actiontype.setAsInt(acttype);
		log.actiontime.setAsDatetime(new Date());
		log.save(con);
		// wf.shwwfproclogs.add(log);
	}

	//

	/**
	 * 驳回到节点procid
	 * 
	 * @param fprocid
	 * @param tprocid
	 * @param aoption
	 * @return
	 * @throws Exception
	 */
	public CJPA wfreject(String fprocid, String tprocid, String aoption) throws Exception {
		return wfreject(fprocid, tprocid, aoption, CSContext.getUserID(), CSContext.getCurEntID());
	}

	/**
	 * 驳回到节点procid
	 * 
	 * @param fprocid
	 * @param tprocid
	 * @param aoption
	 * @param userid
	 * @param entid
	 * @return
	 * @throws Exception
	 */
	public CJPA wfreject(String fprocid, String tprocid, String aoption, String userid, String entid) throws Exception {
		Shwuser cswuser = new Shwuser();
		cswuser.findByID(userid);
		CDBConnection con = pool.getCon(this);
		con.startTrans();// 开始事务
		try {
			findByID4Update(con); // 锁定业务单据
			CField StatField = cfield("stat");
			if (StatField == null)
				throw new Exception("对象没有找到stat字段，不允许驳回流程!");
			if (StatField.getAsIntDefault(0) != 2)
				throw new Exception("创建流程后，才允许驳回流程!");
			if (getJpaStat() != CJPAStat.RSLOAD4DB)
				throw new Exception("未保存的对象，不允许驳回流程!");
			CField WfidField = cfield("wfid");
			if ((WfidField == null) || WfidField.isEmpty())
				throw new Exception("对象没有找到wfid字段，不允许驳回流程!");

			Shwwf wf = new Shwwf();
			wf.findByID4Update(con, WfidField.getValue(), true);
			Shwwfproc fproc = getProcByID(wf, fprocid);
			if (fproc == null)
				throw new Exception("根据节点ID【" + fprocid + "】未能找到节点!");
			Shwwfprocuser puser = checkProc(wf, fproc, userid);
			if (puser == null)
				throw new Exception("当前用户未参与节点<" + fproc.procname.getValue() + ">审批");
			Shwwfproc tproc = getProcByID(wf, tprocid);
			if ("1".equals(fproc.isbegin.getValue()))
				throw new Exception("开始节点<" + fproc.procname.getValue() + ">，不允许驳回");
			if ("1".equals(tproc.isbegin.getValue()))
				throw new Exception("不允许驳回到开始节点!");
			if (fproc.canreject.getAsIntDefault(0) == 2)
				throw new Exception("节点不允许驳回!");

			Shwwfproc proctem = fproc;
			while (!(proctem.procid.getValue().equals(tproc.procid.getValue()))) {
				proctem.stat.setAsInt(ProcStat.waitfor);// 设置节点为待审批状态
				SetProcUsersStat(proctem, ProcUserStat.waitfor);// 设置节点所有参与用户为待审批状态
				Shwwfproc proctem1 = getPreProc(wf, proctem);
				if (proctem1 == null)
					throw new Exception("<" + proctem.procname.getValue() + ">回溯节点为空!");
				proctem = proctem1;
			}
			fproc.livetime.setAsDatetime(new Date());// 记录驳回节点时间
			tproc.arivetime.setAsDatetime(new Date());// 记录下个节点到达时间
			tproc.stat.setAsInt(ProcStat.focued);// 设置节点为活动状态
			SetProcUsersStat(tproc, ProcUserStat.waitfor);

			Shwwfprocuser cuser = GetActiveUser(fproc, userid);
			if (cuser == null)
				throw new Exception("<" + fproc.procname.getValue() + ">未找到活动用户!");
			cuser.opinion.setValue(aoption);
			cuser.stat.setAsInt(ProcUserStat.refused); // .setValue("3");
			cuser.audittime.setAsDatetime(new Date());
			newwflog(con, wf, fproc, 2, puser.userid.getValue(), puser.displayname.getValue(), puser.opinion.getValue());// 插入驳回日志
			setProcUserPressTime(tproc);// 设置驳回到节点所有参与人 催办次数和时间
			wf.save(con);
			if (getPublicControllerBase() != null) {
				((JPAController) getPublicControllerBase()).OnWfReject((CJPA) this, con, wf, fproc, tproc);
				((JPAController) getPublicControllerBase()).OnArriveProc((CJPA) this, wf, tproc, con, ArraiveType.atSubmit);
				((JPAController) getPublicControllerBase()).OnLiveProc((CJPA) this, wf, fproc, con, ArraiveType.atSubmit);
			}
			JPAControllerBase ctr = getController();
			if (ctr != null) {
				((JPAController) ctr).OnWfReject((CJPA) this, con, wf, fproc, tproc);
				((JPAController) ctr).OnArriveProc((CJPA) this, wf, tproc, con, ArraiveType.atSubmit);
				((JPAController) ctr).OnLiveProc((CJPA) this, wf, fproc, con, ArraiveType.atSubmit);
			}
			con.submit();
			CWFNotify.procArivedNotify(this, wf, tproc, userid);// 不能抛出错误
			CWFNotify.procRefuseNotify(this, wf, fproc, tproc, userid);// 不能抛出错误
			return (CJPA) this;
		} catch (Exception e) {
			con.rollback();
			Logsw.error("驳回流程错误:", e);
			throw e;
		} finally {
			con.close();
		}
	}

	private Shwwfprocuser GetActiveUser(Shwwfproc proc, String curuserid) throws Exception {
		for (CJPABase jpapu : proc.shwwfprocusers) {
			Shwwfprocuser pu = (Shwwfprocuser) jpapu;
			if ((pu.userid.getValue().equalsIgnoreCase(curuserid)) && ((pu.stat.getAsIntDefault(0) == 1) || (pu.stat.getAsIntDefault(0) == 3))
					&& (pu.jointype.getAsIntDefault(0) < 3))
				return pu;
		}
		return null;
	}

	private Shwwfproc getPreProc(Shwwf wf, Shwwfproc proc) {
		for (CJPABase jwf : wf.shwwfprocs) {
			Shwwfproc preproc = (Shwwfproc) jwf;
			if (proc.procid.getValue().equals(preproc.nextprocs.getValue())) {
				return preproc;
			}
		}
		return null;
	}

	private void SetProcUsersStat(Shwwfproc proc, int value) {
		for (CJPABase ju : proc.shwwfprocusers) {
			Shwwfprocuser u = (Shwwfprocuser) ju;
			u.stat.setAsInt(value);
		}
	}

	//
	/**
	 * 中断流程
	 * 
	 * @return
	 * @throws Exception
	 */
	public CJPA wfbreak() throws Exception {
		return wfbreak(CSContext.getUserID(), CSContext.getCurEntID());
	}

	/**
	 * 中断流程
	 * 
	 * @param userid
	 * @param entid
	 * @return
	 * @throws Exception
	 */
	public CJPA wfbreak(String userid, String entid) throws Exception {
		// Shwuser cuser = new Shwuser();
		// cuser.findByID(userid);

		CDBConnection con = pool.getCon(this);
		con.startTrans();// 开始事务
		try {
			findByID4Update(con); // 锁定业务单据
			CField StatField = cfield("stat");
			if (StatField == null)
				throw new Exception("对象没有找到stat字段，无法中断流程!");
			if (StatField.getAsIntDefault(0) != 2)
				throw new Exception("创建流程后，才能中断流程!");
			if (getJpaStat() != CJPAStat.RSLOAD4DB)
				throw new Exception("未保存的对象，无法中断流程!");
			CField WfidField = cfield("wfid");
			if ((WfidField == null) || WfidField.isEmpty())
				throw new Exception("对象没有找到wfid字段，无法中断流程!");

			Shwwf wf = new Shwwf();
			wf.findByID4Update(con, WfidField.getValue(), true);

			Shwwfproc proc = null;
			for (CJPABase jprc : wf.shwwfprocs) {
				Shwwfproc proc1 = (Shwwfproc) jprc;
				if ("2".equals(proc1.stat.getValue())) {
					proc = proc1;
					break;
				}
			}
			if (proc == null)
				throw new Exception("流程没有找到活动节点，无法中断");
			if (proc.canbreak.getAsIntDefault(0) == 2)
				throw new Exception("节点不允许中断");
			Shwwfprocuser puser = checkProc(wf, proc, userid);
			if (puser == null)
				throw new Exception("当前用户未参与节点<" + proc.procname.getValue() + ">审批");

			// wf.setJpaStat(CJPAStat.RSDEL);// 删除流程实体
			StatField.setAsInt(1);// 单据到制单状态
			WfidField.setValue("");// 删除单据流程ID字段
			// wf.save(con);
			// wf.delete(con, true); 后面去删除 因为发送信息 会用到流程实例
			// save(con);
			if (getPublicControllerBase() != null)
				((JPAController) getPublicControllerBase()).OnWfBreak((CJPA) this, wf, con);
			JPAControllerBase ctr = getController();
			if (ctr != null) {
				((JPAController) ctr).OnWfBreak((CJPA) this, wf, con);
			}
			CWFNotify.wfBreakNotify(this, wf, userid);
			wf.delete(con, true);
			save(con);
			con.submit();
			return (CJPA) this;
		} catch (Exception e) {
			con.rollback();
			Logsw.error("中断流程错误:", e);
			throw e;
		} finally {
			con.close();
		}
	}

	//
	/**
	 * 转办
	 * 
	 * @param touserid
	 * @param aoption
	 * @return
	 * @throws Exception
	 */
	public CJPA wftransfer(String touserid, String aoption) throws Exception {
		return wftransfer(touserid, aoption, CSContext.getUserID(), CSContext.getCurEntID());
	}

	/**
	 * 转办
	 * 
	 * @param touserid
	 * @param aoption
	 * @param userid
	 * @param entid
	 * @return
	 * @throws Exception
	 */
	public CJPA wftransfer(String touserid, String aoption, String userid, String entid) throws Exception {
		CDBConnection con = pool.getCon(this);
		con.startTrans();// 开始事务
		try {
			findByID4Update(con); // 锁定业务单据

			CField WfidField = cfield("wfid");
			if ((WfidField == null) || WfidField.isEmpty())
				throw new Exception("对象没有找到wfid字段，无法转办流程!");

			Shwwf wf = new Shwwf();
			wf.findByID4Update(con, WfidField.getValue(), true);

			// 检查当前用户是否在当前流程中
			Shwwfproc proc = null;
			for (CJPABase jprc : wf.shwwfprocs) {
				Shwwfproc proc1 = (Shwwfproc) jprc;
				if ("2".equals(proc1.stat.getValue())) {
					proc = proc1;
					break;
				}
			}
			if (proc == null)
				throw new Exception("流程没有找到活动节点，无法转办");
			Shwwfprocuser user = checkProc(wf, proc, userid);
			if (user == null) {
				throw new Exception("当前用户未参与节点<" + proc.procname.getValue() + ">审批");
			}

			Shwuser fruser = new Shwuser();
			fruser.findByID(userid);

			Shwuser touser = new Shwuser();

			if (touserid.equalsIgnoreCase(CSContext.getUserID()))
				throw new Exception("转办给自己？！");

			touser.findByID(touserid, true);
			if (touser.isEmpty())
				throw new Exception("根据用户ID<" + touserid + ">未找到用户，无法转办");
			user.opinion.setValue(aoption);
			user.userid.setValue(touser.userid.getValue());
			newwflog(con, wf, proc, 4, touserid, user.displayname.getValue(), aoption);// 插入转办日志
			user.displayname.setValue(touser.displayname.getValue() + "(转自)" + user.displayname.getValue());
			wf.save(con);
			if (getPublicControllerBase() != null) {
				((JPAController) getPublicControllerBase()).OnWfTransfer((CJPA) this, con, wf, proc, fruser, touser);
			}

			JPAControllerBase ctr = getController();
			if (ctr != null) {
				((JPAController) ctr).OnWfTransfer((CJPA) this, con, wf, proc, fruser, touser);
			}
			CWFNotify.wfTransferNotify(this, wf, proc, user, fruser, touser, userid);
			con.submit();
			return (CJPA) this;
		} catch (Exception e) {
			con.rollback();
			Logsw.error("转办流程错误:", e);
			throw e;
		} finally {
			con.close();
		}
	}

	public static Shwwfprocuser checkProc(Shwwf wf, Shwwfproc proc, String curuserid) throws Exception {
		if (proc.stat.getAsIntDefault(0) != ProcStat.focued)
			throw new Exception("当前节点<" + proc.procname.getValue() + ">不是活动节点");
		Shwuser cu = new Shwuser();
		cu.findByID(curuserid);
		for (CJPABase jpapu : proc.shwwfprocusers) {
			Shwwfprocuser pu = (Shwwfprocuser) jpapu;
			if (((pu.stat.getAsIntDefault(0) == 1) || (pu.stat.getAsIntDefault(0) == 3)) && (pu.jointype.getAsIntDefault(0) < 3)) {
				if (pu.userid.getValue().equalsIgnoreCase(curuserid)) {
					return pu;
				} else {
					// 出差中 并且代理给当前用户
					Shwuser u = new Shwuser();
					u.findByID(pu.userid.getValue());
					if (u.goout.getAsIntDefault(0) == 1) {
						String sqlstr = "SELECT COUNT(*) ct FROM `shwuser_wf_agent` a"
								+ " WHERE a.`userid`=" + u.userid.getValue() + " AND a.`wftempid`=" + wf.wftemid.getValue() + " AND a.`auserid`=" + curuserid;
						String ct = DBPools.defaultPool().openSql2List(sqlstr).get(0).get("ct");
						if (Integer.valueOf(ct) == 1) {
							pu.displayname.setValue(cu.displayname.getValue() + "(代)" + u.displayname.getValue());
							return pu;
						}
					}
				}
			}
		}
		return null;
	}

	private CField getFieldByParmName(String pname) throws Exception {
		// "DPAEDT_Tdrp_item_price_cust_property1"
		if (pname == null)
			throw new Exception("根据参数名获取字段时，发现参数名为Null");
		String fdname = pname.substring(8 + this.getClass().getSimpleName().length() + 1, pname.length());
		return this.cfield(fdname);
	}

	/**
	 * 执行节点扩展函数 先取消
	 * 
	 * @param wftemproc
	 * @param acttion
	 * @param stage
	 * @param eoparms
	 * @throws Exception
	 */
	// private void dealProcExtOptions(Shwwftempproc wftemproc, String acttion, String stage, EOParms eoparms) throws Exception {
	// JSONArray ops = getProcExtOptions(wftemproc, acttion, stage);
	// if (ops == null)
	// return;
	// for (int i = 0; i < ops.size(); i++) {
	// JSONObject op = ops.getJSONObject(i);
	// callProcExtFunction(op, eoparms);
	// }
	// }

	// private JSONArray getProcExtOptions(Shwwftempproc wftemproc, String acttion, String stage) {
	// if ((acttion == null) || (stage == null))
	// return null;
	// if (!wftemproc.procoption.isEmpty()) {
	// JSONArray options = JSONArray.fromObject(wftemproc.procoption.getValue());
	// JSONArray rst = new JSONArray();
	// for (int i = 0; i < options.size(); i++) {
	// JSONObject op = options.getJSONObject(i);
	// if ((acttion.equals(op.getString("acttion"))) && (stage.equals(op.getString("stage")))) {
	// rst.add(op);
	// }
	// }
	// return (rst.size() == 0) ? null : rst;
	// } else
	// return null;
	// }

	// private void getParmsClass(String sps, EOParms parms, List<Class> cls, List<Object> obs) {
	// String[] ps = sps.split(",");
	// for (String p : ps) {
	// if (p == null)
	// continue;
	// if (p.equals(EOParmsstr._dbcon)) {
	// cls.add(CDBConnection.class);
	// obs.add(parms.dbcon);
	// }
	// if (p.equals(EOParmsstr._jpa)) {
	// cls.add(CJPA.class);
	// obs.add(parms.jpa);
	// }
	// if (p.equals(EOParmsstr._wftemp)) {
	// cls.add(Shwwftemp.class);
	// obs.add(parms.wftemp);
	// }
	// if (p.equals(EOParmsstr._tempproc)) {
	// cls.add(Shwwftempproc.class);
	// obs.add(parms.tempproc);
	// }
	// if (p.equals(EOParmsstr._tempprocusers)) {
	// cls.add(CJPALineData.class);
	// obs.add(parms.tempprocusers);
	// }
	// if (p.equals(EOParmsstr._wf)) {
	// cls.add(Shwwf.class);
	// obs.add(parms.wf);
	// }
	// if (p.equals(EOParmsstr._proc)) {
	// cls.add(Shwwfproc.class);
	// obs.add(parms.proc);
	// }
	// if (p.equals(EOParmsstr._procusers)) {
	// cls.add(CJPALineData.class);
	// obs.add(parms.procusers);
	// }
	// if (p.equals(EOParmsstr._fromproc)) {
	// cls.add(Shwwfproc.class);
	// obs.add(parms.fromproc);
	// }
	// if (p.equals(EOParmsstr._toproc)) {
	// cls.add(Shwwfproc.class);
	// obs.add(parms.toproc);
	// }
	// if (p.equals(EOParmsstr._touser)) {
	// cls.add(Shwuser.class);
	// obs.add(parms.touser);
	// }
	// }
	// }

	/**
	 * 根据流程实例获取JPA实例，包含JPA数据的
	 * 
	 * @param wf
	 * @return
	 * @throws Exception
	 */
	public static CJPABase getJPAByWF(Shwwf wf) throws Exception {
		String jpaclass = wf.clas.getValue();
		CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass);
		return jpa.findByID(wf.ojcectid.getValue());
	}
	// 暂时取消
	// parms: dbcon,jpa,wftemp,tempproc,tempprocusers,wf,proc,procusers,fromproc,toproc,touser
	// private void callProcExtFunction(JSONObject eop, EOParms parms) throws Exception {
	// String funcname = eop.getString("funcname");
	// String classname = funcname.substring(0, funcname.lastIndexOf("."));
	// String mname = funcname.substring(funcname.lastIndexOf(".") + 1);
	// Class<?> extclass = Class.forName(classname);
	// String sps = eop.getString("prams");
	// List<Class> cls = new ArrayList<Class>();
	// List<Object> obs = new ArrayList<Object>();
	// getParmsClass(sps, parms, cls, obs);
	//
	// // System.out.println("cls:::::" + cls);
	// // System.out.println("clssize:::::" + cls.size());
	//
	// Class[] clss = null;
	// clss = cls.toArray(new Class[0]);
	// Object[] args = null;
	// args = obs.toArray(new Object[0]);
	//
	// Method method = extclass.getMethod(mname, clss);
	// if (method == null)
	// throw new Exception("没有找到满足条件的方法" + classname + "." + mname);
	// Object co = extclass.newInstance();
	// try {
	// method.invoke(co, args);
	// } catch (IllegalAccessException e) {
	// throw new Exception(e.getCause());
	// } catch (IllegalArgumentException e) {
	// throw new Exception(e.getCause());
	// } catch (InvocationTargetException e) {
	// throw new Exception(e.getCause());
	// }
	// }
}
