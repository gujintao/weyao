package com.weyao.srv.asist;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;

/**
 * 用来产生操作字节码处理器的工厂类
 * 使用静态方法导出处理类
 * 出于业务考虑，这里可以将一些常用的字节码操作进行分类
 */
@Scope("singleton")
@Component
public final class AsistHandlerFactory {
	
	public static void main(String[] args) throws SrvAsistException {
		AsistHandler handler = AsistHandlerFactory.getDefaultAsistHandler("com", "Test", true);
		handler.begin();
		handler.addSetterAndGetter("java.lang.String", "name");
		handler.addSetterAndGetter("java.lang.Integer", "age");
		handler.end();
	}
	
	private static Logger LOGGER = LoggerFactory.getLogger(AsistHandler.class);
	
	public static AsistHandler getDefaultAsistHandler(String packagePath, String className, boolean ifCreate) throws SrvAsistException{
		return new DefaultAsistHandler(packagePath, className, ifCreate);
	}
	
	/**
	 * 默认可用的字节码处理器
	 * @author dujingjing
	 *
	 */
	private static class DefaultAsistHandler implements AsistHandler{
		
		/**
		 * 静态ClassPool
		 */
		private static ClassPool POOL = ClassPool.getDefault();
		
		CtClass ctClass;
		
		DefaultAsistHandler(String packagePath, String className, boolean ifCreate) throws SrvAsistException{
			Package pkg = Package.getPackage(packagePath);
			String path = packagePath + "." + className;
			try{
				if(pkg.isSealed()){
					throw new SrvAsistException("[" + packagePath + "]该包已经被封闭，不可再编辑");
				}
				ctClass = POOL.makeClass(path);
				LOGGER.info("");
			}catch(NullPointerException e){
				throw new SrvAsistException("[" + packagePath + "]该包不存在，无法操作", e);
			}catch(RuntimeException e){
				throw new SrvAsistException("[" + path + "]类建立失败，具体原因是:" + e.getMessage(), e);
			}
		}
		
		@Override
		public boolean begin() throws SrvAsistException {
			if(ctClass == null){
				throw new SrvAsistException("字节码处理器初始化失败！");
			}
			ctClass.defrost();
			return true;
		}

		@Override
		public boolean addSetterAndGetter(String classType, String field) throws SrvAsistException {
			try {
				//添加private字段
				CtField ctField = new CtField(POOL.getCtClass(classType), field , ctClass);
				ctField.setModifiers(Modifier.PRIVATE);
				ctClass.addField(ctField);
				String columnWithUpperChar = (char)(field.charAt(0) - 32) + field.substring(1);
				//添加get方法
				CtMethod getter = new CtMethod(POOL.get(classType), "get" + columnWithUpperChar, new CtClass[]{}, ctClass);
				getter.setBody("{ return this." + field + "}");
				ctClass.addMethod(getter);
				//添加set方法
				CtMethod setter = new CtMethod(CtClass.voidType, "set" + columnWithUpperChar, new CtClass[]{POOL.get(classType)}, ctClass);
				getter.setBody("{ this." + field + " = $1}");
				ctClass.addMethod(setter);
				return true;
			} catch (CannotCompileException | NotFoundException e) {
				throw new SrvAsistException("字节码操作失败，原因：" + e.getMessage(), e);
			}
		}

		@Override
		public boolean end() throws SrvAsistException {
			try {
				ctClass.writeFile();
				if(!ctClass.isFrozen()){
					ctClass.freeze();
				}
				return true;
			} catch (NotFoundException | IOException | CannotCompileException e) {
				throw new SrvAsistException("字节码操作失败，原因：" + e.getMessage(), e);
			}
		}
		
	}
}
