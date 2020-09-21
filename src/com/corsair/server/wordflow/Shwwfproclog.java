package com.corsair.server.wordflow;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

import java.sql.Types;

/**
 * 流程日志，只与流程关联，取消与节点的关联
 * 
 * @author Administrator
 *
 */
@CEntity()
public class Shwwfproclog extends CJPA {
	@CFieldinfo(fieldname = "wflid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField wflid; // ID
	@CFieldinfo(fieldname = "objecttype", caption = "", datetype = Types.INTEGER)
	public CField objecttype; //
	@CFieldinfo(fieldname = "objectname", caption = "", datetype = Types.VARCHAR)
	public CField objectname; //
	@CFieldinfo(fieldname = "objectdisname", caption = "", datetype = Types.VARCHAR)
	public CField objectdisname; //
	@CFieldinfo(fieldname = "wfid", caption = "流程ID", datetype = Types.INTEGER)
	public CField wfid; // 流程ID
	@CFieldinfo(fieldname = "wfname", caption = "流程名", datetype = Types.VARCHAR)
	public CField wfname; // 流程名
	@CFieldinfo(fieldname = "procid", caption = "节点ID", datetype = Types.INTEGER)
	public CField procid; // 节点ID
	@CFieldinfo(fieldname = "userid", caption = "用户ID", datetype = Types.VARCHAR)
	public CField userid; // 用户ID
	@CFieldinfo(fieldname = "username", caption = "用户名", datetype = Types.VARCHAR)
	public CField username; // 用户名
	@CFieldinfo(fieldname = "displayname", caption = "显示名称", datetype = Types.VARCHAR)
	public CField displayname; // 显示名称
	@CFieldinfo(fieldname = "opinion", caption = "内容", datetype = Types.VARCHAR)
	public CField opinion; // 内容
	@CFieldinfo(fieldname = "actiontype", caption = "操作类型 提交 驳回 中断 转办", datetype = Types.VARCHAR)
	public CField actiontype; // 操作类型 提交 驳回 中断 转办 完成 起草
	@CFieldinfo(fieldname = "actiontime", caption = "操作时间", datetype = Types.TIMESTAMP)
	public CField actiontime; // 操作时间
	@CFieldinfo(fieldname = "arivetime", caption = "到达时间", datetype = Types.TIMESTAMP)
	public CField arivetime; // 到达时间
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
	@CFieldinfo(fieldname = "objectid", caption = "某个节点允许上传附件？？", datetype = Types.INTEGER)
	public CField objectid; // 某个节点允许上传附件？？
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwwfproclog() throws Exception {
	}

	@Override
	public boolean InitObject() {// 类初始化调用的方法
		super.InitObject();
		return true;
	}

	@Override
	public boolean FinalObject() { // 类释放前调用的方法
		super.FinalObject();
		return true;
	}
}
