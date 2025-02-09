package com.ecom.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecom.model.Cart;
import com.ecom.model.Product;
import com.ecom.model.User;
import com.ecom.repository.CartRepository;
import com.ecom.repository.ProductRepository;
import com.ecom.repository.UserRepository;
import com.ecom.service.CartService;

@Component
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;

	@Override
	public Cart saveCart(String productId, String userId) {
		User user = userRepository.findByUserId(userId);
		Product product = productRepository.findByProductId(productId);
		Cart cartStatus = cartRepository.findByProductProductIdAndUserUserId(productId, userId);
		Cart cart = null;
		if (ObjectUtils.isEmpty(cartStatus)) {
			cart = new Cart();
			cart.setProduct(product);
			cart.setUser(user);
			cart.setQuantity(1);
			double productPrice = product.getPrice() - ((product.getPrice() * product.getDiscount()) / 100);
			cart.setTotalPrice(1 * productPrice);
		} else {
			cart = cartStatus;
			cart.setQuantity(cartStatus.getQuantity() + 1);
			double productPrice = product.getPrice() - ((product.getPrice() * product.getDiscount()) / 100);
			cart.setTotalPrice(cart.getQuantity() * productPrice);
		}
		Cart saveCart = cartRepository.save(cart);
		return saveCart;
	}

	@Override
	public List<Cart> getCartsByUser(String userId) {
		List<Cart> carts = cartRepository.findByUserUserId(userId);
		Double totalOrderPrice = 0.0;
		List<Cart> updatedCarts = new ArrayList<>();
		for (Cart c : carts) {
			double productPrice = c.getProduct().getPrice() - ((c.getProduct().getPrice() * c.getProduct().getDiscount()) / 100);
			double totalPrice = productPrice * c.getQuantity();
			c.setTotalPrice(totalPrice);
			totalOrderPrice = totalOrderPrice + totalPrice;
			c.setTotalOrderPrice(totalOrderPrice);
			updatedCarts.add(c);
		}
		return updatedCarts;
	}

	@Override
	public Integer getCountCart(String userId) {
		Integer countByUserId = cartRepository.countByUserUserId(userId);
		return countByUserId;
	}

	@Override
	public void updateQuantity(String symbol, String cartId) {
		Cart cart = cartRepository.findByCartId(cartId);
		int updatedQuantity;
		if (symbol.equals("de")) {
			updatedQuantity = cart.getQuantity() - 1;
			if (updatedQuantity <= 0) {
				cartRepository.delete(cart);
			} else {
				cart.setQuantity(updatedQuantity);
				Cart updatedCart = cartRepository.save(cart);
			}
		} else {
			updatedQuantity = cart.getQuantity() + 1;
			cart.setQuantity(updatedQuantity);
			Cart updatedCart = cartRepository.save(cart);
		}
	}
}
