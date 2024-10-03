package com.example.demo.entity.po;

import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Builder
@Getter
public class DataCollection {

  private Integer id;
  private Timestamp createdOn;
  private Timestamp updatedOn;
  private int ordersFileId;
  private int assetsFileId;
  private int inventoryFileId;
  private DataCollectionStatus status;
  private String tag;
  private String note;

  public DataCollection reconstitute(int ordersFileId, int assetsFileId, int inventoryFileId,
      String tag, String note) {

    this.ordersFileId = ordersFileId;
    this.assetsFileId = assetsFileId;
    this.inventoryFileId = inventoryFileId;
    this.status = DataCollectionStatus.ACTIVATED;
    this.tag = tag;
    this.note = note;

    return this;
  }

  public void deactivate() {
    this.status = DataCollectionStatus.DEACTIVATED;
  }
}
