package com.weyao.srv.task.branch.mail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.baidu.disconf.client.addons.properties.ReloadablePropertiesFactoryBean;
import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;

/**
 * 简单的值类，用于存放邮件相关配置
 * @author dujingjing
 *
 */
@Scope("singleton")
@Component
@DisconfFile(filename = "mail.properties")
public final class MetaMailPkg {

	String smtp;
    String username;   
    String password;
    boolean auth = true;


    private static Map<String, EmailTarget> EMAIL_TARGET = new HashMap<String, EmailTarget>();
    
    private static String PREFIX_MAILSUBJECT = "mail.mailSubject.";
    private static String PREFIX_CONTENT = "mail.content.";
    private static String PREFIX_MAILADDRESS = "mail.mailAddr.";
    

    @Autowired
    private ReloadablePropertiesFactoryBean configure;

    /**
     * 根据对应的key来获取对应的emailTarget
     * @param key
     * @return
     * @throws IOException
     */
    EmailTarget getEmailTarget(String key) throws IOException{
    	return EMAIL_TARGET.containsKey(key) ? EMAIL_TARGET.get(key) : new EmailTarget(key);
    }
    
	/**
     * 表示一个key对应的地址列表
     * @author dujingjing
     *
     */
    class EmailTarget{
    	
    	String mailSubject;
    	
    	String content;
    	
    	String mailAddr;
    	
    	EmailTarget(String key) throws IOException{
    		Properties prop = configure.getObject();
    		this.mailSubject = prop.getProperty(PREFIX_MAILSUBJECT + key);
    		this.content = prop.getProperty(PREFIX_CONTENT + key);
    		this.mailAddr = prop.getProperty(PREFIX_MAILADDRESS + key);
//    		this.mailAddr = prop.getProperty(PREFIX_MAILADDRESS + "test");
    		EMAIL_TARGET.put(key, this);
    	}
    }
    
    @DisconfFileItem(name = "mail.smtp.host",associateField="smtp")
	public void setSmtp(String smtp) {
		this.smtp = smtp;
	}

	@DisconfFileItem(name = "mail.username",associateField="username")
	public void setUsername(String username) {
		this.username = username;
	}

	@DisconfFileItem(name = "mail.password",associateField="password")
	public void setPassword(String password) {
		this.password = password;
	}
}
