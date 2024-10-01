package com.example.demo.repository.impl;

import com.example.demo.entity.exception.DataAccessException;
import com.example.demo.entity.po.DataFile;
import com.example.demo.entity.po.FileType;
import com.example.demo.entity.po.ValidationStatus;
import com.example.demo.repository.DataFileRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class DataFileRepositoryImpl implements DataFileRepository {

  private static final String GET_BY_ID_SQL = "SELECT * FROM eii_test.data_files WHERE id = ?";

  // Column Label
  private static final String ID_COLUMN_LABEL = "id";
  private static final String CREATE_ON_COLUMN_LABEL = "created_on";
  private static final String UPDATE_ON_COLUMN_LABEL = "updated_on";
  private static final String FILE_TYPE_COLUMN_LABEL = "file_type";
  private static final String VALIDATION_STATUS_COLUMN_LABEL = "validation_status";

  private static final Logger logger = LoggerFactory.getLogger(DataFileRepositoryImpl.class);

  private final DataSource dataSource;

  public DataFileRepositoryImpl(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public Optional<DataFile> getById(int id) {
    try (Connection connection = dataSource.getConnection()) {
      PreparedStatement statement = connection.prepareStatement(GET_BY_ID_SQL);
      statement.setInt(1, id);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        DataFile data = DataFile.builder().id(resultSet.getInt(ID_COLUMN_LABEL))
            .createdOn(resultSet.getTimestamp(CREATE_ON_COLUMN_LABEL))
            .updatedOn(resultSet.getTimestamp(UPDATE_ON_COLUMN_LABEL))
            .fileType(FileType.valueOf(resultSet.getString(FILE_TYPE_COLUMN_LABEL)))
            .validationStatus(
                ValidationStatus.valueOf(resultSet.getString(VALIDATION_STATUS_COLUMN_LABEL)))
            .build();

        return Optional.of(data);
      }
    } catch (SQLException e) {
      logger.error("Error fetching data files by ID: {}", e.getMessage());
      throw new DataAccessException(e.getMessage());
    }

    return Optional.empty();
  }
}