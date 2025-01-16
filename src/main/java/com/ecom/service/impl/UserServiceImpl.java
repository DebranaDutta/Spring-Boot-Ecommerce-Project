package com.ecom.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.User;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserService;
import com.ecom.util.AppConstant;

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
		user.setEnabled(true);
		user.setAccountNonLocked(true);
		user.setFailedAttempt(0);

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

	@Override
	public List<User> getAllUser() {
		List<User> users = userRepository.findAll();
		return users;
	}

	@Override
	public boolean updateAccountStatus(String id, Boolean status) {
		Optional<User> findByuser = userRepository.findById(id);
		if (findByuser.isPresent()) {
			User user = findByuser.get();
			user.setEnabled(status);
			userRepository.save(user);
			return true;
		}
		return false;
	}

	@Override
	public Integer deleteUserByUserId(String userId) {
		int count = userRepository.deleteByUserId(userId);
		return count;
	}

	@Override
	public void increaseFailedAttempt(User user) {
		int attempt = user.getFailedAttempt() + 1;
		user.setFailedAttempt(attempt);
		userRepository.save(user);
	}

	@Override
	public void userAccountLocked(User user) {
		user.setAccountNonLocked(false);
		user.setLockTime(new Date());
		userRepository.save(user);
	}

	@Override
	public boolean unlockAccountTimeExpired(User user) {
		long lockTime = user.getLockTime().getTime();
		long unlockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;
		long currentTime = System.currentTimeMillis();
		if (unlockTime < currentTime) {
			user.setAccountNonLocked(true);
			user.setFailedAttempt(0);
			user.setLockTime(null);
			userRepository.save(user);
			return true;
		}
		return false;
	}

	@Override
	public void resetAttempt(int userId) {
		// TODO Auto-generated method stub

	}
}
