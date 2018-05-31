package com.weyao.srv.disocnfi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfItem;
import com.baidu.disconf.client.common.annotations.DisconfUpdateService;

@Service
@Scope("singleton")
@DisconfUpdateService(itemKeys = { "youyidan_app_id" })
public class AppConfig {
    private static final Log LOGGER=LogFactory.getLog(AppConfig.class);
    private static String youYiDanAppId;
    
    @DisconfItem(key = "youyidan_app_id", associateField = "youYiDanAppId")
    public static String getYouYiDanAppId() {
        return AppConfig.youYiDanAppId;
    }


    public static void setYouYiDanAppId(String youYiDanAppId) {
        AppConfig.youYiDanAppId = youYiDanAppId;
        LOGGER.debug(String.format("current youyidan_app_id is【%s】", youYiDanAppId));
    }
}
