package os.component.upload.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "owinfo.upload")
public class FileUploadConfig {
    private boolean enabled = false;
    // ftp、minio、fastdfs
    private String type = "minio";
}
