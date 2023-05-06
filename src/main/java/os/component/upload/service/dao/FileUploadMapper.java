package os.component.upload.service.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import os.component.upload.service.model.FileUpload;

import java.util.List;

/**
 *  使用前需要通过代码判断NPE异常
 */
public interface FileUploadMapper {

    @Insert("insert into file_upload(file_uuid, data_uuid, file_origin_name, remote_file_name, remote_dir, upload_time) " +
            " values(#{fileUuid}, #{dataUuid}, #{fileOriginName}, #{remoteFileName}, #{remoteDir}, #{uploadTime})")
    void insert(FileUpload fileUpload);

    @Select("select file_uuid as fileUuid, data_uuid as dataUuid, file_origin_name as fileOriginName, " +
            " remote_file_name as remoteFileName, remote_dir as remoteDir" +
            " from file_upload where file_uuid = #{fileUuid}")
    FileUpload getFile(@Param("fileUuid") String fileUuid);

    /**
     * 时间查询驱动问题，暂时不查询时间
     *
     * @param dataUuid
     * @return
     */
    @Select("select file_uuid as fileUuid, data_uuid as dataUuid, file_origin_name as fileOriginName, " +
            " remote_file_name as remoteFileName, remote_dir as remoteDir" +
            " from file_upload where data_uuid = #{dataUuid} order by upload_time asc")
    List<FileUpload> getFileList(@Param("dataUuid") String dataUuid);

    @Delete("delete from file_upload where file_uuid = #{fileUuid}")
    void deleteFile(@Param("fileUuid") String fileUuid);

    @Delete("delete from file_upload where pk_id = #{dataUuid}}")
    void deleteFileByDataUuid(@Param("dataUuid") String dataUuid);
}
