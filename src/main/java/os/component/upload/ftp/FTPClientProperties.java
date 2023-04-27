package os.component.upload.ftp;

import lombok.Data;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * FTPClient 配置类
 *
 * @author pengjunjie
 */
@Data
@Component
@ConfigurationProperties(prefix = "owinfo.ftp")
public class FTPClientProperties {
    private String host;
    private int port = 21;
    private String username;
    private String password;
    // 默认采用主动模式
    private boolean passiveMode = false;
    private String encoding = "UTF-8";
    // 连接超时时间
    private int connectTimeoutSecond = 3;
    private int transferFileType = FTPClient.BINARY_FILE_TYPE;
    private boolean renameUploaded = false;
    // 尝试连接次数
    private int retryTimes = 3;
    // 获取数据超时时间
    private int dataTimeoutSecond = 120;
    // socket阻塞时间
    private int soTimeoutSecond = 10;
    // borrowMaxWailTimeMills，borrow最大等待时间
    private int borrowMaxWailTimeMills = 10_000;
    // maxIdle
    private int maxIdle = 10;
    // minIdle
    private int midIdle = 10;
    // maxTotal
    private int maxTotal = 10;
}
 