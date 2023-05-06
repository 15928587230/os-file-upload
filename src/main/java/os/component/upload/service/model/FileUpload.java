package os.component.upload.service.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileUpload {

    private String fileUuid;
    private String pkId;
    private String fileOriginName;
    private String remoteFileName;
    private String remoteDir;
    private LocalDateTime uploadTime;
}
