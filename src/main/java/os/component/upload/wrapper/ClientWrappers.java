package os.component.upload.wrapper;

import org.apache.commons.net.ftp.FTPClient;
import os.component.upload.ftp.FTPClientWrapper;

public class ClientWrappers {

    public static FTPClientWrapper wrapFTPClient(FTPClient ftpClient) {
        return new FTPClientWrapper(ftpClient);
    }

    public static FTPClient unwrapFTPClient(FTPClientWrapper clientWrapper) {
        return clientWrapper.getClient();
    }
}
