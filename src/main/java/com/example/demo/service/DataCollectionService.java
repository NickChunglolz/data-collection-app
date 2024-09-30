package com.example.demo.service;

import com.example.demo.entity.po.DataFile;
import com.example.demo.entity.po.FileType;
import com.example.demo.entity.po.ValidationStatus;
import com.example.demo.entity.request.CreateDataCollectionRequest;
import com.example.demo.entity.dto.DataCollectionDto;
import com.example.demo.entity.po.DataCollection;
import com.example.demo.entity.request.UpdateCollectionRequest;
import com.example.demo.repository.DataCollectionRepository;
import com.example.demo.repository.DataFileRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DataCollectionService {

  @Autowired
  private DataCollectionRepository dataCollectionRepository;
  @Autowired
  private DataFileRepository dataFileRepository;

  public List<DataCollectionDto> getAllDataCollections(Map<String, String> filters, String sortBy,
      int sortOrder) {
    return dataCollectionRepository.findAll(filters, sortBy, sortOrder).stream().map(this::map)
        .toList();
  }

  public DataCollectionDto getDataCollection(int id) {
    DataCollection data = dataCollectionRepository.getById(id)
        .orElseThrow(throwNotExistingItemException(id));

    return this.map(data);
  }

  public Integer createDataCollection(CreateDataCollectionRequest request) {
    return dataCollectionRepository.create(this.map(request));
  }

  public void updateDataCollection(int id, UpdateCollectionRequest request) {
    DataCollection data = dataCollectionRepository.getById(id)
        .orElseThrow(throwNotExistingItemException(id));

    this.validateFiles(request.getOrdersFileId(), request.getAssetsFileId(),
        request.getInventoryFileId());

    dataCollectionRepository.update(this.map(data, request));
  }

  public void deleteDataCollection(int id) {
    dataCollectionRepository.getById(id).orElseThrow(throwNotExistingItemException(id));
    dataCollectionRepository.deactivate(id);
  }


  private DataCollectionDto map(DataCollection data) {
    return DataCollectionDto.builder().id(data.getId()).ordersFileId(data.getOrdersFileId())
        .assetsFileId(data.getAssetsFileId()).inventoryFileId(data.getInventoryFileId())
        .status(data.getStatus()).tag(data.getTag()).note(data.getNote()).build();
  }

  private DataCollection map(CreateDataCollectionRequest request) {
    return DataCollection.builder().assetsFileId(request.getAssetsFileId())
        .ordersFileId(request.getOrdersFileId()).inventoryFileId(request.getInventoryFileId())
        .status(request.getStatus()).tag(request.getTag()).note(request.getNote()).build();
  }

  private DataCollection map(DataCollection data, UpdateCollectionRequest request) {
    return data.reconstitute(request.getOrdersFileId(), request.getAssetsFileId(),
        request.getInventoryFileId(), request.getTag(), request.getNote());
  }

  private void validateFiles(int ordersFileId, int assetsFileId, int inventoryFilesId) {
    DataFile ordersFile = dataFileRepository.getById(ordersFileId)
        .orElseThrow(throwNotExistingFileException(ordersFileId));
    DataFile assetsFile = dataFileRepository.getById(assetsFileId)
        .orElseThrow(throwNotExistingFileException(assetsFileId));
    DataFile inventoryFile = dataFileRepository.getById(inventoryFilesId)
        .orElseThrow(throwNotExistingFileException(inventoryFilesId));

    validateFile(ordersFile, FileType.ORDERS);
    validateFile(assetsFile, FileType.ASSETS);
    validateFile(inventoryFile, FileType.INVENTORY);
  }

  private void validateFile(DataFile file, FileType type) {
    if (!file.getValidationStatus().equalsIgnoreCase(ValidationStatus.VALID.toString())) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
          "Invalid File Validation Status: " + file.getValidationStatus() + ", ID: "
              + file.getId());
    }

    if (!file.getFileType().equalsIgnoreCase(type.toString())) {
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
