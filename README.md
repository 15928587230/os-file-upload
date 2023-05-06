# os-file-upload

文件上传下载
FTP
FastDFS
MinIO

> 提供统一的上传下载删除文件入口，通过yml配置动态切换存储方式， 按条件自动装配Datasource、FileUploadTemplate和FileUploadEndpoint【controller】  
> @ConditionalOnProperty(prefix = "owinfo.upload", name = "endpointEnabled", havingValue = "true", matchIfMissing = false)。   
> @ConditionalOnProperty(prefix = "owinfo.upload", name = "enabled", havingValue = "true", matchIfMissing = false)   
> 提供file_upload表统一存储上传的文件列表信息，DAO层参考美团号段模式的持久层写法。和外部数据ID进行关联。

```yml
server:
  port: 8080

owinfo:
  upload:
    enabled: true
    endpointEnabled: true
    #FTP?FASTDFS?MINIO
    type: FTP
    # 预览nginxUrl + remoteDir + "/" + fileRemoteName
    nginxUrl: http://192.168.20.20:80
    driverClassName: com.microsoft.sqlserver.jdbc.SQLServerDriver
    url: jdbc:sqlserver://xxx;DatabaseName=xxx
    username: xxx
    password: xxx
  ftp:
    host: 192.168.20.20
    port: 21
    username: xxx
    password: xxx
    # 服务端需要关闭http token验证机制
  fastdfs:
    trackerServers: 192.168.20.20:22122
  minio:
    endpoint: http://192.168.20.20:9000
    username: xxx
    password: xxx

spring:
  servlet:
    multipart:
      max-file-size: 100MB
      max-request-size: 100MB
```

```java
/**
 * 传入的参数不会有任何变动，比如inputStream不会close
 *
 * @author pengjunjie
 */
public interface FileUploadTemplate {
    /**
     * 上传文件到指定目录
     *
     * @param inputStream 文件的输入流
     * @param fileName 文件中文简称
     * @return FileUploadReply
     */
    FileUploadReply uploadFile(InputStream inputStream, String fileName) throws Exception;

    /**
     * 文件下载，统一返回字节数组，至于浏览器中的下载或者是预览，需要在Response自定义
     *
     * @param fileUuid 文件UUID
     * @param remoteFileName 远程文件名称
     * @param remoteDir 文件所在远程目录
     * @return FileUploadReply ByteBuffer 下载出来的具体的文件，转到Response输入流
     */
    FileUploadReply downloadFile(String fileUuid, String remoteFileName, String remoteDir) throws Exception;


    /**
     * 删除存储的文件
     *
     * @param fileUuid 文件UUID
     * @param remoteFileName 远程文件名称
     * @param remoteDir 文件所在远程目录
     * @return FileUploadReply
     */
    FileUploadReply deleteFile(String fileUuid, String remoteFileName, String remoteDir) throws Exception;

    FileUploadPool getFileUploadPool();
}
```

# FTP Nginx配置和预览、下载方式

> nginxUrl + remoteDir + "/" + remoteFileName 进行访问

**nginx FTP 配置如下, 访问案例 192.168.20.20:80/2023/4/27/3aef709efac247b58a0391b00a436d9c-FTP文件上传测试大小5M.exe**

```shell
[root@localhost 27]# pwd
/home/ftpuser/2023/4/27
[root@localhost 27]# ll
总用量 15276
-rw-r--r-- 1 ftpuser ftpuser 5211440 4月  27 15:00 3aef709efac247b58a0391b00a436d9c-FTP文件上传测试大小5M.exe
-rw-r--r-- 1 ftpuser ftpuser 5211440 4月  27 14:54 f4a7495a579c4f34a7771e02b00c1ed9-FTP文件上传测试大小5M.exe
-rw-r--r-- 1 ftpuser ftpuser 5211440 4月  27 15:40 f93f69d143ba449f9095773eab0878f8-FTP文件上传测试大小5M.exe
[root@localhost 27]#
```

```html
location / {
    root   /home/ftpuser;
    index  index.html index.htm;
}
```

# FDFS Nginx配置和预览、下载方式
> nginxUrl + remoteDir + "/" + remoteFileName 进行访问

**nginx FDFS 需要FDFS模块, 配置如下。 访问案例 192.168.20.20:80/group1/M00/00/00/wKgUFGRTffuAUtMYAE-FMPVMIn4279.exe**

```shell
[root@localhost 00]# pwd
/opt/fdfs/storage/files/data/00/00
[root@localhost 00]# ll
总用量 20388
-rw-r--r-- 1 root root 5211440 5月   4 17:42 wKgUFGRTffuAUtMYAE-FMPVMIn4279.exe
-rw-r--r-- 1 root root      12 5月   4 10:19 wKgUFGRTFiuASpLfAAAADP8xkfI889_big.txt
-rw-r--r-- 1 root root      49 5月   4 10:19 wKgUFGRTFiuASpLfAAAADP8xkfI889_big.txt-m
-rw-r--r-- 1 root root      12 5月   4 10:19 wKgUFGRTFiuASpLfAAAADP8xkfI889.txt
-rw-r--r-- 1 root root      49 5月   4 10:19 wKgUFGRTFiuASpLfAAAADP8xkfI889.txt-m
-rw-r--r-- 1 root root 5211440 5月   4 18:06 wKgUFGRTg7uAMr7_AE-FMPVMIn4847.exe
-rw-r--r-- 1 root root      77 5月   4 18:06 wKgUFGRTg7uAMr7_AE-FMPVMIn4847.exe-m
-rw-r--r-- 1 root root 5211440 5月   4 17:53 wKgUFGRTgJWALeWXAE-FMPVMIn4573.exe
-rw-r--r-- 1 root root 5211440 5月   4 18:04 wKgUFGRTgxKAbnGGAE-FMPVMIn4289.exe
[root@localhost 00]#
```

```html
#这里需要安装Nginx FDFS模块
#部署配置nginx负载均衡:

upstream fastdfs_storage_server {
    server 192.168.92.131:80;
    server 192.168.92.132:80;
    server 192.168.92.133:80;
    server 192.168.92.134:80;
}

#nginx拦截请求路径：
location ~ /group[1-9]/M0[0-9] {
    proxy_pass http://fastdfs_storage_server;
}
```