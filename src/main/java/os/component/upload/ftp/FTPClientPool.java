package os.component.upload.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * FTP客户端连接池
 */
public class FTPClientPool implements ObjectPool<FTPClient> {
    private static final Integer MAX_SIZE = 10;
    private BlockingQueue<PooledObject<FTPClient>> blockingQueue;
    private FTPClientFactory clientFactory;

    public FTPClientPool(FTPClientFactory clientFactory) throws InterruptedException {
        this.clientFactory = clientFactory;
        this.blockingQueue = new ArrayBlockingQueue<>(MAX_SIZE * 2);
        initPool();
    }

    private void initPool() throws InterruptedException {
        for (int i = 0; i < MAX_SIZE; i++) {
            addObject();
        }
    }

    @Override
    public FTPClient borrowObject() {
        return null;
    }

    @Override
    public void returnObject(FTPClient ftpClient) {

    }

    @Override
    public void invalidateObject(FTPClient ftpClient) throws Exception {

    }

    @Override
    public void addObject() throws InterruptedException {
        PooledObject<FTPClient> pooledObject = clientFactory.makeObject();
        if (pooledObject == null) {
            addObject();
        }
        blockingQueue.offer(pooledObject, 3, TimeUnit.SECONDS);
    }

    @Override
    public int getNumIdle() {
        return 0;
    }

    @Override
    public int getNumActive() {
        return 0;
    }

    @Override
    public void clear() throws Exception {

    }

    @Override
    public void close() {}
}