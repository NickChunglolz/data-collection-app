package com.example.demo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class JdbcConfiguration {

  @Value("${spring.datasource.driver-class-name}")
  private String driver;

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Value("${spring.datasource.username}")
  private String dbUsername;

  @Value("${spring.datasource.password}")
  private String dbPassword;

  @Bean
  @Primary
  public DataSource dataSource() {
    return DataSourceBuilder.create().driverClassName(driver).url(dbUrl).username(dbUsername)
        .password(dbPassword).build();
  }
}
