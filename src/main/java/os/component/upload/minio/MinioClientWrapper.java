package os.component.upload.minio;

import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.util.StringUtils;
import os.component.upload.FileUploadClient;
import os.component.upload.FileUploadReply;
import os.component.upload.util.FileUploadUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
public class MinioClientWrapper implements FileUploadClient {
    private final MinioClient client;

    public MinioClientWrapper(MinioClient client) {
        this.client = client;
    }

    public MinioClient getClient() {
        return client;
    }

    @Override
    public FileUploadReply uploadFile(InputStream inputStream, String fileName) throws Exception {
        String bucketName = getBucketName();
        String remoteDir = "/" + bucketName;

        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String remoteFileName = uuid.concat("-").concat(fileName);

        BucketExistsArgs existsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
        boolean bucketExists = client.bucketExists(existsArgs);
        if (!bucketExists) {
            MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder().bucket(bucketName).build();
            client.makeBucket(makeBucketArgs);
        }

        PutObjectArgs putObjectArgs = PutObjectArgs.builder().bucket(bucketName).object(remoteFileName)
                .stream(inputStream, inputStream.available(), -1).build();
        client.putObject(putObjectArgs);
        FileUploadReply reply = FileUploadReply.reply(FTPReply.COMMAND_OK, uuid, remoteFileName, remoteDir);
        reply.setFileOriginName(fileName);
        return reply;
    }

    @Override
    public FileUploadReply downloadFile(String fileUuid, String remoteFileName, String remoteDir) throws Exception {
        FileUploadReply existReply = exist(fileUuid, remoteFileName, remoteDir);
        if (!existReply.isSuccess()) {
            return FileUploadReply.error("Download error, File does not exist.");
        }

        FileUploadReply fileUploadReply = FileUploadReply.success("Download Success.");
        String bucketName = remoteDir.replaceAll("/", "").trim();
        GetObjectResponse response = null;
        try {
            BucketExistsArgs existsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
            boolean bucketExists = client.bucketExists(existsArgs);
            if (!bucketExists) {
                log.error("Download Error, Remote Dir does not exist.");
                return FileUploadReply.error("Download Error, Remote Dir does not exist.");
            }

            GetObjectArgs getObjectArgs = GetObjectArgs.builder().
                    bucket(bucketName).object(remoteFileName).build();
            response = client.getObject(getObjectArgs);
            byte[] byteArray = inputStreamToByteArray(response);
            ByteBuffer byteBuffer = ByteBuffer.allocate(byteArray.length);
            byteBuffer.put(byteArray);
            byteBuffer.flip();
            fileUploadReply.setByteBuffer(byteBuffer);
        } catch (Exception e) {
            log.error("File Download Exception, Exception={}", e.toString());
            return FileUploadReply.error("File Download Exception, EX=" + e.toString());
        } finally {
            IOUtils.closeQuietly(response);
        }
        return fileUploadReply;
    }

    @Override
    public FileUploadReply deleteFile(String fileUuid, String remoteFileName, String remoteDir) throws Exception {
        FileUploadReply existReply = exist(fileUuid, remoteFileName, remoteDir);
        if (!existReply.isSuccess()) {
            return FileUploadReply.error("Delete error, File does not exist.");
        }

        try {
            String bucketName = remoteDir.replaceAll("/", "").trim();
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket(bucketName).object(remoteFileName).build();
            client.removeObject(removeObjectArgs);
        } catch (Exception e) {
            log.error("Delete File exception, EX = {}", e.toString());
            return FileUploadReply.error("Delete File exception, EX = {}" + e);
        }
        return FileUploadReply.success("Delete File Success.");
    }

    @Override
    public FileUploadReply exist(String fileUuid, String remoteFileName, String remoteDir) throws Exception {
        if (FileUploadUtils.emptyAll(fileUuid, remoteFileName, remoteDir)) {
            return FileUploadReply.error("File does not exist.");
        }

        String bucketName = remoteDir.replaceAll("/", "").trim();
        StatObjectArgs statObjectArgs = StatObjectArgs.builder().bucket(bucketName).object(remoteFileName).build();
        try {
            client.statObject(statObjectArgs);
        } catch (Exception ex) {
            log.error("File does not exist.");
            return FileUploadReply.error("File does not exist.");
        }
        return FileUploadReply.success("File exist.");

    }

    /**
     * 动态创建buckets，年-月为一个buckets，同样作为remoteDir返回
     */
    protected String getBucketName() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        return year + "-" + month;
    }

    protected byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        byte[] bytes = outputStream.toByteArray();
        IOUtils.closeQuietly(outputStream);
        return bytes;
    }
}
