package com.ecom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecom.model.Category;
import com.ecom.model.Product;

import jakarta.transaction.Transactional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM Product WHERE productId = :productId ")
	public Integer deleteProductById(@Param("productId") String productId);
}
