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
        String downloadPath = "C:\\Users\\Administrator\\Desktop\\FTP下载-钉钉安装客户端.exe";
        String uuid = "6e0c1de5496842ffa4c7c20acfa76f7a";
        String fileName = "FTP文件上传测试大小5M.exe";
        String remoteDir = "/2023/4/27";
        FileUploadReply fileUploadReply = fileUploadTemplate.downloadFile(uuid, fileName, remoteDir);
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
        String uuid = "6e0c1de5496842ffa4c7c20acfa76f7a";
        String fileName = "FTP文件上传测试大小5M.exe";
        String remoteDir = "/2023/4/27";
        FileUploadReply fileUploadReply = fileUploadTemplate.deleteFile(uuid, fileName, remoteDir);
        if (fileUploadReply.isSuccess()) {
            System.out.println("文件删除成功");
        }
    }
}
