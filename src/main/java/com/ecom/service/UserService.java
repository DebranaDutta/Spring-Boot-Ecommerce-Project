package com.ecom.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.User;

public interface UserService {
	public User saveUser(User user, MultipartFile file) throws IOException;
}
