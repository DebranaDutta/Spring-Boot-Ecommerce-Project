package com.ecom.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;

import jakarta.mail.MessagingException;

@Component
public interface ProductOrderService {
	public void saveOrder(String userId, OrderRequest orderRequest) throws UnsupportedEncodingException, MessagingException;

	public List<ProductOrder> getOrderByUser(String userId);

	public ProductOrder updateOrderStatus(String id, String Status);

	public void deleteOrderById(String orderId);

	public List<ProductOrder> getAllOrders();

	public Page<ProductOrder> getAllOrdersWithPagination(Integer pageNo, Integer pageSize);

	public List<ProductOrder> searchOrder(String ch);

	public Page<ProductOrder> searchOrderWithPagination(Integer pageNo, Integer pageSize, String ch);
}
