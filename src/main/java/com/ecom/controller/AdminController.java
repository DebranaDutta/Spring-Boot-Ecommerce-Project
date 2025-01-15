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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.User;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	public ProductService productService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private UserService userService;

	@GetMapping("/")
	public String index() {
		return "Admin/AdminIndex.html";
	}

	/**** Product Specific Tasks ****/

	@RequestMapping(method = RequestMethod.GET, path = "/loadAddProduct")
	public String loadAddProduct(Model model) {
		List<Category> categories = categoryService.getAllCategory();
		model.addAttribute("categories", categories);
		return "Admin/AddProduct.html";
	}

	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("isActive") String isActive, @RequestParam("productImage") MultipartFile file, HttpSession session) throws IOException {
		if (product.getDiscount() < 0 || product.getDiscount() > 100) {
			session.setAttribute("errorMsg", "Discount Amount shoud be between 0% to 100% !");
		} else {
			Product saveProduct = productService.saveProdcut(product, isActive, file);
			if (!ObjectUtils.isEmpty(saveProduct)) {
				session.setAttribute("successMsg", "Product saved successfully");
			} else {
				session.setAttribute("errorMsg", "Something went wrong");
			}
		}
		return "redirect:/admin/loadAddProduct";
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

	@GetMapping("/editProduct/{productId}")
	public String loadEditProductPage(@PathVariable("productId") String productId, Model model) {
		Product product = productService.getProdcutById(productId);
		model.addAttribute("product", product);

		List<Category> categories = categoryService.getAllCategory();
		model.addAttribute("categories", categories);

		return "Admin/EditProduct.html";
	}

	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product newProduct, @RequestParam("isActive") String isActive, @RequestParam("productImage") MultipartFile file, HttpSession session) throws IOException {
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
		return "redirect:/admin/editProduct/" + newProduct.getProductId();
	}

	@GetMapping("/deleteProduct/{productId}")
	public String deleteProduct(@PathVariable("productId") String productId, HttpSession session) {
		Integer count = productService.deleteProductById(productId);
		if (count >= 1) {
			session.setAttribute("successMsg", "Product deleted successfully");
		} else {
			session.setAttribute("errorMsg", "Something went wrong!.....");
		}
		return "redirect:/admin/viewProducts";
	}

	/**** Product Specific Tasks ****/

	/**** Category Specific Tasks ****/

	@RequestMapping(method = RequestMethod.GET, path = "/loadAddCategory")
	public String loadAddCategory(Model model) {
		model.addAttribute("categories", categoryService.getAllCategory());
		return "Admin/AddCategory.html";
	}

	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category, @RequestParam("isActive") String isActive, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
		String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
		category.setImage(imageName);
		if (isActive.equalsIgnoreCase("true")) {
			category.setActive(true);
		} else {
			category.setActive(false);
		}

		boolean isCategoryExist = categoryService.isCategoryExist(category);

		if (isCategoryExist) {
			session.setAttribute("errorMsg", "Category name already exist");
		} else {
			Category saveCategory = categoryService.saveCategory(category);
			if (ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("errorMsg", "Not saved! Internal server error !");
			} else {
				/* Image Store */
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category" + File.separator + imageName);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				/* Image Store */
				session.setAttribute("successMsg", "Saved successfully");
			}
		}
		return "redirect:/admin/loadAddCategory";
	}

	@GetMapping(path = "/loadEditCategory/{name}")
	public String loadEditCategory(@PathVariable("name") String name, Model model) {
		Category category = categoryService.getCategoryByName(name);
		model.addAttribute("category", category);
		return "Admin/EditCategory.html";
	}

	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category newCategory, @RequestParam("isActive") String isActive, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
		Category oldCategory = categoryService.getCategoryById(newCategory.getId());
		String fileName = file.isEmpty() ? oldCategory.getImage() : file.getOriginalFilename();
		if (!ObjectUtils.isEmpty(oldCategory)) {
			oldCategory.setName(newCategory.getName());

			if (isActive.equalsIgnoreCase("true")) {
				oldCategory.setActive(true);
			} else {
				oldCategory.setActive(false);
			}
			oldCategory.setImage(fileName);
		}
		Category updatedCategory = categoryService.saveCategory(oldCategory);
		if (!ObjectUtils.isEmpty(updatedCategory)) {
			if (!file.isEmpty()) {
				/* Image Store */
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category" + File.separator + fileName);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				/* Image Store */
			}
			session.setAttribute("successMsg", "Category updated successfully");
		} else {
			session.setAttribute("errorMsg", "Something went wrong!.....");
		}
		return "redirect:/admin/loadEditCategory/" + newCategory.getName();
	}

	@GetMapping("/deleteCategory/{name}")
	public String deleteCategory(@PathVariable("name") String name, HttpSession session) {
		Integer count = categoryService.deleteCategory(name);
		if (count >= 1) {
			session.setAttribute("successMsg", "Category deleted successfully");
		} else {
			session.setAttribute("errorMsg", "Something went wrong!.....");
		}
		return "redirect:/admin/loadAddCategory";
	}

	/**** Category Specific Tasks ****/

	/**** User Specific Tasks ****/

	@GetMapping("/viewUsers")
	public String loadViewUsers(Model model, HttpSession session) {
		List<User> users = userService.getAllUser();
		model.addAttribute("users", users);
		return "Admin/ViewUsers.html";
	}

	@GetMapping("/updateStatus")
	public String updateUserAccountStatus(@RequestParam("userstatus") Boolean status, @RequestParam("userid") String id, HttpSession session) {
		
		System.out.println(" Status "+ status);
		System.out.println(" User ID  "+ id);
		
		//boolean isEnable = true;
		
		boolean isEnable = userService.updateAccountStatus(id, status);
		
		if(isEnable) {
			session.setAttribute("successMsg", "Account Status Updated");
		}else {
			session.setAttribute("errorMsg","Something wrong on server");
		}
		
		return "redirect:/admin/viewUsers";
	}

	/**** User Specific Tasks ****/
}
