IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[file_upload]') AND type in (N'U')) DROP TABLE [dbo].[file_upload];
CREATE TABLE [dbo].[file_upload](
    file_uuid VARCHAR(100) NOT NULL,
    data_uuid VARCHAR(400),
    file_origin_name VARCHAR(200),
    remote_file_name VARCHAR(200),
    remote_dir VARCHAR(50),
    upload_time DATETIME,
    PRIMARY KEY (file_uuid)
    );