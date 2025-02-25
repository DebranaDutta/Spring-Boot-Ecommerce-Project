package com.ecom.controller;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.ecom.model.Cart;
import com.ecom.model.Category;
import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import com.ecom.model.User;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductOrderService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;
import com.google.gson.Gson;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(path = "/user")
public class UserController {
	Gson gson = new Gson();

	@Autowired
	UserService userService;

	@Autowired
	CategoryService categoryService;

	@Autowired
	CartService cartService;

	@Autowired
	ProductOrderService orderService;

	@Autowired
	CommonUtil commonUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/")
	public String loadhomePage() {
		return "User/homePage.html";
	}

	private User getLoggedInUserDetails(Principal principal) {
		String email = principal.getName();
		User user = userService.getUserByEmail(email);
		return user;
	}

	@ModelAttribute
	public void getUserDetails(Principal principal, Model model) {
		if (principal != null) {
			String email = principal.getName();
			User user = userService.getUserByEmail(email);
			model.addAttribute("user", user);
			Integer countByUserId = cartService.getCountCart(user.getUserId());
			model.addAttribute("countByUserId", countByUserId);
		}

		List<Category> categories = categoryService.getAllActiveCategory();
		model.addAttribute("categories", categories);
	}

	@GetMapping("/addToCart")
	public String productsAddToCart(@RequestParam("pid") String productId, @RequestParam("uid") String userId, HttpSession session) {
		Cart saveCart = cartService.saveCart(productId, userId);
		if (ObjectUtils.isEmpty(saveCart)) {
			session.setAttribute("errorMsg", "Product add to cart failed .......!");
		} else {
			session.setAttribute("successMsg", "Product added to cart...........!");
		}
		return "redirect:/viewProduct/" + productId;
	}

	@GetMapping("/cart")
	public String loadCartPage(Principal principal, Model model) {
		User user = getLoggedInUserDetails(principal);
		List<Cart> carts = cartService.getCartsByUser(user.getUserId());
		model.addAttribute("carts", carts);
		int length = carts.size();
		model.addAttribute("length", length);
		if (length > 0) {
			double totalOrderPrice = carts.get(length - 1).getTotalOrderPrice();
			model.addAttribute("totalOrderPrice", totalOrderPrice);
		}
		return "User/Cart.html";
	}

	@GetMapping("/cartQuantityUpdate")
	public String updateCartQuantity(@RequestParam("sy") String symbol, @RequestParam("cid") String cartId) {
		cartService.updateQuantity(symbol, cartId);
		return "redirect:/user/cart";
	}

	@GetMapping("/orders")
	public String loadOrderPage(Principal principal, Model model) {
		User user = getLoggedInUserDetails(principal);
		List<Cart> carts = cartService.getCartsByUser(user.getUserId());
		model.addAttribute("carts", carts);
		if (carts.size() > 0) {
			double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
			model.addAttribute("totalOrderPrice", totalOrderPrice);
			double totalPayable = totalOrderPrice + 50;
			model.addAttribute("totalPayable", totalPayable);
		}
		return "User/Orders.html";
	}

	@GetMapping("/success")
	public String loadSuccessPage() {
		return "User/OrderSuccess.html";
	}

	@PostMapping("/save-order")
	public String processOrder(@ModelAttribute OrderRequest orderRequest, Principal principal) throws UnsupportedEncodingException, MessagingException {
		User user = getLoggedInUserDetails(principal);
		orderService.saveOrder(user.getUserId(), orderRequest);
		return "redirect:/user/success";
	}

	@GetMapping("/myOrders")
	public String loadMyOrdersPage(Principal principal, Model model) {
		User user = getLoggedInUserDetails(principal);
		List<ProductOrder> productOrders = orderService.getOrderByUser(user.getUserId());
		System.out.println(" *****  ProductOrders : " + gson.toJson(productOrders));
		model.addAttribute("productOrders", productOrders);
		return "User/MyOrders.html";
	}

	@GetMapping("/cancelOrder")
	public String cancelOrder(@RequestParam("id") String orderId, @RequestParam("st") Integer st, HttpSession session) throws UnsupportedEncodingException, MessagingException {
		System.out.println("** st : " + st);
		String status = null;
		OrderStatus[] orderStatus = OrderStatus.values();
		for (OrderStatus os : orderStatus) {
			Integer id = os.getId();
			if (id.equals(st)) {
				status = os.getName();
			}
		}
		ProductOrder updateOrder = orderService.updateOrderStatus(orderId, status);

		commonUtil.sendMailForProductOrder(updateOrder, status);

		if (!ObjectUtils.isEmpty(updateOrder)) {
			session.setAttribute("successMsg", "Status updated");
		} else {
			session.setAttribute("errorMsg", "Status not updated");
		}
		return "redirect:/user/myOrders";
	}

	@GetMapping("/viewProfile")
	public String loadViewProfilePage(Principal principal, Model model) {
		User user = getLoggedInUserDetails(principal);
		model.addAttribute("user", user);
		return "User/ViewProfile.html";
	}

	@PostMapping("/updateProfile")
	public String updateProfile(@ModelAttribute User user, HttpSession session) {
		User updatedUser = userService.updateUserProfile(user);
		if (!ObjectUtils.isEmpty(updatedUser)) {
			session.setAttribute("successMsg", "Profile updated");
		} else {
			session.setAttribute("errorMsg", "Profile not updated");
		}
		return "redirect:/user/viewProfile";
	}

	@PostMapping("/changePassword")
	public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword, @RequestParam String confirmPassword, Principal principal, HttpSession session) {
		if (newPassword.equals(confirmPassword)) {
			User user = getLoggedInUserDetails(principal);
			boolean matches = passwordEncoder.matches(currentPassword, user.getPassword());
			if (matches) {
				String encodedPassword = passwordEncoder.encode(newPassword);
				user.setPassword(encodedPassword);
				User updatedUser = userService.updateUser(user);
				if (!ObjectUtils.isEmpty(updatedUser)) {
					session.setAttribute("psuccessMsg", "Password updated");
				} else {
					session.setAttribute("perrorMsg", "Current password is incorrect");
				}
			} else {
				session.setAttribute("perrorMsg", "Current password is incorrect");
			}
		} else {
			session.setAttribute("perrorMsg", "New Password and Confirm Pasword does not matches");
		}
		return "redirect:/user/viewProfile";
	}
}
