package com.ecom.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecom.model.ProductOrder;

import jakarta.transaction.Transactional;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, String> {
	public List<ProductOrder> findByUserUserId(String userId);

	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = "Delete from ecommercesbdb.product_order where order_id= :orderId")
	public void deleteByOrderId(@Param("orderId") String orderId);

	public List<ProductOrder> findByOrderIdContainingIgnoreCaseOrUserFirstNameContainingIgnoreCaseOrAddressEmailContainingIgnoreCaseOrProductProductNameContainingIgnoreCase(String ch1, String ch2, String ch3, String ch4);

	public Page<ProductOrder> findByOrderIdContainingIgnoreCaseOrUserFirstNameContainingIgnoreCaseOrAddressEmailContainingIgnoreCaseOrProductProductNameContainingIgnoreCase(String ch1, String ch2, String ch3, String ch4, Pageable pageable);
}
