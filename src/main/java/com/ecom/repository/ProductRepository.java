package com.ecom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
	
	public List<Product> findByIsActiveTrue();

	public List<Product> findByCategoryAndIsActiveTrue(String category);
	
	/*@Modifying
	@Transactional
	@Query(value = "SELECT * FROM Product WHERE category = :category and is_active = 'TRUE' ;")
	public List<Product> findByCategoryAndIsActiveTrue(@Param("category") String category);*/

}
