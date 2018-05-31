package com.weyao.srv.document;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 用作操作Excel的基础类
 * @author dujingjing
 * @version 1.0
 */
@Component(value="workBookRegister")
@Scope(value="singleton")
public final class WorkBookRegister implements Register{

	/*
	 * 模板缓存
	 */
	private static Map<String, Template<?>> TEMPLATE_CACHE = new ConcurrentHashMap<String, Template<?>>();
	
	/**
	 * 定义一个模板模型
	 * @param target 指定Class
	 */
	@SuppressWarnings("unchecked")
	public <T> Template<T> defineTemplate(String templateKey, String reportName, Class<T> target){
		Template<T> template = null;
		if(TEMPLATE_CACHE.containsKey(templateKey)){
			template = (Template<T>) TEMPLATE_CACHE.get(templateKey);
		}else{
			template = new Template<T>(reportName, target);
			TEMPLATE_CACHE.put(templateKey, template);
		}
		return template;
	}
	
	/**
	 * 定义一个模板模型
	 * @param target 指定Class
	 */
	@SuppressWarnings("unchecked")
	public <T> Writer<T> tryToGetTemplate(String templateKey, String serviceName){
		if(TEMPLATE_CACHE.containsKey(templateKey)){
			return (Writer<T>) TEMPLATE_CACHE.get(templateKey).unlock(serviceName);
		}else{
			throw new RuntimeException("该类型对应的Excel模板还未创建");
		}
	}

	/**
	 * 定义一个模板模型
	 * @param target 指定Class
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> Writer<T> tryToGetTemplate(String templateKey, String serviceName, CallBack callback) {
		if(TEMPLATE_CACHE.containsKey(templateKey)){
			return (Writer<T>) TEMPLATE_CACHE.get(templateKey).unlock(serviceName, callback);
		}else{
			throw new RuntimeException("该类型对应的Excel模板还未创建");
		}
	}
	
	/**
	 * 用于定义一个模板对应的key的表述
	 * @author dujingjing
	 *
	 */
//	private static class TemplateKey<T>{
//		
//		private String key;
//		
//		private Class<T> token;
//		
//		private TemplateKey(String key, Class<T> token){
//			this.key = key;
//			this.token = token;
//		}
//		
//		@Override
//		public boolean equals(Object obj) {
//			if(obj == null || !(obj instanceof TemplateKey)){
//				return false;
//			}
//			if(obj == this){
//				return true;
//			}
//			TemplateKey<?> t = (TemplateKey<?>)obj;
//			return t.key.equals(key);
//		}
//		
//		@Override
//		public int hashCode() {
//			return key.hashCode();
//		}
//	}
}
