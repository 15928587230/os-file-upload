package os.component.upload.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "owinfo.upload")
public class FileUploadConfig {
    private boolean enabled = false;
    private boolean endpointEnabled = false;
    // FTP、MINIO、FASTDFS
    private String type = "MINIO";
    // 返回nginx地址，通过该地址 + 文件的相关元数据信息预览或者下载
    // 拼接方式 nginxUrl + remoteDir + "/" + remoteFileName
    private String nginxUrl;

    // 自定义数据源相关
    private String driverClassName;
    private String url;
    private String username;
    private String password;
}
