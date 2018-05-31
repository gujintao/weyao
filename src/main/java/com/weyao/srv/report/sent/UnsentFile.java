package com.weyao.srv.report.sent;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 表示一个待发送的附件的
 * @author dujingjing
 * @version 1.0
 */
public class UnsentFile {
	
	/*
	 * 空对象 
	 */
	public static UnsentFile NULL = new NULL();
	
	private String key;
	
	private String filePath;
	
	private String fileName;
	
	public UnsentFile(String filePath, String fileName, String key){
		this.filePath = filePath;
		this.fileName = fileName;
	}
	
	public File readAsFile() throws FileNotFoundException{
		File file = new File(filePath);
		if(file.exists()){
			return file;
		}
		throw new FileNotFoundException();
	}

	public String getFilePath() {
		return new String(filePath);
	}

	public String getFileName() {
		return new String(fileName);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	/**
	 * UnsentFile的空对象
	 * @author dujingjing
	 *
	 */
	private static class NULL extends UnsentFile{

		private String msg = "该实例为空对象，不支持任何操作";
		
		public NULL() {
			super(null, null, null);
			msg.intern();
		}
		
		@Override
		public File readAsFile() throws FileNotFoundException {
			throw new UnsupportedOperationException(msg);
		}

		@Override
		public String getFilePath() {
			throw new UnsupportedOperationException(msg);
		}

		@Override
		public String getFileName() {
			throw new UnsupportedOperationException(msg);
		}

		@Override
		public String getKey() {
			throw new UnsupportedOperationException(msg);
		}

		@Override
		public void setKey(String key) {
			throw new UnsupportedOperationException(msg);
		}
	}
}
