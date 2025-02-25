package com.ecom.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;

import jakarta.mail.MessagingException;

@Component
public interface ProductOrderService {
	public void saveOrder(String userId, OrderRequest orderRequest) throws UnsupportedEncodingException, MessagingException;

	public List<ProductOrder> getOrderByUser(String userId);

	public ProductOrder updateOrderStatus(String id, String Status);
	
	public List<ProductOrder> getAllOrders();
	
	public List<ProductOrder> getOrdersBySearch(String ch);
}
