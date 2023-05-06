package os.component.upload.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import os.component.upload.FileUploadReply;
import os.component.upload.config.FileUploadConfig;
import os.component.upload.service.FileUploadService;
import os.component.upload.service.model.FileUpload;
import os.component.upload.template.FileUploadTemplate;
import os.component.upload.util.Result;
import os.component.upload.util.Status;

import java.io.InputStream;
import java.time.LocalDateTime;

@Slf4j
@RequestMapping("fileUploadEndpoint")
public class FileUploadEndpoint extends MarkController {
    private static final String FILE_NAME = "file";
    private FileUploadService fileUploadService;
    private FileUploadTemplate fileUploadTemplate;
    private FileUploadConfig fileUploadConfig;

    @PostMapping("uploadFile")
    public Result uploadFile(MultipartHttpServletRequest request, @RequestParam String oid) {
        MultipartFile file = request.getFile(FILE_NAME);
        assert file != null;
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            FileUploadReply fileUploadReply = fileUploadTemplate.uploadFile(inputStream, file.getOriginalFilename());
            saveFileInfo(fileUploadReply, oid);
            return Result.build(Status.getInstance(fileUploadReply.getReplyCode()), fileUploadReply.getReplyMsg(), fileUploadReply);
        } catch (Exception ex) {
            log.error("File Upload Exception，EX = {}", ex.toString());
            Result.build(Status.FAILURE, ex.toString());
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return Result.failure();
    }


    @GetMapping("getNginxUrl")
    public Result getNginxUrl() {
        return Result.build(Status.SUCCESS, fileUploadConfig.getNginxUrl());
    }

    public void setFileUploadService(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    public void setFileUploadTemplate(FileUploadTemplate fileUploadTemplate) {
        this.fileUploadTemplate = fileUploadTemplate;
    }

    protected void saveFileInfo(FileUploadReply fileUploadReply, String pkId) {
        if (fileUploadReply != null && fileUploadReply.isSuccess()) {
            FileUpload fileUpload = new FileUpload();
            fileUpload.setFileUuid(fileUploadReply.getFileUuid());
            fileUpload.setPkId(pkId);
            fileUpload.setFileOriginName(fileUploadReply.getFileOriginName());
            fileUpload.setRemoteFileName(fileUploadReply.getRemoteFileName());
            fileUpload.setRemoteDir(fileUploadReply.getRemoteDir());
            fileUpload.setUploadTime(LocalDateTime.now());
            fileUploadService.insert(fileUpload);
        }
    }

    public void setFileUploadConfig(FileUploadConfig fileUploadConfig) {
        this.fileUploadConfig = fileUploadConfig;
    }
}
