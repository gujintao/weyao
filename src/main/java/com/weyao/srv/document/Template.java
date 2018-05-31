package com.weyao.srv.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.sql.JDBCType;
import java.util.*;

/**
 * Excel模板类，该类预定义了一些常用模板，
 * 通常持有某属性名与在Excel中坐标的对应关系
 * @author dujingjing
 * @version 1.0
 */
public class Template<T> {

	private Logger logger = LoggerFactory.getLogger(Template.class);
	
	/*
	 * 模板信息缓存
	 */
	private Map<String, Col> map = new LinkedHashMap<>();
	
	/*
	 * 用来表示一个Excel页的列集合，以列号进行排序
	 */
	private List<Col> cols = new ArrayList<Col>();
	
	private Class<T> token;
	
	private String reportName;
	
	private Comparator<Col> colComparator = new Comparator<Col>() {

		public int compare(Col o1, Col o2) {
			return o1.x > o2.x ? 1 : (o1.x == o2.x ? 0: -1);
		}
	};
	
	Template(String reportName, Class<T> target){
		this.token = target;
		this.reportName = reportName;
	}
	
	/**
	 * 向模板中注册单元格
	 * @param key 对应key
	 * @param x 横坐标
	 * @param type 纵坐标
	 * @return
	 */
	public Template<T> defineCol(String key, String colName, int x, JDBCType type, boolean mergeAllowed){
		if(map.containsKey(key)){
			throw new RuntimeException("重复定义的单元格，名称为：" + key);
		}
		if(x < 0){
			throw new RuntimeException("参数错误");
		}
		String getter = "get" + (char)(key.charAt(0) - 32) + key.substring(1);
		Method method;
		try {
			method = token.getMethod(getter);
			map.put(key, new Col(key, x, colName, method, type, mergeAllowed));
		} catch (NoSuchMethodException | SecurityException e) {
			logger.error("类定义错误：" + token.getName());
		}
		return this;
	}
	
	/**
	 * 向模板中注册单元格
	 * @param key 对应key
	 * @param x 横坐标
	 * @param type 纵坐标
	 * @return
	 */
	public Template<T> defineCol(String key, String colName, int x, JDBCType type){
		return defineCol(key, colName, x, type, false);
	}
	
	/**
	 * 转换成可写模式
	 */
	public WritableTemplate<T> unlock(String serviceName){
		WritableTemplate<T> writer = new WritableTemplate<T>();
		return writer.loadTemplate(serviceName, this, null);
	}
	
	/**
	 * 转换成可写模式
	 */
	public WritableTemplate<T> unlock(String serviceName, CallBack callback){
		WritableTemplate<T> writer = new WritableTemplate<T>();
		return writer.loadTemplate(serviceName, this, callback);
	}
	
	/**
	 * 导出列行号
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Col> exportCols(){
		if(cols.size() == 0 && map.size() > 0){
			for (Map.Entry<String, Col> entry : map.entrySet()) {
				try {
					cols.add((Col)entry.getValue().clone());
				} catch (CloneNotSupportedException e) {
					logger.error(e.getMessage());
				}
			}
		}
		Collections.sort(cols, colComparator);
		return cols;
	}
	
	/**
	 * 返回模板Excel名字，对应报表名字
	 * @return
	 */
	public String getReportName(){
		return this.reportName;
	}
	
	/**
	 * 表示Excel模板中的一列数据
	 */
	public final class Col implements Cloneable{
		
		private int x;
		
		private String key;
		
		private String colName;
		
		private Method method;
		
		private JDBCType type;
		
		private String lastContent;
		
		private boolean allowMerge = false;
		
		Col(String key, int x, String colName, Method method, JDBCType type){
			this.x = x;
			this.colName = colName;
			this.method = method;
			this.type = type;
			this.key = key;
		}

		Col(String key, int x, String colName, Method method, JDBCType type, boolean allowMerge){
			this(key, x, colName, method, type);
			this.allowMerge = allowMerge;
		}
		
		public String getColName() {
			return colName;
		}

		public Method getMethod() {
			return method;
		}

		public JDBCType getType(){
			return type;
		}
		
		public void logContent(String content){
			this.lastContent = content;
		}
		
		public String getLastContent(){
			return this.lastContent;
		}
		
		public boolean isMergeAllowed(){
			return this.allowMerge;
		}
		
		public String getKey() {
			return key;
		}

		public void clear(){
			this.lastContent = null;
		}
		
		@Override
		protected Object clone() throws CloneNotSupportedException {
			return super.clone();
		}
	}
}
