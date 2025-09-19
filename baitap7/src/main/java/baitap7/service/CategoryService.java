package baitap7.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import baitap7.entity.Category;

import java.util.*;

public interface CategoryService {
    Category createCategory(Category category);
    Category updateCategory(Long id,Category category);
    void deleteCategory(Long id);
    List<Category> findAll();
    List<Category> findByName(String name);
    Optional<Category> findById(Long id);
    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Category> findAll(Pageable pageable);
}
