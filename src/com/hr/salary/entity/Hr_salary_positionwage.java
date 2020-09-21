package com.hr.salary.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import com.hr.salary.ctr.CtrHr_salary_positionwage;

import java.sql.Types;

@CEntity(caption="职位工资标准",controller =CtrHr_salary_positionwage.class)
public class Hr_salary_positionwage extends CJPA {
	@CFieldinfo(fieldname="ptwg_id",iskey=true,notnull=true,precision=20,scale=0,caption="职位工资ID",datetype=Types.INTEGER)
	public CField ptwg_id;  //职位工资ID
	@CFieldinfo(fieldname="ptwg_code",codeid=124,notnull=true,precision=16,scale=0,caption="职位工资编码",datetype=Types.VARCHAR)
	public CField ptwg_code;  //职位工资编码
	@CFieldinfo(fieldname="ptwg_name",precision=32,scale=0,caption="职位工资标准名",datetype=Types.VARCHAR)
	public CField ptwg_name;  //职位工资标准名
	@CFieldinfo(fieldname="ospid",precision=20,scale=0,caption="职位ID",datetype=Types.INTEGER)
	public CField ospid;  //职位ID
	@CFieldinfo(fieldname="ospcode",precision=16,scale=0,caption="职位编码",datetype=Types.VARCHAR)
	public CField ospcode;  //职位编码
	@CFieldinfo(fieldname="sp_name",precision=128,scale=0,caption="标准职位名称",datetype=Types.VARCHAR)
	public CField sp_name;  //标准职位名称
	@CFieldinfo(fieldname="lv_id",notnull=true,precision=10,scale=0,caption="职级ID",datetype=Types.INTEGER)
	public CField lv_id;  //职级ID
	@CFieldinfo(fieldname="lv_num",notnull=true,precision=4,scale=1,caption="职级",datetype=Types.DECIMAL)
	public CField lv_num;  //职级
	@CFieldinfo(fieldname="hg_id",notnull=true,precision=10,scale=0,caption="职等ID",datetype=Types.INTEGER)
	public CField hg_id;  //职等ID
	@CFieldinfo(fieldname="hg_name",precision=128,scale=0,caption="职等名称",datetype=Types.VARCHAR)
	public CField hg_name;  //职等名称
	@CFieldinfo(fieldname="hwc_idzl",notnull=true,precision=20,scale=0,caption="职类ID",datetype=Types.INTEGER)
	public CField hwc_idzl;  //职类ID
	@CFieldinfo(fieldname="hwc_namezl",precision=128,scale=0,caption="职类名称",datetype=Types.VARCHAR)
	public CField hwc_namezl;  //职类名称
	@CFieldinfo(fieldname="hwc_idzq",notnull=true,precision=20,scale=0,caption="职群ID",datetype=Types.INTEGER)
	public CField hwc_idzq;  //职群ID
	@CFieldinfo(fieldname="hwc_namezq",precision=128,scale=0,caption="职群名称",datetype=Types.VARCHAR)
	public CField hwc_namezq;  //职群名称
	@CFieldinfo(fieldname="hwc_idzz",notnull=true,precision=20,scale=0,caption="职种ID",datetype=Types.INTEGER)
	public CField hwc_idzz;  //职种ID
	@CFieldinfo(fieldname="hwc_namezz",precision=128,scale=0,caption="职种名称",datetype=Types.VARCHAR)
	public CField hwc_namezz;  //职种名称
	@CFieldinfo(fieldname="psl1",precision=5,scale=2,caption="psl1",datetype=Types.DECIMAL)
	public CField psl1;  //L1
	@CFieldinfo(fieldname="psl2",precision=5,scale=2,caption="psl2",datetype=Types.DECIMAL)
	public CField psl2;  //L2
	@CFieldinfo(fieldname="psl3",precision=5,scale=2,caption="psl3",datetype=Types.DECIMAL)
	public CField psl3;  //L3
	@CFieldinfo(fieldname="psl4",precision=5,scale=2,caption="psl4",datetype=Types.DECIMAL)
	public CField psl4;  //L4
	@CFieldinfo(fieldname="psl5",precision=5,scale=2,caption="psl5",datetype=Types.DECIMAL)
	public CField psl5;  //L5
	@CFieldinfo(fieldname="remark",precision=512,scale=0,caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	@CFieldinfo(fieldname="wfid",precision=20,scale=0,caption="wfid",datetype=Types.INTEGER)
	public CField wfid;  //wfid
	@CFieldinfo(fieldname="attid",precision=20,scale=0,caption="attid",datetype=Types.INTEGER)
	public CField attid;  //attid
	@CFieldinfo(fieldname="stat",notnull=true,precision=2,scale=0,caption="流程状态",datetype=Types.INTEGER)
	public CField stat;  //流程状态
	@CFieldinfo(fieldname="entid",notnull=true,precision=5,scale=0,caption="entid",datetype=Types.INTEGER)
	public CField entid;  //entid
	@CFieldinfo(fieldname="idpath",notnull=true,precision=256,scale=0,caption="idpath",datetype=Types.VARCHAR)
	public CField idpath;  //idpath
	@CFieldinfo(fieldname="creator",notnull=true,precision=32,scale=0,caption="创建人",datetype=Types.VARCHAR)
	public CField creator;  //创建人
	@CFieldinfo(fieldname="createtime",notnull=true,precision=19,scale=0,caption="创建时间",datetype=Types.TIMESTAMP)
	public CField createtime;  //创建时间
	@CFieldinfo(fieldname="updator",precision=32,scale=0,caption="更新人",datetype=Types.VARCHAR)
	public CField updator;  //更新人
	@CFieldinfo(fieldname="updatetime",precision=19,scale=0,caption="更新时间",datetype=Types.TIMESTAMP)
	public CField updatetime;  //更新时间
	@CFieldinfo(fieldname="property1",precision=32,scale=0,caption="备用字段1",datetype=Types.VARCHAR)
	public CField property1;  //备用字段1
	@CFieldinfo(fieldname="property2",precision=64,scale=0,caption="备用字段2",datetype=Types.VARCHAR)
	public CField property2;  //备用字段2
	@CFieldinfo(fieldname="property3",precision=32,scale=0,caption="备用字段3",datetype=Types.VARCHAR)
	public CField property3;  //备用字段3
	@CFieldinfo(fieldname="property4",precision=32,scale=0,caption="备用字段4",datetype=Types.VARCHAR)
	public CField property4;  //备用字段4
	@CFieldinfo(fieldname="property5",precision=32,scale=0,caption="备用字段5",datetype=Types.VARCHAR)
	public CField property5;  //备用字段5
	@CFieldinfo(fieldname="usable",notnull=true,precision=1,scale=0,caption="有效",defvalue="1",datetype=Types.INTEGER)
	public CField usable;  //有效
	@CFieldinfo(fieldname="stru_id",precision=20,scale=0,caption="工资结构ID",datetype=Types.BIGINT)
	public CField stru_id;  //工资结构ID
	@CFieldinfo(fieldname="stru_code",precision=64,scale=0,caption="工资结构编码",datetype=Types.VARCHAR)
	public CField stru_code;  //工资结构编码
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hr_salary_positionwage() throws Exception {
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

