package os.component.upload;

import java.io.InputStream;

public interface FileUploadClient {
    /**
     * 上传文件到指定目录
     *
     * @param inputStream 文件的输入流
     * @param fileName 文件中文简称
     * @throws Exception
     */
    FileUploadReply uploadFile(InputStream inputStream, String fileName) throws Exception;

    /**
     * 文件下载
     *
     * @param fileUuid 文件UUID
     * @param fileName 文件名称
     * @param remoteDir 文件所在远程目录
     * @return FileUploadReply
     */
    FileUploadReply downloadFile(String fileUuid, String fileName, String remoteDir) throws Exception;
}
