package com.weyao.srv.task.branch.mail;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weyao.srv.report.sent.TinyUnsentTaskFlow;
import com.weyao.srv.report.sent.UnsentAttachQueue;
import com.weyao.srv.report.sent.UnsentFile;
import com.weyao.srv.report.sent.UnsentQueue;

/**
 * 发送邮件的定时任务
 * @author dujingjing
 * @version 1.0
 */
public class ReportMailTask{

	private static Logger logger = LoggerFactory.getLogger(ReportMailTask.class);

	private Calendar calendar = Calendar.getInstance();
	
	private String smtp;
    private String username;   
    private String password;
//    private String mailSubject;//标题
//    private String content;//内容
//    private String mailAddr;//收件人
//    private String from;//发件人
//    private String nickname;
    private static boolean auth = true;
    
    private MetaMailPkg pkg;
    
    /*
     * 待发送邮件任务队列
     */
    private UnsentQueue unsentQueue = UnsentAttachQueue.getInstance();
    
    /**
     * 在构造函数中加载配置信息
     * @param pkg
     */
    public ReportMailTask(MetaMailPkg pkg) {
    	smtp = pkg.smtp;
        username = pkg.username;   
        password = pkg.password;
        this.pkg = pkg;
    }

    public Integer action(boolean compress) {
    	int count = 0;
    	Properties props = new Properties();
		props.put("mail.smtp.host", smtp);
		props.put("mail.smtp.auth", auth);
		Session session = Session.getDefaultInstance(props, null); //获得邮件会话对象
    	try{
    		Transport transport = session.getTransport("smtp");
    		logger.debug("开始生成邮件...");
		    synchronized (unsentQueue) {
		    	MetaMailPkg.EmailTarget et;
		    	while(unsentQueue.hasMoreTask()){
		        	TinyUnsentTaskFlow flow = unsentQueue.getTask();
		        	//获取对应的配置
		        	String key = flow.getKey();
		        	et = this.pkg.getEmailTarget(key);
		        	logger.debug("收件人： " + et.mailAddr);
		        	//进行线程暂停
//		        	if(!flow.delay(unsentQueue)){
//			            flow.finish();
//		        		continue;
//		        	}
				    transport.connect(smtp, username, password);
			    	//设置报表时间，为当前时间-1天。
			    	calendar.setTime(new Date());
			    	if(!key.equals("yyd_online_payment")){
				    	calendar.add(Calendar.DAY_OF_YEAR, -1);
			    	}
			    	String reportDate = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
		        	Multipart mp = new MimeMultipart();  //Multipart对象,邮件内容,标题,附件等内容均添加到其中后再生成MimeMessage对象

		        	Address address = new InternetAddress(username);
		            MimeMessage mimeMsg = new MimeMessage(session); //创建MIME邮件对象
		            mimeMsg.setSubject(MimeUtility.encodeText(String.format(et.mailSubject, reportDate), "UTF-8","B"));
		            mimeMsg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(MimeUtility.encodeText(et.mailAddr, "UTF-8", "B")));
		            mimeMsg.setFrom(address); //设置发信人
		            
		            //添加正文信息
		            BodyPart bp = new MimeBodyPart();
		            bp.setContent(String.format(et.content, reportDate), "text/html; charset=UTF-8");
		            mp.addBodyPart(bp);
		            mimeMsg.isMimeType("text/html");
		            mimeMsg.setContent(mp);
		            ZipHandler handler = null;
		            if(compress){
		            	handler = new ZipHandler();
		            }
		            //添加附件
		            MimeBodyPart mbp = null;
		            for (UnsentFile unsentFile : flow.getUnsentList()) {
		            	String fileName = String.format(unsentFile.getFileName(), reportDate);
		            	String filePath = unsentFile.getFilePath();
		            	mbp = new MimeBodyPart();
		                //添加附件
		                FileDataSource fds = new FileDataSource(filePath); //得到数据源  
		                mbp.setDataHandler(new DataHandler(fds)); //得到附件本身并至入BodyPart  
		                //对于邮件内容的字符编码进行操作。
//		                sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
//		                mbp.setFileName("=?GBK?B?" + enc.encode(String.format(unsentFile.getFileName(), reportDate).getBytes())+"?=");//得到文件名同样至入BodyPart 
		                mbp.setFileName(MimeUtility.encodeText(fileName));  //得到文件名同样至入BodyPart 
		                mp.addBodyPart(mbp);
		                if(handler != null){
		                	handler.compressFile(fileName, filePath);
		                }
					}
		            if(handler != null){
		            	addFileToMail(handler.close(), mp, "整合报表数据.zip");
		            }
		            mimeMsg.saveChanges();
		            //发送邮件
		            transport.sendMessage(mimeMsg, mimeMsg.getRecipients(Message.RecipientType.TO));
		            transport.close();
		            //执行回调处理
		            flow.finish();
				    logger.debug("邮件发送成功...");
				    count++;
		        }
			}
			return count;
		}catch(Exception e){
        	logger.error("mail sent failed!" + e);
        	e.printStackTrace();
        	if (e instanceof SendFailedException) {
        		SendFailedException se = (SendFailedException) e;
        		Address[] unsend = se.getValidUnsentAddresses();
        		if(null!=unsend) {
        			String validAddress = "";
        			 for(int i=0;i<unsend.length;i++) {
        				 validAddress += unsend[i] + ",";
        			 }
        			 validAddress = validAddress.substring(0,validAddress.length()-1);
 		            sendFailMail(compress, validAddress); 
        		}
        	}
        	return -1;
		}
    }
    
    private void addFileToMail(File file, Multipart mp, String fileName) throws MessagingException, UnsupportedEncodingException{
    	MimeBodyPart mbp = new MimeBodyPart();
        //添加附件
        FileDataSource fds = new FileDataSource(file); //得到数据源  
        mbp.setDataHandler(new DataHandler(fds)); //得到附件本身并至入BodyPart  
        //对于邮件内容的字符编码进行操作。
//        sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
//        mbp.setFileName("=?GBK?B?" + enc.encode(String.format(unsentFile.getFileName(), reportDate).getBytes())+"?=");//得到文件名同样至入BodyPart 
        mbp.setFileName(MimeUtility.encodeText(fileName));  //得到文件名同样至入BodyPart 
        mp.addBodyPart(mbp);
    }
    
//    private void addFileToMail(String filePath, Multipart mp, String fileName) throws MessagingException, UnsupportedEncodingException{
//    	File file = new File(filePath);
//    	addFileToMail(file, mp, fileName);
//    }
    
    /**
     * TODO 后期业务复杂之后，将该部分提取出来作为一个独立的模块使用
     * 暂时将ZIP处理类作为内部类使用，便于实例化
     * @author dujingjing
     *
     */
    private static class ZipHandler{
    	
    	private File out;
    	
    	private ZipOutputStream zos;
    	
    	private BufferedOutputStream bos;
    	
    	ZipHandler() throws IOException{
    		String fileName = String.format("%s.zip", System.currentTimeMillis() + new Random().nextInt());
    		out = com.weyao.boss.common.util.FileUtil.createTempFile(fileName);
        	CheckedOutputStream csum = new CheckedOutputStream(new FileOutputStream(out), new Adler32());
    		//创建压缩输出流
        	zos = new ZipOutputStream(csum);
        	bos = new BufferedOutputStream(zos);
    	}
    	
    	void compressFile(String fileName, String filePath) throws IOException{
    		FileInputStream fis = new FileInputStream(new File(filePath));
			zos.putNextEntry(new ZipEntry(fileName));
			byte[] bytes = new byte[1024];
			while(fis.read(bytes) != -1){
				bos.write(bytes);
			}
			fis.close();
			bos.flush();
    	}
    	
    	File close() throws IOException{
    		this.bos.close();
    		this.zos.close();
    		return out;
    	}
    }
    
    //send the mail when mail address wrong.  
    private Integer sendFailMail(boolean compress, String mailTOAddress ) {  
    	int count = 0;
    	Properties props = new Properties();
		props.put("mail.smtp.host", smtp);
		props.put("mail.smtp.auth", auth);
		Session session = Session.getDefaultInstance(props, null); //获得邮件会话对象
    	try{
    		Transport transport = session.getTransport("smtp");
    		logger.debug("开始生成邮件...");
		    synchronized (unsentQueue) {
		    	MetaMailPkg.EmailTarget et;
		    	while(unsentQueue.hasMoreTask()){
		        	TinyUnsentTaskFlow flow = unsentQueue.getTask();
		        	//获取对应的配置
		        	String key = flow.getKey();
		        	et = this.pkg.getEmailTarget(key);
		        	logger.debug("收件人： " + mailTOAddress);
		        	
				    transport.connect(smtp, username, password);
			    	//设置报表时间，为当前时间-1天。
			    	calendar.setTime(new Date());
			    	if(!key.equals("yyd_online_payment")){
				    	calendar.add(Calendar.DAY_OF_YEAR, -1);
			    	}
			    	String reportDate = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
		        	Multipart mp = new MimeMultipart();  //Multipart对象,邮件内容,标题,附件等内容均添加到其中后再生成MimeMessage对象

		        	Address address = new InternetAddress(username);
		            MimeMessage mimeMsg = new MimeMessage(session); //创建MIME邮件对象
		            mimeMsg.setSubject(MimeUtility.encodeText(String.format(et.mailSubject, reportDate), "UTF-8","B"));
		            mimeMsg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(MimeUtility.encodeText(mailTOAddress, "UTF-8", "B")));
		            mimeMsg.setFrom(address); //设置发信人
		            
		            //添加正文信息
		            BodyPart bp = new MimeBodyPart();
		            bp.setContent(String.format(et.content, reportDate), "text/html; charset=UTF-8");
		            mp.addBodyPart(bp);
		            mimeMsg.isMimeType("text/html");
		            mimeMsg.setContent(mp);
		            ZipHandler handler = null;
		            if(compress){
		            	handler = new ZipHandler();
		            }
		            //添加附件
		            MimeBodyPart mbp = null;
		            for (UnsentFile unsentFile : flow.getUnsentList()) {
		            	String fileName = String.format(unsentFile.getFileName(), reportDate);
		            	String filePath = unsentFile.getFilePath();
		            	mbp = new MimeBodyPart();
		                //添加附件
		                FileDataSource fds = new FileDataSource(filePath); //得到数据源  
		                mbp.setDataHandler(new DataHandler(fds)); //得到附件本身并至入BodyPart  
		                //对于邮件内容的字符编码进行操作。
		                mbp.setFileName(MimeUtility.encodeText(fileName));  //得到文件名同样至入BodyPart 
		                mp.addBodyPart(mbp);
		                if(handler != null){
		                	handler.compressFile(fileName, filePath);
		                }
					}
		            if(handler != null){
		            	addFileToMail(handler.close(), mp, "整合报表数据.zip");
		            }
		            mimeMsg.saveChanges();
		            //发送邮件
		            transport.sendMessage(mimeMsg, mimeMsg.getRecipients(Message.RecipientType.TO));
		            transport.close();
		            //执行回调处理
		            flow.finish();
				    logger.debug("邮件发送成功...");
				    count++;
		        }
			}
			return count;
		}catch(Exception e){
        	logger.error("mail sent failed!" + e);
        	e.printStackTrace();
        	return -1;
		}
    }
}