package os.component.upload.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import os.component.upload.FileUploadPool;
import os.component.upload.ftp.FTPClientPool;
import os.component.upload.ftp.FTPClientProperties;
import os.component.upload.template.AbstractUploadTemplate;
import os.component.upload.template.FileUploadTemplate;

/**
 *  同时支持多种存储方式可以后续改造下
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "owinfo.upload", name = "enabled",
        havingValue = "true", matchIfMissing = false)
public class FileUploadAutoconfiguration {
    private final FTPClientProperties ftpProps;
    private final FileUploadConfig uploadConfig;

    public FileUploadAutoconfiguration(FTPClientProperties ftpProps, FileUploadConfig uploadConfig) {
        this.ftpProps = ftpProps;
        this.uploadConfig = uploadConfig;
    }

    @Bean
    public FileUploadPool fileUploadPool() {
        if (FileUploadConstant.FTP.equals(uploadConfig.getType())) {
            return new FTPClientPool(ftpProps);
        }
        log.error("文件上传组件加载失败，未找到合适的上传组件");
        throw new RuntimeException("NO useful fileUpload impl");
    }

    @Bean
    public FileUploadTemplate fileUploadTemplate(FileUploadPool fileUploadPool) {
        return new AbstractUploadTemplate(fileUploadPool);
    }
}
