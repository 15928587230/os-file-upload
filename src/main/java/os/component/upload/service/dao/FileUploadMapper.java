package os.component.upload.service.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import os.component.upload.service.model.FileUpload;

import java.util.List;

/**
 *  使用前需要通过代码判断NPE异常
 */
public interface FileUploadMapper {

    @Insert("insert into file_upload(file_uuid, pk_id, file_origin_name, remote_file_name, remote_dir, upload_time) " +
            " values(#{fileUuid}, #{pkId}, #{fileOriginName}, #{remoteFileName}, #{remoteDir}, #{uploadTime})")
    void insert(FileUpload fileUpload);

    @Select("select file_uuid as fileUuid, pk_id as pkId, file_origin_name as fileOriginName, " +
            " remote_file_name as remoteFileName, remote_dir as remoteDir, upload_time as uploadTime" +
            " from file_upload where file_uuid = #{fileUuid}")
    FileUpload getFile(String fileUuid);

    @Select("select file_uuid as fileUuid, pk_id as pkId, file_origin_name as fileOriginName, " +
            " remote_file_name as remoteFileName, remote_dir as remoteDir, upload_time as uploadTime" +
            " from file_upload where pk_id = #{pkId} order by upload_time asc")
    List<FileUpload> getFileList(String pkId);

    @Delete("delete from file_upload where file_uuid = #{fileUuid}}")
    void deleteFile(String fileUuid);

    @Delete("delete from file_upload where pk_id = #{pkId}}")
    void deleteFileByPkId(String pkId);
}
