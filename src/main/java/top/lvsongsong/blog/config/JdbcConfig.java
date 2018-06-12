package top.lvsongsong.blog.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author LvSS
 * @create 2018/05/12 15:55
 */
@Configuration
public class JdbcConfig {

    @Bean/*(destroyMethod = "close", initMethod = "init")*/
    @ConfigurationProperties("spring.datasource")
    public DruidDataSource druidDataSource() {
        return new DruidDataSource();
    }
}
