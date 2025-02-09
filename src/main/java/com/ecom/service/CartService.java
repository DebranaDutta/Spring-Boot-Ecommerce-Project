package com.ecom.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ecom.model.Cart;

@Component
public interface CartService {
	public Cart saveCart(String productId, String userId);

	public List<Cart> getCartsByUser(String userId);

	public Integer getCountCart(String userId);

	public void updateQuantity(String symbol, String cartId);
}
