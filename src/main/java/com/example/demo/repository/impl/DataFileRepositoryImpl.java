package com.example.demo.repository.impl;

import com.example.demo.entity.po.DataFile;
import com.example.demo.repository.DataFileRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DataFileRepositoryImpl implements DataFileRepository {

  @Autowired
  private DataSource dataSource;

  @Override
  public List<DataFile> findAll() {
    List<DataFile> dataFiles = new LinkedList<>();
    try (Connection connection = dataSource.getConnection()) {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT * FROM eii_test.data_files");
      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next()) {
        DataFile dataFile = DataFile.builder().id(resultSet.getInt("id"))
            .createdOn(resultSet.getTimestamp("created_on"))
            .updatedOn(resultSet.getTimestamp("updated_on"))
            .fileType(resultSet.getString("file_type"))
            .validationStatus(resultSet.getString("validation_status")).build();
        dataFiles.add(dataFile);
      }
    } catch (SQLException e) {
      System.out.println("Error fetching data files: " + e.getMessage());
    }
    return dataFiles;
  }

  @Override
  public Optional<DataFile> getById(int id) {
    try (Connection connection = dataSource.getConnection()) {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT * FROM eii_test.data_files WHERE id = ?");
      statement.setInt(1, id);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        DataFile data = DataFile.builder().id(resultSet.getInt("id"))
            .createdOn(resultSet.getTimestamp("created_on"))
            .updatedOn(resultSet.getTimestamp("updated_on"))
            .fileType(resultSet.getString("file_type"))
            .validationStatus(resultSet.getString("validation_status")).build();

        return Optional.of(data);
      }
    } catch (SQLException e) {
      System.out.println("Error fetching data file by ID: " + e.getMessage());
    }
    return Optional.empty();
  }
}