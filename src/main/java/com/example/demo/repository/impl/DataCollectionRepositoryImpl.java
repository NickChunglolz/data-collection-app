package com.example.demo.repository.impl;

import com.example.demo.entity.po.DataCollection;
import com.example.demo.entity.po.DataCollectionStatus;
import com.example.demo.repository.DataCollectionRepository;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.util.Pair;

import java.sql.PreparedStatement;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class DataCollectionRepositoryImpl implements DataCollectionRepository {

  // SQL
  private static final String FIND_ALL_SQL = "SELECT * FROM eii_test.data_collections";
  private static final String GET_BY_ID_SQL = "SELECT * FROM eii_test.data_collections WHERE id = ?";
  private static final String CREATE_SQL = "INSERT INTO eii_test.data_collections (created_on, updated_on, file_id_orders, file_id_assets, file_id_inventory, status, tag, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
  private static final String UPDATE_SQL = "UPDATE eii_test.data_collections SET created_on = ?, updated_on = ?, file_id_orders = ?, file_id_assets = ?, file_id_inventory = ?, status = ?,  tag = ?, note = ? WHERE id = ?";

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

  private final JdbcTemplate jdbcTemplate;

  public DataCollectionRepositoryImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List<DataCollection> findAll(Map<String, Pair<String, String>> stringFilters,
      Map<String, Pair<String, Integer>> numberFilters,
      Map<String, Pair<String, Timestamp>> timestampFilters, String sortBy, int sortOrder) {
    List<Object> parameters = new LinkedList<>();
    StringBuilder queryBuilder = new StringBuilder(FIND_ALL_SQL);

    prepareQueryElements(stringFilters, numberFilters, timestampFilters, sortBy, sortOrder,
        parameters, queryBuilder);

    return jdbcTemplate.query(queryBuilder.toString(), dataCollectionRowMapper,
        parameters.toArray());
  }

  @Override
  public Optional<DataCollection> getById(int id) {
    try {
      DataCollection dataCollection = jdbcTemplate.queryForObject(GET_BY_ID_SQL,
          dataCollectionRowMapper, id);
      return Optional.ofNullable(dataCollection);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public int create(DataCollection data) {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    Timestamp currentTimestamp = Timestamp.from(Instant.now());

    jdbcTemplate.update(connection -> {
      PreparedStatement statement = connection.prepareStatement(CREATE_SQL,
          Statement.RETURN_GENERATED_KEYS);
      statement.setTimestamp(1, currentTimestamp);
      statement.setTimestamp(2, currentTimestamp);
      statement.setInt(3, data.getOrdersFileId());
      statement.setInt(4, data.getAssetsFileId());
      statement.setInt(5, data.getInventoryFileId());
      statement.setString(6, DataCollectionStatus.ACTIVATED.toString());
      statement.setString(7, data.getTag());
      statement.setString(8, data.getNote());
      return statement;
    }, keyHolder);

    Map<String, Object> keys = keyHolder.getKeys();
    if (keys != null && !keys.isEmpty()) {
      Number generatedId = (Number) keys.get("id");
      return generatedId.intValue();
    }

    return data.getId();
  }

  @Override
  public void update(DataCollection data) {
    jdbcTemplate.update(UPDATE_SQL, data.getCreatedOn(), data.getUpdatedOn(),
        data.getOrdersFileId(), data.getAssetsFileId(), data.getInventoryFileId(),
        data.getStatus().toString(), data.getTag(), data.getNote(), data.getId());
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


  private static final RowMapper<DataCollection> dataCollectionRowMapper = (resultSet, rowNum) -> DataCollection.builder()
      .id(resultSet.getInt(ID_COLUMN_LABEL))
      .createdOn(resultSet.getTimestamp(CREATE_ON_COLUMN_LABEL))
      .updatedOn(resultSet.getTimestamp(UPDATE_ON_COLUMN_LABEL))
      .ordersFileId(resultSet.getInt(FILE_ID_ORDERS_COLUMN_LABEL))
      .assetsFileId(resultSet.getInt(FILE_ID_ASSETS_COLUMN_LABEL))
      .inventoryFileId(resultSet.getInt(FILE_ID_INVENTORY_COLUMN_LABEL))
      .status(DataCollectionStatus.valueOf(resultSet.getString(STATUS_COLUMN_LABEL)))
      .tag(resultSet.getString(TAG_COLUMN_LABEL)).note(resultSet.getString(NOTE_COLUMN_LABEL))
      .build();
}
