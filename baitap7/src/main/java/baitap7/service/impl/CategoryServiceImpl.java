package baitap7.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import baitap7.entity.Category;
import baitap7.repository.CategoryRepository;
import baitap7.service.CategoryService;

import java.util.*;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category createCategory(Category category) {
        return  categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, Category category) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if(optionalCategory.isPresent()) {
            return  categoryRepository.save(category);
        }
        return null;
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public List<Category> findAll() {
        return  categoryRepository.findAll();
    }

    @Override
    public List<Category> findByName(String name) {
        return categoryRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable) {
        return categoryRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }
}
