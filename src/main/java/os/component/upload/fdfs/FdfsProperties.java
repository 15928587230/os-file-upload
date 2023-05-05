package os.component.upload.fdfs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "owinfo.fastdfs")
public class FdfsProperties {
    private String trackerServers;
    private int connectTimeoutSecond = 5;
    private int soTimeoutSecond = 10;
    private String charset = "UTF-8";
    // 默认关闭token验证
    private boolean stealToken = false;
    private String secretKey = "FastDFS1234567890";
    private int trackerHttpPort = 80;
    private int poolSize = 10;
    // 重试次数
    private int retryTimes = 3;
    // borrowMaxWailTimeMills，borrow最大等待时间
    private int borrowMaxWailTimeMills = 10_000;
    // maxIdle
    private int maxIdle = 10;
    // minIdle
    private int midIdle = 10;
    // maxTotal
    private int maxTotal = 10;
}
