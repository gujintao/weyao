package com.weyao.srv.asist;

/**
 * 字节码服务异常
 * @author dujingjing
 *
 */
public class SrvAsistException extends Exception{

	private static final long serialVersionUID = -5547810698595296146L;

	public SrvAsistException(String msg){
		super(msg);
	}
	
	public SrvAsistException(String msg, Throwable cause){
		super(msg, cause);
	}
}
