package com.corsair.server.wordflow;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

@CEntity()
public class Shwwfproc extends CJPA {
	@CFieldinfo(fieldname = "procid", iskey = true, notnull = true, caption = "", datetype = Types.INTEGER)
	public CField procid; //
	@CFieldinfo(fieldname = "wfid", notnull = true, caption = "", datetype = Types.INTEGER)
	public CField wfid; //
	@CFieldinfo(fieldname = "procname", caption = "", datetype = Types.VARCHAR)
	public CField procname; //
	@CFieldinfo(fieldname = "proctype", caption = "类型", datetype = Types.INTEGER)
	public CField proctype; // 类型 1 普通 4 自动
	@CFieldinfo(fieldname = "operright", caption = "", datetype = Types.INTEGER)
	public CField operright; //
	@CFieldinfo(fieldname = "manager", caption = "", datetype = Types.VARCHAR)
	public CField manager; //
	@CFieldinfo(fieldname = "minperson", caption = "", datetype = Types.INTEGER)
	public CField minperson; //
	@CFieldinfo(fieldname = "allperson", caption = "", datetype = Types.INTEGER)
	public CField allperson; //
	@CFieldinfo(fieldname = "allowemptyuser", notnull = true, caption = "允许空审批人", datetype = Types.INTEGER)
	public CField allowemptyuser; // 允许空审批人
	@CFieldinfo(fieldname = "selectuser", caption = "选择用户", defvalue = "2", datetype = Types.INTEGER)
	public CField selectuser; // 选择用户
	@CFieldinfo(fieldname = "msgusers", caption = "", datetype = Types.VARCHAR)
	public CField msgusers; //
	@CFieldinfo(fieldname = "stat", caption = "1 待审批 2 当前 3 完成", datetype = Types.INTEGER)
	public CField stat; // 1 待审批 2 当前 3 完成
	@CFieldinfo(fieldname = "note", caption = "", datetype = Types.VARCHAR)
	public CField note; //
	@CFieldinfo(fieldname = "frmclassname", caption = "", datetype = Types.VARCHAR)
	public CField frmclassname; //
	@CFieldinfo(fieldname = "submitfunc", caption = "", datetype = Types.VARCHAR)
	public CField submitfunc; //
	@CFieldinfo(fieldname = "rejectfunc", caption = "", datetype = Types.VARCHAR)
	public CField rejectfunc; //
	@CFieldinfo(fieldname = "breakfunc", caption = "", datetype = Types.VARCHAR)
	public CField breakfunc; //
	@CFieldinfo(fieldname = "checkfunc", caption = "", datetype = Types.VARCHAR)
	public CField checkfunc; //
	@CFieldinfo(fieldname = "width", caption = "", datetype = Types.INTEGER)
	public CField width; //
	@CFieldinfo(fieldname = "height", caption = "", datetype = Types.INTEGER)
	public CField height; //
	@CFieldinfo(fieldname = "left", caption = "", datetype = Types.INTEGER)
	public CField left; //
	@CFieldinfo(fieldname = "top", caption = "", datetype = Types.INTEGER)
	public CField top; //
	@CFieldinfo(fieldname = "delaytimes", caption = "", datetype = Types.INTEGER)
	public CField delaytimes; //
	@CFieldinfo(fieldname = "canreject", caption = "", datetype = Types.INTEGER)
	public CField canreject; //
	@CFieldinfo(fieldname = "canbreak", caption = "", datetype = Types.INTEGER)
	public CField canbreak; //
	@CFieldinfo(fieldname = "canmodify", caption = "", datetype = Types.INTEGER)
	public CField canmodify; //
	@CFieldinfo(fieldname = "nextprocs", caption = "", datetype = Types.VARCHAR)
	public CField nextprocs; //
	@CFieldinfo(fieldname = "proctempid", notnull = true, caption = "", datetype = Types.INTEGER)
	public CField proctempid; //
	@CFieldinfo(fieldname = "property1", caption = "", datetype = Types.VARCHAR)
	public CField property1; //
	@CFieldinfo(fieldname = "property2", caption = "", datetype = Types.VARCHAR)
	public CField property2; //
	@CFieldinfo(fieldname = "property3", caption = "", datetype = Types.VARCHAR)
	public CField property3; //
	@CFieldinfo(fieldname = "property4", caption = "", datetype = Types.VARCHAR)
	public CField property4; //
	@CFieldinfo(fieldname = "property5", caption = "", datetype = Types.VARCHAR)
	public CField property5; //
	@CFieldinfo(fieldname = "isbegin", caption = "", datetype = Types.INTEGER)
	public CField isbegin; //
	@CFieldinfo(fieldname = "isend", caption = "", datetype = Types.INTEGER)
	public CField isend; //
	@CFieldinfo(fieldname = "arivetime", caption = "到达时间", datetype = Types.TIMESTAMP)
	public CField arivetime; // 到达时间
	@CFieldinfo(fieldname = "formurl", caption = "窗体路径", datetype = Types.VARCHAR)
	public CField formurl; // 窗体路径
	@CFieldinfo(fieldname = "clockdata", caption = "锁定数据", datetype = Types.INTEGER)
	public CField clockdata; // 锁定数据
	@CFieldinfo(fieldname = "caneditlinedata", caption = "可以编辑明细", datetype = Types.INTEGER)
	public CField caneditlinedata; // 可以编辑明细
	@CFieldinfo(fieldname = "livetime", caption = "离开节点时间", datetype = Types.TIMESTAMP)
	public CField livetime; // 离开节点时间
	@CFieldinfo(fieldname = "timeout", caption = "节点超时", datetype = Types.INTEGER)
	public CField timeout; // 节点超时
	@CFieldinfo(fieldname = "itmeoutmsg", caption = "节点超时发送的提醒消息，空不处理", datetype = Types.VARCHAR)
	public CField itmeoutmsg; // 节点超时发送的提醒消息，空不处理
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量
	@CLinkFieldInfo(jpaclass = Shwwfproccondition.class, linkFields = { @LinkFieldItem(lfield = "wfid", mfield = "wfid"), @LinkFieldItem(lfield = "procid", mfield = "procid") })
	public CJPALineData<Shwwfproccondition> shwwfprocconditions;
	@CLinkFieldInfo(jpaclass = Shwwfprocuser.class, linkFields = { @LinkFieldItem(lfield = "wfid", mfield = "wfid"), @LinkFieldItem(lfield = "procid", mfield = "procid") })
	public CJPALineData<Shwwfprocuser> shwwfprocusers;
	// @CLinkFieldInfo(jpaclass = Shwwfproclog.class, linkFields = { @LinkFieldItem(lfield = "wfid", mfield = "wfid"), @LinkFieldItem(lfield = "procid", mfield
	// = "procid") })
	// public CJPALineData<Shwwfproclog> shwwfproclogs;

	// 自关联数据定义

	public Shwwfproc() throws Exception {
	}

	public boolean InitObject() {// 类初始化调用的方法
		super.InitObject();
		/*
		 * shwwfprocconditions.addLinkField("wfid", "wfid"); shwwfprocconditions.addLinkField("procid", "procid"); shwwfprocusers.addLinkField("wfid", "wfid");
		 * shwwfprocusers.addLinkField("procid", "procid"); shwwfproclogs.addLinkField("wfid", "wfid"); shwwfproclogs.addLinkField("procid", "procid");
		 */
		// shwwfproclogs.setLazy(true);
		return true;
	}

	public boolean FinalObject() { // 类释放前调用的方法
		super.FinalObject();
		return true;
	}

}
