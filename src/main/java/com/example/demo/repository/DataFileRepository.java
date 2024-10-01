package com.example.demo.repository;

import com.example.demo.entity.po.DataFile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataFileRepository {
  
  Optional<DataFile> getById(int id);
}
