package os.component.upload.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RequestMapping("fileUploadEndpoint")
public class FileUploadEndpoint extends MarkController {
    private static final String FILE_NAME = "file";
    private FileUploadService fileUploadService;
    private FileUploadTemplate fileUploadTemplate;
    private FileUploadConfig fileUploadConfig;

    /**
     * 这里的dataUuid可能没那么简单，比如表单单条数据可能有多个字段都是图片上传
     * 因此这里的dataUuid字段唯一值应该为数据的oid + 表单的唯一英文名称 + 字段的唯一英文名称
     *
     * @param dataUuid
     * @return
     */
    @PostMapping("uploadFile")
    public Result uploadFile(MultipartHttpServletRequest request, @Valid @NotBlank(message = "dataUuid：不能为空") @RequestParam String dataUuid) {
        MultipartFile file = request.getFile(FILE_NAME);
        assert file != null;
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            FileUploadReply fileUploadReply = fileUploadTemplate.uploadFile(inputStream, file.getOriginalFilename());
            FileUpload fileUpload = saveFileInfo(fileUploadReply, dataUuid);
            fileUpload.setViewOrDownloadUrl(getViewOrDownloadUrl(fileUpload.getRemoteDir(), fileUpload.getRemoteFileName()));
            return Result.build(Status.getInstance(fileUploadReply.getReplyCode()), fileUploadReply.getReplyMsg(), fileUpload);
        } catch (Exception ex) {
            log.error("File Upload Exception，EX = {}", ex.toString());
            Result.build(Status.FAILURE, ex.toString());
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return Result.failure();
    }

    @GetMapping("getFileInfoList")
    public Result getFileInfoList(@Valid @NotBlank(message = "dataUuid：不能为空") @RequestParam String dataUuid) {
        List<FileUpload> fileList = fileUploadService.getFileList(dataUuid);
        fileList.forEach(fileUpload -> {
            fileUpload.setViewOrDownloadUrl(getViewOrDownloadUrl(fileUpload.getRemoteDir(), fileUpload.getRemoteFileName()));
        });
        return Result.build(Status.SUCCESS, "Get File Info List Success.", fileList);
    }

    // 附件或者图片删除
    @GetMapping("deleteFile")
    public Result deleteFile(@Valid @NotBlank(message = "fileUuid：不能为空") @RequestParam String fileUuid) {
        FileUpload file = fileUploadService.getFile(fileUuid);
        if (file == null) {
            return Result.build(Status.FAILURE, "File Does Not Exist.");
        }

        fileUploadService.deleteFile(fileUuid);
        try {
            // 这里删除失败，不管，权当冗余存储了。
            fileUploadTemplate.deleteFile(fileUuid, file.getRemoteFileName(), file.getRemoteDir());
        } catch (Exception ex) {
            log.error("File Delete Exception, EX={}", ex.toString());
        }
        return Result.build(Status.SUCCESS, "File Delete Success.");
    }

    // 附件或者图片下载
    @GetMapping("downloadFile")
    public void downloadFile(HttpServletResponse response, @Valid @NotBlank(message = "fileUuid：不能为空") @RequestParam String fileUuid) throws Exception {
        FileUpload file = fileUploadService.getFile(fileUuid);
        if (file == null || StringUtils.isEmpty(file.getRemoteFileName())
                || StringUtils.isEmpty(file.getRemoteDir())) {
            writeResponse(response, Status.FAILURE, "File Does Not Exist.");
            return;
        }

        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getFileOriginName(), "UTF-8"));

        OutputStream outputStream = null;
        try {
            FileUploadReply fileUploadReply = fileUploadTemplate.downloadFile(fileUuid, file.getRemoteFileName(), file.getRemoteDir());
            if (!fileUploadReply.isSuccess()) {
                throw new RuntimeException(fileUploadReply.getReplyMsg());
            }
            outputStream = response.getOutputStream();
            ByteBuffer byteBuffer = fileUploadReply.getByteBuffer();
            response.addHeader("Content-Length", String.valueOf(byteBuffer.limit()));
            outputStream.write(byteBuffer.array());
            response.flushBuffer();
        } catch (Exception ex) {
            log.error("File Download Exception. EX = {}", ex.toString());
            writeResponse(response, Status.FAILURE, ex.toString());
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    @GetMapping("getNginxUrl")
    public Result getNginxUrl() {
        return Result.build(Status.SUCCESS, fileUploadConfig.getNginxUrl());
    }

    protected void writeResponse(HttpServletResponse response, Status status, String msg) throws IOException {
        Result build = Result.build(status, msg);
        response.setContentType("application/json; charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.println(build);
        out.flush();
        out.close();
    }

    protected String getViewOrDownloadUrl(String remoteDir, String remoteFileName) {
        return fileUploadConfig.getNginxUrl() + remoteDir + "/" + remoteFileName;
    }

    public void setFileUploadService(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    public void setFileUploadTemplate(FileUploadTemplate fileUploadTemplate) {
        this.fileUploadTemplate = fileUploadTemplate;
    }

    protected FileUpload saveFileInfo(FileUploadReply fileUploadReply, String dataUuid) {
        if (fileUploadReply != null && fileUploadReply.isSuccess()) {
            FileUpload fileUpload = new FileUpload();
            fileUpload.setFileUuid(fileUploadReply.getFileUuid());
            fileUpload.setDataUuid(dataUuid);
            fileUpload.setFileOriginName(fileUploadReply.getFileOriginName());
            fileUpload.setRemoteFileName(fileUploadReply.getRemoteFileName());
            fileUpload.setRemoteDir(fileUploadReply.getRemoteDir());
            fileUpload.setUploadTime(LocalDateTime.now());
            fileUploadService.insert(fileUpload);
            return fileUpload;
        }
        return null;
    }

    public void setFileUploadConfig(FileUploadConfig fileUploadConfig) {
        this.fileUploadConfig = fileUploadConfig;
    }
}
