package os.component.upload.service;

import os.component.upload.service.model.FileUpload;

import java.util.List;

public interface FileUploadService {

    /**
     *  插入上传的文件信息
     */
    void insert(FileUpload fileUpload);

    /**
     *  通过主键获取文件信息
     */
    FileUpload getFile(String fileUuid);

    /**
     *  获取文件信息列表
     */
    List<FileUpload> getFileList(String pkId);

    /**
     *  删除指定fileUuid的单个文件信息
     */
    void deleteFile(String fileUuid);

    /**
     * 删除pkId关联的文件信息列表
     */
    void deleteFileByPkId(String pkId);
}
