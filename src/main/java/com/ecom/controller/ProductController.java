package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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

import com.ecom.model.Product;
import com.ecom.service.ProductService;
import com.google.gson.Gson;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/product")
public class ProductController {
	@Autowired
	public ProductService productService;

	Gson gson = new Gson();

	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("productImage") MultipartFile file, HttpSession session) throws IOException {

		String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
		product.setImage(imageName);
		Product saveProduct = productService.saveProdcut(product);

		System.out.println(gson.toJson(saveProduct));

		if (!ObjectUtils.isEmpty(saveProduct)) {
			/* Image Store */
			File saveFile = new ClassPathResource("static/img").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product" + File.separator + imageName);
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			/* Image Store */
			session.setAttribute("successMsg", "Product saved successfully");
		} else {
			session.setAttribute("errorMsg", "Something went wrong");
		}
		return "redirect:/admin/loadAddProduct";
	}

	@GetMapping("/viewProducts")
	public String loadViewProducts(Model model, HttpSession session) {
		List<Product> products = productService.getAllProducts();
		model.addAttribute("products", products);
		return "Admin/ViewProducts.html";
	}

	@GetMapping("/deleteProduct/{productId}")
	public String deleteProduct(@PathVariable("productId") String productId, HttpSession session) {

		System.out.println(" Product Id : " + productId);

		Integer count = productService.deleteProductById(productId);

		if (count >= 1) {
			session.setAttribute("successMsg", "Category deleted successfully");
		} else {
			session.setAttribute("errorMsg", "Something went wrong!.....");
		}
		return "redirect:/product/viewProducts";
	}

}
