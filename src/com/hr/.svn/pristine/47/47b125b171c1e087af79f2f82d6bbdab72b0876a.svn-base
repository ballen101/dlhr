package com.hr.msg.entity;

import java.sql.Types;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;

@CEntity()
public class Wx_msg_send  extends CJPA  {
	@CFieldinfo(fieldname = "id", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField id; // ID
	@CFieldinfo(fieldname="content",iskey=false,notnull=true,caption="内容",datetype=Types.VARCHAR)
	public CField content;  //内容
	@CFieldinfo(fieldname="employee_code",iskey=false,notnull=true,caption="工号",datetype=Types.VARCHAR)
	public CField employee_code;  //工号
	@CFieldinfo(fieldname="send_time",iskey=false,notnull=false,caption="发送时间",datetype=Types.TIMESTAMP)
	public CField send_time;  //发送时间
	@CFieldinfo(fieldname="errcode",iskey=false,notnull=false,caption="错误码",datetype=Types.VARCHAR)
	public CField errcode;  //错误码
	@CFieldinfo(fieldname="errmsg",iskey=false,notnull=false,caption="错误内容",datetype=Types.INTEGER)
	public CField errmsg;  //错误内容

	public Wx_msg_send() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}
}
