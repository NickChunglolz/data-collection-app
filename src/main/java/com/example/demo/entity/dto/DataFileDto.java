package com.example.demo.entity.dto;

import com.example.demo.entity.po.FileType;
import com.example.demo.entity.po.ValidationStatus;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DataFileDto {

  private int id;
  private FileType fileType;
  private ValidationStatus validationStatus;
}
