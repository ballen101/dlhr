package com.hr.canteen.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.canteen.ctr.CtrHr_canteen_room;

import java.sql.Types;

@CEntity(caption="餐厅设置",controller =CtrHr_canteen_room.class)
public class Hr_canteen_room extends CJPA {
	@CFieldinfo(fieldname="ctr_id",iskey=true,notnull=true,caption="餐厅ID",datetype=Types.INTEGER)
	public CField ctr_id;  //餐厅ID
	@CFieldinfo(fieldname="ctr_code",codeid=97,notnull=true,caption="餐厅编码",datetype=Types.VARCHAR)
	public CField ctr_code;  //餐厅编码
	@CFieldinfo(fieldname="ctr_name",notnull=true,caption="餐厅名称",datetype=Types.VARCHAR)
	public CField ctr_name;  //餐厅名称
	@CFieldinfo(fieldname="address",caption="地点",datetype=Types.VARCHAR)
	public CField address;  //地点
	@CFieldinfo(fieldname="supplier",caption="供应商",datetype=Types.VARCHAR)
	public CField supplier;  //供应商
	@CFieldinfo(fieldname="contact",caption="联系人",datetype=Types.VARCHAR)
	public CField contact;  //联系人
	@CFieldinfo(fieldname="cellphone",caption="固定电话",datetype=Types.VARCHAR)
	public CField cellphone;  //固定电话
	@CFieldinfo(fieldname="cttype",notnull=true,caption="1:自有 2：外包 性质",datetype=Types.VARCHAR)
	public CField cttype;  //1:自有 2：外包 性质
	@CFieldinfo(fieldname="qualification",caption="公司资质",datetype=Types.VARCHAR)
	public CField qualification;  //公司资质
	@CFieldinfo(fieldname="legalperson",caption="法人",datetype=Types.VARCHAR)
	public CField legalperson;  //法人
	@CFieldinfo(fieldname="hygienelicense",caption="1:有 2：无 卫生许可",defvalue="1",datetype=Types.INTEGER)
	public CField hygienelicense;  //1:有 2：无 卫生许可
	@CFieldinfo(fieldname="licensebg",caption="许可证有效期开始时间",datetype=Types.TIMESTAMP)
	public CField licensebg;  //许可证有效期开始时间
	@CFieldinfo(fieldname="licenseed",caption="许可证有效期截止时间",datetype=Types.TIMESTAMP)
	public CField licenseed;  //许可证有效期截止时间
	@CFieldinfo(fieldname="remark",caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	@CFieldinfo(fieldname="wfid",caption="wfid",datetype=Types.INTEGER)
	public CField wfid;  //wfid
	@CFieldinfo(fieldname="attid",caption="attid",datetype=Types.INTEGER)
	public CField attid;  //attid
	@CFieldinfo(fieldname="stat",notnull=true,caption="流程状态",datetype=Types.INTEGER)
	public CField stat;  //流程状态
	@CFieldinfo(fieldname="idpath",notnull=true,caption="idpath",datetype=Types.VARCHAR)
	public CField idpath;  //idpath
	@CFieldinfo(fieldname="entid",notnull=true,caption="entid",datetype=Types.INTEGER)
	public CField entid;  //entid
	@CFieldinfo(fieldname="creator",notnull=true,caption="创建人",datetype=Types.VARCHAR)
	public CField creator;  //创建人
	@CFieldinfo(fieldname="createtime",notnull=true,caption="创建时间",datetype=Types.TIMESTAMP)
	public CField createtime;  //创建时间
	@CFieldinfo(fieldname="updator",caption="更新人",datetype=Types.VARCHAR)
	public CField updator;  //更新人
	@CFieldinfo(fieldname="updatetime",caption="更新时间",datetype=Types.TIMESTAMP)
	public CField updatetime;  //更新时间
	@CFieldinfo(fieldname="attribute1",caption="备用字段1",datetype=Types.VARCHAR)
	public CField attribute1;  //备用字段1
	@CFieldinfo(fieldname="attribute2",caption="备用字段2",datetype=Types.VARCHAR)
	public CField attribute2;  //备用字段2
	@CFieldinfo(fieldname="attribute3",caption="备用字段3",datetype=Types.VARCHAR)
	public CField attribute3;  //备用字段3
	@CFieldinfo(fieldname="attribute4",caption="备用字段4",datetype=Types.VARCHAR)
	public CField attribute4;  //备用字段4
	@CFieldinfo(fieldname="attribute5",caption="备用字段5",datetype=Types.VARCHAR)
	public CField attribute5;  //备用字段5
	@CFieldinfo(fieldname="sync_sn",caption="关联id",datetype=Types.VARCHAR)
	public CField sync_sn;  //关联id
	@CFieldinfo(fieldname="mobile",caption="手机",datetype=Types.VARCHAR)
	public CField mobile;  //固定电话
        @CFieldinfo(fieldname="mbt1",caption="早餐开始时间",datetype=Types.VARCHAR)
	public CField mbt1;  //早餐开始时间
	@CFieldinfo(fieldname="met1",caption="早餐结束时间",datetype=Types.VARCHAR)
	public CField met1;  //早餐结束时间
	@CFieldinfo(fieldname="mbt2",caption="中餐开始时间",datetype=Types.VARCHAR)
	public CField mbt2;  //中餐开始时间
	@CFieldinfo(fieldname="met2",caption="中餐结束时间",datetype=Types.VARCHAR)
	public CField met2;  //中餐结束时间
	@CFieldinfo(fieldname="mbt3",caption="晚餐开始时间",datetype=Types.VARCHAR)
	public CField mbt3;  //晚餐开始时间
	@CFieldinfo(fieldname="met3",caption="晚餐结束时间",datetype=Types.VARCHAR)
	public CField met3;  //晚餐结束时间
	@CFieldinfo(fieldname="mbt4",caption="宵夜开始时间",datetype=Types.VARCHAR)
	public CField mbt4;  //宵夜开始时间
	@CFieldinfo(fieldname="met4",caption="宵夜结束时间",datetype=Types.VARCHAR)
	public CField met4;  //宵夜结束时间
	@CFieldinfo(fieldname="usable",precision=1,scale=0,caption="是否可用",datetype=Types.INTEGER)
	public CField usable;  //是否可用
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

	// 自关联数据定义
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hr_canteen_room() throws Exception {
	}

	@Override
	public boolean InitObject() {//类初始化调用的方法
		super.InitObject();
		return true;
	} 

	@Override
	public boolean FinalObject() { //类释放前调用的方法
		super.FinalObject(); 
		return true; 
	} 
} 
