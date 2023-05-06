package os.component.upload.service.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import os.component.upload.service.model.FileUpload;

import java.util.List;

public class FileUploadDao {
    private final SqlSessionFactory sqlSessionFactory;

    public FileUploadDao(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public void insertFile(FileUpload fileUpload) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            sqlSession.insert("os.component.upload.service.dao.FileUploadMapper.insert", fileUpload);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    public FileUpload getFile(String fileUuid) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            return sqlSession.selectOne("os.component.upload.service.dao.FileUploadMapper.getFile", fileUuid);
        } finally {
            sqlSession.close();
        }
    }

    public List<FileUpload> getFileList(String dataUuid) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            return sqlSession.selectList("os.component.upload.service.dao.FileUploadMapper.getFileList", dataUuid);
        } finally {
            sqlSession.close();
        }
    }

    public void deleteFile(String fileUuid) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            sqlSession.delete("os.component.upload.service.dao.FileUploadMapper.deleteFile", fileUuid);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    public void deleteFileByPkId(String dataUuid) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            sqlSession.delete("os.component.upload.service.dao.FileUploadMapper.deleteFileByDataUuid", dataUuid);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }
}
