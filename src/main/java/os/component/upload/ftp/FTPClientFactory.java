package os.component.upload.ftp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * FTPClient 工厂
 *
 * @author pengjunjie
 */
@Slf4j
public class FTPClientFactory implements PooledObjectFactory<FTPClient> {
    private final FTPClientProperties properties;
    public FTPClientFactory(FTPClientProperties properties) {
        this.properties = properties;
    }

    @Override
    public PooledObject<FTPClient> makeObject() throws Exception {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setConnectTimeout(properties.getConnectTimeoutSecond() * 1000);
        try {
            ftpClient.connect(properties.getHost(), properties.getPort());
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                log.error("FTPServer refused connection");
                return null;
            }

            boolean login = ftpClient.login(properties.getUsername(), properties.getPassword());
            if (!login) {
                log.error("FTPServer authentication failed user={}, password={}", properties.getUsername(), properties.getPassword());
                return null;
            }

            ftpClient.setDataTimeout(properties.getDataTimeoutSecond() * 1000);
            ftpClient.setControlEncoding(properties.getEncoding());
            ftpClient.setFileType(properties.getTransferFileType());
            ftpClient.setSoTimeout(properties.getSoTimeoutSecond() * 1000);
            ftpClient.enterLocalActiveMode();
            if (properties.isPassiveMode()) {
                ftpClient.enterLocalPassiveMode();
            }
        } catch (Exception exception) {
            log.error("FTPServer create error, Exception={}", exception.toString());
            throw new RuntimeException("FTPServer create error");
        }

        return new DefaultPooledObject<>(ftpClient);
    }

    @Override
    public void destroyObject(PooledObject<FTPClient> client) throws Exception {
        if (client == null || client.getObject() == null) return;
        FTPClient ftpClient = client.getObject();
        try {
            ftpClient.logout();
        } catch (Exception e) {
            log.error("FTPClient logout exception");
        } finally {
            try {
                if (ftpClient.isConnected())
                    ftpClient.disconnect();
            } catch (Exception e) {
                log.error("FTPClient disconnect exception");
            }
        }
    }

    @Override
    public boolean validateObject(PooledObject<FTPClient> client) {
        if (client == null || client.getObject() == null) return false;
        try {
            return client.getObject().sendNoOp();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void activateObject(PooledObject<FTPClient> client) throws Exception {}

    @Override
    public void passivateObject(PooledObject<FTPClient> client) throws Exception {}
}
