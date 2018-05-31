package com.weyao.srv.document;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.weyao.srv.document.SheetStatus.*;

/**
 * 该类表示一个可写的Excel模板
 * @author dujingjing
 *
 */
public final class WritableTemplate<T> implements Writer<T>{
	
	private Logger logger = LoggerFactory.getLogger(WritableTemplate.class);
	
	private List<DocumentService<T>> documentSrvs;

	private String reportName;
	
	/*
	 * 表示当前状态
	 */
	private StateMonitor state = new StateMonitor();
	
	/**
	 * 加载一个Excel模板，并进行初始化操作
	 * @param template
	 */
	WritableTemplate<T> loadTemplate(String serviceName, Template<T> template, CallBack callback){
		if(template == null){
			return this;
		}
		if(state.current == BLANK){
			state.Initialized();
			try {
				//通过serviceName以及callback来获取到对应的文档服务列表
				documentSrvs = DocumentSrvFramework.getService(serviceName, callback);
				logger.info("[key=" + serviceName + "]，总共获取到[" + documentSrvs.size() + "]个文档服务");
				//分步生成文件
				if(documentSrvs.size() > 0){
					for (DocumentService<T> documentSrv : documentSrvs) {
						//让每一个文档服务进行加载模板操作
						documentSrv.createDocument(template);
					}
				}else{
					state.done();
					return this;
				}
			} catch (Exception e) {
				e.printStackTrace();
				state.error();
			}
		}
		this.reportName = template.getReportName();
		return this;
	}
	
	
	
	/**
	 * 将数据写入到Excel中，并导出其二进制数据
	 * @param source
	 * @return
	 * @throws Exception 
	 */
	public File[] fillBlank(List<T> source) throws Exception{
		File[] files = new File[documentSrvs.size()];
		for (int i = 0; i < documentSrvs.size(); i++) {
			DocumentService<T> documentSrv = documentSrvs.get(i);
			files[i] = documentSrv.fillBlank(source);
		}
		return files;
	}
	
	
	
	/**
	 * 表示当前页状态
	 */
	private class StateMonitor{
		
		SheetStatus current = BLANK;
		
		void Initialized(){
			current = INITIALIZED;
		}
		
		void done(){
			current = FINISHED;
		}
		
		void error(){
			current = ERROR;
		}
	}



	@Override
	public String getReportName() {
		return this.reportName;
	}
}
