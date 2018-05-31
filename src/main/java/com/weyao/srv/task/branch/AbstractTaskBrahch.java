package com.weyao.srv.task.branch;

import com.google.common.reflect.TypeToken;
import com.weyao.api.info.AliyunData;
import com.weyao.api.util.AliyunUploadFileUtil;
import com.weyao.common.Context;
import com.weyao.common.JsonHelper2;
import com.weyao.exception.SrvException;
import com.weyao.info.dataproxy.ResultType;
import com.weyao.redis.RedisService;
import com.weyao.srv.dataproxy.DataProxySrv;
import com.weyao.srv.disocnfi.AliyunConfig;
import com.weyao.srv.document.CallBack;
import com.weyao.srv.document.Register;
import com.weyao.srv.document.Writer;
import com.weyao.srv.report.sent.UnsentAttachQueue;
import com.weyao.srv.report.sent.UnsentFile;
import com.weyao.srv.task.branch.mail.MetaMailPkg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.*;

/**
 * 任务分支抽象类
 * @author dujingjing
 *
 */
abstract class AbstractTaskBrahch<T> implements TaskBranch, InitializingBean{

	private static Logger logger = LoggerFactory.getLogger(TaskBranch.class);
	
	protected static final String DEFAULT = "default";
	
	/*
	 * 倒计时
	 */
	private CountDownLatch latch;
	
	/*
	 * key值，保证全局唯一
	 */
	private final String key;
	
//	private final Class<T> token;
	
	private CallBack callBack;
	
	private DefaultDelayEvent event;
	
	@Autowired
	private DataProxySrv dataProxySrv;

	@Autowired
	private RedisService redis;
	
	@Autowired
	private Register register;
	
	private AliyunData aliyunCon;
	
	@Autowired
    private AliyunConfig aliyunConfig;

	@Autowired
	private MetaMailPkg pkg;
	
	protected AbstractTaskBrahch(String key){
		this.key = key;
		//获得被擦除的泛型
//		ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
//		Type[] types = type.getActualTypeArguments();
//		this.token = (Class<T>) types[0];
	}
	
	protected AbstractTaskBrahch(String key, CallBack callBack){
		this(key);
		this.callBack = callBack;
	}
	
//	static class FileTask{
//		
//		FileTask(String task, String fileName){
//			this.task = task;
//			this.fileName = fileName;
//		}
//		
//		private String task;
//		
//		private String fileName;
//
//		@Override
//		public boolean equals(Object obj) {
//			if(obj == null || !(obj instanceof FileTask)){
//				return false;
//			}
//			FileTask t = (FileTask) obj;
//			return t.task.equals(this.task) && t.fileName.equals(this.fileName);
//		}
//		
//		@Override
//		public int hashCode() {
//			return this.task.hashCode();
//		}
//	}

	@Override
	public void run() {
		try {
			this.dealBranch();
			logger.info("[key：" + this.key + "]任务完成。");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("分支处理失败, 错误信息：" + e.getMessage());
		}
	}

	@Override
	public String getKey(){
		return this.key;
	}
	
	@Override
	public void dealBranch() throws Exception {
		this.event.delay();
		this.latch.countDown();
	}
	
	/**
	 * 查询服务
	 * @param sql
	 * @param resultType
	 * @param params
	 * @return
	 * @throws SrvException 
	 */
	protected List<T> proxyQuery(String sql, ResultType resultType, List<Object> params, Class<T> token) throws SrvException{
		logger.info("开始调用\"proxyQuery\", SQL为：" + sql + ",参数列表：" + params);
		String queryResult = this.dataProxySrv.excuteQuery(sql, resultType, params);
		return this.transToPojoList(queryResult, token);
	}
	
	@SuppressWarnings("serial")
	private List<T> transToPojoList(String queryResult, Class<T> token){
		//TODO 暂时补救方式，需要采取具体配置策略进行优化
		List<Map<?, ?>> result = JsonHelper2.fromJson(queryResult, new TypeToken<T>(token) {
		}.getRawType());
		List<T> list = new ArrayList<T>();
		try{
			T entity;
			for (Map<?, ?> map : result) {
				entity = token.getConstructor().newInstance();
				for(Field field : token.getDeclaredFields()){
					if(field.getType() == DecimalFormat.class || field.getType() == long.class){
						continue;
					}
					field.setAccessible(true);
					if(field.getType() == Integer.class){
						Integer value = ((Double) map.get(field.getName())).intValue();
						field.set(entity, value);
					}else if(field.getType() == Float.class){
						Float value = ((Double) map.get(field.getName())).floatValue();
						field.set(entity, value);
					}else{
						field.set(entity, map.get(field.getName()));
					}
				}
				list.add(entity);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 加载倒计时
	 */
	public void loadComponent(CountDownLatch latch, Integer businessHour, Integer hour, boolean compress, String serviceName){
		this.loadComponent(latch, businessHour, hour, true, compress, serviceName);
	}
	
	/**
	 * 加载倒计时
	 */
	public void loadComponent(CountDownLatch latch, Integer businessHour, Integer hour, boolean ignore, boolean compress, String serviceName){
		this.latch = latch;
		if(hour < businessHour){
			throw new IllegalArgumentException("业务处理时刻不可大于邮件发送时刻！");
		}
		//初始化延迟事件
		this.event = new DefaultDelayEvent(hour, businessHour, ignore, compress, serviceName);
		
	}
	
	/**
	 * 导出相关的数据
	 * @return
	 */
	protected abstract Map<String, List<? extends Object>> export() throws SrvException;
	
	/**
	 * 延迟时间
	 * @author dujingjing
	 */
	class DefaultDelayEvent implements DelayEvent{

		private Integer hour;
		
		private Integer businessHour;
		
		private boolean ignore = true;
		
		private boolean compress = false;
		
		private String serviceName;
		
		DefaultDelayEvent(Integer businessHour, Integer hour, boolean ignore, boolean compress, String serviceName){
			this.hour = hour;
			this.ignore = ignore;
			this.businessHour = businessHour;
			this.compress = compress;
			this.serviceName = serviceName;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean delay() throws Exception {
			delayAfterSpecialSeconds("业务数据生成", businessHour);
			Map<String, List<? extends Object>> result = AbstractTaskBrahch.this.export();
//			UnsentFile[] unsentFiles = new UnsentFile[result.size()];
			final List<UnsentFile> unsentFiles = new ArrayList<UnsentFile>();
			for (Entry<String, List<? extends Object>> entry : result.entrySet()) {
				final String key = entry.getKey();
				final Writer<T> writer = register.tryToGetTemplate(key, serviceName, callBack);
				List<T> list = (List<T>) entry.getValue();
				// useless @since 2017/12/04
				/*String queryResult = JsonHelper2.toJson(list);
				//上传至Redis集群
				redis.lpush(key, queryResult);*/
				final File[] files = writer.fillBlank(list);
				for (File file : files) {
					String reportName = writer.getReportName();
					String fileName = file.getName();
					unsentFiles.add(new UnsentFile(file.getAbsolutePath(), reportName.split("\\.")[0] + "." + fileName.split("\\.")[1], key));
				}
				FutureTask<Boolean> aliyunTask = new FutureTask<>(new Callable<Boolean>() {

					@Override
					public Boolean call() throws Exception {
						for (File file : files) {
							AliyunUploadFileUtil.saveFile(Context.OSS_FILE_SMS, file, aliyunCon);
						}
						return Boolean.TRUE;
					}
				});
				try{
					aliyunTask.get(5, TimeUnit.SECONDS);
				}catch(TimeoutException e){
					logger.error("上传至Aliyun服务器超时, key=" + key + ",reason=" + e.getMessage());
				}catch(Exception e){
					logger.error("上传至Aliyun服务器失败, key=" + key + ",reason=" + e.getMessage());
				}
			}
			delayAfterSpecialSeconds("发送邮件", hour);
			String key = AbstractTaskBrahch.this.key;
			UnsentFile[] ufArray = new UnsentFile[unsentFiles.size()];
			unsentFiles.toArray(ufArray);
			//添加待发送邮件附件任务
			UnsentAttachQueue.getInstance().addTask(key, pkg, compress, ufArray);
			return true;
		}
		
		private boolean delayAfterSpecialSeconds(String message, Integer hour) throws InterruptedException{
			long delayTime = 0L;
			Calendar calendar = Calendar.getInstance();
			long millions = calendar.getTimeInMillis();
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, 0);
			
			//TODO 更改为测试代码
//			delayTime = (calendar.getTimeInMillis() - millions) / 1000;
			delayTime = (calendar.getTimeInMillis() - millions);
			if(delayTime < 0){
				logger.info("[key=" + key + "]的任务已经超时，" + (ignore ? "准备丢弃" : "直接[" + message + "]") + "...");
				return !ignore;
			}
			logger.info("[key=" + key + "]的任务进入休眠状态，预计" + (delayTime/1000) + "秒后进行[" + message + "]");
			// 这里不能调用interrupted()方法，否则会改变中断状态，即不会实际上进行中断
			for(long single = 0L, TEN_SECONDS = 10000L, ONE_SECOND = 1000L; delayTime > 1000 && !Thread.currentThread().isInterrupted();){
				single = delayTime > TEN_SECONDS ? TEN_SECONDS : ONE_SECOND;
				TimeUnit.MILLISECONDS.sleep(single);
				delayTime = delayTime - single;
				logger.info("[key=" + key + "]的任务距离[" + message + "]时间还剩" + (delayTime/1000) + "秒");
			}
			return true;
		}

		@Override
		public int getSpecificHour() {
			return hour.intValue();
		}
		
//		private void caculateDelayTime(){
//			Calendar calendar = Calendar.getInstance();
//			long millions = calendar.getTimeInMillis();
//			calendar.set(Calendar.HOUR_OF_DAY, hour);
//			calendar.set(Calendar.MINUTE, 0);
//			calendar.set(Calendar.SECOND, 0);
//			calendar.add(Calendar.MINUTE, hour);
//			this.delayTime = calendar.getTimeInMillis() - millions;
//		}
	}
	
	/**
	 * 采用Spring的Bean后处理器，当Bean生成完整后，进行Excel模板的注册
	 * @throws Exception
	 */
	public void afterPropertiesSet() throws Exception {
		//上传至文件服务器
		aliyunCon = new AliyunData();
		org.springframework.beans.BeanUtils.copyProperties(aliyunConfig, aliyunCon);
	}
}
