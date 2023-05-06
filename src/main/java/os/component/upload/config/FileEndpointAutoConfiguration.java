package os.component.upload.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import os.component.upload.endpoint.FileUploadEndpoint;
import os.component.upload.service.FileUploadImpl;
import os.component.upload.service.FileUploadService;
import os.component.upload.service.dao.FileUploadDao;
import os.component.upload.service.dao.FileUploadMapper;
import os.component.upload.template.FileUploadTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 *  激活sql层，和controller接口
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "owinfo.upload", name = "endpointEnabled",
        havingValue = "true", matchIfMissing = false)
public class FileEndpointAutoConfiguration {
    private final FileUploadConfig fileUploadConfig;
    private final FileUploadTemplate fileUploadTemplate;

    public FileEndpointAutoConfiguration(FileUploadConfig fileUploadConfig, FileUploadTemplate fileUploadTemplate) {
        this.fileUploadConfig = fileUploadConfig;
        this.fileUploadTemplate = fileUploadTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(fileUploadConfig.getDriverClassName());
        dataSource.setUrl(fileUploadConfig.getUrl());
        dataSource.setUsername(fileUploadConfig.getUsername());
        dataSource.setPassword(fileUploadConfig.getPassword());
        try {
            dataSource.init();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return dataSource;
    }

    @Bean
    public FileUploadEndpoint fileUploadEndpoint(DataSource dataSource) {
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        org.apache.ibatis.session.Configuration configuration =
                new org.apache.ibatis.session.Configuration(environment);
        configuration.addMapper(FileUploadMapper.class);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        FileUploadDao fileUploadDao = new FileUploadDao(sqlSessionFactory);
        FileUploadService fileUploadService = new FileUploadImpl(fileUploadDao);
        FileUploadEndpoint fileUploadEndpoint = new FileUploadEndpoint();
        fileUploadEndpoint.setFileUploadService(fileUploadService);
        fileUploadEndpoint.setFileUploadTemplate(fileUploadTemplate);
        fileUploadEndpoint.setFileUploadConfig(fileUploadConfig);
        return fileUploadEndpoint;
    }
}
