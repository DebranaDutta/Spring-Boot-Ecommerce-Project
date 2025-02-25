package com.ecom.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ecom.model.Address;
import com.ecom.model.Cart;
import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import com.ecom.repository.CartRepository;
import com.ecom.repository.ProductOrderRepository;
import com.ecom.service.ProductOrderService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;
import com.google.gson.Gson;

import jakarta.mail.MessagingException;

@Component
public class ProductOrderServiceImpl implements ProductOrderService {

	Gson gson = new Gson();

	@Autowired
	private ProductOrderRepository productOrderRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CommonUtil commonUtil;

	@Override
	public void saveOrder(String userId, OrderRequest orderRequest) throws UnsupportedEncodingException, MessagingException {
		List<Cart> carts = cartRepository.findByUserUserId(userId);

		for (Cart cart : carts) {
			ProductOrder productOrder = new ProductOrder();
			productOrder.setOrderDate(new Date());
			productOrder.setProduct(cart.getProduct());
			double productPrice = commonUtil.calculateProductPrice(cart.getProduct().getProductId());
			productOrder.setPrice(productPrice);
			productOrder.setQuantity(cart.getQuantity());
			productOrder.setUser(cart.getUser());
			productOrder.setStatus(OrderStatus.IN_PROGRESS.getName());
			productOrder.setPaymentType(orderRequest.getPaymentType());

			Address address = new Address();
			address.setFirstName(orderRequest.getFirstName());
			address.setLastName(orderRequest.getLastName());
			address.setEmail(orderRequest.getEmail());
			address.setMobileNo(orderRequest.getMobileNo());
			address.setAddress(orderRequest.getAddress());
			address.setCity(orderRequest.getCity());
			address.setState(orderRequest.getState());
			address.setPincode(orderRequest.getPincode());

			productOrder.setAddress(address);

			ProductOrder saverder = productOrderRepository.save(productOrder);
			commonUtil.sendMailForProductOrder(saverder, "Success");
		}
	}

	@Override
	public List<ProductOrder> getOrderByUser(String userId) {
		List<ProductOrder> productOrders = new ArrayList<>();
		productOrders = productOrderRepository.findByUserUserId(userId);
		return productOrders;
	}

	@Override
	public ProductOrder updateOrderStatus(String id, String Status) {
		Optional<ProductOrder> findById = productOrderRepository.findById(id);
		if (findById.isPresent()) {
			ProductOrder productOrder = findById.get();
			productOrder.setStatus(Status);
			ProductOrder updateProductOrder = productOrderRepository.save(productOrder);
			return updateProductOrder;
		}
		return null;
	}

	@Override
	public List<ProductOrder> getAllOrders() {
		List<ProductOrder> productOrders = productOrderRepository.findAll();
		return productOrders;
	}

	@Override
	public List<ProductOrder> getOrdersBySearch(String ch) {
		List<ProductOrder> list = productOrderRepository.findByOrderIdContainingIgnoreCaseOrUserFirstNameContainingIgnoreCaseOrAddressEmailContainingIgnoreCaseOrProductProductNameContainingIgnoreCase(ch, ch, ch, ch);
		return list;
	}

}
