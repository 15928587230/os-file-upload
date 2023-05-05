package os.component.upload.fdfs;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.ProtoCommon;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public class FdfsServerFactory implements PooledObjectFactory<TrackerServer> {
    private final FdfsProperties fdfsProperties;
    private final Properties properties = new Properties();

    public FdfsServerFactory(FdfsProperties properties) {
        // 初始化全局配置、初始化多个tracker节点配置
        this.fdfsProperties = properties;
        try {
            initGlobalConfig();
        } catch (Exception ex) {
            log.error("FDFS Global Config Init Exception, EX = {}", ex.toString());
        }
    }

    @Override
    public PooledObject<TrackerServer> makeObject() throws Exception {
        try {
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            boolean b = ProtoCommon.activeTest(trackerServer.getSocket());
            if (!b) {
                log.error("Tracker Server Connected Failed.");
                return null;
            }
            return new DefaultPooledObject<>(trackerServer);
        } catch (Exception ex) {
            log.error("Tracker Server Connected Exception, EX={}", ex.toString());
            return null;
        }
    }

    @Override
    public void destroyObject(PooledObject<TrackerServer> p) throws Exception {
        if (p != null && p.getObject() != null) {
            TrackerServer trackerServer = p.getObject();
            try {
                trackerServer.close();
            } catch (Exception ex) {
                log.error("Tracker Server Destroy Exception, EX = {}", ex.toString());
            }
        }
    }

    @Override
    public boolean validateObject(PooledObject<TrackerServer> p) {
        if (p == null || p.getObject() == null) return false;
        TrackerServer trackerServer = p.getObject();
        try {
            return ProtoCommon.activeTest(trackerServer.getSocket());
        } catch (Exception ex) {
            log.error("Tracker Server Validate Exception, EX = {}", ex.toString());
            return false;
        }
    }

    @Override
    public void activateObject(PooledObject<TrackerServer> p) throws Exception {
    }

    @Override
    public void passivateObject(PooledObject<TrackerServer> p) throws Exception {
    }

    //全局配置，
    private void initGlobalConfig() throws MyException, IOException {
        properties.put(ClientGlobal.PROP_KEY_CONNECT_TIMEOUT_IN_SECONDS, fdfsProperties.getConnectTimeoutSecond());
        properties.put(ClientGlobal.PROP_KEY_NETWORK_TIMEOUT_IN_SECONDS, fdfsProperties.getSoTimeoutSecond());
        properties.put(ClientGlobal.PROP_KEY_CHARSET, fdfsProperties.getCharset());
        properties.put(ClientGlobal.PROP_KEY_HTTP_ANTI_STEAL_TOKEN, fdfsProperties.isStealToken());
        properties.put(ClientGlobal.PROP_KEY_HTTP_SECRET_KEY, fdfsProperties.getSecretKey());
        properties.put(ClientGlobal.PROP_KEY_HTTP_TRACKER_HTTP_PORT, fdfsProperties.getTrackerHttpPort());
        properties.put(ClientGlobal.PROP_KEY_TRACKER_SERVERS, fdfsProperties.getTrackerServers());
        ClientGlobal.initByProperties(properties);
    }
}
