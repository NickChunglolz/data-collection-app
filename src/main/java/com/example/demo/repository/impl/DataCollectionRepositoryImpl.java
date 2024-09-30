package com.example.demo.repository.impl;

import com.example.demo.entity.po.DataCollection;
import com.example.demo.entity.po.DataCollectionStatus;
import com.example.demo.repository.DataCollectionRepository;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Component
public class DataCollectionRepositoryImpl implements DataCollectionRepository {

  @Autowired
  private DataSource dataSource;

  @Override
  public List<DataCollection> findAll(Map<String, String> filters, String sortBy, int sortOrder) {
    List<DataCollection> dataCollections = new LinkedList<>();
    StringBuilder queryBuilder = new StringBuilder("SELECT * FROM eii_test.data_collections");
    List<Object> parameters = new LinkedList<>();

    // Build filter conditions
    if (filters != null && !filters.isEmpty()) {
      queryBuilder.append(" WHERE ");
      List<String> conditions = new LinkedList<>();
      for (Map.Entry<String, String> entry : filters.entrySet()) {
        String column = entry.getKey();
        String value = entry.getValue();

        // Check if the value can be parsed as an integer
        if (isInteger(value)) {
          conditions.add(column + " = ?");
          parameters.add(Integer.parseInt(value)); // Add as Integer
        } else {
          conditions.add(column + " = ?");
          parameters.add(value); // Add as String
        }
      }
      queryBuilder.append(String.join(" AND ", conditions));
    }

    // Add sorting only if sortBy is provided
    if (sortBy != null && !sortBy.isEmpty()) {
      queryBuilder.append(" ORDER BY ").append(sortBy).append(" ")
          .append(sortOrder == -1 ? "DESC" : "ASC");
    }

    try (Connection connection = dataSource.getConnection()) {
      PreparedStatement statement = connection.prepareStatement(queryBuilder.toString());

      // Set parameters for filter
      for (int i = 0; i < parameters.size(); i++) {
        statement.setObject(i + 1, parameters.get(i));
      }

      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next()) {
        DataCollection dataCollection = DataCollection.builder().id(resultSet.getInt("id"))
            .createdOn(resultSet.getTimestamp("created_on"))
            .updatedOn(resultSet.getTimestamp("updated_on"))
            .ordersFileId(resultSet.getInt("file_id_orders"))
            .assetsFileId(resultSet.getInt("file_id_assets"))
            .inventoryFileId(resultSet.getInt("file_id_inventory"))
            .status(resultSet.getString("status")).tag(resultSet.getString("tag"))
            .note(resultSet.getString("note")).build();
        dataCollections.add(dataCollection);
      }
    } catch (SQLException e) {
      System.out.println("Error fetching data collections: " + e.getMessage());
    }
    return dataCollections;
  }

  @Override
  public Optional<DataCollection> getById(int id) {
    try (Connection connection = dataSource.getConnection()) {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT * FROM eii_test.data_collections WHERE id = ?");
      statement.setInt(1, id);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        DataCollection dataCollection = DataCollection.builder().id(resultSet.getInt("id"))
            .createdOn(resultSet.getTimestamp("created_on"))
            .updatedOn(resultSet.getTimestamp("updated_on"))
            .ordersFileId(resultSet.getInt("file_id_orders"))
            .assetsFileId(resultSet.getInt("file_id_assets"))
            .inventoryFileId(resultSet.getInt("file_id_inventory"))
            .status(resultSet.getString("status")).tag(resultSet.getString("tag"))
            .note(resultSet.getString("note")).build();
        return Optional.of(dataCollection);
      }
    } catch (SQLException e) {
      System.out.println("Error fetching data collection by ID: " + e.getMessage());
    }
    return Optional.empty();
  }

  @Override
  public int create(DataCollection data) {
    try (Connection connection = dataSource.getConnection()) {

      PreparedStatement statement = connection.prepareStatement(
          "INSERT INTO eii_test.data_collections (created_on, updated_on, file_id_orders, file_id_assets, file_id_inventory, status, tag, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS);
      Timestamp currentTimestamp = Timestamp.from(Instant.now());
      statement.setTimestamp(1, currentTimestamp);
      statement.setTimestamp(2, currentTimestamp);
      statement.setInt(3, data.getOrdersFileId());
      statement.setInt(4, data.getAssetsFileId());
      statement.setInt(5, data.getInventoryFileId());
      statement.setString(6, DataCollectionStatus.ACTIVATED.toString());
      statement.setString(7, data.getTag());
      statement.setString(8, data.getNote());
      statement.executeUpdate();
      ResultSet generatedKeys = statement.getGeneratedKeys();
      if (generatedKeys.next()) {
        data.setId(generatedKeys.getInt(1));
      }
    } catch (SQLException e) {
      System.out.println("Error creating data collection: " + e.getMessage());
    }

    return data.getId();
  }

  @Override
  public void update(DataCollection data) {
    try (Connection connection = dataSource.getConnection()) {

      PreparedStatement statement = connection.prepareStatement(
          "UPDATE eii_test.data_collections SET created_on = ?, updated_on = ?, file_id_orders = ?, file_id_assets = ?, file_id_inventory = ?, tag = ?, note = ? WHERE id = ?");
      statement.setTimestamp(1, data.getCreatedOn());
      statement.setTimestamp(2, data.getUpdatedOn());
      statement.setInt(3, data.getOrdersFileId());
      statement.setInt(4, data.getAssetsFileId());
      statement.setInt(5, data.getInventoryFileId());
      statement.setString(6, data.getTag());
      statement.setString(7, data.getNote());
      statement.setInt(8, data.getId());
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Error updating data collection: " + e.getMessage());
    }
  }

  @Override
  public void deactivate(int id) {
    try (Connection connection = dataSource.getConnection()) {

      PreparedStatement statement = connection.prepareStatement(
          "UPDATE eii_test.data_collections SET status = ? WHERE id = ?");
      statement.setString(1, DataCollectionStatus.DEACTIVATED.toString());
      statement.setInt(2, id);
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Error updating data collection: " + e.getMessage());
    }
  }

  private boolean isInteger(String value) {
    try {
      Integer.parseInt(value);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
