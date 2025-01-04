package com.ecom.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.User;
import com.ecom.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(path = "/user")
public class UserController {
	@Autowired
	UserService userService;

	@PostMapping(path = "/saveUser")
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
}
