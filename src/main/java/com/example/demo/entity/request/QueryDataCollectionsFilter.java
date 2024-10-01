package com.example.demo.entity.request;

import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryDataCollectionsFilter {

  private String sortBy;
  private int orderBy = -1;
  private Filters filters;

  @Getter
  @Setter
  public static class Filters {

    private FilterSet<Integer> ordersFileId;
    private FilterSet<Integer> assetsFileId;
    private FilterSet<Integer> inventoryFileId;
    private FilterSet<String> status;
    private FilterSet<String> tag;
    private FilterSet<String> note;
    private FilterSet<ZonedDateTime> createdOn;
    private FilterSet<ZonedDateTime> updatedOn;
  }

  @Getter
  @Setter
  public static class FilterSet<T> {

    private FilterComparison comparison;
    private T value;
  }
}
