package com.hr.canteen.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption="消费记录统计明细")
public class Hr_canteen_costrecordscount extends CJPA {
	@CFieldinfo(fieldname="corec_id",iskey=true,notnull=true,precision=20,scale=0,caption="ID",datetype=Types.BIGINT)
	public CField corec_id;  //ID
	@CFieldinfo(fieldname="cardnumber",notnull=true,precision=64,scale=0,caption="卡号",datetype=Types.VARCHAR)
	public CField cardnumber;  //卡号
	@CFieldinfo(fieldname="er_id",precision=10,scale=0,caption="档案ID",datetype=Types.INTEGER)
	public CField er_id;  //档案ID
	@CFieldinfo(fieldname="employee_code",precision=16,scale=0,caption="工号",datetype=Types.VARCHAR)
	public CField employee_code;  //工号
	@CFieldinfo(fieldname="employee_name",precision=64,scale=0,caption="姓名",datetype=Types.VARCHAR)
	public CField employee_name;  //姓名
	@CFieldinfo(fieldname="orgid",notnull=true,precision=10,scale=0,caption="部门ID",datetype=Types.INTEGER)
	public CField orgid;  //部门ID
	@CFieldinfo(fieldname="orgcode",notnull=true,precision=16,scale=0,caption="部门编码",datetype=Types.VARCHAR)
	public CField orgcode;  //部门编码
	@CFieldinfo(fieldname="orgname",notnull=true,precision=128,scale=0,caption="部门名称",datetype=Types.VARCHAR)
	public CField orgname;  //部门名称
	@CFieldinfo(fieldname="ospid",precision=20,scale=0,caption="职位ID",datetype=Types.INTEGER)
	public CField ospid;  //职位ID
	@CFieldinfo(fieldname="ospcode",precision=16,scale=0,caption="职位编码",datetype=Types.VARCHAR)
	public CField ospcode;  //职位编码
	@CFieldinfo(fieldname="sp_name",precision=128,scale=0,caption="职位名称",datetype=Types.VARCHAR)
	public CField sp_name;  //职位名称
	@CFieldinfo(fieldname="lv_id",precision=10,scale=0,caption="职级ID",datetype=Types.INTEGER)
	public CField lv_id;  //职级ID
	@CFieldinfo(fieldname="lv_num",precision=4,scale=1,caption="职级",datetype=Types.DECIMAL)
	public CField lv_num;  //职级
	@CFieldinfo(fieldname="ctr_id",notnull=true,precision=20,scale=0,caption="用餐餐厅ID",datetype=Types.INTEGER)
	public CField ctr_id;  //用餐餐厅ID
	@CFieldinfo(fieldname="ctr_code",notnull=true,precision=16,scale=0,caption="用餐餐厅编码",datetype=Types.VARCHAR)
	public CField ctr_code;  //用餐餐厅编码
	@CFieldinfo(fieldname="ctr_name",notnull=true,precision=64,scale=0,caption="用餐餐厅名称",datetype=Types.VARCHAR)
	public CField ctr_name;  //用餐餐厅名称
	@CFieldinfo(fieldname="ctcr_id",notnull=true,precision=20,scale=0,caption="卡机ID",datetype=Types.INTEGER)
	public CField ctcr_id;  //卡机ID
	@CFieldinfo(fieldname="ctcr_code",notnull=true,precision=16,scale=0,caption="卡机编号",datetype=Types.VARCHAR)
	public CField ctcr_code;  //卡机编号
	@CFieldinfo(fieldname="ctcr_name",notnull=true,precision=64,scale=0,caption="名称",datetype=Types.VARCHAR)
	public CField ctcr_name;  //名称
	@CFieldinfo(fieldname="costtime",precision=19,scale=0,caption="刷卡时间",datetype=Types.TIMESTAMP)
	public CField costtime;  //刷卡时间
	@CFieldinfo(fieldname="mc_id",precision=20,scale=0,caption="餐类ID",datetype=Types.INTEGER)
	public CField mc_id;  //餐类ID
	@CFieldinfo(fieldname="mc_name",precision=64,scale=0,caption="餐类名称",datetype=Types.VARCHAR)
	public CField mc_name;  //餐类名称
	@CFieldinfo(fieldname="cost",precision=4,scale=2,caption="消费金额",datetype=Types.DECIMAL)
	public CField cost;  //消费金额
	@CFieldinfo(fieldname="remark",precision=512,scale=0,caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	@CFieldinfo(fieldname="entid",notnull=true,precision=5,scale=0,caption="entid",datetype=Types.INTEGER)
	public CField entid;  //entid
	@CFieldinfo(fieldname="creator",notnull=true,precision=32,scale=0,caption="创建人",datetype=Types.VARCHAR)
	public CField creator;  //创建人
	@CFieldinfo(fieldname="createtime",notnull=true,precision=19,scale=0,caption="创建时间",datetype=Types.TIMESTAMP)
	public CField createtime;  //创建时间
	@CFieldinfo(fieldname="updator",precision=32,scale=0,caption="更新人",datetype=Types.VARCHAR)
	public CField updator;  //更新人
	@CFieldinfo(fieldname="updatetime",precision=19,scale=0,caption="更新时间",datetype=Types.TIMESTAMP)
	public CField updatetime;  //更新时间
	@CFieldinfo(fieldname="attribute1",precision=32,scale=0,caption="备用字段1",datetype=Types.VARCHAR)
	public CField attribute1;  //备用字段1
	@CFieldinfo(fieldname="attribute2",precision=32,scale=0,caption="备用字段2",datetype=Types.VARCHAR)
	public CField attribute2;  //备用字段2
	@CFieldinfo(fieldname="attribute3",precision=32,scale=0,caption="备用字段3",datetype=Types.VARCHAR)
	public CField attribute3;  //备用字段3
	@CFieldinfo(fieldname="attribute4",precision=32,scale=0,caption="备用字段4",datetype=Types.VARCHAR)
	public CField attribute4;  //备用字段4
	@CFieldinfo(fieldname="attribute5",precision=32,scale=0,caption="备用字段5",datetype=Types.VARCHAR)
	public CField attribute5;  //备用字段5
	@CFieldinfo(fieldname="idpath",notnull=true,precision=256,scale=0,caption="idpath",datetype=Types.VARCHAR)
	public CField idpath;  //idpath
	@CFieldinfo(fieldname="inporttime",precision=19,scale=0,caption="导入时间",datetype=Types.TIMESTAMP)
	public CField inporttime;  //导入时间
	@CFieldinfo(fieldname="synid",caption="关联ID",datetype=Types.INTEGER)
	public CField synid;  //关联ID
	@CFieldinfo(fieldname="zaonum",precision=2,scale=0,caption="早餐数",defvalue="0",datetype=Types.INTEGER)
	public CField zaonum;  //早餐数
	@CFieldinfo(fieldname="wunum",precision=2,scale=0,caption="中餐数",defvalue="0",datetype=Types.INTEGER)
	public CField wunum;  //中餐数
	@CFieldinfo(fieldname="wannum",precision=2,scale=0,caption="晚餐数",defvalue="0",datetype=Types.INTEGER)
	public CField wannum;  //晚餐数
	@CFieldinfo(fieldname="yenum",precision=2,scale=0,caption="夜宵数",defvalue="0",datetype=Types.INTEGER)
	public CField yenum;  //夜宵数
	@CFieldinfo(fieldname="wunum5",precision=2,scale=0,caption="5.8中餐数",defvalue="0",datetype=Types.INTEGER)
	public CField wunum5;  //5.8中餐数
	@CFieldinfo(fieldname="wunum7",precision=2,scale=0,caption="7.8中餐数",defvalue="0",datetype=Types.INTEGER)
	public CField wunum7;  //7.8中餐数
	@CFieldinfo(fieldname="wannum5",precision=2,scale=0,caption="5.8晚餐数",defvalue="0",datetype=Types.INTEGER)
	public CField wannum5;  //5.8晚餐数
	@CFieldinfo(fieldname="wannum7",precision=2,scale=0,caption="7.8晚餐数",defvalue="0",datetype=Types.INTEGER)
	public CField wannum7;  //7.8晚餐数
	@CFieldinfo(fieldname="zaopay",precision=8,scale=1,caption="早餐费",defvalue="0.0",datetype=Types.DECIMAL)
	public CField zaopay;  //早餐费
	@CFieldinfo(fieldname="wupay",precision=8,scale=1,caption="中餐费",defvalue="0.0",datetype=Types.DECIMAL)
	public CField wupay;  //中餐费
	@CFieldinfo(fieldname="wanpay",precision=8,scale=1,caption="晚餐费",defvalue="0.0",datetype=Types.DECIMAL)
	public CField wanpay;  //晚餐费
	@CFieldinfo(fieldname="yepay",precision=8,scale=1,caption="夜宵费",defvalue="0.0",datetype=Types.DECIMAL)
	public CField yepay;  //夜宵费
	@CFieldinfo(fieldname="wupay5",precision=8,scale=1,caption="5.8中餐费",defvalue="0.0",datetype=Types.DECIMAL)
	public CField wupay5;  //5.8中餐费
	@CFieldinfo(fieldname="wupay7",precision=8,scale=1,caption="7.8中餐费",defvalue="0.0",datetype=Types.DECIMAL)
	public CField wupay7;  //7.8中餐费
	@CFieldinfo(fieldname="wanpay5",precision=8,scale=1,caption="5.8晚餐费",defvalue="0.0",datetype=Types.DECIMAL)
	public CField wanpay5;  //5.8晚餐费
	@CFieldinfo(fieldname="wanpay7",precision=8,scale=1,caption="7.8晚餐费",defvalue="0.0",datetype=Types.DECIMAL)
	public CField wanpay7;  //7.8晚餐费
	@CFieldinfo(fieldname="zaosub",precision=8,scale=1,caption="早餐补贴",defvalue="0.0",datetype=Types.DECIMAL)
	public CField zaosub;  //早餐补贴
	@CFieldinfo(fieldname="wusub",precision=8,scale=1,caption="早餐补贴",defvalue="0.0",datetype=Types.DECIMAL)
	public CField wusub;  //早餐补贴
	@CFieldinfo(fieldname="wansub",precision=8,scale=1,caption="早餐补贴",defvalue="0.0",datetype=Types.DECIMAL)
	public CField wansub;  //早餐补贴
	@CFieldinfo(fieldname="yesub",precision=8,scale=1,caption="早餐补贴",defvalue="0.0",datetype=Types.DECIMAL)
	public CField yesub;  //早餐补贴
	@CFieldinfo(fieldname="wuwannum5",precision=2,scale=0,caption="5.8中晚餐数",defvalue="0",datetype=Types.INTEGER)
	public CField wuwannum5;  //5.8中晚餐数
	@CFieldinfo(fieldname="wuwannum7",precision=2,scale=0,caption="7.8中晚餐数",defvalue="0",datetype=Types.INTEGER)
	public CField wuwannum7;  //7.8中晚餐数
	@CFieldinfo(fieldname="totalpay",precision=8,scale=1,caption="总餐费",defvalue="0.0",datetype=Types.DECIMAL)
	public CField totalpay;  //总餐费
	@CFieldinfo(fieldname="totalsub",precision=8,scale=1,caption="总补贴",defvalue="0.0",datetype=Types.DECIMAL)
	public CField totalsub;  //总补贴
	@CFieldinfo(fieldname="issub",precision=1,scale=0,caption="是否补贴",defvalue="1",datetype=Types.INTEGER)
	public CField issub;  //是否补贴
	@CFieldinfo(fieldname="sublimit",precision=4,scale=0,caption="补贴餐数",datetype=Types.INTEGER)
	public CField sublimit;  //补贴餐数
	@CFieldinfo(fieldname ="classtype",precision=1,scale=0,caption="餐类类型",datetype=Types.INTEGER)
	public CField classtype;  //餐类类型
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hr_canteen_costrecordscount() throws Exception {
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

