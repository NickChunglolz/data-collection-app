package com.example.demo.service;

import com.example.demo.entity.dto.DataFileDto;
import com.example.demo.entity.po.DataFile;
import com.example.demo.entity.po.FileType;
import com.example.demo.entity.po.ValidationStatus;
import com.example.demo.entity.request.CreateDataCollectionRequest;
import com.example.demo.entity.dto.DataCollectionDto;
import com.example.demo.entity.po.DataCollection;
import com.example.demo.entity.request.QueryDataCollectionsFilter;
import com.example.demo.entity.request.UpdateCollectionRequest;
import com.example.demo.repository.DataCollectionRepository;
import com.example.demo.repository.DataFileRepository;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DataCollectionService {

  @Autowired
  private DataCollectionRepository dataCollectionRepository;
  @Autowired
  private DataFileRepository dataFileRepository;

  public List<DataCollectionDto> queryDataCollections(QueryDataCollectionsFilter filter) {
    Map<String, Pair<String, String>> stringFilters = new HashMap<>();
    Map<String, Pair<String, Integer>> numberFilters = new HashMap<>();
    Map<String, Pair<String, Timestamp>> timestampFilters = new HashMap<>();

    provideQueryFilters(filter.getFilters(), stringFilters, numberFilters, timestampFilters);

    return dataCollectionRepository.findAll(stringFilters, numberFilters, timestampFilters,
        filter.getSortBy(), filter.getOrderBy()).stream().map(this::map).toList();
  }

  public DataCollectionDto getDataCollection(int id) {
    DataCollection data = dataCollectionRepository.getById(id)
        .orElseThrow(throwNotExistingItemException(id));

    return map(data);
  }

  public DataCollectionDto createDataCollection(CreateDataCollectionRequest request) {
    validateFiles(request.getOrdersFileId(), request.getAssetsFileId(),
        request.getInventoryFileId());

    int id = dataCollectionRepository.create(this.map(request));

    DataCollection data = dataCollectionRepository.getById(id)
        .orElseThrow(throwNotExistingItemException(id));

    return map(data);
  }

  public DataCollectionDto updateDataCollection(int id, UpdateCollectionRequest request) {
    validateFiles(request.getOrdersFileId(), request.getAssetsFileId(),
        request.getInventoryFileId());

    DataCollection data = dataCollectionRepository.getById(id)
        .orElseThrow(throwNotExistingItemException(id));

    dataCollectionRepository.update(this.map(data, request));

    data = dataCollectionRepository.getById(id).orElseThrow(throwNotExistingItemException(id));

    return map(data);
  }

  public String deleteDataCollection(int id) {
    DataCollection data = dataCollectionRepository.getById(id)
        .orElseThrow(throwNotExistingItemException(id));
    data.deactivate();
    dataCollectionRepository.update(data);

    return "Data Collection " + data.getId() + " is deactivated";
  }


  private DataCollectionDto map(DataCollection data) {
    DataFile ordersFile = dataFileRepository.getById(data.getOrdersFileId())
        .orElseThrow(throwNotExistingFileException(data.getOrdersFileId()));
    DataFile assetsFile = dataFileRepository.getById(data.getAssetsFileId())
        .orElseThrow(throwNotExistingFileException(data.getAssetsFileId()));
    DataFile inventoryFile = dataFileRepository.getById(data.getInventoryFileId())
        .orElseThrow(throwNotExistingFileException(data.getInventoryFileId()));

    return DataCollectionDto.builder().id(data.getId()).ordersFile(map(ordersFile))
        .assetsFile(map(assetsFile)).inventoryFile(map(inventoryFile)).status(data.getStatus())
        .tag(data.getTag()).note(data.getNote()).build();
  }

  private DataFileDto map(DataFile file) {
    return DataFileDto.builder().id(file.getId()).fileType(file.getFileType())
        .validationStatus(file.getValidationStatus()).build();
  }

  private DataCollection map(CreateDataCollectionRequest request) {
    return DataCollection.builder().assetsFileId(request.getAssetsFileId())
        .ordersFileId(request.getOrdersFileId()).inventoryFileId(request.getInventoryFileId())
        .tag(request.getTag()).note(request.getNote()).build();
  }

  private DataCollection map(DataCollection data, UpdateCollectionRequest request) {
    return data.reconstitute(request.getOrdersFileId(), request.getAssetsFileId(),
        request.getInventoryFileId(), request.getTag(), request.getNote());
  }

  private void provideQueryFilters(QueryDataCollectionsFilter.Filters filters,
      Map<String, Pair<String, String>> stringFilters,
      Map<String, Pair<String, Integer>> numberFilters,
      Map<String, Pair<String, Timestamp>> timestampFilters) {

    if (filters == null) {
      return;
    }

    if (filters.getOrdersFileId() != null && filters.getOrdersFileId().getValue() != null
        && filters.getOrdersFileId().getComparison() != null) {

      numberFilters.put("file_id_orders",
          Pair.of(filters.getOrdersFileId().getComparison().getValue(),
              filters.getOrdersFileId().getValue()));
    }

    if (filters.getAssetsFileId() != null && filters.getAssetsFileId().getValue() != null
        && filters.getAssetsFileId().getComparison() != null) {

      numberFilters.put("file_id_assets",
          Pair.of(filters.getAssetsFileId().getComparison().getValue(),
              filters.getAssetsFileId().getValue()));
    }

    if (filters.getInventoryFileId() != null && filters.getInventoryFileId().getValue() != null
        && filters.getInventoryFileId().getComparison() != null) {

      numberFilters.put("file_id_inventory",
          Pair.of(filters.getInventoryFileId().getComparison().getValue(),
              filters.getInventoryFileId().getValue()));
    }

    if (filters.getStatus() != null && filters.getStatus().getValue() != null
        && filters.getStatus().getComparison() != null) {

      stringFilters.put("status",
          Pair.of(filters.getStatus().getComparison().getValue(), filters.getStatus().getValue()));
    }

    if (filters.getTag() != null && filters.getTag().getValue() != null
        && filters.getTag().getComparison() != null) {

      stringFilters.put("tag",
          Pair.of(filters.getTag().getComparison().getValue(), filters.getTag().getValue()));
    }

    if (filters.getNote() != null && filters.getNote().getValue() != null
        && filters.getNote().getComparison() != null) {

      stringFilters.put("note",
          Pair.of(filters.getNote().getComparison().getValue(), filters.getNote().getValue()));
    }

    if (filters.getCreatedOn() != null && filters.getCreatedOn().getValue() != null
        && filters.getCreatedOn().getComparison() != null) {

      timestampFilters.put("created_on", Pair.of(filters.getCreatedOn().getComparison().getValue(),
          Timestamp.valueOf(filters.getCreatedOn().getValue().toLocalDateTime())));
    }

    if (filters.getUpdatedOn() != null && filters.getUpdatedOn().getValue() != null
        && filters.getUpdatedOn().getComparison() != null) {

      timestampFilters.put("updated_on", Pair.of(filters.getUpdatedOn().getComparison().getValue(),
          Timestamp.valueOf(filters.getUpdatedOn().getValue().toLocalDateTime())));
    }
  }

  private void validateFiles(int ordersFileId, int assetsFileId, int inventoryFilesId) {
    DataFile ordersFile = dataFileRepository.getById(ordersFileId)
        .orElseThrow(throwNotExistingFileException(ordersFileId));
    DataFile assetsFile = dataFileRepository.getById(assetsFileId)
        .orElseThrow(throwNotExistingFileException(assetsFileId));
    DataFile inventoryFile = dataFileRepository.getById(inventoryFilesId)
        .orElseThrow(throwNotExistingFileException(inventoryFilesId));

    validateFile(ordersFile, FileType.orders);
    validateFile(assetsFile, FileType.assets);
    validateFile(inventoryFile, FileType.inventory);
  }

  private void validateFile(DataFile file, FileType type) {
    if (!file.getValidationStatus().equals(ValidationStatus.valid)) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
          "Invalid File Validation Status: " + file.getValidationStatus() + ", ID: "
              + file.getId());
    }

    if (!file.getFileType().equals(type)) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
          "Invalid File Type: " + file.getFileType() + ", ID: " + file.getId());
    }
  }

  private Supplier<RuntimeException> throwNotExistingItemException(int id) {
    return () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
        "Data collection not found with ID: " + id);
  }

  private Supplier<RuntimeException> throwNotExistingFileException(int id) {
    return () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid File ID: " + id);
  }
}
