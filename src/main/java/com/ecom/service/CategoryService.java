package com.ecom.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ecom.model.Category;

@Component
public interface CategoryService {
	public Category saveCategory(Category category);

	public List<Category> getAllCategory();

	public boolean isCategoryExist(Category category);

	public Integer deleteCategory(String name);

	public Category getCategoryByName(String name);

	public Category getCategoryById(String uuid);
	
	public List<Category> getAllActiveCategory();
}
