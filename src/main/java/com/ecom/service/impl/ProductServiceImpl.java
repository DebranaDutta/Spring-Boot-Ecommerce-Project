package com.ecom.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Product;
import com.ecom.repository.ProductRepository;
import com.ecom.service.ProductService;

@Component
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;

	public Product saveProduct(Product product, String isActive, MultipartFile file) throws IOException {
		if (isActive.equalsIgnoreCase("true")) {
			product.setActive(true);
		} else {
			product.setActive(false);
		}

		String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
		product.setImage(imageName);

		if (!file.isEmpty()) {
			/* Image Store */
			File saveFile = new ClassPathResource("static/img").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product" + File.separator + imageName);
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			/* Image Store */
		}

		Product saveProduct = productRepository.save(product);

		return saveProduct;
	}

	@Override
	public List<Product> getAllProducts(String category) {
		List<Product> products = new ArrayList<>();
		if (ObjectUtils.isEmpty(category)) {
			products = productRepository.findAll();
		} else {
			products = productRepository.findByCategory(category);
		}
		return products;
	}

	@Override
	public Page<Product> getAllProductsWithPagination(Integer pageNo, Integer pageSize, String category) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<Product> pageProducts = null;
		if (ObjectUtils.isEmpty(category)) {
			pageProducts = productRepository.findAll(pageable);
		} else {
			pageProducts = productRepository.findByCategory(category, pageable);
		}
		return pageProducts;
	}

	@Override
	public List<Product> getAllActiveProducts(String category) {
		List<Product> products = new ArrayList<>();
		if (ObjectUtils.isEmpty(category)) {
			products = productRepository.findByIsActiveTrue();
		} else {
			products = productRepository.findByCategoryAndIsActiveTrue(category);
		}
		return products;
	}

	@Override
	public Integer deleteProductById(String productId) {
		int count = productRepository.deleteProductById(productId);
		return count;
	}

	@Override
	public Product getProductById(String productId) {
		Product product = productRepository.findByProductId(productId);
		return product;
	}

	@Override
	public Product updateProduct(Product newProduct, String isActive, MultipartFile file) throws IOException {
		Product dbProduct = productRepository.findByProductId(newProduct.getProductId());

		String fileName = file.isEmpty() ? dbProduct.getImage() : file.getOriginalFilename();

		if (!ObjectUtils.isEmpty(dbProduct)) {
			dbProduct.setProductName(newProduct.getProductName());
			dbProduct.setDescription(newProduct.getDescription());
			dbProduct.setCategory(newProduct.getCategory());
			dbProduct.setPrice(newProduct.getPrice());
			dbProduct.setStock(newProduct.getStock());
			dbProduct.setImage(fileName);
			dbProduct.setDiscount(newProduct.getDiscount());
			if (isActive.equalsIgnoreCase("true")) {
				dbProduct.setActive(true);
			} else {
				dbProduct.setActive(false);
			}
		}

		Product updatedProduct = productRepository.save(dbProduct);

		if (!file.isEmpty()) {
			/* Image Store */
			File saveFile = new ClassPathResource("static/img").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product" + File.separator + fileName);
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			/* Image Store */
		}

		return updatedProduct;
	}

	@Override
	public List<Product> searchProduct(String ch) {
		List<Product> list = productRepository.findByProductNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch);
		return list;
	}

	@Override
	public Page<Product> searchProductWithPagination(Integer pageNo, Integer pageSize, String ch) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<Product> pageProducts = productRepository.findByProductNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch, pageable);
		return pageProducts;
	}

	@Override
	public Page<Product> getAllActiveProductWithPagination(Integer pageNo, Integer pageSize, String category) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<Product> pageProduct = null;
		if (ObjectUtils.isEmpty(category)) {
			pageProduct = productRepository.findByIsActiveTrue(pageable);
		} else {
			pageProduct = productRepository.findByCategoryAndIsActiveTrue(pageable, category);
		}
		return pageProduct;
	}

}
