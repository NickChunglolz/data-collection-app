package com.example.demo.entity.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DataFileDto {

  private int id;
  private String fileType;
  private String validationStatus;
}
