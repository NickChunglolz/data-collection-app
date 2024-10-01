package com.example.demo.controller;

import com.example.demo.entity.request.CreateDataCollectionRequest;
import com.example.demo.entity.dto.DataCollectionDto;
import com.example.demo.entity.request.QueryDataCollectionsFilter;
import com.example.demo.entity.request.UpdateCollectionRequest;
import com.example.demo.service.DataCollectionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/data-collections")
public class DataCollectionController {

  @Autowired
  private DataCollectionService dataCollectionService;

  @PostMapping(path = "/query")
  public ResponseEntity<List<DataCollectionDto>> queryDataCollections(
      @RequestBody QueryDataCollectionsFilter filter) {
    return ResponseEntity.ok().body(dataCollectionService.queryDataCollections(filter));
  }

  @GetMapping(path = "/{id}")
  public ResponseEntity<DataCollectionDto> getDataCollection(@PathVariable("id") int id) {
    return ResponseEntity.ok().body(dataCollectionService.getDataCollection(id));
  }

  @PostMapping
  public ResponseEntity<DataCollectionDto> createDataCollection(
      @RequestBody CreateDataCollectionRequest request) {
    return ResponseEntity.ok().body(dataCollectionService.createDataCollection(request));
  }

  @PutMapping(path = "/{id}")
  public ResponseEntity<DataCollectionDto> updateDataCollection(@PathVariable("id") int id,
      @RequestBody UpdateCollectionRequest request) {

    return ResponseEntity.ok().body(dataCollectionService.updateDataCollection(id, request));
  }

  @DeleteMapping(path = "/{id}")
  public ResponseEntity<String> deleteDataCollection(@PathVariable("id") int id) {
    return ResponseEntity.ok().body(dataCollectionService.deleteDataCollection(id));
  }
}
