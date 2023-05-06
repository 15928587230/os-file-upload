package os.component.upload.ftp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.util.StringUtils;
import os.component.upload.FileUploadClient;
import os.component.upload.FileUploadReply;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
public class FTPClientWrapper implements FileUploadClient {
    private final FTPClient client;

    public FTPClientWrapper(FTPClient client) {
        this.client = client;
    }

    public FTPClient getClient() {
        return client;
    }

    @Override
    public FileUploadReply uploadFile(InputStream inputStream, String fileName) throws Exception {
        String remoteDir = getRemoteDir();
        if (StringUtils.isEmpty(remoteDir)) {
            remoteDir = "/temp/";
        }

        createDirectoryTree(remoteDir);
        client.changeWorkingDirectory(remoteDir);
        // 这里留给本地使用，远程需要转成StandardCharsets.ISO_8859_1
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String localRemoteFileName = uuid.concat("-").concat(fileName);
        // 这里本地是乱码的，但是FTP显示是正常的
        String remoteFileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        client.storeFile(uuid.concat("-").concat(remoteFileName), inputStream);
        FileUploadReply reply = FileUploadReply.reply(FTPReply.COMMAND_OK, uuid, localRemoteFileName, remoteDir);
        reply.setFileOriginName(fileName);
        return reply;
    }

    /**
     * 文件下载
     *
     * @param fileUuid  文件UUID
     * @param remoteFileName  文件名称
     * @param remoteDir 文件所在远程目录
     * @return FileUploadReply
     */
    @Override
    public FileUploadReply downloadFile(String fileUuid, String remoteFileName, String remoteDir) throws Exception {
        if (StringUtils.isEmpty(remoteDir) || StringUtils.isEmpty(remoteFileName) || StringUtils.isEmpty(fileUuid)) {
            return FileUploadReply.error("fileUuid or remoteFileName or remote Dir can not be empty.");
        }

        boolean b = client.changeWorkingDirectory(remoteDir);
        if (!b) {
            return FileUploadReply.error("Download error, remoteDir is not exist.");
        }

        int index = remoteFileName.indexOf("-");
        String fileName = remoteFileName.substring(index + 1);

        FileUploadReply fileUploadReply = FileUploadReply.success("Download Success.");
        String fileRemoteName = fileUuid + "-" + new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            client.retrieveFile(fileRemoteName, outputStream);
            byte[] byteArray = outputStream.toByteArray();
            ByteBuffer byteBuffer = ByteBuffer.allocate(byteArray.length);
            byteBuffer.put(byteArray);
            byteBuffer.flip();
            fileUploadReply.setByteBuffer(byteBuffer);
        } catch (Exception e) {
            log.error("File Download Exception, Exception={}", e.toString());
            return FileUploadReply.error("File Download Exception, EX=" + e);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
        return fileUploadReply;
    }

    @Override
    public FileUploadReply deleteFile(String fileUuid, String remoteFileName, String remoteDir) throws Exception {
        if (StringUtils.isEmpty(remoteDir) || StringUtils.isEmpty(remoteFileName) || StringUtils.isEmpty(fileUuid)) {
            return FileUploadReply.error("fileUuid or remoteFileName or remote Dir can not be empty.");
        }

        boolean b = client.changeWorkingDirectory(remoteDir);
        if (!b) {
            return FileUploadReply.error("Download error, remoteDir is not exist.");
        }

        int index = remoteFileName.indexOf("-");
        String fileName = remoteFileName.substring(index + 1);

        String fileRemoteName = fileUuid + "-" + new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        try {
            boolean deleteFile = client.deleteFile(fileRemoteName);
            if (deleteFile) {
                return FileUploadReply.success("File Deleted Success.");
            }
            throw new RuntimeException("File Delete Exception");
        } catch (Exception e) {
            log.error("File Delete Exception, Exception={}", e.toString());
            return FileUploadReply.error("File Delete Exception");
        }
    }

    /**
     * 直接年月日目录在服务器创建目录
     */
    public String getRemoteDir() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        return "/" + year + "/" + month + "/" + day;
    }

    /**
     * 按照层级生成目录
     *
     * @param dirTree 目录以/切割
     */
    public void createDirectoryTree(String dirTree) throws Exception {
        boolean dirExists = true;

        // tokenize the string and attempt to change into each directory level. If you cannot, then start creating.
        String[] directories = dirTree.split("/");
        for (String dir : directories) {
            if (!dir.isEmpty()) {
                if (dirExists) {
                    dirExists = client.changeWorkingDirectory(dir);
                }
                if (!dirExists) {
                    if (!client.makeDirectory(dir)) {
                        throw new RuntimeException("Unable to create remote directory '" + dir + "'.  error='" + client.getReplyString() + "'");
                    }
                    if (!client.changeWorkingDirectory(dir)) {
                        throw new RuntimeException("Unable to change into newly created remote directory '" + dir + "'.  error='" + client.getReplyString() + "'");
                    }
                }
            }
        }
    }
}
