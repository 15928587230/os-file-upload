package os.component.upload.template;

import os.component.upload.FileUploadClient;
import os.component.upload.FileUploadPool;
import os.component.upload.FileUploadReply;

import java.io.InputStream;

public class AbstractUploadTemplate implements FileUploadTemplate {
    public FileUploadPool fileUploadPool;

    public AbstractUploadTemplate(FileUploadPool fileUploadPool) {
        this.fileUploadPool = fileUploadPool;
    }

    @Override
    public FileUploadReply uploadFile(InputStream inputStream, String localPath) throws Exception {
        FileUploadClient fileUploadClient = null;
        try {
            fileUploadClient = fileUploadPool.borrowClient();
            return fileUploadClient.uploadFile(inputStream, localPath);
        } finally {
            if (fileUploadClient != null)
                fileUploadPool.returnClient(fileUploadClient);
        }
    }

    @Override
    public FileUploadReply deleteFile(String fileUuid, String fileName, String remoteDir) throws Exception {
        FileUploadClient fileUploadClient = null;
        try {
            fileUploadClient = fileUploadPool.borrowClient();
            return fileUploadClient.deleteFile(fileUuid, fileName, remoteDir);
        } finally {
            if (fileUploadClient != null)
                fileUploadPool.returnClient(fileUploadClient);
        }
    }

    @Override
    public FileUploadReply downloadFile(String fileUuid, String fileName, String remoteDir) throws Exception {
        FileUploadClient fileUploadClient = null;
        try {
            fileUploadClient = fileUploadPool.borrowClient();
            return fileUploadClient.downloadFile(fileUuid, fileName, remoteDir);
        } finally {
            if (fileUploadClient != null)
                fileUploadPool.returnClient(fileUploadClient);
        }
    }

    @Override
    public FileUploadPool getFileUploadPool() {
        return this.fileUploadPool;
    }
}
