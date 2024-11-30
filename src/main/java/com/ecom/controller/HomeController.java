package com.ecom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class HomeController {
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
	public String prodcut(){
		return "Product.html";
	}
	
	@GetMapping("/viewProduct")
	public String products() {
		return "viewProduct.html";
	}
	
}
