package os.component.upload.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import os.component.upload.service.FileUploadService;
import os.component.upload.template.FileUploadTemplate;

@Slf4j
@RequestMapping("fileUploadEndpoint")
public class FileUploadEndpoint extends MarkController {
    private FileUploadService fileUploadService;
    private FileUploadTemplate fileUploadTemplate;

    public void setFileUploadService(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    public void setFileUploadTemplate(FileUploadTemplate fileUploadTemplate) {
        this.fileUploadTemplate = fileUploadTemplate;
    }

    @RequestMapping("uploadFile")
    public void uploadFile(MultipartHttpServletRequest request) {

    }
}
