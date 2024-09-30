package com.example.demo.controller;

import com.example.demo.entity.request.CreateDataCollectionRequest;
import com.example.demo.entity.dto.DataCollectionDto;
import com.example.demo.entity.request.UpdateCollectionRequest;
import com.example.demo.service.DataCollectionService;
import java.security.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/data-collections")
public class DataCollectionController {

  @Autowired
  private DataCollectionService dataCollectionService;

  @GetMapping
  public ResponseEntity<List<DataCollectionDto>> getAllDataCollections(
      @RequestParam(required = false) Integer ordersFileId,
      @RequestParam(required = false) Integer assetsFileId,
      @RequestParam(required = false) Integer inventoryFileId,
      @RequestParam(required = false) String status, @RequestParam(required = false) String tag,
      @RequestParam(required = false) String note,
      @RequestParam(required = false) Timestamp createdOn,
      @RequestParam(required = false) Timestamp updatedOn,
      @RequestParam(required = false) String sortBy,
      @RequestParam(defaultValue = "-1") Integer sortOrder) {

    Map<String, String> filters = new HashMap<>();

    if (ordersFileId != null) {
      filters.put("file_id_orders", String.valueOf(ordersFileId));
    }
    if (assetsFileId != null) {
      filters.put("file_id_assets", String.valueOf(assetsFileId));
    }
    if (inventoryFileId != null) {
      filters.put("file_id_inventory", String.valueOf(inventoryFileId));
    }
    if (status != null) {
      filters.put("status", status);
    }
    if (tag != null) {
      filters.put("tag", tag);
    }
    if (note != null) {
      filters.put("note", note);
    }
    if (createdOn != null) {
      filters.put("created_on", createdOn.toString());
    }
    if (updatedOn != null) {
      filters.put("updated_on", updatedOn.toString());
    }

    return ResponseEntity.ok()
        .body(dataCollectionService.getAllDataCollections(filters, sortBy, sortOrder));
  }

  @GetMapping(path = "/{id}")
  public ResponseEntity<DataCollectionDto> getDataCollection(@PathVariable("id") int id) {
    return ResponseEntity.ok().body(dataCollectionService.getDataCollection(id));
  }

  @PostMapping
  public ResponseEntity<Integer> createDataCollection(
      @RequestBody CreateDataCollectionRequest request) {
    return ResponseEntity.ok().body(dataCollectionService.createDataCollection(request));
  }

  @PutMapping(path = "/{id}")
  public ResponseEntity<Void> updateDataCollection(@PathVariable("id") int id,
      @RequestBody UpdateCollectionRequest request) {
    dataCollectionService.updateDataCollection(id, request);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping(path = "/{id}")
  public ResponseEntity<Void> deleteDataCollection(@PathVariable("id") int id) {
    dataCollectionService.deleteDataCollection(id);
    return ResponseEntity.ok().build();
  }
}
