package com.corsair.cjpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.corsair.cjpa.CJPABase.JPAAction;
import com.corsair.cjpa.util.CPoint;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.PraperedSql;

/**
 * 控制器基类
 * 
 * @author Administrator
 *
 */
public class JPAControllerBase {
	public enum ArraiveType {
		atCreate, atSubmit, atReject
	};

	public String OngetNewCode(CJPABase jpa, int codeid) {
		return null;
	}

	/**
	 * 保存前
	 * 
	 * @param jpa里面有值
	 * ，还没检测数据完整性，没生成ID CODE 设置默认值
	 * @param con
	 * @param selfLink
	 * @throws Exception
	 */
	public void BeforeSave(CJPABase jpa, CDBConnection con, boolean selfLink) throws Exception {

	}

	/**
	 * 保存中
	 * 
	 * @param jpa
	 * 完成完整性检测、生成ID、CODE、设置完默认值、生成SQL语句；还未保存到数据库，JPA状态未变
	 * @param con
	 * @param sqllist
	 * @param selfLink
	 * @throws Exception
	 */
	public void OnSave(CJPABase jpa, CDBConnection con, ArrayList<PraperedSql> sqllist, boolean selfLink) throws Exception {
	}

	/**
	 * 完成保存
	 * 
	 * @param jpa
	 * 已经实例化到数据库，JPA变为 RSLOAD4DB 状态
	 * @param con
	 * @param selfLink
	 */
	public void AfterSave(CJPABase jpa, CDBConnection con, boolean selfLink) throws Exception {

	}

	/**
	 * 删除前 貌似列表JPA被干掉时候无法监控到
	 * 
	 * @param jpa还未保存到数据库
	 * ，JPA状态未变
	 * @param con
	 * @param selfLink
	 * @throws Exception
	 */
	public void OnDelete(CJPABase jpa, CDBConnection con, boolean selfLink) throws Exception {
	}

	/**
	 * 监控JPA 持久化 插入、更新、删除
	 * JPA通过ID删除 貌似无法监控到自关联数据删除
	 * 保存中，已经生成SQL语句，重新给JPA赋值也没卵用了
	 * 
	 * @param jpa
	 * @param con
	 * @param act
	 * @throws Exception
	 */
	public void OnJAPAction(CJPABase jpa, CDBConnection con, JPAAction act) throws Exception {
	}

	/**
	 * 打印到Excel
	 * 用于从数据库获取数据
	 * 
	 * @param jpa
	 * @param mdfname
	 * @return
	 */
	public List<HashMap<String, String>> OnPrintDBData2Excel(CJPABase jpa, String mdfname) {
		return null;
	}

	/**
	 * 打印某一个字段到某一个CELL事件，返回false 将不执行默认打印
	 * 
	 * @param jpa
	 * @param mdfname
	 * @param fdname
	 * @param cell
	 * @return
	 */
	public String OnPrintField2Excel(CJPABase jpa, String modelkey, String fdname, String value, Cell cell) {
		return null;
	}

	/**
	 * 向Excel写入一条数据后
	 * 
	 * @param jpa
	 * @param mdfname
	 * @param sheet
	 * @param cellfrom
	 * col == x row ==y
	 * @param cellto
	 */
	public void AfterPrintItem2Excel(CJPABase jpa, String modelkey, Workbook workbook, Sheet sheet, CPoint cellfrom, CPoint cellto) {

	}
}
