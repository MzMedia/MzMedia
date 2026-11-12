package cn.iocoder.yudao.module.media.conf;

import cn.iocoder.yudao.module.media.service.bean.AlarmType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 配置mybatis
 */
@Configuration("mediaMybatisConfig")
public class MybatisConfig {

    @Autowired
    private UserSetting userSetting;

    @Bean
    public DatabaseIdProvider databaseIdProvider() {
        VendorDatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.setProperty("Oracle", "oracle");
        properties.setProperty("MySQL", "mysql");
        properties.setProperty("DB2", "db2");
        properties.setProperty("Derby", "derby");
        properties.setProperty("H2", "h2");
        properties.setProperty("HSQL", "hsql");
        properties.setProperty("Informix", "informix");
        properties.setProperty("MS-SQL", "ms-sql");
        properties.setProperty("PostgreSQL", "postgresql");
        properties.setProperty("Sybase", "sybase");
        properties.setProperty("Hana", "hana");
        // 达梦(DM8)：JDBC getDatabaseProductName() 返回 "DM DBMS"，此处做等名字匹配；另保留 "DM" 兼容别名
        properties.setProperty("DM DBMS", "dm");
        properties.setProperty("DM", "dm");
        properties.setProperty("KingbaseES", "kingbase");
        properties.setProperty("KingBase8", "kingbase");
        databaseIdProvider.setProperties(properties);
        return databaseIdProvider;
    }

    @Bean
    public ConfigurationCustomizer mediaConfigurationCustomizer() {
        return configuration -> {
            if (Boolean.TRUE.equals(userSetting.getSqlLog())) {
                configuration.setLogImpl(StdOutImpl.class);
            }
            configuration.setMapUnderscoreToCamelCase(true);
            configuration.getTypeHandlerRegistry().register(AlarmType.class, EnumOrdinalTypeHandler.class);
        };
    }

}
