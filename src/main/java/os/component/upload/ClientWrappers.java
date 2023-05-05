package os.component.upload;

import io.minio.MinioClient;
import org.apache.commons.net.ftp.FTPClient;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerServer;
import os.component.upload.fdfs.FdfsClientWrapper;
import os.component.upload.ftp.FTPClientWrapper;
import os.component.upload.minio.MinioClientWrapper;

import java.time.LocalDate;

public class ClientWrappers {

    public static FTPClientWrapper wrapFTPClient(FTPClient ftpClient) {
        return new FTPClientWrapper(ftpClient);
    }

    public static FTPClient unwrapFTPClient(FTPClientWrapper clientWrapper) {
        return clientWrapper.getClient();
    }

    public static FdfsClientWrapper wrapFDFSClient(StorageClient storageClient, TrackerServer trackerServer) {
        return new FdfsClientWrapper(storageClient, trackerServer);
    }

    public static MinioClientWrapper wrapMINIOClient(MinioClient minioClient) {
        return new MinioClientWrapper(minioClient);
    }

    public static MinioClient unwrapMINIOClient(MinioClientWrapper clientWrapper) {
        return clientWrapper.getClient();
    }
}
