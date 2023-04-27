package os.component.upload;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.net.ftp.FTPReply;

import java.nio.ByteBuffer;

/**
 *  文件上传响应
 */
@Getter
@Setter
@ToString
public class FileUploadReply {
    public FileUploadReply() {
    }

    public FileUploadReply(String fileUuid, String fileName, String fileRemoteDir, int replyCode) {
        this.fileUuid = fileUuid;
        this.fileName = fileName;
        this.fileRemoteDir = fileRemoteDir;
        this.replyCode = replyCode;
    }

    private String fileUuid;
    private String fileName;
    private String fileRemoteDir;
    private int replyCode;
    private String replyMsg;
    // 文件下载到ByteBuffer中
    private ByteBuffer byteBuffer;

    public static FileUploadReply reply(int replyCode, String fileUuid, String fileName, String fileRemoteDir) {
        return new FileUploadReply(fileUuid, fileName, fileRemoteDir, replyCode);
    }

    public static FileUploadReply error(String replyMsg) {
        FileUploadReply fileUploadReply = new FileUploadReply();
        fileUploadReply.setReplyCode(FTPReply.UNRECOGNIZED_COMMAND);
        fileUploadReply.setReplyMsg(replyMsg);
        return fileUploadReply;
    }

    public static FileUploadReply success(String replyMsg) {
        FileUploadReply fileUploadReply = new FileUploadReply();
        fileUploadReply.setReplyCode(FTPReply.COMMAND_OK);
        fileUploadReply.setReplyMsg(replyMsg);
        return fileUploadReply;
    }

    public boolean isSuccess() {
        return getReplyCode() == FTPReply.COMMAND_OK;
    }
}
