package com.example.demo.repository.impl;

import com.example.demo.entity.po.DataFile;
import com.example.demo.entity.po.FileType;
import com.example.demo.entity.po.ValidationStatus;
import com.example.demo.repository.DataFileRepository;

import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class DataFileRepositoryImpl implements DataFileRepository {

  private static final String GET_BY_ID_SQL = "SELECT * FROM eii_test.data_files WHERE id = ?";

  // Column Label
  private static final String ID_COLUMN_LABEL = "id";
  private static final String CREATE_ON_COLUMN_LABEL = "created_on";
  private static final String UPDATE_ON_COLUMN_LABEL = "updated_on";
  private static final String FILE_TYPE_COLUMN_LABEL = "file_type";
  private static final String VALIDATION_STATUS_COLUMN_LABEL = "validation_status";

  private final JdbcTemplate jdbcTemplate;

  public DataFileRepositoryImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Optional<DataFile> getById(int id) {
    try {
      DataFile dataFile = jdbcTemplate.queryForObject(GET_BY_ID_SQL, dataFileRowMapper, id);
      return Optional.ofNullable(dataFile);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  private static final RowMapper<DataFile> dataFileRowMapper = (resultSet, rowNum) -> DataFile.builder()
      .id(resultSet.getInt(ID_COLUMN_LABEL))
      .createdOn(resultSet.getTimestamp(CREATE_ON_COLUMN_LABEL))
      .updatedOn(resultSet.getTimestamp(UPDATE_ON_COLUMN_LABEL))
      .fileType(FileType.valueOf(resultSet.getString(FILE_TYPE_COLUMN_LABEL))).validationStatus(
          ValidationStatus.valueOf(resultSet.getString(VALIDATION_STATUS_COLUMN_LABEL))).build();
}