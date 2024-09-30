package com.example.demo.entity.po;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Builder
@Getter
@Setter
public class DataFile {

  private Integer id;
  private Timestamp createdOn;
  private Timestamp updatedOn;
  private String fileType;
  private String validationStatus;
}
