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