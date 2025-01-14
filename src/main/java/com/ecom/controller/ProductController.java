package com.ecom.controller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.google.gson.Gson;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/product")
public class ProductController {
	@Autowired
	public ProductService productService;
	@Autowired
	public CategoryService categoryService;

	Gson gson = new Gson();

	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("isActive") String isActive, @RequestParam("productImage") MultipartFile file, HttpSession session) throws IOException {
		if(product.getDiscount()<0||product.getDiscount()>100) {
			session.setAttribute("errorMsg", "Discount Amount shoud be between 0% to 100% !");
		}else {
			Product saveProduct = productService.saveProdcut(product, isActive, file);
			if(!ObjectUtils.isEmpty(saveProduct)) {
				session.setAttribute("successMsg", "Product saved successfully");
			}else {
				session.setAttribute("errorMsg", "Something went wrong");
			}
		}
		return "redirect:/admin/loadAddProduct";
	}

	@GetMapping("/editProduct/{productId}")
	public String loadEditProductPage(@PathVariable("productId") String productId, Model model) {
		Product product = productService.getProdcutById(productId);
		model.addAttribute("product", product);

		List<Category> categories = categoryService.getAllCategory();
		model.addAttribute("categories", categories);

		return "Admin/EditProduct.html";
	}

	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product newProduct, @RequestParam("isActive") String isActive ,@RequestParam("productImage") MultipartFile file, HttpSession session) throws IOException {
		if (newProduct.getDiscount() < 0 || newProduct.getDiscount() > 100) {
			session.setAttribute("errorMsg", "Discount Amount shoud be between 0% to 100% !");
		} else {
			Product updatedProduct = productService.updateProduct(newProduct, isActive, file);
			if (!ObjectUtils.isEmpty(updatedProduct)) {
				session.setAttribute("successMsg", "Product updated successfully");
			} else {
				session.setAttribute("errorMsg", "Something went wrong!.....");
			}
		}
		return "redirect:/product/editProduct/" + newProduct.getProductId();
	}

	@GetMapping("/viewProducts")
	public String loadViewProducts(Model model, HttpSession session, @RequestParam(value = "category", defaultValue = "") String category) {
		
		List<Category> categories = categoryService.getAllCategory();
		model.addAttribute("categories", categories);
		
		List<Product> products = productService.getAllProducts(category);
		model.addAttribute("products", products);
		
		model.addAttribute("paramValue", category);
		
		return "Admin/ViewProducts.html";
	}

	@GetMapping("/deleteProduct/{productId}")
	public String deleteProduct(@PathVariable("productId") String productId, HttpSession session) {
		Integer count = productService.deleteProductById(productId);
		if (count >= 1) {
			session.setAttribute("successMsg", "Product deleted successfully");
		} else {
			session.setAttribute("errorMsg", "Something went wrong!.....");
		}
		return "redirect:/product/viewProducts";
	}

}
