package com.ecom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;

@Controller
public class HomeController {

	@Autowired
	CategoryService categoryService;

	@Autowired
	ProductService productService;

	@GetMapping("/")
	public String index() {
		return "Index.html";
	}

	@RequestMapping(method = RequestMethod.GET, path = "/login")
	public String login() {
		return "login";
	}

	@RequestMapping(method = RequestMethod.GET, path = "/register")
	public String register() {
		return "Registration.html";
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
