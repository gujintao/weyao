package com.weyao.srv.document;

import static java.sql.JDBCType.BIGINT;
import static java.sql.JDBCType.DATE;
import static java.sql.JDBCType.DOUBLE;
import static java.sql.JDBCType.FLOAT;
import static java.sql.JDBCType.INTEGER;
import static java.sql.JDBCType.TIMESTAMP;
import static java.sql.JDBCType.VARCHAR;

import java.math.BigDecimal;
import java.sql.JDBCType;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 将各个类型的数据转化成字符串类型
 * @author dujingjing
 */
public final class Adapters {

	private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public static <T> String toString(T value, JDBCType type){
		if(value == null){
			return "";
		}
		if(type == VARCHAR){
			return value.toString();
		}
		//TODO 暂时解决方案，后期替换掉，采用GSON或者其他类库
		if(type == INTEGER || type == FLOAT || type == DOUBLE || type == BIGINT){
			if(value instanceof Integer){
				return ((Integer)value).toString();
			}
			if(value instanceof Float){
				Float f = (Float) value;
				return String.valueOf(f.floatValue());
			}
			if(value instanceof Double){
//				Double f = Double.parseDouble(String.valueOf(value));
//				return String.valueOf(f.doubleValue());
				BigDecimal bd = new BigDecimal(String.valueOf(value));
				return bd.toPlainString();
			}
			if(value instanceof Long){
				Long f = (Long) value;
				return String.valueOf(f.longValue());
			}
			if(value instanceof String){
				return value.toString();
			}
//			try{
//				Float f = Float.class.cast(value);
//				if(f.intValue() == f){
//					return Integer.toString(f.intValue());
//				}
//				return Float.toString(f);
//			}catch(ClassCastException e){
//				Double f = Double.class.cast(value);
//				if(f.intValue() == f){
//					return Integer.toString(f.intValue());
//				}
//				return Double.toString(f);
//			}
//			return 
		}
		if(type == TIMESTAMP){
			if (value instanceof String){
				return value.toString();
			} else {
				try {
					return dateTimeFormat.format(Date.class.cast(value));
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("时间为:-----"+value.toString());
					return value.toString();
				}
				
			}
		}
		if(type == DATE){
			if (value instanceof String){
				return value.toString();
			} else {
				return dateFormat.format(Date.class.cast(value));
			}
//			return dateFormat.format(Date.class.cast(value));
		}
		System.out.println(type + "  " + value.getClass());
		throw new RuntimeException("未知的参数类型.");
	}
	
//	private static boolean isNumeric(String str){ 
//		for (int i = str.length(); --i>=0; ){ 
//			if (!Character.isDigit(str.charAt(i))){
//				return false; 
//			} 
//		} 
//		return true; 
//	} 
}
