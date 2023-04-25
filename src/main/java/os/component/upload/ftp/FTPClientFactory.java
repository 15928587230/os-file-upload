package os.component.upload.ftp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.springframework.lang.Nullable;

import java.io.IOException;

/**
 * FTPClient 工厂
 */
@Slf4j
public class FTPClientFactory implements PooledObjectFactory<FTPClient> {
    private FTPClientProperties properties;

    public FTPClientFactory(FTPClientProperties properties) {
        this.properties = properties;
    }

    @Nullable
    public PooledObject<FTPClient> makeObject() {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setConnectTimeout(properties.getClientTimeout());
        try {
            ftpClient.connect(properties.getHost(), properties.getPort());
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                log.error("FTPServer refused connection...");
                return null;
            }

            boolean result = ftpClient.login(properties.getUsername(), properties.getPassword());
            if (!result) {
                log.error("FTPServer authenticate failed...");
                return null;
            }

            ftpClient.setFileType(properties.getTransferFileType());
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding(properties.getEncoding());
            if (properties.getPassiveMode().equals("true")) {
                ftpClient.enterLocalPassiveMode();
            }
            return new DefaultPooledObject<>(ftpClient);
        } catch (IOException e) {
            log.error("FTPServer connect exception...");
            return null;
        }
    }

    @Override
    public void destroyObject(PooledObject<FTPClient> pooledObject) {
        FTPClient ftpClient = pooledObject.getObject();
        boolean ftpNotNull = ftpClient != null;
        try {
            if (ftpNotNull && ftpClient.isConnected())
                ftpClient.logout();
        } catch (IOException e) {
            log.error("FTPServer logout exception..., Exception={}", e.toString());
        } finally {
            try {
                if (ftpNotNull)
                    ftpClient.disconnect();
            } catch (IOException e) {
                log.error("FTPServer disconnect exception..., Exception={}", e.toString());
            }
        }
    }

    @Override
    public boolean validateObject(PooledObject<FTPClient> pooledObject) {
        FTPClient ftpClient = pooledObject.getObject();
        try {
            return ftpClient.sendNoOp();
        } catch (IOException e) {
            throw new RuntimeException("Failed to validate client: " + e, e);
        }
    }

    @Override
    public void activateObject(PooledObject<FTPClient> pooledObject) throws Exception {
    }

    @Override
    public void passivateObject(PooledObject<FTPClient> pooledObject) throws Exception {
    }
}
