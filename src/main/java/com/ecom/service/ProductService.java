package com.ecom.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Product;

public interface ProductService {
	public Product saveProdcut(Product product);
	public List<Product> getAllProducts();
	public Product getProdcutById(String productId);
	public Integer deleteProductById(String productId);
	public Product updateProduct(Product product, MultipartFile file) throws IOException;
}
