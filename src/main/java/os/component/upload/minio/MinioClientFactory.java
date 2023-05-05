package os.component.upload.minio;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * minio对象工厂
 *
 * @author pengjunjie
 */
@Slf4j
public class MinioClientFactory implements PooledObjectFactory<MinioClient> {
    private final MinioProperties properties;
    public MinioClientFactory(MinioProperties properties) {
        this.properties = properties;
    }

    @Override
    public PooledObject<MinioClient> makeObject() throws Exception {
        try {
            MinioClient client = MinioClient.builder()
                    .endpoint(properties.getEndpoint())
                    .credentials(properties.getUsername(), properties.getPassword()).build();
            client.setTimeout(properties.getConnectTimeoutMills(), properties.getWriteTimeoutMills(), properties.getReadTimeoutMills());
            client.listBuckets();
            return new DefaultPooledObject<>(client);
        } catch (Exception ex) {
            log.error("MINIO Server Connected Exception, EX= {}", ex.toString());
            return null;
        }
    }

    @Override
    public void destroyObject(PooledObject<MinioClient> pooledObject) throws Exception {
        // 这里MinioClient使用HttpClient的无状态连接，可以直接销毁
    }

    @Override
    public boolean validateObject(PooledObject<MinioClient> pooledObject) {
        if (pooledObject == null || pooledObject.getObject() == null) return false;
        try {
            MinioClient minioClient = pooledObject.getObject();
            minioClient.listBuckets();
            return true;
        } catch (Exception ex) {
            log.error("MINIO Server Connected Exception, EX= {}", ex.toString());
            return false;
        }
    }

    @Override
    public void activateObject(PooledObject<MinioClient> pooledObject) throws Exception {}

    @Override
    public void passivateObject(PooledObject<MinioClient> pooledObject) throws Exception {}
}
