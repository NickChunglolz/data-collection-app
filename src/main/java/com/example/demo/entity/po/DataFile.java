package com.example.demo.entity.po;

import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Builder
@Getter
public class DataFile {

  private Integer id;
  private Timestamp createdOn;
  private Timestamp updatedOn;
  private FileType fileType;
  private ValidationStatus validationStatus;
}
