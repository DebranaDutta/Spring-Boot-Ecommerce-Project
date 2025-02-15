package com.ecom.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;

@Component
public interface ProductOrderService {
	public void saveOrder(String userId, OrderRequest orderRequest);

	public List<ProductOrder> getOrderByUser(String userId);

	public boolean updateOrderStatus(String id, String Status);
	
	public List<ProductOrder> getAllOrders();
}
