package os.component.upload.ftp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.util.StringUtils;
import os.component.upload.FileUploadClient;
import os.component.upload.FileUploadReply;
import os.component.upload.util.FileUploadUtils;

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
        FileUploadReply existReply = exist(fileUuid, remoteFileName, remoteDir);
        if (!existReply.isSuccess()) {
            return FileUploadReply.error("Download error, File does not exist.");
        }

        boolean b = client.changeWorkingDirectory(existReply.getRemoteDir());
        if (!b) {
            return FileUploadReply.error("Download error, File is not exist.");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            client.retrieveFile(existReply.getRemoteFileName(), outputStream);
            byte[] byteArray = outputStream.toByteArray();
            ByteBuffer byteBuffer = ByteBuffer.allocate(byteArray.length);
            byteBuffer.put(byteArray);
            byteBuffer.flip();
            existReply.setByteBuffer(byteBuffer);
            existReply.setReplyMsg("Download success.");
        } catch (Exception e) {
            log.error("File Download Exception, Exception={}", e.toString());
            return FileUploadReply.error("File Download Exception, EX=" + e);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
        return existReply;
    }

    @Override
    public FileUploadReply deleteFile(String fileUuid, String remoteFileName, String remoteDir) throws Exception {
        FileUploadReply exist = exist(fileUuid, remoteFileName, remoteDir);
        if (!exist.isSuccess()) {
            return FileUploadReply.error("Delete error, File does not exist.");
        }

        boolean b = client.changeWorkingDirectory(exist.getRemoteDir());
        if (!b) {
            return FileUploadReply.error("Delete error, File does not exist.");
        }

        try {
            client.deleteFile(exist.getRemoteFileName());
            return FileUploadReply.success("File Deleted Success.");
        } catch (Exception e) {
            log.error("File Delete Exception, Exception={}", e.toString());
            return FileUploadReply.error("File Delete Exception");
        }
    }

    @Override
    public FileUploadReply exist(String fileUuid, String remoteFileName, String remoteDir) throws Exception {
        if (FileUploadUtils.emptyAll(fileUuid, remoteFileName, remoteDir)) {
            return FileUploadReply.error("File does not exist.");
        }

        int index = remoteFileName.indexOf("-");
        String fileName = remoteFileName.substring(index + 1);
        String fileRemoteName = fileUuid + "-" + new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

        FTPFile[] ftpFiles = client.listFiles(remoteDir + "/" + fileRemoteName);
        if (ftpFiles == null || ftpFiles.length == 0) {
            return FileUploadReply.error("File does not exist.");
        }

        FileUploadReply fileUploadReply = FileUploadReply.success("File exist.");
        fileUploadReply.setFileOriginName(fileName);
        fileUploadReply.setRemoteFileName(fileRemoteName);
        fileUploadReply.setRemoteDir(remoteDir);
        fileUploadReply.setFileUuid(fileUuid);
        return fileUploadReply;
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
