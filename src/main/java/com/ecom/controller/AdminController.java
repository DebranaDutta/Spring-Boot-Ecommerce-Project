package com.ecom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.ecom.model.Category;
import com.ecom.service.CategoryService;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private CategoryService categoryService;

	@GetMapping("/")
	public String index() {
		return "Admin/AdminIndex.html";
	}

	@RequestMapping(method = RequestMethod.GET, path = "/loadAddProduct")
	public String loadAddProduct(Model model) {
		List<Category> categories = categoryService.getAllCategory();
		model.addAttribute("categories", categories);
		return "Admin/AddProduct.html";
	}

	@RequestMapping(method = RequestMethod.GET, path = "/loadAddCategory")
	public String loadAddCategory(Model model) {
		model.addAttribute("categories", categoryService.getAllCategory());
		return "Admin/AddCategory.html";
	}
}
