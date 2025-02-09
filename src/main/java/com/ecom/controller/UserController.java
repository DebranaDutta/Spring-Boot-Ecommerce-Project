package com.ecom.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.ecom.model.User;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(path = "/user")
public class UserController {
	@Autowired
	UserService userService;

	@Autowired
	CategoryService categoryService;

	@Autowired
	CartService cartService;

	@GetMapping("/")
	public String homePage() {
		return "User/homePage.html";
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
		User user = getLoggedINUserDetails(principal);
		List<Cart> carts = cartService.getCartsByUser(user.getUserId());
		model.addAttribute("carts", carts);
		double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
		model.addAttribute("totalOrderPrice", totalOrderPrice);
		return "User/Cart.html";
	}

	private User getLoggedINUserDetails(Principal principal) {
		String email = principal.getName();
		User user = userService.getUserByEmail(email);
		return user;
	}

	@GetMapping("/cartQuantityUpdate")
	public String updateCartQuantity(@RequestParam("sy") String symbol, @RequestParam("cid") String cartId) {
		System.out.println("Symbol " + symbol);
		System.out.println("Cart ID  " + cartId);
		cartService.updateQuantity(symbol, cartId);
		return "redirect:/user/cart";
	}

}
