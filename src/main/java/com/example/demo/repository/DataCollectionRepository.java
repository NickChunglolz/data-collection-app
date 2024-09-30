package com.example.demo.repository;

import com.example.demo.entity.po.DataCollection;
import java.util.Map;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataCollectionRepository {

  List<DataCollection> findAll(Map<String, String> filters, String sortBy, int sortOrder);

  Optional<DataCollection> getById(int id);

  int create(DataCollection data);

  void update(DataCollection data);

  void deactivate(int id);
}
