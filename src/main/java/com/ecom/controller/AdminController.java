package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.ecom.model.ProductOrder;
import com.ecom.model.User;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductOrderService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;
import com.google.gson.Gson;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	Gson gson = new Gson();

	@Autowired
	public ProductService productService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private UserService userService;

	@Autowired
	private ProductOrderService productOrderService;

	@Autowired
	CommonUtil commonUtil;

	@Autowired
	PasswordEncoder passwordEncoder;

	@GetMapping("/")
	public String index() {
		return "Admin/AdminIndex.html";
	}

	private User getLoggedInUserDetails(Principal principal) {
		String email = principal.getName();
		User user = userService.getUserByEmail(email);
		return user;
	}

	/**** Admin Specific Tasks ****/

	@PostMapping(path = "/registerAdmin")
	public String saveAdmin(@ModelAttribute User user, @RequestParam("profile_image") MultipartFile file, @RequestParam("cpassword") String cpassword, HttpSession session) throws IOException {
		if (user.getPassword().equals(cpassword)) {
			User saveUser = userService.saveAdmin(user, file);
			if (!ObjectUtils.isEmpty(saveUser)) {
				session.setAttribute("successMsg", "New admin saved successfully");
			} else {
				session.setAttribute("errorMsg", "Something went wrong");
			}
		} else {
			session.setAttribute("errorMsg", "Password mismatch");
		}
		return "redirect:/admin/addAdmin";
	}

	@GetMapping("/addAdmin")
	public String loadAddAdminPage() {
		return "Admin/AddAdmin.html";
	}

	@GetMapping("/viewAdmins")
	public String loadViewAdmins(Model model, HttpSession session) {
		List<User> users = userService.getAllUser("ROLE_ADMIN");
		model.addAttribute("users", users);
		return "Admin/ViewUsers.html";
	}

	@GetMapping("/viewAdminProfile")
	public String loadViewAdminProfilePage(Principal principal, Model model) {
		User adminUser = getLoggedInUserDetails(principal);
		model.addAttribute("user", adminUser);
		return "Admin/ViewAdminProfile.html";
	}

	@PostMapping("/updateAdminProfile")
	public String updateAdminProfile(@ModelAttribute User user, HttpSession session, Principal principal) {
		User updatedUser = userService.updateUserProfile(user);
		if (!ObjectUtils.isEmpty(updatedUser)) {
			session.setAttribute("successMsg", "Profile updated");
		} else {
			session.setAttribute("errorMsg", "Profile not updated");
		}
		return "redirect:/admin/viewAdminProfile";
	}

	@PostMapping("/changeAdminPassword")
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
		return "redirect:/admin/viewAdminProfile";
	}

	/**** Admin Specific Tasks ****/

	/**** Order Specific Tasks ****/
	@GetMapping("/viewOrders")
	public String loadViewOrdersPage(Model model, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "4") Integer pageSize) {
		Page<ProductOrder> page = productOrderService.getAllOrdersWithPagination(pageNo, pageSize);
		List<ProductOrder> productOrders = page.getContent();
		Integer productOrderSize = productOrders.size();
		model.addAttribute("productOrderSize", productOrderSize);
		model.addAttribute("productOrders", productOrders);
		model.addAttribute("pageNo", page.getNumber());
		model.addAttribute("totalElements", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("isFirst", page.isFirst());
		model.addAttribute("isLast", page.isLast());
		model.addAttribute("pageSize", pageSize);
		return "Admin/ViewOrders.html";
	}

	@PostMapping("/update-order")
	public String updateOrderStatus(@RequestParam("id") String orderId, @RequestParam("st") Integer st, HttpSession session) throws UnsupportedEncodingException, MessagingException {
		String status = null;
		OrderStatus[] orderStatus = OrderStatus.values();
		for (OrderStatus os : orderStatus) {
			Integer id = os.getId();
			if (id.equals(st)) {
				status = os.getName();
			}
		}

		ProductOrder updateOrder = productOrderService.updateOrderStatus(orderId, status);

		commonUtil.sendMailForProductOrder(updateOrder, status);

		if (!ObjectUtils.isEmpty(updateOrder)) {
			session.setAttribute("successMsg", "Status updated");
		} else {
			session.setAttribute("errorMsg", "Status not updated");
		}

		return "redirect:/admin/viewOrders";
	}

	@PostMapping("/delete-order")
	public String deleteOrder(@RequestParam("id") String orderId) {
		productOrderService.deleteOrderById(orderId);
		return "redirect:/admin/viewOrders";
	}

	/**** Order Specific Tasks ****/

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
			Product saveProduct = productService.saveProduct(product, isActive, file);
			if (!ObjectUtils.isEmpty(saveProduct)) {
				session.setAttribute("successMsg", "Product saved successfully");
			} else {
				session.setAttribute("errorMsg", "Something went wrong");
			}
		}
		return "redirect:/admin/loadAddProduct";
	}

	@GetMapping("/viewProducts")
	public String loadViewProducts(Model model, HttpSession session, @RequestParam(value = "category", defaultValue = "") String category, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "4") Integer pageSize) {

		List<Category> categories = categoryService.getAllCategory();
		model.addAttribute("categories", categories);

		Page<Product> page = productService.getAllProductsWithPagination(pageNo, pageSize, category);
		List<Product> products = page.getContent();
		Integer productSize = products.size();
		model.addAttribute("productSize", productSize);
		model.addAttribute("products", products);
		model.addAttribute("pageNo", page.getNumber());
		model.addAttribute("totalElements", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("isFirst", page.isFirst());
		model.addAttribute("isLast", page.isLast());
		model.addAttribute("pageSize", pageSize);

		model.addAttribute("paramValue", category);

		return "Admin/ViewProducts.html";
	}

	@GetMapping("/editProduct/{productId}")
	public String loadEditProductPage(@PathVariable("productId") String productId, Model model) {
		Product product = productService.getProductById(productId);
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
		List<User> users = userService.getAllUser("ROLE_USER");
		model.addAttribute("users", users);
		return "Admin/ViewUsers.html";
	}

	@GetMapping("/updateStatus")
	public String updateUserAccountStatus(@RequestParam("userstatus") Boolean status, @RequestParam("userid") String id, HttpSession session) {
		User user = userService.getUserID(id);
		boolean isEnable = userService.updateAccountStatus(id, status);
		if (isEnable) {
			if (user.getRole().equals("ROLE_ADMIN")) {
				session.setAttribute("successMsg", "Account Status Updated");
				return "redirect:/admin/viewAdmins";
			} else {
				session.setAttribute("successMsg", "Account Status Updated");
				return "redirect:/admin/viewUsers";
			}

		} else {
			session.setAttribute("errorMsg", "Something wrong on server");
			return null;
		}
	}

	@GetMapping("/deleteUser")
	public String deleteUser(@RequestParam("userid") String userid, HttpSession session) {
		int count = userService.deleteUserByUserId(userid);
		if (count >= 1) {
			session.setAttribute("successMsg", "User deleted successfully");
		} else {
			session.setAttribute("errorMsg", "Something went wrong!.....");
		}
		return "redirect:/admin/viewUsers";
	}

	/**** User Specific Tasks ****/

	/* Search Functionality Module */
	@GetMapping("/searchOrder")
	public String searchProductOrders(@RequestParam String ch, Model model, HttpSession session, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "4") Integer pageSize) {
		Page<ProductOrder> page = productOrderService.searchOrderWithPagination(pageNo, pageSize, ch);
		List<ProductOrder> productOrders = page.getContent();
		Integer productOrderSize = productOrders.size();
		model.addAttribute("productOrderSize", productOrderSize);
		model.addAttribute("productOrders", productOrders);
		model.addAttribute("pageNo", page.getNumber());
		model.addAttribute("totalElements", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("isFirst", page.isFirst());
		model.addAttribute("isLast", page.isLast());
		model.addAttribute("pageSize", pageSize);

		return "Admin/ViewOrders.html";
	}

	@GetMapping("/searchProduct")
	public String searchProduct(@RequestParam String ch, Model model, HttpSession session, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "4") Integer pageSize) {
		Page<Product> page = productService.searchProductWithPagination(pageNo, pageSize, ch);
		List<Product> products = page.getContent();
		Integer productSize = products.size();
		model.addAttribute("productSize", productSize);
		model.addAttribute("products", products);
		model.addAttribute("pageNo", page.getNumber());
		model.addAttribute("totalElements", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("isFirst", page.isFirst());
		model.addAttribute("isLast", page.isLast());
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("products", products);

		return "Admin/ViewProducts.html";
	}
	/* Search Functionality Module */
}