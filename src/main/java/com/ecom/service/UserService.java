package com.ecom.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.User;

public interface UserService {
	public User saveUser(User user, MultipartFile file) throws IOException;

	public User saveAdmin(User user, MultipartFile file) throws IOException;

	public User getUserByEmail(String userEmial);

	public List<User> getAllUser(String role);

	public boolean updateAccountStatus(String id, Boolean status);

	public Integer deleteUserByUserId(String userId);

	public void increaseFailedAttempt(User user);

	public void userAccountLocked(User user);

	public boolean unlockAccountTimeExpired(User user);

	public void resetAttempt(int userId);

	public void updateUserResetToken(String emailId, String token);

	public User getUserByToken(String token);

	public User updateUser(User user);

	public User updateUserProfile(User user);

	public User getUserID(String userId);
}
