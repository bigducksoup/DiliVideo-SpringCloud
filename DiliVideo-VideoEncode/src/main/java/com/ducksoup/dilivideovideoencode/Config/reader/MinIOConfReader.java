package com.ducksoup.dilivideovideoencode.Config.reader;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("minio")
public class MinIOConfReader {

    /**
     * 地址
     */
    private String endpoint;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String password;
}
