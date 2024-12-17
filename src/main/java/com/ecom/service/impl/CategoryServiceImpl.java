package com.ecom.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.ecom.model.Category;
import com.ecom.repository.CategoryRepository;
import com.ecom.service.CategoryService;

@Component
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Override
	public Category saveCategory(Category category) {
		return categoryRepository.save(category);
	}

	@Override
	public List<Category> getAllCategory() {
		return categoryRepository.findAll();
	}

	@Override
	public boolean isCategoryExist(Category category) {
		boolean isCategoryExist = categoryRepository.existsByName(category.getName());
		return isCategoryExist;
	}

	@Override
	public Integer deleteCategory(String name) {
		Category category = categoryRepository.findByName(name);
		int count = categoryRepository.deleteCategoryByUUID(category.getId());
		return count;
	}

	@Override
	public Category getCategoryByName(String name) {
		Category category = categoryRepository.findByName(name);
		return category;
	}

	@Override
	public Category getCategoryById(String uuid) {
		Category category = categoryRepository.findById(uuid);
		return category;
	}

}
