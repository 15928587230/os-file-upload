package os.component.upload.service;

import os.component.upload.service.dao.FileUploadDao;
import os.component.upload.service.model.FileUpload;

import java.util.List;

public class FileUploadImpl implements FileUploadService {
    private FileUploadDao fileUploadDao;

    public FileUploadImpl(FileUploadDao fileUploadDao) {
        this.fileUploadDao = fileUploadDao;
    }

    @Override
    public void insert(FileUpload fileUpload) {
        fileUploadDao.insertFile(fileUpload);
    }

    @Override
    public FileUpload getFile(String fileUuid) {
        return fileUploadDao.getFile(fileUuid);
    }

    @Override
    public List<FileUpload> getFileList(String pkId) {
        return fileUploadDao.getFileList(pkId);
    }

    @Override
    public void deleteFile(String fileUuid) {
        fileUploadDao.deleteFile(fileUuid);
    }

    @Override
    public void deleteFileByPkId(String pkId) {
        fileUploadDao.deleteFileByPkId(pkId);
    }
}
