package com.weyao.srv.asist;

/**
 * 字节码操作处理器接口
 * @author dujingjing
 *
 */
public interface AsistHandler {

	/**
	 * 开始处理字节码数据，调用begin()之后，将会解锁字节码文件，将其置入可编辑状态
	 * @throws SrvAsistException 
	 */
	public boolean begin() throws SrvAsistException;
	
	/**
	 * 对一个Class中加入某个field，并且赋予常规的setter和getter
	 * @param field
	 * @return
	 * @throws SrvAsistException 
	 */
	public boolean addSetterAndGetter(String classType, String field) throws SrvAsistException;
	
	/**
	 * 处理完成，将数据写入到字节码文件中，进行锁定
	 * 注意的是：
	 * 由于现代JVM当类加载完成之后，不再接受任何形式的编辑，所以调用该end()方法之后，不可再进行任何处理
	 * @return
	 * @throws SrvAsistException 
	 */
	public boolean end() throws SrvAsistException;
}
