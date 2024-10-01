package com.example.demo.repository.impl;

import com.example.demo.entity.exception.DataAccessException;
import com.example.demo.entity.po.DataCollection;
import com.example.demo.entity.po.DataCollectionStatus;
import com.example.demo.repository.DataCollectionRepository;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class DataCollectionRepositoryImpl implements DataCollectionRepository {

  // SQL
  private static final String FIND_ALL_SQL = "SELECT * FROM eii_test.data_collections";
  private static final String GET_BY_ID_SQL = "SELECT * FROM eii_test.data_collections WHERE id = ?";
  private static final String CREATE_SQL = "INSERT INTO eii_test.data_collections (created_on, updated_on, file_id_orders, file_id_assets, file_id_inventory, status, tag, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
  private static final String UPDATE_SQL = "UPDATE eii_test.data_collections SET created_on = ?, updated_on = ?, file_id_orders = ?, file_id_assets = ?, file_id_inventory = ?, tag = ?, note = ? WHERE id = ?";

  // Column Label
  private static final String ID_COLUMN_LABEL = "id";
  private static final String CREATE_ON_COLUMN_LABEL = "created_on";
  private static final String UPDATE_ON_COLUMN_LABEL = "updated_on";
  private static final String FILE_ID_ORDERS_COLUMN_LABEL = "file_id_orders";
  private static final String FILE_ID_ASSETS_COLUMN_LABEL = "file_id_assets";
  private static final String FILE_ID_INVENTORY_COLUMN_LABEL = "file_id_inventory";
  private static final String STATUS_COLUMN_LABEL = "status";
  private static final String TAG_COLUMN_LABEL = "tag";
  private static final String NOTE_COLUMN_LABEL = "note";

  private static final Logger logger = LoggerFactory.getLogger(DataCollectionRepositoryImpl.class);

  private final DataSource dataSource;

  public DataCollectionRepositoryImpl(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public List<DataCollection> findAll(Map<String, Pair<String, String>> stringFilters,
      Map<String, Pair<String, Integer>> numberFilters,
      Map<String, Pair<String, Timestamp>> timestampFilters, String sortBy, int sortOrder) {
    List<DataCollection> dataCollections = new LinkedList<>();
    StringBuilder queryBuilder = new StringBuilder(FIND_ALL_SQL);
    List<Object> parameters = new LinkedList<>();

    prepareQueryElements(stringFilters, numberFilters, timestampFilters, sortBy, sortOrder,
        parameters, queryBuilder);

    try (Connection connection = dataSource.getConnection()) {

      PreparedStatement statement = connection.prepareStatement(queryBuilder.toString());

      // Set parameters for filter
      for (int i = 0; i < parameters.size(); i++) {
        statement.setObject(i + 1, parameters.get(i));
      }

      ResultSet resultSet = statement.executeQuery();

      while (resultSet.next()) {
        DataCollection dataCollection = DataCollection.builder()
            .id(resultSet.getInt(ID_COLUMN_LABEL))
            .createdOn(resultSet.getTimestamp(CREATE_ON_COLUMN_LABEL))
            .updatedOn(resultSet.getTimestamp(UPDATE_ON_COLUMN_LABEL))
            .ordersFileId(resultSet.getInt(FILE_ID_ORDERS_COLUMN_LABEL))
            .assetsFileId(resultSet.getInt(FILE_ID_ASSETS_COLUMN_LABEL))
            .inventoryFileId(resultSet.getInt(FILE_ID_INVENTORY_COLUMN_LABEL))
            .status(DataCollectionStatus.valueOf(resultSet.getString(STATUS_COLUMN_LABEL)))
            .tag(resultSet.getString(TAG_COLUMN_LABEL)).note(resultSet.getString(NOTE_COLUMN_LABEL))
            .build();
        dataCollections.add(dataCollection);
      }
    } catch (SQLException e) {
      logger.error("Error fetching data collections: {}", e.getMessage());
      throw new DataAccessException(e.getMessage());
    }
    return dataCollections;
  }

  @Override
  public Optional<DataCollection> getById(int id) {
    try (Connection connection = dataSource.getConnection()) {

      PreparedStatement statement = connection.prepareStatement(GET_BY_ID_SQL);
      statement.setInt(1, id);
      ResultSet resultSet = statement.executeQuery();

      if (resultSet.next()) {
        DataCollection dataCollection = DataCollection.builder()
            .id(resultSet.getInt(ID_COLUMN_LABEL))
            .createdOn(resultSet.getTimestamp(CREATE_ON_COLUMN_LABEL))
            .updatedOn(resultSet.getTimestamp(UPDATE_ON_COLUMN_LABEL))
            .ordersFileId(resultSet.getInt(FILE_ID_ORDERS_COLUMN_LABEL))
            .assetsFileId(resultSet.getInt(FILE_ID_ASSETS_COLUMN_LABEL))
            .inventoryFileId(resultSet.getInt(FILE_ID_INVENTORY_COLUMN_LABEL))
            .status(DataCollectionStatus.valueOf(resultSet.getString(STATUS_COLUMN_LABEL)))
            .tag(resultSet.getString(TAG_COLUMN_LABEL)).note(resultSet.getString(NOTE_COLUMN_LABEL))
            .build();
        return Optional.of(dataCollection);
      }
    } catch (SQLException e) {
      logger.error("Error fetching data collection by ID: {}", e.getMessage());
      throw new DataAccessException(e.getMessage());
    }
    return Optional.empty();
  }

  @Override
  public int create(DataCollection data) {
    try (Connection connection = dataSource.getConnection()) {

      PreparedStatement statement = connection.prepareStatement(CREATE_SQL,
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
      logger.error("Error creating data collection: {}", e.getMessage());
      throw new DataAccessException(e.getMessage());
    }

    return data.getId();
  }

  @Override
  public void update(DataCollection data) {
    try (Connection connection = dataSource.getConnection()) {

      PreparedStatement statement = connection.prepareStatement(UPDATE_SQL);
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
      logger.error("Error updating data collection: {}", e.getMessage());
      throw new DataAccessException(e.getMessage());
    }
  }

  private static void prepareQueryElements(Map<String, Pair<String, String>> stringFilters,
      Map<String, Pair<String, Integer>> numberFilters,
      Map<String, Pair<String, Timestamp>> timestampFilters, String sortBy, int sortOrder,
      List<Object> parameters, StringBuilder queryBuilder) {
    // Build filter conditions
    List<String> conditions = new LinkedList<>();
    if (stringFilters != null && !stringFilters.isEmpty()) {
      for (Map.Entry<String, Pair<String, String>> entry : stringFilters.entrySet()) {
        String column = entry.getKey();
        Pair<String, String> pair = entry.getValue();
        String comparator = pair.getFirst();
        String value = pair.getSecond();

        conditions.add(column + " " + comparator + " ?");
        parameters.add(value);
      }
    }

    if (numberFilters != null && !numberFilters.isEmpty()) {
      for (Map.Entry<String, Pair<String, Integer>> entry : numberFilters.entrySet()) {
        String column = entry.getKey();
        Pair<String, Integer> pair = entry.getValue();
        String comparator = pair.getFirst();
        Integer value = pair.getSecond();

        conditions.add(column + " " + comparator + " ?");
        parameters.add(value);
      }
    }

    if (timestampFilters != null && !timestampFilters.isEmpty()) {
      for (Map.Entry<String, Pair<String, Timestamp>> entry : timestampFilters.entrySet()) {
        String column = entry.getKey();
        Pair<String, Timestamp> pair = entry.getValue();
        String comparator = pair.getFirst();
        Timestamp value = pair.getSecond();

        conditions.add(column + " " + comparator + " ?");
        parameters.add(value);
      }
    }

    if (!conditions.isEmpty()) {
      queryBuilder.append(" WHERE ").append(String.join(" AND ", conditions));
    }

    // Add sorting only if sortBy is provided
    if (sortBy != null && !sortBy.isEmpty()) {
      queryBuilder.append(" ORDER BY ").append(sortBy).append(" ")
          .append(sortOrder == -1 ? "DESC" : "ASC");
    }
  }
}
