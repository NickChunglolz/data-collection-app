package com.example.demo.entity.dto;

import com.example.demo.entity.po.DataCollectionStatus;
import com.example.demo.entity.po.DataFile;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DataCollectionDto {

  private int id;
  private DataFileDto ordersFile;
  private DataFileDto assetsFile;
  private DataFileDto inventoryFile;
  private DataCollectionStatus status;
  private String tag;
  private String note;
}
