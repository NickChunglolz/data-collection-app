package com.example.demo.configuration;

import com.example.demo.repository.impl.DataCollectionRepositoryImpl;
import com.example.demo.repository.impl.DataFileRepositoryImpl;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

  @Bean
  public DataCollectionRepositoryImpl dataCollectionRepositoryImpl(DataSource dataSource) {
    return new DataCollectionRepositoryImpl(dataSource);
  }

  @Bean
  public DataFileRepositoryImpl dataFileRepositoryImpl(DataSource dataSource) {
    return new DataFileRepositoryImpl(dataSource);
  }
}
