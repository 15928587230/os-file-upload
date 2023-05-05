package os.component.upload.minio;

import io.minio.MinioClient;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import os.component.upload.ClientWrappers;
import os.component.upload.FileUploadClient;
import os.component.upload.FileUploadPool;

/**
 * FTPClient连接池
 *
 * @author pengjunjie
 */
public class MinioClientPool implements FileUploadPool {
    private final GenericObjectPool<MinioClient> pool;

    public MinioClientPool(MinioProperties properties) {
        MinioClientFactory factory = new MinioClientFactory(properties);
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxWaitMillis(properties.getBorrowMaxWailTimeMills());
        config.setMaxTotal(properties.getMaxTotal());
        config.setMinIdle(properties.getMidIdle());
        config.setMaxIdle(properties.getMaxIdle());
        pool = new GenericObjectPool<>(factory, config);
    }

    public MinioClient borrowObject() throws Exception {
        return pool.borrowObject();
    }

    public void returnObject(MinioClient minioClient) {
        pool.returnObject(minioClient);
    }

    @Override
    public FileUploadClient borrowClient() throws Exception {
       return ClientWrappers.wrapMINIOClient(borrowObject());
    }

    @Override
    public void returnClient(FileUploadClient client) throws Exception {
        if (client instanceof MinioClientWrapper) {
            returnObject(ClientWrappers.unwrapMINIOClient((MinioClientWrapper) client));
        }
    }
}