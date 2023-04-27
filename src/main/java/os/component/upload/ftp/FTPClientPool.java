package os.component.upload.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import os.component.upload.FileUploadClient;
import os.component.upload.FileUploadPool;
import os.component.upload.wrapper.ClientWrappers;

/**
 * FTPClient连接池
 *
 * @author pengjunjie
 */
public class FTPClientPool implements FileUploadPool {
    private final GenericObjectPool<FTPClient> pool;

    public FTPClientPool(FTPClientProperties properties) {
        FTPClientFactory factory = new FTPClientFactory(properties);
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxWaitMillis(properties.getBorrowMaxWailTimeMills());
        config.setMaxTotal(properties.getMaxTotal());
        config.setMinIdle(properties.getMidIdle());
        config.setMaxIdle(properties.getMaxIdle());
        pool = new GenericObjectPool<>(factory, config);
    }

    public FTPClient borrowObject() throws Exception {
        return pool.borrowObject();
    }

    public void returnObject(FTPClient ftpClient) {
        pool.returnObject(ftpClient);
    }

    @Override
    public FileUploadClient borrowClient() throws Exception {
        return ClientWrappers.wrapFTPClient(borrowObject());
    }

    @Override
    public void returnClient(FileUploadClient client) throws Exception {
        if (client instanceof FTPClientWrapper) {
            returnObject(ClientWrappers.unwrapFTPClient((FTPClientWrapper) client));
        }
    }
}
