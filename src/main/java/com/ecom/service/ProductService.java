package com.ecom.service;

import java.util.List;

import com.ecom.model.Product;

public interface ProductService {
	public Product saveProdcut(Product product);
	public List<Product> getAllProducts();
	public Product getProdcutByName(String id);
	public Integer deleteProductById(String productId);
}
