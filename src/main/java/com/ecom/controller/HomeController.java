package com.ecom.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.User;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	CategoryService categoryService;

	@Autowired
	ProductService productService;

	@Autowired
	UserService userService;

	@ModelAttribute
	public void getUserDetails(Principal principal, Model model) {
		if (principal != null) {
			String email = principal.getName();
			User user = userService.getUserByEmail(email);
			model.addAttribute("user", user);
		}

		List<Category> categories = categoryService.getAllActiveCategory();
		model.addAttribute("categories", categories);
	}

	@GetMapping("/")
	public String index() {
		return "Index.html";
	}

	@GetMapping("/signin")
	public String login() {
		return "Login.html";
	}

	@RequestMapping(method = RequestMethod.GET, path = "/register")
	public String register() {
		return "Registration.html";
	}

	@PostMapping(path = "/registerUser")
	public String saveUser(@ModelAttribute User user, @RequestParam("profile_image") MultipartFile file, @RequestParam("cpassword") String cpassword, HttpSession session) throws IOException {
		if (user.getPassword().equals(cpassword)) {
			User saveUser = userService.saveUser(user, file);
			if (!ObjectUtils.isEmpty(saveUser)) {
				session.setAttribute("successMsg", "User saved successfully");
			} else {
				session.setAttribute("errorMsg", "Something went wrong");
			}
		} else {
			session.setAttribute("errorMsg", "Password mismatch");
		}
		return "redirect:/register";
	}

	@GetMapping("/products")
	public String prodcut(Model model, @RequestParam(value = "category", defaultValue = "") String category) {

		List<Category> categories = categoryService.getAllActiveCategory();
		model.addAttribute("categories", categories);

		List<Product> products = productService.getAllActiveProducts(category);
		model.addAttribute("products", products);

		model.addAttribute("paramValue", category);

		return "Product.html";
	}

	@GetMapping("/viewProduct/{productId}")
	public String products(@PathVariable("productId") String productId, Model model) {
		Product viewProduct = productService.getProdcutById(productId);
		model.addAttribute("product", viewProduct);
		return "viewProduct.html";
	}

}
