package os.component.upload.fdfs;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.util.StringUtils;
import os.component.upload.FileUploadClient;
import os.component.upload.FileUploadReply;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

@Slf4j
public class FdfsClientWrapper implements FileUploadClient {
    private final StorageClient client;
    private final TrackerServer trackerServer;

    public FdfsClientWrapper(StorageClient client, TrackerServer trackerServer) {
        this.client = client;
        this.trackerServer = trackerServer;
    }

    public StorageClient getClient() {
        return client;
    }

    public TrackerServer getTrackerServer() {
        return trackerServer;
    }

    /**
     * String new_group_name = (new String(pkgInfo.body, 0, 16)).trim();
     * String remote_filename = new String(pkgInfo.body, 16, pkgInfo.body.length - 16);
     * results = new String[]{new_group_name, remote_filename};
     * if (meta_list == null || meta_list.length == 0) {
     * String[] var230 = results;
     * return var230;
     * }
     *
     * @param inputStream 文件的输入流
     * @param fileName    文件中文简称
     * @return
     * @throws Exception
     */
    @Override
    public FileUploadReply uploadFile(InputStream inputStream, String fileName) throws Exception {
        byte[] bytes = inputStreamToByteArray(inputStream);
        // 如上返回的是groupName和remoteFileName
        String extensionName = FilenameUtils.getExtension(fileName);
        String[] uploadResult = client.upload_file(bytes, extensionName, null);
        if (uploadResult != null && uploadResult.length == 2) {
            String groupName = uploadResult[0];
            String filePath = uploadResult[1];

            int index = filePath.lastIndexOf("/");
            String remoteDir = "/" + groupName + "/" + filePath.substring(0, index);
            String remoteFileName = filePath.substring(index + 1);
            String fileUuid = "";
            int lastIndex;
            if (StringUtils.hasLength(remoteFileName) && (lastIndex = remoteFileName.lastIndexOf(".")) != -1) {
                fileUuid = remoteFileName.substring(0, lastIndex);
            }
            return FileUploadReply.reply(FTPReply.COMMAND_OK, fileUuid, remoteFileName, remoteDir);
        }
        return FileUploadReply.error("FDFS Upload Error.");
    }

    /**
     * 这样返回的 fileUuid = wKgUFGRUZKmAFJsTAE-FMPVMIn4926
     * remoteFileName=wKgUFGRUZKmAFJsTAE-FMPVMIn4926.exe, fileRemoteDir=/group1/M00/00/00
     *
     * @param fileUuid 文件UUID
     * @param remoteFileName 文件名称
     * @param remoteDir 文件所在远程目录
     * @return
     * @throws Exception
     */
    @Override
    public FileUploadReply downloadFile(String fileUuid, String remoteFileName, String remoteDir) throws Exception {
        if (StringUtils.isEmpty(remoteDir) || StringUtils.isEmpty(remoteFileName) || StringUtils.isEmpty(fileUuid)) {
            return FileUploadReply.error("fileUuid or remoteFileName or remote Dir can not be empty.");
        }

        String[] remoteMetaInfo = remoteDir.split("/");
        String extension = FilenameUtils.getExtension(remoteFileName);
        String fileName = fileUuid + "." + extension;
        if (remoteMetaInfo.length != 5) {
            return FileUploadReply.error("Remote Dir Error");
        }
        remoteFileName = remoteMetaInfo[2] + "/" + remoteMetaInfo[3] + "/" + remoteMetaInfo[4] + "/" + fileName;
        String groupName = remoteMetaInfo[1];

        FileUploadReply fileUploadReply = FileUploadReply.success("Download Success.");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            byte[] byteArray = client.download_file(groupName, remoteFileName);
            ByteBuffer byteBuffer = ByteBuffer.allocate(byteArray.length);
            byteBuffer.put(byteArray);
            byteBuffer.flip();
            fileUploadReply.setByteBuffer(byteBuffer);
        } catch (Exception e) {
            log.error("File Download Exception, Exception={}", e.toString());
            return FileUploadReply.error("File Download Exception");
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

        String[] remoteMetaInfo = remoteDir.split("/");
        String extension = FilenameUtils.getExtension(remoteFileName);
        String fileName = fileUuid + "." + extension;
        if (remoteMetaInfo.length != 5) {
            return FileUploadReply.error("Remote Dir Error");
        }
        remoteFileName = remoteMetaInfo[2] + "/" + remoteMetaInfo[3] + "/" + remoteMetaInfo[4] + "/" + fileName;
        String groupName = remoteMetaInfo[1];
        try {
            client.delete_file(groupName, remoteFileName);
        } catch (Exception ex) {
            log.error("Delete File Exception, EX = {}", ex.toString());
            return FileUploadReply.error("Delete File Exception, Exception = " + ex);
        }
        return FileUploadReply.success("Delete File Success.");
    }

    public byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
        return outputStream.toByteArray();
    }
}
