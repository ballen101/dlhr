package com.corsair.server.weixin.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption = "微信参数二维码")
public class Shwwxappqrcode extends CJPA {
	@CFieldinfo(fieldname = "qcid", iskey = true, notnull = true, precision = 10, scale = 0, caption = "平台ID", datetype = Types.INTEGER)
	public CField qcid; // 平台ID
	@CFieldinfo(fieldname = "appid", notnull = true, precision = 10, scale = 0, caption = "平台appid", datetype = Types.INTEGER)
	public CField appid; // 平台appid
	@CFieldinfo(fieldname="expiretime",notnull=true,precision=11,scale=0,caption="到期时间 linux时间戳 /1000",datetype=Types.INTEGER)
	public CField expiretime;  //到期时间 linux时间戳 /1000   0表示永久
	@CFieldinfo(fieldname = "ticket", notnull = true, precision = 128, scale = 0, caption = "获取的二维码ticket https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=TICKET", datetype = Types.VARCHAR)
	public CField ticket; // 获取的二维码ticket https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=TICKET
	@CFieldinfo(fieldname = "url", precision = 256, scale = 0, caption = "二维码字符串", datetype = Types.VARCHAR)
	public CField url; // 二维码字符串
	@CFieldinfo(fieldname = "scene_id", precision = 10, scale = 0, caption = "场景值ID", datetype = Types.INTEGER)
	public CField scene_id; // 场景值ID
	@CFieldinfo(fieldname = "scene_str", precision = 64, scale = 0, caption = "字符串形式的ID", datetype = Types.VARCHAR)
	public CField scene_str; // 字符串形式的ID
	@CFieldinfo(fieldname = "remark", precision = 32, scale = 0, caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Shwwxappqrcode() throws Exception {
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
