package com.weyao.srv.document;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.weyao.srv.document.impl.ExcelService;
import com.weyao.srv.document.impl.PDFService;

/**
 * 简单的文档服务框架
 * @author dujingjing
 *
 */
public final class DocumentSrvFramework {

	private static Map<String, Class<?>> CLASS_CACHE = new HashMap<String, Class<?>>();
	
	static{
		CLASS_CACHE.put("PDF", PDFService.class);
		CLASS_CACHE.put("Excel", ExcelService.class);
	}
	
	/**
	 * 根据传入的Key，进行处理类的实例化
	 * @param key
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<DocumentService<T>> getService(String key, CallBack callback) throws Exception{
		if(key == null){
			return Collections.emptyList();
		}
		if(key.equals("All")){
			List<DocumentService<T>> result = new ArrayList<DocumentService<T>>();
			for (Class<?> cla : CLASS_CACHE.values()) {
				if(callback == null){
					result.add((DocumentService<T>)cla.newInstance());
				}else{
					Constructor<?> constructor;
					try{
						constructor = cla.getConstructor(CallBack.class);
						result.add((DocumentService<T>)constructor.newInstance(callback));
					}catch(NoSuchMethodException e){
						result.add((DocumentService<T>)cla.newInstance());
					}
				}
			}
			return result;
		}else {
			Class<?> cla = CLASS_CACHE.get(key);
			if(cla == null){
				return null;
			}else{
				if(callback == null){
					return Arrays.asList((DocumentService<T>)cla.newInstance());
				}else{
					Constructor<?> constructor;
					try{
						constructor = cla.getConstructor(CallBack.class);
						return Arrays.asList((DocumentService<T>)constructor.newInstance(callback));
					}catch(NoSuchMethodException e){
						return Arrays.asList((DocumentService<T>)cla.newInstance());
					}
				}
			}
		}
	}
}
