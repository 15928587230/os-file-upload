package os.component.upload;

public interface FileUploadPool {

    FileUploadClient borrowClient() throws Exception;

    void returnClient(FileUploadClient client) throws Exception;
}
