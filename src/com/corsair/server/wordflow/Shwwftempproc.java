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
public class Shwwftempproc extends CJPA {
	@CFieldinfo(fieldname = "wftempid", notnull = true, caption = "节点ID", datetype = Types.INTEGER)
	public CField wftempid; // 节点ID
	@CFieldinfo(fieldname = "proctempid", iskey = true, notnull = true, caption = "模板ID", datetype = Types.INTEGER)
	public CField proctempid; // 模板ID
	@CFieldinfo(fieldname = "proctempname", caption = "模板名称", datetype = Types.VARCHAR)
	public CField proctempname; // 模板名称
	@CFieldinfo(fieldname = "proctype", caption = "节点类型", datetype = Types.INTEGER)
	public CField proctype; // 节点类型
	@CFieldinfo(fieldname = "procoption", caption = "自动值", datetype = Types.VARCHAR)
	public CField procoption; // 自动值
	@CFieldinfo(fieldname = "operright", caption = "", datetype = Types.INTEGER)
	public CField operright; //
	@CFieldinfo(fieldname = "manager", caption = "管理员", datetype = Types.VARCHAR)
	public CField manager; // 管理员
	@CFieldinfo(fieldname = "minperson", caption = "最小人数", datetype = Types.INTEGER)
	public CField minperson; // 最小人数
	@CFieldinfo(fieldname = "allperson", caption = "所有人", datetype = Types.INTEGER)
	public CField allperson; // 所有人
	@CFieldinfo(fieldname = "allowemptyuser", notnull = true, caption = "允许空审批人", datetype = Types.INTEGER)
	public CField allowemptyuser; // 允许空审批人
	@CFieldinfo(fieldname="selectuser",caption="选择用户",defvalue="2",datetype=Types.INTEGER)
	public CField selectuser;  //选择用户
	@CFieldinfo(fieldname = "msgusers", caption = "", datetype = Types.VARCHAR)
	public CField msgusers; //
	@CFieldinfo(fieldname = "stat", caption = "状态", datetype = Types.INTEGER)
	public CField stat; // 状态
	@CFieldinfo(fieldname = "frmclassname", caption = "", datetype = Types.VARCHAR)
	public CField frmclassname; //
	@CFieldinfo(fieldname = "note", caption = "", datetype = Types.VARCHAR)
	public CField note; //
	@CFieldinfo(fieldname = "submitfunc", caption = "提交函数", datetype = Types.VARCHAR)
	public CField submitfunc; // 提交函数
	@CFieldinfo(fieldname = "rejectfunc", caption = "驳回函数", datetype = Types.VARCHAR)
	public CField rejectfunc; // 驳回函数
	@CFieldinfo(fieldname = "breakfunc", caption = "中断函数", datetype = Types.VARCHAR)
	public CField breakfunc; // 中断函数
	@CFieldinfo(fieldname = "checkfunc", caption = "检查函数", datetype = Types.VARCHAR)
	public CField checkfunc; // 检查函数
	@CFieldinfo(fieldname = "width", caption = "位置1", datetype = Types.INTEGER)
	public CField width; // 位置1
	@CFieldinfo(fieldname = "height", caption = "位置2", datetype = Types.INTEGER)
	public CField height; // 位置2
	@CFieldinfo(fieldname = "left", caption = "位置3", datetype = Types.INTEGER)
	public CField left; // 位置3
	@CFieldinfo(fieldname = "top", caption = "位置4", datetype = Types.INTEGER)
	public CField top; // 位置4
	@CFieldinfo(fieldname = "delaytimes", caption = "", datetype = Types.INTEGER)
	public CField delaytimes; //
	@CFieldinfo(fieldname = "canreject", caption = "允许驳回", datetype = Types.INTEGER)
	public CField canreject; // 允许驳回
	@CFieldinfo(fieldname = "canbreak", caption = "允许中断", datetype = Types.INTEGER)
	public CField canbreak; // 允许中断
	@CFieldinfo(fieldname = "canmodify", caption = "允许编辑", datetype = Types.INTEGER)
	public CField canmodify; // 允许编辑
	@CFieldinfo(fieldname = "nextprocs", caption = "", datetype = Types.VARCHAR)
	public CField nextprocs; //
	@CFieldinfo(fieldname = "isbegin", caption = "是否开始节点", datetype = Types.INTEGER)
	public CField isbegin; // 是否开始节点
	@CFieldinfo(fieldname = "isend", caption = "是否结束节点", datetype = Types.INTEGER)
	public CField isend; // 是否结束节点
	@CFieldinfo(fieldname = "formurl", caption = "页面路径", datetype = Types.VARCHAR)
	public CField formurl; // 页面路径
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
	@CLinkFieldInfo(jpaclass = Shwwftempproccondition.class, linkFields = {
			@LinkFieldItem(lfield = "proctempid", mfield = "proctempid"),
			@LinkFieldItem(lfield = "wftempid", mfield = "wftempid") })
	public CJPALineData<Shwwftempproccondition> shwwftempprocconditions; // 提交前检查条件
	@CLinkFieldInfo(jpaclass = Shwwftempprocuser.class, linkFields = {
			@LinkFieldItem(lfield = "proctempid", mfield = "proctempid"),
			@LinkFieldItem(lfield = "wftempid", mfield = "wftempid") })
	public CJPALineData<Shwwftempprocuser> shwwftempprocusers; // 参与节点的用户

	// 自关联数据定义
	public Shwwftempproc() throws Exception {
	}

	public boolean InitObject() {// 类初始化调用的方法
		super.InitObject();
		// shwwftempprocconditions.addLinkField("proctempid", "proctempid");
		// shwwftempprocconditions.addLinkField("wftempid", "wftempid");
		// shwwftempprocusers.addLinkField("proctempid", "proctempid");
		// shwwftempprocusers.addLinkField("wftempid", "wftempid");
		return true;
	}

	public boolean FinalObject() { // 类释放前调用的方法
		super.FinalObject();
		return true;
	}
}