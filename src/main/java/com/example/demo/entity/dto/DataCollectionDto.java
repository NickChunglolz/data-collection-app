package com.example.demo.entity.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DataCollectionDto {

  private int id;
  private int ordersFileId;
  private int assetsFileId;
  private int inventoryFileId;
  private String status;
  private String tag;
  private String note;
}
