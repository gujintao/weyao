package com.weyao.srv.disocnfi;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;

@Service
@Scope("singleton")
@DisconfFile(filename = "aliyun.properties")
public class AliyunConfig {
    private String ossKey;

    private String ossSecret;

    private boolean ossDebug;
    
    private String ossFileDomainPrd; //生产环境oss域名
    private String ossFileDomainTest; //测试环境oss域名
    private String ossFilePrefixPrd; //生产环境oss文件夹根目录
    private String ossFilePrefixTest; //测试环境oss文件夹根目录
    
    
    
    @DisconfFileItem(name = "aliyun.oss.key",associateField="ossKey")
	public String getOssKey() {
		return ossKey;
	}

    @DisconfFileItem(name = "oss_file_prefix_prd",associateField="ossFilePrefixPrd")
    public String getOssFilePrefixPrd() {
		return ossFilePrefixPrd;
	}
    
	public void setOssFilePrefixPrd(String ossFilePrefixPrd) {
		this.ossFilePrefixPrd = ossFilePrefixPrd;
	}

	@DisconfFileItem(name = "oss_file_prefix_test",associateField="ossFilePrefixTest")
	public String getOssFilePrefixTest() {
		return ossFilePrefixTest;
	}

	public void setOssFilePrefixTest(String ossFilePrefixTest) {
		this.ossFilePrefixTest = ossFilePrefixTest;
	}

	@DisconfFileItem(name = "oss_file_domain_prd",associateField="ossFileDomainPrd")
	public String getOssFileDomainPrd() {
		return ossFileDomainPrd;
	}

	public void setOssFileDomainPrd(String ossFileDomainPrd) {
		this.ossFileDomainPrd = ossFileDomainPrd;
	}

	 @DisconfFileItem(name = "oss_file_domain_test",associateField="ossFileDomainTest")
	public String getOssFileDomainTest() {
		return ossFileDomainTest;
	}

	public void setOssFileDomainTest(String ossFileDomainTest) {
		this.ossFileDomainTest = ossFileDomainTest;
	}

	public void setOssKey(String ossKey) {
		this.ossKey = ossKey;
	}

	@DisconfFileItem(name = "aliyun.oss.secret",associateField="ossSecret")
	public String getOssSecret() {
		return ossSecret;
	}

	public void setOssSecret(String ossSecret) {
		this.ossSecret = ossSecret;
	}


	@DisconfFileItem(name = "aliyun.oss.debug",associateField="ossDebug")
	public boolean getOssDebug() {
		return ossDebug;
	}

	public void setOssDebug(boolean ossDebug) {
		this.ossDebug = ossDebug;
	}
    
    
}
