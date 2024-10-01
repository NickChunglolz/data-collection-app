package com.example.demo.service;

import com.example.demo.entity.dto.DataCollectionDto;
import com.example.demo.entity.po.DataCollection;
import com.example.demo.entity.po.DataFile;
import com.example.demo.entity.po.FileType;
import com.example.demo.entity.po.ValidationStatus;
import com.example.demo.entity.request.CreateDataCollectionRequest;
import com.example.demo.entity.request.FilterComparison;
import com.example.demo.entity.request.QueryDataCollectionsFilter;
import com.example.demo.entity.request.QueryDataCollectionsFilter.FilterSet;
import com.example.demo.entity.request.UpdateCollectionRequest;
import com.example.demo.repository.DataCollectionRepository;
import com.example.demo.repository.DataFileRepository;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DataCollectionServiceTest {

  @InjectMocks
  private DataCollectionService dataCollectionService;

  @Mock
  private DataCollectionRepository dataCollectionRepository;

  @Mock
  private DataFileRepository dataFileRepository;

  @BeforeEach
  void setup() {
    when(dataFileRepository.getById(1)).thenReturn(Optional.ofNullable(
        DataFile.builder().id(1).validationStatus(ValidationStatus.valid).fileType(FileType.orders)
            .build()));
    when(dataFileRepository.getById(2)).thenReturn(Optional.ofNullable(
        DataFile.builder().id(2).validationStatus(ValidationStatus.valid).fileType(FileType.assets)
            .build()));
    when(dataFileRepository.getById(3)).thenReturn(Optional.ofNullable(
        DataFile.builder().id(3).validationStatus(ValidationStatus.valid)
            .fileType(FileType.inventory).build()));
  }

  @AfterEach
  void tearDown() {
    reset(dataCollectionRepository);
    reset(dataFileRepository);
  }

  @Test
  void GivenQueryFilter_WhenValidFilterAndOneDataExist_ThenReturnsOneSizeResult() {
    // Given
    QueryDataCollectionsFilter filter = new QueryDataCollectionsFilter();
    filter.setFilters(new QueryDataCollectionsFilter.Filters());

    FilterSet<Integer> filterSet = new FilterSet<>();
    filterSet.setValue(1);
    filterSet.setComparison(FilterComparison.EQ);
    filter.getFilters().setOrdersFileId(filterSet);

    // When
    when(dataCollectionRepository.findAll(any(), any(), any(), any(), anyInt())).thenReturn(List.of(
        DataCollection.builder().id(1).ordersFileId(1).assetsFileId(2).inventoryFileId(3).build()));
    List<DataCollectionDto> result = dataCollectionService.queryDataCollections(filter);

    // Then
    assertEquals(1, result.size());
  }

  @Test
  void GivenDataCollectionId_WhenDataExist_ThenReturnsRequiredResult() {
    // Given
    int id = 1;
    DataCollection data = DataCollection.builder().id(id).ordersFileId(1).assetsFileId(2)
        .inventoryFileId(3).build();

    // When
    when(dataCollectionRepository.getById(id)).thenReturn(Optional.of(data));
    DataCollectionDto result = dataCollectionService.getDataCollection(id);

    // Then
    assertEquals(id, result.getId());
  }

  @Test
  void GivenCreateRequest_WhenValidRequestAndDataFilesExist_ThenReturnsCreatedItem() {
    // Given
    CreateDataCollectionRequest request = new CreateDataCollectionRequest();
    request.setOrdersFileId(1);
    request.setAssetsFileId(2);
    request.setInventoryFileId(3);
    DataCollection data = DataCollection.builder().id(1).ordersFileId(1).assetsFileId(2)
        .inventoryFileId(3).build();

    // When
    when(dataCollectionRepository.create(any())).thenReturn(1);
    when(dataCollectionRepository.getById(1)).thenReturn(Optional.of(data));
    DataCollectionDto result = dataCollectionService.createDataCollection(request);

    // Then
    assertEquals(1, result.getId());
  }

  @Test
  void GivenUpdateRequest_WhenValidRequestAndDataExist_ThenReturnsUpdatedItem() {
    // Given
    int id = 1;
    UpdateCollectionRequest request = new UpdateCollectionRequest();
    request.setOrdersFileId(1);
    request.setAssetsFileId(2);
    request.setInventoryFileId(3);
    DataCollection data = DataCollection.builder().id(id).build();

    // When
    when(dataCollectionRepository.getById(id)).thenReturn(Optional.of(data));
    DataCollectionDto result = dataCollectionService.updateDataCollection(id, request);

    // Then
    assertEquals(id, result.getId());
  }

  @Test
  void GivenDataCollectionId_WhenDataExist_ThenReturnsDeactivatedMessage() {
    // Given
    int id = 1;
    DataCollection data = DataCollection.builder().id(id).build();

    // When
    when(dataCollectionRepository.getById(id)).thenReturn(Optional.of(data));
    String result = dataCollectionService.deleteDataCollection(id);

    // Then
    assertEquals("Data Collection 1 is deactivated", result);
  }
}