package com.corsair.cjpa;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CJPASqlUtil;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.dbpool.CDBPool;
import com.corsair.dbpool.DBPools;

/**
 * JPA基类
 * 
 * @author Administrator
 *
 */
public abstract class CJPABase {
	/**
	 * JPA状态
	 * 
	 * @author Administrator
	 *
	 */
	public enum CJPAStat {
		RSLOAD4DB, RSINSERT, RSUPDATED, RSDEL
	};

	/**
	 * JPA操作
	 * 
	 * @author Administrator
	 *
	 */
	public enum JPAAction {
		actInsert, actUpdate, actDel, actNull
	}

	/**
	 * 表名
	 */
	public String tablename;
	/**
	 * 连接池
	 */
	public CDBPool pool;
	private CJPAStat cJpaStat = CJPAStat.RSINSERT;
	/**
	 * 保存前，JPA执行保存的方法
	 */
	public JPAAction _cjpaact;//
	/**
	 * 附加条件
	 */
	public String SqlWhere;
	/**
	 * 扩展
	 */
	public int tag = 0;
	/**
	 * 用来保存额外用户数据
	 */
	public Object _userdata = null;//
	/**
	 * 所有字段
	 */
	public List<CField> cFields = new ArrayList<CField>();
	/**
	 * 所有行表
	 */
	public List<CJPALineData<CJPABase>> cJPALileDatas = new ArrayList<CJPALineData<CJPABase>>();
	/**
	 * 工作流标题
	 */
	public List<CField> wfsubjectfields = new ArrayList<CField>();

	private JPAControllerBase controller = null;

	private Class<JPAControllerBase> clsctr = null;

	protected abstract JPAControllerBase getPublicControllerBase() throws Exception;

	// public List<CJPABase> cJPAs = new ArrayList<CJPABase>();

	// /////////////////////////create//////////////////

	@SuppressWarnings("unchecked")
	public CJPABase() throws Exception {
		String poolname = null;
		if (this.getClass().isAnnotationPresent(CEntity.class)) {
			CEntity entity = this.getClass().getAnnotation(CEntity.class);
			poolname = entity.dbpool();
			tablename = entity.tablename();
			if ((entity.controller() != JPAControllerBase.class) && (JPAControllerBase.class.isAssignableFrom(entity.controller()))) {
				clsctr = (Class<JPAControllerBase>) entity.controller();
			}
		}

		if ((poolname == null) || poolname.isEmpty()) {// if ((pool == null) || (poolname.isEmpty())) {
			this.pool = DBPools.defaultPool();
		} else {
			this.pool = DBPools.poolByName(poolname);
		}

		if ((tablename == null) || (tablename.isEmpty())) {
			this.tablename = this.getClass().getSimpleName();// .toLowerCase();
		}
		// System.out.println("pool:" + pool);
		// System.out.println("tablename:" + tablename);
		if (pool != null)
			tablename = CJPASqlUtil.getSqlTable(pool.getDbtype(), tablename);
		try {
			initfieldsex();
		} catch (Exception e) {
			throw e;
		}
		InitObject();
	}

	private void initfieldsex() throws Exception {
		Field[] fields = this.getClass().getFields();
		for (Field field : fields) {
			Object object = field.get(this);
			if (field.getGenericType() == CField.class) {
				if (object == null) {
					if (!field.isAnnotationPresent(CFieldinfo.class))
						throw new Exception("JPA 字段未发现注解@CFieldinfo");
					CFieldinfo fdinfo = field.getAnnotation(CFieldinfo.class);
					String fdname = fdinfo.fieldname();// .toLowerCase();
					String cfdname = field.getName();// .toLowerCase();
					if ((fdname == null) || (fdname.isEmpty()))
						fdname = cfdname;
					CField cfd = new CField(this, fdname);
					cfd.setCfieldname(cfdname);
					cfd.setIskey(fdinfo.iskey());
					cfd.setAutoinc(fdinfo.autoinc());
					cfd.setNullable(!fdinfo.notnull());
					cfd.setFieldtype(fdinfo.datetype());
					cfd.setDicid(fdinfo.dicid());
					cfd.setDicclass(fdinfo.dicclass());
					cfd.setCodeid(fdinfo.codeid());
					cfd.setCaption(fdinfo.caption());
					cfd.setFdinfo(fdinfo);
					field.set(this, cfd);
					cFields.add(cfd);
				}
			}
			if (field.getType() == CJPALineData.class) {
				if (object == null) {
					ParameterizedType pt = (ParameterizedType) field.getGenericType();
					@SuppressWarnings("unchecked")
					Class<CJPABase> clz = (Class<CJPABase>) pt.getActualTypeArguments()[0];
					// System.out.println(clz);
					CJPALineData<CJPABase> cfd = new CJPALineData<CJPABase>(this, clz);
					cfd.setCfieldname(field.getName());
					if (field.isAnnotationPresent(CLinkFieldInfo.class))
						cfd.setLinkfdinfo(field.getAnnotation(CLinkFieldInfo.class));
					field.set(this, cfd);
					cJPALileDatas.add(cfd);
				}
			}
		}
	}

	/**
	 * 清空数据
	 */
	public void clear() {
		for (CJPALineData<CJPABase> cld : cJPALileDatas) {
			cld.clear();
		}
		/*
		 * for (CJPABase jpa : cJPAs) { jpa.clear(); }
		 */
		setJpaStat(CJPAStat.RSINSERT);
		for (CField cf : cFields) {
			cf.setValue(null);
			cf.setChanged(false);
		}
	}

	/**
	 * 判断一个实体是否为空，由根据ID是否为空判断 改为 判断所有字段
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		// String id = getid(); //moyh 170524
		// return (id == null) || id.isEmpty();
		for (CField fd : cFields) {
			if (!fd.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public boolean InitObject() {// 类初始化调用的方法
		return true;
	}

	public boolean FinalObject() { // 类释放前调用的方法
		return true;
	}

	/**
	 * @return 字段数
	 */
	public int cfiledcount() {
		return cFields.size();
	}

	/**
	 * 获取字段
	 * 
	 * @param cindex
	 * @return
	 */
	public CField cfield(int cindex) {
		return cFields.get(cindex);
	}

	/**
	 * 获取字段
	 * 
	 * @param fieldname
	 * @return
	 */
	public CField cfield(String fieldname) {
		for (CField fd : cFields) {
			if (fd.getFieldname().equals(fieldname)) {
				return fd;
			}
		}
		return null;
	}

	/**
	 * 获取字段
	 * 
	 * @param fieldname
	 * @return
	 */
	public CField cfieldNoCase(String fieldname) {
		for (CField fd : cFields) {
			if (fd.getFieldname().equalsIgnoreCase(fieldname)) {
				return fd;
			}
		}
		return null;
	}

	/**
	 * 获取字段
	 * 
	 * @param cfieldname
	 * @return
	 */
	public CField cfieldbycfieldname(String cfieldname) {
		for (CField fd : cFields) {
			if (fd.getCfieldname().equals(cfieldname)) {
				return fd;
			}
		}
		return null;
	}

	/**
	 * 获取第一个KEY字段（针对一个KEY的表很有用）
	 * 
	 * @return
	 */
	public CField getIDField() {
		for (CField fd : cFields) {
			if (fd.isIskey()) {
				return fd;
			}
		}
		return null;
	}

	/**
	 * 获取实体KEY值
	 * 
	 * @return
	 */
	public String getid() {
		CField idfd = getIDField();
		if (idfd == null)
			return null;
		else
			return idfd.getValue();
	}

	/**
	 * 设置实体KEY值
	 * 
	 * @param value
	 */
	public void setid(String value) {
		getIDField().setValue(value);
	}

	/**
	 * 获取实体KEY字段名
	 * 
	 * @return
	 */
	public String getIDFieldName() {
		if (getIDField() == null)
			return null;
		return getIDField().getFieldname();
	}

	private boolean inFields(String[] fdnames, CField cfd) {
		for (String fdname : fdnames) {
			if (fdname.equals(cfd.getFieldname())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取字段名列表
	 * 
	 * @return
	 */
	public String[] getFieldNames() {
		String[] disFields = {};
		return getFieldNames(disFields);
	}

	/**
	 * 获取字段名列表
	 * 
	 * @param disFields
	 * @return
	 */
	public String[] getFieldNames(String[] disFields) {
		String rst = "";
		for (CField fd : cFields) {
			if (!isInArray(disFields, fd.getFieldname()))
				rst = rst + fd.getFieldname() + ",";
		}
		return rst.split(",");
	}

	private boolean isInArray(String[] arr, String value) {
		for (String s : arr) {
			if (value.equals(s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 实体赋值
	 * 
	 * @param cjpa
	 * @param fdnames
	 */
	public void assignfieldOnlyValue(CJPABase cjpa, String[] fdnames) {
		for (CField fd : cjpa.cFields) {
			if (!inFields(fdnames, fd))
				continue;
			CField field = cfield(fd.getFieldname());// this.field
			if (field != null) {
				field.setValue(fd.getValue());
			}
		}
	}

	/**
	 * 实体赋值
	 * 
	 * @param cjpa
	 * @param asvalue 是否包含字段值
	 * @param fdnames
	 */
	public void assignfield(CJPABase cjpa, boolean asvalue, String[] fdnames) {
		for (CField fd : cjpa.cFields) {
			if (!inFields(fdnames, fd))
				continue;
			CField field = cfield(fd.getFieldname());// this.field
			if (field != null) {
				field.setFieldtype(fd.getFieldtype());
				field.setSize(fd.getSize());
				field.setFieldtype(fd.getFieldtype());
				if (asvalue)
					field.setValue(fd.getValue());
			}
		}
	}

	/**
	 * 实体赋值
	 * 
	 * @param cjpa
	 * @param asvalue
	 */
	public void assignfield(CJPABase cjpa, boolean asvalue) {
		for (CField fd : cjpa.cFields) {
			CField field = cfield(fd.getFieldname());// this.field
			if (field != null) {
				field.setFieldtype(fd.getFieldtype());
				field.setSize(fd.getSize());
				field.setFieldtype(fd.getFieldtype());
				if (asvalue)
					field.setValue(fd.getValue());
			}
		}
	}

	/**
	 * 设置所有实体状态
	 * 
	 * @param jstat
	 */
	public void setAllJpaStat(CJPAStat jstat) {
		setJpaStat(jstat);
		for (CJPALineData<CJPABase> ld : cJPALileDatas) {
			for (CJPABase cj : ld) {
				cj.setAllJpaStat(jstat);
			}
		}
	}

	/**
	 * 根据类全名获取明细实体列表
	 * 
	 * @param entityclasname
	 * @return
	 */
	public CJPALineData<CJPABase> lineDataByEntyClasname(String entityclasname) {
		for (CJPALineData<CJPABase> ld : cJPALileDatas) {
			// System.out.println(this.getClass().getName() + " " +
			// entityclasname);
			if (ld.getEntityClas().getName().equalsIgnoreCase(entityclasname))
				return ld;
		}
		return null;
	}

	/**
	 * 根据简单类名获取明细实体列表
	 * 
	 * @param entityclasname
	 * @return
	 */
	public CJPALineData<CJPABase> lineDataByEntySimpleClasname(String entityclasname) {
		for (CJPALineData<CJPABase> ld : cJPALileDatas) {
			// System.out.println(this.getClass().getName() + " " +
			// entityclasname);
			if (ld.getEntityClas().getSimpleName().equalsIgnoreCase(entityclasname))
				return ld;
		}
		return null;
	}

	/**
	 * 根据字段名获取明细实体列表
	 * 
	 * @param cfieldname
	 * @return
	 */
	public CJPALineData<CJPABase> lineDataByCFieldName(String cfieldname) {
		for (CJPALineData<CJPABase> ld : cJPALileDatas) {
			// System.out.println(this.getClass().getName() + " " +
			// entityclasname);
			if (ld.getCfieldname().equalsIgnoreCase(cfieldname))
				return ld;
		}
		return null;
	}

	/**
	 * 根据属性名获取明细实体列表
	 * 
	 * @param cfieldname
	 * @return
	 */
	public CJPALineData<CJPABase> lineDataByCFieldNameNoCase(String cfieldname) {
		for (CJPALineData<CJPABase> ld : cJPALileDatas) {
			// System.out.println(this.getClass().getName() + " " +
			// entityclasname);
			if (ld.getCfieldname().equalsIgnoreCase(cfieldname))
				return ld;
		}
		return null;
	}

	/**
	 * 根据表名获取明细实体列表
	 * 
	 * @param tablename
	 * @return
	 */
	public CJPALineData<CJPABase> lineDataByTableName(String tablename) {
		for (CJPALineData<CJPABase> ld : cJPALileDatas) {
			if (ld.getTableName().equalsIgnoreCase(tablename))
				return ld;
		}
		return null;
	}

	/**
	 * 获取控制器
	 * 
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public JPAControllerBase getController() throws InstantiationException, IllegalAccessException {
		if ((clsctr != null) && (controller == null)) {
			controller = clsctr.newInstance();
		}
		return controller;
	}

	/**
	 * 清除所有ID及其关联字段
	 */
	public void clearAllId() {
		for (CField fd : cFields) {
			if (fd.isIskey())
				fd.setValue(null);
		}
		setJpaStat(CJPAStat.RSINSERT);
		for (CJPALineData<CJPABase> line : cJPALileDatas) {
			List<String> lfds = getLineLinkedfdnames(line);
			for (CJPABase jpa : line) {
				for (String lfd : lfds) {
					jpa.cfield(lfd).setValue(null);
				}
				jpa.clearAllId();
			}
		}
	}

	private List<String> getLineLinkedfdnames(CJPALineData<CJPABase> linedata) {
		List<String> lfds = new ArrayList<String>();
		CLinkFieldInfo lfi = linedata.getLinkfdinfo();
		if (lfi == null) {
			List<LinkField> lfs = linedata.getLinkfields();
			for (LinkField lf : lfs) {
				if (cfield(lf.getMfield()).isIskey()) {
					lfds.add(lf.getLfield());
				}
			}
		} else {
			for (LinkFieldItem lkf : lfi.linkFields()) {
				if (cfield(lkf.mfield()).isIskey()) {
					lfds.add(lkf.lfield());
				}
			}
		}
		return lfds;
	}

	public CJPAStat getJpaStat() {
		return cJpaStat;
	}

	/**
	 * 清楚操作标识
	 */
	public void clearJPAAction() {
		for (CJPALineData<CJPABase> line : cJPALileDatas) {
			for (CJPABase jpa : line) {
				jpa.clearJPAAction();
			}
		}
		this._cjpaact = CJPABase.JPAAction.actNull;
	}

	/**
	 * 设置状态
	 * 
	 * @param cJpaStat
	 */
	public void setJpaStat(CJPAStat cJpaStat) {
		if ((cJpaStat == CJPAStat.RSINSERT) || (cJpaStat == CJPAStat.RSDEL)) {
			for (CJPALineData<CJPABase> line : cJPALileDatas) {
				for (CJPABase jpa : line) {
					jpa.setJpaStat(cJpaStat);
				}
			}
		}
		this.cJpaStat = cJpaStat;
	}

}
