package com.example.demo.service;

import com.example.demo.entity.dto.DataCollectionDto;
import com.example.demo.entity.po.DataCollection;
import com.example.demo.entity.po.DataCollectionStatus;
import com.example.demo.entity.po.DataFile;
import com.example.demo.entity.po.FileType;
import com.example.demo.entity.po.ValidationStatus;
import com.example.demo.entity.request.CreateDataCollectionRequest;
import com.example.demo.entity.request.UpdateCollectionRequest;
import com.example.demo.repository.DataCollectionRepository;
import com.example.demo.repository.DataFileRepository;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DataCollectionServiceTest {

  @Mock
  private DataCollectionRepository dataCollectionRepository;

  @Mock
  private DataFileRepository dataFileRepository;

  @InjectMocks
  private DataCollectionService dataCollectionService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testGetAllDataCollections() {
    // Given
    DataCollection dataCollection = DataCollection.builder().id(1).ordersFileId(100)
        .assetsFileId(200).inventoryFileId(300).status(DataCollectionStatus.ACTIVATED.toString())
        .build();

    when(dataCollectionRepository.findAll(any(), any(), anyInt())).thenReturn(
        List.of(dataCollection));

    // When
    var result = dataCollectionService.getAllDataCollections(new HashMap<>(), "id", 1);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(dataCollection.getId(), result.get(0).getId());
  }

  @Test
  public void testGetDataCollection() {
    // Given
    DataCollection dataCollection = DataCollection.builder().id(1).ordersFileId(100).build();

    when(dataCollectionRepository.getById(1)).thenReturn(Optional.of(dataCollection));

    // When
    DataCollectionDto result = dataCollectionService.getDataCollection(1);

    // Then
    assertNotNull(result);
    assertEquals(dataCollection.getId(), result.getId());
  }

  @Test
  public void testCreateDataCollection() {
    // Given
    CreateDataCollectionRequest request = new CreateDataCollectionRequest();
    request.setOrdersFileId(100);
    request.setAssetsFileId(200);
    request.setInventoryFileId(300);
    request.setStatus("ACTIVATED");

    when(dataCollectionRepository.create(any(DataCollection.class))).thenReturn(1);

    // When
    Integer result = dataCollectionService.createDataCollection(request);

    // Then
    assertNotNull(result);
    assertEquals(1, result);
  }

  @Test
  public void testUpdateDataCollection() {
    // Given
    UpdateCollectionRequest request = new UpdateCollectionRequest();
    request.setOrdersFileId(100);
    request.setAssetsFileId(200);
    request.setInventoryFileId(300);
    request.setTag("Updated Tag");

    DataCollection existingCollection = DataCollection.builder().id(1).ordersFileId(50).build();
    existingCollection.setId(1);
    existingCollection.setOrdersFileId(50);

    when(dataCollectionRepository.getById(1)).thenReturn(Optional.of(existingCollection));
    when(dataFileRepository.getById(100)).thenReturn(Optional.of(
        DataFile.builder().validationStatus(ValidationStatus.VALID.toString())
            .fileType(FileType.ORDERS.toString()).build()));
    when(dataFileRepository.getById(200)).thenReturn(Optional.of(
        DataFile.builder().validationStatus(ValidationStatus.VALID.toString())
            .fileType(FileType.ASSETS.toString()).build()));
    when(dataFileRepository.getById(300)).thenReturn(Optional.of(
        DataFile.builder().validationStatus(ValidationStatus.VALID.toString())
            .fileType(FileType.INVENTORY.toString()).build()));

    // When
    dataCollectionService.updateDataCollection(1, request);

    // Then
    verify(dataCollectionRepository).update(any(DataCollection.class));
  }

  @Test
  public void testDeleteDataCollection() {
    // Given
    when(dataCollectionRepository.getById(1)).thenReturn(
        Optional.of(DataCollection.builder().build()));

    // When
    dataCollectionService.deleteDataCollection(1);

    // Then
    verify(dataCollectionRepository).deactivate(1);
  }

  @Test
  public void testThrowNotExistingItemException() {
    // Given
    when(dataCollectionRepository.getById(1)).thenReturn(Optional.empty());

    // When & Then
    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
      dataCollectionService.getDataCollection(1);
    });

    assertEquals(HttpStatus.NOT_FOUND.toString(), exception.getStatusCode().toString());
    assertTrue(Objects.requireNonNull(exception.getReason()).contains("Data collection not found with ID: 1"));
  }
}
