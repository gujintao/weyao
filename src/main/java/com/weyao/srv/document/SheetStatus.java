package com.weyao.srv.document;

/**
 * 该枚举用来表示一个Excel的Sheet页当前状态
 * @author dujingjing
 *
 */
public enum SheetStatus {
	
	/*
	 * 空状态
	 */
	BLANK,
	/*
	 * 已经经过初始化，分配完内存，并写入标题栏
	 */
	INITIALIZED,
	/*
	 * 表示已经将数据写入完毕
	 */
	FINISHED,
	/*
	 * 发生未知错误，该Sheet页丢弃，不可再用
	 */
	ERROR;
}
