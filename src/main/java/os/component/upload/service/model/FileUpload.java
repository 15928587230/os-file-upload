package os.component.upload.service.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileUpload {

    private String fileUuid;
    private String dataUuid;
    private String fileOriginName;
    private String remoteFileName;
    private String remoteDir;
    private LocalDateTime uploadTime;

    // 每个文件给一个nginxUrl预览或者可通过nginx下载的地址
    private String viewOrDownloadUrl;
}
