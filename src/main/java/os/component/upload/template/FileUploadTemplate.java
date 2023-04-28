package os.component.upload.template;

import os.component.upload.FileUploadPool;
import os.component.upload.FileUploadReply;

import java.io.InputStream;

public interface FileUploadTemplate {
    /**
     * 上传文件到指定目录
     *
     * @param inputStream 文件的输入流
     * @param fileName 文件中文简称
     * @return FileUploadReply
     */
    FileUploadReply uploadFile(InputStream inputStream, String fileName) throws Exception;

    /**
     * 文件下载，统一返回字节数组，至于浏览器中的下载或者是预览，需要在Response自定义
     *
     * @param fileUuid 文件UUID
     * @param fileName 文件名称
     * @param remoteDir 文件所在远程目录
     * @return FileUploadReply ByteBuffer 下载出来的具体的文件，转到Response输入流
     */
    FileUploadReply downloadFile(String fileUuid, String fileName, String remoteDir) throws Exception;


    /**
     * 删除存储的文件
     *
     * @param fileUuid 文件UUID
     * @param fileName 文件名称
     * @param remoteDir 文件所在远程目录
     * @return FileUploadReply
     */
    FileUploadReply deleteFile(String fileUuid, String fileName, String remoteDir) throws Exception;

    FileUploadPool getFileUploadPool();
}
