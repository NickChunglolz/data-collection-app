package com.example.demo.entity.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCollectionRequest {

  @NotNull(message = "ordersFileId should not be NULL.")
  private Integer ordersFileId;
  @NotNull(message = "assetsFileId should not be NULL.")
  private Integer assetsFileId;
  @NotNull(message = "inventoryFileId should not be NULL.")
  private Integer inventoryFileId;
  private String status;
  private String tag;
  private String note;
}
