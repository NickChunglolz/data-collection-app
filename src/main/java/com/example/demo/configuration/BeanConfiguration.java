package com.example.demo.configuration;

import com.example.demo.repository.impl.DataCollectionRepositoryImpl;
import com.example.demo.repository.impl.DataFileRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class BeanConfiguration {

  @Bean
  public DataCollectionRepositoryImpl dataCollectionRepositoryImpl(JdbcTemplate jdbcTemplate) {
    return new DataCollectionRepositoryImpl(jdbcTemplate);
  }

  @Bean
  public DataFileRepositoryImpl dataFileRepositoryImpl(JdbcTemplate jdbcTemplate) {
    return new DataFileRepositoryImpl(jdbcTemplate);
  }
}
