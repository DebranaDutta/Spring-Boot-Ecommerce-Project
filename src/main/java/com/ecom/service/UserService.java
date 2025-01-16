package com.ecom.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.User;

public interface UserService {
	public User saveUser(User user, MultipartFile file) throws IOException;

	public User getUserByEmail(String userEmial);

	public List<User> getAllUser();

	public boolean updateAccountStatus(String id, Boolean status);

	public Integer deleteUserByUserId(String userId);

	public void increaseFailedAttempt(User user);

	public void userAccountLocked(User user);

	public boolean unlockAccountTimeExpired(User user);

	public void resetAttempt(int userId);
}
