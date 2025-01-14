package com.ecom.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.User;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserService;

@Component
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public User saveUser(User user, MultipartFile file) throws IOException {
		String imageName = file.isEmpty() ? "defaultUser.jpg" : file.getOriginalFilename();
		user.setProfileImage(imageName);
		user.setRole("ROLE_USER");

		String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);

		if (!file.isEmpty()) {
			/* Image Store */
			File saveFile = new ClassPathResource("static/img").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "user" + File.separator + imageName);
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			/* Image Store */
		}

		User saveUser = userRepository.save(user);

		System.out.println(" User : " + user);

		return saveUser;
	}

	@Override
	public User getUserByEmail(String userEmail) {
		User user = userRepository.findByEmail(userEmail);
		return user;
	}

}
