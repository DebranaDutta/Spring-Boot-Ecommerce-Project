package com.ecom.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecom.model.Category;
import com.ecom.model.Product;

import jakarta.transaction.Transactional;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

	public Product findByProductId(String productId);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM Product WHERE productId = :productId ")
	public Integer deleteProductById(@Param("productId") String productId);

	public List<Product> findByCategory(String category);

	public Page<Product> findByCategory(String category, Pageable pageable);

	public List<Product> findByIsActiveTrue();

	public Page<Product> findByIsActiveTrue(Pageable pageable);

	public List<Product> findByCategoryAndIsActiveTrue(String category);

	public Page<Product> findByCategoryAndIsActiveTrue(Pageable pageable, String category);

	public List<Product> findByProductNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch1, String ch2);

	public Page<Product> findByProductNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch1, String ch2, Pageable pageable);

}
