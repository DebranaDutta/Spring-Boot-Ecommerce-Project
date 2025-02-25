package com.ecom.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
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
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import com.google.gson.Gson;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	CategoryService categoryService;

	@Autowired
	ProductService productService;

	@Autowired
	UserService userService;

	@Autowired
	CommonUtil commonUtil;

	@Autowired
	CartService cartService;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	Gson gson = new Gson();

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

	@GetMapping("/")
	public String index(Model model) {
		List<Category> categories = categoryService.getAllActiveCategory().stream().sorted((c1, c2) -> c1.getId().compareTo(c2.getId())).limit(6).toList();
		List<Product> products = productService.getAllActiveProducts("").stream().sorted((p1, p2) -> p2.getProductId().compareTo(p1.getProductId())).limit(8).toList();
		model.addAttribute("categories", categories);
		model.addAttribute("products", products);
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
	public String prodcut(Model model, @RequestParam(value = "category", defaultValue = "") String category, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "5") Integer pageSize) {
		List<Category> categories = categoryService.getAllActiveCategory();
		model.addAttribute("categories", categories);

		/*
		 * List<Product> products = productService.getAllActiveProducts(category);
		 * model.addAttribute("products", products);
		 */

		Page<Product> page = productService.getAllActiveProductWithPagination(pageNo, pageSize, category);
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

		return "Product.html";
	}

	@GetMapping("/viewProduct/{productId}")
	public String products(@PathVariable("productId") String productId, Model model) {
		Product viewProduct = productService.getProductById(productId);
		model.addAttribute("product", viewProduct);
		return "viewProduct.html";
	}

	/* Forgot Password Module */

	@GetMapping("/forgot-password")
	public String loadForgotPasswordPage() {
		return "FogotPassword.html";
	}

	@PostMapping("/forgot-password")
	public String processForgotPassword(@RequestParam(value = "mailId") String mailId, HttpSession session, HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		if (StringUtils.isEmpty(mailId)) {
			session.setAttribute("errorMsg", "Please enter your mail id.....!");
		} else {
			User user = userService.getUserByEmail(mailId);
			if (ObjectUtils.isEmpty(user)) {
				session.setAttribute("errorMsg", "Invalid Email ID.....!");
			} else {
				String resetToken = commonUtil.randomUUIDGenerator();
				userService.updateUserResetToken(mailId, resetToken);

				// Generate URI :
				// http://localhost:8080/reset-password?token=bakbkacsahljpqojndqanjqwd
				String url = commonUtil.generateURL(request) + "/reset-password?token=" + resetToken;

				Boolean sendMail = commonUtil.sendMail(url, mailId);
				if (sendMail) {
					session.setAttribute("successMsg", " Please check your email to get the reset pasword link.....!");
				} else {
					session.setAttribute("errorMsg", "Something wrong on the server!..Mail not sent.....!");
				}
			}
		}
		return "redirect:/forgot-password";
	}

	/*
	 * @GetMapping("/reset-password") public String loadResetPasswordPage() { return
	 * "ResetPassword.html"; }
	 */

	@GetMapping("/reset-password")
	public String showResetPassword(@RequestParam("token") String token, HttpSession session, Model model) {
		User user = userService.getUserByToken(token);
		if (ObjectUtils.isEmpty(user)) {
			model.addAttribute("errorMsg", "Your link is invalide or expired");
			return "Error.html";
		}
		model.addAttribute("token", token);
		return "ResetPassword.html";
	}

	@PostMapping("/reset-password")
	public String processResetPasswordPage(@RequestParam("token") String token, @RequestParam("pssword") String password, @RequestParam("cnfPassword") String cnfPassword, HttpSession session) {
		User user = userService.getUserByToken(token);
		System.out.println(" Token : " + token + "Password : " + password);
		if (password.equals(cnfPassword)) {
			user.setResetToken(null);
			String encodedPassword = bCryptPasswordEncoder.encode(password);
			user.setPassword(encodedPassword);
			userService.updateUser(user);
			session.setAttribute("succMsg", "Password changed successfully...");
		}
		return "ResetPassword.html";
	}

	/* Forgot Password Module */

	/* Search Functionality Module */
	@GetMapping("/search")
	public String searchProduct(@RequestParam String ch, Model model, HttpSession session, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "5") Integer pageSize) {
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
		return "Product.html";
	}
	/* Search Functionality Module */
}
