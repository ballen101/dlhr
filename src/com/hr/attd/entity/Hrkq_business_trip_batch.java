package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.attd.ctr.CtrHrkq_business_trip_batch;

import java.sql.Types;

@CEntity(caption = "批量出差申请", controller = CtrHrkq_business_trip_batch.class, tablename = "Hrkq_business_trip_batch")
public class Hrkq_business_trip_batch extends CJPA {
	@CFieldinfo(fieldname = "btab_id", iskey = true, notnull = true, precision = 10, scale = 0, caption = "批量出差申请ID", datetype = Types.INTEGER)
	public CField btab_id; // 批量出差申请ID
	@CFieldinfo(fieldname = "btab_code", codeid = 143, notnull = true, precision = 16, scale = 0, caption = "批量出差申请编码", datetype = Types.VARCHAR)
	public CField btab_code; // 批量出差申请编码
	@CFieldinfo(fieldname ="orgid",notnull=true,precision=10,scale=0,caption="部门ID",datetype=Types.INTEGER)
	public CField orgid;  //部门ID
	@CFieldinfo(fieldname ="orgcode",notnull=true,precision=16,scale=0,caption="部门编码",datetype=Types.VARCHAR)
	public CField orgcode;  //部门编码
	@CFieldinfo(fieldname ="orgname",notnull=true,precision=128,scale=0,caption="部门名称",datetype=Types.VARCHAR)
	public CField orgname;  //部门名称
	@CFieldinfo(fieldname = "wfid", precision = 20, scale = 0, caption = "wfid", datetype = Types.INTEGER)
	public CField wfid; // wfid
	@CFieldinfo(fieldname = "attid", precision = 20, scale = 0, caption = "attid", datetype = Types.INTEGER)
	public CField attid; // attid
	@CFieldinfo(fieldname = "stat", notnull = true, precision = 2, scale = 0, caption = "表单状态", datetype = Types.INTEGER)
	public CField stat; // 表单状态
	@CFieldinfo(fieldname = "idpath", notnull = true, precision = 256, scale = 0, caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	@CFieldinfo(fieldname = "entid", notnull = true, precision = 5, scale = 0, caption = "entid", datetype = Types.INTEGER)
	public CField entid; // entid
	@CFieldinfo(fieldname = "creator", notnull = true, precision = 32, scale = 0, caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", notnull = true, precision = 19, scale = 0, caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", precision = 32, scale = 0, caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", precision = 19, scale = 0, caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	@CFieldinfo(fieldname = "attr1", precision = 32, scale = 0, caption = "备用字段1", datetype = Types.VARCHAR)
	public CField attr1; // 备用字段1
	@CFieldinfo(fieldname = "attr2", precision = 32, scale = 0, caption = "备用字段2", datetype = Types.VARCHAR)
	public CField attr2; // 备用字段2
	@CFieldinfo(fieldname = "attr3", precision = 32, scale = 0, caption = "备用字段3", datetype = Types.VARCHAR)
	public CField attr3; // 备用字段3
	@CFieldinfo(fieldname = "attr4", precision = 32, scale = 0, caption = "备用字段4", datetype = Types.VARCHAR)
	public CField attr4; // 备用字段4
	@CFieldinfo(fieldname = "attr5", precision = 32, scale = 0, caption = "备用字段5", datetype = Types.VARCHAR)
	public CField attr5; // 备用字段5
	@CFieldinfo(fieldname ="remark",precision=512,scale=0,caption="部门编码",datetype=Types.VARCHAR)
	public CField remark;  //备注
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	@CLinkFieldInfo(jpaclass = Hrkq_business_trip_batchline.class, linkFields = { @LinkFieldItem(lfield = "btab_id", mfield = "btab_id") })
	public CJPALineData<Hrkq_business_trip_batchline> hrkq_business_trip_batchlines;

	public Hrkq_business_trip_batch() throws Exception {
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
