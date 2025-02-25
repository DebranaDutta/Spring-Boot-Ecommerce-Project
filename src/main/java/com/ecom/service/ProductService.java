package com.ecom.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Product;

public interface ProductService {
	public Product saveProduct(Product product, String isActive, MultipartFile file) throws IOException;

	public List<Product> getAllProducts(String category);

	public Page<Product> getAllProductsWithPagination(Integer pageNo, Integer pageSize, String category);

	public List<Product> getAllActiveProducts(String category);
	
	public Page<Product> getAllActiveProductWithPagination(Integer pageNo, Integer pageSize, String category);

	public Product getProductById(String productId);

	public Integer deleteProductById(String productId);

	public Product updateProduct(Product product, String isActive, MultipartFile file) throws IOException;

	public List<Product> searchProduct(String ch);

	public Page<Product> searchProductWithPagination(Integer pageNo, Integer pageSize, String ch);
}
