package os.component.upload.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * minio 配置类
 *
 * @author pengjunjie
 */
@Data
@Component
@ConfigurationProperties(prefix = "owinfo.minio")
public class MinioProperties {
    /**
     *  minio访问地址列表
     */
    private String endpoint;
    // 账号
    private String username;
    // 密码
    private String password;
    // borrowMaxWailTimeMills，borrow最大等待时间
    private int borrowMaxWailTimeMills = 10_000;
    // maxIdle
    private int maxIdle = 10;
    // minIdle
    private int midIdle = 10;
    // maxTotal
    private int maxTotal = 10;
    private int connectTimeoutMills = 10_000;
    private int readTimeoutMills = 120_000;
    private int writeTimeoutMills = 120_000;
}
