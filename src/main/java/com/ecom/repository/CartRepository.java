package com.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecom.model.Cart;

import jakarta.persistence.Transient;
import jakarta.transaction.Transactional;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {

	public Cart findByProductProductIdAndUserUserId(String productId, String userId);

	public Integer countByUserUserId(String userId);

	public List<Cart> findByUserUserId(String userId);

	public Cart findByCartId(String cartId);

	public boolean deleteByCartId(String cartId);

}
