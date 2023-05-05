package os.component.upload.fdfs;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.TrackerServer;
import os.component.upload.ClientWrappers;
import os.component.upload.FileUploadClient;
import os.component.upload.FileUploadPool;

/**
 * FDFS 连接池
 *
 * @author pengjunjie
 */
public class FdfsClientPool implements FileUploadPool {
    private final GenericObjectPool<TrackerServer> pool;

    public FdfsClientPool(FdfsProperties properties) {
        FdfsServerFactory factory = new FdfsServerFactory(properties);
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxWaitMillis(properties.getBorrowMaxWailTimeMills());
        config.setMaxTotal(properties.getPoolSize());
        config.setMinIdle(properties.getMidIdle());
        config.setMaxIdle(properties.getMaxIdle());
        pool = new GenericObjectPool<>(factory, config);
    }

    public TrackerServer borrowObject() throws Exception {
        return pool.borrowObject();
    }

    public void returnObject(TrackerServer trackerServer) {
        pool.returnObject(trackerServer);
    }

    @Override
    public FileUploadClient borrowClient() throws Exception {
        TrackerServer trackerServer = borrowObject();
        StorageClient storageClient = new StorageClient1(trackerServer, null);
        return ClientWrappers.wrapFDFSClient(storageClient, trackerServer);
    }

    @Override
    public void returnClient(FileUploadClient client) throws Exception {
        if (client instanceof FdfsClientWrapper) {
            FdfsClientWrapper wrapper = (FdfsClientWrapper) client;
            returnObject(wrapper.getTrackerServer());
        }
    }
}
