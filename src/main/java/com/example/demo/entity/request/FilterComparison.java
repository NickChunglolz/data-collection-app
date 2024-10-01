package com.example.demo.entity.request;

import lombok.Getter;

@Getter
public enum FilterComparison {
  EQ("="), GTE(">="), GT(">"), LT("<"), LTE("<=");

  private final String value;

  FilterComparison(String value) {
    this.value = value;
  }
}