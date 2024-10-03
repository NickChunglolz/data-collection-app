package com.example.demo.entity.dto;

import com.example.demo.entity.po.FileType;
import com.example.demo.entity.po.ValidationStatus;
import java.sql.Timestamp;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DataFileDto {

  private int id;
  private Timestamp createdOn;
  private Timestamp updatedOn;
  private FileType fileType;
  private ValidationStatus validationStatus;
}
