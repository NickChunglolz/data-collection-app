package com.example.demo.repository;

import com.example.demo.entity.po.DataCollection;
import java.sql.Timestamp;
import java.util.Map;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataCollectionRepository {

  List<DataCollection> findAll(Map<String, Pair<String, String>> stringFilters,
      Map<String, Pair<String, Integer>> numberFilters,
      Map<String, Pair<String, Timestamp>> timestampFilters, String sortBy, int sortOrder);

  Optional<DataCollection> getById(int id);

  int create(DataCollection data);

  void update(DataCollection data);
}
