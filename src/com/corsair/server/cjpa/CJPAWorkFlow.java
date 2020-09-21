package com.corsair.server.cjpa;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPAForDelphi;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.JPAControllerBase;
import com.corsair.cjpa.JPAControllerBase.ArraiveType;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.Logsw;
import com.corsair.server.base.CSContext;
import com.corsair.server.generic.Shworg;
import com.corsair.server.generic.Shwuser;
import com.corsair.server.util.CorUtil;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwflinkline;
import com.corsair.server.wordflow.Shwwflinklinecondition;
import com.corsair.server.wordflow.Shwwfproc;
import com.corsair.server.wordflow.Shwwfproccondition;
import com.corsair.server.wordflow.Shwwfproclog;
import com.corsair.server.wordflow.Shwwfprocuser;
import com.corsair.server.wordflow.Shwwftemp;
import com.corsair.server.wordflow.Shwwftemp_choice;
import com.corsair.server.wordflow.Shwwftemplinkline;
import com.corsair.server.wordflow.Shwwftemplinklinecondition;
import com.corsair.server.wordflow.Shwwftempproc;
import com.corsair.server.wordflow.Shwwftempproccondition;
import com.corsair.server.wordflow.Shwwftempprocuser;

/**不要了
 * @author Administrator
 *
 */
public abstract class CJPAWorkFlow extends CJPAForDelphi {

	public CJPAWorkFlow() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}
	/*
	 * // 节点状态 private class ProcStat { public final static int waitfor = 1;//
	 * 待审批 public final static int focued = 2;// 活动节点 public final static int
	 * finished = 3;// 完成 }
	 * 
	 * // 节点用户状态 private class ProcUserStat { public final static int waitfor =
	 * 1;// 1 待审批 public final static int finished = 2;// 2 已完成 public final
	 * static int refused = 3;// 3 驳回 }
	 * 
	 * public CJPAWorkFlow() throws Exception {
	 * 
	 * }
	 * 
	 * public CJPAWorkFlow(String sqlstr) throws Exception { super(sqlstr); }
	 * 
	 * // 子类继承的函数 ，用来接受流程信息 end
	 * 
	 * // 启动流程 创建流程实例
	 * 
	 * public CJPA wfcreate(String wftempid) throws Exception { CDBConnection
	 * con = pool.getCon(this); con.startTrans();// 开始事务 try {
	 * wfcreate(wftempid, con, CSContext.getUserID(), CSContext.getCurEntID());
	 * con.submit(); return (CJPA) this; } catch (Exception e) { con.rollback();
	 * throw new Exception(e); } }
	 * 
	 * public CJPA wfcreate(String wftempid, CDBConnection con) throws Exception
	 * { return wfcreate(wftempid, con, CSContext.getUserID(),
	 * CSContext.getCurEntID()); }
	 * 
	 * // 启动流程 创建流程实例
	 * 
	 * @SuppressWarnings("unchecked") public CJPA wfcreate(String wftempid,
	 * CDBConnection con, String userid, String entid) throws Exception {
	 * Shwuser cuser = new Shwuser(); cuser.findByID(userid);
	 * findByID4Update(con); // 锁定业务单据 // this.tojsonfile("c:\\s.txt"); if
	 * (getPublicControllerBase() != null) ((JPAController)
	 * getPublicControllerBase()).BeforeWFStart((CJPA) this, wftempid, con); if
	 * (getPublicControllerBase() != null) ((JPAController)
	 * getPublicControllerBase()).BeforeWFStart((CJPA) this, wftempid, con);
	 * JPAControllerBase ctr = getController(); if (ctr != null)
	 * ((JPAController) ctr).BeforeWFStart((CJPA) this, wftempid, con);
	 * 
	 * Shwwftemp wftem = new Shwwftemp(); CField StatField = cfield("stat"); if
	 * (StatField == null) throw new Exception("对象没有找到stat字段，不允许创建流程!"); if
	 * (StatField.getAsIntDefault(0) != 1) throw new
	 * Exception("只有制单状态的对象，才允许创建流程,当前状态为【" + StatField.getAsIntDefault(0) +
	 * "】"); boolean filished = false; if ((wftempid == null) ||
	 * wftempid.isEmpty()) { StatField.setAsInt(9); filished = true; } else {
	 * Shwwf wf = new Shwwf(); if (getJpaStat() != CJPAStat.RSLOAD4DB) throw new
	 * Exception("未保存的对象，不允许创建流程!"); CField WfidField = cfield("wfid"); if
	 * (WfidField == null) throw new Exception("对象没有找到wfid字段，不允许创建流程!");
	 * 
	 * CJPALineData<Shworg> curorgs = new CJPALineData<Shworg>(Shworg.class);
	 * CField IDPathField = cfield("idpath"); if ((IDPathField != null) &&
	 * (!IDPathField.isEmpty())) { Shworg objorg = new Shworg();
	 * objorg.findBySQL("select * from shworg where idpath='" +
	 * IDPathField.getValue() + "'", true); if (objorg.isEmpty()) throw new
	 * Exception("没有找到路径为" + IDPathField.getValue() + "的机构!");
	 * curorgs.add(objorg); } else { curorgs = CSContext.getUserOrgs(userid,
	 * entid); }
	 * 
	 * wftem.findByID(wftempid, true); if (wftem.isEmpty()) throw new
	 * Exception("载入流程模板错误");
	 * 
	 * if (createNewWFObjByTemID(curorgs, wf, wftem, cuser, entid)) if
	 * (!CreateProcsRes(wf, wftem, con)) throw new Exception("创建流程错误"); String
	 * sbjt = null; for (CField sfd : wfsubjectfields) { sbjt = sbjt +
	 * sfd.getValue() + "."; } if ((sbjt != null) && (!sbjt.isEmpty())) sbjt =
	 * sbjt.substring(1, sbjt.length() - 1);
	 * wf.subject.setValue(wftem.wftempname.getValue() + "(" + sbjt + ")");
	 * wf.ojcectid.setValue(this.getid());
	 * WfidField.setValue(wf.wfid.getValue()); StatField.setAsInt(2); filished =
	 * false;
	 * 
	 * if (getPublicControllerBase() != null) ((JPAController)
	 * getPublicControllerBase()).BeforeWFStartSave((CJPA) this, wf, con,
	 * filished); ctr = getController(); if (ctr != null) ((JPAController)
	 * ctr).BeforeWFStartSave((CJPA) this, wf, con, filished); wf.save(con); }
	 * save(con); if (getPublicControllerBase() != null) ((JPAController)
	 * getPublicControllerBase()).AfterWFStart((CJPA) this, con, filished); ctr
	 * = getController(); if (ctr != null) ((JPAController)
	 * ctr).AfterWFStart((CJPA) this, con, filished); return (CJPA) this; }
	 * 
	 * private boolean createNewWFObjByTemID(CJPALineData<Shworg> curorgs, Shwwf
	 * wf, Shwwftemp wftem, Shwuser cuser, String entid) throws Exception {
	 * wf.assignfield(wftem, true);// 给相同字段赋值
	 * wf.wftemid.setValue(wftem.wftempid.getValue());
	 * wf.wfname.setValue(wftem.wftempname.getValue());
	 * wf.creator.setValue(cuser.displayname.getValue());
	 * wf.createtime.setAsDatetime(new Date()); wf.entid.setValue(entid);
	 * wf.submituserid.setValue(cuser.userid.getValue());
	 * createProcsBytemProcs(wf, curorgs, wf.shwwfprocs, wftem.shwwftempprocs,
	 * cuser, entid); CreateWFLinksByTemp(wf.shwwflinklines,
	 * wftem.shwwftemplinklines); return true; }
	 * 
	 * private boolean CreateProcsRes(Shwwf wf, Shwwftemp wftem, CDBConnection
	 * con) throws Exception { wf.checkOrCreateIDCode("shwwf"); //
	 * wf.toxmlfile("c:\\jpa.xml"); for (CJPABase jpaproc : wf.shwwfprocs) {
	 * Shwwfproc proc = (Shwwfproc) jpaproc; for (CJPABase jpaproctem :
	 * wftem.shwwftempprocs) { Shwwftempproc proctem = (Shwwftempproc)
	 * jpaproctem; if
	 * (proc.proctempid.getValue().equalsIgnoreCase(proctem.proctempid
	 * .getValue())) { if (!setprocnext(wf, proc, proctem, con)) return false; }
	 * } } return true; }
	 * 
	 * private boolean setprocnext(Shwwf wf, Shwwfproc proc, Shwwftempproc
	 * proctem, CDBConnection con) throws Exception { //
	 * System.out.println(wf.shwwflinklines.size()); for (CJPABase jpaline :
	 * wf.shwwflinklines) { Shwwflinkline line = (Shwwflinkline) jpaline; //
	 * System.out.println(line.fromprocid.getValue() + " next " + //
	 * proctem.proctempid.getValue()); if
	 * (line.fromprocid.getValue().equals(proctem.proctempid.getValue())) {
	 * line.fromprocid.setValue(proc.procid.getValue()); for (CJPABase
	 * japprocnxt : wf.shwwfprocs) { Shwwfproc procnext = (Shwwfproc)
	 * japprocnxt; if
	 * (line.toprocid.getValue().equals(procnext.proctempid.getValue())) { if
	 * (proc.isbegin.getAsIntDefault(0) == 1) { procnext.stat.setAsInt(2);//
	 * 设置为当前节点 proc.nextprocs.setValue(procnext.procid.getValue());
	 * 
	 * if (getPublicControllerBase() != null) { ((JPAController)
	 * getPublicControllerBase()).OnArriveProc((CJPA) this, procnext, con,
	 * ArraiveType.atCreate); ((JPAController)
	 * getPublicControllerBase()).OnLiveProc((CJPA) this, procnext, con,
	 * ArraiveType.atCreate); } JPAControllerBase ctr = getController(); if (ctr
	 * != null) { ((JPAController) ctr).OnArriveProc((CJPA) this, procnext, con,
	 * ArraiveType.atCreate); ((JPAController) ctr).OnLiveProc((CJPA) this,
	 * procnext, con, ArraiveType.atCreate); } }
	 * line.toprocid.setValue(procnext.procid.getValue()); break; } } } } return
	 * true; }
	 * 
	 * private boolean createProcsBytemProcs(Shwwf wf, CJPALineData<Shworg>
	 * curorgs, CJPALineData<Shwwfproc> wfprocs, CJPALineData<Shwwftempproc>
	 * wftemprocs, Shwuser cuser, String entid) throws Exception { for (CJPABase
	 * jpawftemproc : wftemprocs) { Shwwftempproc wftemproc = (Shwwftempproc)
	 * jpawftemproc; Shwwfproc wfproc = new Shwwfproc();
	 * wfproc.assignfield(wftemproc, true);// 给相同字段赋值
	 * wfproc.wfid.setValue(null); wfproc.procid.setValue(null);
	 * wfproc.procname.setValue(wftemproc.proctempname.getValue());
	 * wfproc.stat.setAsInt(1);// 待审批 wfproc.nextprocs.setValue(null);//
	 * 用以标记实际审批过程中流程流向 if (wfproc.proctempid.isEmpty()) throw new
	 * Exception("流程模板节点id获取错误!"); wfprocs.add(wfproc); if
	 * (!createProcUsersByTem(wf, curorgs, wfproc, wfproc.shwwfprocusers,
	 * wftemproc, cuser, entid))// 创建用户 return false; if
	 * ((wfproc.shwwfprocusers.size() == 0) && (wfproc.isend.getAsIntDefault(0)
	 * != 1) && (wfproc.isbegin.getAsIntDefault(0) != 1)) { throw new
	 * Exception("流程节点【" + wftemproc.proctempname.getValue() + "】无执行人"); } if
	 * (!CreateProcconditionsByTemp(wfproc.shwwfprocconditions,
	 * wftemproc.shwwftempprocconditions))// 创建条件 return false; } return true; }
	 * 
	 * private boolean createProcUsersByTem(Shwwf wf, CJPALineData<Shworg>
	 * curorgs, Shwwfproc wfproc, CJPALineData<Shwwfprocuser> users,
	 * Shwwftempproc wftemproc, Shwuser cuser, String entid) throws Exception {
	 * CJPALineData<Shwwftempprocuser> temusers = wftemproc.shwwftempprocusers;
	 * if (wfproc.isbegin.getAsIntDefault(0) == 1) { Shwwfprocuser user = new
	 * Shwwfprocuser(); user.userid.setValue(cuser.userid.getValue());
	 * user.displayname.setValue(cuser.displayname.getValue());
	 * user.isposition.setAsInt(2); user.sortindex.setAsInt(1);
	 * user.stat.setAsInt(ProcUserStat.finished);// 已经完成 2
	 * user.jointype.setAsInt(1); wfproc.stat.setAsInt(ProcStat.finished);//
	 * 头结点设置成已经完成3 users.add(user); return true; } for (CJPABase jpatemuser :
	 * temusers) { Shwwftempprocuser temuser = (Shwwftempprocuser) jpatemuser;
	 * if (temuser.isposition.getAsIntDefault(0) == 2) {// 真人 if
	 * (temuser.userid.getAsIntDefault(0) == -1) {// 提交者 Shwwfprocuser user =
	 * new Shwwfprocuser(); user.assignfield(jpatemuser, true);
	 * user.userid.setValue(cuser.userid.getValue());
	 * user.displayname.setValue(cuser.displayname.getValue());
	 * user.stat.setAsInt(1);// 待审批 users.add(user); } else if
	 * (temuser.userid.getAsIntDefault(0) == -2) {// 管理员 // 根据流程管理员ID载入用户信息 }
	 * else {// 普通老百姓 Shwwfprocuser user = new Shwwfprocuser();
	 * user.assignfield(jpatemuser, true); users.add(user); } } else {// 岗位
	 * CJPALineData<Shwuser> orgusers = findUserByPosition(wftemproc, wf,
	 * wfproc, curorgs, temuser, entid); for (CJPABase jpauser : orgusers) {
	 * Shwuser user = (Shwuser) jpauser; Shwwfprocuser procuser = new
	 * Shwwfprocuser(); procuser.assignfield(temuser, true);
	 * procuser.userid.setValue(user.userid.getValue());
	 * procuser.displayname.setValue(user.displayname.getValue());
	 * users.add(procuser); } } }// for return true; }
	 * 
	 * @SuppressWarnings("unchecked") private CJPALineData<Shwuser>
	 * findUserByPosition(Shwwftempproc wftemproc, Shwwf wf, Shwwfproc proc,
	 * CJPALineData<Shworg> curorgs, Shwwftempprocuser temuser, String entid)
	 * throws Exception { String PostionID = temuser.userid.getValue(); String
	 * sqlstr = "SELECT  a.*  " // a.userid,a.username,a.displayname +
	 * "  FROM shwuser a, shwposition b, shwpositionuser c  " +
	 * "  where a.userid = c.userid and b.positionid = c.positionid " +
	 * "  and a.actived = 1 and b.isvisible = 1 and b.positionid = " +
	 * PostionID; CJPALineData<Shwuser> fdusers = new
	 * CJPALineData<Shwuser>(null, (Class<CJPABase>) ((java.lang.reflect.Type)
	 * Shwuser.class)); fdusers.findDataBySQL(sqlstr, true, true); if
	 * (fdusers.size() == 0) throw new Exception("节点【" +
	 * wftemproc.proctempname.getValue() + "】岗位【" +
	 * temuser.displayname.getValue() + "】未找到执行人!"); if (fdusers.size() == 1) {
	 * return fdusers; } java.util.Iterator<CJPABase> iter =
	 * fdusers.listIterator(); while (iter.hasNext()) { Shwuser u = (Shwuser)
	 * iter.next(); if (temuser.userfindtype.getAsIntDefault(0) == 1) {// 当前机构
	 * 取单据所在机构 if (!isUserInCurOrgs(u, curorgs)) { // 失败则取当前用户所在机构（包括默认机构）
	 * iter.remove(); } } if (temuser.userfindtype.getAsIntDefault(0) == 2) {//
	 * 从当前机构上朔 if (!isUserInSuperOrgs(u, curorgs)) iter.remove(); } if
	 * (temuser.userfindtype.getAsIntDefault(0) == 3) {// 从当前机构下朔 if
	 * (!isUserInChildOrgs(u, curorgs, entid)) iter.remove(); } if
	 * (temuser.userfindtype.getAsIntDefault(0) == 4) {// 所有机构
	 * 
	 * } if (temuser.userfindtype.getAsIntDefault(0) == 5) {// JPA控制器 if
	 * (getPublicControllerBase() != null) { Shwuser utem = ((JPAController)
	 * getPublicControllerBase()).OnWFFindUserByPosition((CJPA) this, wf, proc,
	 * temuser); if ((utem == null) || (utem.userid.getAsInt() !=
	 * u.userid.getAsInt())) { iter.remove(); } } JPAControllerBase ctr =
	 * getController(); if (ctr != null) { Shwuser utem = ((JPAController)
	 * ctr).OnWFFindUserByPosition((CJPA) this, wf, proc, temuser); if ((utem ==
	 * null) || (utem.userid.getAsInt() != u.userid.getAsInt())) {
	 * iter.remove(); } } } } return fdusers; }
	 * 
	 * private boolean isUserInCurOrgs(Shwuser u, CJPALineData<Shworg> curorgs)
	 * throws Exception { String orgids = null; for (CJPABase jpaorg : curorgs)
	 * { Shworg org = (Shworg) jpaorg; orgids = orgids + org.orgid.getValue() +
	 * ","; } if (orgids == null) { throw new Exception("检查用户所属机构时，机构为空!"); }
	 * CDBConnection con = null; Statement stmt = null; ResultSet rs = null; try
	 * { orgids = orgids.substring(0, orgids.length() - 1); orgids = "(" +
	 * orgids + ")"; String sqlstr =
	 * "select  count(*) ct from shworguser a where a.userid=" +
	 * u.userid.getValue() + " and a.orgid in" + orgids; con =
	 * pool.getCon(this); stmt = con.con.createStatement(); rs =
	 * stmt.executeQuery(sqlstr); return (rs.getInt("ct") != 0); } finally {
	 * rs.close(); stmt.close(); con.close(); } }
	 * 
	 * private boolean isIDinIDs(String id, List<String> ids) { if ((id == null)
	 * || id.isEmpty()) return true; for (String idstr : ids) { if
	 * (id.equalsIgnoreCase(idstr)) return true; } return false; }
	 * 
	 * private boolean isUserInSuperOrgs(Shwuser u, CJPALineData<Shworg>
	 * curorgs) throws Exception { String orgidpaths = ""; for (CJPABase jpaorg
	 * : curorgs) { Shworg org = (Shworg) jpaorg; orgidpaths = orgidpaths +
	 * org.idpath.getValue() + ","; } String[] arrids = orgidpaths.split(",");
	 * List<String> ids = new ArrayList<String>(); for (String id : arrids) { if
	 * ((id != null) && (!id.isEmpty()) && (!isIDinIDs(id, ids))) ids.add(id); }
	 * String orgids = ""; for (String id : ids) { orgids = orgids + id + ","; }
	 * if (orgids.isEmpty()) { throw new Exception("检查用户所属机构时，机构为空!"); }
	 * CDBConnection con = null; Statement stmt = null; ResultSet rs = null; try
	 * { orgids = orgids.substring(0, orgids.length() - 1); orgids = "(" +
	 * orgids + ")"; String sqlstr =
	 * "select  count(*) ct from shworguser a where a.userid=" +
	 * u.userid.getValue() + " and a.orgid in" + orgids;
	 * System.out.println("sqlstr:" + sqlstr); con = pool.getCon(this); stmt =
	 * con.con.createStatement(); rs = stmt.executeQuery(sqlstr); if (rs.next())
	 * return (rs.getInt("ct") != 0); else return false; } finally { rs.close();
	 * stmt.close(); con.close(); } }
	 * 
	 * @SuppressWarnings("unchecked") private boolean isUserInChildOrgs(Shwuser
	 * u, CJPALineData<Shworg> curorgs, String entid) throws Exception {
	 * CJPALineData<Shworg> usorgs = new CJPALineData<Shworg>(null,
	 * (Class<CJPABase>) ((java.lang.reflect.Type) Shworg.class)); String sqlstr
	 * =
	 * "select a.* from shworg a,shworguser b where a.orgid=b.orgid and a.entid="
	 * + entid + " and b.userid=" + u.userid.getValue();
	 * usorgs.findDataBySQL(sqlstr, true, true); for (CJPABase jpa : usorgs) {
	 * Shworg uorg = (Shworg) jpa; for (CJPABase cjpa : curorgs) { Shworg corg =
	 * (Shworg) cjpa; String uidpath = uorg.idpath.getValue(); String cidpath =
	 * corg.idpath.getValue(); if (uidpath.substring(0,
	 * cidpath.length()).equalsIgnoreCase(cidpath)) return true; } } return
	 * false; }
	 * 
	 * private boolean
	 * CreateProcconditionsByTemp(CJPALineData<Shwwfproccondition> cdns,
	 * CJPALineData<Shwwftempproccondition> cdntems) throws Exception { for
	 * (CJPABase jpa : cdntems) { Shwwftempproccondition cndtem =
	 * (Shwwftempproccondition) jpa; Shwwfproccondition cdn = new
	 * Shwwfproccondition(); cdn.assignfield(cndtem, true); cdns.add(cdn); }
	 * return true; }
	 * 
	 * private boolean CreateLinkCdtn(CJPALineData<Shwwflinklinecondition>
	 * cdtns, CJPALineData<Shwwftemplinklinecondition> cdtntems) throws
	 * Exception { for (CJPABase jpact : cdtntems) { Shwwftemplinklinecondition
	 * ct = (Shwwftemplinklinecondition) jpact; Shwwflinklinecondition c = new
	 * Shwwflinklinecondition(); c.assignfield(ct, true); cdtns.add(c); } return
	 * true; }
	 * 
	 * private boolean CreateWFLinksByTemp(CJPALineData<Shwwflinkline> links,
	 * CJPALineData<Shwwftemplinkline> linktems) throws Exception { for
	 * (CJPABase jpalt : linktems) { Shwwftemplinkline linktem =
	 * (Shwwftemplinkline) jpalt; Shwwflinkline link = new Shwwflinkline();
	 * link.assignfield(linktem, true); //
	 * System.out.println(linktem.fromproctempid.getValue() + //
	 * " CreateWFLinksByTemp " + linktem.toproctempid.getValue());
	 * link.fromprocid.setValue(linktem.fromproctempid.getValue());//
	 * ///后期生成流程实例关系时候会需要用到
	 * link.toprocid.setValue(linktem.toproctempid.getValue()); if
	 * (!(CreateLinkCdtn(link.shwwflinklineconditions,
	 * linktem.shwwftemplinklineconditions))) return false; links.add(link); }
	 * return true; }
	 * 
	 * // create wftemp end
	 * 
	 * // 提交节点
	 * 
	 * public CJPA wfsubmit(String procid, String aoption) throws Exception {
	 * return wfsubmit(procid, aoption, CSContext.getUserID(),
	 * CSContext.getCurEntID()); }
	 * 
	 * public CJPA wfsubmit(String procid, String aoption, String userid, String
	 * entid) throws Exception { // Shwuser cuser = new Shwuser(); //
	 * cuser.findByID(userid); CDBConnection con = pool.getCon(this);
	 * con.startTrans();// 开始事务 try { wfsubmit(con, procid, aoption, userid,
	 * entid); con.submit(); return (CJPA) this; } catch (Exception e) {
	 * con.rollback(); Logsw.error("提交流程错误:", e); throw e; } }
	 * 
	 * public CJPA wfsubmit(CDBConnection con, String procid, String aoption,
	 * String userid, String entid) throws Exception { findByID4Update(con); //
	 * 锁定业务单据 CField StatField = cfield("stat"); if (StatField == null) throw
	 * new Exception("对象没有找到stat字段，不允许提交流程!"); if (StatField.getAsIntDefault(0)
	 * != 2) throw new Exception("创建流程后，才允许提交流程!"); if (getJpaStat() !=
	 * CJPAStat.RSLOAD4DB) throw new Exception("未保存的对象，不允许提交流程!"); CField
	 * WfidField = cfield("wfid"); if ((WfidField == null) ||
	 * WfidField.isEmpty()) throw new Exception("对象没有找到wfid字段，不允许提交流程!");
	 * 
	 * Shwwf wf = new Shwwf(); wf.findByID(WfidField.getValue(), true);
	 * Shwwfproc proc = getProcByID(wf, procid); if (proc == null) throw new
	 * Exception("根据节点ID【" + procid + "】未能找到节点!"); Shwwfprocuser puser =
	 * checkProc(wf, proc, userid); if (puser == null) throw new
	 * Exception("当前用户未参与节点<" + proc.procname.getValue() + ">审批");
	 * 
	 * // 检查当前节点条件是否满足提交 checkProcConditions(proc);
	 * 
	 * puser.opinion.setValue(aoption);
	 * puser.stat.setAsInt(ProcUserStat.finished);// 用户审批完成2
	 * puser.audittime.setAsDatetime(new Date()); Shwwfproc nxtproc = null; if
	 * (CheckProcFinished(proc)) { proc.stat.setAsInt(ProcStat.finished);//
	 * 节点完成3 nxtproc = setNextsActive(wf, proc);// 设置后续节点 同时检查分支条件 } boolean
	 * isFilishedWF = (wf.stat.getAsIntDefault(1) == 2); if (isFilishedWF) { //
	 * System.out.println("111111111111111111111111 filished!");
	 * StatField.setAsInt(9); save(con); } newwflog(wf, proc, puser, 1);//
	 * 插入审批日志 wf.save(con); if (proc.stat.getAsIntDefault(0) ==
	 * ProcStat.finished) {// 3 proc.livetime.setAsDatetime(new Date());//
	 * 记录当前节点离开时间 nxtproc.arivetime.setAsDatetime(new Date());// 记录下个节点到达时间
	 * OnProcFlished(proc); OnProcArived(nxtproc, con); if
	 * (getPublicControllerBase() != null) { ((JPAController)
	 * getPublicControllerBase()).OnArriveProc((CJPA) this, nxtproc, con,
	 * ArraiveType.atSubmit); ((JPAController)
	 * getPublicControllerBase()).OnLiveProc((CJPA) this, proc, con,
	 * ArraiveType.atSubmit); ((JPAController)
	 * getPublicControllerBase()).OnWfSubmit((CJPA) this, con, proc, nxtproc,
	 * isFilishedWF); } JPAControllerBase ctr = getController(); if (ctr !=
	 * null) { ((JPAController) ctr).OnArriveProc((CJPA) this, nxtproc, con,
	 * ArraiveType.atSubmit); ((JPAController) ctr).OnLiveProc((CJPA) this,
	 * proc, con, ArraiveType.atSubmit); ((JPAController) ctr).OnWfSubmit((CJPA)
	 * this, con, proc, nxtproc, isFilishedWF); } }
	 * 
	 * if (!proc.submitfunc.isEmpty()) { // String submf =
	 * proc.submitfunc.getValue(); // System.out.println(submf); //
	 * proc.submitfunc.getValue().调用定义的函数 } return (CJPA) this; }
	 * 
	 * private void OnProcFlished(Shwwfproc proc) { // 触发节点完成事件 根据节点类型 比如调用http
	 * 发邮件 短信 发微信 等操作 }
	 * 
	 * private void OnProcArived(Shwwfproc proc, CDBConnection con) throws
	 * Exception { // 触发某个节点到达事件 }
	 * 
	 * @SuppressWarnings("unchecked") private Shwwfproc setNextsActive(Shwwf wf,
	 * Shwwfproc proc) throws Exception { CJPALineData<Shwwflinkline> lines =
	 * new CJPALineData<Shwwflinkline>(null, (Class<CJPABase>)
	 * ((java.lang.reflect.Type) Shwwflinkline.class)); for (CJPABase jpaline :
	 * wf.shwwflinklines) { if (((Shwwflinkline)
	 * jpaline).fromprocid.getValue().equalsIgnoreCase(proc.procid.getValue()))
	 * lines.add(jpaline); } String err = null; checkLinkLineConditions(lines,
	 * err); if (lines.size() == 0) throw new Exception("没有找到后续节点无法提交:" + err);
	 * if (lines.size() > 1) throw new Exception("找到多个后续节点，请检查流程跳转条件设置是否合理?");
	 * Shwwflinkline line = (Shwwflinkline) lines.get(0); for (CJPABase jpaprc :
	 * wf.shwwfprocs) { Shwwfproc nxtproc = (Shwwfproc) jpaprc; if
	 * (line.toprocid.getValue().equalsIgnoreCase(nxtproc.procid.getValue())) {
	 * if ("1".equalsIgnoreCase(nxtproc.isend.getValue())) {
	 * wf.stat.setValue("2");// 流程完成 for (CJPABase jpap : wf.shwwfprocs) {
	 * ((Shwwfproc) jpap).stat.setAsInt(3);// 每个节点完成 } } else {
	 * proc.nextprocs.setValue(nxtproc.procid.getValue());
	 * nxtproc.stat.setAsInt(ProcStat.focued);// 后续节点设置为当前节点2 } return nxtproc;
	 * } } throw new Exception("没有找到后续节点无法提交!"); }
	 * 
	 * private void checkProcConditions(Shwwfproc proc) throws Exception { for
	 * (CJPABase jpa : proc.shwwfprocconditions) { Shwwfproccondition pcd =
	 * (Shwwfproccondition) jpa; String fdname = pcd.parmname.getValue(); if
	 * ((fdname == null) || fdname.isEmpty()) continue; CField cfd =
	 * cfield(fdname); if (cfd == null) throw new Exception("<" + fdname +
	 * ">字段在DPA中未发现!"); if (!CorUtil.isStrMath(cfd.getValue(),
	 * pcd.reloper.getValue(), pcd.parmvalue.getValue())) { throw new
	 * Exception("未满足条件:" + cfd.getCaption() + " " + cfd.getValue() +
	 * pcd.reloper.getValue() + pcd.parmvalue.getValue()); } } }
	 * 
	 * private void checkLinkLineConditions(CJPALineData<Shwwflinkline> lines,
	 * String err) throws Exception { Iterator<CJPABase> linesit =
	 * lines.iterator(); while (linesit.hasNext()) { CJPABase jpaline =
	 * linesit.next(); Shwwflinkline line = (Shwwflinkline) jpaline; boolean
	 * ismz = true; for (CJPABase jpalc : line.shwwflinklineconditions) {
	 * Shwwflinklinecondition lc = (Shwwflinklinecondition) jpalc; String fdname
	 * = lc.parmname.getValue();// DPAEDT_Tdrp_item_price_cust_cust_id if
	 * ((fdname == null) || fdname.isEmpty()) continue; if
	 * (fdname.startsWith("DPAEDT_")) { fdname = fdname.substring(9 +
	 * getClass().getSimpleName().length(), fdname.length()); // ustfordelphi }
	 * CField cfd = cfield(fdname); if (cfd == null) throw new Exception("<" +
	 * fdname + ">字段在DPA中未发现!"); // System.out.println(lc.frmcaption.getValue()
	 * + // ";cfd.getValue():" + cfd.getValue() + ";" + // lc.reloper.getValue()
	 * + lc.parmvalue.getValue()); if (!CorUtil.isStrMath(cfd.getValue(),
	 * lc.reloper.getValue(), lc.parmvalue.getValue())) { err = err + "未满足条件:" +
	 * cfd.getValue() + lc.reloper.getValue() + lc.parmvalue.getValue();
	 * Logsw.debug(err); ismz = false; break; } } if (!ismz) { err = err +
	 * "分支:<" + line.lltitle.getValue() + ">未能满足条件:" + err; linesit.remove(); }
	 * } }
	 * 
	 * private boolean CheckProcFinished(Shwwfproc proc) { boolean FD_JCZC =
	 * false; for (CJPABase jpapu : proc.shwwfprocusers) { Shwwfprocuser pu =
	 * (Shwwfprocuser) jpapu; if (pu.jointype.getAsIntDefault(0) == 1) { if
	 * (pu.stat.getAsIntDefault(0) == 1) {// 有一个决策者未完成则认为不可以完成 return false; }
	 * FD_JCZC = true;// 有决策者 } } if (FD_JCZC)// 有决策者，说明 所有决策者已经审批通过 return
	 * true; int minperson = proc.minperson.getAsIntDefault(0); //
	 * System.out.println("minperson:" + minperson); int cp = 0; for (CJPABase
	 * jpapu : proc.shwwfprocusers) { Shwwfprocuser pu = (Shwwfprocuser) jpapu;
	 * // System.out.println("pu.stat:" + pu.stat.getAsIntDefault(0) + //
	 * " displayname:" + pu.displayname.getValue()); if
	 * (pu.stat.getAsIntDefault(0) == 2) { cp++; // System.out.println("cp:" +
	 * cp); if ((minperson != 0) && (cp >= minperson)) {// 设置了最小人数 且超过最少人数
	 * return true; } } else { if (minperson == 0)// 没有设置最小通过人数 只要有没审批通过的人
	 * 判断节点没完成 return false; } } return true; }
	 * 
	 * private Shwwfproc getProcByID(Shwwf wf, String procid) { for (CJPABase
	 * jpaproc : wf.shwwfprocs) { Shwwfproc proc = (Shwwfproc) jpaproc; if
	 * (procid.equalsIgnoreCase(proc.procid.getValue())) return proc; } return
	 * null; }
	 * 
	 * private void newwflog(Shwwf wf, Shwwfproc proc, Shwwfprocuser puser, int
	 * acttype) throws Exception { Shwwfproclog log = new Shwwfproclog();
	 * log.objectid.setValue(this.getid());
	 * log.objectname.setValue(this.getClass().getSimpleName());
	 * log.objectdisname.setValue(wf.subject.getValue());
	 * log.wfid.setValue(wf.wfid.getValue());
	 * log.wfname.setValue(wf.wfname.getValue());
	 * log.procid.setValue(proc.procid.getValue());
	 * log.userid.setValue(puser.userid.getValue());
	 * log.displayname.setValue(puser.displayname.getValue());
	 * log.opinion.setValue(puser.opinion.getValue());
	 * log.actiontype.setAsInt(acttype); log.actiontime.setAsDatetime(new
	 * Date()); wf.shwwfproclogs.add(log); }
	 * 
	 * // 驳回到节点procid
	 * 
	 * public CJPA wfreject(String fprocid, String tprocid, String aoption)
	 * throws Exception { return wfreject(fprocid, tprocid, aoption,
	 * CSContext.getUserID(), CSContext.getCurEntID()); }
	 * 
	 * public CJPA wfreject(String fprocid, String tprocid, String aoption,
	 * String userid, String entid) throws Exception { Shwuser cswuser = new
	 * Shwuser(); cswuser.findByID(userid); CDBConnection con =
	 * pool.getCon(this); con.startTrans();// 开始事务 try { findByID4Update(con);
	 * // 锁定业务单据
	 * 
	 * CField StatField = cfield("stat"); if (StatField == null) throw new
	 * Exception("对象没有找到stat字段，不允许驳回流程!"); if (StatField.getAsIntDefault(0) !=
	 * 2) throw new Exception("创建流程后，才允许驳回流程!"); if (getJpaStat() !=
	 * CJPAStat.RSLOAD4DB) throw new Exception("未保存的对象，不允许驳回流程!"); CField
	 * WfidField = cfield("wfid"); if ((WfidField == null) ||
	 * WfidField.isEmpty()) throw new Exception("对象没有找到wfid字段，不允许驳回流程!");
	 * 
	 * Shwwf wf = new Shwwf(); wf.findByID(WfidField.getValue(), true);
	 * Shwwfproc fproc = getProcByID(wf, fprocid); if (fproc == null) throw new
	 * Exception("根据节点ID【" + fprocid + "】未能找到节点!"); Shwwfprocuser puser =
	 * checkProc(wf, fproc, userid); if (puser == null) throw new
	 * Exception("当前用户未参与节点<" + fproc.procname.getValue() + ">审批"); Shwwfproc
	 * tproc = getProcByID(wf, tprocid); if
	 * ("1".equals(fproc.isbegin.getValue())) throw new Exception("开始节点<" +
	 * fproc.procname.getValue() + ">，不允许驳回"); if
	 * ("1".equals(tproc.isbegin.getValue())) throw new
	 * Exception("不允许驳回到开始节点!");
	 * 
	 * Shwwfproc proctem = fproc; while
	 * (!(proctem.procid.getValue().equals(tproc.procid.getValue()))) {
	 * proctem.stat.setAsInt(1);// 设置节点为待审批状态 SetProcUsersStat(proctem, 1);//
	 * 设置节点所有参与用户为待审批状态 Shwwfproc proctem1 = getPreProc(wf, proctem); if
	 * (proctem1 == null) throw new Exception("<" + proctem.procname.getValue()
	 * + ">回溯节点为空!"); proctem = proctem1; } fproc.livetime.setAsDatetime(new
	 * Date());// 记录驳回节点时间 tproc.arivetime.setAsDatetime(new Date());//
	 * 记录下个节点到达时间 tproc.stat.setAsInt(2);// 设置节点为活动状态 SetProcUsersStat(tproc,
	 * 1);
	 * 
	 * Shwwfprocuser cuser = GetActiveUser(fproc, userid); if (cuser == null)
	 * throw new Exception("<" + fproc.procname.getValue() + ">未找到活动用户!");
	 * cuser.opinion.setValue(aoption); cuser.stat.setValue("3");
	 * cuser.audittime.setAsDatetime(new Date()); newwflog(wf, fproc, puser,
	 * 2);// 插入审批日志 wf.save(con); if (getPublicControllerBase() != null) {
	 * ((JPAController) getPublicControllerBase()).OnWfReject((CJPA) this, con,
	 * fproc, tproc); ((JPAController)
	 * getPublicControllerBase()).OnArriveProc((CJPA) this, tproc, con,
	 * ArraiveType.atSubmit); ((JPAController)
	 * getPublicControllerBase()).OnLiveProc((CJPA) this, fproc, con,
	 * ArraiveType.atSubmit); } JPAControllerBase ctr = getController(); if (ctr
	 * != null) { ((JPAController) ctr).OnWfReject((CJPA) this, con, fproc,
	 * tproc); ((JPAController) ctr).OnArriveProc((CJPA) this, tproc, con,
	 * ArraiveType.atSubmit); ((JPAController) ctr).OnLiveProc((CJPA) this,
	 * fproc, con, ArraiveType.atSubmit); } con.submit(); return (CJPA) this; }
	 * catch (Exception e) { con.rollback(); Logsw.error("驳回流程错误:", e); throw e;
	 * } }
	 * 
	 * private Shwwfprocuser GetActiveUser(Shwwfproc proc, String curuserid)
	 * throws Exception { for (CJPABase jpapu : proc.shwwfprocusers) {
	 * Shwwfprocuser pu = (Shwwfprocuser) jpapu; if
	 * ((pu.userid.getValue().equalsIgnoreCase(curuserid)) &&
	 * ((pu.stat.getAsIntDefault(0) == 1) || (pu.stat.getAsIntDefault(0) == 3))
	 * && (pu.jointype.getAsIntDefault(0) < 3)) return pu; } return null; }
	 * 
	 * private Shwwfproc getPreProc(Shwwf wf, Shwwfproc proc) { for (CJPABase
	 * jwf : wf.shwwfprocs) { Shwwfproc preproc = (Shwwfproc) jwf; if
	 * (proc.procid.getValue().equals(preproc.nextprocs.getValue())) { return
	 * preproc; } } return null; }
	 * 
	 * private void SetProcUsersStat(Shwwfproc proc, int value) { for (CJPABase
	 * ju : proc.shwwfprocusers) { Shwwfprocuser u = (Shwwfprocuser) ju;
	 * u.stat.setAsInt(value); } }
	 * 
	 * // 中断流程 public CJPA wfbreak() throws Exception { return
	 * wfbreak(CSContext.getUserID(), CSContext.getCurEntID()); }
	 * 
	 * public CJPA wfbreak(String userid, String entid) throws Exception { //
	 * Shwuser cuser = new Shwuser(); // cuser.findByID(userid);
	 * 
	 * CDBConnection con = pool.getCon(this); con.startTrans();// 开始事务 try {
	 * findByID4Update(con); // 锁定业务单据 CField StatField = cfield("stat"); if
	 * (StatField == null) throw new Exception("对象没有找到stat字段，无法中断流程!"); if
	 * (StatField.getAsIntDefault(0) != 2) throw new Exception("创建流程后，才能中断流程!");
	 * if (getJpaStat() != CJPAStat.RSLOAD4DB) throw new
	 * Exception("未保存的对象，无法中断流程!"); CField WfidField = cfield("wfid"); if
	 * ((WfidField == null) || WfidField.isEmpty()) throw new
	 * Exception("对象没有找到wfid字段，无法中断流程!");
	 * 
	 * Shwwf wf = new Shwwf(); wf.findByID(WfidField.getValue(), true);
	 * 
	 * Shwwfproc proc = null; for (CJPABase jprc : wf.shwwfprocs) { Shwwfproc
	 * proc1 = (Shwwfproc) jprc; if ("2".equals(proc1.stat.getValue())) { proc =
	 * proc1; break; } } if (proc == null) throw new
	 * Exception("流程没有找到活动节点，无法中断"); Shwwfprocuser puser = checkProc(wf, proc,
	 * userid); if (puser == null) throw new Exception("当前用户未参与节点<" +
	 * proc.procname.getValue() + ">审批");
	 * 
	 * wf.setJpaStat(CJPAStat.RSDEL);// 删除流程实体 StatField.setAsInt(1);// 单据到制单状态
	 * WfidField.setValue("");// 删除单据流程ID字段 wf.save(con); save(con); if
	 * (getPublicControllerBase() != null) ((JPAController)
	 * getPublicControllerBase()).OnWfBreak((CJPA) this, con); JPAControllerBase
	 * ctr = getController(); if (ctr != null) { ((JPAController)
	 * ctr).OnWfBreak((CJPA) this, con); } con.submit(); return (CJPA) this; }
	 * catch (Exception e) { con.rollback(); Logsw.error("中断流程错误:", e); throw e;
	 * } }
	 * 
	 * // 转办 public CJPA wftransfer(String touserid, String aoption) throws
	 * Exception { return wftransfer(touserid, aoption, CSContext.getUserID(),
	 * CSContext.getCurEntID()); }
	 * 
	 * public CJPA wftransfer(String touserid, String aoption, String userid,
	 * String entid) throws Exception { CDBConnection con = pool.getCon(this);
	 * con.startTrans();// 开始事务 try { findByID4Update(con); // 锁定业务单据
	 * 
	 * CField WfidField = cfield("wfid"); if ((WfidField == null) ||
	 * WfidField.isEmpty()) throw new Exception("对象没有找到wfid字段，无法转办流程!");
	 * 
	 * Shwwf wf = new Shwwf(); wf.findByID(WfidField.getValue(), true);
	 * 
	 * // 检查当前用户是否在当前流程中 Shwwfproc proc = null; for (CJPABase jprc :
	 * wf.shwwfprocs) { Shwwfproc proc1 = (Shwwfproc) jprc; if
	 * ("2".equals(proc1.stat.getValue())) { proc = proc1; break; } } if (proc
	 * == null) throw new Exception("流程没有找到活动节点，无法转办"); Shwwfprocuser user =
	 * checkProc(wf, proc, userid); if (user == null) { throw new
	 * Exception("当前用户未参与节点<" + proc.procname.getValue() + ">审批"); } Shwuser
	 * touser = new Shwuser();
	 * 
	 * if (touserid.equalsIgnoreCase(CSContext.getUserID())) throw new
	 * Exception("转办给自己？您是神!");
	 * 
	 * touser.findByID(touserid, true); if (touser.isEmpty()) throw new
	 * Exception("根据用户ID<" + touserid + ">未找到用户，无法转办");
	 * user.opinion.setValue(aoption);
	 * user.userid.setValue(touser.userid.getValue());
	 * user.displayname.setValue(touser.displayname.getValue() + "(转自)" +
	 * user.displayname.getValue()); newwflog(wf, proc, user, 4);// 插入审批日志
	 * wf.save(con); if (getPublicControllerBase() != null) ((JPAController)
	 * getPublicControllerBase()).OnWfTransfer((CJPA) this, con, proc, touser);
	 * JPAControllerBase ctr = getController(); if (ctr != null) {
	 * ((JPAController) ctr).OnWfTransfer((CJPA) this, con, proc, touser); }
	 * con.submit(); return (CJPA) this; } catch (Exception e) { con.rollback();
	 * Logsw.error("中断流程错误:", e); throw e; } }
	 * 
	 * public static Shwwfprocuser checkProc(Shwwf wf, Shwwfproc proc, String
	 * curuserid) throws Exception { if (!"2".equals(proc.stat.getValue()))
	 * throw new Exception("当前节点<" + proc.procname.getValue() + ">不是活动节点");
	 * Shwuser cu = new Shwuser(); cu.findByID(curuserid); for (CJPABase jpapu :
	 * proc.shwwfprocusers) { Shwwfprocuser pu = (Shwwfprocuser) jpapu; if
	 * (((pu.stat.getAsIntDefault(0) == 1) || (pu.stat.getAsIntDefault(0) == 3))
	 * && (pu.jointype.getAsIntDefault(0) < 3)) { if
	 * (pu.userid.getValue().equalsIgnoreCase(curuserid)) { return pu; } else {
	 * // 出差中 并且代理给当前用户 Shwuser u = new Shwuser();
	 * u.findByID(pu.userid.getValue()); if (u.goout.getAsIntDefault(0) == 1) {
	 * String sqlstr = "SELECT COUNT(*) ct FROM `shwuser_wf_agent` a" +
	 * " WHERE a.`userid`=" + u.userid.getValue() + " AND a.`wftempid`=" +
	 * wf.wftemid.getValue() + " AND a.`auserid`=" + curuserid; String ct =
	 * DBPools.defaultPool().openSql2List(sqlstr).get(0).get("ct"); if
	 * (Integer.valueOf(ct) == 1) {
	 * pu.displayname.setValue(cu.displayname.getValue() + "(代)" +
	 * u.displayname.getValue()); return pu; } } } } } return null; }
	 * 
	 * private CField getFieldByParmName(String pname) throws Exception { //
	 * "DPAEDT_Tdrp_item_price_cust_property1" if (pname == null) throw new
	 * Exception("根据参数名获取字段时，发现参数名为Null"); String fdname = pname.substring(8 +
	 * this.getClass().getSimpleName().length() + 1, pname.length()); return
	 * this.cfield(fdname); }
	 * 
	 * // WFMain函数调用
	 * 
	 * @SuppressWarnings("unchecked") public String _findWfTem(String xmldata)
	 * throws Exception { Document document = DocumentHelper.parseText(xmldata);
	 * Element employees = document.getRootElement(); String frmclassname =
	 * employees.element("frmclassname").getText(); String jpaxml =
	 * employees.element("jpaxml").getText(); fromxml(jpaxml); String sqlstr =
	 * "SELECT distinct b.wftempid,a.wftempname,b.parmname,b.reloper,b.parmvalue FROM shwwftemp a, shwwftemparms b "
	 * + " WHERE a.wftempid = b.wftempid AND a.stat = 1 AND a.entid = " +
	 * CSContext.getUserParmValue("curentid") +
	 * " AND UPPER(b.frmclassname) = UPPER('" + frmclassname + "')" +
	 * "   order by b.wftempid"; CJPALineData<Shwwftemp_choice> wfts = new
	 * CJPALineData<Shwwftemp_choice>(null, (Class<CJPABase>)
	 * ((java.lang.reflect.Type) Shwwftemp_choice.class));
	 * CJPALineData<Shwwftemp_choice> wftoks = new
	 * CJPALineData<Shwwftemp_choice>(null, (Class<CJPABase>)
	 * ((java.lang.reflect.Type) Shwwftemp_choice.class));
	 * wftoks.setTableName("Shwwftemp_choice"); wfts.findDataBySQL(sqlstr, true,
	 * true); for (CJPABase jpa : wfts) { Shwwftemp_choice wft =
	 * (Shwwftemp_choice) jpa; CField fd =
	 * getFieldByParmName(wft.parmname.getValue()); if (fd == null) { throw new
	 * Exception("<" + wft.parmname.getValue() + ">未找到字段"); } String value1 =
	 * fd.getValue(); if (CorUtil.isStrMath(value1, wft.reloper.getValue(),
	 * wft.parmvalue.getValue())) wftoks.add(wft); } // if (wftoks.size() == 0)
	 * // return "not find wf";
	 * 
	 * document = DocumentHelper.createDocument(); Element root =
	 * document.addElement("linedata");// 创建根节点 wftoks.toxmlnode(root); String
	 * result = document.asXML(); return result; }
	 * 
	 * // WFMain函数调用 public String _createWFByTemp(String xmldata) throws
	 * Exception { Document document = DocumentHelper.parseText(xmldata);
	 * Element employees = document.getRootElement(); String wftemid =
	 * employees.element("wftemid").getText(); String jpaxml =
	 * employees.element("jpaxml").getText(); fromxml(jpaxml); CDBConnection con
	 * = pool.getCon(this); con.startTrans();// 开始事务 try { wfcreate(wftemid,
	 * con, CSContext.getUserID(), CSContext.getCurEntID()).toxml();
	 * con.submit(); return toxml(); } catch (Exception e) { con.rollback();
	 * Logsw.error("创建流程错误:", e); throw e; } }
	 * 
	 * // WFMain函数调用 public String _submitWF(String xmldata) throws Exception {
	 * Document document = DocumentHelper.parseText(xmldata); Element employees
	 * = document.getRootElement(); String procid =
	 * employees.element("procid").getText(); String aoption =
	 * employees.element("aoption").getText(); String jpaxml =
	 * employees.element("jpaxml").getText(); fromxml(jpaxml); // // String rst
	 * = wfsubmit(procid, aoption).toxml(); return rst; }
	 * 
	 * // WFMain函数调用 public String _rejectWF(String xmldata) throws Exception {
	 * Document document = DocumentHelper.parseText(xmldata); Element employees
	 * = document.getRootElement(); String fprocid =
	 * employees.element("fprocid").getText(); String tprocid =
	 * employees.element("tprocid").getText(); String aoption =
	 * employees.element("aoption").getText(); String jpaxml =
	 * employees.element("jpaxml").getText(); fromxml(jpaxml);
	 * 
	 * String rst = wfreject(fprocid, tprocid, aoption).toxml(); return rst;
	 * 
	 * }
	 * 
	 * // WFMain函数调用 public String _breakWF(String xmldata) throws Exception {
	 * Document document = DocumentHelper.parseText(xmldata); Element employees
	 * = document.getRootElement(); String jpaxml =
	 * employees.element("jpaxml").getText(); fromxml(jpaxml); return
	 * wfbreak().toxml(); }
	 * 
	 * // WFMain函数调用 public String _transferWF(String xmldata) throws Exception
	 * { Document document = DocumentHelper.parseText(xmldata); Element
	 * employees = document.getRootElement(); String touserid =
	 * employees.element("touserid").getText(); String aoption =
	 * employees.element("aoption").getText(); String jpaxml =
	 * employees.element("jpaxml").getText(); fromxml(jpaxml); // // return
	 * wftransfer(touserid, aoption).toxml(); }
	 */
}
