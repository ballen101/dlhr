package com.corsair.server.weixin.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Wx_user extends CJPA {
	@CFieldinfo(fieldname = "wxuserid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField wxuserid; // ID
	@CFieldinfo(fieldname = "appid", precision = 64, scale = 0, caption = "wxappid", datetype = Types.VARCHAR)
	public CField appid; // wxappid
	@CFieldinfo(fieldname = "subscribe", caption = "关注状态", datetype = Types.INTEGER)
	public CField subscribe; // 关注状态
	@CFieldinfo(fieldname = "openid", caption = "openid", datetype = Types.VARCHAR)
	public CField openid; // openid
	@CFieldinfo(fieldname = "nickname", caption = "nickname", datetype = Types.VARCHAR)
	public CField nickname; // nickname
	@CFieldinfo(fieldname = "b64nickname", caption = "base64昵称", datetype = Types.VARCHAR)
	public CField b64nickname; // base64昵称
	@CFieldinfo(fieldname = "sex", caption = "性别 1 男 2女 3 不男不女", datetype = Types.INTEGER)
	public CField sex; // 性别 1 男 2女 3 不男不女
	@CFieldinfo(fieldname = "city", caption = "城市", datetype = Types.VARCHAR)
	public CField city; // 城市
	@CFieldinfo(fieldname = "province", caption = "省份", datetype = Types.VARCHAR)
	public CField province; // 省份
	@CFieldinfo(fieldname = "country", caption = "国家", datetype = Types.VARCHAR)
	public CField country; // 国家
	@CFieldinfo(fieldname = "language", caption = "语言", datetype = Types.VARCHAR)
	public CField language; // 语言
	@CFieldinfo(fieldname = "headimgurl", caption = "头像", datetype = Types.VARCHAR)
	public CField headimgurl; // 头像
	@CFieldinfo(fieldname = "subscribe_time", caption = "关注时间", datetype = Types.INTEGER)
	public CField subscribe_time; // 关注时间
	@CFieldinfo(fieldname = "unionid", caption = "统一ID", datetype = Types.VARCHAR)
	public CField unionid; // 统一ID
	@CFieldinfo(fieldname = "remark", precision = 64, scale = 0, caption = "公众号运营者对粉丝的备注", datetype = Types.VARCHAR)
	public CField remark; // 公众号运营者对粉丝的备注
	@CFieldinfo(fieldname = "subscribe_scene", precision = 32, scale = 0, caption = "返回用户关注的渠道来源", datetype = Types.VARCHAR)
	public CField subscribe_scene;
	// 返回用户关注的渠道来源，ADD_SCENE_SEARCH 公众号搜索，ADD_SCENE_ACCOUNT_MIGRATION 公众号迁移，ADD_SCENE_PROFILE_CARD 名片分享，ADD_SCENE_QR_CODE
	// 扫描二维码，ADD_SCENEPROFILE LINK 图文页内名称点击，ADD_SCENE_PROFILE_ITEM 图文页右上角菜单，ADD_SCENE_PAID 支付后关注，ADD_SCENE_OTHERS 其他
	@CFieldinfo(fieldname = "qr_scene", precision = 16, scale = 0, caption = "二维码扫码场景（开发者自定义）", datetype = Types.VARCHAR)
	public CField qr_scene; // 二维码扫码场景（开发者自定义）
	@CFieldinfo(fieldname = "qr_scene_str", precision = 128, scale = 0, caption = "二维码扫码场景描述（开发者自定义）", datetype = Types.VARCHAR)
	public CField qr_scene_str; // 二维码扫码场景描述（开发者自定义）
	@CFieldinfo(fieldname = "update_time", caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField update_time; // 更新时间
	@CFieldinfo(fieldname = "mobil", caption = "mobil", datetype = Types.VARCHAR)
	public CField mobil; // mobil
	@CFieldinfo(fieldname = "name", caption = "用户姓名", datetype = Types.VARCHAR)
	public CField name; // 用户姓名
	@CFieldinfo(fieldname = "addr", caption = "地址", datetype = Types.VARCHAR)
	public CField addr; // 地址
	@CFieldinfo(fieldname = "privilege", caption = "特权", datetype = Types.VARCHAR)
	public CField privilege; // 特权
	@CFieldinfo(fieldname = "inickname", caption = "ICEFALL昵称", datetype = Types.VARCHAR)
	public CField inickname; // ICEFALL昵称
	@CFieldinfo(fieldname = "iheadimgurl", caption = "ICEFALL头像", datetype = Types.VARCHAR)
	public CField iheadimgurl; // ICEFALL头像
	@CFieldinfo(fieldname = "isignature", caption = "ICEFALL个性签名", datetype = Types.VARCHAR)
	public CField isignature; // ICEFALL个性签名
	@CFieldinfo(fieldname = "itruename", caption = "真实姓名", datetype = Types.VARCHAR)
	public CField itruename; // 真实姓名
	@CFieldinfo(fieldname = "userid", caption = "系统登录用户", datetype = Types.INTEGER)
	public CField userid; // 系统登录用户
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Wx_user() throws Exception {
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
