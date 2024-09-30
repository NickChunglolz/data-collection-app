package com.example.demo.entity.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDataCollectionRequest {

  private int ordersFileId;
  private int assetsFileId;
  private int inventoryFileId;
  private String status;
  private String tag;
  private String note;
}
