package os.component.upload;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import os.component.upload.template.FileUploadTemplate;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@SpringBootTest
public class OsFileUploadApplicationTests {

    @Autowired
    private FileUploadTemplate fileUploadTemplate;

    @Test
    public void ftpUploadFileTest() throws Exception {
        System.out.println(fileUploadTemplate);
        FileUploadPool fileUploadPool = fileUploadTemplate.getFileUploadPool();
        // 看下集合里面wrapper的instance实例是不是同一个就行了
        FileUploadClient fileUploadClient = null;
        for (int i = 0; i < 10; i++) {
            try {
                fileUploadClient = fileUploadPool.borrowClient();
                System.out.println(fileUploadClient);
            } finally {
                fileUploadPool.returnClient(fileUploadClient);
            }
        }

        File file = new File("C:\\Users\\Administrator\\Desktop\\FTP文件上传测试大小5M.exe");
        InputStream inputStream = Files.newInputStream(file.toPath());
        // 上传测试 FTP上传失败直接抛出异常
        FileUploadReply fileUploadReply = fileUploadTemplate.uploadFile(inputStream, file.getName());
        System.out.println(fileUploadReply);
        IOUtils.closeQuietly(inputStream);
    }

    @Test
    public void ftpDownLoadFileTest() throws Exception {
        String downloadPath = "C:\\Users\\Administrator\\Desktop\\FTPddd下载-钉钉安装客户端.exe";
        String uuid = "657124aa1d9c44bda5fc26fa8af53818";
        String remoteFileName = "657124aa1d9c44bda5fc26fa8af53818-FTP文件上传测试大小5M.exe";
        String remoteDir = "/2023/5/4";
        FileUploadReply fileUploadReply = fileUploadTemplate.downloadFile(uuid, remoteFileName, remoteDir);
        if (fileUploadReply.isSuccess()) {
            try {
                ByteBuffer byteBuffer = fileUploadReply.getByteBuffer();
                Path path = Paths.get(downloadPath);
                Files.write(path, byteBuffer.array());
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("文件下载失败");
            }
        }
    }

    @Test
    public void ftpDeleteFileTest() throws Exception {
        String uuid = "657124aa1d9c44bda5fc26fa8af53818";
        String remoteFileName = "657124aa1d9c44bda5fc26fa8af53818-FTP文件上传测试大小5M.exe";
        String remoteDir = "/2023/5/4";
        FileUploadReply fileUploadReply = fileUploadTemplate.deleteFile(uuid, remoteFileName, remoteDir);
        if (fileUploadReply.isSuccess()) {
            System.out.println("文件删除成功");
        }
    }

    @Test
    public void fdfsUploadFileTest() throws Exception {
        FileUploadPool fileUploadPool = fileUploadTemplate.getFileUploadPool();
        // 看下集合里面wrapper的instance实例是不是同一个就行了
        FileUploadClient fileUploadClient = null;
        for (int i = 0; i < 10; i++) {
            try {
                fileUploadClient = fileUploadPool.borrowClient();
                System.out.println(fileUploadClient);
            } finally {
                fileUploadPool.returnClient(fileUploadClient);
            }
        }

        File file = new File("C:\\Users\\Administrator\\Desktop\\FTP文件上传测试大小5M.exe");
        InputStream inputStream = Files.newInputStream(file.toPath());
        // 上传测试 FTP上传失败直接抛出异常
        FileUploadReply fileUploadReply = fileUploadTemplate.uploadFile(inputStream, file.getName());
        System.out.println(fileUploadReply);
        IOUtils.closeQuietly(inputStream);
    }

    @Test
    public void testFdfsDownload() throws Exception {
        String downloadPath = "C:\\Users\\Administrator\\Desktop\\FTPddd下载-钉钉安装客户端.exe";
        String uuid = "wKgUFGRUZKmAFJsTAE-FMPVMIn4926";
        String remoteFileName = "wKgUFGRUZKmAFJsTAE-FMPVMIn4926.exe";
        String remoteDir = "/group1/M00/00/00";
        FileUploadReply fileUploadReply = fileUploadTemplate.downloadFile(uuid, remoteFileName, remoteDir);
        if (fileUploadReply.isSuccess()) {
            try {
                ByteBuffer byteBuffer = fileUploadReply.getByteBuffer();
                Path path = Paths.get(downloadPath);
                Files.write(path, byteBuffer.array());
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("文件下载失败");
            }
        }
    }

    @Test
    public void testFdfsDelete() throws Exception {
        String uuid = "wKgUFGRUZKmAFJsTAE-FMPVMIn4926";
        String remoteFileName = "wKgUFGRUZKmAFJsTAE-FMPVMIn4926.exe";
        String remoteDir = "/group1/M00/00/00";
        FileUploadReply fileUploadReply = fileUploadTemplate.deleteFile(uuid, remoteFileName, remoteDir);
        if (fileUploadReply.isSuccess()) {
            System.out.println(fileUploadReply);
        }
    }

    @Test
    public void minioUploadFileTest() throws Exception {
        FileUploadPool fileUploadPool = fileUploadTemplate.getFileUploadPool();
        // 看下集合里面wrapper的instance实例是不是同一个就行了
        FileUploadClient fileUploadClient = null;
        for (int i = 0; i < 10; i++) {
            try {
                fileUploadClient = fileUploadPool.borrowClient();
                System.out.println(fileUploadClient);
            } finally {
                fileUploadPool.returnClient(fileUploadClient);
            }
        }

        File file = new File("C:\\Users\\Administrator\\Desktop\\FTP文件上传测试大小5M.exe");
        InputStream inputStream = Files.newInputStream(file.toPath());
        // 上传测试 FTP上传失败直接抛出异常
        FileUploadReply fileUploadReply = fileUploadTemplate.uploadFile(inputStream, file.getName());
        System.out.println(fileUploadReply);
        IOUtils.closeQuietly(inputStream);
    }

    @Test
    public void testMinioDownload() throws Exception {
        String downloadPath = "C:\\Users\\Administrator\\Desktop\\FTPddd下载-钉钉安装客户端.exe";
        String uuid = "007e68ad5d6749d7846fe5448a7bc5a3";
        String remoteFileName = "007e68ad5d6749d7846fe5448a7bc5a3-FTP文件上传测试大小5M.exe";
        String remoteDir = "/2023-5";
        FileUploadReply fileUploadReply = fileUploadTemplate.downloadFile(uuid, remoteFileName, remoteDir);
        if (fileUploadReply.isSuccess()) {
            try {
                ByteBuffer byteBuffer = fileUploadReply.getByteBuffer();
                Path path = Paths.get(downloadPath);
                Files.write(path, byteBuffer.array());
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("文件下载失败");
            }
        }
    }

    @Test
    public void testMinioDelete() throws Exception {
        String uuid = "007e68ad5d6749d7846fe5448a7bc5a3";
        String remoteFileName = "007e68ad5d6749d7846fe5448a7bc5a3-FTP文件上传测试大小5M.exe";
        String remoteDir = "/2023-5";
        FileUploadReply fileUploadReply = fileUploadTemplate.deleteFile(uuid, remoteFileName, remoteDir);
        if (fileUploadReply.isSuccess()) {
            System.out.println(fileUploadReply);
        }
    }
}
